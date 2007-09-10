/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.tree.generic;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import se.kth.cid.layout.GroupLayout;
import se.kth.cid.tree.TreeTagNode;
import se.kth.cid.util.TagManager;

/**
 * @author Matthias Palmer
 * @version $Revision$
 */
public class MemTreeTagNode implements TreeTagNode {
	Vector order;

	Hashtable id2ttn;

	Hashtable id2rn;

	HashSet hideIDs;

	Object userObject;

	String id;

	Object tag;

	TreeTagNode parent;

	TagManager tagManager;

	String value;
	
	boolean lock = false;

	private double priority = Double.NaN;

	public MemTreeTagNode(String id, Object tag, TagManager tagManager) {
		this.id = id;
		this.tag = tag;
		if (tagManager != null)
			this.tagManager = tagManager;
		else
			this.tagManager = new MemTreeTagManager(null);

		order = new Vector();
		id2ttn = new Hashtable();
		id2rn = new Hashtable();
		hideIDs = new HashSet();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getURI() {
		return id;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public Object getTag() {
		return tag;
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public TreeNode getParent() {
		return parent;
	}

	public void setParent(MutableTreeNode np) {
		parent = (TreeTagNode) np;
	}

	public void removeFromParent() {
		if (getParent() == null)
			return;

		((MutableTreeNode) getParent()).remove(this);
	}

	public boolean isLeaf() {
		return order.size() == 0;
	}

	public void add(MutableTreeNode mtn) {
		insert(mtn, order.size());
	}
	
	public void addAccordingToPriority(TreeTagNode ttn) {
		int position = -1;
		for (Iterator iter = order.iterator(); iter.hasNext();) {
			TreeTagNode child = (TreeTagNode) iter.next();
			if (ttn.getPriority()<child.getPriority()) {
				position = order.indexOf(child);
				break;
			}
		}
		if (position == -1) {
			add(ttn);
		} else {
			insert(ttn, position);
		}
	}

	/**
	 * Warning, since this class is used for delegation, the parent should not be the delegate 
	 * rather the wrapping node. Hence, the parent is not set.
	 * Be sure to set it in the wrapper when wrapping the methods {@link #add(MutableTreeNode)}, 
	 * {@link #insert(MutableTreeNode, int)}, or {@link #addAccordingToPriority(TreeTagNode)}.
	 */
	public void insert(MutableTreeNode mtn, int position) {
//		mtn.setParent(this);
		TreeTagNode ttn = (TreeTagNode) mtn;
		order.insertElementAt(ttn, position);
		// hashtables doesn't accept null, hence we take another specific object
		// to represent 'no tag'.
		tagManager.addTag(ttn.getTag());

		id2ttn.put(ttn.getURI(), ttn);
		id2rn = null;
		updatePriority((TreeTagNode) mtn);
	}

	public void remove(int index) {
		remove((MutableTreeNode) getChildAt(index));
	}

	public void remove(MutableTreeNode mtn) {
		if (lock) {
			return;
		}
		lock = true;
		TreeTagNode ttn = (TreeTagNode) mtn;

		if (!order.remove(ttn))
			return;

		id2ttn.remove(ttn.getURI());
		hideIDs.remove(ttn.getURI());
		id2rn = null;
		tagManager.removeTag(ttn.getTag());
		mtn.setParent(null);
		lock = false;
	}

	public boolean recursivelyRemoveChild(MutableTreeNode mtn) {
		if (order.contains(mtn)) {
			remove(mtn);
			return true;
		}

		refreshRecurse();
		Enumeration en = id2rn.elements();
		while (en.hasMoreElements())
			if (((TreeTagNode) en.nextElement()).recursivelyRemoveChild(mtn))
				return true;
		return false;
	}

	public Enumeration children() {
		return order.elements();
	}

	public Vector getChildren() {
		return order;
	}

	public TreeTagNode getChild(String id) {
		return (TreeTagNode) id2ttn.get(id);
	}

	public TreeTagNode recursivelyGetChild(String id) {
		TreeTagNode ttn = getChild(id);
		if (ttn == null) {
			refreshRecurse();
			Enumeration en = id2rn.elements();
			while (en.hasMoreElements()) {
				ttn = ((TreeTagNode) en.nextElement()).recursivelyGetChild(id);
				if (ttn != null)
					return ttn;
			}
		}
		return ttn;
	}

	// FIXME: what the heck is this for??
	public void setUserObject(Object obj) {
		userObject = obj;
	}

	public Object getUserObject() {
		return userObject;
	}

	public int getIndex(TreeNode tn) {
		return order.indexOf(tn);
	}

	public TreeNode getChildAt(int index) {
		return (TreeNode) order.elementAt(index);
	}

	public int getChildCount() {
		return order.size();
	}

	public void lowerChild(TreeNode tn) {
		order.remove(tn);
		order.add(tn);
		updatePriority((TreeTagNode) tn);
	}

	public void raiseChild(TreeNode tn) {
		order.remove(tn);
		order.insertElementAt(tn, 0);
		updatePriority((TreeTagNode) tn);
	}

	// FIXME: what about when index is bigger than tn's current position?
	public void setIndex(TreeNode tn, int index) {
		if (index >= order.size())
			return;
		order.remove(tn);
		order.insertElementAt(tn, index);
		updatePriority((TreeTagNode) tn);
	}

	private void updatePriority(TreeTagNode node) {
		int index = getIndex(node);
		double np = node.getPriority();
		boolean notSet = Double.isNaN(np);
		TreeTagNode before = index > 0 ? (TreeTagNode) getChildAt(index-1): null;
		TreeTagNode after = index <  (getChildCount() -1) ? (TreeTagNode) getChildAt(index+1) : null;
		if (before != null && after != null) {
			if (notSet || node.getPriority() < before.getPriority() || node.getPriority() > after.getPriority()) {
				node.setPriority((before.getPriority() + after.getPriority())/2d);
			}
		} else if (before != null && after == null) {
			if (notSet || node.getPriority() < before.getPriority()) {
				node.setPriority(before.getPriority() + 1d);
			}
		} else if (before == null && after != null) {
			if (notSet || node.getPriority() > after.getPriority()) {
				node.setPriority(after.getPriority() - 1d);
			}
		} else if (notSet) {
			node.setPriority(1);
		}
	}

	public void sortChildrenAfterPriority() {
		TreeSet plist = new TreeSet(new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((TreeTagNode) o1).getPriority() - ((TreeTagNode) o2).getPriority() < 0 ? -1 : 1;
			}
		});
		plist.addAll(order);
		order = new Vector(plist);		
	}

	public void sortChildrenAfterPriorityRecursively() {
		sortChildrenAfterPriority();
		for (Iterator iter = order.iterator(); iter.hasNext();) {
			TreeTagNode ttn = (TreeTagNode) iter.next();
			if (!ttn.isLeaf()) {
				ttn.sortChildrenAfterPriorityRecursively();
			}
		}
	}
	
	public void setChildHidden(String id, boolean hidden) {
		if (getChild(id) == null)
			return;
		if (hidden)
			hideIDs.add(id);
		else
			hideIDs.remove(id);
	}

	public boolean getChildHidden(String id) {
		if (getChild(id) == null)
			return false;
		return hideIDs.contains(id);
	}

	public TagManager getTreeTagManager() {
		return tagManager;
	}

	public Vector getChildren(int visible, Class restrictToType) {
		Vector collect = new Vector();
		getChildren(collect, visible, restrictToType);
		return collect;
	}

	public void getChildren(Vector collect, int visible, Class restrictToType) {
		Enumeration or = order.elements();
		while (or.hasMoreElements()) {
			TreeTagNode ttn = (TreeTagNode) or.nextElement();
			switch (visible) {
			case ONLY_VISIBLE:
				if (hideIDs.contains(ttn.getURI())
						|| !tagManager.getTagVisible(ttn.getTag()))
					continue;
				break;
			case ONLY_INVISIBLE:
				if (!hideIDs.contains(ttn.getURI())
						&& tagManager.getTagVisible(ttn.getTag()))
					continue;
				break;
			// If IGNORE_VISIBILITY always show.
			}
			if (restrictToType == null || restrictToType.isInstance(ttn))
				collect.add(ttn);

			if (!ttn.isLeaf())
				ttn.getChildren(collect, visible, restrictToType);
		}
	}

	public Hashtable getChildrenAllowingChildren() {
		refreshRecurse();
		return id2rn;
	}

	private void refreshRecurse() {
		if (id2rn != null)
			return;

		id2rn = new Hashtable();
		Enumeration en = children();
		while (en.hasMoreElements()) {
			TreeTagNode ttn = (TreeTagNode) en.nextElement();
			if (ttn.getAllowsChildren())
				id2rn.put(ttn.getURI(), ttn);
		}
	}

	public Set IDSet() {
		HashSet collect = new HashSet();
		IDSet(collect);
		return collect;
	}

	public void IDSet(Set collect) {
		collect.addAll(id2ttn.keySet());
		refreshRecurse();
		Enumeration grp = id2rn.elements();
		while (grp.hasMoreElements())
			((GroupLayout) grp.nextElement()).IDSet(collect);
	}

	public double getPriority() {
		return priority;
	}

	public void setPriority(double prio) {
		this.priority = prio;
	}
}
