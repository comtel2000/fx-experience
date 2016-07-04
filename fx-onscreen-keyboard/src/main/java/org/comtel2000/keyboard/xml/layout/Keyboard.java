
package org.comtel2000.keyboard.xml.layout;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Row" maxOccurs="unbounded"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Key" maxOccurs="unbounded"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;attribute name="codes" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="popupCharacters" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="popupKeyboard" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="keyLabel" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="keyOutputText" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="movable" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                           &lt;attribute name="sticky" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                           &lt;attribute name="modifier" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                           &lt;attribute name="repeatable" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                           &lt;attribute name="keyIconStyle" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="keyLabelStyle" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="keyWidth" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *                           &lt;attribute name="keyEdgeFlags"&gt;
 *                             &lt;simpleType&gt;
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                                 &lt;enumeration value="left"/&gt;
 *                                 &lt;enumeration value="right"/&gt;
 *                               &lt;/restriction&gt;
 *                             &lt;/simpleType&gt;
 *                           &lt;/attribute&gt;
 *                           &lt;attribute name="horizontalGap" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *                 &lt;attribute name="rowEdgeFlags"&gt;
 *                   &lt;simpleType&gt;
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                       &lt;enumeration value="top"/&gt;
 *                       &lt;enumeration value="bottom"/&gt;
 *                     &lt;/restriction&gt;
 *                   &lt;/simpleType&gt;
 *                 &lt;/attribute&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="keyWidth" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="keyHeight" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="horizontalGap" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="verticalGap" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "row"
})
@XmlRootElement(name = "Keyboard")
public class Keyboard {

    @XmlElement(name = "Row", required = true)
    protected List<Keyboard.Row> row;
    @XmlAttribute(name = "keyWidth")
    protected Integer keyWidth;
    @XmlAttribute(name = "keyHeight")
    protected Integer keyHeight;
    @XmlAttribute(name = "horizontalGap")
    protected Integer horizontalGap;
    @XmlAttribute(name = "verticalGap")
    protected Integer verticalGap;

    /**
     * Gets the value of the row property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the row property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRow().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Keyboard.Row }
     * 
     * 
     */
    public List<Keyboard.Row> getRow() {
        if (row == null) {
            row = new ArrayList<Keyboard.Row>();
        }
        return this.row;
    }

    /**
     * Gets the value of the keyWidth property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getKeyWidth() {
        return keyWidth;
    }

    /**
     * Sets the value of the keyWidth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setKeyWidth(Integer value) {
        this.keyWidth = value;
    }

    /**
     * Gets the value of the keyHeight property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getKeyHeight() {
        return keyHeight;
    }

    /**
     * Sets the value of the keyHeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setKeyHeight(Integer value) {
        this.keyHeight = value;
    }

    /**
     * Gets the value of the horizontalGap property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHorizontalGap() {
        return horizontalGap;
    }

    /**
     * Sets the value of the horizontalGap property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHorizontalGap(Integer value) {
        this.horizontalGap = value;
    }

    /**
     * Gets the value of the verticalGap property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVerticalGap() {
        return verticalGap;
    }

    /**
     * Sets the value of the verticalGap property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVerticalGap(Integer value) {
        this.verticalGap = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="Key" maxOccurs="unbounded"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;attribute name="codes" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="popupCharacters" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="popupKeyboard" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="keyLabel" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="keyOutputText" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="movable" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *                 &lt;attribute name="sticky" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *                 &lt;attribute name="modifier" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *                 &lt;attribute name="repeatable" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *                 &lt;attribute name="keyIconStyle" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="keyLabelStyle" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="keyWidth" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
     *                 &lt;attribute name="keyEdgeFlags"&gt;
     *                   &lt;simpleType&gt;
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *                       &lt;enumeration value="left"/&gt;
     *                       &lt;enumeration value="right"/&gt;
     *                     &lt;/restriction&gt;
     *                   &lt;/simpleType&gt;
     *                 &lt;/attribute&gt;
     *                 &lt;attribute name="horizontalGap" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *       &lt;attribute name="rowEdgeFlags"&gt;
     *         &lt;simpleType&gt;
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *             &lt;enumeration value="top"/&gt;
     *             &lt;enumeration value="bottom"/&gt;
     *           &lt;/restriction&gt;
     *         &lt;/simpleType&gt;
     *       &lt;/attribute&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "key"
    })
    public static class Row {

        @XmlElement(name = "Key", required = true)
        protected List<Keyboard.Row.Key> key;
        @XmlAttribute(name = "rowEdgeFlags")
        protected String rowEdgeFlags;

        /**
         * Gets the value of the key property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the key property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getKey().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Keyboard.Row.Key }
         * 
         * 
         */
        public List<Keyboard.Row.Key> getKey() {
            if (key == null) {
                key = new ArrayList<Keyboard.Row.Key>();
            }
            return this.key;
        }

        /**
         * Gets the value of the rowEdgeFlags property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRowEdgeFlags() {
            return rowEdgeFlags;
        }

        /**
         * Sets the value of the rowEdgeFlags property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRowEdgeFlags(String value) {
            this.rowEdgeFlags = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;attribute name="codes" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="popupCharacters" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="popupKeyboard" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="keyLabel" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="keyOutputText" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="movable" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
         *       &lt;attribute name="sticky" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
         *       &lt;attribute name="modifier" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
         *       &lt;attribute name="repeatable" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
         *       &lt;attribute name="keyIconStyle" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="keyLabelStyle" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="keyWidth" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
         *       &lt;attribute name="keyEdgeFlags"&gt;
         *         &lt;simpleType&gt;
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
         *             &lt;enumeration value="left"/&gt;
         *             &lt;enumeration value="right"/&gt;
         *           &lt;/restriction&gt;
         *         &lt;/simpleType&gt;
         *       &lt;/attribute&gt;
         *       &lt;attribute name="horizontalGap" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Key {

            @XmlAttribute(name = "codes", required = true)
            protected String codes;
            @XmlAttribute(name = "popupCharacters")
            protected String popupCharacters;
            @XmlAttribute(name = "popupKeyboard")
            protected String popupKeyboard;
            @XmlAttribute(name = "keyLabel")
            protected String keyLabel;
            @XmlAttribute(name = "keyOutputText")
            protected String keyOutputText;
            @XmlAttribute(name = "movable")
            protected Boolean movable;
            @XmlAttribute(name = "sticky")
            protected Boolean sticky;
            @XmlAttribute(name = "modifier")
            protected Boolean modifier;
            @XmlAttribute(name = "repeatable")
            protected Boolean repeatable;
            @XmlAttribute(name = "keyIconStyle")
            protected String keyIconStyle;
            @XmlAttribute(name = "keyLabelStyle")
            protected String keyLabelStyle;
            @XmlAttribute(name = "keyWidth")
            protected Integer keyWidth;
            @XmlAttribute(name = "keyEdgeFlags")
            protected String keyEdgeFlags;
            @XmlAttribute(name = "horizontalGap")
            protected Integer horizontalGap;

            /**
             * Gets the value of the codes property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCodes() {
                return codes;
            }

            /**
             * Sets the value of the codes property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCodes(String value) {
                this.codes = value;
            }

            /**
             * Gets the value of the popupCharacters property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPopupCharacters() {
                return popupCharacters;
            }

            /**
             * Sets the value of the popupCharacters property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPopupCharacters(String value) {
                this.popupCharacters = value;
            }

            /**
             * Gets the value of the popupKeyboard property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPopupKeyboard() {
                return popupKeyboard;
            }

            /**
             * Sets the value of the popupKeyboard property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPopupKeyboard(String value) {
                this.popupKeyboard = value;
            }

            /**
             * Gets the value of the keyLabel property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKeyLabel() {
                return keyLabel;
            }

            /**
             * Sets the value of the keyLabel property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKeyLabel(String value) {
                this.keyLabel = value;
            }

            /**
             * Gets the value of the keyOutputText property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKeyOutputText() {
                return keyOutputText;
            }

            /**
             * Sets the value of the keyOutputText property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKeyOutputText(String value) {
                this.keyOutputText = value;
            }

            /**
             * Gets the value of the movable property.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public Boolean isMovable() {
                return movable;
            }

            /**
             * Sets the value of the movable property.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setMovable(Boolean value) {
                this.movable = value;
            }

            /**
             * Gets the value of the sticky property.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public Boolean isSticky() {
                return sticky;
            }

            /**
             * Sets the value of the sticky property.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setSticky(Boolean value) {
                this.sticky = value;
            }

            /**
             * Gets the value of the modifier property.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public Boolean isModifier() {
                return modifier;
            }

            /**
             * Sets the value of the modifier property.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setModifier(Boolean value) {
                this.modifier = value;
            }

            /**
             * Gets the value of the repeatable property.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public Boolean isRepeatable() {
                return repeatable;
            }

            /**
             * Sets the value of the repeatable property.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setRepeatable(Boolean value) {
                this.repeatable = value;
            }

            /**
             * Gets the value of the keyIconStyle property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKeyIconStyle() {
                return keyIconStyle;
            }

            /**
             * Sets the value of the keyIconStyle property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKeyIconStyle(String value) {
                this.keyIconStyle = value;
            }

            /**
             * Gets the value of the keyLabelStyle property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKeyLabelStyle() {
                return keyLabelStyle;
            }

            /**
             * Sets the value of the keyLabelStyle property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKeyLabelStyle(String value) {
                this.keyLabelStyle = value;
            }

            /**
             * Gets the value of the keyWidth property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getKeyWidth() {
                return keyWidth;
            }

            /**
             * Sets the value of the keyWidth property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setKeyWidth(Integer value) {
                this.keyWidth = value;
            }

            /**
             * Gets the value of the keyEdgeFlags property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKeyEdgeFlags() {
                return keyEdgeFlags;
            }

            /**
             * Sets the value of the keyEdgeFlags property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKeyEdgeFlags(String value) {
                this.keyEdgeFlags = value;
            }

            /**
             * Gets the value of the horizontalGap property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getHorizontalGap() {
                return horizontalGap;
            }

            /**
             * Sets the value of the horizontalGap property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setHorizontalGap(Integer value) {
                this.horizontalGap = value;
            }

        }

    }

}
