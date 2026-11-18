# Running Tests

This is a multi-module project. Target the module with `-pl` so a `-Dtest` filter is not applied
to every module.

All tests currently live in `fx-onscreen-keyboard/src/test/java`. Test layout XML files live in
`fx-onscreen-keyboard/src/test/resources/xml/`.

## Running Specific Tests

**Run a specific test class:**

```bash
mvn -q -B -pl fx-onscreen-keyboard test -Dtest=XmlHelperTest
```

**Run a specific test method:**

```bash
mvn -q -B -pl fx-onscreen-keyboard test -Dtest=XmlHelperTest#readAttribute
```

**Run tests matching a pattern:**

```bash
mvn -q -B -pl fx-onscreen-keyboard test -Dtest='Key*Test'
```

## Module Targeting Flags

- **`-pl <module>`**: build/test only the given module(s)
- **`-am`** (also-make): also build the modules the `-pl` target depends on
- **`-amd`** (also-make-dependents): also build the modules that depend on the `-pl` target

### When to use each

**`-pl` alone**: the change and the tests are in the same module.

```bash
mvn -q -B -pl fx-onscreen-keyboard test -Dtest=ClassName
```

**`-pl ... -am`**: the change is in `fx-onscreen-keyboard` and the tests are in
`fx-onscreen-keyboard-swing`. This rebuilds the core module so tests run against the latest code:

```bash
mvn -q -B -pl fx-onscreen-keyboard-swing -am test -Dtest=ClassName -Dsurefire.failIfNoSpecifiedTests=false
```

**`-pl ... -amd`**: the change is in the core module and you want everything that depends on it
to compile and test:

```bash
mvn -q -B -pl fx-onscreen-keyboard -amd test
```

### Rule of thumb

If the code you changed and the tests you run are in **different modules**, add `-am`. Without it
the tests run against a stale core jar from your local repository.

## JavaFX Tests and Headless Mode

Tests that create JavaFX nodes, stages or timelines need the JavaFX toolkit, and the toolkit needs
a display.

- Use `FxTestSupport` (`fx-onscreen-keyboard/src/test/java/org/comtel2000/keyboard/control/`).
  Call `FxTestSupport.startToolkit()` in `@BeforeAll`; it starts the toolkit once, disables
  implicit exit (so closing a test stage does not stop the toolkit), and **skips** the test class
  when no display is available instead of hanging.
- Run FX code with `FxTestSupport.runFx(...)` or `callFx(...)`. They run on the FX thread, wait at
  most 10 seconds and rethrow assertion failures on the test thread. `waitForFxEvents()` flushes
  work queued with `Platform.runLater`; `waitFor(condition, millis)` polls for async results such
  as fade animations.
- On Linux without a display, run the tests under Xvfb, as CI does:

```bash
xvfb-run -a mvn -q -B test
```

  Without `xvfb-run` the FX test classes are reported as skipped and only toolkit-free tests run.
- `RecordingRobot` (`src/test/java/org/comtel2000/keyboard/robot/`) records keys sent by a
  `KeyboardPane`; add it with `addRobotHandler` to assert typed characters without a real window.

Prefer tests that need no toolkit at all, such as XML parsing through `XmlHelper` or pure logic,
when the behavior allows it.

## Run All Tests

```bash
mvn -q -B clean verify
```

`verify` also runs `spotless:check`. Run `mvn -q -B spotless:apply` first if formatting fails.

## Coverage

JaCoCo runs with every `test` build. The report for the core module is written to
`fx-onscreen-keyboard/target/site/jacoco/index.html` (CSV in `jacoco.csv`). CI uploads it as the
`coverage-report` artifact from the JDK 17 job.
