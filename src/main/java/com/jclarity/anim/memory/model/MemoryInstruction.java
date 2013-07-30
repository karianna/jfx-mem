package com.jclarity.anim.memory.model;

/**
 * A model class which holds an operation and a parameter
 *
 * @author kittylyst
 */
public class MemoryInstruction {

    private final OpCode op;
    private final int p;
    private int threadId;

    @Override
    public String toString() {
        return "MemoryInstruction{" + "op=" + op + ", p=" + p + ", threadId=" + threadId + '}';
    }

    public MemoryInstruction(OpCode op_, int p_) {
        op = op_;
        p = p_;
    }

    public MemoryInstruction(OpCode opCode) {
        op = opCode;
        p = 0;
    }

    public OpCode getOp() {
        return op;
    }

    public int getParam() {
        return p;
    }

    public void setThreadId(int threadId_) {
        threadId = threadId_;
    }
    public int getThreadId() { return threadId; }
}
