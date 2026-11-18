# Keyboard Layouts

Layouts are XML files validated by
`fx-onscreen-keyboard/src/main/resources/org/comtel2000/keyboard/control/layer/kb-layout.xsd`.

## Where layouts live

```
layer/
  default/          DefaultLayer.DEFAULT (English is the folder root)
    kb-layout*.xml
    de/ es/ ru/     one folder per language, named by ISO 639 language code
  numblock/         DefaultLayer.NUMBLOCK (same layout set plus a number block)
    kb-layout*.xml
    de/ es/ ru/
```

`KeyboardLocales` lists the language folders under the active layer and maps each to a `Locale`.
The layer root itself is `Locale.ENGLISH`. Callers can point `KeyboardPane.setLayerPath(...)` at a
folder on disk with the same structure to use their own layouts.

## One file per keyboard type

`LayoutLocaleSwitcher` loads these files from the locale folder:

| File                        | `KeyboardType` |
|-----------------------------|----------------|
| `kb-layout.xml`             | `TEXT`         |
| `kb-layout-shift.xml`       | `TEXT_SHIFT`   |
| `kb-layout-sym.xml`         | `SYMBOL`       |
| `kb-layout-sym-shift.xml`   | `SYMBOL_SHIFT` |
| `kb-layout-ctrl.xml`        | `CTRL`         |
| `kb-layout-numeric.xml`     | `NUMERIC`      |
| `kb-layout-email.xml`       | `EMAIL`        |
| `kb-layout-url.xml`         | `URL`          |

A file missing from a locale folder falls back to the English file in the layer root. A type with
no file at all falls back to `TEXT` (or `SYMBOL` for `NUMERIC`). So a language folder only needs
the files that differ from English, but in practice each language ships the five text, shift,
symbol, symbol-shift and ctrl files.

## XML structure

```xml
<Keyboard keyWidth="40" keyHeight="30" horizontalGap="5" verticalGap="5">
  <Row>
    <Key codes="113" keyLabel="q" />
    <Key codes="97,228,229" keyLabel="a" />
    <Key codes="-5" keyLabel="!" keyLabelStyle=".font-fxkeyboard" repeatable="true" />
    <Key codes="" keyWidth="10" />
  </Row>
</Keyboard>
```

- `codes`: comma-separated code points. The first is the key; the rest are offered in the long-press
  popup (`MultiKeyButton`). An empty `codes` is a spacer.
- Negative codes are the special keys in `StandardKeyCode` (for example `-1` shift, `-2` symbol,
  `-5` backspace, `-7` locale switch, `-9` enter). Add new special keys there, not as magic numbers.
- `keyLabel` is the text shown. `keyOutputText` overrides the text that is typed.
- `keyLabelStyle` / `keyIconStyle` refer to classes in `KeyboardButtonStyle.css`;
  `.font-fxkeyboard` uses the glyphs in `FontKeyboardFX.ttf`.
- `keyWidth` on a key overrides the keyboard default. `repeatable`, `sticky` and `movable` are
  booleans.
- The locale switch key (`-7`) lists every available locale in its popup automatically.

## Checklist when changing layouts

- Keep the same file set in `default/<lang>` and `numblock/<lang>` so both layers support the
  language.
- Keep row widths consistent between a layout and its shift / symbol variants so the keyboard does
  not jump in size when the user switches.
- Save files as UTF-8 with LF line endings.
- Validate each file against `kb-layout.xsd`, for example
  `xmllint --noout --schema .../layer/kb-layout.xsd .../layer/default/<lang>/*.xml`.
- If a screenshot in `doc/` or the README feature list changes, update it.
