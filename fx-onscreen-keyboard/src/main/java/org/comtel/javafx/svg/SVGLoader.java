package org.comtel.javafx.svg;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SVGLoader is a class for loading SVG file.
 * 
 * <pre> URL url = ...;
 * SVGContent content SVGLoader.load(url);
 * 
 * container.getChildren.add(content);</pre>
 */
public class SVGLoader {
    private SVGLoader() {}
    
    /**
     * Load SVG file and convert it to JavaFX.
     * 
     * @param url The location of SVG file
     * @return a SVGContent object that indicates SVG content
     */
    public static SVGContent load(String url) {
        URL tempUrl = null;
        try {
            tempUrl = new URL(url);
        } catch (MalformedURLException ex) {
            tempUrl = SVGLoader.class.getResource(url);
            if (tempUrl == null) {
                try {
                    tempUrl = new File(url).toURI().toURL();
                    
                    if (tempUrl == null) {
                        Logger.getLogger(SVGLoader.class.getName()).log(Level.SEVERE, "Illegal URL: " + url);
                        return null;
                    }
                } catch (Exception ex1) {
                    Logger.getLogger(SVGLoader.class.getName()).log(Level.SEVERE, null, ex1);
                    return null;
                }
            }
        }

        return loadInternal(tempUrl);
    }
    
    /**
     * Load SVG file and convert it to JavaFX.
     * 
     * @param url The location of SVG file
     * @return a SVGContent object that indicates SVG content
     */
    public static SVGContent load(URL url) {
        return loadInternal(url);
    }
    
    private static SVGContent loadInternal(URL url) {
        SVGContent root = null;

        SVGContentBuilder builder = new SVGContentBuilder(url);
        try {
            root = builder.build();
        } catch (Exception ex) {
            Logger.getLogger(SVGLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return root;
    }
}
