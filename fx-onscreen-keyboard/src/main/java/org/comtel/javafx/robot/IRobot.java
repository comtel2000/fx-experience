package org.comtel.javafx.robot;

import org.comtel.javafx.KeyBoardPanel;

public interface IRobot {

	void sendToComponent(KeyBoardPanel scene, char ch, boolean ctrl);
}
