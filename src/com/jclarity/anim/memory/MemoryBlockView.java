package com.jclarity.anim.memory;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Region;

/**
 *
 * @author kittylyst
 */
public class MemoryBlockView extends Region {
    
    
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
        memoryStatus = new SimpleObjectProperty<>(this, "owner", 
                MemoryStatus.FREE);

        styleProperty().bind(Bindings.when(memoryStatus.isEqualTo(MemoryStatus.FREE))
                .then("-fx-background-color: radial-gradient(radius 100%, white .1, gray .9, darkgray 1)")
                .otherwise(Bindings.when(memoryStatus.isEqualTo(MemoryStatus.ALLOCATED))
                .then("-fx-background-color: radial-gradient(radius 100%, blue .4, gray .9, darkgray 1)")
                .otherwise(Bindings.when(memoryStatus.isEqualTo(MemoryStatus.DEAD))
                .then("-fx-background-colour: radial-gradient(radius 100%, white 0, black .6")
                .otherwise("")
                .concat("; -fx-background-radius: 1000em; -fx-background-insets: 5"))));
        
        setPrefSize(30, 30);
    }
    
    void die() {
        memoryStatus.setValue(MemoryStatus.DEAD);
    }
    
    public MemoryBlock getBlock() { return mine; }

    void setBlock(MemoryBlock mb) {
        mine = mb;
        memoryStatus.setValue(mb.getStatus());
    }
    
}
