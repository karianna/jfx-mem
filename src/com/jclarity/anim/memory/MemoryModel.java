package com.jclarity.anim.memory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * This class implements a simple memory model, with a fixed number of rows
 * but variable number of columns
 * 
 * @author boxcat
 */
public class MemoryModel {

    private final int height = 8;
    private final int wEden;
    private final int wSrv;
    private final int wOld;

    
    private final MemoryBlock[][] eden;
    private final MemoryBlock[][] s1;
    private final MemoryBlock[][] s2;
    private final MemoryBlock[][] tenured;
    
    private final ConcurrentMap<Integer, Integer> threadToCurrentTLAB = new ConcurrentHashMap<>();
    
    private final Lock edenLock = new ReentrantLock();
    
    private static final int TENURING_THRESHOLD = 4;
    
    private static final int RUN_LENGTH = 200;
    
    private MemoryBlock.MemoryBlockFactory factory = MemoryBlock.MemoryBlockFactory.getInstance();
    
    private final MemoryBlock[] allocList;
    
    public MemoryModel(int wEden_, int wSrv_, int wOld_) {
        wEden = wEden_;
        wSrv = wSrv_;
        wOld = wOld_;
        
        eden = new MemoryBlock[wEden][height];
        s1 = new MemoryBlock[wSrv][height];
        s2 = new MemoryBlock[wSrv][height];
        tenured = new MemoryBlock[wOld][height];
        
        int nblocks = height * (wEden * + 2 * wSrv + wOld);
        
        allocList = new MemoryBlock[nblocks * RUN_LENGTH];
    }

    /**
     * This method allocates a new block in Eden
     * 
     */
    void allocate() {
        edenLock.lock();
        try {
            if (canAlocate()) {
               MemoryBlock mb = factory.getBlock();
               allocList[mb.getId()] = mb;
            } else {
                youngCollection();
            }
        } finally {
            edenLock.unlock();
        }
    }

    void destroy(int id) {
        // FIXME
    }
    
    /**
     * Determine if we can allocate a new block
     * @return 
     */
    private boolean canAlocate() {
        if (currentTLABCanAllocate())
            return true;
        else if (canAllocateNewTLAB()) {
            setupNewTLAB();
            // FIXME - allocate zeroth element of new TLAB
            // Integer nextRow = threadToCurrentTLAB.get(incoming);
            // eden[nextRow][0]
            return true;
        } 
            
        
        return false;
    }

    private void youngCollection() {
        // FIXME What we need to do is to step through the allocation list
        // and retire everything which is dead. 
        //
        // and also handle promotion. :)
        
        
    }

    private boolean currentTLABCanAllocate() {
        // FIXME - This is bullshit.
        return true;
    }

    private boolean canAllocateNewTLAB() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void setupNewTLAB() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    
}
