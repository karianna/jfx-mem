package com.jclarity.anim.memory;

/**
 *
 * @author kittylyst
 */
public class MemoryInstruction {
    
    private final OpCode op;
    
    public MemoryInstruction(OpCode op_) {
        op = op_;
    }
    
    public OpCode getOp() { return op; }
    
    // FIXME Currently, this doesn't say which application thread is allocating 
    // the memory....
    
    
}
