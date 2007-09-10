/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

import java.net.URI;

/** This class wraps around a Concept containing a number of relations
 *  pointing to components. It is in essence,
 *  a set of components, usually in turn pointing to external resources.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class RelationSet
{
  /** The component containing the relation set.
   */
  Resource component;

  /** The Components contained as relations in the Resource.
   */
  Component[] relations;

  /** The loader user to load the components.
   */
  ResourceStore store;

  /**As the next constructor but without the component loaded.
   * 
   * @exception ComponentException if something goes wrong.
   */
  public RelationSet(URI uri, String kind, ResourceStore store)
    throws ComponentException
  {
    this(store.getAndReferenceComponent(uri), kind, store);
  }



  /** Constructs a RelationSet from a given Resource URI.
   *
   * @param kind the value of the kind elemtent for the wanted relations.
   * @param store the ResourceStore used to retrieve the components.
   */
  public RelationSet(Resource comp,String kind, ResourceStore store)
  {
//    Vector vrels = new Vector();
    component=comp;
    
    /*MetaData.Relation[] rels = component.getMetaData().get_relation();
    if(rels != null)
      {
	for(int i = 0; i < rels.length; i++)
	  {
	    if(rels[i].kind != null && rels[i].kind.string.equals(kind)
	       && rels[i].resource_location != null)
	      {
		try{
		  URI reluri = URIClassifier.parseURI(rels[i].resource_location,
						      URIClassifier.parseValidURI(comp.getURI()));
		  vrels.addElement(store.getAndReferenceComponent(reluri));
		} catch (MalformedURIException e)
		  {
		    Tracer.trace("Ignoring illegal relation location: "
				 + e.getURI() + ":\n " + e.getMessage(), Tracer.DETAIL);
		  }
		catch (ComponentException e)
		  {
		    Tracer.trace("Ignoring illegal component: \n "
				 + e.getMessage(), Tracer.DETAIL);
		  }
	      }
	  }
      }
    relations = (Component[]) vrels.toArray(new Component[vrels.size()]);*/
    relations = new Component[0];
  }

  /** Returns the Components in this RelationSet.
   *
   * @return the Components in this RelationSet.
   */
  public Component[] getRelations()
  {
    return relations;
  }

  /** Returns the Resource containing this ContentSet.
   *
   * @return the Resource containing this ContentSet.
   */
  public Resource getComponent()
  {
    return component;
  }

}
