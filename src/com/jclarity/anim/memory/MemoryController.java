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
    private ComboBox edenColumnsCombo;
    
    @FXML
    private ComboBox survivorColumnsCombo;
    
    @FXML
    private ComboBox tenuredColumnsCombo;
    
    @FXML
    private TextField resourcePath;
    
    @FXML 
    private ComboBox resourceType;
    
    @FXML
    private Button beginButton;

    @FXML
    private void beginSimulation() {
        System.out.println("Begin Simulation");
        System.out.println("Eden Size Requested: " + edenColumnsCombo.getSelectionModel().getSelectedItem());
        System.out.println("Survivor Size Requested: " + survivorColumnsCombo.getSelectionModel().getSelectedItem());
        System.out.println("Tenured Size Requested: " + tenuredColumnsCombo.getSelectionModel().getSelectedItem());
        System.out.println("File Path: " + resourcePath.getText());
        
        switch(resourceType.getSelectionModel().getSelectedItem().toString()) {
            case "File" :  memoryInterpreter = new MemoryInterpreterFileLoader(resourcePath.getText());
                break;
        }
        
        //FIXME - This shouldn't need to do a cast as selection model has a generic type. Figure this out in FXML
        Integer edenColumns = (Integer) edenColumnsCombo.getSelectionModel().getSelectedItem();
        Integer survivorColumns = (Integer) survivorColumnsCombo.getSelectionModel().getSelectedItem();
        Integer tenuredColumns = (Integer) tenuredColumnsCombo.getSelectionModel().getSelectedItem();
        
        model = new MemoryModel(edenColumns, survivorColumns, tenuredColumns);
        
        beginButton.setDisable(true);
        
        //TODO Kick off simulation loop to poll get next and update view
    }
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        resourceType.getSelectionModel().selectFirst();
        edenColumnsCombo.getSelectionModel().selectFirst();
        survivorColumnsCombo.getSelectionModel().selectFirst();
        tenuredColumnsCombo.getSelectionModel().selectFirst();
    }    
}
