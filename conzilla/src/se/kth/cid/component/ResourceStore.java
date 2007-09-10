/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import se.kth.cid.collaboration.CollaborillaReader;
import se.kth.cid.collaboration.CollaborillaSupport;
import se.kth.cid.collaboration.MetaDataCache;
import se.kth.cid.component.cache.ComponentCache;
import se.kth.cid.concept.Concept;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.install.Installer;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.identity.MIMEType;
import se.kth.cid.layout.ContextMap;

/** ResourceStore is the central place for component handling and caching.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ResourceStore {

    /**
     * The cache to use.
     */
    ComponentCache cache;
    
    /**
     * Metadata cache with information from Collaborilla.
     */
    MetaDataCache metaCache;

    ComponentFactory componentManager;

    //PathURNResolver resolver;

    /**
	 * Creates a ResourceStore with the given resolver and cache.
	 * 
	 */
	public ResourceStore(ComponentCache cache, MetaDataCache metaCache,
			ComponentFactory componentManager) {
		this.cache = cache;
		this.componentManager = componentManager;
		this.metaCache = metaCache;
	}
    
    public void refresh() {
        this.getContainerManager().refreshCacheOfContainers(this.getCache());
        cache.clear();
    }

    /**
     * Returns the cache of this ResourceStore.
     *
     *  @return the cache of this ResourceStore.
     */
    public ComponentCache getCache() {
        return cache;
    }
    
    public MetaDataCache getMetaDataCache() {
		return metaCache;
	}

	public ContainerManager getContainerManager() {
		return this.componentManager.getContainerManager();
	}

	public ComponentFactory getComponentManager() {
		return this.componentManager;
	}

    /**
	 * Loads a Concept, referencing it in the cache. If the component already is
	 * in the cache, that component is returned.
	 * 
	 * @param uri
	 *            the URI of the component to load.
	 * @return the loaded component. Never null.
	 * @exception ComponentException
	 *                if the component could not be loaded, or was no concept.
	 */
    public Concept getAndReferenceConcept(URI uri) throws ComponentException {
       	Component c = cache.getComponent(uri.toString());
        if (c != null) {
            if (c instanceof Concept) {
            	return (Concept) c;
            }
            throw new ComponentException("The URI for the requested Concept is already in use and is not a Concept, the URI requested was: "+uri);
        }
        Concept concept = this.componentManager.loadConcept(uri);
        cache.referenceComponent(concept);
        return concept;
    }

    /** Loads a ConceptMap, referencing it in the cache.
     *  If the ConceptMap already is in the cache, that ConceptMap is returned.
     *
     *  @param uri the URI of the component to load.
     *  @return the loaded component. Never null.
     *  @exception ComponentException if the component
     *             could not be loaded, or was no ConceptMap.
     */
    public ContextMap getAndReferenceConceptMap(URI uri)
        throws ComponentException {
    	Component c = cache.getComponent(uri.toString());
        if (c != null) {
            if (c instanceof ContextMap) {
            	return (ContextMap) c;
            }
            throw new ComponentException("The URI for the requested ContextMap is already in use and is not a ContextMap, the URI requested was: "+uri);
        }
        
		CollaborillaSupport cs = new CollaborillaSupport(ConfigurationManager.getConfiguration());
		CollaborillaReader collaborillaReader = new CollaborillaReader(cs);
		Set required = collaborillaReader.getRequiredContainers(uri);
		if (required != null) {
			for (Iterator requiredIt = required.iterator(); requiredIt.hasNext(); ) {
				String requiredContainerURI = (String) requiredIt.next();
				getAndReferenceContainer(URI.create(requiredContainerURI), false);
			}
		}

		ContextMap cMap = this.componentManager.loadContextMap(uri, true);
		if (cMap != null) {
			cache.referenceComponent(cMap);
		}
		return cMap;
    }
    
    public ContextMap getAndReferenceLocalContextMap(URI uri, Session session) throws ComponentException {
    	Component c = cache.getComponent(uri.toString());
        if (c != null) {
            if (c instanceof ContextMap) {
            	return (ContextMap) c;
            }
            throw new ComponentException("The URI for the requested ContextMap is already in use and is not a ContextMap, the URI requested was: " + uri);
        }
        
		URI conceptContainerURI = URI.create(session.getContainerURIForConcepts());
		URI layoutContainerURI = URI.create(session.getContainerURIForLayouts());
		getAndReferenceContainer(conceptContainerURI);
        getAndReferenceContainer(layoutContainerURI);
        
		ContextMap cMap = componentManager.loadContextMap(uri, false);
		if (cMap != null) {
			cache.referenceComponent(cMap);
		}
		
		return cMap;
    }

    /** Loads any Component, referencing it in the cache.
     *  If the Component already is in the cache, that Component is returned.
     *
     *  @param uri the URI of the component to load.
     *  @return the loaded component. Never null.
     *  @exception ComponentException if the component
     *             could not be loaded.
     */
    public Component getAndReferenceComponent(URI uri)
        throws ComponentException {
        Component c = cache.getComponent(uri.toString());
        if (c != null) {
            return c;
        }

        c = this.componentManager.loadComponent(uri);

        if (c != null) {
            cache.referenceComponent(c);
            return c;
        }

        throw new ComponentException("Component could not be found:\n" + uri);
    }

    public Container getAndReferenceContainer(URI uri) 
        throws ComponentException {
        return getAndReferenceContainer(uri, false);
    }

    /** Loads any ContainerComponent, referencing it in the cache.
     *  If the container already is in cache, that container is returned.
     *
     *  @param uri the URI of the component to load.
     *  @return the loaded component. Never null.
     *  @exception ComponentException if the component
     *             could not be loaded.
     */
    public Container getAndReferenceContainer(URI uri, boolean createIfMissing)
        throws ComponentException {
        Container c = this.getContainerManager().getContainer(uri.toString());
        if (c != null) {
            return c;
        }

        if (isConzillaURI(uri)) {
            File file = resolveURIToFile(uri);        	
            if (file != null) {
            	return this.getContainerManager().loadContainer(uri, file.toURI(), createIfMissing, this.getCache());            	
            }
        } else {
        	try {
        		if (isPathURN(uri)) {
        			File file = resolveURIToFile(uri);        	
        			return this.getContainerManager().loadContainer(uri, file.toURI(), createIfMissing, this.getCache());
        		} else {
        			return this.getContainerManager().loadContainer(uri, uri, createIfMissing, this.getCache());
        		}
        	} catch (ComponentException e) {
                Container cont = this.getContainerManager().loadPublishedContainer(uri, cache);
                if (cont != null) {
                	return cont;
                }
        	}
        }
        
        throw new ComponentException(
            new ComponentException("Failed loading of '" + uri + "', neither locally available, " +
            		"remotely available directly (as for ftp or http) nor resolvable via collaborilla"));
    }
    
    /** Tests if a component with this URI can be created.
     *  This is a possibly heavy-weight operation, involving not only a URI resolve,
     *  but also interaction with the server.
     *
     *  @return An array containing the URI that should be used in a createComponent call,
     *          and the MIME-type.
     *  @param uri URI of the new component to check.
     *  @exception ComponentException if anything stops us from creating the component.
     */
    public Object[] checkCreateContainer(URI uri) throws ComponentException {
    	File file = resolveURIToFile(uri);
    	if (file != null) {
    		URI furi = file.toURI();
            this.getContainerManager().checkCreateContainer(furi);
            return new Object[] { furi, MIMEType.RDF };
    	} else {
            this.getContainerManager().checkCreateContainer(uri);
            return new Object[] { uri, MIMEType.RDF };
    	}
    }
    
    public void checkLoadContainer(URI uri) throws ComponentException {
    	File file = resolveURIToFile(uri);
    	if (file != null) {
    		this.getContainerManager().checkLoadContainer(file.toURI());
    	} else {
    		this.getContainerManager().checkLoadContainer(uri);
    	}
    }
    
    public Container createContainer(URI originalURI, URI resolvedURI, MIMEType mt) throws ComponentException {
        return this.getContainerManager().createContainer(originalURI, resolvedURI, this.getCache());
    }
    
    public File resolveURIToFile(URI uri) {
    	String suri = uri.toString();
    	if (isPathURN(uri)) {
    		File cdir = Installer.getConzillaDir();
    		int slash = suri.lastIndexOf('/');
    		String path = suri.substring(10, slash);
    		path = path.replace('/', '_');
    		File file = new File(cdir, Installer.LOCALDIR+"/"+path+suri.substring(slash));
    		return file;
    	} else if (isConzillaURI(uri)) {
    		File cdir = Installer.getConzillaDir();
    		String path = suri.substring(11);
    		File file = new File(cdir, path);
    		return file;
    	}
    	return null;
    }
    
    private boolean isConzillaURI(URI uri) {
    	return uri.toString().startsWith("conzilla://");
    }	

    private boolean isPathURN(URI uri) {
    	return uri.toString().startsWith("urn:path:/");
    }
}