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
import java.util.*;
import java.awt.event.*;


/** This class holds the basic functionality for creating new neurons,
 *  it is recomended to inherit from this class.
 *
 *  @see ComponentDraft
 */
public class ConceptMapDraft extends ComponentDraft
{
  public ConceptMapDraft(ConzillaKit kit, java.awt.Component parent)
    {
      super(kit, parent);
    }
  
  protected void adjustMetaData(MetaData md)
    {
      super.adjustMetaData(md);
      MetaData.LangString [] langStrings = new MetaData.LangString[1];
      langStrings[0] = new MetaData.LangString(null, MIMEType.CONCEPTMAP.toString());
      md.set_technical_format(new MetaData.LangStringType(langStrings));
    }

  Component createComponent(URI uri, URI createURI, MIMEType type) throws ComponentException, MalformedURIException
    {
      return kit.getComponentStore().getHandler().createConceptMap(uri, createURI, type);
    }
}
