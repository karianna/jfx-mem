package com.jclarity.anim.memory;

/**
 * The point of this class is to allocate a deterministic / predictable amount 
 * of memory.
 * 
 * This is to be used for simple testing until the File-based and other 
 * approaches are ready
 * 
 */
class SimpleTemporaryMemoryInterpreter implements IMemoryInterpreter {

    private volatile int seq = 1;
    
    private volatile int alloc_seq = 0;
    
    private volatile int free_seq = 0;
    
    @Override
    public MemoryInstruction getNextStep() {
        seq++;
        
        if ((seq % 5 == 0) || (seq % 5 == 3)) {
            return new MemoryInstruction(OpCode.KILL, free_seq++);
        } else {
            return new MemoryInstruction(OpCode.ALLOC, alloc_seq++);
        }
        
    }

}
