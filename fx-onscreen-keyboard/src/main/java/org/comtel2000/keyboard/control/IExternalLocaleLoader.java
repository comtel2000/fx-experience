package org.comtel2000.keyboard.control;

import java.util.Locale;
import java.util.Map;

/**
 * Created by Guglielmo Moretti - CEIA SpA
 * Date: 16/10/2017.
 */
public interface IExternalLocaleLoader {

    /**
     * Return additional locales that are not stored inside the default fx-onscreen-keyboard library jar
     * This can be used if you implemented additional languages that you wish to display on the keyboard
     *
     * @return a map containing  Locale <-> path to the xml descriptor of the keyboard layout of the defined locale
     */
    Map<Locale,String> loadExternalLocale();
}
