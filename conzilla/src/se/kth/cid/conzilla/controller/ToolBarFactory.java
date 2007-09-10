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

import se.kth.cid.conzilla.edit.layers.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import se.kth.cid.test.*;

import java.awt.*;
import javax.swing.*;

public class ToolBarFactory extends LazyToolBar
  {
    MapManager manager;
    ToolFactory factory;
    
    public ToolBarFactory(String name, MapManager manager, ToolFactory factory)
    {
      super(name);
      this.factory=factory;
      this.manager = manager;
    }

    protected ToolSetBar makeToolBarImpl()
    {
      return factory.newToolBar(manager, getName(), null);
    }
    
    protected void detachImpl(ToolSetBar toolSetBar)
    {
      factory.detachToolBar(toolSetBar);
    }
  }
