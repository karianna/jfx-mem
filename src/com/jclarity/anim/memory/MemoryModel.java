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

        eden = createMemoryBlockModel(wEden, height);
        s1 = createMemoryBlockModel(wSrv, height);
        s2 = createMemoryBlockModel(wSrv, height);
        tenured = createMemoryBlockModel(wOld, height);

        int nblocks = height * (wEden * +2 * wSrv + wOld);

        allocList = new MemoryBlock[nblocks * RUN_LENGTH];

        // FIXME Just single thread for now
        threadToCurrentTLAB.put(0, 0);
    }

    private ObjectProperty<MemoryBlockView>[][] createMemoryBlockModel(int width, int height) {
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
        
        // FIXME Are we even getting to here?
        eden[0][threadToCurrentTLAB.get(0)].getValue().setMemoryStatus(MemoryStatus.ALLOCATED);
        try {
            boolean hasAllocated = false;
            MemoryBlock mb = factory.getBlock();
            allocList[mb.getBlockId()] = mb;
            // FIXME Single allocating thread
            INNER:
            for (int i = 0; i < wEden; i++) {
                if (eden[i][threadToCurrentTLAB.get(0)].getValue().getStatus() == MemoryStatus.FREE) {
                    // DANGER WILL ROBINSON DOES JFX LET US DO THIS?
                    eden[i][threadToCurrentTLAB.get(0)].getValue().setBlock(mb);
                    hasAllocated = true;
                    break INNER;
                }
            }
            if (hasAllocated) return;
            
            // Now try allocating a new TLAB for this thread
            // FIXME Single allocating thread
            hasAllocated = setNewTLABForThread(0);
            if (hasAllocated) return;
             
            
            // Can't do anything in Eden, must collect
            youngCollection();
            

        } finally {
            edenLock.unlock();
        }
    }

    /**
     * This method sets the Memory Block at position i in the allocation list
     * to a dead state
     * @param id 
     */
    void destroy(int id) {
        allocList[id].die();
    }


    private void youngCollection() {
        // FIXME What we need to do is to step through the allocation list
        // and retire everything which is dead. 
        //
        // and also handle promotion. :)
        // and reset threadToCurrentTLAB
        //
        // FIXME Single allocating thread
        threadToCurrentTLAB.put(0, 0);
    }

    /**
     * Manages the map of thread ids to currently being used TLAB
     * @param i
     * @return 
     */
    boolean setNewTLABForThread(int i) {
        // FIXME Test case PLX
        Integer row = threadToCurrentTLAB.get(i);
        int max = 0;
        for (Integer val : threadToCurrentTLAB.values()) {
            int v = val.intValue();
            if (v >= height - 1) return false;
            if (v > max) max = v;
        }
        threadToCurrentTLAB.put(i, max + 1);
        return true;
    }
}
