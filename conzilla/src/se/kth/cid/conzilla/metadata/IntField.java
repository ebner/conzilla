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
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;



class IntField extends JTextField
{

  class IntDocument extends PlainDocument
  {
   
    public void insertString(int offs, String str, AttributeSet a) 
      throws BadLocationException
      {
	if (str == null)
	  return;

	super.insertString(offs, str, a);

	checkValue();
      }

    
    public void remove(int offs, int len)
      throws BadLocationException
      {
	super.remove(offs, len);
	checkValue();
      }
    
    void checkValue() throws BadLocationException
      {
	int value = 0;
	try {
	  value = Integer.parseInt(this.getText(0, getLength()));
	} catch(NumberFormatException e)
	  {
	    setForeground(Color.red);
	    return;
	  }
	
	if(value < 0)
	  setForeground(Color.red);
	else
	  setForeground(Color.black);
      }
  }
  
  
  public IntField(int cols, int init, boolean editable)
    {
      super(cols);
      setBackground(Color.white);
      setEditable(editable);
    }

  public int getInt()
    {
      String text = getText();
      if(text.length() == 0)
	return -1;

      int value;
      try {
	value = Integer.parseInt(text);
      } catch(NumberFormatException e)
	{
	  return -1;
	}
      
      if(value < 0)
	return -1;
      
      return value;
    }
  
  protected Document createDefaultModel()
    {
      return new IntDocument();
    }
}


