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


package se.kth.cid.conzilla.library;
import se.kth.cid.util.*;
import se.kth.cid.content.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/** This tool changes the current-neuron in the library.
 *
 *  @author Matthias Palmèr
 *  @version $Revision$
 */
public class LibraryCloseTool extends Tool
{

  MapController controller;
  
  public LibraryCloseTool(MapController controller)
    {
      super("Close", Tool.ACTION);
      this.controller=controller;
    }

  protected void activateImpl()
  {
    controller.getLibraryDisplayer().hideLibrary();
  }

  protected void deactivateImpl()
    {}
  
  protected void detachImpl()
  {
    controller=null;
  }
}


