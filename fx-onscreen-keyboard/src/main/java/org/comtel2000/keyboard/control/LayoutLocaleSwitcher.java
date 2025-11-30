/* Copyright (c) 2025 comtel2000 */
package org.comtel2000.keyboard.control;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import javafx.scene.layout.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LayoutLocaleSwitcher {

  private static final Logger logger = LoggerFactory.getLogger(LayoutLocaleSwitcher.class);

  private final Map<KeyboardType, Region> typeRegionMap = new EnumMap<>(KeyboardType.class);

  private final KeyboardPane keyboard;

  LayoutLocaleSwitcher(KeyboardPane keyboard) {
    this.keyboard = keyboard;
  }

  void setLayout(final Locale local) throws Exception {

    logger.debug("try to set keyboard local: {}->{}", keyboard.getActiveLocale(), local);

    var localeMap = keyboard.getAvailableLocales();
    if (localeMap.containsKey(local)) {
      if (local.equals(keyboard.getActiveLocale())) {
        logger.debug("locale already active: {}", local);
        return;
      }
      keyboard.setActiveLocale(local);
    } else if (localeMap.containsKey(Locale.forLanguageTag(local.getLanguage()))) {
      if (Locale.forLanguageTag(local.getLanguage()).equals(keyboard.getActiveLocale())) {
        logger.debug("locale language already active: {}", local);
        return;
      }
      keyboard.setActiveLocale(Locale.forLanguageTag(local.getLanguage()));
    } else {
      if (Locale.ENGLISH.equals(keyboard.getActiveLocale())) {
        logger.debug("locale language already active: {}", local);
        return;
      }
      keyboard.setActiveLocale(Locale.ENGLISH);
    }
    logger.debug("use keyboard local: {}", keyboard.getActiveLocale());
    String root = localeMap.get(keyboard.getActiveLocale());

    addTypeRegion(KeyboardType.TEXT, root, "kb-layout.xml");
    addTypeRegion(KeyboardType.TEXT_SHIFT, root, "kb-layout-shift.xml");
    addTypeRegion(KeyboardType.SYMBOL, root, "kb-layout-sym.xml");
    addTypeRegion(KeyboardType.SYMBOL_SHIFT, root, "kb-layout-sym-shift.xml");
    addTypeRegion(KeyboardType.CTRL, root, "kb-layout-ctrl.xml");
    addTypeRegion(KeyboardType.NUMERIC, root, "kb-layout-numeric.xml");
    addTypeRegion(KeyboardType.EMAIL, root, "kb-layout-email.xml");
    addTypeRegion(KeyboardType.URL, root, "kb-layout-url.xml");
  }

  private void addTypeRegion(KeyboardType type, String root, String file) throws Exception {
    var url = KeyboardPane.class.getResource(root + "/" + file);
    if (url == null && Files.exists(Paths.get(root, file))) {
      url = Paths.get(root, file).toUri().toURL();
    }
    if (url != null) {
      logger.debug("add layout: {}", url);
      typeRegionMap.put(type, keyboard.getKeyboardPane(url));
      return;
    }
    String defaultRoot = keyboard.getAvailableLocales().get(Locale.ENGLISH);
    if (defaultRoot == null) {
      logger.error("layout: {} / {} not found - no default available", root, file);
      return;
    }
    url = KeyboardPane.class.getResource(defaultRoot + "/" + file);
    if (url != null) {
      logger.debug("add default layout: {}", url);
      typeRegionMap.put(type, keyboard.getKeyboardPane(url));
      return;
    }
    if (Files.exists(Paths.get(defaultRoot, file))) {
      url = Paths.get(defaultRoot, file).toUri().toURL();
      logger.debug("add default layout: {}", url);
      typeRegionMap.put(type, keyboard.getKeyboardPane(url));
    }
  }

  private Region getRegion(KeyboardType type) {
    return typeRegionMap.get(type);
  }

  private Region getRegionOrDefault(KeyboardType type, KeyboardType defaultRegion) {
    return typeRegionMap.getOrDefault(type, typeRegionMap.get(defaultRegion));
  }

  void switchKeyboardTypeRegion(KeyboardType type, Consumer<Region> comsumer) {
    logger.debug("try to set type: {}->{}", keyboard.getActiveType(), type);
    if (type.equals(keyboard.getActiveType())) {
      return;
    }
    keyboard.setActiveType(type);
    Region pane;
    switch (type) {
      case NUMERIC:
        keyboard.setControl(false);
        keyboard.setShift(false);
        keyboard.setSymbol(false);
        pane = getRegionOrDefault(type, KeyboardType.SYMBOL);
        break;
      case SYMBOL:
        keyboard.setControl(false);
        keyboard.setShift(false);
        keyboard.setSymbol(true);
        pane = getRegion(type);
        break;
      case SYMBOL_SHIFT:
        keyboard.setControl(false);
        keyboard.setShift(true);
        keyboard.setSymbol(true);
        pane = getRegion(type);
        break;
      case CTRL:
        keyboard.setControl(true);
        keyboard.setShift(false);
        keyboard.setSymbol(false);
        pane = getRegion(type);
        break;
      case TEXT_SHIFT:
        keyboard.setControl(false);
        keyboard.setShift(true);
        keyboard.setSymbol(false);
        pane = getRegion(type);
        break;
      case EMAIL, URL:
      default:
        keyboard.setControl(false);
        keyboard.setShift(false);
        keyboard.setSymbol(false);
        pane = getRegion(type);
        break;
    }
    if (pane == null) {
      pane = getRegion(KeyboardType.TEXT);
    }
    comsumer.accept(pane);
  }
}
