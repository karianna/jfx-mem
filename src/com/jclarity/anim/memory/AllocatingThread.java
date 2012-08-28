/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jclarity.anim.memory;

import java.util.concurrent.Callable;

/**
 * A class which models a thread which "executes code" and allocates memory
 * 
 */
public class AllocatingThread implements Callable<Void> {
    
    private final IMemoryInterpreter memoryInterpreter;
    private final MemoryModel model;
    
    public AllocatingThread(IMemoryInterpreter memoryInterpreter_, MemoryModel model_) {
        model = model_;
        memoryInterpreter = memoryInterpreter_;
    }

    @Override
    public Void call() throws Exception {
         // Some TODOS still to do
        // Kick off simulation loop to poll get next and update view
        MemoryInstruction ins = memoryInterpreter.getNextStep();
        INTERP: while (ins != null) {
            switch (ins.getOp()) {
                case NOP: 
                    break;
                case ALLOC:
                case LARGE_ALLOC: 
                    model.allocate();
                    // FIXME Do we need to update the model manually?
                    break;
                case KILL:
                    // FIXME ins.getParam()
                    model.destroy(ins.getParam());
                    break;
                case EOF: 
                    break INTERP;
                default: // Shouldn't happen 
                    break INTERP; 
            }
            ins = memoryInterpreter.getNextStep();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
        return null;
    }

}
