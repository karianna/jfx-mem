/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jclarity.anim.memory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
       
        Parent root = mv.createParent();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Memory.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
