/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jclarity.anim.memory;

import javafx.scene.Group;

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
     * Method which will take the root of the stage and build out the memory component
     * 
     * @param HEIGHT
     * @param WIDTH
     * @param WIDTH_EDEN
     * @param WIDTH_SRV
     * @return 
     */
    public void addComponent(Group group){ 
        //throw new UnsupportedOperationException("Not yet implemented");
        System.out.println("Ready to add to group");
    }

    public MemoryView(int height_, int width_, int wEden_, int wSrv_) {
        height = height_;
        width = width_;
        wEden = wEden_;
        wSrv = wSrv_;
    }
    
}
