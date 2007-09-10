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
import se.kth.cid.component.*;
import se.kth.cid.conzilla.controller.*;
import java.beans.*;

/** This interface describes the functionality of an object that
 *  is able to select amongst content.
 *  In contrast to ContentFilter, this interface is intended to
 *  be asynchronous, i.e., allows user interaction.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface ContentSelector
{
    String COLOR_CONTENT_FROM_BOX = "conzilla.content.frombox.color";
    

  void setController(MapController controller);
  MapController getController();

  /** Sets the content to select amongst.
   *
   *  If set to null, the selecting of content is disabled.
   *
   *  @param content the content to select amongst.
   */
  void selectContentFromSet(Component[] content);
    
  /** Selects one content, i.e. a remote control.
   *
   *  @param o this object can be interpreted differently depending on implementation.
   */
  void select(int index);

  /** Same as select without changing the existing selection.
   *
   *  @param o a Object used to do the actual choosing.
   *  @returns a content in the form of a Component. 
   */
  Component getContent(int index);

  /** If a content is selected it is returned.
   */
  Component getSelectedContent();


  java.awt.Component getComponent();

  void setContentPath(String [] path);

  /** Selection choosen property.
   */
  String SELECTION="selection";  
  
  /** ContentSelector opened or closed.
   */
  String SELECTOR="selector";
    
  /** Adds a selection listener to this object.
   *
   *  The selection listener will receive notification when
   *  a content has been selected.
   *
   *  @param l the listener to add.
   */
  void addSelectionListener(String propertyName, PropertyChangeListener l);


  /** Removes a selection listener from this object.
   *
   *  @param l the listener to remove.
   */
  void removeSelectionListener(String propertyName, PropertyChangeListener l);
}
