/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jclarity.anim.memory;

import com.jclarity.anim.memory.model.MemoryInstruction;
import com.jclarity.anim.memory.model.OpCode;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class TestRandomMemory {
    private int RUNS = 10_000_000;
    
    @Test
    public void testMultiAlloc() throws Exception {
        RandomMemoryInterpreter rm = new RandomMemoryInterpreter(0.01);
        byte[] allocTable = new byte[RUNS];
        
        for (int i = 0; i < RUNS; ++i) {
            MemoryInstruction mi = rm.getNextStep();
            System.out.println(mi);
            if (mi.getOp() == OpCode.ALLOC) {
                allocTable[mi.getParam()] = 1;
            } else if (mi.getOp() == OpCode.KILL) {
                allocTable[mi.getParam()] = 0;
            } else {
                throw new Exception("Illegal state encountered: "+ mi.getOp());
            }
        }
        int countAlloc =0;
        for (int i = 0; i < RUNS; ++i) {
            countAlloc += allocTable[i];
        }
        System.out.println("Allocs: "+ countAlloc);
        assertEquals(0.01, (double)countAlloc / RUNS, 0.001);
    }

}
