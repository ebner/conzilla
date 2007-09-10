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
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.content.*;
import java.awt.*;
import java.awt.event.*;


/** This class holds the basic functionality for editing content,
 *
 *  @see ComponentEditor
 */
public class ContentDialog extends ComponentDialog 
{
  StringPanel contentLocationField;
    
  StringPanel MIMETypeField;

  public ContentDialog(ConzillaKit kit)
    {
      super(kit);

      JButton view = new JButton("View");
      view.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    try {
	      ContentDialog.this.kit.getContentDisplayer().setContent(component);
	    }  catch (ContentException ce)
	      {
		ErrorMessage.showError("View Error",
				       "Could not view content\n\n"
				       + component.getURI(), ce, null);
	      }
	  }});
      toolBar.add(view);
    }

  protected String getComponentString()
    {
      return "Content";
    }

  void updateFields()
    {
      MetaData.Location[] loc = component.getMetaData().get_technical_location();
      if(loc != null
)	contentLocationField.setText(loc[0].string);
      else
	contentLocationField.setText("");

      MetaData.LangStringType form = component.getMetaData().get_technical_format();
      if(form.langstring != null)
	MIMETypeField.setText(form.langstring[0].string);
      else
	MIMETypeField.setText("");
    }
  
  protected void createComponentTab()
    {
      super.createComponentTab();
      
      contentLocationField = new StringPanel("", false, false, null, null);
      MIMETypeField = new StringPanel("", false, false, null, null);

      updateFields();

      componentPanel.addPanel("Content location (Technical/Location)", contentLocationField);
      componentPanel.addPanel("MIME Type (Technical/Format)", MIMETypeField);      
    }

  //necessary??????????
  public ComponentDialog copy()
  {
    ComponentDialog cd=new ContentDialog(kit);
    //maybe copy stuff...
    return cd;
  }
}
