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


package se.kth.cid.component;


/** EditEvents are fired by components that are changed.
 *  They are intended to be exported over CORBA, something that will require
 *  some thought.
 *
 *  An EditEvent contains the component that has been edited, the type of edit
 *  as specified by a constant in the individual component class,
 *  and an object describing the targeted change, such as the new value.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class EditEvent
{
  /** The component that has been edited.
   */
  Component source;

  /** The type of edit.
   */
  int editType;

  /* The targeted change. See the relevant edit method in the component of interest for
   * details.
   *
   * This field will cause trouble in a CORBA environment, and thus needs fixing.
   */
  Object    target;

  /** Constructs an EditEvent.
   *
   * @param source the component that has been edited.
   * @param editType the type of edit.
   * @param target the targeted change.
   */
  public EditEvent(Component source, int editType, Object target)
  {
    this.source = source;
    this.editType = editType;
    this.target = target;
  }

  /** Returns the component that has been edited.
   *
   * @return the component that has been edited.
   */
  public Component getSource()
  {
    return source;
  }

  /** Returns the type of edit.
   *
   * @return the type of edit.
   */
  public int getEditType()
  {
    return editType;
  }

  /** Returns the targeted change.
   *
   * @return the targeted change.
   */
  public Object getTarget()
  {
    return target;
  }

  public String toString()
  {
    return "EditEvent[" + source + "," + editType + "," + target + "]";
  }
}

