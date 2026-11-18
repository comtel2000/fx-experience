/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.robot;

import java.util.ArrayList;
import java.util.List;

/** {@link IRobot} that records every key sent by the keyboard. */
public class RecordingRobot implements IRobot {

  /** A single key sent to the robot. */
  public record Sent(char ch, boolean ctrl) {}

  private final List<Sent> sent = new ArrayList<>();

  @Override
  public void sendToComponent(Object kb, char ch, boolean ctrl) {
    sent.add(new Sent(ch, ctrl));
  }

  public List<Sent> sent() {
    return sent;
  }

  public String text() {
    var sb = new StringBuilder();
    sent.forEach(s -> sb.append(s.ch()));
    return sb.toString();
  }

  public Sent last() {
    return sent.isEmpty() ? null : sent.get(sent.size() - 1);
  }

  public void clear() {
    sent.clear();
  }
}
