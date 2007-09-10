/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.component.Component;
import se.kth.cid.component.ComponentManager;
import se.kth.cid.component.Container;
import se.kth.cid.component.EditEvent;
import se.kth.cid.component.FiringResourceImpl;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.identity.MIMEType;
import se.kth.cid.util.Tracer;
import se.kth.nada.kmr.shame.util.RDFUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFException;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.rdf.model.impl.SelectorImpl;
import com.hp.hpl.jena.vocabulary.RDF;

/** This class wraps RDF-resources spanning several models.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class RDFComponent
	extends FiringResourceImpl
    implements se.kth.cid.component.Component {

	public static final Log LOG = LogFactory.getLog(RDFComponent.class);

	protected RDFComponentManager rcm;
	
    /**
     * Local cache of load model.
     */
    protected RDFModel model;

    /**
     * Local cache of the Jena resource representing this RDFComponent.
     */
    protected Resource resource;

	/** A cache of the type of this resource.
     */
    protected String type = null;

	private boolean updating;
        
    public RDFComponent(URI uri) {
    	super(uri, uri, MIMEType.RDF);
    }
        
    /**
     * Initializes stuff, use when to create a new component.
     * It initalizes which model that should be the load model for
     * this RDFComponent. Defaults is to use the current concept container found
     * through the {@link RDFContainerManager#getCurrentConceptContainer()}.
     */
    public void initialize(RDFComponentManager rcm, RDFModel initializeInModel) {
        this.rcm = rcm;
        initializeInModel(initializeInModel);
//        refreshRelevantContainers();
        setEdited(true);
    }

    /**
     * Should be called whenever the resource is loaded from model, or whenever
     * this RDFComponent is out of phase. First initUpdate is called, then
     * updateModel for each relevant model and finally endUpdate.
     */
    public void update(RDFComponentManager rcm) {
    	updating = true;
    	this.rcm = rcm;

        initUpdate();

        Iterator it = rcm.getLoadedRelevantContainers().iterator();
        while (it.hasNext()) {
            RDFModel m = (RDFModel) rcm.getContainer((URI) it.next());
            updateFromModel(m);
        }
        endUpdate();
    	updating = false;
    }
    
    protected boolean isUpdating() {
    	return updating;
    }
    /**
     * Called to accomplish initialize a RDFComponent in the the given RDFModel.
     */
    protected void initializeInModel(RDFModel model) {
    	this.model = model;
    	rcm.containerIsRelevant(URI.create(model.getURI()));
    }


    /**
     * Typically updating of stuff that was initialized with initializeInModel
     * when this RDFComponent was created (and initialized) - not necessarily in
     * this conzilla session.
     */
    protected void initUpdate() {
        type = null;
		rcm.refresh();
//        refreshRelevantContainers();
    }

    /**
     * Here this RDFComponent has a chance to load information from the given
     * model, called from {@link RDFComponent#update()}.
     */
    protected void updateFromModel(RDFModel m) {
    }

    /**
     * Similar to {@link #initUpdate()}but after the RDFComponent has been loaded
     * from all the relevant models.
     */
    protected void endUpdate() {
    }
    
    public void removeFromAllRelevantModels() {
        Iterator it = rcm.getLoadedRelevantContainers().iterator();
        while (it.hasNext())
            removeFromModel((RDFModel) rcm.getContainer((URI) it.next()));
    }

    protected void removeFromModel(RDFModel model) {
        RDFUtil.remove(model, model.createResource(getURI().toString()));
        model.setEdited(true);
    }

    public List getAttributeEntry(String attribute) {
        Vector attval = new Vector();
        try {
            Iterator it = rcm.getLoadedRelevantContainers().iterator();
            while (it.hasNext()) {
            	URI uri = (URI) it.next();
                RDFModel m = (RDFModel) rcm.getContainer(uri);
                StmtIterator stmt = getPropertiesFromModel(m, attribute);
                while (stmt.hasNext())
                    attval.add(new RDFAttributeEntry(m, this, stmt.nextStatement()));
            }
        } catch (RDFException re) {}
        return attval;
    }

    /**
     * @see se.kth.cid.component.Component#getAttributeEntry(java.lang.String, java.lang.String, Boolean, java.lang.String)
     */
    public AttributeEntry getAttributeEntry(String attribute, String value, Boolean isValueAURI, String containerURI) {
        try {
            if (containerURI != null) {
                RDFModel m = (RDFModel) rcm.getContainer(URI.create(containerURI));
                if (m == null) {
                    return null;
                }
                StmtIterator stmt = getPropertiesFromModel(m, attribute, value, isValueAURI);
                if (stmt.hasNext()) {
                    return new RDFAttributeEntry(m, this, stmt.nextStatement());
                }
            } else {
                Iterator it = rcm.getLoadedRelevantContainers().iterator();
                while (it.hasNext()) {
                    URI uri = (URI) it.next();
                    RDFModel m = (RDFModel) rcm.getContainer(uri);
                    StmtIterator stmt = getPropertiesFromModel(m, attribute, value, isValueAURI);
                    if (stmt.hasNext()) {
                        return new RDFAttributeEntry(m, this, stmt.nextStatement());
                    }
                }
            }
        } catch (RDFException re) {}
        return null;
    }
    
    /** Fetches all tripples with the given attribute as a predicate and
     *  this RDFComponent as subject. They are returned as pairs with the
     *  model they occured in.
     *  
     * @see AttributeEntry
     * @see Component#addAttributeEntry(String, Object)
     */
    public AttributeEntry addAttributeEntry(String attribute, Object value)
        throws ReadOnlyException {
        Resource re = getResource();
        RDFModel m = (RDFModel) rcm.getCurrentConceptContainer();
        AttributeEntry ae = RDFAttributeEntry.addStatement(
            m,
            this,
            re,
            attribute,
            value);
       rcm.containerIsRelevant(URI.create(m.getURI()));
       
       fireEditEvent(new EditEvent(this, rcm.getCurrentConceptContainer(), Component.ATTRIBUTES_EDITED, attribute));
       return ae;
    }

    /**
     * @see se.kth.cid.component.Component#removeAttributeEntry(se.kth.cid.component.AttributeEntry)
     */
    public void removeAttributeEntry(AttributeEntry ae)
        throws ReadOnlyException {
        ae.remove();
        rcm.refresh();
        fireEditEvent(new EditEvent(this, rcm.getCurrentConceptContainer(), Component.ATTRIBUTES_EDITED, this));
    }
    
    public ComponentManager getComponentManager() {
        return rcm;
    }

    public String getType() {
        if (type != null)
            return type;
    
        RDFModel model = getLoadModel();
        if (model != null) {
        	Statement stmt = model.getResource(getURI()).getProperty(RDF.type);
            if (stmt != null) {
            	type = stmt.getResource().getURI();
            } else {
            	return null;
            }
        }
        
        return type;
    }

    public void remove() {
        Iterator it = rcm.getLoadedRelevantContainers().iterator();
        while (it.hasNext()) {
        	URI uri = (URI) it.next();
        	RDFModel model = (RDFModel) rcm.getContainer(uri);
        	if (model.isEditable()) {
        		removeFromModel(model);
        	}
        }
    }    
	
    public StmtIterator getProperties(Property prop) {
        //return getPropertiesFromModel(totalModel.getTotalRDFModel(), prop);
        return getPropertiesFromModel(getLoadModel(), prop.toString());
    }
    
    public StmtIterator getProperties(String prop) {
        //return getPropertiesFromModel(totalModel.getTotalRDFModel(), prop);
        return getPropertiesFromModel(getLoadModel(), prop);
    }

    protected StmtIterator getPropertiesFromModel(Model model, String prop) {
    	if (model == null) {
    		return null;
    	}
        try {
            Resource subject = model.getResource(getURI());
            Property property = prop != null ? model.getProperty(prop) : null;
            return model.listStatements(new SelectorImpl(subject, property,
                    (RDFNode) null));
        } catch (RDFException re) {
            Tracer.debug("failed fetching property " + prop
                    + " for resource" + getURI() + "\n" + re.getMessage());
        }
        return null;
    }

    protected StmtIterator getPropertiesFromModel(Model model, String prop, String obj, Boolean isObjectListeral) {
        try {
            Resource subject = model.getResource(getURI());
            Property property = model.getProperty(prop);
            RDFNode object = null;
            if (isObjectListeral != null) {
                object = isObjectListeral.booleanValue() ? (RDFNode) model.createLiteral(obj) : model.createResource(obj);
                return model.listStatements(new SelectorImpl(subject, property, object));
            } else {
                object = model.createLiteral(obj);             
                StmtIterator stmt = model.listStatements(new SelectorImpl(subject, property, object));
                if (stmt.hasNext()) {
                    return stmt;
                } else {
                    object = model.createResource(obj);
                    return model.listStatements(new SelectorImpl(subject, property, object));
                }
            } 
        } catch (RDFException re) {
            Tracer.debug("failed fetching property " + prop
                    + " for resource" + getURI() + "\n" + re.getMessage());
        }
        return null;
    }
    
    /**
     * Returns the Jena Component corresponding to this RDFComponent.
     */
    public Resource getResource() {
        RDFModel model = getLoadModel();
        if (resource == null) {
            if (model != null)
                try {
                    resource = model.getResource(getURI());
                    return resource;
                } catch (RDFException re) {
                }
            resource = new ResourceImpl(getURI());
        }
        return resource;
    }


    /**
     * Overload for correct type handling....
     */
    public RDFModel getLoadModel() {
        if (model == null) {
            Container cont = rcm.getContainerManager().findLoadContainerForResource(this);
            if (! (cont instanceof RDFModel)) {
            	LOG.fatal("LoadModel is not a RDFModel.");
            }
            if (cont == null) {
                cont = rcm.getCurrentConceptContainer();
            }
            model = (RDFModel) cont;
        }

        return model;
    }

    /**
     * @see se.kth.cid.component.Component#getLoadContainer()
     */
    public String getLoadContainer() {
        return getLoadModel().getURI();
    }
    
	public String toString() {
		return getURI();
	}
}
