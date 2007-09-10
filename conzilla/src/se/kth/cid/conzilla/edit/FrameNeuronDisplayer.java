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
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.center.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;


public class FrameNeuronDisplayer extends JFrame implements NeuronDisplayer
{

  JDesktopPane desktop;
  Hashtable neuronDialogs;
  class Pair
  {
    public Object one;
    public Object two;
    public Pair(Object one, Object two)
      {
	this.one=one;
	this.two=two;
      }
  }
  Hashtable neuronDialogDrafts;
  ConzKit kit;
  
  public FrameNeuronDisplayer(ConzKit kit)
    {
      super("NeuronEditor");
      this.kit=kit;
      desktop=new JDesktopPane();
      setContentPane(desktop);
      neuronDialogs=new Hashtable();
      neuronDialogDrafts=new Hashtable();
      addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  close();
	}
      });
      setSize(420, 420);
      setLocation(100, 100);
    }

  class MInternalFrameAdapter extends InternalFrameAdapter
  {
    NeuronDialog neuronDialog;
    public MInternalFrameAdapter(NeuronDialog nd)
      {
	neuronDialog=nd;
      }
    public void InternalFrameClosed(InternalFrameEvent e) {
      close(neuronDialog);
    }
  }
  
  public void         editNeuron(Neuron neuron)
    {
      URI nuri=null;
      try {
	nuri=new URI(neuron.getURI());

	if (!neuron.isEditable() && neuron.isEditingPossible() &&
	    kit.saver!=null && kit.saver.isComponentSavable(neuron))
	  neuron.setEditable(true);

	Pair pair;
	if ((pair=((Pair) neuronDialogs.get(nuri)))!=null)
	  show((NeuronDialog) pair.one);
	else
	  {
	    NeuronDialogAdapter nda=new NeuronDialogAdapter(kit);
	    nda.setNeuron(neuron);
	    addNeuronDialog(nda);
	  }
      } catch (MalformedURIException e) {
	Tracer.trace("The neuron have a malformed URI."
		     +"Therefore it is shown as a draft.",
		     Tracer.MAJOR_INT_EVENT);
	boolean gotIt=false;
	Enumeration en=neuronDialogDrafts.keys();
	for (;en.hasMoreElements();)
	  {
	    NeuronDialog nd=(NeuronDialog) en.nextElement();
	    if (neuron==nd.getNeuron())
	      {
		show(nd);
		gotIt=true;
		break;
	      }
	  }
	if (!gotIt)
	  {
	    if (!neuron.isEditingPossible())
	      {
		Tracer.trace("This is serious, a non editable object,"
			     + "for example a corba-object has a non acceptable URI!!!!"
			     +"Please notify the provider of this concept.",
			     Tracer.MAJOR_INT_EVENT);
	      }
	    else
	      if (!neuron.isEditable())
		neuron.setEditable(true);
	    NeuronDialogAdapter nda=new NeuronDialogAdapter(kit);
	    nda.setNeuron(neuron);
	    addNeuronDialog(nda);
	  }
      }
      setVisible(true);
    }
  
  public void         addNeuronDialog(NeuronDialog nd)
    {
      JInternalFrame jif =new JInternalFrame("Neuron", true, true, true, true);
      JScrollPane scroll = new JScrollPane((Container) nd);
      jif.setContentPane(scroll);
      jif.addInternalFrameListener(new MInternalFrameAdapter(nd));
      jif.setSize(300,300);
      jif.setLocation(50,50);
      desktop.add(jif);
      
      if (nd.getNeuronURI()!=null)
	neuronDialogs.put(nd.getNeuronURI(), new Pair(nd,jif));
      else
	neuronDialogDrafts.put(nd, jif);
    }
      
  public boolean      removeNeuronDialog(NeuronDialog nd)
    {
      if (nd.getNeuronURI()!=null)
	return neuronDialogs.remove(nd.getNeuronURI())!=null;
      else
	return neuronDialogDrafts.remove(nd)!=null;
    }
  public void         show(NeuronDialog nd)
    {}
  public void         hide(NeuronDialog nd)
    {}
  public void close()
    {
      setVisible(false);
    }
  public void         exit()
    {
      Enumeration en=neuronDialogs.elements();
      for(;en.hasMoreElements();)
	close((NeuronDialog) ((Pair) en.nextElement()).one);
      en=neuronDialogDrafts.keys();
      for(;en.hasMoreElements();)
	close((NeuronDialog)  en.nextElement());
    }
  public void         close(NeuronDialog nd)
    {
      removeNeuronDialog(nd);
      //something else?? maybe add old dialogs in a menu.
    }
  
}

