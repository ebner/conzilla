/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.filter;
import java.net.URI;

import se.kth.cid.component.Resource;
import se.kth.cid.conzilla.controller.MapController;


/** The filter is a tool for structuring content in a concept map.
 *  It reads from a filterconcept that comes with a concept or a concept map.
 *
 *  @author Daniel Pettersson
 *  @version $Revision$
 */
public class ConcreteFilter extends AbstractFilter
{
    /** Constructs a ConcreteFilter.
     *
     *  @param cont the controller controlling the manager.
     *  @param filterConcept the URI as a string.
     */
    public ConcreteFilter(MapController cont, URI filterConcept)
           throws FilterException
    {
      super(cont, filterConcept);
    }

   /** Filters the content of given component with given node.
    *
    *  @param node the filternode to use.
    *  @param concept the concept whose content will be filtered.
    *  @return a vector with filtered content.

    public Vector filterContent(FilterNode node, Resource component)
    {
      setContent(component);

      return recursiveContent(node);
    }
    */

  /** Decides wheter the component should pass or not, 
   *  override to change conditions.
   *  @return true if component passes node.
   */
  public boolean componentPasses(Resource component, FilterNode node)
  {
      
      //FIXME this algorithm is outdated. Filternodes need to be redone.
/*    DataValue[] search = node.getFilterConcept().getDataValues();
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
	if(!search[i].predicateURI().equals("general_keywords"))
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
	  language=node.getFilterConcept().getMetaData().get_metametadata_language();
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
      */
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
	Tracer.debug("Now filtering concept :"+node.getFilterConcept().getURI());
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
