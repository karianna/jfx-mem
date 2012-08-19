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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.PaneBuilder;
import javafx.scene.layout.StackPaneBuilder;

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
    private GridPane edenGridPane;

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
        
        model = new MemoryModel(edenColumns, survivorColumns, tenuredColumns);
        
        //Eden setup on the board TODO move height somewhere consistent
        for(int i=0; i < edenColumns; i++) {
            for(int j=0; j < 8; j++) {
                System.out.println("Build a block");
                MemoryBlock block = model.getEden()[i][j].get();
                System.out.println( block.getStatus() );
                System.out.println( block.getStyle());
                System.out.println( "H: " +  block.getHeight());
                System.out.println( "W: " + block.getWidth());
                edenGridPane.add(PaneBuilder.create().children(block).build(), i, j);
            }
        }


        System.out.println("Finish mem construction");
        
        beginButton.setDisable(true);
  
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
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        resourceType.getSelectionModel().selectFirst();
        edenColumnsCombo.getSelectionModel().selectFirst();
        survivorColumnsCombo.getSelectionModel().selectFirst();
        tenuredColumnsCombo.getSelectionModel().selectFirst();
    }    
}
