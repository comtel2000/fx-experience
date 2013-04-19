package org.comtel.mt.msc.gui.communication;

public interface VdmaMessageProcessor {

	void processXML(byte[] xml);
	
	void processImage(byte[] image);
	
	//void connectLost();
}
