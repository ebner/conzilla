/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.style;

import java.awt.Stroke;
import java.util.List;

import se.kth.cid.graphics.LineDraw;


public class LineStyle {
    
    final public static int PATH_TYPE_STRAIGHT = 0;
    final public static int PATH_TYPE_QUAD = 1;
    final public static int PATH_TYPE_CURVE = 2;

    final public static int CONTINUOUS = 0;
    final public static int DOTTED = 1;
    final public static int FINELYDOTTED = 2;
    final public static int DASHED = 3;
    final public static int DASHDOT = 4;
    final public static int DASHDOTDOT = 5;
    final public static int DASHDOTDOTDOT = 6;

    //Arghhh, duplicated the strings below from class se.kth.cid.rdf.CV
    //to avoid Jena dependencies.
    public static String SNS = "http://conzilla.org/model/style#"; //from CV.SNS

    public static String lineType = SNS + "lineStyle";//CV.lineStyle
    public static String lineThickness = SNS + "lineThickness";//CV.lineThickness

    protected Stroke stroke;
    protected float thickness = 3;
    protected String typeStr = "continuous";
    protected int type;


    /**
     * @return Returns the stroke.
     */
    public Stroke getStroke() {
        if (stroke == null) {
            stroke = LineDraw.makeStroke(this, null);
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
    }

    public LineStyle() {
    }
    
    public LineStyle(float thickness, String typeStr) {
        this.thickness = thickness;
        this.typeStr = typeStr;
        type = discoverType();
    }
    
    public void fetchStyle(StyleManager sm, List stack) {
        typeStr = (String) sm.getAttributeValue(stack, lineType, typeStr);
        type = discoverType();
        thickness =
            (float) (((Double) sm
                .getAttributeValue(
                    stack,
                    lineThickness,
                    new Double(thickness)))
                .doubleValue());
        stroke = LineDraw.makeStroke(this, null);
    }
    protected int discoverType() {
        if (typeStr.equalsIgnoreCase("continuous")) {
            return LineStyle.CONTINUOUS;
        } else if (typeStr.equalsIgnoreCase("dotted")) {
            return LineStyle.DOTTED;
        } else if (typeStr.equalsIgnoreCase("finelydotted")) {
            return LineStyle.FINELYDOTTED;
        } else if (typeStr.equals("dashed")) {
            return LineStyle.DASHED;
        } else if (typeStr.equals("dashdot")) {
            return LineStyle.DASHDOT;
        } else if (typeStr.equals("dashdotdot")) {
            return LineStyle.DASHDOTDOT;
        } else if (typeStr.equals("dashdotdotdot")) {
            return LineStyle.DASHDOTDOTDOT;
        }
        return LineStyle.CONTINUOUS;
    }
}