module fx.onscreen.keyboard {

    requires javafx.graphics;
    requires javafx.controls;
    requires java.desktop;
    requires org.slf4j;

    exports org.comtel2000.keyboard.robot;
    exports org.comtel2000.keyboard.control;

    /**provides org.comtel2000.keyboard.robot.IRobot
     with org.comtel2000.keyboard.robot.FXRobotHandler; */
}