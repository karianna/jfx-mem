package com.jclarity.anim.memory;

import com.jclarity.anim.memory.model.OpCode;
import java.util.concurrent.CountDownLatch;

/**
 * The point of this class is to allocate a predictable amount of memory.
 * When an appropriate limit is reached, the class should signal via a CDL
 * and then not progress any more.
 * 
 * This is to be used for unit testing a MemoryModel
 * 
 */
class MemoryPatternMaker implements IMemoryInterpreter {

    private volatile int seq = 1;
    
    private volatile int alloc_seq = 0;
    
    private volatile int free_seq = 0;
    
    private final CountDownLatch cdl;
    private final int limit;
    
    public MemoryPatternMaker(CountDownLatch cdl_, int limit_) {
        cdl = cdl_;
        limit = limit_;
    }
    
    
    @Override
    public MemoryInstruction getNextStep() {
        seq++;
        
        if (seq >= limit) {
            cdl.countDown();
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {

                }
            }
        }
        
        if ((seq % 5 == 0) || (seq % 5 == 3)) {
            return new MemoryInstruction(OpCode.KILL, free_seq++);
        } else {
            return new MemoryInstruction(OpCode.ALLOC, alloc_seq++);
        }
        
    }

}
