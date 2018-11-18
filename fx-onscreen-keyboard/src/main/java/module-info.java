module fx.onscreen.keyboard {

  requires transitive javafx.base;
  requires transitive javafx.graphics;
  requires javafx.controls;
  requires org.slf4j;
  requires java.xml;
  requires java.desktop;

  exports org.comtel2000.keyboard.robot;
  exports org.comtel2000.keyboard.control;
  exports org.comtel2000.keyboard.event;

  provides org.comtel2000.keyboard.robot.IRobot with org.comtel2000.keyboard.robot.FXRobotHandler;

}