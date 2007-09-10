/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.bookmark;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * This class is a work-around for Java 1.4 to be able to use XMLEncoder. Since
 * DefaultTreeModel does not have a default empty constructor, it does not
 * fulfill the Bean requirements. Basically all TreeModel information is stored
 * in its Nodes, so it is enough to make the Root Node accessible, combined with
 * a default constructor.
 * 
 * XMLEncoder/DefaultTreeModel works on Java 1.5+ without this workaround.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class BookmarkTreeModel extends DefaultTreeModel {
	
	private TreeNode rootNode;
	
	public BookmarkTreeModel() {
		super(new BookmarkNode(new BookmarkInformation("root", "Bookmarks", BookmarkInformation.TYPE_FOLDER)));
		rootNode = root;
	}
	
	public TreeNode getRootNode() {
		return rootNode;
	}
	
	public void setRootNode(TreeNode node) {
		setRoot(node);
		rootNode = node;
	}

}