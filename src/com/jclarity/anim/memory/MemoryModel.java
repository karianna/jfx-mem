/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jclarity.anim.memory;


/**
 * This class implements 
 * 
 * @author boxcat
 */
public class MemoryModel {

    private final int height;
    private final int width;
    private final int wEden;
    private final int wSrv;

    private final MemoryBlock[][] eden;
    private final MemoryBlock[][] s1;
    private final MemoryBlock[][] s2;
    private final MemoryBlock[][] tenured;
    
    private int tenuringThreshold = 4;
    
    MemoryModel(int height_, int width_, int wEden_, int wSrv_) {
        height = height_;
        width = width_;
        wEden = wEden_;
        wSrv = wSrv_;
        eden = new MemoryBlock[wEden][height];
        s1 = new MemoryBlock[wSrv][height];
        s2 = new MemoryBlock[wSrv][height];
        tenured = new MemoryBlock[width - (wEden + wSrv)][height];
    }

    
}
