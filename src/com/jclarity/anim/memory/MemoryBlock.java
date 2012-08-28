package com.jclarity.anim.memory;

/**
 * This is a model class
 *
 */
public class MemoryBlock {

    private final int id;
    private volatile int generation = 0;
    private MemoryBlockView view;
    private MemoryStatus memoryStatus = MemoryStatus.ALLOCATED;
    
    private MemoryBlock(int id_) {
        id = id_;
    }

    void collect() {
        generation++;
    }
    
    public int generation() { return generation; }
    
    public int getBlockId() { return id; } 
    
    public MemoryBlockView getView() { return view; }
    
    void die() {
        memoryStatus = MemoryStatus.DEAD;
        view.die();
    }

    MemoryStatus getStatus() {
        return memoryStatus;
    }

    /**
     * Helper factory to ensure the properties of the MemoryBlockView are OK.
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
