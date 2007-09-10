/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf;

import java.net.URI;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import se.kth.cid.tree.TreeTagNode;
import se.kth.cid.tree.TreeTagNodeResource;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.vocabulary.RDF;

/** This is a treenode that stores the order of children as a rdf:Seq.
 * TODO: not tested for editing.
 * 
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class RDFItem extends RDFTreeTagNode implements TreeTagNodeResource {

	public RDFItem(URI uri) {
		super(uri, CV.content, CV.Item);
	}

    public boolean getAllowsChildren() {
        return true;
    }

    protected RDFTreeTagNode loadNode(URI uri, RDFModel m) {
    	RDFItem item = new RDFItem(uri);
    	item.update(new RDFComponentManager(((RDFComponentFactory) rcm.getComponentFactory()), uri, rcm.getTagManager(), false));
    	return item;
    }
    
    /** Loads all children from the given model and appends them to the list (in the new mirror).
     */
    protected void updateFromModel(RDFModel m) {
        super.updateFromModel(m);
        if (!m.createResource(getURI()).hasProperty(RDF.type, RDF.Seq)) {
        	return;
        }
        int nr = 1;
        NodeIterator nit = m.createSeq(getURI()).iterator();
        while (nit.hasNext()) {
        	String id = nit.next().toString();
        	RDFTreeTagNode t = null;
        	if (mirror!= null) {
        		t = (RDFTreeTagNode) mirror.getChild(id);
        		if (t != null) {
        			t.update(t.rcm);
        		}
        	}
        	
        	if (t == null) {
        		t = loadNode(URI.create(id), m);
    			t.setPriority(nr);
        	}
        	
        	if (t != null) {
        		//Very important to set the tag before adding it,
        		//otherwise the tag won't be referenced in the TagManager.
                                        
        		t.setTag(URI.create(m.getURI()));
        		newmirror.add(t);
        		t.setParent(this);
        	}
        	nr++;
        }
    }
    
    ///Continue editing here...
    public void add(MutableTreeNode mtn) {
        if (!(mtn instanceof RDFTreeTagNode))
            return;
        RDFTreeTagNode rttn = (RDFTreeTagNode) mtn;
        RDFModel m = getCurrentModel();
        Seq seq = m.createSeq(getURI());
        seq.add(seq.size() + 1, m.createResource(rttn.getURI()));
        
//        rttn.setTag(URI.create(m.getURI())); //Done when mirror is constructed in initialize and update respectively.
            
        super.add(mtn);

    	rcm.containerIsRelevant(URI.create(m.getURI()));
        setEdited(true);
        m.setEdited(true);
    }
    
    public void insert(MutableTreeNode mtn, int position) {
    	if (!(mtn instanceof RDFTreeTagNode))
    		return;
    	RDFTreeTagNode rttn = (RDFTreeTagNode) mtn;
    	RDFModel m = getCurrentModel();

    	Seq seq = m.createSeq(getURI());
    	if (position > seq.size())
    		return;
    	seq.add(position + 1, m.createResource(rttn.getURI()));
    	//rttn.setTag(URI.create(m.getURI()));
    	super.insert(mtn, position);

    	rcm.containerIsRelevant(URI.create(m.getURI()));
    }
    

	public void lowerChild(TreeNode tn) {
		remove((MutableTreeNode) tn);
		add((MutableTreeNode) tn);
	}

	public void raiseChild(TreeNode tn) {
		remove((MutableTreeNode) tn);
		insert((MutableTreeNode) tn, 0);
	}

	// FIXME: what about when index is bigger than tn's current position?
	public void setIndex(TreeNode tn, int index) {
		if (index >= mirror.getChildCount())
			return;
		remove((MutableTreeNode) tn);
		insert((MutableTreeNode) tn, index);
	}
    
    public void remove(MutableTreeNode mtn) {
    	TreeTagNode ttn = (TreeTagNode) mtn;
    	RDFModel m = getLoadModel();
    	Seq seq = m.createSeq(getURI());
    	int relIndex = seq.indexOf(m.getResource(ttn.getURI()));
    	if (relIndex == 0)
    		return;
    	seq.remove(relIndex);
    	mirror.remove(mtn);
    }    
}
