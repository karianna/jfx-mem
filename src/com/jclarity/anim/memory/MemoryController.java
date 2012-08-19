/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jclarity.anim.memory;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.PaneBuilder;

/**
 *
 * @author jpgough
 */
public class MemoryController implements Initializable {
    
    private MemoryModel model;
    //Probably not needed as we will update by bindings
    //private MemoryView view;
    private IMemoryInterpreter memoryInterpreter;
    
    private int height = 8;
    
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
    private GridPane edenGridPane;
    
    @FXML
    private GridPane s1GridPane;
    
    @FXML
    private GridPane s2GridPane;
    
    @FXML
    private GridPane tenuredGridPane;

    @FXML
    private void beginSimulation() {
        System.out.println("Begin Simulation");
        System.out.println("Eden Size Requested: " + edenColumnsCombo.getSelectionModel().getSelectedItem());
        System.out.println("Survivor Size Requested: " + survivorColumnsCombo.getSelectionModel().getSelectedItem());
        System.out.println("Tenured Size Requested: " + tenuredColumnsCombo.getSelectionModel().getSelectedItem());
        System.out.println("File Path: " + resourcePath.getText());
        
        switch(resourceType.getSelectionModel().getSelectedItem().toString()) {
            case "File" :  
                // memoryInterpreter = new MemoryInterpreterFileLoader(resourcePath.getText());
                memoryInterpreter = new MemoryPatternMaker();
                break;
        }
        

        //FIXME - This shouldn't need to do a cast as selection model has a generic type. Figure this out in FXML
        Integer edenColumns = (Integer) edenColumnsCombo.getSelectionModel().getSelectedItem();
        Integer survivorColumns = (Integer) survivorColumnsCombo.getSelectionModel().getSelectedItem();
        Integer tenuredColumns = (Integer) tenuredColumnsCombo.getSelectionModel().getSelectedItem();
        
        model = new MemoryModel(edenColumns, survivorColumns, tenuredColumns, height);
        
        //Eden setup on the board 
        initialiseMemoryView(model.getEden(), edenColumns, edenGridPane);
        initialiseMemoryView(model.getS1(), survivorColumns, s1GridPane);
        initialiseMemoryView(model.getS2(), survivorColumns, s2GridPane);
        initialiseMemoryView(model.getTenured(), tenuredColumns, tenuredGridPane);
        
        beginButton.setDisable(true);
        
        //Test allocation
        model.getEden()[0][0].get().setMemoryStatus(MemoryStatus.ALLOCATED);
  
        //FIXME - reintroduce this code after we have some blocks on screen
        
        //TODO Kick off simulation loop to poll get next and update view
//        MemoryInstruction ins = memoryInterpreter.getNextStep();
//        INTERP: while (ins != null) {
//            switch (ins.getOp()) {
//                case NOP: 
//                    break;
//                case ALLOC:
//                case LARGE_ALLOC: 
//                    model.allocate();
//                    // FIXME Do we need to update the model manually?
//                    break;
//                case KILL:
//                    // FIXME ins.getParam()
//                    model.destroy(0);
//                    break;
//                case EOF: 
//                    break INTERP;
//                default: // Shouldn't happen 
//                    break INTERP; 
//            }
//            ins = memoryInterpreter.getNextStep();
//        }
        
    }
    
    private void initialiseMemoryView(ObjectProperty<MemoryBlock>[][] modelArray, int columns, GridPane gridPane) {
               //Eden setup on the board 
        for(int i=0; i < columns; i++) {
            for(int j=0; j < height; j++) {
                MemoryBlock block = modelArray[i][j].get();
                gridPane.add(PaneBuilder.create().children(block).build(), i, j);
            }
        } 
    }
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        resourceType.getSelectionModel().selectFirst();
        edenColumnsCombo.getSelectionModel().selectFirst();
        survivorColumnsCombo.getSelectionModel().selectFirst();
        tenuredColumnsCombo.getSelectionModel().selectFirst();
    }    
}
