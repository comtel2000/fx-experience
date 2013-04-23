package org.comtel.mt.msc.gui.communication;


public interface VdmaMessageProcessor {

	void processXML(byte[] xml, byte[] image);
	
	void processXML(byte[] xml);

	//void connectionLost();
}
