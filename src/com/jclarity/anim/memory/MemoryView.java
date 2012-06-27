/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jclarity.anim.memory;

import javafx.scene.Parent;

/**
 *
 * @author boxcat
 */
public class MemoryView {
    private final int height;
    private final int width;
    private final int wEden;
    private final int wSrv;

    /**
     * Factory method to return the parent node for our memory grid.
     * 
     * @param HEIGHT
     * @param WIDTH
     * @param WIDTH_EDEN
     * @param WIDTH_SRV
     * @return 
     */
    public Parent createParent(){ 
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public MemoryView(int height_, int width_, int wEden_, int wSrv_) {
        height = height_;
        width = width_;
        wEden = wEden_;
        wSrv = wSrv_;
    }
    
}
