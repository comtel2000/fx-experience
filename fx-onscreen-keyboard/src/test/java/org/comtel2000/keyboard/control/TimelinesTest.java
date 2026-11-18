/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

import static org.comtel2000.keyboard.control.FxTestSupport.runFx;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import javafx.animation.Animation.Status;
import javafx.event.ActionEvent;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TimelinesTest {

  @BeforeAll
  static void initToolkit() {
    FxTestSupport.startToolkit();
  }

  @Test
  void timelinesAreCreatedLazilyAndReused() {
    runFx(
        () -> {
          var t = new Timelines();
          // stopping before creation must not fail
          t.stop();
          assertSame(t.longPressDelayTimeline(), t.longPressDelayTimeline());
          assertSame(t.repeatableButtonTimeline(), t.repeatableButtonTimeline());
          assertTrue(t.longPressDelayTimeline() instanceof LongPressDelayTimeline);
          assertTrue(t.repeatableButtonTimeline() instanceof RepeatableButtonTimeline);
          assertTrue(t.longPressDelayTimeline().isEnabled());
          assertTrue(t.repeatableButtonTimeline().isEnabled());
        });
  }

  @Test
  void ownerChangeStopsRunningTimeline() {
    runFx(
        () -> {
          var t = new Timelines();
          var n1 = new Pane();
          var n2 = new Pane();
          var timeline = t.longPressDelayTimeline(n1, e -> {});
          assertTrue(timeline.isOwner(n1));
          assertFalse(timeline.isOwner(n2));
          assertFalse(timeline.isOwner(null));

          timeline.playFromStart();
          assertEquals(Status.RUNNING, timeline.getStatus());
          assertTrue(timeline.getCurrentRate() > 0);

          // same owner keeps running
          t.longPressDelayTimeline(n1, e -> {});
          assertEquals(Status.RUNNING, timeline.getStatus());

          t.longPressDelayTimeline(n2, e -> {});
          assertEquals(Status.STOPPED, timeline.getStatus());
          assertTrue(timeline.isOwner(n2));
        });
  }

  @Test
  void stopStopsBothTimelines() {
    runFx(
        () -> {
          var t = new Timelines();
          var node = new Pane();
          t.longPressDelayTimeline(node, e -> {}).playFromStart();
          t.repeatableButtonTimeline(node, e -> {}).playFromStart();
          t.stop();
          assertEquals(Status.STOPPED, t.longPressDelayTimeline().getStatus());
          assertEquals(Status.STOPPED, t.repeatableButtonTimeline().getStatus());

          t.repeatableButtonTimeline().playFromStart();
          t.stopLongPressDelayTimeline();
          assertEquals(Status.RUNNING, t.repeatableButtonTimeline().getStatus());
          t.stopRepeatableButtonTimeline();
          assertEquals(Status.STOPPED, t.repeatableButtonTimeline().getStatus());
        });
  }

  @Test
  void handlerIsCalled() {
    runFx(
        () -> {
          var count = new AtomicInteger();
          var timeline =
              new Timelines().repeatableButtonTimeline(new Pane(), e -> count.incrementAndGet());
          timeline.handle(new ActionEvent());
          assertEquals(1, count.get());
        });
  }

  @Test
  void handlerIsOptional() {
    runFx(() -> new LongPressDelayTimeline().handle(new ActionEvent()));
  }

  @Test
  void longPressDelayFiresAfterDelay() {
    var count = new AtomicInteger();
    runFx(
        () ->
            new Timelines()
                .longPressDelayTimeline(new Pane(), e -> count.incrementAndGet())
                .playFromStart());
    assertTrue(FxTestSupport.waitFor(() -> count.get() == 1, 3000));
  }

  @Test
  void timelineWithoutDelayIsDisabled() {
    runFx(
        () -> {
          var noDelay = new ButtonTimeline(0) {};
          assertFalse(noDelay.isEnabled());
          var nanDelay = new ButtonTimeline(Double.NaN) {};
          assertFalse(nanDelay.isEnabled());
        });
  }
}
