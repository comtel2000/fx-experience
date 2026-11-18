# Java Coding Conventions

## Formatting

Formatting is owned by Spotless with google-java-format (2-space indent, 100-column limit). Every
Java file starts with the license header:

```java
/* Copyright (c) 2025 comtel2000 (BSD 3-Clause) */
```

Run `mvn -q -B spotless:apply` after editing. Don't hand-format or reflow code the formatter
would change anyway.

## Variables and Types

Use `var` for local variables only when the type is obvious from the right-hand side. When in
doubt, write the explicit type.

**Obvious from the right-hand side:**

- Constructor calls with concrete types: `new KeyboardPane()`, `new ArrayList<String>()`
- Literals and `List.of(...)` / `Map.of(...)` with literals
- Standard library methods with obvious return types: `isEmpty()`, `size()`, `toString()`

```java
// Good: type is clear
var keyboard = new KeyboardPane();
var latch = new CountDownLatch(1);

// Good: explicit type when the call doesn't say it
Region pane = keyboard.getKeyboardPane(url);
Map<Locale, String> locales = keyboard.getAvailableLocales();

// Avoid
var result = load(); // what type?
```

## Imports

Import classes and use simple names. Use a fully qualified name only to resolve a clash, such as
`java.awt.event.KeyEvent` next to `javafx.scene.input.KeyEvent`. Avoid wildcard imports in main
code.

## JavaFX

- Touch the scene graph only on the JavaFX Application Thread. Use `Platform.runLater(...)` from
  other threads.
- Follow the existing lazy property pattern in `KeyboardPane`: a plain field holds the value until
  the `xxxProperty()` accessor is first called, then the property object takes over. Keep
  `getXxx()`, `setXxx(...)` and `xxxProperty()` together and final.
- Style through the CSS in `KeyboardButtonStyle.css` and style classes, not inline `setStyle`.
- Swing code in `fx-onscreen-keyboard-swing` must touch Swing components on the EDT
  (`SwingUtilities.invokeLater`) and JavaFX nodes on the FX thread. Don't block one thread on the
  other.

## Modules (JPMS)

Each module has a `module-info.java`. When you add a package that callers need, export it there.
When you add a dependency, add the matching `requires`. Resource folders read through
`Class.getResource` from another module must be in an opened or same-module package.

## Logging

Use SLF4J (`LoggerFactory.getLogger(Foo.class)`) with `{}` placeholders. Don't use
`System.out` or `printStackTrace` in main code.

## Documentation

- Document public API with Javadoc. See `documentation-guidelines.md`.
- Javadoc tag descriptions begin with a lowercase letter and end with a period.

## Other

For anything not covered here, follow established Java best practice. The code base targets
Java 17 and must also build and test on JDK 21 and 25.
