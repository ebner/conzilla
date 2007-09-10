/* $Id$:*/
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
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.util.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.browse.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;


public class NeuronLayer extends LayerComponent implements MapEventListener, EditListener
{
  MapController controller;
  MapManager manager;
  JPopupMenu boxChoice, backgroundChoice, roleChoice;
  MapEvent   mapevent;
  MapEventListener neuronedit;
  MapEventListener roleedit;
  MapEventListener mapedit;
  ConceptMap conceptmap;
  Neuron     neuron;
  MapEventListener delegate;
  AbstractAction pasteneuron, insertneuron, removeneuron;
  AbstractAction addrole,removerole,showrole,hiderole;
  AbstractAction neuronbox,neuronline;
  AbstractAction adddetailedmap, removedetailedmap;
  StoreCommandTool store;
  InfoCommandTool info;
  ViewCommandTool view;
  TemplateCommandTool template;
  NeuronDisplayerCommandTool neurondisp;
  
  class RadioListenerModel implements ChangeListener
  { public void stateChanged(ChangeEvent e) {} }

  class MenuSwitch extends JRadioButton
  {
    public MenuSwitch(String str,RadioListenerModel rlm)
    {
      super(str);
      addChangeListener(rlm);
    }
  }

  public NeuronLayer(MapController controller,MapManager manager )
  {
    this.controller=controller;
    this.manager=manager;
    
    conceptmap=manager.getDisplayer().getMap();
    setSize(manager.getDisplayer().getMap().getBoundingBox());    
    neuron=null;
    
    neuronedit = new NeuronEdit(controller,manager, this);
    roleedit =   neuronedit;
    mapedit =    neuronedit;

    buildMenuItems();
    
    roleMenu();
    boxMenu();
    backgroundMenu();
    
    mapevent=null;
    delegate=null;

  }
  
  private void buildMenuItems()
  {
    pasteneuron=new AbstractAction("Insert from clipboard")
      {
        public void actionPerformed(ActionEvent ae)
	  {
	    Tracer.debug("Insert neuron from clipboard");
	    ((NeuronEdit) neuronedit).pasteNeuron();
	  }
      };
    insertneuron=new AbstractAction("Insert neuron with URI..")
      {
	String lastval;
	
        public void actionPerformed(ActionEvent ae)
        {
          Tracer.debug("Insert neuron with URI..");
	  String newval = (String) JOptionPane.showInputDialog(NeuronLayer.this, "Enter URI for neuron",
							       "New Neuron",
							       JOptionPane.QUESTION_MESSAGE,
							       null, null, lastval);
	  if(newval != null)
	    {
	      lastval = newval;
	      ((NeuronEdit) neuronedit).insertNeuron(lastval);
	    }
	}
      };
    removeneuron=new AbstractAction("Remove neuron from map")
      {
	public void actionPerformed(ActionEvent ae)
	{
	  Tracer.debug("Remove neuron from map");
	  ((NeuronEdit) neuronedit).removeNeuron();
	}
      };
    addrole=new AbstractAction("Add role to neuron")
      {
	public void actionPerformed(ActionEvent ae)
	{
	  Tracer.debug("Adds a role to the neuron");
	  ((NeuronEdit) neuronedit).addRole();
	}
      };
    removerole=new AbstractAction("Remove role from neuron")
      {
	public void actionPerformed(ActionEvent ae)
	{
	  Tracer.debug("Removes a role from the neuron");
	  ((NeuronEdit) neuronedit).removeRole();
	}
      };
    showrole=new AbstractAction("Show role in this map")
      {
	public void actionPerformed(ActionEvent ae)
	{
	  Tracer.debug("Show a graphical represantation of a role in this map");
	  ((NeuronEdit) neuronedit).showRole();
	}
      };
    hiderole=new AbstractAction("Remove role from this map")
      {
	public void actionPerformed(ActionEvent ae)
	{
	  Tracer.debug("Removes a role from the map");
	  ((NeuronEdit) neuronedit).hideRole();
	}
      };
    neuronbox=new AbstractAction("Toggle neuronbox visible")
	{
	    public void  actionPerformed(ActionEvent ae)
	{
	  Tracer.debug("Toggles neuronbox visible");
	  ((NeuronEdit) neuronedit).toggleNeuronBox();
	}
      };
    neuronline=new AbstractAction("Toggle neuronline visible")
	{
	    public void  actionPerformed(ActionEvent ae)
	{
	  Tracer.debug("Toggles neuronline visible");
	  ((NeuronEdit) neuronedit).toggleNeuronLine();
	}
      };
    adddetailedmap=new AbstractAction("Set conceptmap from clipboard as detailedmap here.")
	{
	  public void  actionPerformed(ActionEvent ae)
	    {
	      Tracer.debug("Set conceptmap from clipboard as detailedmap here.");
	      ((NeuronEdit) neuronedit).addDMap();
	    }
	};
    removedetailedmap=new AbstractAction("Removes detailedmap from this neuron.")
	{
	  public void  actionPerformed(ActionEvent ae)
	    {
	      Tracer.debug("Removes detailedmap from this neuron.");
	      ((NeuronEdit) neuronedit).removeDMap();
	    }
	};

    template=new TemplateCommandTool(manager,controller);
    neurondisp=new NeuronDisplayerCommandTool(manager, controller);
    info=new InfoCommandTool(manager,controller);
    store=new StoreCommandTool(manager,controller);
    view=new ViewCommandTool(manager,controller);
  }
  
  private void roleMenu()
    {
      roleChoice= new JPopupMenu();

      neurondisp.putActionInMenu(roleChoice);
      roleChoice.add(removeneuron);
      
      roleChoice.addSeparator();

      roleChoice.add(showrole);
      roleChoice.add(addrole);
      roleChoice.add(hiderole);
      roleChoice.add(removerole);

      roleChoice.addSeparator();
      
      roleChoice.add(neuronbox);
      roleChoice.add(neuronline);
      roleChoice.add(adddetailedmap);
      roleChoice.add(removedetailedmap);

      roleChoice.addSeparator();
      
      info.putActionInMenu(roleChoice);
      store.putActionInMenu(roleChoice);
      view.putActionInMenu(roleChoice);
    }

  private void boxMenu()
    {
      boxChoice= new JPopupMenu();
      
      neurondisp.putActionInMenu(boxChoice);
      boxChoice.add(removeneuron);

      boxChoice.addSeparator();

      boxChoice.add(showrole);
      boxChoice.add(addrole);

      boxChoice.addSeparator();
      
      roleChoice.add(neuronbox);
      roleChoice.add(neuronline);
      boxChoice.add(adddetailedmap);
      boxChoice.add(removedetailedmap);

      boxChoice.addSeparator();
      
      info.putActionInMenu(boxChoice);
      store.putActionInMenu(boxChoice);
      view.putActionInMenu(boxChoice);      
    }
  private void backgroundMenu()
    {
      backgroundChoice= new JPopupMenu();
      
      neurondisp.putActionInMenu(backgroundChoice);
      template.putActionInMenu(backgroundChoice);
      JMenu insn=new JMenu("Insert Neuron");
      insn.add(pasteneuron);
      insn.add(insertneuron);
      backgroundChoice.add(insn);
      
      backgroundChoice.addSeparator();
      
      info.putActionInMenu(backgroundChoice);
      store.putActionInMenu(backgroundChoice);
    }

  public void activate()
    {
      Tracer.debug("Activates NeuronLayer!!!!");
    // manager.getDisplayer().addMapEventListener(this,Manager.GetDisplayer().MOVE_DRAGG);
    manager.getDisplayer().addMapEventListener(this,MapDisplayer.PRESS_RELEASE);
    manager.getDisplayer().addMapEventListener(this,MapDisplayer.CLICK);
    info.activate();
    store.activate();
    view.activate();
    template.activate();
    neurondisp.activate();
    }
  public void deActivate()
  {
    // manager.getDisplayer().removeMapEventListener(this,Manager.GetDisplayer().MOVE_DRAGG);
    manager.getDisplayer().removeMapEventListener(this,MapDisplayer.PRESS_RELEASE);
    manager.getDisplayer().removeMapEventListener(this,MapDisplayer.CLICK);
    info.deactivate();
    store.deactivate();
    view.deactivate();
    template.deactivate();
    neurondisp.deactivate();
  }  

  public void eventTriggered(MapEvent m)
  {
    if (delegate!=null)
	delegate.eventTriggered(m);
    else if (m.mouseevent.isPopupTrigger())
	popup(m);
  }

  protected void popup(MapEvent m)
  {
    mapevent=m;
    store.updateState(m);
    info.updateState(m);
    view.updateState(m);
    template.updateState(m);
    neurondisp.updateState(m);
    m.consume();
    switch (m.hit)
      {
      case MapEvent.HIT_BOX:
      case MapEvent.HIT_TITLE:
      case MapEvent.HIT_DATA:
	if(setNeuron(m.neuronstyle.getNeuron()))
	  {
	    checkMap();
	    checkNeuron();
	    /*	    pasteneuron.setEnabled(false);
	    insertneuron.setEnabled(false);
	    removerole.setEnabled(false);
	    hiderole.setEnabled(false);*/
	    boxChoice.show(manager.getDisplayer(),m.mouseevent.getX(),
			   m.mouseevent.getY());
	  }
	break;
      case MapEvent.HIT_NEURONLINE:
	if(setNeuron(m.neuronstyle.getNeuron()))
	  {
	    checkMap();
	    checkNeuron();
	    //pasteneuron.setEnabled(false);
	    //insertneuron.setEnabled(false);
	    removerole.setEnabled(false);
	    hiderole.setEnabled(false);
	    roleChoice.show(manager.getDisplayer(),m.mouseevent.getX(),
			    m.mouseevent.getY());
	  }
	break;
      case MapEvent.HIT_ROLELINE:
	if(setNeuron(m.rolestyle.getRoleOwner().getNeuron()))
	  {
	    checkMap();
	    checkNeuron();

	    //pasteneuron.setEnabled(false);
	    //insertneuron.setEnabled(false);
	    removerole.setEnabled(true);
	    showrole.setEnabled(true);
	    roleChoice.show(manager.getDisplayer(),m.mouseevent.getX(),
		m.mouseevent.getY());
	  }
	break;
      case MapEvent.HIT_NONE:
	checkMap();
	//CheckMap has enabled removeneuron and insertneuron if possible...
	//	pasteneuron.setEnabled(true);
	//	insertneuron.setEnabled(true);
	/*	removeneuron.setEnabled(false);
	addrole.setEnabled(false);
	removerole.setEnabled(false);
	showrole.setEnabled(false);
	hiderole.setEnabled(false);
	neuronbox.setEnabled(false);
	neuronline.setEnabled(false);
	adddetailedmap.setEnabled(false);
	removedetailedmap.setEnabled(false);*/
	backgroundChoice.show(manager.getDisplayer(),m.mouseevent.getX(),
		m.mouseevent.getY());
      }
  }
  
  private boolean setNeuron(Neuron ne)
  {
    if (neuron!=null)
      neuron.removeEditListener(this);
    neuron=ne;
    if (neuron!=null)
      neuron.addEditListener(this);
    return neuron!=null;
  }
  
  //////////////******************Detach!!!!!
  
  private void checkMap()
  {
    ComponentSaver csaver=controller.getComponentSaver();
    boolean editable=conceptmap.isEditingPossible() && csaver!=null
      && csaver.isComponentSavable(conceptmap);
    ClipboardLibrary cl=(ClipboardLibrary) controller.getLibraryDisplayer().getLibrary().getLibrary("clipboardlibrary");
    if (cl!=null && cl.getCurrentNeuron()!=null)
      pasteneuron.setEnabled(editable);
    else
      pasteneuron.setEnabled(false);
    insertneuron.setEnabled(editable);
    neuronline.setEnabled(editable);
    neuronbox.setEnabled(editable);
  }

  private void checkNeuron()
  {
    ComponentSaver csaver=controller.getComponentSaver();
    boolean mapeditable=conceptmap.isEditingPossible() && csaver!=null
      && csaver.isComponentSavable(conceptmap);
    boolean neuroneditable=neuron.isEditingPossible() && csaver!=null
      && csaver.isComponentSavable(neuron);
    
    removeneuron.setEnabled(mapeditable);
    addrole.setEnabled(mapeditable && neuroneditable);
    removerole.setEnabled(mapeditable && neuroneditable);
    showrole.setEnabled(mapeditable);
    hiderole.setEnabled(mapeditable);
    ClipboardLibrary cl=(ClipboardLibrary) controller.getLibraryDisplayer().getLibrary().getLibrary("clipboardlibrary");
    Neuron ne;
    if (cl!=null && (ne=cl.getCurrentNeuron())!=null && ne.getType().indexOf("contentdescription")!=-1)
      {
	String[] strl=ne.getDataValues("MIMEType");
	if (strl.length!=0 && strl[0].equals("application/x-conceptmap"))
	  {
	    adddetailedmap.setEnabled(true);
	    Tracer.debug("YES! detailedmap can be fetched from clippboard.");
	  }
	else
	  {
	    Tracer.debug("String.length =="+strl.length);
	    Tracer.debug("strl[0]=="+strl[0]);
	    adddetailedmap.setEnabled(false);
	  }

      }
    else
	adddetailedmap.setEnabled(false);
    if (mapevent.neuronstyle!=null )
      removedetailedmap.setEnabled(mapevent.neuronstyle.getDetailedMap()!=null);
    else if (mapevent.rolestyle!=null && mapevent.rolestyle.getRoleOwner() != null)  //Super Ugly, but i seems
      //necessary to avoid a 'hard to find' nullpointerexception..... investigate in future!!!!
      removedetailedmap.setEnabled(mapevent.rolestyle.getRoleOwner().getDetailedMap()!=null);
  }

  public void componentEdited(EditEvent e)
  {
    if (e.getEditType()==se.kth.cid.component.Component.EDITABLE_CHANGED)
      {
	checkMap();
	if (neuron!=null)
	  checkNeuron();
      }
  }
  public MapEvent getMapEvent()
  {
    return mapevent;
  }

  public void reset(MapEventListener mel)
  {
    if (delegate==mel)
	delegate=null;
  }
  public void setProcessingSubDecisions(MapEventListener mel)
  {
    delegate=mel;
  }
  
  public void paint(Graphics g) {}
}
