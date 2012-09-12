/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jclarity.anim.memory;

import javafx.animation.FadeTransition;
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
        FadeTransition fadeOldBlockOut = new FadeTransition(Duration.millis(10), view);
        fadeOldBlockOut.setFromValue(1.0);
        fadeOldBlockOut.setToValue(0.0);
        fadeOldBlockOut.setCycleCount(1);
        fadeOldBlockOut.setAutoReverse(false);
        fadeOldBlockOut.play();

        FadeTransition fadeNewBlockIn = new FadeTransition(Duration.millis(1500), view);
        fadeNewBlockIn.setFromValue(0.0);
        fadeNewBlockIn.setToValue(1.0);
        fadeNewBlockIn.setCycleCount(1);
        fadeNewBlockIn.setAutoReverse(false);
        fadeNewBlockIn.setDelay(Duration.millis(1));
        fadeNewBlockIn.play(); 
    }
    
}
