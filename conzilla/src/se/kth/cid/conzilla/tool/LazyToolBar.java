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


package se.kth.cid.conzilla.tool;
import se.kth.cid.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

/** This class is an abstract class used to implement tool bars that are not
 *  constructed until they are used.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class LazyToolBar
{
  /** The tool bar.
   */
  ToolSetBar toolBar;

  /** The name of the tool bar.
   */
  String     name;


  /** Constructs a LazyToolBar from an already existing tool bar.
   *
   *  @param toolBar the existing tool bar.
   */
  public LazyToolBar(ToolSetBar toolBar)
  {
    this.toolBar = toolBar;
    this.name = toolBar.getName();
  }

  /** Constructs a LazyToolBar with the given name.
   *
   *  Only used by extending classes.
   *
   * @param name the name of the toolbar.
   */
  protected LazyToolBar(String name)
  {
    this.name = name;
  }

  /** Returns the tool bar.
   *
   *  Null if it has not yet been created.
   *  @return the tool bar.
   */
  public ToolSetBar getToolBar()
  {
    return toolBar;
  }


  /** Returns the name of the tool bar.
   *
   *  @return the name of the tool bar.
   */
  public String getName()
  {
    return name;
  }

  public String toString()
  {
    return name;
  }

  /** Makes sure the tool bar is constructed.
   */
  public final void makeToolBar()
  {
    if(toolBar != null)
      return;

    toolBar = makeToolBarImpl();
  }

  /** Constructs the tool bar. Will at most be called once.
   *
   *  @return the tool bar.
   */
  protected ToolSetBar makeToolBarImpl()
  {
    return null;
  }

  /** Detaches the tool bar.
   *
   *  Calls detachImpl, removes all tools from the tool bar,
   *  and removes all component from the tool bar.
   */
  public void detach()
  {
    if(toolBar != null)
      {
	detachImpl(toolBar);
	toolBar.removeAllTools();
	toolBar.removeAll();
      }
  }

  /** Detaches the sub-class specifics of the tool bar.
   *
   *  @param toolSetBar the tool bar to detach. Never null.
   */
  protected void detachImpl(ToolSetBar toolSetBar)
  { 
  }
}
