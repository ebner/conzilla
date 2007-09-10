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

package se.kth.cid.conzilla.identity;

import se.kth.cid.identity.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;



public class URIField extends JTextField
{

  URI base;
  
  class URIDocument extends PlainDocument
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
	try {
	  URI uri = URIClassifier.parseURI(this.getText(0, getLength()), base);
	} catch(MalformedURIException e)
	  {
	    setForeground(Color.red);
	    return;
	  }
	
	setForeground(Color.black);
      }
  }
  
  
  public URIField(int cols, URI base)
    {
      super(cols);
      this.base = base;
    }

  public URI getURI() throws MalformedURIException
    {
      return URIClassifier.parseURI(getText(), base);
    }

  public String getRelativeURI(boolean allowDotDot) throws MalformedURIException
    {
      return base.makeRelative(URIClassifier.parseURI(getText(), base), allowDotDot);
    }

  public void setBaseURI(URI uri)
    {
      base = uri;
      setText(getText());
    }
  
  protected Document createDefaultModel()
    {
      return new URIDocument();
    }
}


