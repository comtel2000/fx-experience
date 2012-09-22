package org.comtel.javafx.control;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.util.Builder;

import org.comtel.javafx.robot.IRobot;

public class KeyBoardBuilder implements Builder<KeyBoard> {

	private Path layerPath;
	private Locale initLocale;
	private List<IRobot> iRobots = new ArrayList<>();
	private double initScale = 0.0;

	protected KeyBoardBuilder() {
	}

	public static KeyBoardBuilder create() {
		return new KeyBoardBuilder();
	}

	public KeyBoardBuilder layerPath(Path path) {
		layerPath = path;
		return this;
	}

	public KeyBoardBuilder initLocale(Locale locale) {
		initLocale = locale;
		return this;
	}

	public KeyBoardBuilder initScale(double scale) {
		initScale  = scale;
		return this;
	}
	
	public KeyBoardBuilder addIRobot(IRobot robot) {
		iRobots.add(robot);
		return this;
	}

	public KeyBoard build() {
		KeyBoard keyBoard = new KeyBoard(layerPath, initLocale);
		if (initScale > 0.0){
			keyBoard.setScale(initScale);
		}
		for (IRobot robot : iRobots) {
			keyBoard.addRobotHandler(robot);
		}
		return keyBoard;
	}

}
