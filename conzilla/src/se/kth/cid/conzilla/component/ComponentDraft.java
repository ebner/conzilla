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


package se.kth.cid.conzilla.component;
import javax.swing.*;
import javax.swing.event.*;
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.component.local.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.identity.*;
import se.kth.cid.conzilla.util.*;
import java.util.*;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;


/** This class holds the basic functionality for creating components,
 *  it is recomended to inherit from this class.
 *
 *  @see ComponentDraft
 */
public class ComponentDraft extends JDialog
{
  ConzillaKit kit;
  
  Component component = null;

  protected MetaDataPanel panel;
  protected JToolBar toolBar;
  
  protected JTextField nameTipField;
  protected URIField componentURIField;
  protected URIField resultURI;
  protected MIMEField resultMIMEType;
  
  protected JButton okButton, cancelButton;

  public ComponentDraft(ConzillaKit kit, java.awt.Component parent)
    {
      super(JOptionPane.getFrameForComponent(parent), "New component", true);
      this.kit = kit;
     
      panel = new MetaDataPanel();
      toolBar = new JToolBar();
      toolBar.setFloatable(false);
      JScrollPane sPane = new JScrollPane(panel);
      
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(sPane, BorderLayout.CENTER);
      getContentPane().add(toolBar, BorderLayout.SOUTH);
      
      setAttributes();
      setFocusBehaviour();

      pack();
      setLocationRelativeTo(parent);
    }
    
  protected void setAttributes()
    {
      // ------------------Name-tip-----------------
      nameTipField = new JTextField("", 30);
      nameTipField.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    componentURIField.setText(uriGuess());
	    nameTipField.getNextFocusableComponent().requestFocus();
	  }});

      panel.addPanel("Title", nameTipField);

      // --------------Component-URI----------------
      componentURIField = new URIField(30, null);
      
      componentURIField.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    updateLocationInfo();
	    componentURIField.getNextFocusableComponent().requestFocus();
	  }});

      JLabel resultURILabel = new JLabel("Actual URI: ");
      JLabel resultTypeLabel = new JLabel("MIME type: ");
      
      resultURI = new URIField(30, null);
      resultURI.setEditable(false);
      resultMIMEType = new MIMEField(30);
      resultMIMEType.setEditable(false);

      JPanel box = new JPanel();
      box.setLayout(new BorderLayout());

      JPanel labelBox = new JPanel();
      labelBox.setLayout(new GridLayout(0, 1));
      labelBox.add(resultURILabel);
      labelBox.add(resultTypeLabel);
      
      JPanel valueBox = new JPanel();
      valueBox.setLayout(new GridLayout(0, 1));
      valueBox.add(resultURI);
      valueBox.add(resultMIMEType);

      box.add(labelBox, BorderLayout.WEST);
      box.add(valueBox, BorderLayout.CENTER);
      
      Box URIBox = new Box(BoxLayout.Y_AXIS);
      URIBox.add(componentURIField);
      URIBox.add(box);
      
      panel.addPanel("Component URI", URIBox);

      // --------------Ok-Button-------------------
      okButton = new JButton("Ok");

      AbstractAction okAction = new AbstractAction() {
	  public void actionPerformed(ActionEvent ae) {
	    makeComponent();
	  }};
      
      okButton.registerKeyboardAction(okAction, "Ok",
				      KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER ,0),
				      JComponent.WHEN_FOCUSED);
 
      okButton.addActionListener(okAction);

      // -------------Cancel-Button-----------------
      cancelButton = new JButton ("Cancel");

      AbstractAction cancelAction = new AbstractAction() {
	  public void actionPerformed(ActionEvent ae) {
	    cancel();
	  }};
      
      cancelButton.addActionListener(cancelAction);
      cancelButton.registerKeyboardAction(cancelAction, "Cancel",
					  KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER ,0),
					  JComponent.WHEN_FOCUSED);

      toolBar.add(Box.createHorizontalGlue());
      toolBar.add(okButton);
      toolBar.add(cancelButton);

      // Why doesn't this work?
      nameTipField.requestFocus();
      nameTipField.setCaretPosition(0);
    }
  
  String uriGuess()
    {
      StringBuffer title = new StringBuffer(nameTipField.getText());
      for(int i = 0; i < title.length(); i++)
	if(!Character.isLetterOrDigit(title.charAt(i)))
	  title.setCharAt(i, '_');
      
      return extractBaseURI(componentURIField.getText()) + title;
    }
  
  protected void setFocusBehaviour()
    {
      nameTipField.setNextFocusableComponent(componentURIField);
      componentURIField.setNextFocusableComponent(okButton);
      okButton.setNextFocusableComponent(cancelButton);
    }
  
  
  public void hintsFromComponent(Component comp)
    {
      hintMetaData(comp.getMetaData());
    }
  
  public void hintBaseURI(String buri, boolean stripToBase)
    {
      if (stripToBase)
	componentURIField.setText(extractBaseURI(buri));
      else
	componentURIField.setText(buri);
    }

  public void hintMetaData(MetaData md)
    {
	// copy some of the metadata
    }
  
  protected void adjustMetaData(MetaData md) throws ReadOnlyException
    {
      MetaData.LangString [] langStrings=new MetaData.LangString[1];
      String local_language = MetaDataUtils.getLanguageString(Locale.getDefault());
      langStrings[0] = new MetaData.LangString(local_language, nameTipField.getText());
      md.set_general_title(new MetaData.LangStringType(langStrings));
      
      MetaData.Location [] locations = new MetaData.Location[1];
      locations[0]=new MetaData.Location("URI", md.getComponent().getURI());
      md.set_technical_location(locations);
      
      //More???
    }

  public Component getComponent()
    {
      return component;
    }

  protected void updateLocationInfo()
    {
      URI uri = null;
      Object[] createRet = null;
      try {
	uri = componentURIField.getURI();
	createRet = kit.getComponentStore().getHandler().checkCreateComponent(uri);
	
      } catch(ComponentException e)
	{
	  resultURI.setText("Create not possible.");
	  resultMIMEType.setText("");
	  return;
	} catch(MalformedURIException e)
	{
	  resultURI.setText("Invalid URI.");
	  resultMIMEType.setText("");
	  return;
	}
      resultURI.setText(((URI) createRet[0]).toString());
      resultMIMEType.setText(((MIMEType) createRet[1]).toString());
    }
      
  Component createComponent(URI uri, URI createURI, MIMEType type) throws ComponentException, MalformedURIException, MalformedMIMETypeException
    {
      return kit.getComponentStore().getHandler().createComponent(uri, createURI, type);
    }

  protected void makeComponent()
    {
      URI uri = null;
      Object[] createRet = null;
      try {
	uri = componentURIField.getURI();
	createRet = kit.getComponentStore().getHandler().checkCreateComponent(uri);
      } catch (MalformedURIException me) 
	{
	  ErrorMessage.showError("Malformed URI", "Can't create component: "
				 + uri, me, this);
	  return;
	}
      catch (PathComponentException ce)
	{
	  int ans = JOptionPane.showConfirmDialog(this, "The directories \n"
						  + ce.getPath() + "\n"
						  + "does not exist. \n"
						  + "Create necessary directorys for this uri?");
	  if (ans == JOptionPane.YES_OPTION)
	    {
	      if (ce.makePath())
		{
		  try {
		    createRet = kit.getComponentStore().getHandler().checkCreateComponent(uri);
		  } catch (ComponentException ce2)
		    {
		      ErrorMessage.showError("Create Error", "Couldn't create component with uri = '" + uri + 
					     "',\n"+ 
					     "possible reasons:\n"+
					     "1) The ftpconnection were closed down unexpectedly.\n"+
					     "2) You don't have access rights on this server,\n"+
					     "3) URI already exists.\n"+
					     "(typically the component were snatched just in front of you.\n"+
					     "Could be yourself manually creating the component on disk.)",ce2,this);
		      return;
		    }
		}
	      else
		{ 
		  ErrorMessage.showError("Create Error","Failed to create neccessary path", null, this);
		  return;
		}
	    }
	  else 
	    return; //no message neccessary since you've answered no above.
	}
      catch(ComponentException e)
	{
	  ErrorMessage.showError("Create Error","Can't create component: " + uri, e, this);
	  return;
	}

      if (kit.getComponentStore().getCache().getComponent(uri.toString()) != null)  //Component can't exist in cache.
	  {
	    ErrorMessage.showError("Create Error",
				   "Component already exists in cache but not in storage\n" +
				   " Try to reload, this might free the uri for you.", null, this);
	    return;
	  }

      try {
	component = createComponent(uri, (URI) createRet[0], (MIMEType) createRet[1]);
	adjustMetaData(component.getMetaData());
	component.setEdited(true);
	kit.getComponentStore().getCache().referenceComponent(component);
	dispose();
      } catch(ComponentException e)
	{
	   ErrorMessage.showError("Create Error",
				  "Failed to create component.", e, this);
	}
      catch(MalformedURIException e)
	{
	   ErrorMessage.showError("Create Error",
				  "Invalid URI.", e, this);
	}
      catch(MalformedMIMETypeException e)
	{
	   ErrorMessage.showError("Create Error",
				  "Invalid MIME Type.", e, this);
	}
    }

  public void cancel()
    {
      dispose();
    }

  protected String extractBaseURI(String suri)
    {
      //TODO(MP):
      //A not very nice try to find the base-uri for the components in the conceptmap.
      //Needs some form of standardization of directory structure....
      //Should maybee be separated into a separate configurable class.
      int slashpos=suri.lastIndexOf('/');
      suri=suri.substring(0,slashpos+1);
      return suri;
    }
}
