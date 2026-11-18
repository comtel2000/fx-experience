# Project Context

**Tech Stack:** Java 17, OpenJFX 17, Maven multi-module, JUnit Jupiter, SLF4J

FXOK (fx-experience) is a JavaFX virtual on-screen keyboard for touch screens. Key layouts are
defined in XML, so new languages and keyboard types are added as resources rather than code.

**Modules:**

- **fx-onscreen-keyboard**: core JavaFX component (`KeyboardPane`, popup, key buttons, robots, XML
  layouts). JPMS module `fx.onscreen.keyboard`.
- **fx-onscreen-keyboard-swing**: Swing integration (`KeyBoardWindow`, text component UIs, AWT
  robot handlers).
- **fx-onscreen-keyboard-samples**: demo applications for JavaFX and Swing. Not a library.

## Key Entry Points

- Keyboard pane: `fx-onscreen-keyboard/src/main/java/org/comtel2000/keyboard/control/KeyboardPane.java`
- Popup builder: `fx-onscreen-keyboard/src/main/java/org/comtel2000/keyboard/control/KeyBoardPopupBuilder.java`
- Special key codes: `fx-onscreen-keyboard/src/main/java/org/comtel2000/keyboard/control/StandardKeyCode.java`
- Layout XML and schema: `fx-onscreen-keyboard/src/main/resources/org/comtel2000/keyboard/control/layer/`
- Swing window: `fx-onscreen-keyboard-swing/src/main/java/org/comtel2000/swing/control/KeyBoardWindowBuilder.java`
- Samples: `fx-onscreen-keyboard-samples/src/main/java/org/comtel2000/samples/`

## Building and Testing

The default branch is `dev`. CI (`.github/workflows/ci.yml`) runs `mvn -B test` on JDK 17, 21 and
25 (Liberica `jdk+fx`).

| Command                                    | Purpose                                         |
|--------------------------------------------|-------------------------------------------------|
| `mvn -q -B clean compile`                  | Compile without tests                           |
| `mvn -q -B test`                           | Run all unit tests (what CI runs)               |
| `mvn -q -B clean verify`                   | Full build with tests and the Spotless check    |
| `mvn -q -B spotless:apply`                 | Fix formatting (google-java-format + header)    |
| `mvn -q -B install -DskipTests`            | Install modules locally (needed to run samples) |

Spotless enforces google-java-format and the license header
`/* Copyright (c) $YEAR comtel2000 (BSD 3-Clause) */` on every Java file. Run `spotless:apply`
instead of formatting by hand.

Before running any tests, read `.claude/docs/running-tests.md` for module targeting and the
JavaFX headless setup, and `.claude/docs/test-guidelines.md` for test quality expectations.

## Keyboard Layouts

Before adding or changing a layout XML, a locale, or a key code, read
`.claude/docs/keyboard-layouts.md`. The `add-keyboard-layout` skill walks through adding a new
language.

## Documentation

Before adding or updating Javadoc, read `.claude/docs/documentation-guidelines.md`. Keep class and
method docs caller-focused.

## Additional Resources

- Running tests: `.claude/docs/running-tests.md`
- Test quality: `.claude/docs/test-guidelines.md`
- Java conventions: `.claude/docs/java-coding-conventions.md`
- Keyboard layouts: `.claude/docs/keyboard-layouts.md`
- Documentation: `.claude/docs/documentation-guidelines.md`

## Verification

Use these steps to verify any completed work. Implementation plans should include them as success
criteria.

1. **Format, compile and test** using the `maven-command-runner` agent:
    - `mvn -q -B spotless:apply` - format code
    - `mvn -q -B clean verify` - compile, test and check formatting
2. For UI changes, say that the change was not checked visually unless a sample app was run.

Before committing, ensure all verification steps pass.

---

> **Build Rule:** Use the `maven-command-runner` agent (`.claude/agents/maven-command-runner.md`)
> for Maven commands, so long build output stays out of the main conversation.
