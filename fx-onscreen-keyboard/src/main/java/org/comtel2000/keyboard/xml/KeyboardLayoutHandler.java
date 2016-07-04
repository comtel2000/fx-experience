package org.comtel2000.keyboard.xml;

/*******************************************************************************
 * Copyright (c) 2016 comtel2000
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * 3. Neither the name of the comtel2000 nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.comtel2000.keyboard.xml.layout.Keyboard;
import org.slf4j.LoggerFactory;

public class KeyboardLayoutHandler {

  private final static org.slf4j.Logger logger = LoggerFactory.getLogger(KeyboardLayoutHandler.class);

  private final boolean cached;

  private ConcurrentHashMap<String, Keyboard> layoutStringCache;
  private ConcurrentHashMap<URL, Keyboard> layoutURLCache;
  private Unmarshaller unmarshaller;

  public KeyboardLayoutHandler() {
    this(false);
  }

  public KeyboardLayoutHandler(boolean cached) {
    this.cached = cached;
    try {
      JAXBContext context = JAXBContext.newInstance(Keyboard.class);
      unmarshaller = context.createUnmarshaller();

    } catch (JAXBException e) {
      logger.error(e.getMessage(), e);
    }

    if (cached) {
      layoutStringCache = new ConcurrentHashMap<>(8);
      layoutURLCache = new ConcurrentHashMap<>(8);
    }
  }

  /**
   * Build {@link Keyboard} layout from XML {@link String}
   *
   * @param file String (resources/xml/kb-layout.xml)
   * @return {@link Keyboard} layout instance
   * @throws IOException If this String path could not be read
   */
  public Keyboard getLayout(String file) throws IOException {

    if (cached) {
      return layoutStringCache.computeIfAbsent(file, f -> {
        try {
          URL url = KeyboardLayoutHandler.class.getResource(f);
          if (url != null) {
            return getLayout(url);
          }
          return getLayout(KeyboardLayoutHandler.class.getResourceAsStream(f));
        } catch (Exception e) {
          logger.error("file: " + String.valueOf(f) + " can not be read", e);
        }
        return null;
      });
    }

    URL url = KeyboardLayoutHandler.class.getResource(file);
    if (url != null) {
      return getLayout(url);
    }
    try (InputStream is = KeyboardLayoutHandler.class.getResourceAsStream(file)) {
      if (is != null) {
        return getLayout(is);
      }
    }
    throw new IOException("layout not found on: " + file);
  }

  /**
   * Build {@link Keyboard} layout from XML {@link URL}
   *
   * @param url (resources/xml/kb-layout.xml)
   * @return {@link Keyboard} layout instance
   * @throws IOException If this URL could not be read
   */
  public Keyboard getLayout(URL url) throws IOException {

    if (cached) {
      return layoutURLCache.computeIfAbsent(url, u -> {
        try {
          return (Keyboard) unmarshaller.unmarshal(u);
        } catch (JAXBException e) {
          logger.error("file: " + String.valueOf(u) + " can not be read", e);
        }
        return null;
      });
    }

    try {
      return (Keyboard) unmarshaller.unmarshal(url);
    } catch (JAXBException e) {
      throw new IOException("file: " + url + " can not be read", e);
    }

  }

  /**
   * Build {@link Keyboard} layout from XML {@link InputStream}
   *
   * @param is (resources/xml/kb-layout.xml)
   * @return {@link Keyboard} layout instance
   * @throws IOException If this InputStream could not be read
   */
  private Keyboard getLayout(InputStream is) throws IOException {
    try {
      return (Keyboard) unmarshaller.unmarshal(is);
    } catch (JAXBException e) {
      throw new IOException("stream can not be read", e);
    }

  }
}
