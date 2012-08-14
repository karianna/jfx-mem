/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jclarity.anim.memory;

/**
 *
 * @author jpgough
 */
public class MemoryInterpreterFileLoader implements IMemoryInterpreter {
    
    private final String filePath;
    
    public MemoryInterpreterFileLoader(String filePath) {
        //Potentially load the file here rather than holding the string
        this.filePath = filePath;
    }

    @Override
    public MemoryInstruction getNextStep() {
        //1. Process next step in the file
        
        //2. Add to the model
        
        //Model will do the validation of the add and move the memory around
        //From generation to generation as appropriate
        
        return null;
    }
    
}
