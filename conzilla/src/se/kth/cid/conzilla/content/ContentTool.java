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


package se.kth.cid.conzilla.content;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conceptmap.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;

/**
 *
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public abstract class ContentTool extends AbstractTool 
{
  /** The controller to use.
   */
  protected MapController controller;

  /** UpdateState saves the ContentEvent for use when
   *  the tool is used.*/
  protected int contentIndex;

  public ContentTool(String name,int type, MapController cont)
    {
      super(name, type);
      controller = cont;
    }  
  
  /** Call this before the activateImpl is called,
   *  it will update the MapTool depending on what
   *  to act on.
   *
   *  @param e the event that triggered the update.
   */
  public void update(Object o)
  {
    if ( o != null && o instanceof Integer)
      {
	  contentIndex=((Integer) o).intValue();

	  if (updateImpl())	  
	      enable();
	  else
	      disable();
      }
  }


  /** You must implement this function,
   *  it is intended to add update behaviour.
   */
  protected abstract boolean updateImpl();

  protected void activateImpl()
  {}

  protected void deactivateImpl()
  {}


  /** @see Tool.detachImpl()
   */
  protected void detachImpl()
  {
    controller = null;
  }
}
