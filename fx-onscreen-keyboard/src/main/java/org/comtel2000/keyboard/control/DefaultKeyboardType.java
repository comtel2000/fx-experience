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

import java.util.Optional;

public enum DefaultKeyboardType implements IKeyboardType {

    TEXT(0, false, false, false),
    NUMERIC(1, false, false, false),
    URL(2, false, false, false),
    EMAIL(3, false, false, false),
    CTRL(4, true, false, false),
    SYMBOL_SHIFT(5, false, true, true),
    SYMBOL(7, false, false, true),
    TEXT_SHIFT(6, false, true, false);

    int keyboardTypeNumber = 0;
    boolean control = false;
    boolean shift = false;
    boolean symbol = false;

    DefaultKeyboardType(int keyboardTypeNumber, boolean control, boolean shift, boolean symbol) {
        this.keyboardTypeNumber = keyboardTypeNumber;
        this.control = control;
        this.shift = shift;
        this.symbol = symbol;
    }

    @Override
    public boolean getControl() {
        return control;
    }

    @Override
    public boolean getShift() {
        return shift;
    }

    @Override
    public boolean getSymbol() {
        return symbol;
    }

    @Override
    public int getKeyboardId() {
        return keyboardTypeNumber;
    }

    public static Optional<DefaultKeyboardType> findValue(Object value) {
        if (value == null) {
            return Optional.empty();
        }

        DefaultKeyboardType[] values = values();
        if (value instanceof Number) {
            for (DefaultKeyboardType defaultKeyboardType : values) {
                if (value.equals(defaultKeyboardType.getKeyboardId())) {
                    return Optional.of(defaultKeyboardType);
                }
            }
            return Optional.empty();
        }
        for (DefaultKeyboardType t : values) {
            if (t.toString().equalsIgnoreCase(value.toString())) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }
}
