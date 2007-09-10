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

package se.kth.cid.conzilla.edit.layers;
import se.kth.cid.conzilla.edit.layers.handles.*;
import se.kth.cid.util.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.map.layout.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.tool.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;


public class NeuronEdit implements MapEventListener    //, Destination
{
  private static final int NONE=0;
  private static final int SHOW_ROLE=1;
  private static final int ADD_ROLE=2;
  MapController controller;
  MapManager manager;

  ConceptMap conceptmap;
  NeuronStyle neuronstyle;
  NeuronLayer parent;
  //  CreateNeuronDialog cndialog;
  int state=SHOW_ROLE;
  
  public NeuronEdit(MapController controller,MapManager manager, NeuronLayer parent)
    {
      this.controller=controller;
      this.manager=manager;
      this.parent=parent;
      conceptmap=manager.getDisplayer().getMap();
      //A not very nice try to find the base-uri for the neurons in the conceptmap.
      //Needs some forma of standardization of directory structure....
      //Should maybee be separated into a separate configurable class.
      String uri=conceptmap.getURI().toString();
      int slashpos2=uri.lastIndexOf('/');
      int slashpos1=uri.substring(0,slashpos2-1).lastIndexOf('/');
      if (uri.substring(slashpos1+1,slashpos2).equals("conceptmap"))
	uri=uri.substring(0,slashpos1)+"/neuron/";
      else
	uri=uri.substring(0,slashpos2+1);
      //uri hopefully found.
      //cndialog=new CreateNeuronDialog(JOptionPane.getFrameForComponent(manager),
      //			      uri,(Destination) this,controller);
    }

  public void insertNeuron(String suri)
    {
      //    parent.setProcessingSubDecisions(true);
      try {
	URI uri=new URI(suri);
	Tracer.debug("URI constructed");
	conceptmap.addNeuronStyle(uri);
	Tracer.debug("added neuronstyle");
	NeuronStyle current=conceptmap.getNeuronStyle(uri);
	if (current!=null)
	  {
	    Tracer.debug("Setting bondingbox and title.");
	    MouseEvent me=parent.getMapEvent().mouseevent;
	    current.setBoundingBox(new Rectangle(me.getX()-30,me.getY()-15,60,30));

	    String title = current.getNeuron().getMetaData().getValue("Title");

	    if(title == null || title.equals(""))
	      title = "No title";
	  
	    current.setTitle(title);
	    addAllRoles(current);
	  }
      } catch (MalformedURIException e) {
	TextOptionPane.showError(parent,e.getMessage());
      } catch (ReadOnlyException e) {
	TextOptionPane.showError(parent,e.getMessage());      
      } catch (NeuronStyleException e) {
	TextOptionPane.showError(parent,e.getMessage());
      } catch (ComponentException e){
	TextOptionPane.showError(parent,e.getMessage());    
      }
      parent.reset(this);
    }
  
  public void pasteNeuron()
    {
      ClipboardLibrary cbl=(ClipboardLibrary) controller.getLibraryDisplayer().getLibrary().getLibrary("clipboardlibrary");
      if (cbl==null)
	return;
      NeuronStyle current=cbl.pasteNeuron(conceptmap);
      if (current!=null)
	{
	  Rectangle re=current.getBoundingBox();
	  MouseEvent me=parent.getMapEvent().mouseevent;
	  
	  if(re != null)
	    current.setBoundingBox(new Rectangle(me.getX()-30,me.getY()-15,re.width,re.height));
	  addAllRoles(current);
	  parent.reset(this);
	}
    }
  /*  public void createNeuron()
    {
      parent.setProcessingSubDecisions(this);
      controller.getLibraryDisplayer().showLibrary();
      controller.getLibraryDisplayer().getLibrary().registerDestination(this);
      cndialog.show();
    }
  public void createFromTemplate(NeuronStyle ns)
    {
      cndialog.setNeuronStyle(ns);
    }
  public void  adoptNeuron(NeuronStyle ns)
    {
      Rectangle bb=ns.getBoundingBox();
      MouseEvent me=parent.getMapEvent().mouseevent;
      bb.setLocation(me.getX(),me.getY());
      ns.connect(conceptmap);
      controller.getLibraryDisplayer().getLibrary().refreshTemplate();
      parent.reset(this);
    }
  public void abortTransfer()
    {
      controller.getLibraryDisplayer().getLibrary().refreshTemplate();
      cndialog.abortDialog();
      parent.reset(this);
      }
  */
  public void removeNeuron()
    {
    
      if (parent.getMapEvent().neuronstyle!=null)
	parent.getMapEvent().neuronstyle.disconnect();
      else
	parent.getMapEvent().rolestyle.getRoleOwner().disconnect();
    }
  public void addRole()
    {
      Tracer.debug("addRole");
      if (parent.getMapEvent().neuronstyle!=null)
	neuronstyle=parent.getMapEvent().neuronstyle;
      else
	neuronstyle=parent.getMapEvent().rolestyle.getRoleOwner();
      if (neuronstyle.getNeuron()!=null)
	{
	  state=ADD_ROLE;
	  neuronstyle.getNeuron().setEditable(true);   //This is ugly, should be done higher up and resetted to an original value afterwards.
	  parent.setProcessingSubDecisions(this);
	  Tracer.debug("ADD_ROLE as state.");
	  manager.getDisplayer().repaint();
	}
      else
	{
	  neuronstyle=null;
	  parent.reset(this);
	}	

    }
  public void removeRole()
    {
      RoleStyle rstyle;
      if ((rstyle=parent.getMapEvent().rolestyle)!=null)
	{
	  try {
	    Neuron neuron=rstyle.getRoleOwner().getNeuron();
	    rstyle.disconnect();
	    neuron.removeRole(rstyle.getRole());
	  } catch (ReadOnlyException e)
	    {
	      Tracer.trace(e.getMessage()+ " removeRole within NeuronEdit is confused since neuron"
			   +"is readonly, but that is NeuronLayers responsibility and should already"
			   +"been taken care of!", Tracer.BUG);
	    }
	}
    }
  
  
  
  public void toggleNeuronBox()
    {
      if (parent.getMapEvent().neuronstyle!=null)
	neuronstyle=parent.getMapEvent().neuronstyle;
      else
	neuronstyle=parent.getMapEvent().rolestyle.getRoleOwner();
      if (neuronstyle.getBoundingBox()==null)
        {
	  neuronstyle.setBoundingBox(Layout.getBoundingBox(neuronstyle,parent.getMapEvent()));
	  String title = neuronstyle.getNeuron().getMetaData().getValue("Title");
	    
	  if(title == null || title.equals(""))
	    title = "No title";
	    
	  neuronstyle.setTitle(title);
	}

      else
	{
	  if (subPartsRemovable(neuronstyle))  
	    neuronstyle.setBoundingBox(null);
	  else
	    return;
	}
      manager.getDisplayer().repaint();    
    }

  public void toggleNeuronLine()
    {
      Tracer.debug("toggleNeuronLine");
      if (parent.getMapEvent().neuronstyle!=null)
	neuronstyle=parent.getMapEvent().neuronstyle;
      else
	neuronstyle=parent.getMapEvent().rolestyle.getRoleOwner();
      if (neuronstyle.getLine().length==0)
	neuronstyle.setLine(Layout.getNeuronLine(neuronstyle,parent.getMapEvent()));
      else
	{
	  if (subPartsRemovable(neuronstyle))
	    neuronstyle.setLine(new Point[0]);
	  else
	    return;
	}
      manager.getDisplayer().repaint();
    }
  private boolean subPartsRemovable(NeuronStyle ns)
    {
      int count=0;
      if (ns.getBoundingBox()!=null)
	count++;
      if (ns.getLine().length!=0)
	count++;
      count+=ns.getRoles().size();
      if (count<2)
	{
	  if (JOptionPane.showConfirmDialog(null, 
					    "This neuron will be removed since invisible neurons can't be handled.",
					    "Neuron removal", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)
	    neuronstyle.disconnect();
	  return false;
	}
      return true;
    }

  public void addDMap()
    {
      if (parent.getMapEvent().neuronstyle!=null)
	neuronstyle=parent.getMapEvent().neuronstyle;
      else
	neuronstyle=parent.getMapEvent().rolestyle.getRoleOwner();

      ClipboardLibrary cl=(ClipboardLibrary) controller.getLibraryDisplayer().getLibrary().getLibrary("clipboardlibrary");
      if (cl!=null)
	{
	  URI uri=cl.getCurrentNeuronURI();
	  neuronstyle.setDetailedMap(uri);
	}
    }
  public void removeDMap()
    {
      if (parent.getMapEvent().neuronstyle!=null)
	neuronstyle=parent.getMapEvent().neuronstyle;
      else
	neuronstyle=parent.getMapEvent().rolestyle.getRoleOwner();
      neuronstyle.setDetailedMap(null);
    }
  
  public void showRole()
    {
      Tracer.debug("showRole");
      if (parent.getMapEvent().neuronstyle!=null)
	neuronstyle=parent.getMapEvent().neuronstyle;
      else
	neuronstyle=parent.getMapEvent().rolestyle.getRoleOwner();
      if (neuronstyle.getNeuron()!=null)
	{
	  state=SHOW_ROLE;
	  parent.setProcessingSubDecisions(this);
	  Tracer.debug("SHOW_ROLE as state.");
	  Enumeration en=getRelevantRoles(neuronstyle.getNeuron(),null,null);
	  for (;en.hasMoreElements();)
	    try {
	    NeuronStyle ns=conceptmap.getNeuronStyle(new URI(((Role) en.nextElement()).neuronuri));
	    if (ns!=null)
	      ns.mark(Color.blue);
	  } catch (MalformedURIException e) {
	    TextOptionPane.showError(parent,e.getMessage());
	  }
	  manager.getDisplayer().repaint();
	}
      else
	{
	  neuronstyle=null;
	  parent.reset(this);
	}
    }

  private void showRoleAlternatives(MapEvent m)
    {
      Tracer.debug("showRoleAlternatives");
      if (m.hit!=MapEvent.HIT_NONE)
	{
	  boolean show;
	  JPopupMenu altern=new JPopupMenu();
	  NeuronStyle otherend=m.getNeuronStyle();
	  if (state==SHOW_ROLE)
	    {
	      Enumeration roles=getRelevantRoles(neuronstyle.getNeuron(),null,otherend.getURI());
	      for (;roles.hasMoreElements();)
		altern.add(new RoleAction((Role) roles.nextElement(),neuronstyle, otherend.getURI(),state));
	    }
	  else if(state==ADD_ROLE)
	    {
	      RoleType [] rtypes=neuronstyle.getNeuronType().getRoleTypes();
	      for (int i=0;i<rtypes.length;i++)
		altern.add(new RoleAction(new Role(rtypes[i].type,otherend.getURI().toString(),1,1),neuronstyle,otherend.getURI(),state));
	    }
	  
	  if (altern.getSubElements().length!=0)
	    altern.show(manager.getDisplayer(),m.mouseevent.getX(),m.mouseevent.getY());
	  if (state==SHOW_ROLE)
	    {
	      Enumeration en=getRelevantRoles(neuronstyle.getNeuron(),null,null);
	      for (;en.hasMoreElements();)
		try {
		  NeuronStyle ns=conceptmap.getNeuronStyle(new URI(((Role) en.nextElement()).neuronuri));
		  if (ns!=null)
		    ns.mark(null);
		} catch (MalformedURIException e) {
		  TextOptionPane.showError(parent,e.getMessage());
		}
	    }
	  state=NONE;
	  parent.reset(this);
	  manager.getDisplayer().repaint();
	}
    }
  
  private class RoleAction extends AbstractAction
    {
      Role role;
      URI neuronuri;
      NeuronStyle neuronstyle;
      int state;
    
      public RoleAction(Role role,NeuronStyle neuronstyle, URI neuronuri, int state)
	{
	  super(role.type);
	  this.role=role;
	  this.neuronuri=neuronuri;
	  this.neuronstyle=neuronstyle;
	  this.state=state;
	}
      public void actionPerformed(ActionEvent ae)
	{
	  try {
	    if (state==ADD_ROLE)
	      {
		Role[] roles=neuronstyle.getNeuron().getRolesOfType(role.type);
		boolean result=true;
		for (int i=0; i<roles.length;i++)
		  if (role.equals(roles[i]))
		    result=false;
		if (result)
		  neuronstyle.getNeuron().addRole(role);
	      }
	    RoleStyle rolestyle=neuronstyle.addRoleStyle(role.type,
							 neuronuri);
	    connectVisual(rolestyle); 
	  } catch (RoleStyleException e) {
	    TextOptionPane.showError(parent,e.getMessage());
	  } catch (NeuronException e){
	    TextOptionPane.showError(parent,e.getMessage());
	  }
	}
    }

  private Enumeration getRelevantRoles(Neuron neuron, String type, URI otherend)
    {
      Vector retroles=new Vector();
      String [] types=neuron.getRoleTypes();
      for (int i=0;i<types.length;i++)
	{
	  if (type==null || type.equals(types[i]))
	    {
	      Role [] roles=neuron.getRolesOfType(types[i]);
	      for (int j=0;j<roles.length;j++)
		{
		  if (otherend==null || roles[j].neuronuri.equals(otherend.toString()))
		    {
		      retroles.addElement(roles[j]);  
		    }
		}
	    }
	}
      return retroles.elements();
    }

  public void hideRole()
    {
      if (subPartsRemovable(parent.getMapEvent().rolestyle.getRoleOwner()))
	parent.getMapEvent().rolestyle.disconnect();
      parent.reset(this);
    }

  protected void addAllRoles(NeuronStyle added)
    {
      Enumeration en=conceptmap.getNeuronStyles();
      for (;en.hasMoreElements();)
	addRolesBetween((NeuronStyle) en.nextElement(),added);
    }
  
  protected void addRolesBetween(NeuronStyle start, NeuronStyle end)
    {
      Neuron neuron=start.getNeuron();
      if (neuron!=null)
	{
	  String[] roletypes=	neuron.getRoleTypes();
	  for (int i=0;i<roletypes.length;i++)
	    {
	      Role [] roles= neuron.getRolesOfType(roletypes[i]);
	      for (int j=0;j<roles.length;j++)
		{
		  try{
		    URI neuronuri=new URI(roles[j].neuronuri);
		    NeuronStyle otherend=conceptmap.getNeuronStyle(neuronuri);
		    /*		Tracer.debug("otherend= "+otherend.getURI().toString());
				Tracer.debug("start= "+start.getURI().toString());
				Tracer.debug("end= "+end.getURI().toString());*/
		    if ( (otherend==end) || (otherend!=null && end==start) )
		      {
			RoleStyle rolestyle=start.addRoleStyle(roletypes[i],neuronuri);
			if (!connectVisual(rolestyle))
			  {
			    rolestyle.disconnect();
			    Tracer.debug("Couldn't connect neuron "+start.getURI()+
					 "with "+otherend.getURI()+"The first one is"+
					 " probably not visible!");
			  }
		      }
		  } catch (MalformedURIException e) {
		    TextOptionPane.showError(parent,"Wrong URI in role of type "
					     +roletypes[i]+" to neuron "+roles[j].neuronuri+
					     " inside neuron "+start.getURI().toString());
		  } catch (RoleStyleException e) {
		    TextOptionPane.showError(parent,e.getMessage());
		  }
		}
	    }
	}
    }
  private boolean connectVisual(RoleStyle rolestyle)
    {
      Point [] line=Layout.getRoleLineBetween(rolestyle.getRoleOwner(),rolestyle.getRolePlayer());
      if (line==null)
	return false;
      rolestyle.setLine(line);
      return true;
    }
  public void eventTriggered(MapEvent m)
    {
      m.consume();
      switch (state)
	{
	case ADD_ROLE:
	case SHOW_ROLE:
	  if (m.mouseevent.getID()==MouseEvent.MOUSE_PRESSED)
	    showRoleAlternatives(m);
	  break;
	}
    }
}


