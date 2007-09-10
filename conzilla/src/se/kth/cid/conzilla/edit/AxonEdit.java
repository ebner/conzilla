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
import se.kth.cid.conzilla.edit.layers.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.tool.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AxonEdit extends AbstractTool implements  MapEventListener
{

  protected MapController controller;
  protected Edit  edit;
  protected MapEvent mapEvent;
  protected MapEvent secondMapEvent;
  protected Hashtable nmoToAxons;
  protected boolean createMode;
  protected JPopupMenu menu;

  ToolSet toolSet;
    
  public AxonEdit(String name, MapController controller, Edit edit)
  {
    super(name,Tool.EXCLUSIVE);
    this.controller=controller;
    this.edit=edit;
    
    toolSet = new ToolSet();
    toolSet.addTool(edit);
    toolSet.addTool(this);

    createMode = false;
  }

  public void createAxon(MapEvent me)
    {
      createMode = true;
      update(me);
      activate();	
    }
    
  public void showAxon(MapEvent mapEvent)
    {
	createMode = false;
	update(mapEvent);
	activate();
	highlightChoices();
    }
  
  public void update(Object o)
    {
	if ((o instanceof MapEvent))
	{
	    mapEvent=(MapEvent) o;
	    return;
	}
	mapEvent= null;
    }
    
  protected void activateImpl()
  {
    if (mapEvent == null)
	Tracer.bug("Should never activate AxonEdit if not updated sucessfully"+
		   "before from an MapEvent");
    MapDisplayer mapDisplayer=controller.getMapScrollPane().getDisplayer();
    mapDisplayer.addMapEventListener(this,MapDisplayer.CLICK);
  }
  
  protected void deactivateImpl()
  {
      Tracer.debug("deactivate!!!!!!!!!!!!!!!!!!!!!!!");
    MapDisplayer mapDisplayer=controller.getMapScrollPane().getDisplayer();
    mapDisplayer.removeMapEventListener(this,MapDisplayer.CLICK);

  }
  
  


  protected void highlightChoices()
    {
	nmoToAxons=getNotConnectedToNeuronStyle(mapEvent.mapObject.getNeuron(), 
						mapEvent.mapObject.getNeuronStyle(), 
						controller.getMapScrollPane().getDisplayer());
	Enumeration en=nmoToAxons.keys();
	for (;en.hasMoreElements();)
	    ((NeuronMapObject) en.nextElement()).pushMark(new Mark(ColorManager.EDIT_CHOICES, null, null), this);
    }
    
  static public Hashtable getNotConnectedToNeuronStyle(Neuron ne, NeuronStyle ns, MapDisplayer mapDisplayer)
    {
	Hashtable nmoToAxons=new Hashtable();
	
	String baseuri1=ne.getURI();
	NeuronStyle [] nss=mapDisplayer.getStoreManager().getConceptMap().getNeuronStyles();
	Axon [] as=ne.getAxons();
	for (int i=0; i<as.length;i++)
	  if (ns.getAxonStyle(as[i].getID()) == null) 
	    {
		URI uri1=URIClassifier.parseValidURI(as[i].getEndURI(),baseuri1);
			
		for (int j=0;j<nss.length;j++)
		    {
			String baseuri2 =  nss[j].getConceptMap().getURI();
			URI uri2=URIClassifier.parseValidURI(nss[j].getNeuronURI(), baseuri2);
			if (uri2.equals(uri1))
			    {
				NeuronMapObject nmo=mapDisplayer.getNeuronMapObject(nss[j].getID());
				if (nmoToAxons.get(nmo) == null)
				    {
					Vector nv=new Vector();
					nv.addElement(as[i]);
					nmoToAxons.put(nmo,nv);
				    }
				else
				    {
					Vector nv = (Vector) nmoToAxons.get(nmo);
					nv.addElement(as[i]);
				    }
			    }
		    }
	    }
	return nmoToAxons;
    }
 protected void highlightNone()
    {
	Enumeration en=nmoToAxons.keys();
	for (;en.hasMoreElements();)
	    ((NeuronMapObject) en.nextElement()).popMark(this);
    }

 public void eventTriggered(MapEvent m)
  {
      if (m.hitType == MapEvent.HIT_NONE)
	  {
	      done();
	      return;
	  }
      secondMapEvent=m;
      
      if (createMode)
	  create();
      else
	  show();
  }

  public void done()
      {
	  if (!createMode)
	      highlightNone();
	  edit.activate();
      }

  protected void create()
    {
	Tracer.debug("inside create...............1");
	if (secondMapEvent.mapObject.getNeuron()==null)
	    return;
	Tracer.debug("inside create...............2");
	menu=new JPopupMenu("axontypes");
	AxonType [] at = mapEvent.mapObject.getNeuronType().getAxonTypes();
	if (at.length==1)
	    createAxonWithType(at[0].getType());
	else
	    {
		for (int i=0; i<at.length; i++)
		    {
			AbstractAction aa=new AbstractAction(at[i].getType()) {
				public void actionPerformed(ActionEvent e)
				{
				    createAxonWithType((String) getValue("type"));
				}
			    };
			aa.putValue("type", at[i].getType());
			Tracer.debug("inside create...............2.5");
			menu.add(aa);
		    }
		Tracer.debug("inside create...............3");

		menu.show(controller.getMapScrollPane().getDisplayer(), 
			  secondMapEvent.mouseEvent.getX(),
			  secondMapEvent.mouseEvent.getY());
	    }
    }

  public void createAxonWithType(String type)
    {
	try {

	URI base = URIClassifier.parseValidURI(mapEvent.mapObject.getNeuron().getURI());
	URI absoluteURI=URIClassifier.parseValidURI(secondMapEvent.mapObject.getNeuron().getURI());
	String relativeURI;
	try {
	    relativeURI=base.makeRelative(absoluteURI, false);
	} catch (MalformedURIException me)
	    {
		relativeURI=secondMapEvent.mapObject.getNeuron().getURI();
	    }
	Axon axon=mapEvent.mapObject.getNeuron().addAxon(type, relativeURI);
	addAxonStyle(axon);
	} catch (NeuronException ne){
	} catch (InvalidURIException ie) {}
    }
			    
  protected void show()
    {
      Vector nv=(Vector) nmoToAxons.get(secondMapEvent.mapObject);
      if (nv != null)
	  {
	      if (nv.size()== 1)
		  addAxonStyle((Axon) nv.elementAt(0));
	      else
		  {
		      menu=new JPopupMenu("axons");
		      Enumeration en= nv.elements();
		      for (;en.hasMoreElements();)
			  {
			      Axon ax=(Axon) en.nextElement();
			      String name=ax.getID()+":"+ax.getType();
			      AbstractAction aa=new AbstractAction(name) {
				  public void actionPerformed(ActionEvent e)
				      {
					  addAxonStyle((Axon) getValue("axon"));
				      }
			      };
			      aa.putValue("axon", ax);
			      menu.add(aa);
			  }
		      menu.show(controller.getMapScrollPane().getDisplayer(), 
				secondMapEvent.mouseEvent.getX(),
				secondMapEvent.mouseEvent.getY());

		  }
	  }
      else
	  done();
  }

  protected void addAxonStyle(Axon axon)
    {
	addAxonStyle(axon, mapEvent.mapObject.getNeuronStyle(), secondMapEvent.mapObject.getNeuronStyle(), 
		     new ConceptMap.Position(secondMapEvent.mapX, secondMapEvent.mapY), ((EditMapManager) controller.getManager()).getGridModel());
    }

  static public void addAxonStyle(Axon axon, NeuronStyle owner, NeuronStyle otherEnd, ConceptMap.Position click, GridModel gm)
    {		 
	try {
	    ConceptMap.Position[] pos=new ConceptMap.Position[2];
	    pos=LayoutUtils.axonLine(owner, otherEnd, click, gm);
	    
	    AxonStyle axonStyle=owner.addAxonStyle(axon.getID(), otherEnd);
	    axonStyle.setLine(pos);
	    
	    //	    done();
	} catch (ConceptMapException ce)
	    {
		Tracer.bug("Can't show one of the selected axons.....");
	    }
    }

  protected void detachImpl()
  {
    controller=null;
    mapEvent = null;
    secondMapEvent = null;
  }
}
