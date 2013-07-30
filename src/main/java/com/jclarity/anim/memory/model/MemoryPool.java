package com.jclarity.anim.memory.model;

import com.jclarity.anim.memory.model.MemoryStatus;
import com.jclarity.anim.memory.model.MemoryBlock.MemoryBlockFactory;
import com.jclarity.anim.memory.MemoryBlockView;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 */
public class MemoryPool {

    protected final int width;
    protected final int height;
    private final ObjectProperty<MemoryBlockView>[][] view;
    private final MemoryBlockFactory factory;

    /**
     * Standard constructor
     *
     * @param width_
     * @param height_
     */
    public MemoryPool(MemoryBlockFactory fact, int width_, int height_) {
        factory = fact;
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
    public boolean tryAdd(MemoryBlock mb) {
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
    public MemoryBlockView getValue(int i, int j) {
        return view[i][j].getValue();
    }

    /**
     * Accessor for view
     *
     * @param i
     * @param j
     * @return
     */
    public MemoryBlockView get(int i, int j) {
        return view[i][j].get();
    }

    /**
     * Resets this pool to completely free state
     */
    public void reset() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                view[i][j].getValue().setBlock(factory.getFreeBlock());
            }
        }
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int spaceFree() {
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

    public int size() {
        return width * height;
    }

    public static class Tenured extends MemoryPool {

        public Tenured(MemoryBlockFactory fact_, int width_, int height_) {
            super(fact_, width_, height_);
        }

        public void compact() {
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
