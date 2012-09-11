package com.jclarity.anim.memory;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 */
public class MemoryPool {

    private final int width;
    private final int height;
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
}
