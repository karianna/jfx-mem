package com.jclarity.anim.memory;

import com.jclarity.anim.memory.model.MemoryBlock;
import com.jclarity.anim.memory.model.MemoryStatus;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventType;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author kittylyst
 */
public class MemoryBlockView extends StackPane {

    private Rectangle box;
    private Text text;
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
        super();
        box = new Rectangle(30, 30, Color.web("white"));

        memoryStatus = new SimpleObjectProperty<>(this, "owner",
                MemoryStatus.FREE);

        box.styleProperty().bind(Bindings.when(memoryStatus.isEqualTo(MemoryStatus.FREE))
                .then("-fx-fill: gray")
                .otherwise(Bindings.when(memoryStatus.isEqualTo(MemoryStatus.ALLOCATED))
                .then("-fx-fill: limegreen")
                .otherwise(Bindings.when(memoryStatus.isEqualTo(MemoryStatus.DEAD))
                .then("-fx-fill: darkred")
                .otherwise("")
                .concat(";"))));

        box.setStrokeType(StrokeType.INSIDE);
        box.setStroke(Color.web("white"));
        box.setStrokeWidth(2);
        box.setArcWidth(15);
        box.setArcHeight(15);

        text = new Text("");
        text.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        text.setFill(Color.WHITE);

        getChildren().addAll(box, text);
        
    }

    public void die() {      
        memoryStatus.setValue(MemoryStatus.DEAD);
        setTextOnBox("X");
        animateTransition();
    }

    public MemoryBlock getBlock() {
        return mine;
    }

    public void setBlock(MemoryBlock mb) {
        mine = mb;
        mb.setView(this);
        String txt = mb.generation() > 0 ? ""+ mb.generation() : "";
        setTextOnBox(txt);
        memoryStatus.setValue(mb.getStatus());
        animateTransition();
    }
    
    private void animateTransition() {
       Platform.runLater(new CustomMemoryBlockViewTransition(this));
    }


    private void setTextOnBox(String text_) {
        text.setText(text_);
    }
}
