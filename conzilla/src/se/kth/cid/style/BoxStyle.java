/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.style;

import java.awt.Stroke;

import javax.swing.Icon;

import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.graphics.LineDraw;

public class BoxStyle {
    private boolean isIconCheckedFor = false;

	/**
     * @return Returns the borderLineStyle.
     */
    public LineStyle getBorderLineStyle() {
        return borderLineStyle;
    }
    /**
     * @param borderLineStyle The borderLineStyle to set.
     */
    public void setBorderLineStyle(LineStyle borderLineStyle) {
        this.borderLineStyle = borderLineStyle;
    }
    /**
     * @return Returns the borderType.
     */
    public String getBorderType() {
        return borderType;
    }
    /**
     * @param borderType The borderType to set.
     */
    public void setBorderType(String borderType) {
        this.borderType = borderType;
    }
    /**
     * @return Returns the filled.
     */
    public boolean isFilled() {
        return filled;
    }
    
    public boolean isBackgroundOpaque() {
    	return backgroundOpaque;
    }
    
    public void setBackgroundOpaque(boolean opaque) {
    	this.backgroundOpaque  = opaque;
    }
    
    /**
     * @param filled The filled to set.
     */
    public void setFilled(boolean filled) {
        this.filled = filled;
    }
    /**
     * @return Returns the stroke.
     */
    public Stroke getStroke() {
        if (stroke == null) {
            stroke = LineDraw.makeStroke(borderLineStyle, null);
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
        type = discoverType();
    }
    
    public String getIconPath() {
    	return iconPath;
    }
    
    public Icon getIcon() {
    	if (!isIconCheckedFor) {
    		isIconCheckedFor = true;
    		if (iconPath != null) {
    			icon = Images.getImageIcon(iconPath);
    		}
    	}
    	return icon;
    }
    
    protected Stroke stroke;
    protected boolean filled = false;
    protected boolean backgroundOpaque = true;    
    protected float thickness = 3;
    protected String typeStr = "ellipse";
    protected int type = BoxStyle.ELLIPSE;
    protected String borderType = "continuous";
    protected LineStyle borderLineStyle;
	protected String iconPath;
	protected Icon icon;
	
    final public static int INVISIBLE = 0;
    final public static int RECTANGLE = 1;
    final public static int ROUND_RECTANGLE = 2;
    final public static int DIAMOND = 3;
    final public static int ELLIPSE = 4;
    final public static int FLAT_HEXAGON = 5;
    final public static int UPPER_FIVE = 6;
    final public static int LOWER_FIVE = 7;
    final public static int BAR = 8;
    final public static int AND_BAR = 9;
    final public static int OR_BAR = 10;
    final public static int FIXED_CIRCLE_10 = 11;
    final public static int FIXED_CIRCLE = 12;
    final public static int FIXED_CIRCLE_IN_CIRCLE_INNER_INTERIOR = 13;
    final public static int SQUARE_BRACKETS = 14;
    final public static int EAST_ARROW = 15;
    final public static int WEST_ARROW = 16;
    final public static int EAST_PARALLELOGRAM = 17;
    final public static int SOUT_WEST_CORNER = 18;
    final public static int MAN = 19;
    final public static int HOLLOW_RECTANGLE = 20;
    final public static int CORNER_FOLDED_PAPER = 21;
    final public static int UNDERLINED = 22;
    final public static int RIGHT_HOOKS = 23;    
    final public static int NUMBER_OF_BOXTYPES = 24;
    public static String [] boxTypeNames;

    static {
        BoxStyle.boxTypeNames = new String[BoxStyle.NUMBER_OF_BOXTYPES];
        BoxStyle.boxTypeNames[BoxStyle.INVISIBLE] = "Invisible";
        BoxStyle.boxTypeNames[BoxStyle.RECTANGLE] = "Rectangle";
        BoxStyle.boxTypeNames[BoxStyle.ROUND_RECTANGLE] = "RoundRectangle";
        BoxStyle.boxTypeNames[BoxStyle.DIAMOND] = "Diamond";
        BoxStyle.boxTypeNames[BoxStyle.ELLIPSE] = "Ellipse";
        BoxStyle.boxTypeNames[BoxStyle.FLAT_HEXAGON] = "FlatHexagon";
        BoxStyle.boxTypeNames[BoxStyle.UPPER_FIVE] = "UpperFive";
        BoxStyle.boxTypeNames[BoxStyle.LOWER_FIVE] = "LowerFive";
        BoxStyle.boxTypeNames[BoxStyle.BAR] = "Bar";
        BoxStyle.boxTypeNames[BoxStyle.AND_BAR] = "AndBar";
        BoxStyle.boxTypeNames[BoxStyle.OR_BAR] = "OrBar";
        BoxStyle.boxTypeNames[BoxStyle.FIXED_CIRCLE] = "FixedCircle";
        BoxStyle.boxTypeNames[BoxStyle.FIXED_CIRCLE_10] = "FixedCircle10";
        BoxStyle.boxTypeNames[BoxStyle.FIXED_CIRCLE_IN_CIRCLE_INNER_INTERIOR] = "FixedCircleInCircle_InnerInterior";
        BoxStyle.boxTypeNames[BoxStyle.SQUARE_BRACKETS] = "SquareBrackets";
        BoxStyle.boxTypeNames[BoxStyle.EAST_ARROW] = "EastArrow";
        BoxStyle.boxTypeNames[BoxStyle.WEST_ARROW] = "WestArrow";
        BoxStyle.boxTypeNames[BoxStyle.EAST_PARALLELOGRAM] = "EastParallelogram";
        BoxStyle.boxTypeNames[BoxStyle.SOUT_WEST_CORNER] = "SouthWestCorner";
        BoxStyle.boxTypeNames[BoxStyle.MAN] = "Man";
        BoxStyle.boxTypeNames[BoxStyle.HOLLOW_RECTANGLE] = "HollowRectangle";
        BoxStyle.boxTypeNames[BoxStyle.CORNER_FOLDED_PAPER] = "CornerFoldedPaper";
        BoxStyle.boxTypeNames[BoxStyle.UNDERLINED] = "Underlined";
        BoxStyle.boxTypeNames[BoxStyle.RIGHT_HOOKS] = "RightHooks";
    }
    
    //Arghhh, duplicated the strings below from class se.kth.cid.rdf.CV
    //to avoid Jena dependencies.
    public static String SNS = "http://conzilla.org/model/style#"; //from CV.SNS
    public static String boxType = SNS+"boxStyle";  //from CV.boxStyle;
    public static String boxFilled = SNS+"boxFilled"; //from CV.boxFilled;
    public static String boxBackgroundOpaque = SNS+"boxBackgroundOpaque"; //from CV.boxFilled;
    public static String boxBorderType = SNS+"boxBorderStyle"; //from CV.boxBorderStyle;
    public static String boxBorderThickness = SNS+"boxBorderThickness"; //from CV.boxBorderThickness;
    public static String iconProperty = SNS+"icon";
    
    public void fetchStyle(StyleManager sm, java.util.List stack, boolean isProperty) {
        //fetch from stylestack...
        if (isProperty) {
            typeStr = "invisible";
        }
        typeStr = (String) sm.getAttributeValue(stack, boxType, typeStr);
        type = discoverType();
        backgroundOpaque = ((Boolean) sm
                .getAttributeValue(stack, boxBackgroundOpaque, new Boolean(backgroundOpaque)))
                .booleanValue();

        filled =
            ((Boolean) sm
                .getAttributeValue(stack, boxFilled, new Boolean(filled)))
                .booleanValue();
        borderType =
            (String) sm.getAttributeValue(stack, boxBorderType, borderType);
        thickness =
            (float) (((Double) sm
                .getAttributeValue(
                    stack,
                    boxBorderThickness,
                    new Double(thickness)))
                .doubleValue());
        borderLineStyle = new LineStyle(thickness, borderType);
        stroke = LineDraw.makeStroke(borderLineStyle, null);
        iconPath = (String) sm.getAttributeValue(stack, iconProperty, null);

    }

    protected int discoverType() {
        for (int i = 0; i < BoxStyle.NUMBER_OF_BOXTYPES; i++) {
            if (typeStr.equalsIgnoreCase(BoxStyle.boxTypeNames[i])) {
                return i;
            }
        }
        //FIXME Backward compatability, remove when checked.
        if (typeStr.equalsIgnoreCase("none")) {
            return BoxStyle.INVISIBLE;
        }
        return BoxStyle.RECTANGLE;
    }
}