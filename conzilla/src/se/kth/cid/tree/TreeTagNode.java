/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.tree;

import java.util.Enumeration;
import java.util.Set;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import se.kth.cid.util.TagManager;

/**
 * This is a more advanced class than {@link javax.swing.tree.MutableTreeNode}.<br>
 * A TreeTagNode is required to have both a name and a tag. Several nodes may
 * share a common tag, therefore changing a tags visiblility affects several
 * nodes visibility. Visibility can be controlled on individual nodes as well as
 * on tags. Henceforth for a individual node to be visible both it's individual
 * visibility as well as it's tags visibility must be set to true.<br>
 * 
 * There is also some functions beginning with 'recursive' which simply means
 * that the function is called in a recursive manner on all non-leaf nodes
 * below.<br>
 * 
 * It is also possible to controll the order of the childs with the raise and
 * lowerChild respectively the set- and get- OrderOfChild functions.
 * 
 * @author Matthias Palmer
 * @version $Revision$
 */
public interface TreeTagNode extends MutableTreeNode {

	int IGNORE_VISIBILITY = 0;

	int ONLY_VISIBLE = 1;

	int ONLY_INVISIBLE = 2;

	// Get and set the value the node points to, respectively.
	// The value of a non-leaf node is null.
	String getValue();

	void setValue(String value);

	// ******Access, creation and deletion of Childs.*******
	// -----------------------------------------------------------------------
	String getURI();

	Object getTag();

	void setTag(Object object);

	// Exists already in TreeNode:
	boolean getAllowsChildren();

	// Exists already in TreeNode:
	TreeNode getParent();

	// Exists already in TreeNode:
	boolean isLeaf();

	void add(MutableTreeNode mtn);

	// Exists already in MutableTreeNode:
	void insert(MutableTreeNode mtn, int position);

	// Exists already in MutableTreeNode:
	void remove(int index);

	// Exists already in MutableTreeNode:
	void remove(MutableTreeNode mtn);

	/**
	 * Use if the Enumeration from {@link TreeNode#children()} isn't enough.
	 */
	Vector getChildren();

	// Exists already in TreeNode:
	Enumeration children();

	boolean recursivelyRemoveChild(MutableTreeNode ttn);

	TreeTagNode getChild(String id);

	TreeTagNode recursivelyGetChild(String id);

	// Exists already in MutableTreeNode:
	void setUserObject(Object obj);

	Object getUserObject();

	// ****Manipulation of order of childs.*******
	// ----------------------------------------------------------------------------

	// Exists already in TreeNode:
	int getIndex(TreeNode tn);

	// Exists already in TreeNode:
	TreeNode getChildAt(int index);

	// Exists already in TreeNode:
	int getChildCount();

	void lowerChild(TreeNode tn);

	void raiseChild(TreeNode tn);

	void setIndex(TreeNode tn, int index);
	
	void setPriority(double prio);

	void sortChildrenAfterPriority();

	void sortChildrenAfterPriorityRecursively();
	
	double getPriority();

	void addAccordingToPriority(TreeTagNode ttn);

	// ****Manipulation of visibility of childs.*******
	// ----------------------------------------------------------------------------

	void setChildHidden(String id, boolean hidden);

	boolean getChildHidden(String id);

	TagManager getTreeTagManager();

	// ***********Conditional deep listings of childs.*****************
	// -------------------------------------------------------------------

	Vector getChildren(int visible, Class restrictToType);

	void getChildren(Vector collect, int visible, Class restrictedToType);

	// ***********To find new IDs.************************
	// ---------------------------------------------------

	Set IDSet();

	void IDSet(Set collect);

}