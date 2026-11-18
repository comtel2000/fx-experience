---
name: add-keyboard-layout
description: Use when adding a new language (locale) layout to the FXOK on-screen keyboard, or adding a missing keyboard type file (shift, symbol, ctrl, numeric, email, url) to an existing language.
---

# Add a keyboard layout

Read `.claude/docs/keyboard-layouts.md` first. It explains the folder structure, the file per
keyboard type, the fallback rules and the XML attributes.

## Steps

1. **Pick the language code.** Use the ISO 639 language code in lower case (`fr`, `it`, `pl`).
   The folder name becomes the `Locale` and the label on the locale switch popup.

2. **Start from the closest existing layout.** Copy the five files from the most similar language
   in `layer/default/` (English root for Latin QWERTY, `de/` for QWERTZ, `ru/` for Cyrillic):

   ```bash
   base=fx-onscreen-keyboard/src/main/resources/org/comtel2000/keyboard/control/layer
   mkdir -p "$base/default/<lang>"
   cp "$base/default/de/"kb-layout*.xml "$base/default/<lang>/"
   ```

3. **Edit the keys.** For each letter set `codes` to the Unicode code point (decimal) and
   `keyLabel` to the character. Put accented variants after the first code so they show in the
   long-press popup, for example `codes="101,233,232,234,235" keyLabel="e"`. Shift files use the
   upper-case code points. Set the locale switch key label (`codes="-7"`) to the language code in
   upper case.

4. **Keep widths aligned.** Each row should add up to the same width as the matching row in the
   other files of the language, and close to the source layout, so switching shift or symbol does
   not resize the keyboard.

5. **Repeat for the number block layer.** Create `layer/numblock/<lang>/` the same way, starting
   from the numblock files of the source language, and apply the same key changes. The number
   block columns stay as they are.

6. **Validate the XML.**

   ```bash
   xmllint --noout --schema "$base/kb-layout.xsd" "$base"/default/<lang>/*.xml "$base"/numblock/<lang>/*.xml
   ```

   If `xmllint` is not installed, parse the files with a short `XmlHelper`-based test or a Java
   `SchemaFactory` check instead.

7. **Add a test when behavior is new.** A new language alone needs no Java test. If you added a
   new special key or attribute, add a test in `fx-onscreen-keyboard/src/test/java` following
   `.claude/docs/test-guidelines.md`.

8. **Verify.** Use the `maven-command-runner` agent to run `mvn -q -B clean verify`.

9. **Document.** Mention the new language in the README features or in the PR description. If you
   can run the sample (`StandAloneApp` in `fx-onscreen-keyboard-samples`) with a display, take a
   screenshot for `doc/layout_<lang>.png`; otherwise say the layout was not checked visually.

## Common mistakes

- Using hexadecimal code points. `codes` is decimal.
- Adding the language to `default/` but not `numblock/`, so it disappears when the number block
  layer is used.
- Forgetting the shift file. The keyboard then falls back to the English shift layout.
- Saving with CRLF line endings. `.gitattributes` expects LF.
