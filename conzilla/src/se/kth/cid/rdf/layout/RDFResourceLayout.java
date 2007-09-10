/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf.layout;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;

import se.kth.cid.component.EditEvent;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.layout.BookkeepingResourceLayout;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.LayerLayout;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.rdf.CV;
import se.kth.cid.rdf.RDFComponentManager;
import se.kth.cid.rdf.RDFModel;
import se.kth.cid.rdf.RDFTreeTagNode;
import se.kth.cid.tree.TreeTagNode;
import se.kth.cid.util.Tracer;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * This is an RDF-ResourceLayout.
 * 
 * @author Matthias Palmer
 * @version $Revision$
 */
public class RDFResourceLayout extends RDFTreeTagNode implements LayerLayout,
		BookkeepingResourceLayout {
	protected RDFConceptMap conceptMap;

	Vector objectOfTriples;

	Vector subjectOfTriples;

	protected boolean seekForChildren = true;

	public RDFResourceLayout(URI uri, RDFConceptMap cMap, Resource nodeType) {
		super(uri, CV.displayResource, nodeType != null ? nodeType
				: CV.NodeLayout);
		if (cMap == null)
			if (this instanceof RDFConceptMap)
				conceptMap = (RDFConceptMap) this;
			else
				Tracer.bug("Within constructor for RDFResourceLayout: "
								+ "argument containing conceptmap is null and this object isn't a conceptmap...."
								+ "In short, buggging out in desperate need for a ConceptMap.");
		else
			conceptMap = cMap;
		subjectOfTriples = new Vector();
		objectOfTriples = new Vector();
	}

	protected void initializeInModel(RDFModel model) {
		// TODO Auto-generated method stub
		super.initializeInModel(model);
		RDFModel m = getCurrentModel();
    	if (conceptMap != this) {
    		m.add(m.createStatement(
    				m.createResource(getURI()), 
    				CV.inContextMap, 
    				(RDFNode) m.createResource(conceptMap.getURI())));
    	}
	}

	public ContextMap getConceptMap() {
		return conceptMap;
	}

	public void setEdited(boolean value) {
		if (value == isEdited())
			return;
		if (value == true && conceptMap != this)
			conceptMap.setEdited(true);

		super.setEdited(value);
	}

	public void remove() throws ReadOnlyException {
		// FIXME: WHAT SHOULD THE CHECK BE??
		// if (!conceptMap.isEditable())
		// throw new ReadOnlyException("");

		if (conceptMap != null)
			conceptMap.removeResourceLayout(this);
		super.remove();
	}

	public final void addObjectOfStatementLayout(StatementLayout as) {
		if (as != this) {
			objectOfTriples.add(as);
		}
	}

	public void addSubjectOfStatementLayout(StatementLayout as) {
		subjectOfTriples.add(as);
	}

	public void removeObjectOfStatementLayout(StatementLayout as) {
		if (as != this) {
			objectOfTriples.remove(as);
		}
	}

	public void removeSubjectOfStatementLayout(StatementLayout as) {
		subjectOfTriples.remove(as);
	}

	/**
	 * Returns the StatementLayouts that this ConceptLayout is the object for.
	 * 
	 * @return the StatementLayouts that this ConceptLayout is the object for.
	 *         Never null.
	 */
	public StatementLayout[] getObjectOfStatementLayouts() {
		return (StatementLayout[]) objectOfTriples
				.toArray(new StatementLayout[objectOfTriples.size()]);
	}

	/**
	 * Returns the StatementLayouts that this ConceptLayout is the subject for.
	 * 
	 * @return the StatementLayouts that this ConceptLayout is the subject for.
	 *         Never null.
	 */
	public StatementLayout[] getSubjectOfStatementLayouts() {
		return (StatementLayout[]) subjectOfTriples
				.toArray(new StatementLayout[subjectOfTriples.size()]);
	}

	public boolean getAllowsChildren() {
		return true;
	}

	protected RDFTreeTagNode loadNode(URI uri, RDFModel m) {
		return (RDFTreeTagNode) rcm.getComponentFactory().loadResourceLayout(
				this, uri);
	}

	public void add(MutableTreeNode ttn) {
		super.add(ttn);
//		rcm.containerIsRelevant(ttn.getTag());
		conceptMap.fireEditEvent(new EditEvent(this, this,
				ContextMap.RESOURCELAYOUT_ADDED, ((TreeTagNode) ttn).getURI()));
	}

	public void insert(MutableTreeNode ttn, int index) {
		super.insert(ttn, index);
		conceptMap.fireEditEvent(new EditEvent(this, this,
				ContextMap.RESOURCELAYOUT_ADDED, ((TreeTagNode) ttn).getURI()));
	}

    public void remove(MutableTreeNode mtn) {
    	// OLD STYLE compatability block
    	TreeTagNode ttn = (TreeTagNode) mtn;
    	RDFModel m = getCurrentModel();
    	Resource subj = m.createResource(getURI());
    	if (m.contains(subj, RDF.type, RDF.Seq)) { //Old style removal of children... must support for smome time.
    		Seq seq = m.createSeq(getURI());
    		int relIndex = seq.indexOf(m.getResource(ttn.getURI()));
    		if (relIndex != 0) {
    			seq.remove(relIndex);
    			if (seq.size() == 0) {
    				m.remove(m.createStatement(subj, RDF.type, RDF.Seq));
    			}
    			m.setEdited(true);
    			setEdited(true);
    		}
    	}
    	//End old style compatability block.
    	super.remove(mtn);
		conceptMap.fireEditEvent(new EditEvent(this, this,
				ContextMap.RESOURCELAYOUT_REMOVED, ttn.getURI()));
	}

	/**
	 * @see se.kth.cid.layout.ResourceLayout#isEditable()
	 */
	public boolean isEditable() {
		return getLoadModel().isEditable();
	}

	public void setParent(MutableTreeNode mtn) {
    	if (!isUpdating()) {
            if (getParent() != null) {
    			RDFModel m = getLoadModel();
            	m.remove(m.createStatement(
            			m.createResource(getURI()), 
            			CV.inNodeLayout, 
            			(RDFNode) m.createResource(((TreeTagNode) getParent()).getURI())));
            }
            if (mtn == null) {
            	return;
            }
            TreeTagNode parent = (TreeTagNode) mtn;
            RDFModel m = getLoadModel();
            m.add(m.createStatement(
            		m.createResource(getURI()), 
            		CV.inNodeLayout, 
            		(RDFNode) m.createResource(parent.getURI())));
            super.setParent(mtn);
            setEdited(true);
            m.setEdited(true);
    	} else {
    		super.setParent(mtn);
    	}
	}
	
    /** Loads all children from the given model and appends them to the list (in the new mirror).
     */
    protected void updateFromModel(RDFModel m) {
        super.updateFromModel(m);

        Statement st = m.getProperty(m.createResource(getURI()), CV.priority);
    	if (st != null) {
    		try {
    			double d = st.getDouble();
    			newmirror.setPriority(d);
    		} catch (Exception e) {
    			System.out.println("hmm");
    		}
    	}

    	if (!seekForChildren ) {
    		return;
    	}
        Resource re = m.createResource(getURI());
        ResIterator resIt = m.listSubjectsWithProperty(CV.inNodeLayout, re);
        
        while (resIt.hasNext()) {
        	Resource child = resIt.nextResource();
        	RDFTreeTagNode t = null;
        	if (mirror!= null) {
        		t = (RDFTreeTagNode) mirror.getChild(child.getURI());
        		if (t != null) {
        			t.update((RDFComponentManager) t.getComponentManager());
        		}
        	}
            	
        	if (t == null) {
        		t = loadNode(URI.create(child.getURI()), m);
        	}
            	
        	if (t != null) {
        		t.setTag(URI.create(m.getURI()));
        		newmirror.addAccordingToPriority(t);
        		t.getMirror().setParent(this); //the RDF relation should already be there.
        	}
        }
        
        //OLD style compatability block:
        if (m.createResource(getURI()).hasProperty(RDF.type, RDF.Seq)) {
        	int nr = 1;
        	NodeIterator nit = m.createSeq(getURI()).iterator();
        	while (nit.hasNext()) {
        		String id = nit.next().toString();
        		RDFTreeTagNode t = null;
        		if (mirror != null) {
        			t = (RDFTreeTagNode) mirror.getChild(id);
        			if (t != null) {
        				t.update((RDFComponentManager) t.getComponentManager());
        			}
        		}
        	
        		if (t == null) {
        			try {
        				URI nodeURI = new URI(id);
        				t = loadNode(nodeURI, m);
        			} catch (URISyntaxException e) {
						t = null;
					}
        			if (t != null) {
        				t.getMirror().setPriority(nr); //setting a priority which will not require a setPriority on add below
        				//(avoiding marking all 'old-style' layouts as edited upon load).
        			}
        		}
        	
        		if (t != null) {
        			//Very important to set the tag before adding it,
        			//otherwise the tag won't be referenced in the TagManager.
                                        
//        			t.setTag(URI.create(m.getURI())); //Already done in update of t.
        			newmirror.addAccordingToPriority(t);
        			t.getMirror().setParent(this); //avoiding enforcing the parent relation in RDF upon loading of 'old-style' layout.
        		}
        		nr++;
        	}
        }
        //END OLD STYLE compatability block.
    }

    public void setPriority(double prio) {
        RDFModel m = getCurrentModel();
        if (m == null) {
        	return;
        }
        Resource re = m.createResource(getURI());
        Statement st = m.getProperty(re, CV.priority);
        if (st != null) {
        	m.remove(st);
        }	
        m.add(m.createStatement(re, 
        			CV.priority, 
        			prio));	

		mirror.setPriority(prio);
		setEdited(true);
		m.setEdited(true);
	}
}