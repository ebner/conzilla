/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;

import javax.swing.border.EmptyBorder;

/** 
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class ArrowBorder extends EmptyBorder
{ 
  protected Color color;
  protected Polygon polygon;
  protected int direction;
  protected int position;
  protected int size;
  protected int padding;
  private boolean translated = false;
  public static final int NORTH = 0;
  public static final int SOUTH = 1;
  public static final int WEST = 2;
  public static final int EAST = 3;
  

  public ArrowBorder(int direction, int position) {
      this(direction, position, 4, 2, null);
  }
  
  public ArrowBorder(int direction, int position, int size, int padding, Color color)
  {
      super(position == NORTH ? size + 2*padding : 0, 
            position == WEST ? size + 2*padding : 0, 
            position == SOUTH ? size + 2*padding: 0, 
            position == EAST ? size + 2*padding: 0);
      
     this.color= color != null ? color : Color.black;
     this.direction = direction;
     this.size = size;
     this.padding = padding;
     this.position = position;
     
     polygon = new Polygon();

     int half = (int) (size/2);
     switch (direction) {
         case NORTH:
             polygon.addPoint(0,size);
             polygon.addPoint(size,0);
             polygon.addPoint(2*size,size);
             break;
         case SOUTH:
             polygon.addPoint(0,0);
             polygon.addPoint(size,size);
             polygon.addPoint(2*size,0);
             break;
         case EAST:
             polygon.addPoint(0,0);
             polygon.addPoint(size,size);
             polygon.addPoint(0,2*size);
             break;
         case WEST:
             polygon.addPoint(size,0);
             polygon.addPoint(0,size);
             polygon.addPoint(size,2*size);
             break;
     }
  }

   private void translateOnce(int x, int y, int width, int height) {
	   if (!translated) {
		   translated = true;
		   switch (position) {
		   case NORTH:
			   polygon.translate(x+width/2-size, y+padding);
			   break;
		   case SOUTH:
			   polygon.translate(x+width/2-size, y+height-size-padding);
			   break;
		   case EAST:
			   polygon.translate(x+width-size-padding, y+height/2-size);
			   break;
		   case WEST:
			   polygon.translate(x+padding, y+height/2-size);
			   break;
		   }
	   }
   }
  
    /**
    /**
     * Paints the matte border.
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        translateOnce(x, y, width, height);
    	Color oldColor = g.getColor();
        g.setColor(color);
        
	    ((Graphics2D) g).fill(polygon);
        g.setColor(oldColor);
    }

    /**
     * Returns whether or not the border is opaque.
     */
    public boolean isBorderOpaque() { 
        // If a tileIcon is set, then it may contain transparent bits
        return true; 
    }	  
}
