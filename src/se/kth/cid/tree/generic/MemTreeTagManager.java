/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.tree.generic;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import se.kth.cid.util.TagManager;

/**
 * Managment of visibility state for a bunch of TreeTagNodes.
 * 
 * @author Matthias Palmer
 * @version $Revision$
 */
public class MemTreeTagManager implements TagManager {
	Hashtable tags;

	Object defaultTag;

	private Vector listeners;

	class Check {
		public int count;

		public boolean visible;

		public Check() {
			count = 1;
			visible = true;
		}

		public void referr() {
			count++;
		}

		public boolean deReferr() {
			count--;
			return isReferred();
		}

		public void setVisible(boolean v) {
			visible = v;
		}

		public boolean isVisible() {
			return visible;
		}

		public boolean isReferred() {
			return count > 0;
		}
	}

	/**
	 * @param dt is the default tag to use when no tag is given, if null is
	 *            given this manager will be used as default tag.
	 */
	public MemTreeTagManager(Object dt) {
		defaultTag = this;
		tags = new Hashtable();
		listeners = new Vector();
	}

	// FIXME: Use instead PropertyChangeSupport, neccessary with property
	// listener?

	public void addPropertyChangeListener(java.beans.PropertyChangeListener pcl) {
		listeners.add(pcl);
	}

	public boolean removePropertyChangeListener(
			java.beans.PropertyChangeListener pcl) {
		return listeners.remove(pcl);
	}

	public void firePropertyChangeEvent(java.beans.PropertyChangeEvent e) {
		Iterator it = listeners.iterator();
		while (it.hasNext()) {
			java.beans.PropertyChangeListener phl = (java.beans.PropertyChangeListener) it
					.next();
			phl.propertyChange(e);
		}
	}

	public Object addTag(Object o) {
		// Hashtables doesn't accept null, hence we take another specific object
		// to represent 'no tag'.
		Object tag = o;
		if (o == null)
			tag = defaultTag;

		if (!tags.keySet().contains(tag))
			tags.put(tag, new Check());
		else
			((Check) tags.get(tag)).referr();
		return tag;
	}

	public void removeTag(Object tag) {
		if (tag == null) {
			tag = defaultTag;
		}

		Check che = (Check) tags.get(tag);
		if (che != null) {
			if (!che.deReferr()) {
				tags.remove(tag);
			}
		}
	}

	public void removeTagCompletely(Object tag) {
		if (tag == null) {
			tag = defaultTag;
		}
		tags.remove(tag);
	}
	
	public void setTagVisible(Object tag, boolean visible) {
		Check che = (Check) tags.get(tag);
		if (che != null && che.isVisible() != visible) {
			che.setVisible(visible);
			firePropertyChangeEvent(new java.beans.PropertyChangeEvent(tag,
					TAG_VISIBILITY_CHANGED, new Boolean(!visible), new Boolean(
							visible)));
		}
	}

	public void setTagVisibleSilently(Object tag, boolean visible) {
		Check che = (Check) tags.get(tag);
		if (che != null && che.isVisible() != visible) {
			che.setVisible(visible);
		}
	}

	public boolean getTagVisible(Object tag) {
		Check che = (Check) tags.get(tag);
		if (che != null)
			return che.isVisible();
		return false;
	}

	public Enumeration getTags() {
		return tags.keys();
	}
	
	public boolean hasTag(Object tag) {
		return tags.keySet().contains(tag);
	}
}
