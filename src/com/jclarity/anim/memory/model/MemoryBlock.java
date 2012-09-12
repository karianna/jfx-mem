package com.jclarity.anim.memory.model;

import com.jclarity.anim.memory.MemoryBlockView;
import com.jclarity.anim.memory.model.MemoryStatus;

/**
 * This is a model class
 *
 */
public class MemoryBlock {

    // Used to denote position of an allocated block in memory alloc list
    private final int id;
    private volatile int generation = 0;
    private boolean touched = false;
    private MemoryBlockView view;
    private MemoryStatus memoryStatus = MemoryStatus.ALLOCATED;

    private MemoryBlock(int id_) {
        id = id_;
    }

    void collect() {
        generation++;
    }

    public int generation() {
        return generation;
    }

    public synchronized void mark() {
        if (touched) return;
        generation++;
        touched = true;
    }

    public synchronized void unmark() {
        touched = false;
    }

    public int getBlockId() {
        return id;
    }

    public MemoryBlockView getView() {
        return view;
    }

    public void setView(MemoryBlockView view_) {
        view = view_;
    }

    public void die() {
        memoryStatus = MemoryStatus.DEAD;
        view.die();
    }

    public MemoryStatus getStatus() {
        return memoryStatus;
    }

    /**
     * Helper factory to ensure the properties of the MemoryBlockView are OK.
     */
    public static class MemoryBlockFactory {
        private volatile int seq = 0;

        public MemoryBlockFactory() {
        }

        /**
         * Public factory method
         *
         * @return
         */
        public synchronized MemoryBlock getBlock() {
            return new MemoryBlock(seq++);
        }

        /**
         * Package factory method used in the MemoryModel when resetting Eden
         *
         * @return
         */
        public MemoryBlock getFreeBlock() {
            MemoryBlock out = new MemoryBlock(0);
            out.memoryStatus = MemoryStatus.FREE;
            return out;
        }

        public void reset() {
            seq = 0;
        }
    }
}
