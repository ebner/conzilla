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
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.identity.URIClassifier;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.filter.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conceptmap.ConceptMap;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;

/** This is a view-command-tool that have to be embedded into a menu.
 *  The reason is that it needs a neuron to act on.
 *  Typically this is done by calling updateState with a mapEvent as input.
 *
 *  @author Matthias Palmèr
 *  @version $Revision$
 */
public class ViewAlterationTool extends AbstractTool implements AlterationTool
{

  JMenu choice;
  MapEvent mapEvent;
  MapController controller;
  Filter filter;
  /** Constructs an ViewAlterationTool.
   */
  public ViewAlterationTool(String name, MapController cont)
  {
    super(name, Tool.ACTION);
    controller=cont;
    choice=new JMenu(name);  
  }

  public void update(Object o)
  {
    if ( o != null && o instanceof MapEvent)
      mapEvent=(MapEvent) o;
  }

  public JMenuItem getMenuItem()
  {
      if (mapEvent==null || mapEvent.mapObject == null || mapEvent.mapObject.getNeuron()==null)      //Should-not-occur case
	  {
	      choice.removeAll();
	      JMenuItem mi=new JMenuItem(getName());
	      mi.setEnabled(false);
	      return mi;
	  }
      else
	  return getMenuItemForNeuron(mapEvent.mapObject.getNeuron(),
				      controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap());
  }
    
  public JMenuItem getMenuItemForNeuron(Neuron neuron, ConceptMap cMap)
    {
	choice.removeAll();
	
	if (neuron != null)
	    {
        filter=controller.getConzillaKit().getFilterFactory().createFilter(controller, neuron, cMap);
	if (filter!=null ) //&& filter.getFilterNode().getContent(neuron).size()!=0)
	  {

	      /*	    AbstractAction act=new AbstractAction("all") {
	      public void actionPerformed(ActionEvent ae)
		{
		  ViewAlterationTool.this.show((Component []) getValue("content"));
		}
	    };
	    ComponentStore cstore=controller.getConzillaKit().getComponentStore();
	    RelationSet relset = new RelationSet(neuron, "content", cstore);
	    act.putValue("content", relset.getRelations());
	    choice.add(act);
	   
	    JMenu submenu = new JMenu(subnode.getFilterTag());
	    menu.add(submenu);
	      */
	      
	    filter.getFilterNode().filterThrough(neuron);
	    recursivelyBuildMenu(choice, filter.getFilterNode(), neuron);
	    return choice;
	  }
	//	else
	//	  Tracer.debug("found no filter!");
      }

    //conceptmap filterneuron....

    JMenuItem mi=new JMenuItem(getName());  
    if (neuron != null)
      {
	AbstractAction act=new AbstractAction() {
	  public void actionPerformed(ActionEvent ae)
	    {
		ViewAlterationTool.this.show((Component []) getValue("content"), "all", null);
	    }
	};
	
	ComponentStore cstore=controller.getConzillaKit().getComponentStore();
	RelationSet relset = new RelationSet(neuron, "content", cstore);
	act.putValue("content", relset.getRelations());
	
	mi.setEnabled(relset.getRelations().length!=0);	  
	mi.addActionListener(act);
      }
    else
      mi.setEnabled(false);
    
    return mi;
  }

  protected void recursivelyBuildMenu(JMenu menu, FilterNode node, Neuron neuron)
  {

    MenuManager menuManager = PropertiesManager.getDefaultPropertiesManager().getMenuManager();

    List contents = node.getContent(neuron);

    if (contents.isEmpty())
      {
	menu.setEnabled(false);
	return;
      }
    else
	menu.setEnabled(true);

    FilterNode subnode;

    for (int i=0; i < node.numOfRefines(); i++)
     {    
	 subnode = node.getRefine(i);

	 if (subnode.numOfRefines()==0)
	     {
		 FilterAction filteraction = new FilterAction(subnode) {
			 public void actionPerformed(ActionEvent ae)
			 {
			     ViewAlterationTool.this.show(this.node.getContent(component),null ,this.node);
			 }
		     };
		 filteraction.setComponent(neuron);
		 JMenuItem menuItem=menu.add(filteraction);
		 menuManager.customizeButton(menuItem);
		 String toolTipText=subnode.getToolTipText();
		 if (toolTipText!=null)
		     menuItem.setToolTipText(toolTipText);		 
	     }
	 else
	     {
		 JMenu submenu = new JMenu(subnode.getFilterTag());
		 recursivelyBuildMenu(submenu, subnode, neuron);
		 JMenuItem menuItem=menu.add(submenu);
		 menuManager.customizeButton(menuItem);
		 String toolTipText=subnode.getToolTipText();
		 if (toolTipText!=null)
		     menuItem.setToolTipText(toolTipText);
	     }
     }

    FilterAction menuAny = new FilterAction(node, "Any") {
	    public boolean isEnabled()
	    {
		Set set = this.node.getContentPassedRefines(component);
		
		return set.size() > 0;
	    }

	    public void actionPerformed(ActionEvent ae)
	    {
		//		Tracer.debug("contentpassedRefines ="+this.node.getContentPassedRefines(component).size());
		ViewAlterationTool.this.show(this.node.getContentPassedRefines(component), 
					     "Any", this.node);
	    }
	};
    menuAny.setComponent(neuron);

    FilterAction menuOther = new FilterAction(node, "Other") {
	    public boolean isEnabled()
	    {
		Set set = this.node.getContentPassedRefines(component);
		List list = this.node.getContent(component);
		List other = new Vector(list);
		other.removeAll(set);

		return other.size() > 0;
	    }

	    public void actionPerformed(ActionEvent ae)
	    {
		//		Tracer.debug("contentpassedRefines ="+this.node.getContentPassedRefines(component).size());
		Set set = this.node.getContentPassedRefines(component);
		List list = this.node.getContent(component);
		List other = new Vector(list);
		other.removeAll(set);
		
		ViewAlterationTool.this.show(other,"Other", this.node);
	    }
	};
    menuOther.setComponent(neuron);

    menu.addSeparator();
    menuManager.customizeButton(menu.add(menuAny), "VIEW_ANY");
    menuManager.customizeButton(menu.add(menuOther), "VIEW_OTHER");
    menu.addSeparator();

    ViewAlterationTool vat=new ViewAlterationTool("View filter", controller);
    JMenuItem jmi=vat.getMenuItemForNeuron(node.getFilterNeuron(), null);
    if (jmi!=null)
    menuManager.customizeButton(menu.add(jmi), "VIEW_VIEW_FILTER");
  }

  /** This command results in a set of content is displayed.
   *  Observe that update has to have been succesfull last time called.
   *  Otherwise the view-action isn't activated and this function isn't called.
   *
   *  @see Controller.selectContent()
   */
  public void show(Object comps, String leaf, Object path)
    {
      final ContentSelector sel=controller.getContentSelector();
      Component [] vcomps=null;
      if (comps instanceof Collection)
	  {
	      vcomps = new Component[((Collection) comps).size()];
	      vcomps = (Component []) ((Collection) comps).toArray(vcomps);
	  }
      else if (comps instanceof Component[])
	vcomps = (Component []) comps;

      String [] strpath=null;
      if (path != null && path instanceof FilterNode)
	  {
	      Vector vpath=new Vector();
	      if (leaf != null)
		  vpath.addElement(leaf);
	      FilterNode node=(FilterNode) path;
	      do{
		  vpath.addElement(node.getFilterTag());
		  node=node.getTop();
	      } while (node!=null && node.getTop()!=null); //The top node isn't neccessary to show....
	      
	      strpath=new String[vpath.size()];
	      strpath=(String []) vpath.toArray(strpath);
	  }
      else if (leaf!=null)
	  {
	      strpath=new String[1];
	      strpath[0]=leaf;
	  }
      
      sel.selectContentFromSet(vcomps);
      sel.setContentPath(strpath);


      if (mapEvent!=null)
	  {
	      final NeuronMapObject nmo=mapEvent.mapObject.getNeuronMapObject();
	      nmo.pushMark(new Mark(ColorManager.MAP_CONTENT_FROM_BOX, null, null), this);
	      sel.addSelectionListener(ContentSelector.SELECTOR, new PropertyChangeListener() {
		      public void propertyChange(PropertyChangeEvent e)
		      {
			  sel.removeSelectionListener(this);
			  nmo.popMark(ViewAlterationTool.this);
		      }
		  });
	  }
    }
}
