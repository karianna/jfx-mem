package com.jclarity.anim.memory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;


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

    
    private final ObjectProperty<MemoryBlock>[][] eden;
    private final ObjectProperty<MemoryBlock>[][] s1;
    private final ObjectProperty<MemoryBlock>[][] s2;
    private final ObjectProperty<MemoryBlock>[][] tenured;
    
    private final ConcurrentMap<Integer, Integer> threadToCurrentTLAB = new ConcurrentHashMap<>();
    
    private final Lock edenLock = new ReentrantLock();
    
    private static final int TENURING_THRESHOLD = 4;
    
    private static final int RUN_LENGTH = 200;
    
    private MemoryBlock.MemoryBlockFactory factory = MemoryBlock.MemoryBlockFactory.getInstance();
    
    private final MemoryBlock[] allocList;
    
    public ObjectProperty<MemoryBlock>[][] getEden() {
        return eden;
    }
    
    public MemoryModel(int wEden_, int wSrv_, int wOld_) {
        wEden = wEden_;
        wSrv = wSrv_;
        wOld = wOld_;
        
        eden = new ObjectProperty[wEden][height];
        
        //Initialise Eden
        System.out.println("Init Eden");
        for(int i =0; i < wEden; i++) {
            for(int j = 0; j < height; j++) {
                eden[i][j] = new SimpleObjectProperty<>();
                eden[i][j].set(new MemoryBlock());
            }
        }
        System.out.println("Finish Eden Init");
        
        s1 = new ObjectProperty[wSrv][height];
        s2 = new ObjectProperty[wSrv][height];
        tenured = new ObjectProperty[wOld][height];
        
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
               //allocList[mb.getId()] = mb;
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
