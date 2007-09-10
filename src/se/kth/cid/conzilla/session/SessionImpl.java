/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.session;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author matthias
 */
public class SessionImpl implements Session {

	String uri;

	String title;

	String baseURIForConcepts;

	String baseURIForLayouts;

	String containerURIForLayouts;

	String containerURIForConcepts;

	Collection managed;
	
	boolean modified;

	/**
	 * Constructor for SessionImpl.
	 */
	public SessionImpl() {
		managed = new ArrayList();
		modified = true;
	}

	public SessionImpl(String uri) {
		this();
		this.uri = uri;
	}

	/**
	 * @see Session#getURI()
	 */
	public String getURI() {
		return uri;
	}

	/**
	 * @see Session#getBaseURIForConcepts()
	 */
	public String getBaseURIForConcepts() {
		return baseURIForConcepts;
	}

	/**
	 * @see Session#getBaseURIForLayouts()
	 */
	public String getBaseURIForLayouts() {
		return baseURIForLayouts;
	}

	/**
	 * @see Session#setBaseURIForConcepts(java.lang.String)
	 */
	public void setBaseURIForConcepts(String base) {
		this.baseURIForConcepts = base;
		this.modified = true;
	}

	/**
	 * @see Session#setBaseURIForLayouts(java.lang.String)
	 */
	public void setBaseURIForLayouts(String base) {
		this.baseURIForLayouts = base;
		this.modified = true;
	}

	/**
	 * @see Session#getContainerURIForConcepts()
	 */
	public String getContainerURIForConcepts() {
		return containerURIForConcepts;
	}

	/**
	 * @see Session#getContainerURIForLayouts()
	 */
	public String getContainerURIForLayouts() {
		return containerURIForLayouts;
	}

	/**
	 * @see Session#setContainerURIForConcepts(java.lang.String)
	 */
	public void setContainerURIForConcepts(String container) {
		this.containerURIForConcepts = container;
		this.modified = true;
	}

	/**
	 * @see Session#setContainerURIForLayouts(java.lang.String)
	 */
	public void setContainerURIForLayouts(String container) {
		this.containerURIForLayouts = container;
		this.modified = true;
	}

	/**
	 * @see Session#addManaged(java.lang.String)
	 */
	public void addManaged(String uri) {
		managed.add(uri);
		modified = true;
	}

	/**
	 * @see Session#getManaged()
	 */
	public Collection getManaged() {
		return managed;
	}

	/**
	 * @see Session#isManaged(java.lang.String)
	 */
	public boolean isManaged(String uri) {
		return managed.contains(uri);
	}

	/**
	 * @see Session#removeManaged(java.lang.String)
	 */
	public boolean removeManaged(String uri) {
		if (managed.contains(uri)) {
			managed.remove(uri);
			modified = true;
			return true;
		}
		return false;
	}

	/**
	 * @see Session#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @see Session#setTitle(java.lang.String)
	 */
	public void setTitle(String title) {
		this.title = title;
		this.modified = true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getTitle();
	}
	
	/**
	 * @see se.kth.cid.conzilla.session.Session#setModified(boolean)
	 */
	public void setModified(boolean modified) {
		this.modified = modified;
	}
	
	/**
	 * @see se.kth.cid.conzilla.session.Session#isModified()
	 */
	public boolean isModified() {
		return modified;
	}

}