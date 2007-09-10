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


package se.kth.cid.conceptmap;
import se.kth.cid.neuron.*;
import se.kth.cid.component.cache.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.component.*;

public interface NeuronStyle extends MapGroupStyle
{
  int FIRST_NEURONSTYLE_EDIT_CONSTANT  = ConceptMap.LAST_CONCEPTMAP_ONLY_EDIT_CONSTANT + 1;
  int DETAILEDMAP_EDITED               = FIRST_NEURONSTYLE_EDIT_CONSTANT;
  int BOUNDINGBOX_EDITED      	       = FIRST_NEURONSTYLE_EDIT_CONSTANT + 1;
  int BODYVISIBLE_EDITED      	       = FIRST_NEURONSTYLE_EDIT_CONSTANT + 2;
  int DATATAG_ADDED            	       = FIRST_NEURONSTYLE_EDIT_CONSTANT + 3;
  int DATATAG_REMOVED    	       = FIRST_NEURONSTYLE_EDIT_CONSTANT + 4;
  int AXONSTYLE_REMOVED                = FIRST_NEURONSTYLE_EDIT_CONSTANT + 5;
  int AXONSTYLE_ADDED                  = FIRST_NEURONSTYLE_EDIT_CONSTANT + 6;
  int LINE_EDITED             	       = FIRST_NEURONSTYLE_EDIT_CONSTANT + 7;
  int TEXT_ANCHOR_EDITED                = FIRST_NEURONSTYLE_EDIT_CONSTANT + 8;
  int LAST_NEURONSTYLE_EDIT_CONSTANT   = TEXT_ANCHOR_EDITED;

  int WEST = 0;
  int CENTER = 1;
  int EAST = 2;
  int NORTH = 0;
  int SOUTH = 2;


  /** Returns the ConceptMap this NeuronStyle is located in.
   *
   *  @return the ConceptMap this NeuronStyle is located in.
   */
  ConceptMap getConceptMap();

  /** Returns the URI of this NeuronStyle. This ID
   *  is unique within the ConceptMap.
   *
   *  @return the ID of this NeuronStyle.
   */
  String getURI();

   /** Removes this NeuronStyle from the ConceptMap. Also removes all
    *  AxonStyles it is connected to, either as owner or as end.
    */
  void remove();
  
  /** Returns the URI of the Neuron that this NeuronStyle represents.
   *  This String may be assumed to be a valid URI that may be relative
   *  to the ConceptMap.
   *
   *  @return the URI of the Neuron that this NeuronStyle represents.
   */
  String getNeuronURI();


  /** Returns the detailed map of this NeuronStyle.
   *  This String may be assumed to be a valid URI that may be relative
   *  to the ConceptMap.
   *
   *  @return the detailed map of this NeuronStyle.
   */
  String getDetailedMap();

  /** Sets the detailed map of this NeuronStyle. May be set to null.
   *
   *  @param uri the detailed map of this NeuronStyle.
   *  @exception ReadOnlyException if this ConceptMap is no editable.
   *  @exception InvalidURIException if the given URI is not valid.
   */
  void   setDetailedMap(String uri)
    throws ReadOnlyException, InvalidURIException;
  
  /** Returns the bounding box of the body of this NeuronStyle.
   *
   *  @return the bounding box of the body of this NeuronStyle.
   *          Never null.
   */
  ConceptMap.BoundingBox getBoundingBox();

  /** Sets the bounding box of the body of this NeuronStyle.
   *  Must never be null.
   * 
   *  @param rect the bounding box of the body of this NeuronStyle.
   */
  void setBoundingBox(ConceptMap.BoundingBox rect) throws ReadOnlyException;

  
  /** Returns whether the body of this NeuronStyle should be visible.
   *
   *  @return whether the body of this NeuronStyle should be visible.
   */
  boolean getBodyVisible();

  /** Sets whether the body of this NeuronStyle should be visible.
   *
   *  @param visible whether the body of this NeuronStyle should be visible.
   */
  void setBodyVisible(boolean visible) throws ReadOnlyException;
  

  void setHorisontalTextAnchor(int value) throws ReadOnlyException;
  void setVerticalTextAnchor(int value) throws ReadOnlyException;

  int getHorisontalTextAnchor();
  int getVerticalTextAnchor();

  /** Returns the data tags that should be visible in the body of this
   *  NeuronStyle.
   *
   *  @return the visible data tags. Never null.
   */
  String[] getDataTags();

  /** Adds a visible data tag.
   *
   *  @param tag the tag that should be shown.
   */
  void addDataTag(String tag) throws ReadOnlyException;

  /** Removes a visible data tag.
   *
   *  @param tag the tag that should be removed.
   */  
  void removeDataTag(String tag) throws ReadOnlyException;

  /** Returns the line connecting the body with the axons.
   *  It may be null. If non-null, it contains at least two elements.
   *  The last Position is the end pointing to the body.
   *
   *  @return the line connecting the body with the axons.
   */
  ConceptMap.Position[] getLine();

  /** Sets the line connecting the body with the axons.
   *  It may be null. If non-null, it must contain at least two elements.
   *  The last Position is the end pointing to the body.
   *
   *  @param line the line connecting the body with the axons.
   */  
  void setLine(ConceptMap.Position[] line) throws ReadOnlyException;
  
    int getPathType();
    void setPathType(int pt);

  ////////////AxonStyle/////////////

  /** Returns the AxonStyles that this NeuronStyle is the object for.
   *
   *  @return the AxonStyles that this NeuronStyle is the object for. Never null.
   */
  AxonStyle[] getObjectOfAxonStyles();

  /** Returns the AxonStyles that this NeuronStyle is the subject for.
   *
   *  @return the AxonStyles that this NeuronStyle is the subject for. Never null.
   */
  AxonStyle[] getSubjectOfAxonStyles();
  
  /** Returns the AxonStyles that this NeuronStyle is the owner of.
   *
   *  @return the AxonStyles that this NeuronStyle is the owner of. Never null.
   */
  AxonStyle[] getAxonStyles();

  /** Returns the AxonStyle with the given ID.
   *
   *  @param id the ID of the wanted AxonStyle.
   *  @return the AxonStyle with the given ID.
   */
  AxonStyle getAxonStyle(String id);

  /** Adds an AxonStyle to this NeuronStyle AND WITH THIS
   *  neuronstyle as subject as well.
   *  
   *  @deprecated use <code>addAxonStyle(String, NeuronStyle, NeuronStyle)</code> instead.
   *  @param axonID the Axon ID of the AxonStyle within the Neuron.
   *  @param object the NeuronStyle the AxonStyle will point to.
   */
  AxonStyle addAxonStyle(String axonID, NeuronStyle object)
    throws ReadOnlyException, ConceptMapException;

  /** Adds an AxonStyle to this NeuronStyle.
   *  
   *  @param axonID the Axon ID of the AxonStyle within the Neuron.
   *  @param subject the NeuronStyle the AxonStyle will point from.
   *  @param object the NeuronStyle the AxonStyle will point to.
   */
  AxonStyle addAxonStyle(String axonID, String subjectstyleuri, String objectstyleuri)
    throws ReadOnlyException, ConceptMapException;
  
}



