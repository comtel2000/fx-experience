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

Tests that create JavaFX nodes need the JavaFX toolkit. CI runs on Linux without a display, so:

- Surefire sets `java.awt.headless=true` (root `pom.xml`).
- `KeyButtonTest` shows the pattern: in `@BeforeAll`, set `prism.order=sw`, use the Monocle
  headless platform when its classes are on the class path, then call `Platform.startup(...)` and
  ignore `IllegalStateException` if the toolkit is already running.
- Run JavaFX code inside `Platform.runLater(...)` and wait with a `CountDownLatch` and a
  `@Timeout`, rethrowing any captured exception on the test thread.
- `KeyButtonTest` is disabled on JDK 25 (`@DisabledOnJre(JRE.JAVA_25)`) until Monocle works there.
  Follow the same pattern for new toolkit tests rather than letting CI hang.

Prefer tests that need no toolkit at all, such as XML parsing through `XmlHelper` or pure logic,
when the behavior allows it.

## Run All Tests

```bash
mvn -q -B clean verify
```

`verify` also runs `spotless:check`. Run `mvn -q -B spotless:apply` first if formatting fails.
