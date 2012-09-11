package com.jclarity.anim.memory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    // NOTE The survivor spaces are half the height of the Eden & tenured spaces
    private int height = 8;
    
    // FIXME Move MemoryInterpreter to an AllocatingThread factory
    private IMemoryInterpreter memoryInterpreter;
    private final ExecutorService srv = Executors.newScheduledThreadPool(2);
    
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
        
        
        //FIXME - This shouldn't need to do a cast as selection model has a generic type. Figure this out in FXML
        Integer edenColumns = (Integer) edenColumnsCombo.getSelectionModel().getSelectedItem();
        Integer survivorColumns = (Integer) survivorColumnsCombo.getSelectionModel().getSelectedItem();
        Integer tenuredColumns = (Integer) tenuredColumnsCombo.getSelectionModel().getSelectedItem();
        
        model = new MemoryModel(edenColumns, survivorColumns, tenuredColumns, height);
        
        //Eden setup on the board 
        initialiseMemoryView(model.getEden(), edenGridPane);
        initialiseMemoryView(model.getS1(), s1GridPane);
        initialiseMemoryView(model.getS2(), s2GridPane);
        initialiseMemoryView(model.getTenured(), tenuredGridPane);
        
        beginButton.setDisable(true);
        
        // FIXME needs some refactoring
        switch(resourceType.getSelectionModel().getSelectedItem().toString()) {
            case "File" :  
                 memoryInterpreter = new MemoryInterpreterFileLoader(resourcePath.getText());
//                memoryInterpreter = new MemoryPatternMaker();
                break;
        }
        AllocatingThread at0 = new AllocatingThread(memoryInterpreter, model);
        srv.submit(at0);
    }
    
    public void haltSimulation() {
        srv.shutdownNow();
    }

    /**
     * Setup memory region on the board.
     * 
     * @param pool
     * @param columns
     * @param height_
     * @param gridPane 
     */
    private void initialiseMemoryView(MemoryPool pool, GridPane gridPane) {
        for(int i=0; i < pool.width(); i++) {
            for(int j=0; j < pool.height(); j++) {
                MemoryBlockView block = pool.get(i,j);
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
