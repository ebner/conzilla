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


package se.kth.cid.rdf;
import se.kth.cid.rdf.metadata.*;
import se.kth.cid.component.*;
import se.kth.cid.component.local.*;
import se.kth.cid.identity.*;
import se.kth.cid.util.*;
import java.util.*;
import org.w3c.rdf.implementation.model.*;
import org.w3c.rdf.model.*;
import org.w3c.rdf.util.*;
import org.w3c.rdf.vocabulary.rdf_syntax_19990222.RDF;
import org.w3c.rdf.vocabulary.rdf_schema_200001.RDFS;




	    //FIXME: I can't get resources to be named urn:path since:
	    //1) SAX won't accept loading with sytemId=urn:path.....
	    //2) The value of systemId is ALWAYS used by SiRPAC for namespace of
	    //   local resources(i.e. those given in model).
	    //3) Resources produced by default NodeFactory is uneditable.
	    //4) Overriding NodeFactory won't help since createResource function
	    //   to override have no information of correct namespace to be set...
	    //   (I.e. no knowledge of model or real original uri.)
	    //
	    //  Hence I let the (local) resources be without namespace and
	    //  provide a smarter lookup instead.
	    //  This hack won't be visible from ResourceComponent.
//Solved: Changed to ARP, no check for systemID to be a valid java-URL hence
//        no problem on load of model.
//Remove this comment after documented elsewhere.

/** Should be used for keeping track of RDF-models.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class ConzillaRDFModel extends ModelImpl implements Container, FiringComponent
{
    public static String S_RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    public static String S_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";


  /** A reference to a total model, one containing the union of all models.
   */ 
  protected TotalModel totalModel;

  /** The URI of this component.
   */
  URI      componentURI;

  /** The URI used to load this component.
   */
  URI      componentLoadURI;

  /** The MIME type used when loading this component.
   */
  MIMEType componentLoadMIMEType;

  /** The editListeners of this component.
   */
  Vector editListeners;

  /** Whether this component is editable.
   */
  boolean isEditable = true;
  
  /** The edited state of this component.
   */
  boolean isEdited = false;

  /** The metadata of this component.
   */
  RDFMetaData metaData;
    
  /** Cache of all reifications.
   */
  Hashtable reifications;

  /** Constructs a LocalComponent
   */
  public ConzillaRDFModel(TotalModel totalModel, URI uri, URI loadURI, MIMEType loadType)
    {
      super();
      this.totalModel = totalModel;
      reifications = new Hashtable();
      Tracer.debug("setting sourceuri to "+uri.toString());
      setSourceURI(uri.toString());
      editListeners         = new Vector();
      componentURI          = uri;
      componentLoadURI      = loadURI;
      componentLoadMIMEType = loadType;
    }

  public MetaData getMetaData()
    {
	if (metaData != null)
	    return metaData;

	metaData = new RDFMetaData(totalModel, this);
	metaData.init();
	return metaData;
    }
    
  public String getURI()
    {
      return componentURI.toString();
    }
  
  public String getLoadURI()
    {
      return componentLoadURI.toString();
    }

  public String getLoadMIMEType()
    {
      return componentLoadMIMEType.toString();
    }

  public boolean isEditable()
  {
    return isEditable;
  }

  /** Sets the editable state of this Component. To be used with extreme care;
   *  this state is not expected to change. This function is intended to be
   *  used exclusively in the construction phase of a model.
   *
   * @param editable the new editable state.
   */
  public void setEditable(boolean editable)
    {
      isEditable = editable;
    }
  

  public boolean isEdited()
  {
    return isEdited;
  }

  public void setEdited(boolean b) throws ReadOnlyException
  {
    if(!isEditable())
      throw new ReadOnlyException("This component is read-only");

    isEdited = b;
    if(!isEdited)
      fireEditEventNoEdit(new EditEvent(this, this, SAVED, null));
  }
  
  public void addEditListener(EditListener l)
  {
    editListeners.addElement(l);
  }

  public void removeEditListener(EditListener l)
  {
    editListeners.removeElement(l);
  }


  /** Fires an EditEvent to all listeners and marks the
   *  component as being edited.
   *
   *  @param e the event to fire.
   */
  public void fireEditEvent(EditEvent e)
  {
    isEdited = true;
    fireEditEventNoEdit(e);
  }

  /** Fires an EditEvent to all listeners without marking the component as being edited.
   *
   *  @param e the event to fire.
   */
  public void fireEditEventNoEdit(EditEvent e)
  {
    for(int i = 0; i < editListeners.size(); i++)
      {
	((EditListener) editListeners.elementAt(i)).componentEdited(e);
      }
  }

  /** Tries to parse a URI using this Component's URI as base URI.
   *
   *  @param uri the URI to parse.
   *  @exception InvalidURIException if the URI did not parse.
   */
  public URI tryURI(String uri) throws InvalidURIException
    {
      try {
	return URIClassifier.parseURI(uri, componentURI);
      } catch (MalformedURIException e)
	{
	  throw new InvalidURIException(e.getMessage(), uri);
	}
    }

  /** Constructs a new ID.
   *
   *  The ID is unique with respect to the Strings in the given
   *  Collection (which must not be infinite).
   *
   *  @param uniques the existing ID to not use.
   *  @param uriBase an uri (or any string) to use as base.
   *                 anything after the last '/' will be used.
   *
   *  @return a new unique ID.
   */
  public static String createID(Collection uniques, String uriBase)
    {
      String idBase = "id";
      
      if(uriBase != null)
	{
	  int lastSlash = uriBase.lastIndexOf('/');
	  
	  if(lastSlash + 1 < uriBase.length())
	    idBase = uriBase.substring(lastSlash + 1);
	}
      
      if(!(uniques.contains(idBase)))
	return idBase;
      
      for(int i = 1; true; i++)
	{
	  String s = idBase + i;
	  if(!(uniques.contains(s)))
	    return s;
	}  
    }

    /** FIXME: need this class really be a container?
     *  
     *  @return a component, null if the resource isn't mentioned in this model. 
     */
    public Component getComponent(URI uri) throws ComponentException
    {
	return null;
    }


    /** Finds all statements in this model with the given resource as subject
     *	and returns them as ReifiedResourceComponents.
     *
     *  @param subject the resource that should appear as subject in the statements.
     *  @return a HashSet containing the ReifiedResourceComponents
     *  @see ReifiedResourceComponent
     */
    public HashSet getReifiedResourceComponentCollection(Resource subject)
    {
	HashSet tot = new HashSet();
	
	Iterator it = getReifiedStatementAboutSubject(subject);
	while (it.hasNext())
	    {
		Resource re = (Resource) it.next();		    
		ReifiedResourceComponent rrc = getReifiedResourceComponent(re);
		if (rrc != null)
		    tot.add(rrc);
	    }
	return tot;
    }


    /** Finds all statements (reifications) that has the resource as subject.
     *
     *  @param r a resource that should occure as subject in the statements.
     *  @return a Iterator holding all found statements fulfilling the condition.
     */
    private Iterator getReifiedStatementAboutSubject(Resource r)
    {
	HashSet tot = new HashSet();
	try {
	    Enumeration en = find(null, RDF.subject, r).elements();
	    while (en.hasMoreElements())
		{
		    Resource re = ((Statement) en.nextElement()).subject();
		    if (!RDFUtil.isInstanceOf(this, re, RDF.Statement))
			continue;
		    tot.add(re);
		}		    
	} catch (ModelException me) {
	    Tracer.debug("ModelException!!!");
	}
	return tot.iterator();
    }

    //FIXME: Neccessary?
    public ReifiedResourceComponent[] getReifiedResourceComponents(Resource subject)
    {
	HashSet tot=getReifiedResourceComponentCollection(subject);
	ReifiedResourceComponent [] rrcv = new ReifiedResourceComponent[tot.size()];
	tot.toArray(rrcv);
	return rrcv;
    }

    /** Returns a ReifiedResourceComponent for a URI-string,
     *  if it doesn't already exists it is created provided the reffered
     *  resource is a valid Statement expressed in this model.
     *
     *  @param uri a string representation of a URI that should represent a Statement.
     *  @return a ReifiedResourceComponent, null if the given resource 
     *          isn't a valid statement in this model.
     *  @see #getReifiedResourceComponent(Resource).
     */
    public ReifiedResourceComponent getReifiedResourceComponent(String uri)
    {
	try {
	    ReifiedResourceComponent rrc = (ReifiedResourceComponent) reifications.get(uri);
	    if (rrc != null)
		return rrc;
	    else
		return getReifiedResourceComponent(getNodeFactory().createResource(uri));
	} catch (ModelException me)
	    {}
	return null;
    }


    /** Returns a ReifiedResourceComponent for a resource,
     *  if it doesn't already exists it is created provided the
     *  resource is a valid Statement expressed in this model.
     *
     *  Observe that every model keeps a cache of all ReifiedResourceComponents.
     *
     *  @param  re a Resource that should be of the type Statement.
     *  @return a ReifiedResourceComponent, null if the given resource 
     *          isn't a valid statement in this model.
     */
    public ReifiedResourceComponent getReifiedResourceComponent(Resource re)
    {
	ReifiedResourceComponent rrc = null;
	try {
	    rrc = (ReifiedResourceComponent) reifications.get(re.getURI());
	    if (rrc != null)
		return rrc;
	} catch (ModelException me)
	    {}

	try {
	    Resource subject   = RDFUtil.getObjectResource(this, re, RDF.subject);
	    Resource predicate = RDFUtil.getObjectResource(this, re, RDF.predicate);
	    RDFNode object    = RDFUtil.getObject(this, re, RDF.object);
       		    
	    if (subject !=null && predicate != null && object != null) 
		{
		    rrc=new ReifiedResourceComponent(totalModel, this, 
				re, URIClassifier.parseValidURI(re.getURI()));
		    reifications.put(re.getURI(), rrc);
		    return rrc;
		}
	} catch (ModelException me)
	    {}
	return null;
    }

}
