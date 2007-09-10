/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.tool;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;


/**
 *
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public abstract class ActionMapMenuTool extends MapMenuTool implements ActionListener
{

    public ActionMapMenuTool(String name, MapController cont)
    {
	super(name, cont);
    }  
    
    public ActionMapMenuTool(String name, String resbundle, MapController cont)
    {
	super(name, resbundle, cont);
    }  

    public void setJMenuItem(JMenuItem mi)
    {
	JMenuItem oldmi = getJMenuItem();
	if(oldmi != null)
	    oldmi.removeActionListener(this);
	super.setJMenuItem(mi);
	mi.addActionListener(this);
    }
    
    public void update(MapEvent e)
    {
	super.update(e);
	getJMenuItem().setEnabled(updateEnabled());
    }

    protected abstract boolean updateEnabled();    
}
