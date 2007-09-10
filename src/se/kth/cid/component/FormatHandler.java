/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

import java.net.URI;

import se.kth.cid.identity.MIMEType;

/** FormatHandlers load and save components in a given MIME type.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface FormatHandler
{
  /** An int meaning Resource
   */ 
  int COMPONENT  = 0;
  
  /** An int meaning Container
   */ 
  int CONTAINER  = 1;
  
  /** An int meaning Concept
   */ 
  int CONCEPT     = 2;
  
  /** An int meaning ConceptMap
   */ 
  int CONCEPTMAP = 3;
  

  /** Returns the MIME type served by this FormatHandler
   *
   * @return the MIME type served by this handler.
   */
  MIMEType getMIMEType();

  /** Returns whether this formathandler can deal with a specified URI.
   */
  boolean canHandleURI(URI uri);

  /** To be commented.
   */
  void setComponentStore(ResourceStore store);

  /** Loads the specified container.
   *
   * @param uri the URI from where the component should be loaded.
   * @param origuri the component's real URI.
   * @return the loaded component. Never null.
   * @exception ComponentException if anything went wrong while loading the component.
   */
  Container  loadContainer(URI uri, URI origuri)
    throws ComponentException;
  

  /**
   * Gets the corresponding containermanager for this FormatHandler.
   * 
   */
  ContainerManager getContainerManager();
}

