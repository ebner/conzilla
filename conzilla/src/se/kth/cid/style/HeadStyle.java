/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.style;

import java.awt.Stroke;
import java.util.List;

import se.kth.cid.graphics.LineDraw;


public class HeadStyle {

    public static final int VARROW = 0;
    public static final int ARROW = 1;
    public static final int SHARPARROW = 2;
    public static final int BLUNTARROW = 3;
    public static final int DIAMOND = 4;
    public static final int ELLIPSE = 5;
    public static final int NONE = 6;
    

    //Arghhh, duplicated the strings below from class se.kth.cid.rdf.CV
    //to avoid Jena dependencies.
    public static String SNS = "http://conzilla.org/model/style#"; //from CV.SNS
    
    public static String lineHeadStyle = SNS + "lineHeadStyle";//CV.lineHeadStyle
    public static String lineHeadFilled = SNS + "lineHeadFilled";//CV.lineHeadFilled
    public static String lineHeadWidth = SNS + "lineHeadWidth";//CV.lineHeadWidth
    public static String lineHeadLength = SNS + "lineHeadLength";//CV.lineHeadLength
    public static String lineHeadLineThickness = SNS + "lineHeadLineThickness";//CV.lineHeadLineThickness
    public static String lineHeadInLineEnd = SNS + "lineHeadInLineEnd";//CV.lineHeadInLineEnd


    boolean filled = true;
    boolean clearFill = true;
    String typeStr = "varrow";
    int type;
    float thickness = 3;
    int width = 2;
    int length = 2;
    boolean headForward = true;
    Stroke stroke;
    LineStyle headLineStyle;

    /**
     * @return Returns the clearFill.
     */
    public boolean isClearFill() {
        return clearFill;
    }
    /**
     * @param clearFill The clearFill to set.
     */
    public void setClearFill(boolean clearFill) {
        this.clearFill = clearFill;
    }
    /**
     * @return Returns the filled.
     */
    public boolean isFilled() {
        return filled;
    }
    /**
     * @param filled The filled to set.
     */
    public void setFilled(boolean filled) {
        this.filled = filled;
    }
    /**
     * @return Returns the headForward.
     */
    public boolean isHeadForward() {
        return headForward;
    }
    /**
     * @param headForward The headForward to set.
     */
    public void setHeadForward(boolean headForward) {
        this.headForward = headForward;
    }
    /**
     * @return Returns the headLineStyle.
     */
    public LineStyle getHeadLineStyle() {
        return headLineStyle;
    }
    /**
     * @param headLineStyle The headLineStyle to set.
     */
    public void setHeadLineStyle(LineStyle headLineStyle) {
        this.headLineStyle = headLineStyle;
    }
    /**
     * @return Returns the length.
     */
    public int getLength() {
        return length;
    }
    /**
     * @param length The length to set.
     */
    public void setLength(int length) {
        this.length = length;
    }
    /**
     * @return Returns the stroke.
     */
    public Stroke getStroke() {
        if (stroke == null) {
            stroke = LineDraw.makeStroke(headLineStyle, null);
        }
        return stroke;
    }
    /**
     * @param stroke The stroke to set.
     */
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }
    /**
     * @return Returns the thickness.
     */
    public float getThickness() {
        return thickness;
    }
    /**
     * @param thickness The thickness to set.
     */
    public void setThickness(float thickness) {
        this.thickness = thickness;
    }
    /**
     * @return Returns the type.
     */
    public int getType() {
        return type;
    }
    /**
     * @param type The type to set.
     */
    public void setType(int type) {
        this.type = type;
    }
    /**
     * @return Returns the typeStr.
     */
    public String getTypeStr() {
        return typeStr;
    }
    /**
     * @param typeStr The typeStr to set.
     */
    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
        discoverType();
    }
    /**
     * @return Returns the width.
     */
    public int getWidth() {
        return width;
    }
    /**
     * @param width The width to set.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    public void fetchStyle(StyleManager sm, List stack) {

        filled =
            ((Boolean) sm
                .getAttributeValue(
                    stack,
                    lineHeadFilled,
                    new Boolean(filled)))
                .booleanValue();
        typeStr =
            (String) sm.getAttributeValue(stack, lineHeadStyle, typeStr);
        discoverType();
        width =
            ((Integer) sm
                .getAttributeValue(
                    stack,
                    lineHeadWidth,
                    new Integer(width)))
                .intValue();
        length =
            ((Integer) sm
                .getAttributeValue(
                    stack,
                    lineHeadLength,
                    new Integer(length)))
                .intValue();
        thickness =
            (float) (((Double) sm
                .getAttributeValue(
                    stack,
                    lineHeadLineThickness,
                    new Double(thickness)))
                .doubleValue());
        headLineStyle = new LineStyle(thickness, "continuous");
        headForward =
            (
                (String) sm.getAttributeValue(
                    stack,
                    lineHeadInLineEnd,
                    "forward")).equals(
                "forward");
        stroke = LineDraw.makeStroke(headLineStyle, null);
    }

    protected void discoverType() {
        if (typeStr.equalsIgnoreCase("varrow")) {
            clearFill = false;
            type = HeadStyle.VARROW;
        } else if (typeStr.equalsIgnoreCase("arrow")) {
            type = HeadStyle.ARROW;
        } else if (typeStr.equalsIgnoreCase("sharparrow")) {
            type = HeadStyle.SHARPARROW;
        } else if (typeStr.equalsIgnoreCase("bluntarrow")) {
            type = HeadStyle.BLUNTARROW;
        } else if (typeStr.equalsIgnoreCase("diamond")) {
            type = HeadStyle.DIAMOND;
        } else if (typeStr.equalsIgnoreCase("ellipse")) {
            type = HeadStyle.ELLIPSE;
        } else if (typeStr.equalsIgnoreCase("none")) {
            clearFill = false;
            type = HeadStyle.NONE;
        } else {
            type = HeadStyle.ARROW;
        }

    }

}