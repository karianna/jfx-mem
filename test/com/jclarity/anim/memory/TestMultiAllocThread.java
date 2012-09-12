package com.jclarity.anim.memory;

import com.jclarity.anim.memory.model.MemoryBlock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;

/**
 *
 */
public class TestMultiAllocThread {

    @Test
    public void testMultiAlloc() throws InterruptedException {
        // Eden = 12; S1, S2 = 6; Tenured = 18
        MemoryModel model = new MemoryModel(new MemoryBlock.MemoryBlockFactory(), 2, 2, 3, 6);
        List<String> cm1 = new ArrayList<>();
        List<String> cm2 = new ArrayList<>();

        String[] a1 = {"ALLOC", "ALLOC", "ALLOC", "ALLOC"};
        cm1.addAll(Arrays.asList(a1));
        cm1.addAll(Arrays.asList(a1));
        String[] k1 = {"KILL 0", "KILL 1", "KILL 2", "KILL 3"};
        String[] k2 = {"KILL 4", "KILL 5", "KILL 6"};
        cm1.addAll(Arrays.asList(k1));
        cm1.addAll(Arrays.asList(k2));

        String[] b1 = {"ALLOC", "ALLOC", "ALLOC", "ALLOC"};
        cm2.addAll(Arrays.asList(b1));
        cm2.addAll(Arrays.asList(b1));
        String[] j1 = {"KILL 0", "KILL 1", "KILL 2", "KILL 3"};
        String[] j2 = {"KILL 4", "KILL 5", "KILL 6"};
        cm2.addAll(Arrays.asList(j1));
        cm2.addAll(Arrays.asList(j2));

        
        MemoryInterpreterFileLoader myFl1 = new MemoryInterpreterFileLoader(cm1);
        AllocatingThread at = new AllocatingThread(myFl1, model);

        MemoryInterpreterFileLoader myFl2 = new MemoryInterpreterFileLoader(cm2);
        AllocatingThread at2 = new AllocatingThread(myFl2, model);

        ExecutorService srv = Executors.newScheduledThreadPool(3);
        srv.submit(at);
        srv.submit(at2);
        while (!at.isShutdown() || !at.isShutdown()) {
            Thread.sleep(250);
        }
        srv.shutdownNow();
        // Do asserts
    }
}
