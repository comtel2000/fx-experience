module fx.onscreen.keyboard.swing {

  requires transitive fx.onscreen.keyboard;
  requires transitive javafx.base;
  requires transitive java.desktop;
  requires javafx.swing;
  requires org.slf4j;

  exports org.comtel2000.swing.ui;
  exports org.comtel2000.swing.control;
  exports org.comtel2000.swing.robot;

  provides org.comtel2000.keyboard.robot.IRobot with org.comtel2000.swing.robot.NativeAsciiRobotHandler;

}
