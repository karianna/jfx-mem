/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jclarity.anim.memory;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.geometry.Point3D;
import javafx.util.Duration;

/**
 *
 * @author jpgough
 */
class CustomMemoryBlockViewTransition implements Runnable {
    
    private final MemoryBlockView view;

    public CustomMemoryBlockViewTransition(MemoryBlockView view_) {
        view = view_;
    }

    @Override
    public void run() {
        FadeTransition fadeOldBlockOut = new FadeTransition(Duration.millis(100), view);
        fadeOldBlockOut.setFromValue(1.0);
        fadeOldBlockOut.setToValue(0.0);
        fadeOldBlockOut.setCycleCount(1);
        fadeOldBlockOut.setAutoReverse(false);
        fadeOldBlockOut.play();

        FadeTransition fadeNewBlockIn = new FadeTransition(Duration.millis(100), view);
        fadeNewBlockIn.setFromValue(0.0);
        fadeNewBlockIn.setToValue(1.0);
        fadeNewBlockIn.setCycleCount(1);
        fadeNewBlockIn.setAutoReverse(false);
        fadeNewBlockIn.setDelay(Duration.millis(1));
        fadeNewBlockIn.play(); 
        
        RotateTransition rt = new RotateTransition(Duration.millis(200), view);
        rt.setAxis(new Point3D(0, 1, 0));
        rt.setByAngle(360);
        rt.setCycleCount(0);
        
        rt.play();
        fadeNewBlockIn.play();
    }
    
}
