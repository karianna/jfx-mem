package com.jclarity.anim.memory;

import com.jclarity.anim.memory.model.MemoryInstruction;
import com.jclarity.anim.memory.model.OpCode;
import static java.lang.Math.random;

/**
 * This class should, under almost all circumstances, emit an ALLOC followed by
 * a KILL for the block it has just allocated.
 */
public class RandomMemoryInterpreter implements IMemoryInterpreter {

    private final double pAlloc;
    private MemoryInstruction next = new MemoryInstruction(OpCode.ALLOC, 0);
    private int seq = 0;

    public RandomMemoryInterpreter(double pAlloc_) {
        pAlloc = pAlloc_;
    }

    @Override
    public synchronized MemoryInstruction getNextStep() {
        if (next != null) {
            MemoryInstruction tmp = next;
            next = null;
            return tmp;
        } else if (random() < pAlloc) {
            next = new MemoryInstruction(OpCode.ALLOC, ++seq + 1); // 3
            return new MemoryInstruction(OpCode.ALLOC, seq);
        } else {
            next = new MemoryInstruction(OpCode.KILL, seq++);
        }

        return new MemoryInstruction(OpCode.ALLOC, seq);
    }
}
