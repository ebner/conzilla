/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.filter;
import java.net.URI;
import java.util.Vector;

import se.kth.cid.component.Component;
import se.kth.cid.component.RelationSet;
import se.kth.cid.component.Resource;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;


/** The filter is a tool for structuring content in a concept map.
 *  This is an abstract class.
 *
 *  @author Daniel Pettersson
 *  @version $Revision$
 */
public abstract class AbstractFilter implements Filter
{
  URI filter;
  FilterNode firstnode;
  Resource [] contents;
  Vector lifo;
  protected MapController controller;
  //    Component [] contents;
  //    ContentSet cSet;

    /** Constructs a Filter.
     *
     *  @param cont the controller controlling the manager.
     *  @param filterURI the URI as a string.
     */
    public AbstractFilter(MapController cont, URI filterURI)
                          throws FilterException
    {
      this.filter = filterURI;
      controller = cont;
      lifo = new Vector();
	//	Concept concept=cont.getConzillaKit().getComponentStore().getAndReferenceConcept(filterURI);
      firstnode = new FilterNode(filterURI, ConzillaKit.getDefaultKit().getResourceStore(), lifo, this);
	//      } catch (MalformedURIException e) {
	//	throw new FilterException("Malformed URI, no filter.");
	//      } 
    } 
    public String getURI()
    {
        return filter.toString();
    }

    public FilterNode getFilterNode()
    {
      return firstnode;
    }

    public void setContent(Resource component)
    {
      contents = (new RelationSet(component, "content", 
				 ConzillaKit.getDefaultKit().getResourceStore())).getRelations();
    }

    public void showContent(Vector vcons)
    {
        Component [] comps = new Component[vcons.size()];
        vcons.copyInto(comps);
//TODO: ContentSelector has changed its interface so that now it takes ContentInformation in a collection instead.
//        controller.getContentSelector().selectContentFromSet(comps);
    }

    // Must be implemented in subclass!
    //    public abstract Vector filterContent(FilterNode node, Component component);
}
