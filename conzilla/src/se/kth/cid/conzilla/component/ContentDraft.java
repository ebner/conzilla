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
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.component.local.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.identity.*;
import java.util.*;
import java.awt.event.*;


/** This class holds the basic functionality for creating new neurons,
 *  it is recomended to inherit from this class.
 *
 *  @see ComponentDraft
 */
public class ContentDraft extends ComponentDraft
{
  JTextArea message;

  URIField contentLocationField;
    
  MIMEField MIMETypeField;
  
  public ContentDraft(ConzillaKit kit, java.awt.Component parent)
    {
      super(kit, parent);
    }
    
  protected void setAttributes()
    {
      message = new JTextArea("Warning: \"http://www.crap.org\" is not an\n"+
			      "acceptable URI, \"http://www.crap.org/\" is.\n"+
			      "Note: relative URL's do work.");
      message.setEditable(false);
      message.setColumns(30);
      message.setBorder(BorderFactory.createLineBorder(java.awt.Color.black, 3));

      panel.add(message);
      
      //-------------Content-Location-------------
      contentLocationField = new URIField(30, null);
      
      contentLocationField.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    setTypeFromContentLocation();
	    contentLocationField.getNextFocusableComponent().requestFocus();
	  }});

      panel.addPanel("Content location", contentLocationField);
      
      //----------------MIME-Type------------------
      MIMETypeField = new MIMEField(30);

      MIMETypeField.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    MIMETypeField.getNextFocusableComponent().requestFocus();
	  }});

      panel.addPanel("MIME type", MIMETypeField);

      super.setAttributes();
    }
  
  protected void setFocusBehaviour()
    {
      super.setFocusBehaviour();
      contentLocationField.setNextFocusableComponent(MIMETypeField);
      MIMETypeField.setNextFocusableComponent(nameTipField);
    }

  public void hintContentBaseURI(String buri, boolean stripToBase)
    {
      if (stripToBase)
	contentLocationField.setText(extractBaseURI(buri));
      else
	contentLocationField.setText(buri);
    }
  protected String stripExtension(String str)
  {
    int loc=str.indexOf('.');
    if (loc!=-1)
      return str.substring(0,loc);
    else
      return str;
  }
  
  Component createComponent(URI uri, URI createURI, MIMEType type) throws ComponentException, MalformedURIException, MalformedMIMETypeException
    {
      contentLocationField.setBaseURI(uri);
      URI location = contentLocationField.getURI();
      MIMEType mType = MIMETypeField.getMIMEType();
      
      return super.createComponent(uri, createURI, type);
    }

  protected void updateLocationInfoImpl(URI uri)
    {
	contentLocationField.setBaseURI(uri);
    }

  protected void setTypeFromContentLocation()
    {
      String uri = contentLocationField.getText();
      
      int lastSlash = uri.lastIndexOf('/');
      String title = uri.substring(lastSlash + 1);
      
      nameTipField.setText(stripExtension(title));
      componentURIField.setText(extractBaseURI(componentURIField.getText()) + stripExtension(title));
      
      if (uri.indexOf("http") != -1 || uri.indexOf(".htm")!= -1)
	MIMETypeField.setText("text/html");
      else
	MIMETypeField.setText(MIMEType.CONCEPTMAP.toString());
    }
	    

  protected void adjustMetaData(MetaData md) throws ReadOnlyException
    {
      MetaData.LangString [] langStrings = new MetaData.LangString[1];
      langStrings[0] = new MetaData.LangString("en", nameTipField.getText());
      md.set_general_title(new MetaData.LangStringType(langStrings));
      
      MetaData.Location [] locations = new MetaData.Location[1];
      locations[0] = new MetaData.Location("URI", contentLocationField.getText());
      md.set_technical_location(locations);
      
      langStrings = new MetaData.LangString[1];
      langStrings[0] = new MetaData.LangString(null,MIMETypeField.getText()); 
      md.set_technical_format(new MetaData.LangStringType(langStrings));
      //More???
    }
}
