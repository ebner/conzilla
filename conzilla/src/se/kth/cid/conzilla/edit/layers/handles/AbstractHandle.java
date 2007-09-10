/* $Id$ */
/*
  This file is part of the Conzilla browser, designed for
  the Garden of Knowledge project.
  Copyright (C) 1999  CID (http://www.nada.kth.se/cid)
  
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/


package se.kth.cid.conzilla.edit.layers.handles;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conceptmap.*;
import java.awt.*;
import java.util.*;

/** 
 *  @author Matthias Palmer
 *  @version $version: $
 */
public abstract class AbstractHandle implements Handle
{
    static final BasicStroke thinStroke = new BasicStroke(1f, BasicStroke.CAP_ROUND,
						       BasicStroke.JOIN_ROUND);
    static final BasicStroke thickStroke = new BasicStroke(2f, BasicStroke.CAP_ROUND,
						       BasicStroke.JOIN_ROUND);
    public static int radius=4;

    ConceptMap.Position originalPosition;
    Object parent=null;
    boolean edited=false;
    boolean selected;    

    //Some simple set/get/clear/query functions
    public void clearEdited()                        {originalPosition = getPosition();edited=false;}
    public boolean isEdited()                           {return edited;}
    public ConceptMap.Position getOriginalPosition() {return originalPosition;}
    public void setSelected(boolean selected)        {this.selected=selected;}
    public boolean isSelected()                      {return selected;}
    public void setParent(Object parent)             {this.parent=parent;}
    public Object getParent()                        {return parent;}
    public static void setRadius(int r)              {radius=r;}
    public static int getRadius()                    {return radius;} 


    protected Collection followHandles=null;
    
    /** If there are any followers, this function moves them, 
     *  inteded to be called from within the @link AbstractHandle.drag function.
     *
     *  @see AbstractHandle.drag
     */
    public Collection dragFollowers(int x, int y)
    {
	Collection cols=new Vector();
	Collection col;
	if (followHandles != null)
	    {
		Iterator it=followHandles.iterator();
		while (it.hasNext())
		    {
			Handle followhandle = (Handle) it.next();
			if (followhandle==this)
			    continue;
			col = followhandle.dragForced(x, y);
			if (col!=null)
			    cols.add(col);
		    }
	    }
	return cols;
    }    

/*    public Collection setFollowersSelected(boolean select)
    {
	Collection cols=new Vector();
	Collection col;
	if (followHandles != null)
	    {
		Iterator it=followHandles.iterator();
		while (it.hasNext())
		    {
			Handle followhandle = (Handle) it.next();
			if (followhandle==this)
			    continue;
			cols.add(followhandle.getBounds());
			followhandle.setSelected(select);
			//			col = followhandle.setFollowersSelected(selected);
			//if (col!=null)
			//cols.addAll(col);
		    }
	    }
	return cols;
    }	*/
    /*    public void addFollowers(Collection fols)
    {
	if (followHandles!=null)
	    followHandles.addAll(fols);
	else
	    {
		followHandles= new HashSet();
		followHandles.addAll(fols);
	    }
	    }*/
    public void setFollowers(Collection fols)
    {
	followHandles = fols;
    }
    public Collection getFollowers()
    {
	return followHandles;
    }
    public Rectangle getBounds()
    {return null;}
}
