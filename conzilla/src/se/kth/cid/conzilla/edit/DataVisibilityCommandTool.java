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


package se.kth.cid.conzilla.edit;
import se.kth.cid.util.*;
import se.kth.cid.content.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.center.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class DataVisibilityCommandTool extends AbstractActionTool
{
  protected static String DV_NAME="Data Visibility";

  protected MapEventListener pressListener;

  // Very ugly !!
  public NeuronStyle getOverNeuron()
  {
    return overNeuron;
  }
  public MapManager getManager()
  {
    return manager;
  }

  JMenu menu;
  class CheckBoxListener implements ItemListener {
    public void itemStateChanged(ItemEvent e) {
      if (getOverNeuron()!=null)   //shouldn't be neccessary.. Is it?
	{
	  JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getItemSelectable();
	  String str=(String) source.getText();
	  if (e.getStateChange() == ItemEvent.DESELECTED)
	    getOverNeuron().removeDataTag(str);
	  else if (e.getStateChange() == ItemEvent.SELECTED)
	    getOverNeuron().addDataTag(str);
	}
    }
  }

  CheckBoxListener toggle;


  /** Constructs an DataVisibilityCommandTool.
   *
   *  @param man the MapManager the tool is attached to.
   *  @param cont the controller controlling the manager.
   */
  public DataVisibilityCommandTool(MapManager man, MapController cont)
  {
    super(DV_NAME, man, cont);
    menu=new JMenu(DV_NAME);

    toggle=new CheckBoxListener();

    pressListener = new MapEventListener() {
      public void eventTriggered(MapEvent e)
	{
	  if (e.mouseevent.getID()==MouseEvent.MOUSE_PRESSED)
	    {
	      updateAction(e);
	      menu.getPopupMenu().show(getManager().getDisplayer(),e.mouseevent.getX(),
       			  e.mouseevent.getY());
	    }
	}
    };
  }

  public void putToolInMenu(JPopupMenu me)
    {
      me.add(menu);
    }

  public void setEnabled(boolean bo)
    {
      menu.setEnabled(bo);
    }

  protected boolean updateActionImpl(boolean bo)
  {
    if (!menu.isPopupMenuVisible() && bo)
      {
	menu.removeAll();
	Vector visible=overNeuron.getDataTags();
	String [] strs=overNeuron.getNeuronType().getDataTags();
	for (int i=0;i<strs.length;i++)
	  {
	    JCheckBoxMenuItem jcm=new JCheckBoxMenuItem(strs[i],visible.contains(strs[i]));
	    jcm.addItemListener(toggle);
	    menu.add(jcm);
	  }
	if (menu.getItemCount()!=0 &&
	    mapevent.editabilityChecked && mapevent.mapEditable)
	  menu.setEnabled(true);  //This is called correctly but it doesn't work... A Linux problem???
	else
	  menu.setEnabled(false);
      }
    else
      menu.setEnabled(false);  //Same here, why won't a JMenu become shaded (not enabled)?
    return bo;
  }

  /** Observe that this commandTool shouldn't be activated if it is used as
   *  a submenu. If it is alone responsible for invoking itself it should be activated.
   *  Otherwise its parent menu will invoke it when needed instead of having a mouselistener
   *  waiting to find a suitable trigger.
   */
  protected void activateImpl()
  {
    manager.getDisplayer().addMapEventListener(pressListener,
					       MapDisplayer.PRESS_RELEASE);
  }

  /** See note for activateImpl.*/
  protected void deactivateImpl()
  {
    manager.getDisplayer().removeMapEventListener(pressListener,
						  MapDisplayer.PRESS_RELEASE);
  }

  protected void detachImpl()
  {
    pressListener = null;
    controller = null;
    manager = null;
  }
}
