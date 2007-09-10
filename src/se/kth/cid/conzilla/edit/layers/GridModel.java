/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Vector;

public class GridModel
{
    public static final int STYLE_INVISIBLE=0;
    public static final int STYLE_CONTINUOUS=1;
    public static final int STYLE_CORNERS=2;
    public static final int STYLE_DOTTED=3;

    public final int gridLayouts[]={STYLE_INVISIBLE, 
				   STYLE_CONTINUOUS, 
				   STYLE_CORNERS, 
				   STYLE_DOTTED};

    public final String gridLayoutNames[]={"INVISIBLE", 
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
    int layout;
    Vector listeners;

    public GridModel(int gran)
    {
	granularity=gran;
	gridOn=true;
	layout=STYLE_CORNERS;
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

    public void setGridLayout(int layout)
    {
	if (this.layout==layout)
	    return;
	this.layout=layout;
	firePropertyChangeEvent(new PropertyChangeEvent(this, "Set Layout", null, null));  
    }

    public int getGridLayout()
    {
	return layout;
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
