package org.comtel2000.keyboard.control;

/**
 * Created by Guglielmo Moretti - CEIA SpA
 * Date: 16/10/2017.
 */
public interface IKeyboardType {

    int getKeyboardTypeNumber();

    boolean getControl();

    boolean getShift();

    boolean getSymbol();

}
