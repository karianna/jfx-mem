package com.jclarity.anim.memory;

import com.jclarity.anim.memory.model.OpCode;

/**
 * A model class which holds an operation and a parameter
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

    MemoryInstruction(OpCode opCode) {
        op = opCode;
        p = 0;
    }

    public OpCode getOp() {
        return op;
    }

    public int getParam() {
        return p;
    }
}
