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
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.metadata.*;
import java.awt.*;
import java.awt.event.*;


/** This class holds the basic functionality for editing a component,
 *  it is recomended to inherit from this class.
 *
 *  @see ComponentEditor
 */
public class ConceptMapDialog extends ComponentDialog 
{
  public ConceptMapDialog(ConzillaKit kit)
    {
      super(kit);

      final JButton edit = new JButton("View map");
      edit.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    try { 
		ConceptMapDialog.this.kit.getConzilla().openMapInNewView(URIClassifier.parseValidURI(component.getURI()), null);
	    } catch(ControllerException ex)
		{
		    ErrorMessage.showError("Cannot load map", "Cannot load map:\n " + component.getURI(), ex, edit);
		}
	  }});
      toolBar.add(edit);
    }
  
  protected String getComponentString()
    {
      return "ConceptMap";
    }

    //necessary??????????
  public ComponentDialog copy()
    {
      ComponentDialog cd=new ConceptMapDialog(kit);
      //maybe copy stuff...
      return cd;
    }
}
