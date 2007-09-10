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
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import se.kth.cid.content.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.library.*;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JTextField;
import java.beans.*; //Property change stuff
import java.awt.*;
import java.awt.event.*;

class OpenMapDialog extends JDialog {

  private JOptionPane optionPane;
  private MapController controller;
  private Object[] array=null;
  private final String msgString1 = "Type in a map-URI.";
  private final Frame aFrame;
  private final JTextField textField;
  
  public void abortDialog()
    {
      //      setVisible(false);
      hide();
      //      pack();
      //      validate();
      optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
    }
  public void setURIText(String uri)
    {
      textField.setText(uri);
    }
  
  public OpenMapDialog(final Frame aFrame, String uri, MapController contr) {
    super(aFrame, false);
    this.aFrame=aFrame;
    
    setLocationRelativeTo(aFrame);
    controller=contr;
    
    setTitle("Open map");

    textField = new JTextField(uri, 10);
    array = new Object[2];
    array[0]=msgString1;
    array[1]=textField;

    final String btnString1 = "OK";
    final String btnString2 = "Cancel";
    Object[] options = {btnString1, btnString2};

    optionPane = new JOptionPane(array, 
				 JOptionPane.QUESTION_MESSAGE,
				 JOptionPane.YES_NO_OPTION,
				 null,
				 options,
				 options[0]);
    setContentPane(optionPane);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    pack();
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) 
	{
	  optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
	}
    });

    textField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	optionPane.setValue(btnString1);
      }
    });
    
    optionPane.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent pe) {
	String prop = pe.getPropertyName();

	if (isVisible() 
	    && (pe.getSource() == optionPane)
	    && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
		prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
	  Object value = optionPane.getValue();

	  if (value == JOptionPane.UNINITIALIZED_VALUE) {
	    //ignore reset
	    return;
	  }
	  if (value.equals(btnString2))
	      {
		abortDialog();
		return;
	      }
	  if (value.equals(btnString1)) {
	    try {
	      URI newuri=new URI(textField.getText());
	      try {
		Tracer.debug("Testing a jump!");
		controller.jump(newuri);
		abortDialog();
	      } catch(ControllerException e)
		{
		  Tracer.debug("Jump failed!");
		  ComponentSaver saver=controller.getComponentSaver();
		  if (saver.isURISavable(newuri))
		    {
		      Tracer.debug("uri is savable!");
		      int res=JOptionPane.showConfirmDialog(aFrame,
							    "The map you requested doesn't exists, create it?",
							    "information",
							    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
		      if (res==JOptionPane.OK_OPTION)
			{
			  Tracer.debug("getConceptmapURI");
			  URI mapuri=getConceptmapURI(saver,newuri);
			  Tracer.debug("Suceeded getConceptmapURI");
			  if (mapuri==null)
			    {
			      Tracer.debug("but uri wasn't correct, aborting");
			      abortDialog();
			      return;
			  }
			  try{
			    Tracer.debug("trying to create stuff!");
			    ContentDescription desc;
			    ConceptMap map;
			    URI contentdescription_newmap=new URI("cid:local/template/cd/contentdescription_newmap");
			    URI newmap=new URI("cid:local/template/cm/newmap");
			    ComponentLoader loader=controller.getComponentLoader();
			    Tracer.debug("trying to create stuff next step!");
			    try {
			      desc=new ContentDescription(contentdescription_newmap, loader);
			    } catch (ComponentException ce)
			      {
				ce.printStackTrace();
				Tracer.debug(ce.getMessage());
				throw new ComponentException("Can't create new ContentDescription from template since template \n"
							     +"cid:local/template/cd/contentdescription_newmap isn't a ContenDescription.");
			      }
			    Tracer.debug("alright!");
			    se.kth.cid.component.Component comp = loader.loadComponent(newmap, loader);
			    if(! (comp instanceof ConceptMap))
			      {
			      loader.releaseComponent(comp);
			      throw new ComponentException("Can't create new ConceptMap from template since template \n"
							     +"cid:local/template/cm/newmap isn't a ConceptMap.");
			      }
			    Tracer.debug("alright2!");
			    map = (ConceptMap) comp;
			    Neuron ne=desc.getNeuron();
			    ne.setEditable(true);
			    ne.setURI(newuri.toString());
			    loader.renameComponent(contentdescription_newmap,newuri);
			    Tracer.debug("halfway with desc!");
			    String [] strl=ne.getDataValues("URI");
			    for (int i=0;i<strl.length;i++)
			      ne.removeDataValue("URI",strl[i]);
			    ne.addDataValue("URI",mapuri.toString());
			    int slashpos = mapuri.toString().lastIndexOf('/');
			    ne.getMetaData().setValue("Title",mapuri.toString().substring(slashpos+1,mapuri.toString().length()));
			    Tracer.debug("trying with map!");
			    map.setEditable(true);
			    map.setURI(mapuri.toString());
			    Tracer.debug("Now trying to jump again.!");
			    try {
			      controller.jump(newuri);
			      saver.saveComponent(desc.getNeuron());
			      saver.saveComponent(map);
			    } catch(ControllerException ce)
			      {
				TextOptionPane.showError(aFrame, "Failed to open map:\n "
							 + ce.getMessage());
			    }
			    loader.releaseComponent(map);
			    abortDialog();
			    return;
			    //desc is a ContentDescription, it takes care of it's releasing itself.
			  } catch (MalformedURIException me) {
			    Tracer.trace("Ok, this is really bad, hardcoded uri isn't a uri.....",Tracer.BUG);
			    abortDialog();
			    return;
			  } catch (ComponentException ce) {
			    Tracer.trace(ce.getMessage(),Tracer.ERROR);			  
			    abortDialog();
			    return;
			  }
			}
		      else if (res==JOptionPane.CANCEL_OPTION)
			{
			  abortDialog();
			  return;
			}
		    }
		  else
		    {
		    TextOptionPane.showError(aFrame, "Failed to open map: \n "
					     +"And can't open new since the given URI is unsavable.");
			  abortDialog();
			  return;		    
		    }
		}
	    } catch (MalformedURIException me)
	      {
		TextOptionPane.showError(aFrame, "This is not a valid URI. \n "
					 +me.getMessage());
	      }		
	  }
	  // reset the JOptionPane's value
	  optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
	}
      }
    });
  }
  
  private URI getConceptmapURI(ComponentSaver saver, URI uri)
    {
      String newsuri;
      String suri=uri.toString();
      int slashpos2=suri.lastIndexOf('/');
      int slashpos1=suri.substring(0,slashpos2-1).lastIndexOf('/');
      if (suri.substring(slashpos1+1,slashpos2).equals("cd"))
	newsuri=suri.substring(0,slashpos1)+"/cm/"+suri.substring(slashpos2+1,suri.length());
      else if (suri.substring(slashpos1+1,slashpos2).equals("contentdescription"))
	  newsuri=suri.substring(0,slashpos1)+"/conceptmap/"+suri.substring(slashpos2+1,suri.length());
      else
	newsuri=suri+"_map";
      URI newuri=null;
      try {
	newsuri = (String) JOptionPane.showInputDialog(this,
						       "Map URI",
						       "Map URI",
						       JOptionPane.QUESTION_MESSAGE,
						       null, null, newsuri);
	if (newsuri==null)
	  return null;
	newuri=new URI(newsuri);
	if (saver.isURISavable(newuri))
	  return newuri;
      } catch(MalformedURIException e)
	{
	  TextOptionPane.showError(aFrame, "Malformed URI:\n "
				   + e.getMessage());
	}
      return null;
    }
}

