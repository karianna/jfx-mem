package com.jclarity.anim.memory;

import com.jclarity.anim.memory.model.MemoryInstruction;
import com.jclarity.anim.memory.model.MemoryBlock;
import com.jclarity.anim.memory.model.OpCode;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;

/**
 *
 */
public class TestMemoryInterpreterFileLoader {

    @Test
    public void lowLevelTestFMIFL() throws InterruptedException {
        String[] c = {"ALLOC", "ALLOC", "KILL 0", "NOP  25", "KILL 1", "ALLOC", "TENLOC"};

        MemoryInterpreterFileLoader myFl = new MemoryInterpreterFileLoader(Arrays.asList(c));
        MemoryInstruction mi = myFl.getNextStep();
        assertEquals(OpCode.ALLOC, mi.getOp());
        mi = myFl.getNextStep();
        assertEquals(OpCode.ALLOC, mi.getOp());
        mi = myFl.getNextStep();
        assertEquals(OpCode.KILL, mi.getOp());
        assertEquals(0, mi.getParam());
        mi = myFl.getNextStep();
        assertEquals(OpCode.NOP, mi.getOp());
        assertEquals(25, mi.getParam());
        mi = myFl.getNextStep();
        assertEquals(OpCode.KILL, mi.getOp());
        assertEquals(1, mi.getParam());
        mi = myFl.getNextStep();
        assertEquals(OpCode.ALLOC, mi.getOp());
        mi = myFl.getNextStep();
        assertEquals(OpCode.LARGE_ALLOC, mi.getOp());
    }

    @Test
    public void testFMIFLbyStr() throws InterruptedException {
        // Eden = 8; S1, S2 = 2; Tenured = 8
        MemoryModel model = new MemoryModel(new MemoryBlock.MemoryBlockFactory(), 2, 1, 2, 4);
        List<String> commands = new ArrayList<>();

        String[] c = {"ALLOC", "ALLOC", "KILL 0", "ALLOC", "ALLOC", "ALLOC", "ALLOC", "ALLOC", "ALLOC", "ALLOC", "ALLOC", "KILL 2"};
        commands.addAll(Arrays.asList(c));

        executeScript(model, commands);

        // 2L blocks in Eden, 0 in S1, 0 in S2, 6L + 1D in Tenured - 18 allocated
        assertEquals(6, model.getEden().spaceFree()); 
        assertEquals(2, model.getS1().spaceFree()); 
        assertEquals(2, model.getS2().spaceFree()); 
        assertEquals(1, model.getTenured().spaceFree());
    }

    @Test
    public void testFMIFLWTenuredbyStr() throws InterruptedException {
        // Eden = 8; S1, S2 = 2; Tenured = 8
        MemoryModel model = new MemoryModel(new MemoryBlock.MemoryBlockFactory(),  2, 1, 2, 4);
        List<String> commands = new ArrayList<>();

        String[] c = {"ALLOC", "ALLOC", "KILL 0", "ALLOC", "ALLOC", "ALLOC", "ALLOC", "ALLOC", "ALLOC", "ALLOC", "ALLOC", "KILL 1"};
        commands.addAll(Arrays.asList(c));
        // 2L blocks in Eden, 0 in S1, 0 in S2, 6L + 1D in Tenured - 10 allocated

        String[] d = {"KILL 2", "KILL 3", "KILL 4", "KILL 5", "ALLOC", "ALLOC", "ALLOC", "ALLOC"};
        commands.addAll(Arrays.asList(d));
        // 6L blocks in Eden, 0 in S1, 0 in S2, 2L + 5D in Tenured - 14 allocated

        String[] e = {"KILL 11", "KILL 12", "KILL 13", "NOP 5", "ALLOC", "ALLOC", "ALLOC", "ALLOC"};
        commands.addAll(Arrays.asList(e));
        // 2L blocks in Eden, 0 in S1, 0 in S2, 7L in Tenured - 18 allocated

        executeScript(model, commands);
        assertEquals(6, model.getEden().spaceFree());
        assertEquals(2, model.getS1().spaceFree());
        assertEquals(2, model.getS2().spaceFree());
        assertEquals(1, model.getTenured().spaceFree());
    }

    private void executeScript(MemoryModel model, List<String> commands) throws InterruptedException {
        MemoryInterpreterFileLoader myFl = new MemoryInterpreterFileLoader(commands);
        AllocatingThread at = new AllocatingThread(myFl, model);
        ExecutorService srv = Executors.newSingleThreadExecutor();
        srv.submit(at);
        while (!at.isShutdown()) {
            Thread.sleep(250);
        }
        srv.shutdownNow();
    }
}