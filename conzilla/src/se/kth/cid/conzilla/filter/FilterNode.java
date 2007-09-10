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


package se.kth.cid.conzilla.filter;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/** This is a class for creating a node representing a filterneuron.
 *
 *  @author Daniel Pettersson
 *  @version $Revision$
 */
public class FilterNode{
    String filtertag[];
    Vector refines;
    Vector lifo;
    FilterNode topnode;
  //    FilterAction action;

    URI filterURI;
    Neuron filterNeuron;
    Vector axons;
    Filter filter;
    Hashtable contents;
    Hashtable bottomUp;

    ComponentStore store;

  final String [] taxon ={"Filter"};

    /** Constructs a FilterNode.
     *
     *  @param loader the ComponentLoader to use.
     *  @param filterneuron the Neuron associated with this FilterNode.
     *  @param man the MapManager attached to this FilterNode.
     *  @param lifo the loop vector associated with this FilterNode.
     *  @exception FilterException if the filter is looped.
     */
    public FilterNode(URI filterURI, ComponentStore store, Vector lifo, Filter filter)
                      throws FilterException
    {
	this.filter = filter;
	contents = new Hashtable();
	bottomUp = new Hashtable();
	this.store = store;
      try {
	filterNeuron = store.getAndReferenceNeuron(filterURI);
	URI typeURI=URIClassifier.parseURI(filterNeuron.getType(), filterURI);
	NeuronType type = store.getAndReferenceNeuronType(typeURI);
	if (!MetaDataUtils.isClassifiedAs(type.getMetaData().get_classification(),
					  "NeuronType", "Conzilla", taxon))
	  throw new FilterException("Wrong type in neuron, expected 'Filter'.");
	
	//Check the neurontype in metadata, if classification says filter.
	lifo.addElement(filterURI);
	//	action = new FilterAction(this, controller);
      } catch (ComponentException ce) {
	throw new FilterException("couldn't load filternode: "+ce.getMessage());
      } catch (MalformedURIException me) {
	throw new FilterException("malformed type-URI in neuron \n"+
				  filterURI.toString());
      }
      
      refines = new Vector();
      axons = new Vector();

      
      Axon [] axons=filterNeuron.getAxons();

      for (int i=0; i<axons.length;i++)
	{
	    URI newURI=null;
	  try {
	      newURI=URIClassifier.parseURI(axons[i].objectURI(),filterURI);
	  } catch (MalformedURIException me)
	      {
		  Tracer.trace("Couldn't load filterneuron "+axons[i].objectURI()+
			       me.getMessage(), Tracer.MINOR_EXT_EVENT);
	      }
	  if (!lifo.contains(newURI))
	    {
	      if (axons[i].getType().equals("refine"))
		{
		  try {
		    refines.addElement(new FilterNode(newURI, store, lifo, filter));
		  } catch (FilterException fe) {
		    Tracer.trace("Couldn't load filterneuron "+axons[i].objectURI()+
				 fe.getMessage(), Tracer.MINOR_EXT_EVENT);
		  }
		}
	      else if (axons[i].getType().equals("subfilter"))
		{}
	    }
	  else
	    {
	      Tracer.trace("Avoided loop in filter by not loading filterneuron" + 
			   axons[i].objectURI()
			   , Tracer.MINOR_EXT_EVENT);
	    }
	}
	  
      for (int i=0; i < refines.size(); i++)
	((FilterNode) refines.elementAt(i)).setTop(this);

      lifo.removeElement(filterURI);
    }

  /** The filternode is created from a neuron.
   * @return a filterneuron.
   */
  public Neuron getFilterNeuron()
  {
    return filterNeuron;
  }

  /** Returns the filtertag of this node.
   *
   *  @return the filtertag of this node.
   */   
    public String getFilterTag()
    {
      MetaData md=filterNeuron.getMetaData();
      String title = MetaDataUtils.getLocalizedString(md.get_metametadata_language(), md.get_general_title()).string;
      if(title.length() == 0)
	title = filterNeuron.getURI();
      return title;
    }
    
    public String getToolTipText()
    {
	MetaData.LangStringType[] desc = filterNeuron.getMetaData().get_general_description();
	if (desc!=null)
	    return MetaDataUtils.getLocalizedString(filterNeuron.getMetaData().get_metametadata_language(),
						    desc[0]).string;
	else
	    return null;
    }
		
    
  /** Returns the refine at given index of this node.
   *
   *  @return the refine at given index of this node.
   */
    public FilterNode getRefine(int index)
    {
        return (FilterNode) refines.elementAt(index);
    }

  /** Returns the action associated with this node.
   *
   *  @return the action associated with this node.

    public FilterAction getAction()
    {
        return action;
    }
   */

  /** Returns the number of refines attached to this node.
   *
   *  @return the number of refines attached to this node.
   */
    public int numOfRefines()
    {
      return refines.size();
    }

  /** Returns the node above this node.
   *
   *  @return the node above this node.
   */
    public FilterNode getTop()
    {
        return topnode;
    }

  /** Sets the node above this node.
   *
   *  @param node the node above this node.
   */
    public void setTop(FilterNode node)
    {
        topnode = node;
    }

    public void filterThrough(Component comp)
    {
	for (int i=0; i<numOfRefines();i++)
	    getRefine(i).filterThrough(comp);
	getContent(comp);
    }
    
    public List getContent(Component comp)
    {
	Vector contentFiltered=(Vector) contents.get(comp);
	if (contentFiltered!=null)
	    return contentFiltered;
	else if (getTop()!=null)
	    return filterContent(comp, getTop().getContent(comp));
	else
	    return initContentFromComponent(comp);
    }

    public List initContentFromComponent(Component comp)
    {
	
	Component [] contentsArr = (new RelationSet(comp, "content", 
						    store)).getRelations();
	
	Vector conts=new Vector();
	for (int i=0; i<contentsArr.length;i++)
	    if (filter.componentPasses(contentsArr[i], this))
		conts.addElement(contentsArr[i]);
	contents.put(comp, conts);
	return conts;
    }
    protected List filterContent(Component comp, List list) 
    {
	List conts = new Vector();
	Iterator it=list.iterator();
	for (;it.hasNext();)
	    {
		Component cont=(Component) it.next();
		if (filter.componentPasses(cont, this))
		    conts.add(cont);
	    }
	contents.put(comp, conts);
	if (getTop()!=null && numOfRefines()==0)
		getTop().contentPassedRefines(comp, conts);
	return conts;
    }
    
    public void contentPassedRefines(Component comp, List conts)
    {
	Set passed = (Set) bottomUp.get(comp);
	if (passed == null)
	    {
		passed = new HashSet(conts);
		bottomUp.put(comp, passed);
	    }
	else
	    passed.addAll(conts);
	
	if (getTop() != null)
	    getTop().contentPassedRefines(comp, conts);
    }
	
    public Set getContentPassedRefines(Component comp)
    {
	return (Set) bottomUp.get(comp);
    }
}
