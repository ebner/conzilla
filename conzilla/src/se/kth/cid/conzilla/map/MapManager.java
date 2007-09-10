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


package se.kth.cid.conzilla.map;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import se.kth.cid.util.*;
import se.kth.cid.content.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.controller.*;
import java.util.*;
import se.kth.cid.conzilla.filter.*;


/** This class manages a MapDisplayer and associated tools.
 *  The MapDisplayer is placed inside a ScrollPane.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class MapManager extends JPanel
{
  /*  public final static Integer BACKGROUND_LAYER = new Integer(10);
  public final static Integer MAP_LAYER = new Integer(20);
  public final static Integer OVERLAY_LAYER = new Integer(30);
  */

  /** The displayer to manage.
   */
  MapDisplayer displayer;

  //  JLayeredPane layers;

  /** The scroll pane.
   */
  JScrollPane  scroll;

  /** The toolset belonging to this map.
   */
  ToolSet toolSet;

  /** The tool bars belonging to this map.
   */
  Vector toolBars;

  /** The name of the actvie toolbar.
   */
  String savedToolBar;

  /** The conceptmap may be a library, a IndexLibrary.
   */
  IndexLibrary library;

  /** Constructs a MapManager managing the specified MapDisplayer.
   *
   * @param disp the displayer to manage.
   */

  /** ### The filter belonging to this map.
   */
  Filter filter;

  MapController controller;

  public MapManager(MapController controller, MapDisplayer disp)
  {
    this.controller = controller;

    displayer = disp;

    toolSet = new ToolSet();

    toolBars = new Vector();

    setLayout(new BorderLayout());

    library=null;

    //    layers = new JLayeredPane();
    displayer.setLayout(new LayerManager(displayer));
    scroll = new JScrollPane(displayer, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
    //    layers.add(displayer, MapDisplayer.MAP_LAYER);

    add(scroll, BorderLayout.CENTER);
  }

  /** Constructs a MapManager and fixes the library if there is one.
   *  @param loader  a ComponentLoader needed to initalize the librarys.
   *  @param map     a ConceptMap to start up the MapManager with.
   *  @param cdesc   a ContentDescription describing the map to fetch.
   */
  public MapManager(MapController controller, ConceptMap map, ContentDescription cdesc)
    {
      this(controller, new MapDisplayer(map, cdesc));
      String mimetype=cdesc.getContentType().toString();
      if (mimetype.equals("application/x-conceptmap-clipboardlibrary"))
	{
	  library=controller.getLibraryDisplayer().getLibrary().getLibrary("clipboardlibrary");
	  if (library.getConceptMap()!=map)
	    library=new ClipboardLibrary(controller, null, cdesc.getURI());
	}
      if (mimetype.equals("application/x-conceptmap-templatelibrary"))
	{
	  library=controller.getLibraryDisplayer().getLibrary().getLibrary("templatelibrary");
	  if (library.getConceptMap()!=map)
	    library=new TemplateLibrary(controller, null, cdesc.getURI());
	}
      if (mimetype.equals("application/x-conceptmap-resourcelibrary"))
	{
	  library=controller.getLibraryDisplayer().getLibrary();
	  if (library.getConceptMap()!=map)
	    library=new ResourceLibrary(controller, null, cdesc.getURI());
	}
    }

  /** Constructs a MapManager from a given library.
   *  @param library  an IndexLibrary (at least)
   *  @param cdesc    an ContentDescription describing the map (library).
   */
  public MapManager(MapController controller, IndexLibrary library, ContentDescription cdesc)
    {
      this(controller, new MapDisplayer(library.getConceptMap(),cdesc));
      this.library=library;
    }

  /** Adds a layer above the map.
   *  It will be managed by a LayerManager.
   *
   *  @param comp the component to add.
   */
  public void addOverlayLayer(JComponent comp)
  {
    displayer.add(comp, MapDisplayer.OVERLAY_LAYER, 0);
  }

  /** Reomves a layer above the map.
   *
   *  @param comp the component to remove.
   */
  public void removeOverlayLayer(JComponent comp)
  {
    displayer.remove(comp);
  }

  /** Adds a layer below the map.
   *  It will be managed by a LayerManager.
   *
   *  @param comp the component to add.
   */
  public void addBackgroundLayer(JComponent comp)
  {
    displayer.add(comp, MapDisplayer.BACKGROUND_LAYER, 0);
  }

  /** Removes a layer below the map.
   *
   *  @param comp the component to remove.
   */
  public void removeBackgroundLayer(JComponent comp)
  {
    displayer.remove(comp);
  }

  /** Returns the displayer.
   *
   *  @return the displayer.
   */
  public MapDisplayer getDisplayer()
  {
    return displayer;
  }

  /** Returns the tool set.
   *
   *  @return the tool set.
   */
  public ToolSet getToolSet()
  {
    return toolSet;
  }

  /** Set the toolBar, possibly from the ToolFactory.
   *  If the MimeType indicates a library then the tools are
   *  fetched from the library-class.
   * @param toolFactory is the defautl ToolFactory.
   */
  public void makeTools(ToolFactory toolFactory)
    {
      if (library!=null)
	library.makeTools(this);
      else
	if(toolFactory != null)
	  toolFactory.makeTools(this);
    }

  /** Returns the tool bars.
   *
   *  @return the tool bars.
   */
  public Vector getToolBars()
  {
    return toolBars;
  }

  /** Adds a toolbar to this manager.
   *
   * @param t the toolbar to add.
   */
  public void addToolBar(LazyToolBar t)
  {
    toolBars.addElement(t);
  }

  /** Removes a toolbar to this manager.
   *
   * @param t the toolbar to remove.
   */
  public void removeToolBar(LazyToolBar t)
  {
    toolBars.removeElement(t);
  }

  /** Returns the name of the active toolbar.
   *
   *  @return the name of the active toolbar.
   */
  public String getActiveToolBar()
  {
    return savedToolBar;
  }

  /** Sets the name of the active toolbar.
   *
   *  @param savedToolBar the name of the active toolbar.
   */
  public void setActiveToolBar(String savedToolBar)
  {
    this.savedToolBar = savedToolBar;
  }

  /** Detaches the manager from the environment.
   *
   *  All toolbars are detached, all tools are removed from the toolset
   *  and detached, and all toolbars are emptied of components.
   *
   *  This method should be called to ensure this object will be gc'ed.
   */
  public void detach()
  {
    scroll.removeAll();
    scroll = null;

    int i;

    for(i = 0; i < toolBars.size(); i++)
      {
	LazyToolBar toolBar = (LazyToolBar) toolBars.elementAt(i);
	toolBar.detach();
      }

    Tool[] tools = toolSet.getTools();

    toolSet.removeAllTools();


    for(i = 0; i < tools.length; i++)
      tools[i].detach();


    toolBars.removeAllElements();


    toolBars = null;
    toolSet = null;

    displayer = null;

    savedToolBar = null;
  }

  /** Returns the current filter.
   *
   *  @return the current filter.
   */
  public Filter getFilter()
  {
    return filter;
  }

  /** Returns and sets the current filter given a NeuronStyle.
   *
   *  @param overNeuron the NeuronStyle associated with this filter.
   *  @return the current filter.
   */
  public Filter getFilter(NeuronStyle overNeuron)
  {
    String filterURI;

    if ((filterURI = overNeuron.getNeuron().getMetaData().getValue("Filter")) != null)
    {
      try {
      filter = controller.getFilterFactory().createFilter(this, controller, filterURI);
      } catch (FilterException e)
      {
        filter = null;
        Tracer.debug("Wrong URI, not a filter.");
      }
      return filter;
    }
    else
    {
        if ((filterURI = displayer.getContentDescription().getNeuron().getMetaData().getValue("Filter")) != null)
        {
          if (filter == null || !filter.getURI().equals(filterURI))
          {
            try {
            filter = controller.getFilterFactory().createFilter(this, controller, filterURI);
            } catch (FilterException e)
            {
              filter = null;
              Tracer.debug("Wrong URI, not a filter.");
            }
            controller.setOldFilter(filter);
          }
          return filter;
        }
        else
          return filter = controller.getOldFilter();
    }
  }

  /** Sets the old filter.
   */
  public void setOldFilter()
  {
    String filterURI;

    if ((filterURI = displayer.getContentDescription().getNeuron().getMetaData().getValue("Filter")) != null)
    {
      try {
      filter = controller.getFilterFactory().createFilter(this, controller, filterURI);
      } catch (FilterException e)
      {
        filter = null;
        Tracer.debug("Wrong URI, not a filter.");
      }
      controller.setOldFilter(filter);
    }
  }
}
