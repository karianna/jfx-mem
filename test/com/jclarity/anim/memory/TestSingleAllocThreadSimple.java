package com.jclarity.anim.memory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;

/**
 *
 */
public class TestSingleAllocThreadSimple {

    ExecutorService srv = Executors.newSingleThreadExecutor();
    
    @Test
    public void testSimpleAlloc() throws InterruptedException {
        MemoryModel model = new MemoryModel(3, 6, 8, 8);
        CountDownLatch cdl = new CountDownLatch(1);
        MemoryPatternMaker maker = new MemoryPatternMaker(cdl, 1000);
        AllocatingThread at = new AllocatingThread(maker, model);
        srv.submit(at);
        // Wait for the model to reach known state
        cdl.await();
        
        // Now do some assertions
    }
    
}
