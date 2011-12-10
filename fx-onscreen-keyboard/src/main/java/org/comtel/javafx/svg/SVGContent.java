package org.comtel.javafx.svg;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.Group;
import javafx.scene.Node;

/**
 * SVGContent express SVG content.
 * <p>SVGContent has a root group. The root is a Group object, therefore is able to be added to scene graph:</p>
 * 
 * <pre> 
    URL url = ...;
    SVGContent content = SVGLoader.load(url);
 
    container.getChildren().add(content.getRoot());</pre>
 * 
 * <p>getNode() method returns Node object represented by ID. When loading following SVG file, Rectangle object is gotten by getNode() method.</p>
 * 
 * <p>rectangle.svg</p>
 * <pre>
 &lt;?xml version="1.0" encoding="iso-8859-1"?&gt;
 &lt;!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"&gt;
 &lt;svg version="1.1" id="layer_1"
                     xmlns="http://www.w3.org/2000/svg"
                     xmlns:xlink="http://www.w3.org/1999/xlink"
                     x="0px" y="0px" width="300px" height="200px"
                     viewBox="0 0 300 200"
                     style="enable-background:new 0 0 300 200;"
                     xml:space="preserve"&gt;
   &lt;rect id="rect" 
         x="100" y="50"
         width="100" height="80"
         style="fill:#FFFFFF; stroke:#000000;"/&gt;
&lt;/svg&gt;
 * </pre>
 * 
 * <p>Java code is follows:</p>
 * <pre>
    SVGContent content = SVGLoader.load("rectangle.svg");
    Rectangle rect = (Rectangle) content.getNode("rect");
 * </pre>
 * 
 * <p>getGroup() method returns Group object represented by ID. When loading following SVG file, Group object is gotten by getNode() method.</p>
 * <p>group.svg</p>
 * <pre>
&lt;?xml version="1.0" encoding="iso-8859-1"?&gt;
&lt;!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"&gt;
&lt;svg version="1.1" xmlns="http://www.w3.org/2000/svg"
                   xmlns:xlink="http://www.w3.org/1999/xlink"
                   x="0px" y="0px" width="200px" height="200px" viewBox="0 0 200 200"
                   style="enable-background:new 0 0 200 200;" xml:space="preserve"&gt;
  &lt;g id="group"&gt;
    &lt;circle style="fill:#FF0000;stroke:#000000;" cx="100" cy="100" r="50"/&gt;
  &lt;/g&gt;
&lt;/svg&gt;
 * </pre>
 * 
 * <p>Java code is follows:</p>
 * <pre>
    SVGContent content = SVGLoader.load("group.svg");
    Group group = content.getGroup("group");
 * </pre>
 *
 * note: There are many unsupport SVG element.
 * 
 */
public class SVGContent {
    private Group root;
    private Map<String, Node> nodes = new HashMap<String, Node>();
    private Map<String, Group> groups = new HashMap<String, Group>();
    
    void setRoot(Group root) {
        this.root = root;
    }

    void putNode(String id, Node node) {
        nodes.put(id, node);
    }
    
    /**
     * Gets the root group of SVG content.
     * 
     * @return root group
     */
    public Group getRoot() {
        return root;
    }

    /**
     * Gets node object indicated by id.
     * When there is no node indicated by id, return null.
     * 
     * @param id the name of node
     * @return node  represented by id
     */
    public Node getNode(String id) {
        return nodes.get(id);
    }
    
    void putGroup(String id, Group group) {
        groups.put(id, group);
    }

    /**
     * Gets group object indicated by id.
     * When there is no group indicated by id, return null.
     * 
     * @param id the name of group
     * @return group represented by id
     */
    public Group getGroup(String id) {
        return groups.get(id);
    }
}
