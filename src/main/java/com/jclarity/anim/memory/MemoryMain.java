package com.jclarity.anim.memory;

import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author jpgough
 */
public class MemoryMain extends Application {
    
    private FXMLLoader fxmlLoader;
    
    public static void main(String[] args) {
        Application.launch(MemoryMain.class, args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {   
        URL location = getClass().getResource("MemoryMainView.fxml");

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

        Parent root = (Parent) fxmlLoader.load(location.openStream());
        
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("Memory.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("JavaFX Memory Visualizer");
        stage.show();
    }
    
    @Override
    public void stop() {
        ((MemoryController) fxmlLoader.getController()).haltSimulation();
        
    }
 
}
