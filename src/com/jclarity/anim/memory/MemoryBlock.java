package com.jclarity.anim.memory;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Region;

/**
 *
 * @author kittylyst
 */
public class MemoryBlock extends Region {
    
   // private final int id;
    private volatile int generation = 0;
    private volatile boolean alive = true;
    
    private ObjectProperty<MemoryStatus> memoryStatus;
    
    public ObjectProperty<MemoryStatus> memoryStatus() {
        return memoryStatus;
    }
    
    public MemoryStatus getStatus() {
        return memoryStatus.get();
    }
    
    public void setMemoryStatus(MemoryStatus status) {
        memoryStatus.set(status);
    }

    public MemoryBlock() {
       // id = id_;
        memoryStatus = new SimpleObjectProperty<>(this, "owner", 
                MemoryStatus.FREE);

        styleProperty().bind(Bindings.when(memoryStatus.isEqualTo(MemoryStatus.FREE))
                .then("-fx-background-color: radial-gradient(radius 100%, white .1, gray .9, darkgray 1)")
                .otherwise(Bindings.when(memoryStatus.isEqualTo(MemoryStatus.ALLOCATED))
                .then("-fx-background-color: radial-gradient(radius 100%, white .4, gray .9, darkgray 1)")
                .otherwise(Bindings.when(memoryStatus.isEqualTo(MemoryStatus.DEAD))
                .then("-fx-background-colour: radial-gradient(radius 100%, white 0, black .6")
                .otherwise("")
                .concat("; -fx-background-radius: 1000em; -fx-background-insets: 5"))));
    }
    
    void die() {
        alive = false;
    }
    
    void collect() {
        generation++;
    }
    
    public int generation() { return generation; }
    
    public boolean isAlive() { return alive; }
    
    //public int getId() { return id; } 
    
    /**
     * Helper factory to ensure the properties of the MemoryBlock are OK.
     */
    public static class MemoryBlockFactory {
        private final static MemoryBlockFactory inst = new MemoryBlockFactory();
        
        private int seq = 0;
        
        private MemoryBlockFactory() {  }
        
        public static MemoryBlockFactory getInstance() {
            return inst;
        }
        
        public synchronized MemoryBlock getBlock() {
            return new MemoryBlock();
            //return new MemoryBlock(seq++);
        } 
        
    }
    
}
