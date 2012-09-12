package com.jclarity.anim.memory;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 *
 * @author kittylyst
 */
public class MemoryBlockView extends Rectangle {
    
    
    private ObjectProperty<MemoryStatus> memoryStatus;
    
    private MemoryBlock mine;
    
    public ObjectProperty<MemoryStatus> memoryStatus() {
        return memoryStatus;
    }
    
    public MemoryStatus getStatus() {
        return memoryStatus.get();
    }
    
    public void setMemoryStatus(MemoryStatus status) {
        
        memoryStatus.set(status);
    }

    public MemoryBlockView() {
        super(30, 30, Color.web("gray"));
        
        memoryStatus = new SimpleObjectProperty<>(this, "owner", 
                MemoryStatus.FREE);

        styleProperty().bind(Bindings.when(memoryStatus.isEqualTo(MemoryStatus.FREE))
                .then("-fx-fill: gray ")
                .otherwise(Bindings.when(memoryStatus.isEqualTo(MemoryStatus.ALLOCATED))
                .then("-fx-fill: limegreen")
                .otherwise(Bindings.when(memoryStatus.isEqualTo(MemoryStatus.DEAD))
                .then("-fx-fill: darkred")
                .otherwise("")
                .concat(";"))));
        
        setStrokeType(StrokeType.INSIDE);
        setStroke(Color.web("black"));
        setStrokeWidth(2);
        setArcWidth(15);
        setArcHeight(15); 
    }
    
    void die() {
        memoryStatus.setValue(MemoryStatus.DEAD);
    }
    
    public MemoryBlock getBlock() { return mine; }

    void setBlock(MemoryBlock mb) {
        mine = mb;
        mb.setView(this);
        memoryStatus.setValue(mb.getStatus());
    }
    
}
