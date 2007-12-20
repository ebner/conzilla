/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;

import se.kth.cid.style.BoxStyle;
import se.kth.cid.style.OverlayStyle;

/**
 * Help functionality for drawing boxes in Swing.
 * 
 * @author matthias
 */
public class BoxDraw {

    private static int iconDistance = 2;

	public static void doPaint(
        Graphics2D g,
        Shape shape,
        Rectangle innerBox,
        Rectangle outerBox,
        BoxStyle style,
        OverlayStyle ostyle) {
        Icon icon = style.getIcon();
		Stroke s = g.getStroke();

		if (shape == null || style.getType() == BoxStyle.INVISIBLE) {
	        if (icon != null) {
	        	icon.paintIcon(null, g, outerBox.x, outerBox.y);
	        }
		} else {
			if (ostyle != null && ostyle.isLineWidthModified()) {
				g.setStroke(LineDraw.makeStroke(style.getBorderLineStyle(), ostyle));
			} else
				g.setStroke(style.getStroke());
			if (style.isBackgroundOpaque()) {
				if (style.isFilled()) {
					g.setColor(ostyle.getForegroundColor());
					g.fill(shape);
				} else {
					g.setColor(ostyle.getBackgroundColor());
					g.fill(shape);
				}
			}
			if (ostyle != null) {
				g.setColor(ostyle.getForegroundColor());
			} else {
				g.setColor(Color.BLACK);
			}
			g.draw(shape);
			if (icon != null) {
				icon.paintIcon(null, g, outerBox.x, outerBox.y);
			}
		}
		
        if (ostyle.isMarked()) {
            g.fill(new Ellipse2D.Double(innerBox.x+innerBox.width-8,innerBox.y+2,6,6));
        }
        g.setStroke(s);
    }

    public static Shape appendIcon(BoxStyle style, Shape shape, Rectangle outerBox) {
        Icon icon = style.getIcon();
		
    	if (icon == null) {
    		return shape;
    	}
    	GeneralPath gp = new GeneralPath(shape);
    	gp.append(new Rectangle(outerBox.x, outerBox.y, icon.getIconWidth(), icon.getIconHeight()), false);
    	return gp;
    }
	
	
    public static Shape constructBox(BoxStyle style, Rectangle outerBox) {
        float indent;
        GeneralPath path;

        Icon icon = style.getIcon();
		
    	if (icon != null) {
    		outerBox = new Rectangle((int) outerBox.getX()+icon.getIconWidth()+iconDistance, 
    				(int) outerBox.getY(), 
    				(int) outerBox.getWidth()-icon.getIconWidth()-iconDistance, 
    				(int) outerBox.getHeight());
    	}
        
        switch (style.getType()) {
            case BoxStyle.ROUND_RECTANGLE :
    
                //Bosse says: "German design with proportional corner roundness"
                double corner = 12;
//                double inset = corner * (2 - Math.sqrt(2)) / 4;
    
                return new RoundRectangle2D.Double(
                    outerBox.x,
                    outerBox.y,
                    outerBox.width,
                    outerBox.height,
                    corner,
                    corner);                
            case BoxStyle.DIAMOND :
                int halfx = (int) Math.round(outerBox.x + outerBox.width / 2.0);
                int halfy =
                    (int) Math.round(outerBox.y + outerBox.height / 2.0);
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
                path.moveTo(outerBox.x, halfy);
                path.lineTo(halfx, outerBox.y);
                path.lineTo(outerBox.x + outerBox.width, halfy);
                path.lineTo(halfx, outerBox.y + outerBox.height);
                path.closePath();
                return path;
            case BoxStyle.ELLIPSE :
                return new Ellipse2D.Double(
                    outerBox.x,
                    outerBox.y,
                    outerBox.width,
                    outerBox.height);
            case BoxStyle.FLAT_HEXAGON :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
                path.moveTo(outerBox.x + 0.15f * outerBox.width, outerBox.y);
                path.lineTo(outerBox.x + 0.85f * outerBox.width, outerBox.y);
                path.lineTo(
                    outerBox.x + outerBox.width,
                    outerBox.y + outerBox.height / 2);
                path.lineTo(
                    outerBox.x + 0.85f * outerBox.width,
                    outerBox.y + outerBox.height);
                path.lineTo(
                    outerBox.x + 0.15f * outerBox.width,
                    outerBox.y + outerBox.height);
                path.lineTo(outerBox.x, outerBox.y + outerBox.height / 2);
                path.closePath();
    
                return path;
            case BoxStyle.UPPER_FIVE :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
                path.moveTo(outerBox.x + 0.15f * outerBox.width, outerBox.y);
                path.lineTo(outerBox.x + 0.85f * outerBox.width, outerBox.y);
                path.lineTo(
                    outerBox.x + outerBox.width,
                    outerBox.y + outerBox.height);
                path.lineTo(outerBox.x, outerBox.y + outerBox.height);
                path.closePath();
    
                return path;
            case BoxStyle.LOWER_FIVE :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
                path.moveTo(outerBox.x, outerBox.y);
                path.lineTo(outerBox.x + outerBox.width, outerBox.y);
                path.lineTo(
                    outerBox.x + 0.85f * outerBox.width,
                    outerBox.y + outerBox.height);
                path.lineTo(
                    outerBox.x + 0.15f * outerBox.width,
                    outerBox.y + outerBox.height);
                path.closePath();
    
                return path;
            case BoxStyle.BAR :
                return new Line2D.Float(outerBox.x,outerBox.y+outerBox.height,
                        outerBox.x+outerBox.width,outerBox.y+outerBox.height);
            case BoxStyle.AND_BAR :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
                path.moveTo(outerBox.x, outerBox.y + outerBox.height);
                path.lineTo(outerBox.x + outerBox.width, outerBox.y + outerBox.height);
                path.lineTo(outerBox.x + outerBox.width - 4, outerBox.y + outerBox.height - 10);
                path.lineTo(outerBox.x + outerBox.width - 8, outerBox.y + outerBox.height);
                
                return path;
            case BoxStyle.OR_BAR :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
                path.moveTo(outerBox.x, outerBox.y);
                path.lineTo(outerBox.x + outerBox.width, outerBox.y);
                path.lineTo(outerBox.x + outerBox.width - 4, outerBox.y + 10);
                path.lineTo(outerBox.x + outerBox.width - 8, outerBox.y);
                
                return path;
            case BoxStyle.FIXED_CIRCLE :
                return new Ellipse2D.Double(
                                outerBox.x,
                                outerBox.y,
                                18,
                                18);
            case BoxStyle.FIXED_CIRCLE_10 :
                return new Ellipse2D.Double(
                                outerBox.x,
                                outerBox.y,
                                10,
                                10);
            case BoxStyle.FIXED_CIRCLE_IN_CIRCLE_INNER_INTERIOR :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 10);
                Ellipse2D.Double interior = new Ellipse2D.Double(
                        outerBox.x+4,
                        outerBox.y+4,
                        10,
                        10);
                Ellipse2D.Double exterior = new Ellipse2D.Double(
                        outerBox.x,
                        outerBox.y,
                        18,
                        18);
    
                path.append(interior,false);
                path.append(exterior,false);
                path.append(exterior,false); //Added twice to get the right interior.
    
                return path;
            case BoxStyle.SQUARE_BRACKETS :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 8);
                path.moveTo(outerBox.x+5, outerBox.y);
                path.lineTo(outerBox.x, outerBox.y);
                path.lineTo(outerBox.x, outerBox.y + outerBox.height);
                path.lineTo(outerBox.x + 5, outerBox.y + outerBox.height);
                path.moveTo(outerBox.x + outerBox.width -5, outerBox.y);
                path.lineTo(outerBox.x + outerBox.width, outerBox.y);
                path.lineTo(outerBox.x + outerBox.width, outerBox.y + outerBox.height);
                path.lineTo(outerBox.x + outerBox.width - 5, outerBox.y + outerBox.height);
                
                GeneralPath path7 = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 8);
                path7.append(path,false);
                path7.append(path,false);
                return path7;
            case BoxStyle.RIGHT_HOOKS :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 8);
                path.moveTo(outerBox.x, outerBox.y);
                path.lineTo(outerBox.x+outerBox.width, outerBox.y);
                path.lineTo(outerBox.x+outerBox.width, outerBox.y + 5);
                path.moveTo(outerBox.x + outerBox.width, outerBox.y + outerBox.height -5);
                path.lineTo(outerBox.x + outerBox.width, outerBox.y + outerBox.height);
                path.lineTo(outerBox.x, outerBox.y + outerBox.height);
                
                GeneralPath path8 = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 6);
//                path8.append(path,false);
                path8.append(path,false);
                return path8;
            case BoxStyle.EAST_ARROW :
                indent = (float) (outerBox.height / 4.0 / Math.sqrt(3.0));
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 7);
                path.moveTo(outerBox.x, outerBox.y);
                path.lineTo(outerBox.x + outerBox.width - indent, outerBox.y);
                path.lineTo(outerBox.x + outerBox.width, outerBox.y + (float) (outerBox.height / 2.0));
                path.lineTo(outerBox.x + outerBox.width - indent, outerBox.y + outerBox.height);
                path.lineTo(outerBox.x, outerBox.y + outerBox.height);
                path.lineTo(outerBox.x + indent, outerBox.y + (float) (outerBox.height / 2.0));
                path.lineTo(outerBox.x, outerBox.y);
                
                return path;
            case BoxStyle.WEST_ARROW :
                indent = (float) (outerBox.height / 4.0 / Math.sqrt(3.0));
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 7);
                path.moveTo(outerBox.x + indent, outerBox.y);
                path.lineTo(outerBox.x + outerBox.width, outerBox.y);
                path.lineTo(outerBox.x + outerBox.width - indent, outerBox.y + (float) (outerBox.height / 2.0));
                path.lineTo(outerBox.x + outerBox.width, outerBox.y + outerBox.height);
                path.lineTo(outerBox.x + indent, outerBox.y + outerBox.height);
                path.lineTo(outerBox.x, outerBox.y + (float) (outerBox.height / 2.0));
                path.lineTo(outerBox.x + indent, outerBox.y);
                
                return path;
            case BoxStyle.EAST_PARALLELOGRAM :
                indent = (float) (outerBox.height / Math.sqrt(3.0));
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
                path.moveTo(outerBox.x + indent, outerBox.y);
                path.lineTo(outerBox.x + outerBox.width, outerBox.y);
                path.lineTo(outerBox.x + outerBox.width - indent, outerBox.y + outerBox.height);
                path.lineTo(outerBox.x, outerBox.y + outerBox.height);
                path.closePath();
                
                return path;
            case BoxStyle.SOUT_WEST_CORNER :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
                path.moveTo(outerBox.x, outerBox.y);
                path.lineTo(outerBox.x, outerBox.y + outerBox.height);
                path.lineTo(outerBox.x + outerBox.width, outerBox.y + outerBox.height);
                path.lineTo(outerBox.x, outerBox.y + outerBox.height);
                path.lineTo(outerBox.x, outerBox.y);
                
                return path;
            case BoxStyle.UNDERLINED:
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
                path.moveTo(outerBox.x, outerBox.y + outerBox.height);
                path.lineTo(outerBox.x + outerBox.width, outerBox.y + outerBox.height);
                path.lineTo(outerBox.x, outerBox.y + outerBox.height);
                
                return path;
            case BoxStyle.MAN :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 14);
                float middlex = (float) (outerBox.x + outerBox.width / 2.0);
                float middley = outerBox.y + 20;
                path.moveTo(middlex, middley);
                path.lineTo(middlex, middley + 5);
                path.lineTo(middlex + 10, middley + 15);
                path.lineTo(middlex, middley + 5);
                path.lineTo(middlex - 10, middley +15);
                path.lineTo(middlex, middley + 5);
                path.lineTo(middlex, middley + 25);
                path.lineTo(middlex + 10, middley + 35);
                path.lineTo(middlex, middley + 25);
                path.lineTo(middlex - 10, middley + 35);
                path.lineTo(middlex, middley + 25);
                path.lineTo(middlex, middley);
            
                path.append(new Ellipse2D.Float(middlex - 10, outerBox.y, 20, 20), false);
                
                return path;
            case BoxStyle.INVISIBLE :
                return new GeneralPath();
            case BoxStyle.HOLLOW_RECTANGLE :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 8);
                Rectangle2D.Double r12 = new Rectangle2D.Double(
                    outerBox.x,
                    outerBox.y,
                    outerBox.width,
                    outerBox.height);
                path.append(r12, false);
                path.append(r12, false);
                return path;
            
            case BoxStyle.CORNER_FOLDED_PAPER :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 10);
                path.moveTo(outerBox.width-10f,0f);
                path.lineTo(outerBox.width,10f);
                path.lineTo(outerBox.width,outerBox.height);
                path.lineTo(0f,outerBox.height);
                path.lineTo(0f,0f);
                path.lineTo(outerBox.width-10f,0f);
                path.lineTo(outerBox.width-10f,10f);
                path.lineTo(outerBox.width,10f);
                path.lineTo(outerBox.width-10f,10f);
                path.closePath();
                AffineTransform a = new AffineTransform();
                a.translate(outerBox.getX(), outerBox.getY());
                path.transform(a);
                return path;
            
            case BoxStyle.RECTANGLE :
            default :
                return new Rectangle2D.Double(
                    outerBox.x,
                    outerBox.y,
                    outerBox.width,
                    outerBox.height);
        }
    }

    public static Rectangle calculateInnerFromOuterBox(BoxStyle style,
            Rectangle2D outerBox) {
    	Icon icon = style.getIcon();
		
    	if (icon != null) {
    		Rectangle2D rect = new Rectangle((int) outerBox.getX()+icon.getIconWidth()+iconDistance, 
    				(int) outerBox.getY(), 
    				(int) outerBox.getWidth()-icon.getIconWidth()-iconDistance, 
    				(int) outerBox.getHeight());
    			return calculateInnerFromOuterBox_withoutIcon(style, rect);
    	} else {
    		return calculateInnerFromOuterBox_withoutIcon(style, outerBox);
    	}
    }
    
    private static Rectangle calculateInnerFromOuterBox_withoutIcon(BoxStyle style,
            Rectangle2D outerBox) {
        switch (style.getType()) {
            case BoxStyle.ROUND_RECTANGLE :
    
                double corner = 12;
                double inset = corner * (2 - Math.sqrt(2)) / 4;
    
                return new Rectangle(
                    (int) (outerBox.getX() + inset),
                    (int) outerBox.getY(),
                    (int) (outerBox.getWidth() - 2 * inset),
                    (int) outerBox.getHeight());
            case BoxStyle.DIAMOND :
                return new Rectangle(
                    (int) (outerBox.getX() + outerBox.getWidth() / 4.0),
                    (int) (outerBox.getY() + outerBox.getHeight() / 4.0),
                    (int) (outerBox.getWidth() / 2.0),
                    (int) (outerBox.getHeight() / 2.0));
            case BoxStyle.ELLIPSE :
                double insetFactor = (2 - Math.sqrt(2)) / 4;
                return new Rectangle(
                    (int) (outerBox.getX() + outerBox.getWidth() * insetFactor),
                    (int) (outerBox.getY()
                        + outerBox.getHeight() * insetFactor),
                    (int) (outerBox.getWidth() * (1 - 2 * insetFactor)),
                    (int) (outerBox.getHeight() * (1 - 2 * insetFactor)));
            case BoxStyle.FLAT_HEXAGON :
            case BoxStyle.UPPER_FIVE :
            case BoxStyle.LOWER_FIVE :
                return new Rectangle(
                    (int) (outerBox.getX() + 0.15f * outerBox.getWidth()),
                    (int) outerBox.getY(),
                    (int) (outerBox.getWidth() * 0.7f),
                    (int) outerBox.getHeight());
            case BoxStyle.FIXED_CIRCLE_10:
            return new Rectangle(
                (int) (outerBox.getX() + 14),
                (int) outerBox.getY(),
                ((int) outerBox.getWidth()) - 14 ,
                (int) outerBox.getHeight());            
            case BoxStyle.FIXED_CIRCLE:
            case BoxStyle.FIXED_CIRCLE_IN_CIRCLE_INNER_INTERIOR :
                return new Rectangle(
                    (int) (outerBox.getX() + 22),
                    (int) outerBox.getY(),
                    ((int) outerBox.getWidth()) - 22 ,
                    (int) outerBox.getHeight());            
            case BoxStyle.SQUARE_BRACKETS :
                return new Rectangle(
                    (int) (outerBox.getX() + 5),
                    (int) outerBox.getY(),
                    ((int) outerBox.getWidth()) - 10 ,
                    (int) outerBox.getHeight());
    
            case BoxStyle.EAST_ARROW :
            case BoxStyle.WEST_ARROW :
                float indent = (float) (outerBox.getHeight() / 2.0 / Math.sqrt(3.0));
                return new Rectangle(
                    (int) (outerBox.getX() + indent),
                    (int) outerBox.getY(),
                    (int) (outerBox.getWidth()- 2*indent),
                    (int) outerBox.getHeight());
            case BoxStyle.EAST_PARALLELOGRAM :
                float indent2 = (float) (outerBox.getHeight() / Math.sqrt(3.0));
                return new Rectangle(
                    (int) (outerBox.getX() + indent2),
                    (int) outerBox.getY(),
                    (int) (outerBox.getWidth()- 2*indent2),
                    (int) outerBox.getHeight());
            case BoxStyle.MAN :
                int height_text = 0;
                if (outerBox.getHeight() > 55) {
                    height_text = (int) (outerBox.getHeight() - 55.0);
                }
                return new Rectangle(
                    (int) (outerBox.getX()),
                    (int) outerBox.getY()+55,
                    (int) (outerBox.getWidth()),
                    (int) height_text);
            case BoxStyle.CORNER_FOLDED_PAPER :
                return new Rectangle(
                    (int) (outerBox.getX()),
                    (int) outerBox.getY()+10,
                    (int) (outerBox.getWidth()),
                    (int) outerBox.getHeight()-10);
            default :
                return new Rectangle(
                    (int) outerBox.getX(),
                    (int) outerBox.getY(),
                    (int) outerBox.getWidth(),
                    (int) outerBox.getHeight());
        }
    }

    public static Rectangle2D calculateOuterFromInnerBox(
    		BoxStyle style,
            Rectangle2D innerBox) {
    	Icon icon = style.getIcon();
		Rectangle2D rect = calculateOuterFromInnerBox_withoutIcon(style, innerBox);
    	if (icon != null) {
    		rect = new Rectangle((int) rect.getX() - icon.getIconWidth()-iconDistance, 
    				(int) rect.getY(), 
    				(int) rect.getWidth()+icon.getIconWidth()+iconDistance, 
    				(int) rect.getHeight());
    	}
    	return rect;
    }

    private static Rectangle2D calculateOuterFromInnerBox_withoutIcon(
        BoxStyle style,
        Rectangle2D innerBox) {
        switch (style.getType()) {
            case BoxStyle.ROUND_RECTANGLE :
    
                double corner = 12;
                double inset = corner * (2 - Math.sqrt(2)) / 4;
    
                return new Rectangle(
                    (int) (innerBox.getX() - inset),
                    (int) innerBox.getY(),
                    (int) (innerBox.getWidth() + 2 * inset),
                    (int) innerBox.getHeight());
            case BoxStyle.DIAMOND :
                return new Rectangle(
                    (int) (innerBox.getX() - innerBox.getWidth() / 2.0),
                    (int) (innerBox.getY() - innerBox.getHeight() / 2.0),
                    (int) (innerBox.getWidth() * 2.0),
                    (int) (innerBox.getHeight() * 2.0));
            case BoxStyle.ELLIPSE :
                double insetFactor = (2 - Math.sqrt(2)) / 4;
                double outerWidth = innerBox.getWidth() / (1 - 2 * insetFactor);
                double outerHeight =
                    innerBox.getHeight() / (1 - 2 * insetFactor);
                return new Rectangle(
                    (int) (innerBox.getX() - outerWidth * insetFactor),
                    (int) (innerBox.getY() - outerHeight * insetFactor),
                    (int) outerWidth,
                    (int) outerHeight);
            case BoxStyle.FLAT_HEXAGON :
            case BoxStyle.UPPER_FIVE :
            case BoxStyle.LOWER_FIVE :
                double outerWidth1 = innerBox.getWidth() / 0.7f;
                return new Rectangle(
                    (int) (innerBox.getX() - 0.15f * outerWidth1),
                    (int) innerBox.getY(),
                    (int) outerWidth1,
                    (int) innerBox.getHeight());
            case BoxStyle.FIXED_CIRCLE_10 :
                return new Rectangle(
                    (int) (innerBox.getX() - 14),
                    (int) innerBox.getY(),
                    ((int) innerBox.getWidth()) + 14,
                    (int) innerBox.getHeight());
            case BoxStyle.FIXED_CIRCLE:
            case BoxStyle.FIXED_CIRCLE_IN_CIRCLE_INNER_INTERIOR :
                return new Rectangle(
                    (int) (innerBox.getX() - 22),
                    (int) innerBox.getY(),
                    ((int) innerBox.getWidth()) + 22,
                    (int) innerBox.getHeight());
            case BoxStyle.SQUARE_BRACKETS :
                return new Rectangle(
                    (int) (innerBox.getX() - 5),    
                    (int) innerBox.getY(),
                    ((int) innerBox.getWidth()) + 10,
                    (int) innerBox.getHeight());
            case BoxStyle.WEST_ARROW :
            case BoxStyle.EAST_ARROW :
                float indent = (float) (innerBox.getHeight() / 2.0 / Math.sqrt(3.0));
                return new Rectangle(
                    (int) (innerBox.getX() - indent),
                    (int) innerBox.getY(),
                    (int) (innerBox.getWidth()+ 2*indent),
                    (int) innerBox.getHeight());
    
            case BoxStyle.EAST_PARALLELOGRAM :
                float indent2 = (float) (innerBox.getHeight() / Math.sqrt(3.0));
                return new Rectangle(
                    (int) (innerBox.getX() - indent2),
                    (int) innerBox.getY(),
                    (int) (innerBox.getWidth()+ 2*indent2),
                    (int) innerBox.getHeight());
    
            case BoxStyle.MAN :
                return new Rectangle(
                    (int) (innerBox.getX()),
                    (int) innerBox.getY()-55,
                    (int) (innerBox.getWidth()),
                    (int) innerBox.getHeight() + 55);

            case BoxStyle.CORNER_FOLDED_PAPER :
                return new Rectangle(
                    (int) (innerBox.getX()),
                    (int) innerBox.getY()-10,
                    (int) (innerBox.getWidth()),
                    (int) innerBox.getHeight() + 10);
    
            default :
                return new Rectangle(
                    (int) innerBox.getX(),
                    (int) innerBox.getY(),
                    (int) innerBox.getWidth(),
                    (int) innerBox.getHeight());
        }
    }

    public static Rectangle2D calculateBorderBox(
        Rectangle2D box, BoxStyle style, OverlayStyle oStyle) {
        float thickness = style.getThickness();
        float scale = oStyle != null ? oStyle.getLineWidth() : 1f;
        return new Rectangle2D.Double(
            box.getX() - thickness * scale * 0.5,
            box.getY() - thickness * scale * 0.5,
            box.getWidth() + thickness * scale,
            box.getHeight() + thickness * scale);
    }

    public static Rectangle2D calculateUnBorderBox(
        Rectangle2D box, BoxStyle style, OverlayStyle oStyle) {
        float thickness = style.getThickness();
        float scale = oStyle != null ? oStyle.getLineWidth() : 1f;
        return new Rectangle2D.Double(
            box.getX() + thickness * scale * 0.5,
            box.getY() + thickness * scale * 0.5,
            box.getWidth() - thickness * scale,
            box.getHeight() - thickness * scale);
    }

    public static Rectangle discoverHitBox(BoxStyle style, Rectangle outerBox) {    	
    	Rectangle hb = new Rectangle(outerBox);
        if (outerBox.width < 8) {
            hb.width = 8;
            hb.x -= (8 - outerBox.width) / 2;
        }
    
        if (outerBox.height < 8) {
            hb.height = 8;
            hb.y -= (8 - outerBox.height) / 2;
        }
 
      Icon icon = style.getIcon();
		
    	if (icon == null) {
    		return hb;
    	} else {
         	return hb.union(new Rectangle(outerBox.x, outerBox.y, icon.getIconWidth(), icon.getIconHeight()));
    	}
    }

}
