/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.tool;
import javax.swing.JMenuItem;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.properties.ConzillaResourceManager;


/**
 *
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public abstract class MapMenuTool implements MapMenuItem
{
    /** The controller to use.
     */
    protected MapController controller;
    
    /** The map object we currently are over.
     */
    protected MapObject mapObject;
    
    /** UpdateState saves the MapEvent for use when
     *  the tool is used.*/
    protected MapEvent mapEvent;
    
    JMenuItem menuItem;
    String name;
    String resbundle;

    public MapMenuTool(String name, MapController cont)
    {
	this.name = name;
	this.resbundle = getClass().getName();
	controller = cont;
	setJMenuItem(new JMenuItem());	
    }  
    
    public MapMenuTool(String name, String resbundle, MapController cont)
    {
	this.name = name;
	this.resbundle = resbundle;
	controller = cont;
	setJMenuItem(new JMenuItem());	
    }  

    public void setJMenuItem(JMenuItem mi)
    {
	menuItem = mi;
	if(resbundle != null)
	    ConzillaResourceManager.getDefaultManager().customizeButton(mi, resbundle, name);
	else
	    mi.setText(name);
    }
    
    public final JMenuItem getJMenuItem()
    {
	return menuItem;
    }
    
    public void update(MapEvent e)
    {
	mapEvent = e;
	mapObject = mapEvent.mapObject;	
    }

    public void detach()
    {
	controller = null;
	mapObject = null;    
    }
}
