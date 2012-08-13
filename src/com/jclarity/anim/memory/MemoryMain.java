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
        Parent root = FXMLLoader.load(getClass().getResource("MemoryMainView.fxml"));
        
        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(getClass().getResource("Memory.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("JavaFX Memory Visualizer");
        stage.show();
    }
}
