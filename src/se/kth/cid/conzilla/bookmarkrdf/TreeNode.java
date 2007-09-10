/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.bookmarkrdf;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import se.kth.cid.component.ComponentException;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.rdf.CV;
import se.kth.cid.rdf.RDFModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.rdf.model.impl.SeqImpl;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author ioana
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class TreeNode extends DefaultMutableTreeNode {
    private final BookmarksTree tree;
    public static final int BOOKMARKFOLDER = 0;
    public static final int CMBOOKMARK = 1;
    public static final int ROOT = 2;
    public static final int CONCEPTBOOKMARK = 3;
    public static final int CONCEPTINCONTEXTBOOKMARK = 4;
    int type = -1;
    Resource node;
    ConzillaKit kit;

    public TreeNode(BookmarksTree tree, Resource node) {
        super(node);
        //creates a tree node with no parent & no children, but which allows children
        this.tree = tree;
        this.node = node;
        this.kit = ConzillaKit.getDefaultKit();
        if (node.hasProperty(RDF.type, CV.BookmarkFolder)) {
            type = BOOKMARKFOLDER;
        } else if (node.hasProperty(RDF.type, CV.ContextMapBookmark)) {
            type = CMBOOKMARK;
            setAllowsChildren(false);
        } else if (node.hasProperty(RDF.type, CV.ConceptBookmark)) {
            type = CONCEPTBOOKMARK;
            setAllowsChildren(false);
        }else if (node.hasProperty(RDF.type, CV.ConceptInContextBookmark)) {
            type = CONCEPTINCONTEXTBOOKMARK;
            setAllowsChildren(false);
        }

        if (isBookmarkEnvironmentItem()) {
            Seq seq = new SeqImpl(node, node.getModel());
            NodeIterator children = seq.iterator();
            while (children.hasNext()) {
                RDFNode child = children.nextNode();
                if (child instanceof Resource) {
                    add(new TreeNode(tree, (Resource) child));
                }
            }
        }
    }

    //the root is always a bookmarkenvironment
    public boolean isBookmarkEnvironmentItem() {
        return type == BOOKMARKFOLDER;
    }

    public boolean isContextMapBookmark() {
        return type == CMBOOKMARK;
    }

    public boolean isConceptAloneBookmark() {
        return type == CONCEPTBOOKMARK;
    }
    
    public boolean isConceptInContextBookmark() {
            return type == CONCEPTINCONTEXTBOOKMARK;
        }

    public void createAndAddChild(int newnodeType, FormItemPane shame) {
        int parenttype = this.type;
        Resource childResource = new ResourceImpl();
        if (parenttype == BOOKMARKFOLDER) {
            switch (newnodeType) {
                case BOOKMARKFOLDER :
                    {
                        childResource = node.getModel().createSeq();
                        childResource.addProperty(RDF.type, CV.BookmarkFolder);
                        childResource.addProperty(
                            DC.title,
                            "Default BMFolder title");
                        saveModel((RDFModel) childResource.getModel(), shame);
                        break;
                    }
            }
            TreeNode childtree = new TreeNode(tree, childResource);
            add(childtree);

            //adding also in the RDF
            Seq parentSeq = new SeqImpl(node, node.getModel());
            parentSeq.add(childResource);            

            DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
            treeModel.nodeStructureChanged(this);
        } //endif
        //      else we should not allow adding a child to a bookmark        
    }

    //function only for ContextMapBookmark Items 
    public void deleteBookmarkLeaf(FormItemPane shame) {
        TreeNode parent = (TreeNode) this.getParent();
        Resource parentresource = parent.node;
        Seq parentSeq = parentresource.getModel().getSeq(parentresource);
        Resource childResource = this.node;
        Model model = parentresource.getModel();

        if (model.containsResource(childResource)) {

            NodeIterator ni =
                childResource.getModel().listObjectsOfProperty(
                    childResource,
                    RDF.subject);
            Resource cmresource = (Resource) ni.nextNode();

            //Only if the cmresource is not reffered by other bookmarks we delete it's properties
            ResIterator othercmbookmarks =
                childResource.getModel().listSubjectsWithProperty(
                    RDF.subject,
                    cmresource);
            if (!othercmbookmarks.hasNext())
                cmresource.removeProperties();

            //RDF delete my properties
            childResource.removeProperties();

            //RDF remove from parent
            int index = parentSeq.indexOf(childResource);
            parentSeq.remove(index);
        }
        //Tree remove from parent
        removeFromParent();
        saveModel((RDFModel) model, shame);

        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        treeModel.nodeStructureChanged(parent);
    }

    //function only for BookmarkFolders
    public void deleteMyselfAndChildrenAndParentConnection(FormItemPane shame) {
        TreeNode parent = (TreeNode) this.getParent();
        Resource parentresource = parent.node;
        Seq parentSeq = parentresource.getModel().getSeq(parentresource);
        Resource childResource = this.node;
        int index = parentSeq.indexOf(childResource);
        Model model = parentresource.getModel();

        if (model.containsResource(childResource)) {

            while (this.getChildCount() != 0) {
                TreeNode childOfChild = (TreeNode) getFirstChild();
                childOfChild.deleteMyselfAndChildrenAndParentConnection(shame);
                DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
                treeModel.nodeStructureChanged(parent);
            }

            if (isLeaf()) {
                if (isContextMapBookmark() || isConceptAloneBookmark() || isConceptInContextBookmark())
                    deleteBookmarkLeaf(shame);
                else {

                    node.removeProperties();
                    //removeMyselfFromParent
                    //RDF remove from parent
                    parentSeq.remove(index);

                    //Tree remove from parent
                    removeFromParent();
                    saveModel((RDFModel) model, shame);
                    DefaultTreeModel treeModel =
                        (DefaultTreeModel) tree.getModel();
                    treeModel.nodeStructureChanged(parent);
                }
                return;
            }
        }
    }

    public void switchOrder(TreeNode one, TreeNode another) {
        Resource parentResource = getResource();
        Resource oneResource = one.getResource();
        Resource anotherResource = another.getResource();
        Seq parentResourceSeq =
            new SeqImpl(parentResource, parentResource.getModel());
        int oneIndex = parentResourceSeq.indexOf(oneResource);
        int anotherIndex = parentResourceSeq.indexOf(anotherResource);

        if (oneIndex <= 0 || anotherIndex <= 0)
            return;
        parentResourceSeq.set(oneIndex, anotherResource);
        parentResourceSeq.set(anotherIndex, oneResource);

        remove(one);
        remove(another);
        if (oneIndex < anotherIndex) {
            insert(another, oneIndex - 1);
            insert(one, anotherIndex - 1);
        } else {
            insert(one, anotherIndex - 1);
            insert(another, oneIndex - 1);
        }
    }

    public Resource getResource() {
        return node;
    }

    public int getType() {
        return this.type;
    }

    public String toString() {
        /*
         if (!triedToFindLabel) {
            LangStringMap [] lms = JenaUtilities.getLabels(node, node.getModel());
            if (lms.length > 0) {
                lm = lms[0];
            }
        }
        if (lm == null) {
            return "No label";
        } else {
            return lm.getString();
        }
        */
        String title = "";

        if (this.node.hasProperty(RDF.type, CV.ContextMapBookmark)) {

            if (this.node.hasProperty(DC.title)) {
                NodeIterator ni =
                    node.getModel().listObjectsOfProperty(node, DC.title);
                RDFNode nifirst = ni.nextNode();
                title = nifirst.toString();
            } else
                title = "no Context map title";
        } else {
            if (this.node.hasProperty(RDF.type, CV.ConceptBookmark)) {

                if (this.node.hasProperty(DC.title)) {
                    NodeIterator ni =
                        node.getModel().listObjectsOfProperty(node, DC.title);
                    RDFNode nifirst = ni.nextNode();
                    title = nifirst.toString();
                } else
                    title = "no Concept title";
            } 
            
            else {
            if (this.node.hasProperty(RDF.type, CV.ConceptInContextBookmark)) {

                if (this.node.hasProperty(DC.title)) {
                    NodeIterator ni =
                        node.getModel().listObjectsOfProperty(node, DC.title);
                    RDFNode nifirst = ni.nextNode();
                    title = nifirst.toString();
                } else
                    title = "no Concept in Context title";
            } else {

                if (this.node.hasProperty(RDF.type, CV.BookmarkFolder)) {
                    NodeIterator ni =
                        node.getModel().listObjectsOfProperty(node, DC.title);
                    RDFNode nifirst = ni.nextNode();
                    title = nifirst.toString();
                } else
                    title = "no Bookmarkfolder Title";
            }
            }
        }

            if (title.indexOf("http://allconstraintsvalid") == -1) {
                title =
                    title.substring(title.lastIndexOf('#') + 1, title.length());
            } else {
                title = "";
            }

            String editedString = tree.resourceNodeEdited == this ? " -->" : "";

            return title + editedString;
        }

        public boolean isLeaf() {
            boolean haskids = true;
            if (type == CMBOOKMARK || type == CONCEPTBOOKMARK || type==CONCEPTINCONTEXTBOOKMARK)
                haskids = false;
            if (type == BOOKMARKFOLDER
                && node.getModel().getSeq(node).size() == 0)
                haskids = false;
            return !haskids;
        }

        public boolean saveModel(RDFModel bookMarkModel, FormItemPane shame) {
            try {
                //Clean up valuemodel in shame by forcing it to edit nothing.
                shame.editFormItem(null);
                //Mark the model as edited, otherwise we can't save it.
                bookMarkModel.setEdited(true);
                //Now, save it!
                //ConzillaKit.getDefaultKit().getResourceStore().getContainerManager().saveResource(bookMarkModel);
                ConzillaKit.getDefaultKit().getResourceStore().getComponentManager().saveResource(bookMarkModel);
            } catch (ComponentException e3) {
                e3.printStackTrace();
            }
            return true;
        }
    }