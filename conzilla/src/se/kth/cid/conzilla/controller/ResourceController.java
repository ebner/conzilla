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

package se.kth.cid.conzilla.controller;

import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.content.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.center.*;

import java.util.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.*;

public class ResourceController extends SimpleController
{
  public ResourceController(ConzKit kit)
  {
    super(kit);
  }

  public void resourceLibrary(IndexLibrary library)
    {
      URI currentURI = getMapURI(currentMap);
      String currentMapTitle = null;
      if(currentMap != -1)
	currentMapTitle = ((MapEntry) maps.elementAt(currentMap)).title;

      if(library.getLibraryContentDescription().equals(currentMainMapURI))
	return;

      releaseMaps();

      ContentDescription thiscDesc=null;
      //egentligen borde man hämta från ResourceLibraryt
      try {
	thiscDesc = new ContentDescription(library.getLibraryContentDescription(), kit.loader);
      } catch (ComponentException e)
	{
	  Tracer.trace("ResourceLibrarys contentdescription cannot be loaded: "
		       + library.getLibraryContentDescription() + ": "
		       + e.getMessage(),Tracer.MAJOR_INT_EVENT);
	}

      MapManager newMan=new MapManager(this, library, thiscDesc);

      newMan.makeTools(toolFactory);

      ConceptMap cmap = newMan.getDisplayer().getMap();

      Tracer.debug("Resourcemap loaded: " + cmap.getURI());

      maps.addElement(new MapEntry(thiscDesc, newMan));

      currentMainMapURI = library.getLibraryContentDescription();
      mapBox.setModel(new BoxModel());
      mapBox.setSelectedIndex(0);

      String newMapTitle = ((MapEntry) maps.elementAt(currentMap)).title;

      fireHistoryEvent(new HistoryEvent(HistoryEvent.JUMP, this,
					currentURI, currentMapTitle,
					null, null,
					library.getLibraryContentDescription(),
					newMapTitle));
    }
}
