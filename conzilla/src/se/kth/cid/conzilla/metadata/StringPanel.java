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

package se.kth.cid.conzilla.metadata;
import se.kth.cid.component.MetaData;
import se.kth.cid.component.MetaDataUtils;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;



public class StringPanel extends JTextArea implements MetaDataFieldEditor
{

  boolean edited = false;

  String metaDataField;
  MetaDataEditListener editListener;
  
  public StringPanel(String init, boolean textFlow, boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      super(init);
      this.metaDataField = metaDataField;
      this.editListener = editListener;
      
      if(textFlow)
	{
	  setRows(0);
	  setColumns(36);
	  setLineWrap(true);
	  setWrapStyleWord(true);
	}
      else
	{
	  setRows(1);
	  if(editable)
	    setColumns(12);
	}

      if(editable)
	setBorder(BorderFactory.createLoweredBevelBorder());
      else
	{
	  setBackground(UIManager.getColor("control"));
	  setBorder(BorderFactory.createEtchedBorder());
	}

      setEditable(editable);
      getDocument().addDocumentListener(new DocumentListener() {
	  public void removeUpdate(DocumentEvent l)
	    {
	      fireEdited();
	    }

	  public void insertUpdate(DocumentEvent l)
	    {
	      fireEdited();
	    }
	  public void changedUpdate(DocumentEvent l)
	    {
	    }
	});
    }

  public boolean isEdited()
    {
      return edited;
    }

  public void detach()
    {
    }
  
  public String getString(boolean resetEdited, boolean allowNull)
    {
      if(resetEdited)
	edited = false;
      
      String text = getText();
      if(allowNull && text.length() == 0)
	return null;

      return text;
    }

  void fireEdited()
    {
      edited = true;
      if(editListener != null)
	editListener.fieldEdited(new MetaDataEditEvent(null, metaDataField));
    }
}


