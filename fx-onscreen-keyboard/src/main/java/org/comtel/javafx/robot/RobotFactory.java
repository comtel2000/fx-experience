package org.comtel.javafx.robot;

public class RobotFactory {

	public static IRobot createFXRobot(){
		return new FXRobotHandler();
	}
	
	public static IRobot createAWTRobot(){
		return new AWTRobotHandler();
	}
}
