/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jclarity.anim.memory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author jpgough
 */
public class MemoryMain extends Application {
    
    // FIXME STATIC FOR NOW
    private static int HEIGHT = 8;
    private static int WIDTH = 10;
    private static int WIDTH_EDEN = 2;
    private static int WIDTH_SRV = 3;
    
    
    public static void main(String[] args) {
        Application.launch(MemoryMain.class, args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        MemoryView mv = new MemoryView(HEIGHT, WIDTH, WIDTH_EDEN, WIDTH_SRV);
        MemoryModel mm = new MemoryModel(HEIGHT, WIDTH, WIDTH_EDEN, WIDTH_SRV);
        
        MemoryController mc = new MemoryController(mm, mv);
       
        Group root = new Group();
        Scene scene = new Scene(root, 300, 300, Color.WHEAT);
        scene.getStylesheets().add(getClass().getResource("Memory.css").toExternalForm());
        
        //Hand over the root to the view so it can build itself
        mv.addComponent(root);
        
        //Here we need to bind elements of the view to the model, so as we update the model
        //the state is reflected in the UI. Maybe do this via the controller
        
        stage.setScene(scene);
        stage.show();
    }
}
