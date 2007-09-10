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


package se.kth.cid.component.xml;
import se.kth.cid.component.*;
import se.kth.cid.component.local.*;
import se.kth.cid.xml.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;

import java.util.*;


/** This interface describes the common behaviour of all the different
 *  component handlers.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface XmlComponentHandler
{
  /** Loads a component from the given XML tree.
   * The tree must be for a component of the right type.
   *
   * @param root the root of the XML tree for the component.
   * @exception ComponentException if anything goes wrong.
   */
  LocalComponent loadComponent(XmlElement root, URI origuri)
    throws ComponentException;

  /** Builds an XML tree from a component.
   *
   *  Essentially reverses the loadComponent operation,
   *  The component need not have been loaded from XML, however.
   *  Indeed only public methods in the component are used, so
   *  it could in principle
   *  be used to dump a CORBA component.
   *
   *  The given component must be of the right type.
   *
   *  @param comp the component to build a tree from.
   *  @exception ComponentException if anything goes wrong.
   */ 
  XmlElement buildXmlTree(Component comp)
    throws ComponentException;
}

