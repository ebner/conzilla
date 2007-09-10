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


package se.kth.cid.conzilla.app;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.tool.*;
import javax.swing.JMenu;

/** 
 *  @author Matthias Palmer.
 */
public interface Extra
{
    String getName();
    boolean initExtra(ConzillaKit kit);
    
    void extendMenu(ToolsMenu m, MapController mc);
    void addExtraFeatures(final MapController c, final Object o, String location, String hint);
    
    void refreshExtra();
    boolean saveExtra();
    void exitExtra();
}
