/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Set;

import javax.swing.JComponent;

import se.kth.cid.component.Component;
import se.kth.cid.component.ComponentManager;
import se.kth.cid.component.Container;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.ColorTheme.Colors;
import se.kth.cid.layout.ContextMap;

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
//    String COLOR_CONTENT_FROM_BOX = "conzilla.content.frombox.color";
//    
//    String COLOR_BACKGROUND = "conzilla.content.background.color";
//
//    String COLOR_TEXT = "conzilla.content.text.color";
//
//    String COLOR_SELECTION_BACKGROUND = "conzilla.content.selection.background.color";
//
//    String COLOR_SELECTION_TEXT = "conzilla.content.selection.text.color";
//
//    String COLOR_LEAF_ASPECT = "conzilla.content.leafaspect.color";
//
//    String COLOR_POPUP_BACKGROUND = "conzilla.content.popup.background.color";
//
//    String COLOR_POPUP_BACKGROUND_ACTIVE = "conzilla.content.popup.background.activecolor";
//
//    String COLOR_POPUP_TEXT = "conzilla.content.popup.text.color";
//
//    String COLOR_POPUP_TEXT_ACTIVE = "conzilla.content.popup.text.activecolor";
//
//    String COLOR_POPUP_BORDER = "conzilla.content.popup.border.color";
//
//    String COLOR_POPUP_BORDER_ACTIVE = "conzilla.content.popup.border.activecolor";
	
	// FIXME replace occurences of COLOR_* with Colors.*
	
    String COLOR_CONTENT_FROM_BOX = Colors.CONTENT;
    
    String COLOR_BACKGROUND = Colors.CONTENT; // lighter

    String COLOR_TEXT = Colors.FOREGROUND;

    String COLOR_SELECTION_BACKGROUND = Colors.FOREGROUND;

    String COLOR_SELECTION_TEXT = Colors.CONTENT;

    String COLOR_LEAF_ASPECT = Colors.FOREGROUND;

    String COLOR_POPUP_BACKGROUND = Colors.CONTENT; // translucent

    String COLOR_POPUP_BACKGROUND_ACTIVE = Colors.CONTENT; // lighter

    String COLOR_POPUP_TEXT = Colors.FOREGROUND;

    String COLOR_POPUP_TEXT_ACTIVE = Colors.FOREGROUND;

    String COLOR_POPUP_BORDER = Colors.FOREGROUND;

    String COLOR_POPUP_BORDER_ACTIVE = Colors.FOREGROUND;

  void setController(MapController controller);
  MapController getController();

  /** 
   * Sets the content to select amongst and an optional ComponentManager to use as filter.
   * If the set of contentIndormation is set to null, the content selection is disabled.
   * If the ComponentManager is given, only those contentInformations which originate from
   * visible Containers are shown.
   *
   *  @param contentInformation the content to select amongst as a {@link Collection} of 
   *  {@link se.kth.cid.notions.ContentInformation}s. A {@link ComponentManager} is used 
   *  to filter out only ContentInformations that originate from visible {@link Container}s.
   */
  void selectContentFromSet(Set contentInformation, ComponentManager componentManager);
    
  /** Selects one content, i.e. a remote control.
   *
   *  @param index the content to select.
   */
  void select(int index);

  Component getContent(int index);

  /** If a content is selected it is returned.
   */
  Component getSelectedContent();


  JComponent getComponent();

  void setContentPath(String [] path);

  /** Selection choosen property.
   */
  String SELECTION="selection";  
  
  /** ContentSelector opened or closed.
   */
  String SELECTOR="selector";
  
  /** ContentSelector resized.
   */
  String RESIZE="resize";
    
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
