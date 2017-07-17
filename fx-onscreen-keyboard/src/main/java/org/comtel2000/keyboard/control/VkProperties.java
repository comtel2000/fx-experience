/*******************************************************************************
 * Copyright (c) 2017 comtel2000
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

package org.comtel2000.keyboard.control;

import javafx.scene.Node;

/**
 * Settings for {@link Node#getProperties} to change the default keyboard layout ({@link #VK_TYPE})
 * and locale ({@link #VK_LOCALE}).
 * 
 * @author comtel
 *
 */
public final class VkProperties {

  private VkProperties() {
  }

  /** properties type key */
  public static final String VK_TYPE = "vkType";

  /** properties locale key */
  public static final String VK_LOCALE = "vkLocale";

  /** default text layout type value */
  public static final int VK_TYPE_TEXT = 0;

  /** numeric layout type value */
  public static final int VK_TYPE_NUMERIC = 1;

  /** custom url layout type value */
  public static final int VK_TYPE_URL = 2;

  /** custom email layout type value */
  public static final int VK_TYPE_EMAIL = 3;

  /** control layout type value */
  public static final String VK_TYPE_CTRL = "CTRL";

  /** shifted text layout type value */
  public static final String VK_TYPE_TEXT_SHIFT = "TEXT_SHIFT";

  /** symbol layout type value */
  public static final String VK_TYPE_SYMBOL = "SYMBOL";

  /** shifted symbol layout type value */
  public static final String VK_TYPE_SYMBOL_SHIFT = "SYMBOL_SHIFT";

  /** German locale value */
  public static final String VK_LOCALE_DE = "de";

  /** English locale value */
  public static final String VK_LOCALE_EN = "en";
}
