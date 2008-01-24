/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf;

import java.net.URI;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.rdf.layout.RDFResourceLayout;
import se.kth.cid.tree.TreeTagNode;
import se.kth.cid.tree.generic.MemTreeTagNode;
import se.kth.cid.util.TagManager;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.vocabulary.RDF;

/** This is an RDF-ResourceLayout.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public abstract class RDFTreeTagNode
    extends RDFComponent
    implements TreeTagNode {
	public static final Log LOG = LogFactory.getLog(RDFTreeTagNode.class);

    /** A local cache for all the children with their model-belongings preserved.
     *  All get-methods work against this cache only, and the set-methods work
     *  both against the Jena model and this cache.
     */
    protected MemTreeTagNode mirror;

    /** Temporary storage of new cache at subsequent updating. 
     */
    protected MemTreeTagNode newmirror;
    protected Resource nodeType;

    /** Link to content (an RDFConcept). May be null if there is no content. */
    protected String contentUri;

    /** The RDF predicate (property) linking this RDF resource to its content.
        May be null if there is no content. */
    protected Property property;
    
    protected String type;

    public RDFTreeTagNode(URI uri, Property property, Resource nodeType) {
    	super(uri);
    	this.property = property;
    	this.nodeType = nodeType;
    	this.type = nodeType.getURI();
    }
    
    /** Overload for correct type handling....
     */
    public RDFModel getLoadModel() {
    	if (model == null) {
    		Resource subject = new ResourceImpl(getURI());
    		//model = totalModel.find1Model(subject, RDF.type, null);
    		model = ((RDFContainerManager) rcm.getContainerManager()).find1Model(subject, RDF.type, nodeType);
              
    		if (model == null) {
    			model = (RDFModel) rcm.getCurrentConceptContainer();
    		}
    	}
    	return model;
    }
      
    public TreeTagNode getMirror() {
    	return mirror;
    }

    /** Uses the given node type (in the create constructor) as type.
     */
    protected void initializeInModel(RDFModel model) {
        super.initializeInModel(model);
        mirror = new MemTreeTagNode(getURI().toString(), URI.create(model.getURI()), rcm.getTagManager());
        Resource object = model.getResource(getURI());
        object.addProperty(RDF.type, nodeType);
        if (contentUri != null) {
        	Resource concept = model.getResource(contentUri);
        	object.addProperty(property, concept);
        }
    }

    public String getType() {
        if (type != null)
            return type;

        try {
            Resource object = getLoadModel().getResource(getURI());
            Statement stmt = object.getProperty(RDF.type);
            if (stmt != null) {
                nodeType = stmt.getResource();
                type = nodeType.getURI();
            } else {
                nodeType = CV.Item;
                type = nodeType.getURI();
            }
       
        } catch (Exception re) {
            nodeType = CV.Item;
            type = nodeType.getURI();
        }
        return type;
    }

    public String getValue() {
        return contentUri;
    }

    public void setValue(String contentUri) {
        this.contentUri = contentUri;
    }

    /** A new mirror is initiated.
     */
    protected void initUpdate() {
        super.initUpdate();

        Resource object = getLoadModel().getResource(getURI());
        //conceptUri = object.getProperty(CV.displayresource).getResource().getURI();
        Statement stmt = object.getProperty(property);
        if (stmt != null) {
        	contentUri = stmt.getResource().getURI();
        }

        getType();


        if (mirror != null) {
        	newmirror = new MemTreeTagNode(mirror.getURI(), mirror.getTag(), rcm.getTagManager());
        	newmirror.setPriority(mirror.getPriority());
            newmirror.setParent((MutableTreeNode) mirror.getParent());
        } else {
        	newmirror = new MemTreeTagNode(getURI().toString(), URI.create(model.getURI()), rcm.getTagManager());
        }
    }

    /** the new mirror is set to the new mirror.
     */
    protected void endUpdate() {
        super.endUpdate();
        mirror = newmirror;
    }

    /** Recursively removes this node and all it's children from all models.
     */
    public void recursivelyRemoveFromAllRelevantModels() {
        Vector objectLayouts = new Vector(getChildren());
        for (int i = objectLayouts.size() - 1; i >= 0; i--) {
            RDFTreeTagNode rttn = (RDFTreeTagNode) objectLayouts.elementAt(i);
            rttn.recursivelyRemoveFromAllRelevantModels();
        }
        removeFromAllRelevantModels();
    }

    /** Recursively marks all nodes in the tree to be unedited.
     */
    public static void setUnEditedRecursively(RDFTreeTagNode rttn) {
        rttn.setEdited(false);
        Iterator it = rttn.getChildren().iterator();
        while (it.hasNext())
            setUnEditedRecursively((RDFTreeTagNode) it.next());
    }

    /** Recusively retrieves all relevant models from the trees nodes and adds them
     *  to the set given as first argument.
     */
    public static void getAllRelevantModelsRecursively(
        Set set,
        RDFTreeTagNode rttn) {
        set.addAll(rttn.getComponentManager().getLoadedRelevantContainers());
        Iterator it = rttn.getChildren().iterator();
        while (it.hasNext())
            getAllRelevantModelsRecursively(set, (RDFTreeTagNode) it.next());
    }

    /** Recursively removes this node and all it's children from the given model.
     */
    public void recursivelyRemoveFromModel(RDFModel model) {
        removeFromModel(model, true);
    }
    
    /** Removes this RDFTreeTagNode from the given model, i.e. removes the nodetype, 
     *  the children pointed too in this model and the triple describing this RDFTreeTagNode
     *  as a rdf:sequence.
     */
    protected void removeFromModel(RDFModel model) {
        removeFromModel(model, false);
    }

    private void removeFromModel(RDFModel model, boolean recursively) {
        for (Iterator childs = new Vector(getChildren()).iterator(); childs.hasNext();) {
            RDFTreeTagNode ttn = (RDFTreeTagNode) childs.next();
            mirror.remove(ttn);
            if (recursively)
                ttn.removeFromModel(model, recursively);
        }

        Resource object = model.getResource(getURI());
        object.removeProperties();
    }
    
    protected abstract RDFTreeTagNode loadNode(URI uri, RDFModel m);

    public Object getTag() {
        return mirror.getTag();
    }

    public void setTag(Object object) {
        if (getClass().equals(RDFResourceLayout.class)) {
            mirror.setTag(URI.create(getURI()));
        } else {
            mirror.setTag(object);
        }
    }

    public abstract boolean getAllowsChildren();

    public TreeNode getParent() {
        return mirror.getParent();
    }

    public void setParent(MutableTreeNode mtn) {
        mirror.setParent(mtn);
    }

    public void removeFromParent() {
        mirror.removeFromParent();
    }

    public boolean isLeaf() {
        return mirror.isLeaf();
    }

    
    public void addAccordingToPriority(TreeTagNode ttn) {
    	mirror.addAccordingToPriority(ttn);
    	ttn.setParent(this);
    }

    public void remove(MutableTreeNode mtn) {
    	mirror.remove(mtn);
    }

    public Vector getChildren() {
        return mirror.getChildren();
    }

    public Enumeration children() {
        return mirror.children();
    }

    /** Removes a child from any node reachable from this node (using a recursive approach).
     *  Observe that the function with same name in the cahce (a MemTreeTagNode)
     *  cannot be used since when it finds the child in a node further down in the tree
     *  it only removes it from the mirror (MemTreeTagNode.remove) instead of using
     *  this class remove function (which calls the mirrors remove function on success).
     */
    public boolean recursivelyRemoveChild(MutableTreeNode ttn) {
        int index = mirror.getIndex(ttn);
        if (index != -1) {
            remove(index);
            return true;
        }

        Enumeration en = mirror.getChildrenAllowingChildren().elements();
        while (en.hasMoreElements())
            if (((TreeTagNode) en.nextElement()).recursivelyRemoveChild(ttn))
                return true;
        return false;
    }

    public TreeTagNode getChild(String id) {
        return mirror.getChild(id);
    }

    public TreeTagNode recursivelyGetChild(String id) {
        return mirror.recursivelyGetChild(id);
    }

    public void setUserObject(Object obj) {
        mirror.setUserObject(obj);
    }
    
    public Object getUserObject() {
        return mirror.getUserObject();
    }

    //****Manipulation of order of childs.*******
    //----------------------------------------------------------------------------

    public int getIndex(TreeNode tn) {
        return mirror.getIndex(tn);
    }

    public TreeNode getChildAt(int index) {
        return mirror.getChildAt(index);
    }

    public int getChildCount() {
        return mirror.getChildCount();
    }

    public void lowerChild(TreeNode tn) {
        mirror.lowerChild(tn);
    }

    public void raiseChild(TreeNode tn) {
        mirror.raiseChild(tn);
    }

    public void setIndex(TreeNode tn, int index) {
        mirror.setIndex(tn, index);
    }

    //****Manipulation of visibility of childs.*******
    //----------------------------------------------------------------------------

    public void setChildHidden(String id, boolean hidden) {
        mirror.setChildHidden(id, hidden);
    }

    public boolean getChildHidden(String id) {
        return mirror.getChildHidden(id);
    }

    public TagManager getTreeTagManager() {
        return mirror.getTreeTagManager();
    }

    //***********Conditional deep listings of childs.*****************
    //-------------------------------------------------------------------

    public Vector getChildren(int visible, Class restrictToType) {
        return mirror.getChildren(visible, restrictToType);
    }

    public void getChildren(
        Vector collect,
        int visible,
        Class restrictedToType) {
        mirror.getChildren(collect, visible, restrictedToType);
    }

    //***********To find new IDs.************************
    //---------------------------------------------------

    public Set IDSet() {
        return mirror.IDSet();
    }

    public void IDSet(Set collect) {
        mirror.IDSet(collect);
    }

    /**
     * @see se.kth.cid.rdf.RDFResource#getCurrentModel()
     */
    protected RDFModel getCurrentModel() {
        if (((RDFContainerManager) rcm.getContainerManager()).getTreeTagNodeLoadContainerDomination())
            return getLoadModel();
        else
            return (RDFModel) rcm.getCurrentLayoutContainer();
    }


	public void add(MutableTreeNode mtn) {
		mirror.add(mtn);
		mtn.setParent(this);
	}

	public void insert(MutableTreeNode mtn, int position) {
		mirror.insert(mtn, position);
		mtn.setParent(this);
	}

	public void remove(int index) {
    	remove((MutableTreeNode) mirror.getChildAt(index));
	}
    
	public void setPriority(double prio) {
		mirror.setPriority(prio);
	}

	public void sortChildrenAfterPriority() {
		mirror.sortChildrenAfterPriority();
	}

	public void sortChildrenAfterPriorityRecursively() {
		mirror.sortChildrenAfterPriorityRecursively();
	}
	
	public double getPriority() {
		return mirror.getPriority();
	}
}