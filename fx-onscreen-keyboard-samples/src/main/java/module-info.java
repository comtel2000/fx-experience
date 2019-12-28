module fx.onscreen.keyboard.samples {

  requires fx.onscreen.keyboard;
  requires fx.onscreen.keyboard.swing;

  requires javafx.graphics;
  requires javafx.swing;
  requires javafx.controls;
  requires javafx.media;
  
  opens org.comtel2000.samples.fx to javafx.graphics;

}
