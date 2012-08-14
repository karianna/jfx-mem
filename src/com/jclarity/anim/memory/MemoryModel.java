package com.jclarity.anim.memory;

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
    
    private final Lock edenLock = new ReentrantLock();
    
    private int tenuringThreshold = 4;
    
    public MemoryModel(int wEden_, int wSrv_, int wOld_) {
        wEden = wEden_;
        wSrv = wSrv_;
        wOld = wOld_;
        
        eden = new MemoryBlock[wEden][height];
        s1 = new MemoryBlock[wSrv][height];
        s2 = new MemoryBlock[wSrv][height];
        tenured = new MemoryBlock[wOld][height];
    }

    /**
     * This method allocates a new block in Eden
     * 
     */
    void allocate() {
        edenLock.lock();
        
        if (canAlocate()) {
            
        } else {
            youngCollection();
        }
        
        edenLock.unlock();
    }

    private boolean canAlocate() {
        return true;
    }

    private void youngCollection() {
        // fixme
    }

    
}
