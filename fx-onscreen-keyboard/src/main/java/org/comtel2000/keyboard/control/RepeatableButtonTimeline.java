/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

import javafx.animation.Animation;

class RepeatableButtonTimeline extends ButtonTimeline {

  private static final double KEY_REPEAT_RATE_DEF = 25;

  private static final double KEY_REPEAT_RATE_MIN = 2;
  private static final double KEY_REPEAT_RATE_MAX = 50;

  // key repeat rate (cps)
  private static double keyRepeatRate = KEY_REPEAT_RATE_DEF;

  static {
    String s = System.getProperty("org.comtel2000.keyboard.repeatRate");
    if (s != null && !s.isBlank()) {
      try {
        double delay = Double.parseDouble(s);
        keyRepeatRate = Math.min(Math.max(delay, KEY_REPEAT_RATE_MIN), KEY_REPEAT_RATE_MAX);
      } catch (NumberFormatException e) {
        // ignore
      }
    }
  }

  RepeatableButtonTimeline() {
    super(keyRepeatRate);
    timeline.setCycleCount(Animation.INDEFINITE);
  }
}
