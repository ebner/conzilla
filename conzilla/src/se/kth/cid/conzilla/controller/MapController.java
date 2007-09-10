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
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.center.*;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.filter.*;
import java.beans.*;

/** This interface describes the functionality of an object that
 *  handles a set of maps and surrounding objects.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface MapController
{
  /** Returns the Conzilla-Kit stuffed with information that isn't window specific.
   */
  ConzKit getConzKit();

  /** Returns the component loader used in this controller.
   *
   * @return the loader used in this controller.
   */
  ComponentLoader getComponentLoader();

  /** Returns the component saver used in this controller.
   *
   * @return the saver used in this controller.
   */
  ComponentSaver  getComponentSaver();

  /** Sets the content displayer of this controller.
   *
   *  Fires a PropertyChangeEvent with name "contentDisplayer".
   *
   *  @param disp the content displayer of this controller.
   */
  void setContentDisplayer(ContentDisplayer disp);

  /** Returns the content displayer of this controller.
   *
   *  @return the content displayer of this controller.
   */
  ContentDisplayer getContentDisplayer();

  /** Sets the content selector of this controller.
   *
   *  Fires a PropertyChangeEvent with name "contentSelector".
   *
   *  @param sel the content selector of this controller.
   */
  void setContentSelector(ContentSelector sel);

  /** Returns the content selector of this controller.
   *
   *  @return the content selector of this controller.
   */
  ContentSelector getContentSelector();

  /** Sets the metadata displayer of this controller.
   *
   *  Fires a PropertyChangeEvent with name "metaDataDisplayer".
   *
   *  @param disp the metadata displayer of this controller.
   */
  void setMetaDataDisplayer(MetaDataDisplayer disp);

  /** Returns the metadata displayer of this controller.
   *
   *  @return the metadata displayer of this controller.
   */

  MetaDataDisplayer getMetaDataDisplayer();

  /** Returns the library displayer of this controller.
   *
   *  @return the library displayer of this controller.
   */
  LibraryDisplayer getLibraryDisplayer();

  /** Sets the tool factory of this controller.
   *
   *  Fires a PropertyChangeEvent with name "toolFactory".
   *
   *  @param factory the tool factory of this controller.
   */
  void setToolFactory(ToolFactory factory);

  /** Returns the tool factory of this controller.
   *
   *  @return the tool factory of this controller.
   */
  ToolFactory getToolFactory();

  /** Zooms in using the given neuron in the current map.
   *
   *  Uses the DetailedMap field in the NeuronStyle corresponding to the
   *  given neuron.
   *
   *  @param neuron the URI of a neuron in the current map to zoom in to.
   *  @exception ControllerException if the zoom in could not be performed.
   */
  void zoomIn(URI neuron) throws ControllerException;

  /** Jumps to the given map.
   *
   *  @param mapuri the URI of a map (or rather, its ContentDescription)
   *         to jump to.
   *  @exception ControllerException if the jump could not be performed.
   */
  void jump(URI mapuri) throws ControllerException;

  /** Shows the given map in the current map set.
   *
   *  @param mapuri the URI of a map (or rather, its ContentDescription)
   *         to show.
   *  @exception ControllerException if the show could not be performed.
   */
  void showMap(URI mapuri) throws ControllerException;

  //  void jumpAndShow(URI mapuri, URI submap) throws ControllerException;

  /** Initiates a content selection.
   *
   *  Will use a content filter and a content selector.
   *
   *  @param neuron the URI of a neuron in the current map to select
   *         a content of.
   *  @exception ControllerException if the selection could not be performed.
   */
  void selectContent(URI neuron) throws ControllerException;

  /** Returns the URI of the neuron whose content is currently being selected.
   *
   *  @return the URI of the neuron whose content is currently being selected.
   */
  URI  getSelectContent();

  /** Adds a property change listener to this controller.
   *
   * @param l the property change listener to add.
   */
  void addPropertyChangeListener(PropertyChangeListener l);

  /** Removes a property change listener from this controller.
   *
   * @param l the property change listener to remove.
   */
  void removePropertyChangeListener(PropertyChangeListener l);

  /** Adds a history listener to this controller.
   *
   * @param l the history listener to add.
   */
  void addHistoryListener(HistoryListener l);

  /** Removes a history listener from this controller.
   *
   * @param l the history listener to remove.
   */
  void removeHistoryListener(HistoryListener l);

  /** Fires a history event to all listeners.
   *
   *  @param e the history event to fire.
   */
  void fireHistoryEvent(HistoryEvent e);

  /** Returns the currently displaying manager.
   *
   *  @return the currently displaying manager.
   */
  MapManager getCurrentMapManager();


  /** Returns the URI of the currently displaying map.
   *
   *  @return the URI of the currently displaying map.
   */
  URI getCurrentMapURI();

  /** Returns the title of the currently displaying map.
   *
   *  @return the title of the currently displaying map.
   */
  String getCurrentMapTitle();

  //  TabSet     getCurrentTabSet();
  //  void       userMessage(String message);

  /** Returns the filterFactory.
   *
   *  @return the filterFactory.
   */
  FilterFactory getFilterFactory();

  /** Sets the filterFactory.
   *
   *  @param filterFactory the filterFactory of this controller.
   */
  void setFilterFactory(FilterFactory filterFactory);

  /** Returns the old filter belonging to a previous map.
   *
   *  @return the old filter belonging to a previous map.
   */
  Filter getOldFilter();

  /** Sets the old filter belonging to a previous map.
   *
   *  @param filter the filter belonging to a previous map.
   */
  void setOldFilter(Filter filter);
}
