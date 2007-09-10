//* $Id$ */
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
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


/** The filter is a tool for structuring content in a concept map.
 *  It reads from a filterneuron that comes with a neuron or a concept map.
 *
 *  @author Daniel Pettersson
 *  @version $Revision$
 */
public class ConcreteFilter extends AbstractFilter
{
    /** Constructs a ConcreteFilter.
     *
     *  @param man the MapManager the filter is attached to.
     *  @param cont the controller controlling the manager.
     *  @param filter the URI as a string.
     */
    public ConcreteFilter(MapController cont, URI filterNeuron)
           throws FilterException
    {
      super(cont, filterNeuron);
    }

   /** Filters the content of given component with given node.
    *
    *  @param node the filternode to use.
    *  @param neuron the neuron whose content will be filtered.
    *  @return a vector with filtered content.

    public Vector filterContent(FilterNode node, Component component)
    {
      setContent(component);

      return recursiveContent(node);
    }
    */

  /** Decides wheter the component should pass or not, 
   *  override to change conditions.
   *  @return true if component passes node.
   */
  public boolean componentPasses(Component component, FilterNode node)
  {
    DataValue[] search = node.getFilterNeuron().getDataValues();
    if (search.length==0)
	return true;
    
    MetaData.LangStringType [] lst = component.getMetaData().get_general_keywords();
    if (lst==null || lst.length==0)
	return false;

    // FIXME Special language encoding in data.
    // The special language encoding
    // should *not* be done this way. Rather, an extra attribute
    // (corresponding to xml:lang) should be added to data. Perhaps.
    for (int i=0; i<search.length; i++)
      {
	if(!search[i].predicate().equals("general_keywords"))
	  continue;

	String language=null;
	int kpos=search[i].objectValue().indexOf(':');
	if (kpos != -1)
	  {
	    language=search[i].objectValue().substring(0,kpos);
	    if (language.indexOf('-') != -1)
	      language=language.substring(0,language.indexOf('-'));
	  }
	else
	  language=node.getFilterNeuron().getMetaData().get_metametadata_language();
	String searchString=search[i].objectValue().substring(kpos+1);
	if (language!=null)
	    {
		for (int j=0;j<lst.length;j++)
		    for (int k=0;k<lst[j].langstring.length;k++)
			if ((lst[j].langstring[k].language==null || 
			     lst[j].langstring[k].language.equals(language)) &&
			    lst[j].langstring[k].string.compareToIgnoreCase(searchString) == 0)
			    return true;
	    }
	else
	    {
		for (int j=0;j<lst.length;j++)
		    for (int k=0;k<lst[j].langstring.length;k++)
			if (lst[j].langstring[k].string.compareToIgnoreCase(searchString) == 0)
			    return true;
	    }
      }
    return false;
  }
}


/*
   /** Recursive filter function to aid filterContent.
    *
    *  @param node the filternode to use.
    *  @return a vector with filtered content.
    
    private Vector recursiveContent(FilterNode node)
    {
	Tracer.debug("Now filtering neuron :"+node.getFilterNeuron().getURI());
	Tracer.debug("Filtertag is :"+node.getFilterTag());
      String keywords=null;
      if (node.getTop() != null)
	{
	  Vector vcons = recursiveContent(node.getTop());
	  
	  Tracer.debug("Done top-filtering");
	  Tracer.debug("Nr of preserved elements "+vcons.size());
	  Component component;
	  Iterator it=vcons.iterator();
	  for (;it.hasNext();)
	    {
	      component=(Component) it.next();
	      if (!componentPasses(component, node))
		  {
		      Tracer.debug("removing component :"+component.getURI());
		      it.remove();        
		  }
	      else    Tracer.debug("Preserving component :"+component.getURI());
	    }
	  Tracer.debug("Nr of preserved elements "+vcons.size());
	  return vcons;
	}
      else
	{
	  Vector vcons=new Vector();
	  for (int i=0; i<contents.length;i++)
	    if (componentPasses(contents[i],node))
	      vcons.addElement(contents[i]);
	  if (vcons.size()==0)
	      for (int i=0; i<contents.length;i++)
		  vcons.addElement(contents[i]);
	  return vcons;
	}
    }
*/
