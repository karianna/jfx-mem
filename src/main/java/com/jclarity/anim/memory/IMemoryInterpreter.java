package com.jclarity.anim.memory;

import com.jclarity.anim.memory.model.MemoryInstruction;

/**
 *
 * @author jpgough
 */
public interface IMemoryInterpreter {
    public MemoryInstruction getNextStep();
}
