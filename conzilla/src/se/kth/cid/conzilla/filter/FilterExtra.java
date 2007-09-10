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


package se.kth.cid.conzilla.filter;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conceptmap.ConceptMap;
import se.kth.cid.neuron.Neuron;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.browse.ViewAlterationTool;
import javax.swing.JMenu;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

/** 
 *  @author Matthias Palmer.
 */
public class FilterExtra implements Extra
{
    FilterFactory filterFactory;

    public FilterExtra()
    {
	filterFactory = new SimpleFilterFactory();
    }
    public boolean initExtra(ConzillaKit kit) 
    {
	return true;
    }

    public String getName() {
	return "filter";
    }
    public void refreshExtra() 
    {
	filterFactory.refresh();
    }
    public boolean saveExtra() {return true;}
    public void exitExtra() {}

    public void extendMenu(ToolsMenu menu, MapController c)
    {
    }


    public void addExtraFeatures(final MapController c, final Object o, 
				 String location, String hint)
    {
	if (location.equals("viewalterationtool"))
	    {
		Object[] arr = (Object[]) o;
		ViewAlterationTool va = (ViewAlterationTool) arr[0];
		JMenu menu = (JMenu) arr[1];
		Neuron neuron = (Neuron) arr[2];
		ConceptMap cMap = (ConceptMap) arr[3];
		
		Filter filter=filterFactory.createFilter(c, neuron, cMap);
		if (filter == null)
		    return;
		filter.getFilterNode().filterThrough(neuron);
		FilterNode node = filter.getFilterNode();
		ContentSelector sel=c.getContentSelector();
		recursivelyBuildMenu(va, menu, node, neuron, c);
	    }
    }

 protected void recursivelyBuildMenu(final ViewAlterationTool va, 
					JMenu menu, 
					FilterNode node, 
					Neuron neuron,
				     MapController controller)					
  {
    List contents = node.getContent(neuron);
    if (contents.isEmpty())
	return;
    
    FilterNode subnode;
    
    for (int i=0; i < node.numOfRefines(); i++)
	{    
	    subnode = node.getRefine(i);
	    
	    if (subnode.numOfRefines()==0)
		{
		    FilterAction filteraction = new FilterAction(subnode) {
			    public void actionPerformed(ActionEvent ae)
			    {
				va.show(this.node.getContent(component),null ,this.node);
			    }
			};
		    filteraction.setComponent(neuron);
		    JMenuItem menuItem=menu.add(filteraction);
		    String toolTipText=subnode.getToolTipText();
		    if (toolTipText!=null)
			menuItem.setToolTipText(toolTipText);		 
		}
	    else
		{
		    JMenu submenu = new JMenu(subnode.getFilterTag());
		    recursivelyBuildMenu(va, submenu, subnode, neuron, controller);
		    JMenuItem menuItem=menu.add(submenu);
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
		va.show(this.node.getContentPassedRefines(component), 
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
		
		va.show(other,"Other", this.node);
	    }
	};
    menuOther.setComponent(neuron);

    menu.addSeparator();
    ConzillaResourceManager.getDefaultManager().customizeButton(menu.add(menuAny), FilterExtra.class.getName(),
								"VIEW_ANY");
    ConzillaResourceManager.getDefaultManager().customizeButton(menu.add(menuOther), FilterExtra.class.getName(),
								"VIEW_OTHER");
    menu.addSeparator();

    ViewAlterationTool vat=new ViewAlterationTool("VIEW_FILTER", FilterExtra.class.getName(), controller);
    JMenuItem jmi=vat.getMenuItemForNeuron(node.getFilterNeuron(), null);
    if (jmi!=null)
	ConzillaResourceManager.getDefaultManager().customizeButton(menu.add(jmi), FilterExtra.class.getName(), 
								    "VIEW_VIEW_FILTER");
    
    }
}
