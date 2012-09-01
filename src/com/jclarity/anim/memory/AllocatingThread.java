/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jclarity.anim.memory;

import java.util.concurrent.Callable;

/**
 * A class which models a thread which "executes code" and allocates memory.
 * The simulation is designed to potentially have multiple allocating threads
 * running at once.
 * 
 * FIXME Some operations are currently multiple-allocation-thread-safe but not
 * all of them yet
 * 
 */
public class AllocatingThread implements Callable<Void> {
    
    private final IMemoryInterpreter memoryInterpreter;
    private final MemoryModel model;
    
    private volatile boolean isShutdown = false;
    
    public AllocatingThread(IMemoryInterpreter memoryInterpreter_, MemoryModel model_) {
        model = model_;
        memoryInterpreter = memoryInterpreter_;
    }

    @Override
    public Void call() throws Exception {
         // Some TODOS still to do
        // Kick off simulation loop to poll get next and update view       
        MemoryInstruction ins = memoryInterpreter.getNextStep();
        INTERP: while (ins != null && !isShutdown) {
            switch (ins.getOp()) {
                case NOP: 
                    break;
                case ALLOC:
                case LARGE_ALLOC: 
                    // FIXME Need to deal with a large allocation differently
                    // Maybe allocate two blocks - directly in Tenured?
                    model.allocate();
                    break;
                case KILL:
                    model.destroy(ins.getParam());
                    break;
                case EOF: 
                    break INTERP;
                default: // Shouldn't happen 
                    break INTERP; 
            }
            ins = memoryInterpreter.getNextStep();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                isShutdown = true;
            }
        }
        return null;
    }

}
