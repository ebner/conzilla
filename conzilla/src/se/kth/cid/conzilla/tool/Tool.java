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

/** Tools are objects that are responsible for reacting to the user's
 *  gestures. They are usually placed in menus and in toolbars.
 *
 *  Tools can be in two states: activated or deactivated.
 *  Independently of this, they can be enabled and disabled.
 *
 *  There are three types of tools. The ACTION tools does something
 *  immediately in response to being activated, and thus has no concept
 *  of deactivation.
 *
 *  The EXCLUSIVE tools can be activated only one at a time and usually
 *  responds to user actions continuously while activated.
 *
 *  The STATE tools may be activated and deactivated independently of other
 *  tools.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public abstract class Tool extends AbstractAction
{

    public static final String ENABLED = "enabled";

    String name;
    
    public Tool(String name)
    {
	init(name, getClass().getName());
    }

    
    public Tool(String name, String resbundle)
    {
	init(name, resbundle);
    }

    void init(String name, String resbundle)
    {
	this.name = name;
	if(resbundle != null)
	    {
		String n = ConzillaResourceManager.getDefaultManager().getString(resbundle, name);
		if(n == null)
		    n = name;
		putValue(NAME, n);
		putValue(SHORT_DESCRIPTION, ConzillaResourceManager.getDefaultManager().getString(resbundle,
												  name + "_TOOL_TIP"));
	    }
	else
	   putValue(NAME, name);

    }

    /** Returns the name of the tool.
     *
     *  @return the name of the tool.
     */
    public String getText()
    {
	return (String) getValue(NAME);
    }
    
    public String getName()
    {
	return name;
    }
    
    /** Returns the tool tip of the tool.
     *
     *  @return the tool tip of the tool.
     */
    public String getToolTip()
    {
	return (String) getValue(SHORT_DESCRIPTION);
    }
    
    /** Returns the icon of the tool, if any.
     *
     *  @return the icon of the tool.
     */
    public Icon getIcon()
    {
	return (Icon) getValue(SMALL_ICON);
    }

    public void setIcon(Icon i)
    {
	putValue(SMALL_ICON, i);
    }

    public void setAccelerator(KeyStroke k)
    {
	putValue(ACCELERATOR_KEY, k);
    }

    public KeyStroke getAccelerator()
    {
	return (KeyStroke) getValue(ACCELERATOR_KEY);
    }

    
    /** Should be called when this tool is no longer being used.
     *
     *  Makes sure this tool detaches all listeners etc.
     *  so that it can be gc'ed. Calling any method in the tool after this will
     *  cause errors.
     *
     *  A tool will be deactivated and have its listeners removed.
     */
    public void detach()
    {}
}
