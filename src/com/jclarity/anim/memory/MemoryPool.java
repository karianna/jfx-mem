package com.jclarity.anim.memory;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 */
public abstract class MemoryPool {

    protected final int width;
    protected final int height;
    private final ObjectProperty<MemoryBlockView>[][] view;
    private final MemoryBlock.MemoryBlockFactory factory = MemoryBlock.MemoryBlockFactory.getInstance();

    /**
     * Standard constructor
     *
     * @param width_
     * @param height_
     */
    public MemoryPool(int width_, int height_) {
        width = width_;
        height = height_;
        view = new ObjectProperty[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height_; j++) {
                view[i][j] = new SimpleObjectProperty<>();
                view[i][j].set(new MemoryBlockView());
            }
        }
    }

    /**
     * Try to add a supplied block to this pool
     *
     * @param mb
     * @return
     */
    boolean tryAdd(MemoryBlock mb) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (view[i][j].getValue().getStatus() == MemoryStatus.FREE) {
                    view[i][j].getValue().setBlock(mb);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Accessor for values contained in view
     *
     * @param i
     * @param j
     * @return
     */
    MemoryBlockView getValue(int i, int j) {
        return view[i][j].getValue();
    }

    /**
     * Accessor for view
     *
     * @param i
     * @param j
     * @return
     */
    MemoryBlockView get(int i, int j) {
        return view[i][j].get();
    }

    /**
     * Resets this pool to completely free state
     */
    void reset() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                view[i][j].getValue().setBlock(factory.getFreeBlock());
            }
        }
    }

    /**
     * Compact the pool
     */
    abstract void compact();

    int width() {
        return width;
    }

    int height() {
        return height;
    }

    int spaceFree() {
        int free = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (view[i][j].getValue().getStatus() == MemoryStatus.FREE) {
                    free++;
                }
            }
        }
        return free;
    }

    int size() {
        return width * height;
    }

    static class Eden extends MemoryPool {

        public Eden(int width_, int height_) {
            super(width_, height_);
        }

        @Override
        void compact() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    static class SurvivorSpace extends MemoryPool {

        public SurvivorSpace(int width_, int height_) {
            super(width_, height_);
        }

        @Override
        void compact() {
        }
    }

    static class Tenured extends MemoryPool {

        public Tenured(int width_, int height_) {
            super(width_, height_);
        }

        @Override
        void compact() {
            System.out.println("Trying a tenured compaction");

            final List<MemoryBlock> evacuees = new ArrayList<>();

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    MemoryBlockView mbv = getValue(i, j);
                    if (mbv.getStatus() == MemoryStatus.ALLOCATED) {
                        MemoryBlock alive = mbv.getBlock();
                        alive.mark();
                        evacuees.add(alive);
                    }
                }
            }
            System.out.println(evacuees.size() +" blocks survived tenured compaction");
            // At this point, we have all the live blocks still in tenured
            reset();
            for (MemoryBlock mb : evacuees) {
                // This should always succeed - we never have more in evacuees than 
                // will fit into tenured
                tryAdd(mb);
            }
        }
    }
}
