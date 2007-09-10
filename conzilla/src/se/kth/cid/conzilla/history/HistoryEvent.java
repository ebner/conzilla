/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.history;

import java.net.URI;

import se.kth.cid.conzilla.controller.MapController;


/** This class is used to describe history events.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class HistoryEvent
{
  /** The type fired when a new map is loaded.
   */
  public static final int MAP     = 0;

  /** The type fired when a content selection occurs.
   */
  public static final int CONTENT = 1;

  /** The source of the event.
   */
  MapController source;

  /** The URI of the source map.
   */
  URI sourceMapURI;

  /** The title of the source map.
   */
  String sourceMapTitle;

  
  /** The URI of the source concept.
   */
  URI sourceConceptURI;

  /** The title of the source concept.
   */
  String sourceConceptTitle;


  /** The URI of the destination component.
   */
  URI destinationURI;

  /** The title of the destination component.
   */
  String destinationTitle;

  /** The type of event.
   */
  int type;

  /** Contructs a HistoryEvent.
   */
  public HistoryEvent(int type, MapController source,
		      URI sourceMapURI, String sourceMapTitle,
		      URI sourceConceptURI, String sourceConceptTitle,
		      URI destinationURI, String destinationTitle)
    
  {
    this.type = type;
    this.source = source;

    this.sourceMapURI = sourceMapURI;
    this.sourceMapTitle = sourceMapTitle;
    
    this.sourceConceptURI = sourceConceptURI;
    this.sourceConceptTitle = sourceConceptTitle;
    
    this.destinationURI = destinationURI;
    this.destinationTitle = destinationTitle;
  }

  /** Returns the type of this HistoryEvent.
   *
   *  @return the type of this HistoryEvent.
   */  
  public int getType()
  {
    return type;
  }

  /** Returns the source of this event.
   *
   *  @return the source of this event.
   */
  public MapController getSource()
  {
    return source;
  }
  
  /** Returns the source map of this event.
   *
   *  @return the source map of this event.
   */
  public URI getSourceMapURI()
  {
    return sourceMapURI;
  }

  /** Returns the title of the source map of this event.
   *
   *  @return the title of the source map of this event.
   */
  public String getSourceMapTitle()
  {
    return sourceMapTitle;
  }

  
  /** Returns the source concept of this event.
   *
   *  @return the source concept of this event.
   */
  public URI getSourceConceptURI()
  {
    return sourceConceptURI;
  }

  /** Returns the title of the source concept of this event.
   *
   *  @return the title of the source concept of this event.
   */
  public String getSourceConceptTitle()
  {
    return sourceConceptTitle;
  }

  
  /** Returns the destination of this event.
   *
   *  @return the destination of this event.
   */
  public URI getDestinationURI()
  {
    return destinationURI;
  }
  
  /** Returns the title of the destination of this event.
   *
   *  @return the title of the destination of this event.
   */
  public String getDestinationTitle()
  {
    return destinationTitle;
  }
}
