/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jclarity.anim.memory;

import java.util.concurrent.Callable;

/**
 * A class which models a thread which "executes code" and allocates memory. The
 * simulation is designed to potentially have multiple allocating threads
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

    public boolean isShutdown() {
        return isShutdown;
    }
    
    @Override
    public Void call() throws Exception {
        // Some TODOS still to do
        // Kick off simulation loop to poll get next and update view       
        MemoryInstruction ins = memoryInterpreter.getNextStep();
        INTERP:
        while (ins != null && !isShutdown) {
            try {

                switch (ins.getOp()) {
                    case NOP:
                        // Now we can have no-ops with a parameter for sleeping
                        try {
                            long sleepMs = ins.getParam();
                            Thread.sleep(sleepMs);
                        } catch (InterruptedException ex) {
                            isShutdown = true;
                        }
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
                        isShutdown = true;
                        break INTERP;
                    default: // Shouldn't happen
                        isShutdown = true;
                        break INTERP;
                }
                ins = memoryInterpreter.getNextStep();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                isShutdown = true;
            }
        }
        return null;
    }
}
