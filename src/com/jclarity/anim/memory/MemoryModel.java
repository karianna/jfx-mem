package com.jclarity.anim.memory;

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
    
    // FIXME We also need to model TLABs
    private final ConcurrentMap<Integer, Integer> threadToCurrentTLAB = new ConcurrentHashMap<>();
    private final Lock edenLock = new ReentrantLock();
    private static final int TENURING_THRESHOLD = 4;
    private static final int RUN_LENGTH = 200;
    private MemoryBlock.MemoryBlockFactory factory = MemoryBlock.MemoryBlockFactory.getInstance();
    private final MemoryBlock[] allocList;

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

        eden = createMemoryBlockModel(wEden);
        s1 = createMemoryBlockModel(wSrv);
        s2 = createMemoryBlockModel(wSrv);
        tenured = createMemoryBlockModel(wOld);

        int nblocks = height * (wEden * +2 * wSrv + wOld);

        allocList = new MemoryBlock[nblocks * RUN_LENGTH];

        // FIXME Just single thread for now
        threadToCurrentTLAB.put(0, 0);
    }

    private ObjectProperty<MemoryBlockView>[][] createMemoryBlockModel(int width) {
        ObjectProperty<MemoryBlockView>[][] modelArray = new ObjectProperty[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
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
            System.out.println(mb.getBlockId());
            allocList[mb.getBlockId()] = mb;
            // FIXME Single allocating thread
            INNER:
            for (int i = 0; i < wEden; i++) {
                // FIXME What is the difference between getValue() and get() here ?
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
            hasAllocated = setNewTLABForThread(0);
            if (hasAllocated) {
                return;
            }


            // Can't do anything in Eden, must collect
            youngCollection();


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
    }

    private void resetEden() {
        for (int i = 0; i < wEden; i++) {
            for (int j = 0; j < height; j++) {
                eden[i][j].get().setBlock(factory.getFreeBlock());
            }
        }
    }

    
    private void youngCollection() {
        System.out.println("Trying a young collection");
        // We need to step through Eden (& implicitly the allocation list)
        // and promote live objects
        for (int i = 0; i < wEden; i++) {
            for (int j = 0; j < height; j++) {
                MemoryBlock mb = eden[i][j].get().getBlock();
                switch(mb.getStatus()) {
                    case ALLOCATED:
                        moveToSurvivorSpace(mb);
                        break;
                    case DEAD:
                        break;
                    // Next two can't happen
                    case FREE:
                    default:
                        System.out.println("Block with status of: "+ mb.getStatus() +" detected in Eden at "+ i +", "+ j);
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
     * Manages the map of thread ids to currently being used TLAB.
     * Handles multiple allocating threads
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

    private void moveToSurvivorSpace(MemoryBlock mb) {

    }
}
