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


package se.kth.cid.conzilla.layout;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.Neuron;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.edit.LayoutUtils;
import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.menu.DefaultMenuFactory;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.browse.ViewAlterationTool;
import javax.swing.JMenu;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

/** Proof of concept layout support, star-shaped box layout
 *  with order depending on number of relations attached.
 *
 *  @author Matthias Palmer.
 */
public class LayoutExtra implements Extra, Layout
{
    GridModel gridModel;

    public LayoutExtra()
    {
    }

    public boolean initExtra(ConzillaKit kit) 
    {
	gridModel = new GridModel(6);
	return true;
    }

    public String getName() {
	return "layout";
    }
    public void refreshExtra(){}
    public void showExtra() {}
    public void closeExtra() {}
    public boolean saveExtra() {return true;}
    public void exitExtra() {}

    public void addExtraFeatures(final MapController c, final Object o, String location, String hint) {}

    public void extendMenu(ToolsMenu menu, final MapController mc)
    {
	if(menu.getName().equals(DefaultMenuFactory.TOOLS_MENU))
	    menu.addTool(new Tool("LAYOUT_TRIVIAL", LayoutExtra.class.getName())
		{
		    public void actionPerformed(ActionEvent ae)
		    {
			Tracer.debug("Layouts the current map.");
			layout(mc);
		    }
		}, 200);
    }

  public void layout(MapController controller)
    {
	layout(controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap());
    }

  public void layout(ConceptMap cMap)
  {
      NeuronStyle [] nss = cMap.getNeuronStyles();
      Collection vnss1 = new Vector();
      Collection vnss2 = new Vector();
      separate(nss, vnss1, vnss2);
      vnss2 = sortRelations(vnss2);
      vnss1 = sortBoxes(vnss1, vnss2);
      

      ConceptMap.Dimension size = cMap.getDimension();
      int radius = size.width > size.height ? size.height : size.width;
      radius = (int) (radius*0.5 -50);

      Iterator it = vnss1.iterator();
      int nr = vnss1.size();
      for (int i = 0; i< nr; i++)
	  {
	      NeuronStyle ns = (NeuronStyle) it.next();
	      double v = 2*Math.PI/nr * i;
	      int xpos = (int) (size.width/2 + radius*Math.cos(v));
	      int ypos = (int) (size.height/2 + radius*Math.sin(v));
	      ConceptMap.Position p = new ConceptMap.Position(xpos, ypos);
	      ConceptMap.BoundingBox bb = ns.getBoundingBox();
	      ConceptMap.BoundingBox newbb = new ConceptMap.BoundingBox(bb.dim, p);
	      ns.setBoundingBox(newbb);
	  }
      Vector all = new Vector();
      all.addAll(vnss1);
      all.addAll(vnss2);
      it = all.iterator();
      while (it.hasNext())
	  {
	      NeuronStyle ns1 = (NeuronStyle) it.next();
	      AxonStyle [] ass = ns1.getAxonStyles();
	      ConceptMap.Position middle = LayoutUtils.findMiddleOfNeuronStyles_RegardingBody(ns1, vnss1, gridModel);
	      for (int i = 0; i<ass.length;i++)
		  {
		      NeuronStyle ns2 = ass[i].getEnd();
		      ConceptMap.Position [] points = new ConceptMap.Position[2];
		      points[0] = middle;
		      points[1] = LayoutUtils.findPosition_FirstFromBody(ns2, middle, gridModel); 
		      //		      Tracer.debug("point0 = "+middle);
		      //		      Tracer.debug("point1 = "+points[1]);
		      ass[i].setLine(points);
		  }
	  }   
  }
  public void resizeBoxes(ConceptMap cMap, MapController controller)
    {
      NeuronStyle [] nss = cMap.getNeuronStyles();
      Collection vnss1 = new Vector();
      Collection vnss2 = new Vector();
      separate(nss, vnss1, vnss2);
      
      Iterator it = vnss1.iterator();
      while (it.hasNext())
	  {
	      NeuronStyle ns = (NeuronStyle) it.next();
	      java.awt.Dimension dim=controller.getMapScrollPane().getDisplayer().getNeuronMapObject(ns.getID()).getPreferredSize();
	      ConceptMap.BoundingBox bb = ns.getBoundingBox();
	      ns.setBoundingBox(LayoutUtils.preferredBoxOnGrid(gridModel, 
							       bb.pos.x,
							       bb.pos.y,
							       dim));

	  }
    }

  private void separate(NeuronStyle [] nss, Collection vnss1, Collection vnss2)
  {
    for (int i=0; i<nss.length;i++)
      {
	  NeuronStyle ns = nss[i];
	  if (ns.getBodyVisible())
	      vnss1.add(ns);
	  else
	      vnss2.add(ns);
      }
  }
    private Collection sortBoxes(Collection v1, Collection v2)
    {
	Collection re = new Vector();
	Iterator it = v2.iterator();
	while (it.hasNext())
	    {
		AxonStyle [] ass = ((NeuronStyle) it.next()).getAxonStyles();
		for (int i=0;i<ass.length;i++)
		    if (v1.remove(ass[i].getEnd()))
			re.add(ass[i].getEnd());
	    }
	re.addAll(v1);
	return re;
    }
    private Collection sortRelations(Collection v)
    {
	Tracer.debug("number of relations is "+v.size());
	Hashtable check = new Hashtable();
	Vector ret = new Vector();
	Iterator it = v.iterator();
	while (it.hasNext())
	    {
		NeuronStyle ns = (NeuronStyle) it.next();
		Integer current = new Integer(ns.getAxonStyles().length);
		check.put(ns, current);
		
		int pos = 0;
		boolean dirty = true;
		while (dirty)
		    {
			if (ret.size()<=pos)
			  {
			      dirty=false;
			      continue;
			  }
			NeuronStyle temp = (NeuronStyle) ret.elementAt(pos);
			if (((Integer) check.get(temp)).compareTo(current)<0)
			    dirty = false;
			else
			    pos++;
		    }
		ret.insertElementAt(ns, pos);
	    }
	Tracer.debug("number of relations is "+ret.size());	
	return ret;
    }
}
