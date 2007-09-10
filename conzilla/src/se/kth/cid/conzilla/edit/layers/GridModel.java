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
package se.kth.cid.conzilla.edit.layers;
import javax.swing.*;
import java.util.*;
import java.beans.*;

public class GridModel
{
    public static final int STYLE_INVISIBLE=0;
    public static final int STYLE_CONTINUOUS=1;
    public static final int STYLE_CORNERS=2;
    public static final int STYLE_DOTTED=3;

    public final int gridStyles[]={STYLE_INVISIBLE, 
				   STYLE_CONTINUOUS, 
				   STYLE_CORNERS, 
				   STYLE_DOTTED};

    public final String gridStyleNames[]={"INVISIBLE", 
					  "CONTINUOUS", 
					  "CORNERS", 
					  "DOTTED"};

    public final int gridSizes[]={2, 
				  3, 
				  4,
				  6, 
				  8, 
				  10, 
				  12, 
				  18, 
				  24, 
				  30, 
				  40, 
				  50, 
				  60};
    
    int granularity;
    boolean gridOn;
    int style;
    Vector listeners;

    public GridModel(int gran)
    {
	granularity=gran;
	gridOn=true;
	style=STYLE_CORNERS;
	listeners=new Vector();
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
	listeners.addElement(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
	listeners.remove(listener);
    }
    protected void firePropertyChangeEvent(PropertyChangeEvent pe)
    {
	Enumeration en=listeners.elements();
	for (;en.hasMoreElements();)
	    ((PropertyChangeListener) en.nextElement()).propertyChange(pe);
    }
    
    public boolean isGridOn()
    {
	return gridOn;
    }
    
    public void setGrid(boolean bo)
    {
	if (gridOn==bo)
	    return;
	gridOn=bo;
	firePropertyChangeEvent(new PropertyChangeEvent(this, "Set Grid", null, null));  
    }

    public void setGridStyle(int style)
    {
	if (this.style==style)
	    return;
	this.style=style;
	firePropertyChangeEvent(new PropertyChangeEvent(this, "Set Style", null, null));  
    }

    public int getGridStyle()
    {
	return style;
    }

    public void setGranularity(int granularity)
    {
	if (this.granularity==granularity)
	    return;
	this.granularity=granularity;
	firePropertyChangeEvent(new PropertyChangeEvent(this, "Set Granularity", null, null));  
    }
    
    public int getGranularity()
    {
	return granularity;
    }    
}
