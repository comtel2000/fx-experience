/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

class LongPressDelayTimeline extends ButtonTimeline {

  private static final double KEY_LONG_PRESS_DELAY_DEF = 400;

  private static final double KEY_LONG_PRESS_DELAY_MIN = 100;
  private static final double KEY_LONG_PRESS_DELAY_MAX = 1000;

  private static double keyLongPressDelay = KEY_LONG_PRESS_DELAY_DEF;

  static {
    String s = System.getProperty("org.comtel2000.keyboard.longPressDelay");
    if (s != null && !s.isBlank()) {
      try {
        Double delay = Double.valueOf(s);
        keyLongPressDelay =
            Math.min(Math.max(delay, KEY_LONG_PRESS_DELAY_MIN), KEY_LONG_PRESS_DELAY_MAX);
      } catch (NumberFormatException e) {
        // ignore
      }
    }
  }

  protected LongPressDelayTimeline() {
    super(keyLongPressDelay);
  }
}
