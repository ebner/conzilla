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

package se.kth.cid.conzilla.browse;
import se.kth.cid.util.*;
import se.kth.cid.content.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.filter.*;
import se.kth.cid.neuron.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/** This is a view-command-tool that have to be embedded into a menu.
 *  The reason is that it needs a neuron to act on.
 *  Typically this is done by calling updateState with a mapEvent as input.
 *
 *  @author Matthias Palmèr
 *  @version $Revision$
 */
public class ViewCommandTool extends AbstractActionTool
{
  protected static String VIEW_NAME="View";
  ContentTool contenttool;
  JMenu viewchoice;
  JMenu submenu;
  JPopupMenu choice = null;
  Filter filter;
  String filterURI;
  FilterAction filteraction;
  FilterNode node;
  int pos = -1;
  Vector contents;

  /** Constructs an ViewCommandTool.
   *
   *  @param man the MapManager the tool is attached to.
   *  @param cont the controller controlling the manager.
   */
  public ViewCommandTool(MapManager man, MapController cont)
  {
    super(VIEW_NAME, man, cont);

    action=new AbstractAction(VIEW_NAME)
    {
      public void actionPerformed(ActionEvent ae)
      {
    	    action();
      }
    };

    contenttool=new ContentTool(man,cont);
    manager.setOldFilter();
  }

  /** Updates the aspects in the view menu.
   *  Enables or disenabled the command in the menu accordingly to if
   *  there is content to view.
   */
  protected boolean updateActionImpl(boolean bo)
  {
      if (pos != -1)
         choice.remove(pos);
      if (overNeuron != null)
      {
        filter = manager.getFilter(overNeuron);

        if (filter != null)
        {
          viewchoice = new JMenu(VIEW_NAME);
          createViewMenu(viewchoice);
        }
      }
      putToolInMenu(choice);

      if (bo && overNeuron.getNeuron().getRolesOfType("content").length > 0)
	{
	  action.setEnabled(true);
	  return true;
	}
      action.setEnabled(false);
      return false;
  }

  /** This is a view-command that results in a set of content is displayed.
   *  Observe that updateState has to have been succesfully last time called.
   *  Otherwise the surf-action isn't activated and this function isn't called.
   *
   *  @see Controller.selectContent()
   */
  public void action()
    {
      try{
	controller.selectContent(new URI(overNeuron.getNeuron().getURI()));
      } catch (ControllerException e)
	{
	  TextOptionPane.showError(manager, "Failed to select content:\n "
				   + e.getMessage());
	}
      catch(MalformedURIException e)
	{
	  Tracer.trace("Component had illegal URI: " +
		       overNeuron.getNeuron().getURI()
		       + ": " + e.getMessage()
		       + "!", Tracer.ERROR);
	}
    }

  /** Adds the view-action-command as an action or a menu to a given menu.
   *
   * @param menu a JpopupMenu to add an action or menu to.
   */
  public void putToolInMenu(JPopupMenu menu)
  {
    if (pos == -1)
    {
      choice = menu;
      submenu = new JMenu();
      menu.add(submenu);
      pos = menu.getComponentIndex(submenu);
      menu.remove(pos);
    }
    if (filter == null || overNeuron == null)
      menu.add(action);
    else
      menu.insert(viewchoice, pos);
  }

  public void activateContentRelated()
  {
    contenttool.activateImpl();
  }

  public void deactivateContentRelated()
  {
    contenttool.deactivateImpl();
  }

  protected void activateImpl()
  {
    super.activateImpl();
    activateContentRelated();
  }

  protected void deactivateImpl()
  {
    super.deactivateImpl();
    deactivateContentRelated();
  }

  protected void detachImpl()
  {
    super.detachImpl();
    contenttool.detachImpl();
  }

  /** Creates an aspect tree of menus to a given menu.
   *
   * @param menu a JMenu to add an menu to.
   */
  public void createViewMenu(JMenu menu)
  {
    Tracer.debug("Create View Menu");

    node = filter.getFilterNode();
    contents = filter.filterContent(node, overNeuron.getNeuron());
    if (contents.size() == 0)
       menu.setEnabled(false);
    else
    {
       menu.add(action);
       for (int i=0; i < node.numOfRefines(); i++)
         recursiveMenu(menu, node.getRefine(i));
    }
  }

  /** Creates menus recursively to aid createViewMenu.
   *
   * @param menu a JMenu to add an menu to.
   * @param refine the Filternode to represent as an action or menu.
   */
  public void recursiveMenu(JMenu menu, FilterNode refine)
  {
    if (refine.numOfRefines() != 0)
    {
      submenu = new JMenu(refine.getFilterTag());
      contents = filter.filterContent(refine, overNeuron.getNeuron());
      filteraction = new FilterAction(refine, VIEW_NAME, manager);
      if (contents.size() == 0)
      {
         submenu.setEnabled(false);
         filteraction.setEnabled(false);
      }
      else
      {
         filteraction.setContent(contents);
         submenu.add(filteraction);
         for (int i=0; i < refine.numOfRefines(); i++)
             recursiveMenu(submenu, refine.getRefine(i));
      }
      menu.add(submenu);
    }
    else
    {
       contents = filter.filterContent(refine, overNeuron.getNeuron());
       filteraction = refine.getAction();
       if (contents.size() == 0)
          filteraction.setEnabled(false);
       else
          filteraction.setContent(contents);
       menu.add(filteraction);
    }
  }
}
