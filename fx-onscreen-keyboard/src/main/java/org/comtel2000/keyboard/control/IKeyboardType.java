package org.comtel2000.keyboard.control;

/**
 * Created by Guglielmo Moretti - CEIA SpA
 * Date: 16/10/2017.
 */
public interface IKeyboardType {

    /**
     * A number that is used as keyboard type identifier.
     * It must be unique across all keyboards layout
     *
     * @return the keyboard ID
     */
    int getKeyboardId();

    /**
     * Indicates whether or not this keyboard must be shown when the CTRL button is pressed or not
     *
     * @return true if shown with CTRL button pressed, false otherwise
     */
    boolean getControl();

    /**
     * Indicates whether or not this keyboard must be shown when the SHIFT button is pressed or not
     *
     * @return true if shown with SHIFT button pressed, false otherwise
     */
    boolean getShift();

    /**
     * Indicates whether or not this keyboard must be shown when the SYMBOL button is pressed or not
     *
     * @return true if shown with SYMBOL button pressed, false otherwise
     */
    boolean getSymbol();

}
