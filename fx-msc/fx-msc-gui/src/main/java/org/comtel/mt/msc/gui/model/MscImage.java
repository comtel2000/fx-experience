package org.comtel.mt.msc.gui.model;

import org.comtel.mt.msc.gui.model.jaxb.MSCGUI;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

public class MscImage {

	private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();

	private ObjectProperty<byte[]> xmlProperty = new SimpleObjectProperty<>();

	private ObjectProperty<MSCGUI> guiProperty = new SimpleObjectProperty<>();
	
	public ObjectProperty<Image> imageProperty() {
		return imageProperty;
	}

	public Image getImage() {
		return imageProperty.get();
	}

	public void setXML(byte[] xml) {
		this.xmlProperty.set(xml);
	}

	public byte[] getXML() {
		return xmlProperty.get();
	}

	public void setImage(Image image) {
		this.imageProperty.set(image);
	}

	public MSCGUI getMSCGUI() {
		return guiProperty.get();
	}

	public void setMSCGUI(MSCGUI gui) {
		this.guiProperty.set(gui);
	}
	
	public MscImage() {
		this(null, null);
	}

	public MscImage(Image img, byte[] xml) {
		setImage(img);
		setXML(xml);
	}

}
