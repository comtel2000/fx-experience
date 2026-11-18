# Test Guidelines

## Every test must justify its existence

A test earns its place by verifying a **behavior that matters**: something that could break in a
way a user of the keyboard would notice, such as a wrong character typed, a layout that fails to
load, a key that keeps repeating, or a popup in the wrong place. If you can't say what would go
wrong if the test were deleted, the test probably shouldn't exist.

## Name the WHAT, comment the WHY

Name test methods as camelCase sentences that describe the behavior. Put a `//` comment above the
test to explain *why* it matters when the name alone doesn't.

```java
// Holding one repeatable key and then pressing another must not leave the
// first key repeating, or the user gets a stream of unwanted characters.
@Test
void previousTimelineStoppedWhenNewPressed() {
  ...
}
```

```java
// Locales without their own file for a keyboard type fall back to the
// English layout instead of showing an empty keyboard.
@Test
void missingLocaleLayoutFallsBackToDefault() {
  ...
}
```

Skip the comment when the name already says it, as in `readIntAttributeReturnsDefaultForMissingName`.

## What makes a good test

**Good tests challenge the code.** They verify:

- **Behavioral contracts**: shift then a letter key produces the upper-case character
- **Layout parsing**: XML attributes map to key size, codes, popup characters and styles
- **Fallbacks**: an unknown locale or a missing layout file resolves to the default layout
- **Timing**: long press opens the popup, repeatable keys stop on release
- **Error handling**: malformed XML or an invalid key code fails clearly instead of silently

## What makes a weak test

**Weak tests run code without testing a decision.** Watch for:

- **Getter/setter echo tests**: setting a property and asserting it reads back. Only worth it when
  the setter has logic.
- **Exhaustive repetition**: if two layouts go through the same parser path, one test covers it.
- **Testing JavaFX itself**: don't test that a `Button` shows its text.
- **Sleep-based timing**: use latches, timeline control, or `@Timeout`, not `Thread.sleep`.

## Test structure

- **One behavior per test.** A failing test name should tell you what broke.
- **No logic in tests.** Avoid `if`, `for`, `switch` or `try/catch` in test bodies, except the
  `Platform.runLater` capture pattern described in `running-tests.md`.
- **Arrange / Act / Assert.**
- **Use helpers for test data.** Put test layout XML in
  `fx-onscreen-keyboard/src/test/resources/xml/` and share loader helpers instead of repeating
  stream setup.

## Conventions

- JUnit Jupiter (`org.junit.jupiter.api`). Use `assertThrows(...)` for exceptions.
- Prefer `assertEquals(expected, actual)` over `assertTrue(a == b)`.
- Give toolkit tests a `@Timeout` so a stuck FX thread fails the test instead of hanging CI.
- Group related tests with `@Nested` classes named after the behavior area (`LocaleFallback`,
  `RepeatKeys`), not after the method under test.
