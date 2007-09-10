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


package se.kth.cid.conzilla.library;
import se.kth.cid.util.*;
import se.kth.cid.content.*;
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/** This is a template-command-tool that have to be embedded into a menu.
 *  It launches a dialog where information about the neuron have to
 *  be filled in.
 *  @author Matthias Palmèr
 *  @version $Revision$
 */
public class TemplateCommandTool extends CommandTool
{

  JMenu menu;
  CreateNeuronDialog cndialog;
  NeuronStyle template;
  TemplateLibrary tl;
  
  
  public class NAA extends AbstractAction
  {
    NeuronStyle ns;
    public NAA(NeuronStyle nns)
      {
	super(nns.getTitle());
	ns=nns;
      }
    public void  actionPerformed(ActionEvent ae)
      {
	Tracer.debug("Template-neuron "+ns.getTitle()+" choosen.");
	template(ns);
      }
  }
  
  protected static String TEMPLATE_NAME="create from template";

  /** Constructs a TemplateCommandTool.
   *
   *  @param man the MapManager the tool is attached to.
   *  @param cont the controller controlling the manager.
   */
  public TemplateCommandTool(MapManager man, MapController cont)
  {
    super(TEMPLATE_NAME, man, cont);
    menu=new JMenu(TEMPLATE_NAME);
    
    ConceptMap conceptmap=manager.getDisplayer().getMap();
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
    cndialog=new CreateNeuronDialog(JOptionPane.getFrameForComponent(man),
					 uri,this ,controller);
  }
  
  public void putActionInMenu(JPopupMenu pmenu)
  {
    pmenu.add(menu);
  }

  protected void activateImpl()
  {
    Tracer.debug("TemplateCommandTool activated!!!");
    tl=(TemplateLibrary) controller.getLibraryDisplayer().getLibrary().getLibrary("templatelibrary");
    if (tl!=null)
      {
	Tracer.debug("TemplateLibrary wasn't null");
	java.util.Enumeration en=tl.getTemplates();
	for (;en.hasMoreElements();)
	  {
	    menu.add(new NAA((NeuronStyle) en.nextElement()));
	    Tracer.debug("adding template");
	  }
      }
  }
  
  protected void deactivateImpl()
  {
    menu.removeAll();
    tl=null;
    overNeuron = null;
  }

  
  /** The Template-command is always active.
   */
  protected boolean updateStateImpl(boolean bo)
    {
      if (overNeuron==null)
	{
	  menu.setEnabled(true);
	  menu.removeAll();
	  tl=(TemplateLibrary) controller.getLibraryDisplayer().getLibrary().getLibrary("templatelibrary");
	  if (tl!=null)
	    {
	      Tracer.debug("TemplateLibrary wasn't null");
	      java.util.Enumeration en=tl.getTemplates();
	      for (;en.hasMoreElements();)
		{
		  menu.add(new NAA((NeuronStyle) en.nextElement()));
		  Tracer.debug("adding template");
		}
	    }
	  
	  return true; 
	}
      menu.setEnabled(false);
      return false;
    }

  public void template(NeuronStyle ns)
    {
      template=ns;
      try {
	if (!ns.getNeuron().isEditable())
	  ns.getNeuron().setEditable(true);
	cndialog.setNeuronStyle(new NeuronStyle(ns));
	ns.disconnectNeuron();

	cndialog.show();
      } catch (NeuronStyleException e)
	{
	  Tracer.trace(e.getMessage() +"\nCannot copy neuron from template."
		       +"Probably the template is erroneous.",Tracer.BUG);
	}
      catch (ComponentException e)
	{
	  Tracer.trace(e.getMessage() +"\nCannot copy neuron from template."
		       +"Probably the template is erroneous.",Tracer.BUG);
	}
    }
  public void adoptNeuron(NeuronStyle ns)
    {
      if (ns!=null)
	{
	  Rectangle bb=ns.getBoundingBox();
	  MouseEvent me=mapevent.mouseevent;   //shouldn't be null if we've come this far.
	  bb.setLocation(me.getX(),me.getY());
	  ns.connect(manager.getDisplayer().getMap());
	}
      try {
	template.revive();
      }    catch (Exception e)
	{
	  Tracer.trace("When Library tries to refreshTemplate it doesn't work."
		       +"This is a serious error since it obiousely worked before"
		       +"we used the template. It has to be a programming error!"
		       +e.getMessage(),Tracer.BUG);
	}
    }
}
