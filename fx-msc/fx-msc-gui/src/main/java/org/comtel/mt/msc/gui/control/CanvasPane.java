package org.comtel.mt.msc.gui.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Region;

public class CanvasPane extends Region {

	private ObjectProperty<Canvas> canvasProperty = new SimpleObjectProperty<>();
	private SimpleDoubleProperty scaleProperty = new SimpleDoubleProperty(-1);
	public ObjectProperty<Canvas> CanvasProperty() {
		return canvasProperty;
	}

	public Canvas getCanvas() {
		return canvasProperty.get();
	}

	public void setCanvas(Canvas canvas) {
		scaleProperty.set(canvas.getHeight());
		this.canvasProperty.set(canvas);
	}

	public CanvasPane() {
		this(new Canvas());
	}

	@Override
	protected void layoutChildren() {
		Canvas canvas = canvasProperty.get();
		if (canvas != null) {
			canvas.setWidth(getWidth());
			canvas.setHeight(getHeight());
//			double scale = scaleProperty.get() / getWidth();
//			System.err.println("" + scale);
//			canvas.setScaleX(scale);
//			canvas.setScaleY(scale);
			layoutInArea(canvas, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, VPos.CENTER);
		}
		super.layoutChildren();
	}

	public CanvasPane(Canvas canvas) {
		canvasProperty.addListener(new ChangeListener<Canvas>() {

			@Override
			public void changed(ObservableValue<? extends Canvas> arg0, Canvas oldIV, Canvas newIV) {
				if (oldIV != null) {
					getChildren().remove(oldIV);
				}
				if (newIV != null) {
					getChildren().add(newIV);
				}
			}
		});
		this.canvasProperty.set(canvas);
	}
}