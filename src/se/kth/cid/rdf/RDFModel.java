/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.component.EditEvent;
import se.kth.cid.component.EditListener;
import se.kth.cid.component.FiringResource;
import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.identity.MIMEType;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.mem.ModelMem;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFException;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.SelectorImpl;
import com.hp.hpl.jena.vocabulary.RDF;

/** Should be used for keeping track of RDF-models.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class RDFModel
    extends ModelMem
    implements FiringResource, se.kth.cid.component.Container {

    /** A reference to a total model, one containing the union of all models.
     */
    //protected TotalModel totalModel;
    
    private String purpose = Container.COMMON;

    protected RDFContainerManager mm;

    /** The URI of this component.
     */
    URI componentURI;

    /** The URI used to load this component.
     */
    URI componentLoadURI;

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
    //RDFMetaDataWrapper metaData;

    /** Cache of all reifications.
     */
    Hashtable reifications;

    private String publishURL;

    /** Constructs a LocalComponent
     */
    //public RDFModel(TotalModel totalModel, RDFModelManager mm, URI uri, URI loadURI, MIMEType loadType)
    public RDFModel(
        RDFContainerManager mm,
        URI origURI,
        URI loadURI) {
        super();
        this.mm = mm;
        reifications = new Hashtable();
        editListeners = new Vector();
        componentURI = origURI;
        componentLoadURI = loadURI;
        componentLoadMIMEType = MIMEType.RDF;
    }

    /*
    public TotalModel getTotalModel()
    {
    return totalModel;
    }
    */

    public RDFContainerManager getRDFModelManager() {
        return mm;
    }

    public String getURI() {
        return componentURI.toString();
    }

    public URI getLoadURI() {
        return componentLoadURI;
    }
    
    public void setLoadURI(URI uri) {
    	this.componentLoadURI = uri;
    }
    
    public String getPublishURL() {
        return publishURL;
    }
    
    public void setPublishURL(String publishURL) {
        this.publishURL = publishURL;
    }

    public String getLoadMIMEType() {
        return componentLoadMIMEType.toString();
    }

    public boolean isEditable() {
        return isEditable;
    }

    /** Sets the editable state of this Resource. To be used with extreme care;
     *  this state is not expected to change. This function is intended to be
     *  used exclusively in the construction phase of a model.
     *
     * @param editable the new editable state.
     */
    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean b) throws ReadOnlyException {
        if (!isEditable())
            throw new ReadOnlyException("This component is read-only");

        //FIXME: use the listeners instead....
        //totalModel.modelEdited(this);
        mm.modelEdited(this);
        isEdited = b;
        if (!isEdited)
            fireEditEventNoEdit(new EditEvent(this, this, SAVED, null));
    }

    public void addEditListener(EditListener l) {
        editListeners.addElement(l);
    }

    public void removeEditListener(EditListener l) {
        editListeners.removeElement(l);
    }

    /** Fires an EditEvent to all listeners and marks the
     *  component as being edited.
     *
     *  @param e the event to fire.
     */
    public void fireEditEvent(EditEvent e) {
        isEdited = true;
        fireEditEventNoEdit(e);
    }

    /** Fires an EditEvent to all listeners without marking the component as being edited.
     *
     *  @param e the event to fire.
     */
    public void fireEditEventNoEdit(EditEvent e) {
        for (int i = 0; i < editListeners.size(); i++) {
            ((EditListener) editListeners.elementAt(i)).componentEdited(e);
        }
    }

    /** Tries to parse a URI using this Resource's URI as base URI.
     *
     *  @param uri the URI to parse.
     *  @exception InvalidURIException if the URI did not parse.
     */
    public URI tryURI(String uri) throws InvalidURIException {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new InvalidURIException(e.getMessage(), uri);
        }
    }

    public boolean isReification(String uri) {
        try {
                    com.hp.hpl.jena.rdf.model.Resource re = createResource(uri);

                                //Other reification types than statement is allowed, hence cannot check for that.
                                //However, in principle we should be able to demand that the reification type should
                                //be a subclass of RDF.Statement, for now we use the RDF.subject as indicator instead.
                    return re.hasProperty(RDF.subject);
        }  catch (RDFException rex) {}
        return false;
    }

    /** If the uri represents a reification a statement is returned weather regardless if the statement is present in the
     *  loadmodel or not.
     */
    public Statement getReification(String uri) {
        try {
                    com.hp.hpl.jena.rdf.model.Resource re = createResource(uri);

                                //Other reification types than statement is allowed, hence cannot check for that.
                                //However, in principle we should be able to demand that the reification type should
                                //be a subclass of RDF.Statement, for now we use the RDF.subject as indicator instead.
            com.hp.hpl.jena.rdf.model.Resource subject =
                re.getProperty(RDF.subject).getResource();
            Property predicate =
                getProperty(
                    re.getProperty(RDF.predicate).getResource().getURI());
            RDFNode object = re.getProperty(RDF.object).getObject();

            //This check fails only if the statement only is partial expressed in the model.
            if (subject == null || predicate == null || object == null)
                return null;

            StmtIterator stit =
                listStatements(new SelectorImpl(subject, predicate, object));
            if (stit.hasNext())
                return stit.nextStatement();
            else
                return re.getModel().createStatement(subject, predicate, object);
        } catch (RDFException rex) {}
        return null;
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
    public static String createID(Collection uniques, String uriBase) {
        String idBase = "id";

        if (uriBase != null) {
            int lastSlash = uriBase.lastIndexOf('/');

            if (lastSlash + 1 < uriBase.length())
                idBase = uriBase.substring(lastSlash + 1);
        }

        if (!(uniques.contains(idBase)))
            return idBase;

        for (int i = 1; true; i++) {
            String s = idBase + i;
            if (!(uniques.contains(s)))
                return s;
        }
    }

    //Functions required from se.kth.cid.component.Container.

    /**
     * @see se.kth.cid.component.Container#addRequestedContainerForURI(String, String)
     */
    public void addRequestedContainerForURI(String uri, String containeruri) {
        try {
            this.add(
                this.getResource(uri),
                CV.includeContainer,
                this.getResource(containeruri));
        } catch (RDFException re) {}
    }

    /**
     * @see se.kth.cid.component.Container#getRequestedContainersForURI(String)
     */
    public Collection getRequestedContainersForURI(String uri) {
        try {
            Vector vec = new Vector();
            StmtIterator sit =
                this.getResource(uri).listProperties(CV.includeContainer);
            while (sit.hasNext())
                vec.add(sit.nextStatement().getResource().getURI());
            return vec;
        } catch (RDFException re) {}
        return new Vector();
    }

    /**
     * @see se.kth.cid.component.Container#getURIsWithRequestedContainers()
     */
    public Collection getURIsWithRequestedContainers() {
        try {
            Vector vec = new Vector();
            ResIterator rit =
                this.listSubjectsWithProperty(CV.includeContainer);
            while (rit.hasNext())
                vec.add(rit.nextResource().getURI());
            return vec;
        } catch (RDFException re) {}
        return new Vector();
    }

    /**
     * @see se.kth.cid.component.Container#removeRequestedContainerForURI(String, String)
     */
    public boolean removeRequestedContainerForURI(
        String uri,
        String containeruri) {
        try {
            this.remove(
                this.createStatement(
                    this.getResource(uri),
                    CV.includeContainer,
                    this.getResource(containeruri)));
            return true;
        } catch (RDFException re) {}
        return false;
    }

    public ContainerManager getContainerManager() {
        return mm;
    }
    public void clear() {
      try { 
        StmtIterator stmts = listStatements();
        while (stmts.hasNext()) {
            stmts.next();
            stmts.remove();
        }
    } catch (RDFException e) {}
    }
    
	public List getDefinedContextMaps() {
		ArrayList maps = new ArrayList();
		SelectorImpl selector = new SelectorImpl(null, null, CV.ContextMap);
		try {
			StmtIterator si = listStatements(selector);
            
			while (si.hasNext()) {
				Resource subject = si.nextStatement().getSubject();
				maps.add(subject.toString());
			}
		} catch (RDFException e) {
			e.printStackTrace();
		}
		return maps;
	}
    public String toString() {
        return getURI().toString();
    }

    /**
     * @see se.kth.cid.component.Container#getMapsReferencingResource(java.lang.String)
     */
    public Set getMapsReferencingResource(String uri) {
        Resource resource = createResource(uri);
        Set neighbourHoodMaps = new HashSet();

        //New way:
        ResIterator ri = listSubjectsWithProperty(CV.displayResource, resource);
        while(ri.hasNext()) {
        	Resource subject = ri.nextResource();
        	Statement st = getProperty(subject, CV.inContextMap);
        	if (st != null && st.getObject() instanceof Resource 
        			&& !((Resource) st.getObject()).isAnon()) {
        		neighbourHoodMaps.add(((Resource) st.getObject()).getURI());
        	}
        }
        
        //Old way
        getMapsReferencingResource(neighbourHoodMaps, new HashSet(), resource, true);        
        
        return neighbourHoodMaps;
    }
    
    /**
     * We follow the node backwards via member properties (displayResource property in the first step) 
     * to see if we can find a map.
     * If we do not find it directly, e.g. if there are intermediate layers,
     * we call this function again recursively until we:
     * <ol><li>Find a map</li>
     * <li>run out of relations or</li>
     * <li>end up with nodes we already visited.</li>
     * </ol> 
     * 
     * @param maps a set where found maps are collected.
     * @param visitedNodes those nodes we have visited and should avoid check again, i.e. loop control.
     * @param node the node we are currently checking.
     * @param first wether it is the first level, in that case we have to check the displayResource property instead of the member relation.
     */
    private void getMapsReferencingResource(Set maps, Set visitedNodes, Resource node, boolean first) {
        SelectorImpl selector = new SelectorImpl(null, null, node);
        
        try {
            StmtIterator si = listStatements(selector);

            while (si.hasNext()) {
                Statement statement = si.nextStatement();
                Resource subject = statement.getSubject();
                
                //If we backtraced a member statement and we have not visited this subject already:
                if (!visitedNodes.contains(subject)
                    && ((first && statement.getPredicate().equals(CV.displayResource)) 
                        || ((!first && statement.getPredicate().getOrdinal() != 0 )))) {
                        
                    if (contains(subject,RDF.type,CV.ContextMap)) {
                        //We have found a map if the subject is typed as ContextMap.
                        maps.add(subject.getURI());
                    } else {
                        //The subject is not a map, but it might be an intermediate layer
                        //so we call this function again recursively.
                        visitedNodes.add(subject);
                        getMapsReferencingResource(maps, visitedNodes, subject, false);                        
                    }
                }
            }
        } catch (RDFException e) {
        }
    }

    /**
     * @see com.hp.hpl.jena.rdf.model.Model#createTypedLiteral(java.lang.String, com.hp.hpl.jena.datatypes.RDFDatatype)
     */
    public Literal createTypedLiteral(String arg0, RDFDatatype arg1) {
        return null;
    }

    /**
     * @see se.kth.cid.component.Container#setPurpose(java.lang.String)
     */
    public void setPurpose(String purpose) {
        if (purpose != null) {
            this.purpose = purpose;
        } else {
            this.purpose = Container.COMMON;
        }
    }

    /**
     * @see Container#getPurpose()
     */
    public String getPurpose() {
        return purpose;
    }
    
    public boolean isURIUsed(String uri) {
        Resource resource = createResource(uri); 
        return contains(resource, null, (RDFNode) null)
            || contains(null, null, resource);
    }
}
