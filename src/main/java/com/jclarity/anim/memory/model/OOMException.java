package com.jclarity.anim.memory.model;

/**
 *
 */
public class OOMException extends RuntimeException {

    private String msg;
    
    public OOMException(String msg_) {
        msg = msg_;
    }
    // FIXME Need 4 ctors here...
    
    
}
