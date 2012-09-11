package com.jclarity.anim.memory;

import com.jclarity.anim.memory.MemoryBlock.MemoryBlockFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * This class implements a simple memory model, with a fixed number of rows but
 * variable number of columns
 *
 * @author boxcat
 */
public class MemoryModel {

    private final int wEden;
    private final int wSrv;
    private final int wOld;
    private final int height;
    private final ObjectProperty<MemoryBlockView>[][] eden;
    private final ObjectProperty<MemoryBlockView>[][] s1;
    private final ObjectProperty<MemoryBlockView>[][] s2;
    private final ObjectProperty<MemoryBlockView>[][] tenured;
    // FIXME We also need to model multiple TLABs
    private final ConcurrentMap<Integer, Integer> threadToCurrentTLAB = new ConcurrentHashMap<>();
    private final Lock edenLock = new ReentrantLock();
    // This will become a variable
    private static final int TENURING_THRESHOLD = 4;
    // FIXME Constant used to control lenght of run. Ick.
    private static final int RUN_LENGTH = 200;
    private MemoryBlockFactory factory = MemoryBlockFactory.getInstance();
    private final MemoryBlock[] allocList;
    private boolean isS1Current = false;

    public ObjectProperty<MemoryBlockView>[][] getEden() {
        return eden;
    }

    public ObjectProperty<MemoryBlockView>[][] getS1() {
        return s1;
    }

    public ObjectProperty<MemoryBlockView>[][] getS2() {
        return s2;
    }

    public ObjectProperty<MemoryBlockView>[][] getTenured() {
        return tenured;
    }

    public MemoryModel(int wEden_, int wSrv_, int wOld_, int height_) {
        wEden = wEden_;
        wSrv = wSrv_;
        wOld = wOld_;
        height = height_;

        eden = createMemoryBlockModel(wEden, height);
        s1 = createMemoryBlockModel(wSrv, height / 2);
        s2 = createMemoryBlockModel(wSrv, height / 2);
        tenured = createMemoryBlockModel(wOld, height);

        int nblocks = height * (wEden * +2 * wSrv + wOld);

        allocList = new MemoryBlock[nblocks * RUN_LENGTH];

        // FIXME Just single thread for now
        threadToCurrentTLAB.put(0, 0);
    }

    private ObjectProperty<MemoryBlockView>[][] createMemoryBlockModel(int width, int height_) {
        ObjectProperty<MemoryBlockView>[][] modelArray = new ObjectProperty[width][height_];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height_; j++) {
                modelArray[i][j] = new SimpleObjectProperty<>();
                modelArray[i][j].set(new MemoryBlockView());
            }
        }
        return modelArray;
    }

    /**
     * This method allocates a new block in Eden
     *
     */
    void allocate() {
        edenLock.lock();

        try {
            boolean hasAllocated = false;
            MemoryBlock mb = factory.getBlock();
            allocList[mb.getBlockId()] = mb;

            // FIXME Single allocating thread
            INNER:
            for (int i = 0; i < wEden; i++) {
                // Must use getValue() to actually see bindable behaviour
                if (eden[i][threadToCurrentTLAB.get(0)].getValue().getStatus() == MemoryStatus.FREE) {
                    eden[i][threadToCurrentTLAB.get(0)].getValue().setBlock(mb);
                    hasAllocated = true;
                    break INNER;
                }
            }
            if (hasAllocated) {
                return;
            }

            // Now try allocating a new TLAB for this thread
            // FIXME Single allocating thread
            boolean gotNewTLAB = setNewTLABForThread(0);
            System.out.println("Trying to get new TLAB: " + gotNewTLAB);

            if (gotNewTLAB) {
                // Have new TLAB, know we can allocate at offset 0
                eden[0][threadToCurrentTLAB.get(0)].getValue().setBlock(mb);
                return;
            }


            // Can't do anything in Eden, must collect
            youngCollection();
            
            // Eden is now reset, can allocate at offset 0 on current TLAB
            eden[0][threadToCurrentTLAB.get(0)].getValue().setBlock(mb);
        } finally {
            edenLock.unlock();
        }
    }

    /**
     * This method sets the Memory Block at position i in the allocation list to
     * a dead state
     *
     * @param id
     */
    void destroy(int id) {
        allocList[id].die();
        System.out.println("Killed " + id);
    }

    private void resetEden() {
        for (int i = 0; i < wEden; i++) {
            for (int j = 0; j < height; j++) {
                eden[i][j].getValue().setBlock(factory.getFreeBlock());
            }
        }
    }

    private void resetSrv(ObjectProperty<MemoryBlockView>[][] blocks) {
        for (int i = 0; i < wSrv; i++) {
            for (int j = 0; j < height / 2; j++) {
                blocks[i][j].getValue().setBlock(factory.getFreeBlock());
            }
        }
    }

    /**
     * This class models the state of an ongoing Young generational collection
     */
    private class YGCollectionContext {

        private boolean prematurePromote = false;
        private boolean hasFlippedSrvSpaces = false;
        private int genPromoted = TENURING_THRESHOLD;
    }

    private void youngCollection() {
        System.out.println("Trying a young collection");

        YGCollectionContext ctx = new YGCollectionContext();
        // We need to step through Eden (& implicitly the allocation list)
        // and promote live objects
        for (int i = 0; i < wEden; i++) {
            for (int j = 0; j < height; j++) {
                MemoryBlock mb = eden[i][j].getValue().getBlock();
                switch (mb.getStatus()) {
                    case ALLOCATED:
                        mb.mark();
                        moveToSurvivorSpace(ctx, mb);
                        break;
                    case DEAD:
                        break;
                    // Next two can't happen
                    case FREE:
                    default:
                        System.out.println("Block with status of: " + mb.getStatus() + " detected in Eden at " + i + ", " + j);
                }
            }
        }

        // Now reset all of Eden to FREE state
        resetEden();

        // Reset threadToCurrentTLAB
        // Handles multiple allocating threads
        for (Integer tNum : threadToCurrentTLAB.keySet()) {
            int t = tNum.intValue();
            threadToCurrentTLAB.put(t, t);
        }
    }

    /**
     * Manages the map of thread ids to currently being used TLAB. Handles
     * multiple allocating threads
     *
     * @param i
     * @return
     */
    boolean setNewTLABForThread(int i) {
        // FIXME Test case PLX
        Integer row = threadToCurrentTLAB.get(i);
        int max = row.intValue();
        for (Integer val : threadToCurrentTLAB.values()) {
            int v = val.intValue();
            if (v >= height - 1) {
                return false;
            }
            if (v > max) {
                max = v;
            }
        }
        threadToCurrentTLAB.put(i, max + 1);
        return true;
    }

    private ObjectProperty<MemoryBlockView>[][] currentSurvivorSpace() {
        return (isS1Current ? s1 : s2);
    }

    private void flipSurvivorSpaces() {
        isS1Current = !isS1Current;
    }

    // FIXME Handle Tenured
    private boolean tryToAddToTenured(MemoryBlock mb) {
        for (int i = 0; i < wOld; i++) {
            for (int j = 0; j < height; j++) {
                if (tenured[i][j].getValue().getStatus() == MemoryStatus.FREE) {
                    tenured[i][j].getValue().setBlock(mb);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method is used to empty the current survivor space into the other
     */
    private void compactCurrentSrvSpace(YGCollectionContext ctx) {
        ObjectProperty<MemoryBlockView>[][] from = currentSurvivorSpace();
        flipSurvivorSpaces();
        for (int i = 0; i < wSrv; i++) {
            for (int j = 0; j < height / 2; j++) {
                if (from[i][j].getValue().getStatus() == MemoryStatus.ALLOCATED) {
                    MemoryBlock alive = from[i][j].getValue().getBlock();
                    alive.mark();
                    boolean moved = tryToAddToCurrentSrvSpace(alive);
                    if (!moved) {
                        ctx.prematurePromote = true;
                    }
                }
            }
        }
        resetSrv(from);
    }

    /**
     * Walk through the current survivor space, and promote everything which has
     * this generation or higher
     *
     * @param genPromoted
     */
    private void prematurePromote(int genPromoted) {
        ObjectProperty<MemoryBlockView>[][] from = currentSurvivorSpace();
        for (int i = 0; i < wSrv; i++) {
            INNER:
            for (int j = 0; j < height / 2; j++) {
                if (from[i][j].getValue().getStatus() == MemoryStatus.ALLOCATED) {
                    MemoryBlock alive = from[i][j].getValue().getBlock();
                    if (alive.generation() >= genPromoted) {
                        alive.mark();
                        if (tryToAddToTenured(alive)) {
                            // Remove from Survivor spaces
                            from[i][j].getValue().setBlock(factory.getFreeBlock());
                            continue INNER;
                        } else {
                            throw new RuntimeException("OOME at " + i + ", " + j);
                        }

                    }
                }
            }
        }
    }

    /**
     * This method tries to add a MemoryBlock to the current survivor space
     *
     * @param mb
     * @return
     */
    private boolean tryToAddToCurrentSrvSpace(MemoryBlock mb) {
        ObjectProperty<MemoryBlockView>[][] to = currentSurvivorSpace();
        for (int i = 0; i < wSrv; i++) {
            for (int j = 0; j < height / 2; j++) {
                if (to[i][j].getValue().getStatus() == MemoryStatus.FREE) {
                    to[i][j].getValue().setBlock(mb);
                    return true;
                }
            }
        }
        return false;
    }

    private void moveToSurvivorSpace(YGCollectionContext ctx, MemoryBlock mb) {
        if (tryToAddToCurrentSrvSpace(mb)) {
            return;
        }

        // If we reach here, we haven't found a free block to move our 
        // surviving Eden object to.
        if (!ctx.hasFlippedSrvSpaces) {
            compactCurrentSrvSpace(ctx);
            ctx.hasFlippedSrvSpaces = true;
            // Try to add the surviving Eden object to the flipped srv space
            if (!ctx.prematurePromote && tryToAddToCurrentSrvSpace(mb)) {
                return;
            }

        }
        // Uh-oh. Looks like we need to need to do premature promotion
        // of the next to lowest generation that we've already done
        ctx.genPromoted--;
        prematurePromote(ctx.genPromoted);
    }
}
