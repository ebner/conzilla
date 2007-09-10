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
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/** This is a surf-alteration-tool that have to be embedded into a menu.
 *  The reason is that it needs a neuron to act on.
 *  Typically this is done by calling updateState with a mapEvent as input.
 *
 *  @author Matthias Palm�r
 *  @version $Revision$
 */
public class SurfAlterationTool extends MapMenuTool
{
    JMenu choice;

    PopupLayer popup;
  /** Constructs an SurfMapTool.
   */
  public SurfAlterationTool(MapController cont, PopupLayer popup)
  {
    super("SURF", BrowseMapManagerFactory.class.getName(), cont);
    choice=new JMenu();
    this.popup = popup;
  }

  public void update(MapEvent e)
  {
      super.update(e);
      
      choice.removeAll();
      
      //Should-not-occur case
      if (mapEvent==null)
	{
	    JMenuItem mi = new JMenuItem();
	    setJMenuItem(mi);
	    mi.setEnabled(false);
	}

      ConceptMap cMap=controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();

      //Pick out map or neuroncomponent
      AbstractAction action=null;
      Component component;
      String detailedMap=null;

      if (mapEvent.mapObject == null || mapEvent.mapObject.getNeuron()==null)
	  component=cMap;
      else
	  {
	      component=mapEvent.mapObject.getNeuron();
	      if (mapEvent.mapObject.getNeuronStyle().getDetailedMap() !=null)
		  {
		      URI cmURI=URIClassifier.parseValidURI(mapEvent.mapObject.getNeuronStyle().getDetailedMap(),
							    cMap.getURI());
		      detailedMap=cmURI.toString();
		      
		      String title=null;
		      try {
			  ConceptMap cm=controller.getConzillaKit().getComponentStore().getAndReferenceConceptMap(cmURI);
			  
			  MetaData md=cm.getMetaData();
			  
			  title = MetaDataUtils.getLocalizedString(md.get_metametadata_language(), md.get_general_title()).string;
			  if(title.length() == 0)
			      title = detailedMap;
					      
		      } catch (ComponentException ce)
			  {
			      title="detailedmap";
			  }
		      
		      
		      action=new AbstractAction(title) {
			      public void actionPerformed(ActionEvent e)
			      {
				  surfDetailedMap();
			      }
			  };
		  }
	  }

      //Pick out relationset for component
      RelationSet relset=null;
      try {
	ComponentStore cstore=controller.getConzillaKit().getComponentStore();
	 relset = new RelationSet(URIClassifier.parseURI(component.getURI()), "context", cstore);
      } catch (Exception ex) 
	{
	  Tracer.debug("ViewMapMenuTool: Failed looking up context."+ex.getMessage());
	}

      ConzillaResourceManager manager = ConzillaResourceManager.getDefaultManager();

      //If not several alternatives (detailedmap plus contextmaps)
      //then return a menuItem (possible not enabled if no alternative)
      // otherwise a menu is returned.
      if ( relset==null  || relset.getRelations().length == 0 ||
	   (relset.getRelations().length == 1 && 
	    relset.getRelations()[0].getURI().equals(cMap.getURI())))
	{
	  JMenuItem mi=new JMenuItem();
	  if (action!=null)
	    {
	      mi.addActionListener(action);
	      mi.setEnabled(true);
	    }
	  else
	    mi.setEnabled(false);
	  setJMenuItem(mi);
	}
      else
	{	  
	  JLabel label=new JLabel(manager.getString(BrowseMapManagerFactory.class.getName(), "NEIGHBORHOOD"));
	  label.setPreferredSize(new java.awt.Dimension(40, 15));
	  choice.add(label);
	  
	  boolean detailedMapIsThere=false;

	  Component [] comps=relset.getRelations();
	  for (int i=0; i< comps.length;i++)
	    {
	      if (comps[i].getURI().equals(cMap.getURI()))
		continue;
	      MetaData md=comps[i].getMetaData();

	      String title = MetaDataUtils.getLocalizedString(md.get_metametadata_language(), md.get_general_title()).string;
	      if(title.length() == 0)
		  title = comps[i].getURI();
	      
	      if (detailedMap!=null && detailedMap.equals(comps[i].getURI()))
		  {
		      detailedMapIsThere=true;
		      title="--> "+title;
		  }
	      
	      final Component comp = comps[i];
	      AbstractAction relAction=new AbstractAction(title) {
		public void actionPerformed(ActionEvent e)
		  {
		    surfContextMap(comp);
		  }
	      };
	      choice.add(relAction);
	    }
	  if (!detailedMapIsThere && action!=null)
	      choice.add(action);
	  setJMenuItem(choice);
	}
    }


  /** This is a surf-command that results in a zoomIn via the controller.
   *  Observe that updateState has to have been succesfully last time called.
   *  Otherwise the surf-action isn't activated and this function isn't called.
   *
   *  @see Controller.zoomIn()
   */
  public void surfDetailedMap()
    {
      if (mapEvent.mapObject.getNeuronStyle()==null)
	return;
      try {
	NeuronStyle ns = mapEvent.mapObject.getNeuronStyle();
	ConceptMap cMap = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
	controller.showMap(URIClassifier.parseValidURI(mapEvent.mapObject.getNeuronStyle().getDetailedMap(),
						       cMap.getURI()));	
	controller.getHistoryManager().fireDetailedMapEvent(controller, ns);
	popup.markLastNeuron(controller.getMapScrollPane(), URIClassifier.parseValidURI(mapEvent.mapObject.getNeuronStyle().getNeuronURI(), mapEvent.mapObject.getNeuronStyle().getConceptMap().getURI()).toString());
      }
      catch(ControllerException e)
	{
	  ErrorMessage.showError("Load Error",
				 "Failed to load map\n\n"
				 + mapEvent.mapObject.getNeuronStyle().getDetailedMap(),
				 e,
				 controller.getMapScrollPane());
	}
    }
  public void surfContextMap(Component comp)
  {
    ConceptMap oldMap = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
    try {
      controller.showMap(URIClassifier.parseValidURI(comp.getURI()));
      controller.getHistoryManager().fireOpenNewMapEvent(controller, oldMap, URIClassifier.parseValidURI(comp.getURI()));
      popup.markLastNeuron(controller.getMapScrollPane(), URIClassifier.parseValidURI(mapEvent.mapObject.getNeuronStyle().getNeuronURI(), mapEvent.mapObject.getNeuronStyle().getConceptMap().getURI()).toString());
    }
    catch(ControllerException e)
      {
	ErrorMessage.showError("Load Error",
			       "Failed to load map\n\n" + comp.getURI(),
			       e,
			       controller.getMapScrollPane());
      }
  }    
}