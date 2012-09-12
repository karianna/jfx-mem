package com.jclarity.anim.memory;

import com.jclarity.anim.memory.model.OpCode;
import static java.lang.Math.random;

/**
 *
 */
public class RandomMemoryInterpreter implements IMemoryInterpreter {

    private final double pAlloc;
    private MemoryInstruction next = new MemoryInstruction(OpCode.ALLOC, 0);
    private int seq = 0;

    public RandomMemoryInterpreter(double pAlloc_) {
        pAlloc = pAlloc_;
    }

    @Override
    public MemoryInstruction getNextStep() {
        if (next != null) {
            MemoryInstruction tmp = next;
            next = null;
            return tmp;
        } else if (random() < pAlloc) {
            next = new MemoryInstruction(OpCode.ALLOC, 0);
        } else {
            next = new MemoryInstruction(OpCode.KILL, seq++);
        }

        return new MemoryInstruction(OpCode.ALLOC, 0);
    }
}
