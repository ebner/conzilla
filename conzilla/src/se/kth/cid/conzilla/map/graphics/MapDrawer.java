/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map.graphics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import se.kth.cid.conzilla.map.MapDisplayer;

public abstract class MapDrawer
{
  MapDisplayer displayer;
  
  public MapDrawer(MapDisplayer displayer)
    {
      this.displayer = displayer;
    }
    
  /**
   * Use carefully, only for predefined drawing of types.
   *
   */
  public MapDrawer() {
  }

  public Mark getMark(Mark mark)
    {
	if(getErrorState())
	    return new Mark(MapDisplayer.COLOR_CONCEPT_ERROR, null, null);
	else
	    return mark;
    }    

  public void coloredPaint(Graphics g, DrawerMapObject dmo)
    {
      Mark myMark;
      if (displayer.getGlobalMarkColor()==null)
	  {
	      if (dmo.isDefaultMark())
		  myMark = getMark(dmo.getMark());
	      else
		  myMark=dmo.getMark();
	  }
      else
	  myMark=new Mark(displayer.getGlobalMarkColor(), null, null);

      doPaint((Graphics2D) g, myMark);
    }

  void doPaint(Graphics2D g, Mark mark)
    {
    }

  public abstract boolean getErrorState();
}
