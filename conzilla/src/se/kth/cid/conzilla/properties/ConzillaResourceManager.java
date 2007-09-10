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


package se.kth.cid.conzilla.properties;
import se.kth.cid.util.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/** 
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ConzillaResourceManager
{
    static ConzillaResourceManager defaultManager;
    
    Locale defaultLocale;
    
    HashMap bundles;
    
    ConzillaResourceManager()
    {
	bundles = new HashMap();
	defaultLocale = Locale.getDefault();
    }
    
    ResourceBundle getBundle(String basename)
    {
	//null allowed...

	ResourceBundle rb = (ResourceBundle) bundles.get(basename);

	if(rb != null)
	    return rb;
	
	if(bundles.containsKey(basename))
	    return null;

	try {
	    rb = ResourceBundle.getBundle(basename, defaultLocale);
	} catch(MissingResourceException e)
	    {
		//Do nothing
	    }
	bundles.put(basename, rb);
	return rb;
    }

    public Locale getDefaultLocale()
    {
	return defaultLocale;
    }
    
    public String getString(String resourceBundle, String key)
    {
	ResourceBundle b = getBundle(resourceBundle);

	if(b == null)
	    {
		Tracer.debug("ResourceBundle " + resourceBundle + " not found");
		return null;
	    }
	
	try {
	    return b.getString(key);
	}catch (MissingResourceException e)
	    {
		Tracer.debug("resource " + key + " not found in RB " + resourceBundle);
		return null;
	    }
    }


    public void customizeButton(AbstractButton but, String resourceBundle, String prop)
    {
	String buts = getString(resourceBundle, prop);
	String buttooltip = getString(resourceBundle, prop + "_TOOL_TIP");

	but.setText(buts != null ? buts : prop);
	if(buttooltip != null)
	    but.setToolTipText(buttooltip);
    }    

    

    public static ConzillaResourceManager getDefaultManager()
    {
	if(defaultManager == null)
	    defaultManager = new ConzillaResourceManager();

	return defaultManager;
    }
}
