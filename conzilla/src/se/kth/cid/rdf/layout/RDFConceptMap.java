/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf.layout;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import se.kth.cid.component.EditEvent;
import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.layout.BookkeepingConceptMap;
import se.kth.cid.layout.BookkeepingDrawerLayout;
import se.kth.cid.layout.BookkeepingResourceLayout;
import se.kth.cid.layout.BookkeepingStatementLayout;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.LayerEvent;
import se.kth.cid.layout.LayerLayout;
import se.kth.cid.layout.LayerListener;
import se.kth.cid.layout.LayerManager;
import se.kth.cid.layout.ResourceLayout;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.layout.generic.MemLayerManager;
import se.kth.cid.notions.ContentInformation;
import se.kth.cid.rdf.CV;
import se.kth.cid.rdf.RDFComponentManager;
import se.kth.cid.rdf.RDFModel;
import se.kth.cid.util.TagManager;
import se.kth.cid.util.Tracer;

import com.hp.hpl.jena.rdf.model.RDFException;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/** This is a.layout wich builds on top of a RDFModel.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class RDFConceptMap
    extends RDFResourceLayout
    implements BookkeepingConceptMap, LayerListener {

	ContextMap.Dimension dimension;
    RDFLayerManager layerManager;
    DrawerLayout[] drawerLayouts;
    HashMap concept2ContentInformation;

    class RDFLayerManager extends MemLayerManager {
        public RDFLayerManager(LayerLayout layers, TagManager ttm) {
            super(layers, ttm);
        }
        protected LayerLayout createLayerImpl(
            String id,
            Object tag,
            ContextMap cMap) {
            //The tag is here sometimes a string from the MemLayerManager default managment when no tag is given,
            //hence we ignore it and let the RDFTreeTagManager use the currentModel instead wich of course
            //is always the right way to go. I.e. we don't need to set the tag via setTag method since the
            //constructor does it.
            RDFResourceLayout mgsrc = null;
            try {
                //FIXME: separate id and title!!!!
                //mgsrc = new RDFResourceLayout(totalModel, mm, RDFConceptMap.this, URIClassifier.parseURI(id), null);
            	URI idURI = null;
            	if (id == null) {
                	idURI = rcm.createUniqueLayoutURI();
                } else {
                	idURI = new URI(id);
                }
            	mgsrc =
                    new RDFResourceLayout(
                    		idURI,
                    		RDFConceptMap.this,
                    		null);
            } catch (URISyntaxException mue) {
                mgsrc = new RDFResourceLayout(
                			rcm.createUniqueLayoutURI(),
                			RDFConceptMap.this,
                			null);
            }
            mgsrc.initialize(new RDFComponentManager(rcm.getComponentFactory(), 
            		URI.create(mgsrc.getURI()), rcm.getTagManager(), false), rcm.getCurrentLayoutContainer());
  //          mgsrc.initialize(rcm, rcm.getCurrentLayoutContainer());
            return mgsrc;
        }
    }

    /** 
     * Constructs a ContextMap with the given URI.
     * Call {@link #initialize(RDFComponentManager, RDFModel)} when the 
     * ContextMap is to be created, {@link #update(RDFComponentManager)} otherwise.
     *
     *  @param mapURI the URI of this map.
     */
    public RDFConceptMap(URI mapURI) throws RDFException {
        super(mapURI, null, CV.ContextMap);
        //null above is a special case,.layout need not be asked for it's.layout.....
        //if this results in errors, do extra function 'setConceptMap' in RDFResourceLayout.
    }

    public void initialize(RDFComponentManager rcm, RDFModel initializeInModel) {
        layerManager = new RDFLayerManager(this, rcm.getTagManager());
        //	layerManager.addLayerListener(this);
		// TODO Auto-generated method stub
		super.initialize(rcm, initializeInModel);
	}

    protected void initializeInModel(RDFModel model) {
        super.initializeInModel(model);
        setDimension(new ContextMap.Dimension(200, 200));
    }

    
    public void update(RDFComponentManager rcm) {
    	if (layerManager == null) {
    		layerManager = new RDFLayerManager(this, rcm.getTagManager());
    	}
		super.update(rcm);
	}

	protected void initUpdate() {
        super.initUpdate();
        drawerLayouts = null;
        loadConceptMap();
    }

    protected void endUpdate() {
        super.endUpdate();
        //recursiveUpdate(this);
        fixBookeeping();

        Enumeration en = children();
        if (en.hasMoreElements())
            layerManager.setEditGroupLayout(
                ((LayerLayout) en.nextElement()).getURI());
    }

    protected void fixBookeeping() {
        //Let's fix the Bookeeping.
        //Hash the Layouts in this map temporarily so we can access them easily.
        Hashtable ht = new Hashtable();
        Vector v = getChildren(IGNORE_VISIBILITY, DrawerLayout.class);
        Enumeration en = v.elements();
        while (en.hasMoreElements()) {
            BookkeepingDrawerLayout bkrl =
                (BookkeepingDrawerLayout) en.nextElement();
            ht.put(bkrl.getURI(), bkrl);

            //Remove old bookkeeping.
            StatementLayout[] sl;

            sl = bkrl.getSubjectOfStatementLayouts();
            for (int i = sl.length - 1; i >= 0; i--)
                bkrl.removeSubjectOfStatementLayout(sl[i]);
            sl = bkrl.getObjectOfStatementLayouts();
            for (int i = sl.length - 1; i >= 0; i--)
                bkrl.removeObjectOfStatementLayout(sl[i]);
                
           if (bkrl instanceof BookkeepingStatementLayout) {
               ((BookkeepingStatementLayout) bkrl).setSubjectLayout(null);
               ((BookkeepingStatementLayout) bkrl).setObjectLayout(null);
           }
        }
        
        en = v.elements();
        while (en.hasMoreElements()) {
            DrawerLayout dl = (DrawerLayout) en.nextElement();
            if (!(dl instanceof StatementLayout))
                continue;
            BookkeepingStatementLayout sl = (BookkeepingStatementLayout) dl;
            BookkeepingDrawerLayout subject =
                (BookkeepingDrawerLayout) ht.get(sl.getSubjectLayoutURI());
            BookkeepingDrawerLayout object =
                (BookkeepingDrawerLayout) ht.get(sl.getObjectLayoutURI());
            if (subject != null) {
                sl.setSubjectLayout(subject);
                subject.addSubjectOfStatementLayout(sl);
            }
            if (object != null) {
                sl.setObjectLayout(object);
                object.addObjectOfStatementLayout(sl);
            }
        }
    }

/*    private void recursiveUpdate(RDFResourceLayout mgsrc) {
        Iterator it = mgsrc.getChildren().iterator();
        while (it.hasNext()) {
            RDFResourceLayout os = (RDFResourceLayout) it.next();
            os.update(rcm);
            if (!os.isLeaf())
                recursiveUpdate(os);
        }
    }*/

    private void loadConceptMap() {
        try {
            com.hp.hpl.jena.rdf.model.Resource object =
                getLoadModel().getResource(getURI());

            Statement pdim = object.getProperty(CV.dimension);
            if (pdim != null) {
                StringTokenizer stdim = new StringTokenizer(pdim.getString(), ",");
                if (stdim.countTokens() >= 2) {
                    dimension =
                        new ContextMap.Dimension(
                            Integer.parseInt(stdim.nextToken()),
                            Integer.parseInt(stdim.nextToken()));
                }
            }
            if (dimension == null) {
                dimension = new ContextMap.Dimension(200, 200);
            }
            //this will result in a mismatch between object model and RDFModel.
        } catch (RDFException re) {
            dimension = new ContextMap.Dimension(200, 200);
            Tracer.debug(
                "RDFConceptMap: Failed updating of ConceptLayout information from model, setting default dimension, "
                    + re.getMessage());
        }
    }

    /////////////ConceptMap/////////////

    public ContextMap.Dimension getDimension() {
        return dimension;
    }

    public void setDimension(ContextMap.Dimension dim)
        throws ReadOnlyException {
        if (!isEditable())
            throw new ReadOnlyException("Read only model");

        try {
            com.hp.hpl.jena.rdf.model.Resource object =
                getLoadModel().getResource(getURI());
            ContextMap.Dimension olddim = getDimension();

            //If the new dimension is the same as the old, do nothing.
            if (olddim == dim
                || (olddim != null
                    && dim != null
                    && olddim.width == dim.width
                    && olddim.height == dim.height))
                return;

            try {
                if (olddim != null) {
                    Statement st = object.getProperty(CV.dimension);
                    if (st != null)
                        st.remove();
                }
            } catch (RDFException re) {}

            if (dim != null) {
                String dimension =
                    Integer.toString(dim.width)
                        + ","
                        + Integer.toString(dim.height);
                object.addProperty(CV.dimension, dimension);
            }
            dimension = dim;
            getLoadModel().setEdited(true);
            setEdited(true);
            fireEditEvent(new EditEvent(this, this, DIMENSION_EDITED, dim));
        } catch (RDFException re) {}
    }

    /////////////ConceptLayout////////////

    public LayerManager getLayerManager() {
        return layerManager;
    }

    public DrawerLayout[] getDrawerLayouts() {
        if (drawerLayouts == null) {
            Vector v = getChildren(ONLY_VISIBLE, DrawerLayout.class);
            drawerLayouts = new DrawerLayout[v.size()];
            v.toArray(drawerLayouts);
        }

        return drawerLayouts;
    }

    public ResourceLayout getResourceLayout(String mapID) {
        return (ResourceLayout) recursivelyGetChild(mapID);
    }

    public ConceptLayout addConceptLayout(String conceptURI)
        throws ReadOnlyException, InvalidURIException {
        RDFConceptLayout nsrc = null;
        if (conceptURI != null) {
        	nsrc = new RDFConceptLayout(rcm.createUniqueLayoutURI(), this, conceptURI);
        	nsrc.initialize(new RDFComponentManager(rcm.getComponentFactory(), 
        			URI.create(nsrc.getURI()), rcm.getTagManager(), false), rcm.getCurrentLayoutContainer());
        } else {
        	return null;
        }

        //The tag is already set in the constructor of RDFTreeTagNode to the current model.
        //nsrc.setTag(mm.getCurrentModel());
        layerManager.addResourceLayout(nsrc, null);
        drawerLayouts = null;
        fixBookeeping();
        setEdited(true);
        return nsrc;
    }

    public StatementLayout addStatementLayout(
    		String concepturi,
    		String subjectLayouturi,
    		String objectLayouturi)
    throws ReadOnlyException, InvalidURIException {
    	RDFStatementLayout asrc = null;
    	if (concepturi != null) {
    		if (objectLayouturi != null) {
    			asrc = new RDFStatementLayout(
    					rcm.createUniqueLayoutURI(),
    					RDFConceptMap.this,
    					concepturi,
    					subjectLayouturi,
    					objectLayouturi);
    		} else {
    			asrc = new RDFLiteralStatementLayout(
    					rcm.createUniqueLayoutURI(),
    					RDFConceptMap.this,
    					concepturi,
    					subjectLayouturi);
    		}
    	} else {
    		return null;
    	}

    	asrc.initialize(new RDFComponentManager(rcm.getComponentFactory(), URI.create(asrc.getURI()), rcm.getTagManager(), false), rcm.getCurrentLayoutContainer());
    	layerManager.addResourceLayout(asrc, null);
    	drawerLayouts = null;

    	fixBookeeping();
    	setEdited(true);
    	return asrc;
    }

    public void remove() throws ReadOnlyException {
        recursivelyRemoveFromAllRelevantModels();
        RDFModel m = getLoadModel();
        for (Iterator conts = m.getRequestedContainersForURI(getURI()).iterator();
            conts.hasNext();) {
            String containerURI = (String) conts.next();
            m.removeRequestedContainerForURI(getURI(), containerURI);   
        }
//        rcm.refresh();
    }
    
    //From BookkeepingConceptMap
    public void removeResourceLayout(BookkeepingResourceLayout brl) {
        layerManager.removeResourceLayout(brl);
        drawerLayouts = null;
        fixBookeeping();
    }

    public void layerChange(LayerEvent event) {
        drawerLayouts = null;
        fixBookeeping();
    }

    private void initContentInformation() {
        concept2ContentInformation = new HashMap();
        
        for (Iterator models = rcm.getLoadedRelevantContainers().iterator(); models.hasNext();) {
            RDFModel m = (RDFModel) rcm.getContainer((URI) models.next());
            StmtIterator stmts = m.listStatements(m.createResource(getURI()), CV.includes, (RDFNode) null);
            while (stmts.hasNext()) {
                ContentInContext cic = new ContentInContext(URI.create(stmts.nextStatement().getResource().toString()), this, null, null, null);
                cic.update(rcm);
                addContentInContext(cic);
            }
        }
    }
    
    private void addContentInContext(ContentInContext cic) {
        Set contentInformationList = (Set) concept2ContentInformation.get(cic.getConceptURI());
        if (contentInformationList == null) {
            contentInformationList = new LinkedHashSet();
            concept2ContentInformation.put(cic.getConceptURI(), contentInformationList);
        }
        contentInformationList.add(cic);        
    }

    /**
     * @see se.kth.cid.notions.Context#addContentInContext(String, String, String)
     */
    public ContentInformation addContentInContext(String conceptURI, String CCRelation, String contentURI) throws ReadOnlyException, InvalidURIException {
        if (concept2ContentInformation == null) {
            initContentInformation();
        }
        ContentInContext cc = new ContentInContext(
                                                    rcm.createUniqueLayoutURI(),
                                                    RDFConceptMap.this,
                                                    conceptURI,
                                                    CCRelation,
                                                    contentURI);
    	cc.initialize(new RDFComponentManager(rcm.getComponentFactory(), 
    			URI.create(cc.getURI()), null, false), rcm.getCurrentLayoutContainer());
        addContentInContext(cc);
        return cc;
    }

    /**
     * @see se.kth.cid.notions.Context#removeContentInContext(se.kth.cid.notions.ContentInformation)
     */
    public void removeContentInContext(ContentInformation cc) {
        concept2ContentInformation = null;        
        ContentInContext cic = (ContentInContext) cc;
        cic.removeFromAllRelevantModels();
    }

    /**
     * @see se.kth.cid.notions.Context#getContentInContextForConcept(java.lang.String)
     */
    public Set getContentInContextForConcept(String conceptURI) {
        if (concept2ContentInformation == null) {
            initContentInformation();
        }
        Set set = (Set) concept2ContentInformation.get(conceptURI); 
        return set != null ? set : new HashSet();
    }

	public void refresh() {
		update(rcm);
        fireEditEvent(new EditEvent(this, this, CONTEXTMAP_REFRESHED, null));		
	}
}
