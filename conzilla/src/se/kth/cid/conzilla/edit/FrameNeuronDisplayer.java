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
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.library.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;


public class FrameNeuronDisplayer extends JFrame
           implements NeuronDisplayer, NeuronDraftListener
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
  NeuronDialogFactory neuronDialogFactory;

  public FrameNeuronDisplayer(ConzKit kit)
    {
      super("NeuronEditor");
      this.kit=kit;
      neuronDialogFactory=new NeuronDialogFactory(kit);
      desktop=new JDesktopPane();
      setContentPane(desktop);
      neuronDialogs=new Hashtable();
      neuronDialogDrafts=new Hashtable();
      addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  close();
	}
      });
      setJMenuBar(fixMenu());

      setSize(420, 420);
      setLocation(100, 100);
    }

  class CTMenuListener implements MenuListener
  {
    public void menuSelected(MenuEvent e) {}
    public void menuDeselected(MenuEvent e) {}
    public void menuCanceled(MenuEvent e) {}
  }
  private JMenuBar fixMenu()
    {
      JMenuBar mBar=new JMenuBar();
      JMenu menu=new JMenu("File");
      final TemplateCommandTool tct=new TemplateCommandTool(null, kit);
      tct.putToolInMenu(menu.getPopupMenu());
      final AbstractAction ifc=new AbstractAction("insert from clipboard")
	       {
		 public void actionPerformed(ActionEvent ae)
		   {
		     ClipboardLibrary cbl=(ClipboardLibrary) kit.libraryDisplayer.getLibrary().getLibrary("clipboardlibrary");
		     if (cbl!=null && cbl.getCurrentNeuron()!=null)
		       editNeuron(cbl.getCurrentNeuron());
		   }
	       };
      menu.add(ifc);
      menu.add(new AbstractAction("close")
	      {public void actionPerformed(ActionEvent ae) {close();}});
      menu.addMenuListener(new CTMenuListener()
			   {
			     public void menuSelected(MenuEvent e)
			       {
				 ClipboardLibrary cbl=(ClipboardLibrary) kit.libraryDisplayer.getLibrary().getLibrary("clipboardlibrary");
				 ifc.setEnabled(cbl!=null && cbl.getCurrentNeuron()!=null);
				 tct.action();
			       }
			   });
      mBar.add(menu);
      return mBar;
    }

  class MInternalFrameAdapter extends InternalFrameAdapter
  {
    Container neuronView;
    public MInternalFrameAdapter(Container nv)
      {
	neuronView=nv;
      }
    public void internalFrameClosing(InternalFrameEvent e)  //Seems like this is the only function that is called...
      {
	Tracer.debug("InternalFrameClosing");
	FrameNeuronDisplayer.this.close(neuronView);
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
	  show((JInternalFrame) pair.two);
	else
	  {
	    NeuronDialog nda=neuronDialogFactory.getNeuronDialog(neuron);
	    addNeuronView((Container) nda);
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
		show((Container) nd);
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
	    NeuronDialog nda=neuronDialogFactory.getNeuronDialog(neuron);
	    addNeuronView((Container) nda);
	  }
      }
      setVisible(true);
    }

  public NeuronDraft  addNewDraft()
    {
      NeuronDraft nd=(NeuronDraft) new NeuronDraftAdapter(kit); //TODO(MP): use the factory here!!!!
      addNeuronView((Container) nd);
      return nd;
    }

  public void madeNeuron(NeuronDraft nd)
    {
      removeNeuronView((Container) nd);
      editNeuron(nd.getNeuron());
    }
  public void neuronCanceled(NeuronDraft nd)
    {
      removeNeuronView((Container) nd);
    }

  public void         addNeuronView(Container nv)
    {
      JInternalFrame jif =new JInternalFrame("Neuron", true, true, true, true);
      JScrollPane scroll = new JScrollPane((Container) nv);
      jif.setContentPane(scroll);
      jif.addInternalFrameListener(new MInternalFrameAdapter(nv));
      jif.setSize(300,300);
      jif.setLocation(50,50);
      desktop.add(jif);

      if (nv instanceof NeuronDialog)
	neuronDialogs.put(((NeuronDialog) nv).getNeuronURI(), new Pair(nv,jif));
      else if (nv instanceof NeuronDraft)
	{
	  javax.swing.border.Border border=BorderFactory.createEtchedBorder(Color.red, Color.red.darker());
	  jif.setBorder(border);
	  jif.setTitle("Draft");
	  Insets ins=border.getBorderInsets(jif);
	  ins.bottom=5;
	  neuronDialogDrafts.put(nv, jif);
	  ((NeuronDraft) nv).addListener(this);
	}
      show(jif);
    }

  public boolean      removeNeuronView(Container nv)
    {
      JInternalFrame jif=getInternalFrame(nv);
      if (jif!=null)
	desktop.remove(jif);
      desktop.repaint();
      if (nv instanceof NeuronDialog)
	return neuronDialogs.remove(((NeuronDialog) nv).getNeuronURI())!=null;
      else if ( nv instanceof NeuronDraft)
	{

	  ((NeuronDraft) nv).removeListener(this);
	  return neuronDialogDrafts.remove(nv)!=null;
	}
      return false;
    }

  private JInternalFrame getInternalFrame(Container nv)
    {
      URI uri;
      Pair pair;
      if (nv!=null && nv instanceof NeuronDialog)
	{
	  uri=((NeuronDialog) nv).getNeuronURI();
	  if ((pair=(Pair) neuronDialogs.get(uri))!=null)
	    return (JInternalFrame) pair.two;
	}
      else if ( nv instanceof NeuronDraft )
	return (JInternalFrame) neuronDialogDrafts.get(nv);
      return null;
    }

  public void         display()
    {
      setVisible(true);
    }
  public void         show(Container nv)
    {
      Tracer.debug("inside show");
      show(getInternalFrame(nv));
    }
  private void        show(JInternalFrame jif)
    {
      if (jif!=null)
	jif.moveToFront();
    }
  public void         hide(Container nv)
    {
      Tracer.debug("inside hide");
      hide(getInternalFrame(nv));
    }
  private void hide(JInternalFrame jif)
    {
      if (jif!=null)
	jif.moveToBack();
    }
  public void close()
    {
      setVisible(false);
    }
  public void         exit()
    {
      Enumeration en=neuronDialogs.elements();
      for(;en.hasMoreElements();)
	close((Container) ((Pair) en.nextElement()).one);
      en=neuronDialogDrafts.keys();
      for(;en.hasMoreElements();)
	close((Container)  en.nextElement());
    }
  public void         close(Container nv)
    {
      if (nv instanceof NeuronDraft)
	((NeuronDraft) nv).cancel();
      else
	removeNeuronView(nv);
      //something else?? maybe add old dialogs in a menu.
    }

  public Neuron       getSelected()
    {
      Enumeration en=neuronDialogs.elements();
      Pair pair=null;
      for (;en.hasMoreElements();)
	{
	  pair=(Pair) en.nextElement();
	  if (((JInternalFrame) pair.two).isSelected())
	    return ((NeuronDialog) pair.one).getNeuron();
	}
      return null;
    }
}

