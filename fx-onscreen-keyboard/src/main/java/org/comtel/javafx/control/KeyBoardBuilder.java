package org.comtel.javafx.control;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.util.Builder;

import org.comtel.javafx.robot.IRobot;

public class KeyBoardBuilder implements Builder<KeyboardPane> {

	private Path layerPath;
	private DefaultLayers defaultLayer;
	private Locale initLocale;
	private List<IRobot> iRobots;
	private String style;
	private double initScale = 0.0;

	protected KeyBoardBuilder() {
		iRobots = new ArrayList<>(2);
	}

	public static KeyBoardBuilder create() {
		return new KeyBoardBuilder();
	}

	public KeyBoardBuilder layerPath(Path path) {
		layerPath = path;
		return this;
	}

	public KeyBoardBuilder layer(DefaultLayers layer) {
		defaultLayer = layer;
		return this;
	}

	public KeyBoardBuilder style(String css) {
		style = css;
		return this;
	}
	
	public KeyBoardBuilder initLocale(Locale locale) {
		initLocale = locale;
		return this;
	}

	public KeyBoardBuilder initScale(double scale) {
		initScale = scale;
		return this;
	}

	public KeyBoardBuilder addIRobot(IRobot robot) {
		iRobots.add(robot);
		return this;
	}

	@Override
	public KeyboardPane build(){

		KeyboardPane kb = new KeyboardPane();
		
		if (style != null) {
			kb.setKeyBoardStyle(style);
		}
		if (initLocale != null) {
			kb.setLocale(initLocale);
		}
		if (layerPath != null) {
			kb.setLayerPath(layerPath);
		}

		if (defaultLayer != null) {
			kb.setLayer(defaultLayer);
		}
		if (initScale > 0.0) {
			kb.setScale(initScale);
		}
		for (IRobot robot : iRobots) {
			kb.addRobotHandler(robot);
		}
		iRobots.clear();
		iRobots = null;

		try {
			kb.load();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return kb;
	}

}
