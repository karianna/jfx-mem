/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jclarity.anim.memory;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 *
 * @author jpgough
 */
public class MemoryController implements Initializable {
    
    private final MemoryModel model;
    private final MemoryView view;
    
    public MemoryController(MemoryModel mm, MemoryView mv) {
        model = mm;
        view = mv;
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    

}
