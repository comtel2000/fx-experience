/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;

/**
 * Starts the JavaFX toolkit once for all tests and runs code on the FX application thread.
 *
 * <p>If the toolkit cannot be started (for example no display is available), the calling test is
 * skipped instead of hanging until it times out.
 */
public final class FxTestSupport {

  private static final long TIMEOUT_SECONDS = 10;

  private static volatile Boolean toolkitAvailable;

  private FxTestSupport() {}

  /** Functional interface for test code that may throw. */
  @FunctionalInterface
  public interface FxAction<T> {
    T call() throws Exception;
  }

  /** Functional interface for test code without a result that may throw. */
  @FunctionalInterface
  public interface FxRunnable {
    void run() throws Exception;
  }

  /** Start the JavaFX toolkit or skip the calling test if this is not possible. */
  public static synchronized void startToolkit() {
    if (toolkitAvailable == null) {
      toolkitAvailable = tryStartToolkit();
      if (toolkitAvailable) {
        // closing the last test stage must not shut down the toolkit for the following tests
        Platform.setImplicitExit(false);
      }
    }
    Assumptions.assumeTrue(toolkitAvailable, "JavaFX toolkit not available (no display?)");
  }

  private static boolean tryStartToolkit() {
    var started = new CountDownLatch(1);
    try {
      Platform.startup(started::countDown);
    } catch (IllegalStateException e) {
      // toolkit already running
      return true;
    } catch (RuntimeException | Error e) {
      System.err.println("JavaFX toolkit could not be started: " + e);
      return false;
    }
    try {
      return started.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
  }

  /** Run {@code action} on the FX application thread and return its result. */
  public static <T> T callFx(FxAction<T> action) {
    startToolkit();
    if (Platform.isFxApplicationThread()) {
      try {
        return action.call();
      } catch (Exception e) {
        return Assertions.fail(e);
      }
    }
    var latch = new CountDownLatch(1);
    var result = new AtomicReference<T>();
    var error = new AtomicReference<Throwable>();
    Platform.runLater(
        () -> {
          try {
            result.set(action.call());
          } catch (Throwable t) {
            error.set(t);
          } finally {
            latch.countDown();
          }
        });
    try {
      Assertions.assertTrue(
          latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), "FX action did not finish in time");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      Assertions.fail(e);
    }
    var t = error.get();
    if (t instanceof Error err) {
      throw err;
    }
    if (t instanceof RuntimeException re) {
      throw re;
    }
    if (t != null) {
      Assertions.fail(t);
    }
    return result.get();
  }

  /** Run {@code action} on the FX application thread and wait until it is done. */
  public static void runFx(FxRunnable action) {
    callFx(
        () -> {
          action.run();
          return null;
        });
  }

  /** Wait until all events queued on the FX application thread so far have been processed. */
  public static void waitForFxEvents() {
    runFx(() -> {});
  }

  /** Poll {@code condition} on the FX thread until it is true or the timeout is reached. */
  public static boolean waitFor(FxAction<Boolean> condition, long timeoutMillis) {
    long end = System.currentTimeMillis() + timeoutMillis;
    while (System.currentTimeMillis() < end) {
      if (Boolean.TRUE.equals(callFx(condition))) {
        return true;
      }
      try {
        Thread.sleep(20);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return false;
      }
    }
    return Boolean.TRUE.equals(callFx(condition));
  }
}
