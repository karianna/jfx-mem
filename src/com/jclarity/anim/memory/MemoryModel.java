package com.jclarity.anim.memory;

import com.jclarity.anim.memory.MemoryBlock.MemoryBlockFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private final MemoryPool eden;
    private final MemoryPool s1;
    private final MemoryPool s2;
    private final MemoryPool tenured;

    // FIXME We also need to model multiple TLABs
    private final ConcurrentMap<Integer, Integer> threadToCurrentTLAB = new ConcurrentHashMap<>();
    private final Lock edenLock = new ReentrantLock();
    // This will become a variable
    private static final int TENURING_THRESHOLD = 4;
    // FIXME Constant used to control lenght of run. Ick.
    private static final int RUN_LENGTH = 200;
    private final MemoryBlockFactory factory = MemoryBlockFactory.getInstance();
    private final MemoryBlock[] allocList;
    private boolean isS1Current = false;

    public MemoryPool getEden() {
        return eden;
    }

    public MemoryPool getS1() {
        return s1;
    }

    public MemoryPool getS2() {
        return s2;
    }

    public MemoryPool getTenured() {
        return tenured;
    }

    public MemoryModel(int wEden_, int wSrv_, int wOld_, int height_) {
        wEden = wEden_;
        wSrv = wSrv_;
        wOld = wOld_;
        height = height_;

        eden = new MemoryPool(wEden, height);
        s1 = new MemoryPool(wSrv, height / 2);
        s2 = new MemoryPool(wSrv, height / 2);
        tenured = new MemoryPool(wOld, height);

        int nblocks = height * (wEden * +2 * wSrv + wOld);

        allocList = new MemoryBlock[nblocks * RUN_LENGTH];

        // FIXME Just single thread for now
        threadToCurrentTLAB.put(0, 0);
    }

    // FIXME Move this to an Eden subclass of MemoryPool
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
                MemoryBlockView mbv = eden.getValue(i, threadToCurrentTLAB.get(0));
                if (mbv.getStatus() == MemoryStatus.FREE) {
                    mbv.setBlock(mb);
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
                eden.getValue(0, threadToCurrentTLAB.get(0)).setBlock(mb);
                return;
            }

            // Can't do anything in Eden, must collect
            youngCollection();

            // Eden is now reset, can allocate at offset 0 on current TLAB
            eden.getValue(0, threadToCurrentTLAB.get(0)).setBlock(mb);
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
                MemoryBlock mb = eden.getValue(i,j).getBlock();
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
                        System.out.println("Block id " + mb.getBlockId() + " with status of: " + mb.getStatus() + " detected in Eden at " + i + ", " + j);
                }
            }
        }

        // Now reset all of Eden to FREE state
        eden.reset();

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

    private MemoryPool currentSurvivorSpace() {
        return (isS1Current ? s1 : s2);
    }

    private void flipSurvivorSpaces() {
        isS1Current = !isS1Current;
    }

    
    /**
     * This method is used to empty the current survivor space into the other
     */
    private void compactCurrentSrvSpace(YGCollectionContext ctx) {
        MemoryPool from = currentSurvivorSpace();
        flipSurvivorSpaces();
        for (int i = 0; i < wSrv; i++) {
            for (int j = 0; j < height / 2; j++) {
                MemoryBlockView mbv = from.getValue(i, j);
                if (mbv.getStatus() == MemoryStatus.ALLOCATED) {
                    MemoryBlock alive = mbv.getBlock();
                    alive.mark();
                    boolean moved = currentSurvivorSpace().tryAdd(alive);
                    if (!moved) {
                        ctx.prematurePromote = true;
                    }
                }
            }
        }
        from.reset();
    }

    // FIXME Move to a Tenured subclass of MemoryPool when available
    private void compactTenured() {
        final List<MemoryBlock> evacuees = new ArrayList<>();

        for (int i = 0; i < wOld; i++) {
            for (int j = 0; j < height; j++) {
                MemoryBlockView mbv = tenured.getValue(i, j);
                if (mbv.getStatus() == MemoryStatus.ALLOCATED) {
                    MemoryBlock alive = mbv.getBlock();
                    alive.mark();
                    evacuees.add(alive);
                }
            }
        }
        // At this point, we have all the live blocks still in tenured
        tenured.reset();
        for (MemoryBlock mb : evacuees) {
            // This should always succeed - we never have more in evacuees than 
            // will fit into tenured
            tenured.tryAdd(mb);
        }

    }

    /**
     * Walk through the current survivor space, and promote everything which has
     * this generation or higher
     *
     * @param genPromoted
     */
    private void prematurePromote(int genPromoted) {
        MemoryPool from = currentSurvivorSpace();
        for (int i = 0; i < wSrv; i++) {
            INNER:
            for (int j = 0; j < height / 2; j++) {
                MemoryBlockView mbv = from.getValue(i,j);
                if (mbv.getStatus() == MemoryStatus.ALLOCATED) {
                    MemoryBlock alive = mbv.getBlock();
                    if (alive.generation() >= genPromoted) {
                        alive.mark();
                        if (tenured.tryAdd(alive)) {
                            // Remove from Survivor spaces
                            mbv.setBlock(factory.getFreeBlock());
                            continue INNER;
                        } else {
                            compactTenured();
                            if (tenured.tryAdd(alive)) {
                                // Remove from Survivor spaces
                                mbv.setBlock(factory.getFreeBlock());
                                continue INNER;
                            } else {
                                throw new RuntimeException("OOME at " + i + ", " + j);
                            }
                        }
                    }
                }
            }
        }
    }

    private void moveToSurvivorSpace(YGCollectionContext ctx, MemoryBlock mb) {
        if (currentSurvivorSpace().tryAdd(mb)) {
            return;
        }

        // If we reach here, we haven't found a free block to move our 
        // surviving Eden object to.
        if (!ctx.hasFlippedSrvSpaces) {
            compactCurrentSrvSpace(ctx);
            ctx.hasFlippedSrvSpaces = true;
            // Try to add the surviving Eden object to the flipped srv space
            if (!ctx.prematurePromote && currentSurvivorSpace().tryAdd(mb)) {
                return;
            }

        }
        // Uh-oh. Looks like we need to need to do premature promotion
        // of the next to lowest generation that we've already done
        ctx.genPromoted--;
        prematurePromote(ctx.genPromoted);
    }
}
