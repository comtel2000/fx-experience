/* Copyright (c) 2025 comtel2000 */
package org.comtel2000.keyboard.control;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class KeyboardLocales implements Supplier<Map<Locale, String>> {

  private static final Logger logger = LoggerFactory.getLogger(KeyboardLocales.class);

  private static final String DEFAULT_XML_PATH = "layer";

  private final Map<Locale, String> availableLocales = new LinkedHashMap<>();

  private final KeyboardPane keyboard;

  KeyboardLocales(KeyboardPane keyboard) {
    this.keyboard = keyboard;
  }

  @Override
  public Map<Locale, String> get() {
    return getAvailableLocales();
  }

  void reset() {
    availableLocales.clear();
  }

  private Map<Locale, String> getAvailableLocales() {
    if (!availableLocales.isEmpty()) {
      return availableLocales;
    }
    if (keyboard.getLayerPath() == null) {
      var l = String.valueOf(keyboard.getLayer()).toLowerCase(Locale.ENGLISH);
      String path = String.format("%s/%s", DEFAULT_XML_PATH, l);
      URL url = Objects.requireNonNull(KeyboardLocales.class.getResource(path));
      logger.debug("use embedded layer path: {}", url);
      if (url.toExternalForm().contains("!")) {
        availableLocales.put(Locale.ENGLISH, path);
        readJarLocales(url);
        return availableLocales;
      }

      try {
        keyboard.setLayerPath(Paths.get(url.toURI()));
      } catch (URISyntaxException e) {
        logger.error(e.getMessage(), e);
      }
    }
    availableLocales.put(Locale.ENGLISH, keyboard.getLayerPath().toString());
    try (var stream = Files.newDirectoryStream(keyboard.getLayerPath())) {
      for (var p : stream) {
        if (Files.isDirectory(p)) {
          var locale = new Locale(p.getFileName().toString());
          availableLocales.put(locale, p.toString());
        }
      }
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
    logger.debug("locales: {}", availableLocales.keySet());
    return availableLocales;
  }

  private void readJarLocales(URL url) {
    var array = url.toExternalForm().split("!");
    try (var fs = FileSystems.newFileSystem(URI.create(array[0]), Collections.emptyMap())) {
      final var path = fs.getPath(array[1]);
      try (var stream = Files.newDirectoryStream(path)) {
        for (var p : stream) {
          if (Files.isDirectory(p)) {
            var lang = p.getFileName().toString().replace("/", "");
            availableLocales.put(new Locale(lang), array[1] + "/" + lang);
          }
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }
}
