package com.jclarity.anim.memory;

/**
 *
 * @author kittylyst
 */
public class MemoryBlock {
    
    private final int id;
    private volatile int generation = 0;
    private volatile boolean alive = true;
    
    private MemoryBlock(int id_) {
        id = id_;
    }
    
    void die() {
        alive = false;
    }
    
    void collect() {
        generation++;
    }
    
    public int generation() { return generation; }
    
    public boolean isAlive() { return alive; }
    
    public int getId() { return id; } 
    
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
            return new MemoryBlock(seq++);
        } 
        
    }
    
}
