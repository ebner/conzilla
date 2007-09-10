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


package se.kth.cid.conzilla.tool;
import se.kth.cid.util.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;


public abstract class StateTool extends Tool implements PropertyChangeListener
{
    public static final String ACTIVATED = "activated";

    boolean activated = true;
    
    public StateTool(String name, boolean init)
    {
	super(name);
	setActivated(init);
	addPropertyChangeListener(this);
    }

    public StateTool(String name, String resbundle, boolean init)
    {
	super(name, resbundle);
	setActivated(init);
	addPropertyChangeListener(this);
    }


    public void setActivated(boolean b)
    {
	if(activated == b)
	    return;
	boolean oldValue = activated;
        activated = b;
        firePropertyChange(ACTIVATED, new Boolean(oldValue), new Boolean(b));
    }

    public boolean isActivated()
    {
	return activated;
    }
    
    public final void actionPerformed(ActionEvent e)
    {
	// This tool does not work like an action.
    }

}
