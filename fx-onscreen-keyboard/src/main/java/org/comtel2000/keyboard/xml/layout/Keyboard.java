//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.04.10 um 06:26:43 PM CEST 
//


package org.comtel2000.keyboard.xml.layout;

/*
 * #%L
 * fx-onscreen-keyboard
 * %%
 * Copyright (C) 2014 - 2015 comtel2000
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the comtel2000 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Row" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Key" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="codes" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="keyLabel" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="keyIconStyle" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="keyLabelStyle" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="keyWidth" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="keyEdgeFlags">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="left"/>
 *                                 &lt;enumeration value="right"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="horizontalGap" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="rowEdgeFlags">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="top"/>
 *                       &lt;enumeration value="bottom"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="keyWidth" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="keyHeight" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="horizontalGap" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="verticalGap" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
     * Ruft den Wert der keyWidth-Eigenschaft ab.
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
     * Legt den Wert der keyWidth-Eigenschaft fest.
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
     * Ruft den Wert der keyHeight-Eigenschaft ab.
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
     * Legt den Wert der keyHeight-Eigenschaft fest.
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
     * Ruft den Wert der horizontalGap-Eigenschaft ab.
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
     * Legt den Wert der horizontalGap-Eigenschaft fest.
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
     * Ruft den Wert der verticalGap-Eigenschaft ab.
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
     * Legt den Wert der verticalGap-Eigenschaft fest.
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
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Key" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="codes" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="keyLabel" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="keyIconStyle" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="keyLabelStyle" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="keyWidth" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                 &lt;attribute name="keyEdgeFlags">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;enumeration value="left"/>
     *                       &lt;enumeration value="right"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="horizontalGap" type="{http://www.w3.org/2001/XMLSchema}int" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="rowEdgeFlags">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="top"/>
     *             &lt;enumeration value="bottom"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
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
         * Ruft den Wert der rowEdgeFlags-Eigenschaft ab.
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
         * Legt den Wert der rowEdgeFlags-Eigenschaft fest.
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
         * <p>Java-Klasse für anonymous complex type.
         * 
         * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="codes" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="keyLabel" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="keyIconStyle" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="keyLabelStyle" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="keyWidth" type="{http://www.w3.org/2001/XMLSchema}int" />
         *       &lt;attribute name="keyEdgeFlags">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;enumeration value="left"/>
         *             &lt;enumeration value="right"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="horizontalGap" type="{http://www.w3.org/2001/XMLSchema}int" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Key {

            @XmlAttribute(name = "codes", required = true)
            protected String codes;
            @XmlAttribute(name = "keyLabel")
            protected String keyLabel;
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
             * Ruft den Wert der codes-Eigenschaft ab.
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
             * Legt den Wert der codes-Eigenschaft fest.
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
             * Ruft den Wert der keyLabel-Eigenschaft ab.
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
             * Legt den Wert der keyLabel-Eigenschaft fest.
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
             * Ruft den Wert der keyIconStyle-Eigenschaft ab.
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
             * Legt den Wert der keyIconStyle-Eigenschaft fest.
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
             * Ruft den Wert der keyLabelStyle-Eigenschaft ab.
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
             * Legt den Wert der keyLabelStyle-Eigenschaft fest.
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
             * Ruft den Wert der keyWidth-Eigenschaft ab.
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
             * Legt den Wert der keyWidth-Eigenschaft fest.
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
             * Ruft den Wert der keyEdgeFlags-Eigenschaft ab.
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
             * Legt den Wert der keyEdgeFlags-Eigenschaft fest.
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
             * Ruft den Wert der horizontalGap-Eigenschaft ab.
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
             * Legt den Wert der horizontalGap-Eigenschaft fest.
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
