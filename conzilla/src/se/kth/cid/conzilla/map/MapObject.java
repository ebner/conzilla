/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map;

import java.awt.Dimension;
import java.awt.Graphics;

import se.kth.cid.component.EditListener;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.map.graphics.Mark;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;

/**
 * MapObjects are responsible for drawing the map.
 * 
 * @author matthias
 */
public interface MapObject extends EditListener
{ 
  /**
   * The drawerlayout presented.
   * 
   * @return a {@link DrawerLayout}
   */
  DrawerLayout getDrawerLayout();
  
  /**
   * The concept the drawerlayout presents.
   * @return
   */
  Concept      getConcept();
    
  void clearMark();
  Mark getMark();
  Mark getMark(Object responsible);
  void pushMark(Mark mark, Object responsible);
  Mark popMark(Object responsible);
  void replaceMark(Mark newMark, Object responsible);
  boolean isDefaultMark();

  void setVisible(boolean vis);
  boolean getVisible();
  void setDisplayLanguageDiscrepancy(boolean b);
  void setEditable(boolean editable, MapEvent e);
  void setScale(double scale);
  void paint(Graphics g);
  void colorUpdate();
  Dimension getPreferredSize();
  boolean checkAndFillHit(MapEvent m);
  void detach();
}
