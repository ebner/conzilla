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

package se.kth.cid.conzilla.controller;

import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.content.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.filter.*;
import se.kth.cid.conzilla.center.*;


import java.util.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.*;

public class SimpleController extends JPanel implements MapController
{
  ConzKit kit;

  Vector historyListeners;

  ToolFactory toolFactory;

  Vector maps;
  int currentMap = -1;
  URI currentNeuronContent;
  URI currentMainMapURI;
  //  URI currentMapSetURI;

  //  JPanel mapPanel;
  JComboBox mapBox;
  ToolBarManager toolManager;

  ContentSelector selector;

  ContentDisplayer contentDisplayer=null;

  MetaDataDisplayer metaDataDisplayer=null;

  LibraryDisplayer librarydisplayer;

  FilterFactory filterFactory;

  Filter oldfilter = null;


  class MapEntry
  {
    public ContentDescription  contentDescription;
    public MapManager   mapManager;
    public String title;

    public MapEntry(ContentDescription contentDescription, MapManager mapManager)
    {
      this.mapManager = mapManager;
      this.contentDescription = contentDescription;
      this.title = contentDescription.getNeuron().getMetaData().getValue("Title");
    }
    public String toString()
    {
      return title;
    }
  }

  class BoxModel extends AbstractListModel implements ComboBoxModel
  {
    Object selected = null;
    boolean delaySelect = true;

    public BoxModel()
    {
    }

    public Object getElementAt(int index)
    {
      return maps.elementAt(index);
    }

    public int getSize()
    {
      return maps.size();
    }

    public Object getSelectedItem()
    {
      return selected;
    }

    public void setSelectedItem(Object item)
    {
      selected = item;
      if(delaySelect)
	delaySelect = false;
      else if(item != null)
	{
	  int index = maps.indexOf(item);
	  if(index != currentMap)
	    selectMap(index);
	}
    }
  }

  public SimpleController(ConzKit kit)
  {
    this.kit=kit;

    contentDisplayer=(ContentDisplayer) kit.contentDisplayer;
    metaDataDisplayer=(MetaDataDisplayer) kit.metaData;
    historyListeners = new Vector();

    //    setLayout(new BorderLayout());

    maps = new Vector();

    mapBox = new JComboBox();

    //    add(mapBox, BorderLayout.NORTH);


    //    mapPanel = new JPanel(new BorderLayout());
    //    add(mapPanel, BorderLayout.CENTER);

    toolManager = new ToolBarManager("Tools");
    //    mapPanel.add(toolManager, BorderLayout.SOUTH);

    filterFactory = new SimpleFilterFactory();
  }

  public ConzKit getConzKit()
    {
      return kit;
    }
  public ToolBarManager getToolBar()
  {
    return toolManager;
  }

  public JComboBox getTitleBox()
  {
    return mapBox;
  }

  public void addHistoryListener(HistoryListener l)
  {
    historyListeners.addElement(l);
  }

  public void removeHistoryListener(HistoryListener l)
  {
    historyListeners.removeElement(l);
  }

  public void fireHistoryEvent(HistoryEvent e)
  {
    for(int i = 0; i < historyListeners.size(); i++)
      {
	((HistoryListener) historyListeners.elementAt(i)).historyEvent(e);
      }
  }

  public ComponentLoader getComponentLoader()
  {
    return kit.loader;
  }

  public ComponentSaver getComponentSaver()
  {
    return kit.saver;
  }

  public void setContentDisplayer(ContentDisplayer disp)
  {
    ContentDisplayer oldDisplayer = contentDisplayer;
    contentDisplayer = disp;
    firePropertyChange("contentDisplayer", oldDisplayer, contentDisplayer);
  }

  public ContentDisplayer getContentDisplayer()
  {
    return contentDisplayer;
  }

  public void setContentSelector(ContentSelector sel)
  {
    ContentSelector oldSelector = selector;
    selector = sel;
    firePropertyChange("contentSelector", oldSelector, selector);
  }

  public ContentSelector getContentSelector()
  {
    return selector;
  }

  public void setMetaDataDisplayer(MetaDataDisplayer disp)
  {
    MetaDataDisplayer oldDisplayer = metaDataDisplayer;
    metaDataDisplayer = disp;
    firePropertyChange("metaDataDisplayer", oldDisplayer, disp);
  }

  public MetaDataDisplayer getMetaDataDisplayer()
  {
    return metaDataDisplayer;
  }

  public LibraryDisplayer getLibraryDisplayer()
  {
    return kit.libraryDisplayer;
  }

  public void setToolFactory(ToolFactory factory)
  {
    ToolFactory oldFactory = toolFactory;
    toolFactory = factory;
    firePropertyChange("toolFactory", oldFactory, factory);
  }

  public ToolFactory getToolFactory()
  {
    return toolFactory;
  }


  public void selectContent(URI neuron)
    throws ControllerException
  {
    if(currentMap == -1)
      throw new ControllerException("No current Map when selecting content of neuron" + neuron);

    if(selector == null)
      throw new ControllerException("No selector when selecting content of neuron" + neuron);

    ConceptMap map = ((MapEntry) maps.elementAt(currentMap)).mapManager.
      getDisplayer().getMap();

    NeuronStyle ns = map.getNeuronStyle(neuron);

    if(ns == null)
      throw new ControllerException("No such neuron in current map: "
				      + neuron);

    Tracer.debug("ShowContent: " + ns.getTitle());

    ContentSet cSet = null;

    try {
      cSet = new ContentSet(ns.getNeuron(), kit.loader);
    }
    catch(ComponentException e)
      {
	throw new ControllerException("Could not load content-set for neuron: "
				      + neuron + ":\n " + e.getMessage());
      }
    Vector content = cSet.getContents();
    ContentDescription[] contents =
      new ContentDescription[content.size()];
    content.copyInto(contents);

    selector.selectContent(contents);
    currentNeuronContent = neuron;
  }

  public URI getSelectContent()
  {
    return currentNeuronContent;
  }

  public void zoomIn(URI neuron)
    throws ControllerException
  {
    if(currentMap == -1)
      throw new ControllerException("No current map!");


    ConceptMap map = ((MapEntry) maps.elementAt(currentMap)).mapManager.
      getDisplayer().getMap();

    NeuronStyle ns = map.getNeuronStyle(neuron);

    if(ns == null)
      throw new ControllerException("No such neuron in map: " + neuron);


    URI newMapURI = ns.getDetailedMap();
    URI currentMapURI = getMapURI(currentMap);
    String currentMapTitle = ((MapEntry) maps.elementAt(currentMap)).title;
    String sourceNeuronTitle = ns.getTitle();

    if(newMapURI == null)
      throw new ControllerException("No detailed map in neuron: " + neuron);

    if(newMapURI.equals(currentMainMapURI))
      showMap(newMapURI);
    else
      jumpImpl(newMapURI);

    String newMapTitle = ((MapEntry) maps.elementAt(currentMap)).title;

    fireHistoryEvent(new HistoryEvent(HistoryEvent.ZOOMIN, this,
				      currentMapURI, currentMapTitle,
				      neuron, sourceNeuronTitle,
				      newMapURI, newMapTitle));
  }


  public void jump(URI mapuri) throws ControllerException
  {
    setOldFilter(null);
    URI currentURI = getMapURI(currentMap);
    String currentMapTitle = null;
    if(currentMap != -1)
      currentMapTitle = ((MapEntry) maps.elementAt(currentMap)).title;

    if(mapuri.equals(currentMainMapURI))
      showMap(mapuri);
    else
      jumpImpl(mapuri);

    String newMapTitle = ((MapEntry) maps.elementAt(currentMap)).title;

    fireHistoryEvent(new HistoryEvent(HistoryEvent.JUMP, this,
				      currentURI, currentMapTitle,
				      null, null,
				      mapuri, newMapTitle));
  }


  void jumpImpl(URI uri)
    throws ControllerException
  {
    ContentDescription thiscDesc;
    try {
      thiscDesc = new ContentDescription(uri, kit.loader);
    } catch (ComponentException e)
      {
	throw new ControllerException("Could not load Contentdescription " + uri + ": " + e.getMessage());
      }

    MapManager newMan = loadMap(thiscDesc);
    if(newMan == null)
      return;

    releaseMaps();

    ConceptMap cmap = newMan.getDisplayer().getMap();

    Tracer.debug("Loaded new map: " + cmap.getURI());

    ContentSet mapSet = loadMapSet(cmap);

    int mapIndex = -1;

    if(mapSet == null)
      {
	maps.addElement(new MapEntry(thiscDesc, newMan));
	mapIndex = 0;
      }
    else
      {
	Vector contents = mapSet.getContents();
	int i = 0;

	for(; i < contents.size(); i++)
	  {
	    ContentDescription contDesc
	      = (ContentDescription) contents.elementAt(i);

	    if(contDesc.getContentURI().toString().equals(cmap.getURI()))
	      {
		if(mapIndex != -1)
		  {
		    Tracer.trace("Same map several times in map-set!!", Tracer.ERROR);
		    throw new RuntimeException("Same map several times in map-set "
					       + mapSet.getNeuron().getURI());
		  }
		mapIndex = i;
		maps.addElement(new MapEntry(contDesc, newMan));
	      }
	    else
	      maps.addElement(new MapEntry(contDesc, null));
	  }
	if(mapIndex == -1)
	  {
	    maps.addElement(new MapEntry(thiscDesc, newMan));
	    mapIndex = i;
	  }
      }
    currentMainMapURI = uri;
    mapBox.setModel(new BoxModel());
    mapBox.setSelectedIndex(mapIndex);

    //    selectMap(mapIndex);
    //    mapBox.addItemListener(itemListener);
  }


  public void showMap(URI mapuri) throws ControllerException
  {
    for(int i = 0; i < maps.size(); i++)
      {
	if(mapuri.equals(getMapURI(i)))
	  {
	    mapBox.setSelectedIndex(i);
	    return;
	  }
      }
    throw new ControllerException("No such map in the current mapset: " + mapuri);
  }

  public MapManager getCurrentMapManager()
  {
    if(currentMap == -1)
      return null;

    return ((MapEntry) maps.elementAt(currentMap)).mapManager;
  }

  public URI getCurrentMapURI()
  {
    return getMapURI(currentMap);
  }

  public String getCurrentMapTitle()
  {
    if(currentMap == -1)
      return null;
    return ((MapEntry) maps.elementAt(currentMap)).title;
  }

  void deselectMap(int index)
  {
    if(index < 0)
      return;

    Tracer.debug("deselect " + index);

    MapManager currentManager = ((MapEntry) maps.elementAt(index)).mapManager;

    remove(currentManager);
    if(selector != null)
      selector.selectContent(null);
    currentNeuronContent = null;
  }





  void selectMap(int index)
  {

    MapEntry ent = (MapEntry) maps.elementAt(index);

    try {
      if(ent.mapManager == null)
	ent.mapManager = loadMap(ent.contentDescription);
    } catch(ControllerException e)
      {
	TextOptionPane.showError(this, "Could not find map:\n " + e.getMessage());
      }

    if(ent.mapManager == null)
      {
	Tracer.debug("No such map!!");
	mapBox.setSelectedIndex(currentMap);
      }
    else
      {
	deselectMap(currentMap);
	currentMap = index;

	add(ent.mapManager, BorderLayout.CENTER);
	ent.mapManager.revalidate();
	toolManager.setManager(ent.mapManager);
      }
    repaint();
  }


  URI getMapURI(int index)
  {
    if(index == -1)
      return null;

    return ((MapEntry) maps.elementAt(index)).contentDescription.getURI();
  }




  void releaseMaps()
  {
    //    mapBox.removeItemListener(itemListener);

    deselectMap(currentMap);

    for(int i = 0; i < maps.size(); i++)
      {
	MapEntry ent = (MapEntry) maps.elementAt(i);
	if(ent.mapManager != null)
	  {
	    MapDisplayer mapDisplayer = ent.mapManager.getDisplayer();
	    ConceptMap cmap = mapDisplayer.getMap();
	    ent.mapManager.detach();
	    mapDisplayer.detach();
	    kit.loader.releaseComponent(cmap);
	  }
      }

    maps.removeAllElements();

    currentMap = -1;
  }


  ContentSet loadMapSet(ConceptMap cmap) throws ControllerException
  {
    if(cmap.getMapSet() == null)
      return null;

    ContentSet cSet;

    try {
      cSet = new ContentSet(cmap.getMapSet(), kit.loader);
    }
    catch(ComponentException e)
      {
	throw new ControllerException("Map-set for concept-map "
				      + cmap.getURI() + " could not be loaded:\n "
				      + e.getMessage());
      }

    return cSet;
  }


  MapManager loadMap(ContentDescription cDesc) throws ControllerException
  {
    URI uri=cDesc.getContentURI();

    Component comp;

    Tracer.debug("Will load");
    try {
      comp = kit.loader.loadComponent(uri, kit.loader);
    }
    catch(ComponentException e)
      {
	throw new ControllerException("Concept-map "
				      + uri + " could not be loaded:\n "
				      + e.getMessage());
      }

    if(! (comp instanceof ConceptMap))
      {
	kit.loader.releaseComponent(comp);
	throw new ControllerException("Concept-map "
				      + uri + " could not be loaded:\n "
				      + "Component was no concept-map");
      }

    ConceptMap map = (ConceptMap) comp;


    Tracer.debug("Will make manager");
    MapManager newMan = new MapManager(this, new MapDisplayer(map,cDesc));

    Tracer.debug("Will make tools");
    if(toolFactory != null)
      toolFactory.makeTools(newMan);

    return newMan;
  }

  //  TabSet     getCurrentTabSet();
  //  void       userMessage(String message);

  public FilterFactory getFilterFactory()
  {
      return filterFactory;
  }

  public void setFilterFactory(FilterFactory filterFactory)
  {
      this.filterFactory = filterFactory;
  }

  public Filter getOldFilter()
  {
      return oldfilter;
  }

  public void setOldFilter(Filter filter)
  {
      this.oldfilter = filter;
  }
}
