package com.jclarity.anim.memory;

/**
 *
 * @author kittylyst
 */
public class MemoryInstruction {
    
    private final OpCode op;

    private final int p;
    
    MemoryInstruction(OpCode opCode, int i) {
        op = opCode;
        p = i;
    }
    
    public OpCode getOp() { return op; }
    
    public int getParam() { return p; }
    
    // FIXME Currently, this doesn't say which application thread is allocating 
    // the memory....
    
    // Addendum - nor should it, this is a fucking model class....
    
    
}
