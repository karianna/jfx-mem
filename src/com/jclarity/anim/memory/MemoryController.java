/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jclarity.anim.memory;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 *
 * @author jpgough
 */
public class MemoryController implements Initializable {
    
    private MemoryModel model;
    //Probably not needed as we will update by bindings
    //private MemoryView view;
    private IMemoryInterpreter memoryInterpreter;
    
    @FXML
    private TextField edenSize;
    
    @FXML
    private TextField survivorOneSize;
    
    @FXML
    private TextField survivorTwoSize;
    
    @FXML
    private TextField tenuredSize;
    
    @FXML
    private TextField resourcePath;
    
    @FXML 
    private ComboBox resourceType;
    
    @FXML
    private Button beginButton;

    @FXML
    private void beginSimulation() {
        System.out.println("Begin Simulation");
        System.out.println("Eden Size Requested: " + edenSize.getText());
        System.out.println("Survivor One Size Requested: " + survivorOneSize.getText());
        System.out.println("Survivor Two Size Requested: " + survivorTwoSize.getText());
        System.out.println("Tenured Size Requested: " + tenuredSize.getText());
        
        switch(resourceType.getSelectionModel().getSelectedItem().toString()) {
            case "File" :  memoryInterpreter = new MemoryInterpreterFileLoader(resourcePath.getText());
                break;
        }
        
        // FIXME
        model = new MemoryModel(1, 2, 3);
        
        beginButton.setDisable(true);
        
        //TODO Kick off simulation loop to poll get next and update view
        MemoryInstruction ins = memoryInterpreter.getNextStep();
        INTERP: while (ins != null) {
            switch (ins.getOp()) {
                case NOP: 
                    break;
                case ALLOC:
                case LARGE_ALLOC: 
                    model.allocate();
                    // FIXME Do we need to update the model manually?
                    break;
                case EOF: 
                    break INTERP;
                default: // Shouldn't happen 
                    break INTERP; 
            }
            ins = memoryInterpreter.getNextStep();
        }
        
    }
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        
    }    
}
