/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.session;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Hashtable;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.conzilla.util.ErrorMessage;

/**
 * @author matthias
 */
public abstract class SessionManagerImpl implements SessionManager {

	Hashtable projects;

	Session current;

	SessionFactory projectFactory;

	ResourceStore store;
	
	PropertyChangeSupport propertyChangeSupport;

	/**
	 * Constructor for SessionManagerImpl.
	 */
	public SessionManagerImpl(ResourceStore store) {
		this.store = store;
		projects = new Hashtable();
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * @see SessionManager#getCurrentSession()
	 */
	public Session getCurrentSession() {
		return current;
	}

	/**
	 * @see SessionManager#setCurrentSession(Session)
	 */
	public boolean setCurrentSession(Session session) {
		boolean same = session.getContainerURIForConcepts().equals(session.getContainerURIForLayouts());
		String errorMessage = (same ? "Failed loading combined concept and layout container "
				: "Failed loading concept container ")
				+ session.getContainerURIForConcepts();
		try {
			store.getAndReferenceContainer(new URI(session.getContainerURIForConcepts()), true);
		} catch (ComponentException e) {
			e.printStackTrace();
			ErrorMessage.showError("Loading failed", errorMessage, e, null);
			return false;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			ErrorMessage.showError("Loading failed", errorMessage, e, null);
			return false;
		}

		if (!same) {
			errorMessage = "Failed loading layout container " + session.getContainerURIForLayouts();
			try {
				store.getAndReferenceContainer(new URI(session.getContainerURIForLayouts()), true);
			} catch (ComponentException e1) {
				e1.printStackTrace();
				ErrorMessage.showError("Loading failed", errorMessage, e1, null);
				return false;
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
				ErrorMessage.showError("Loading failed", errorMessage, e1, null);
				return false;
			}
		}
		// If all went well, we set the current session.
		Session oldSession = current;
		current = session;
		propertyChangeSupport.firePropertyChange(SessionManager.PROPERTY_SESSION_SELECTED, oldSession, session);
		return true;
	}

	/**
	 * @see SessionManager#getSessions()
	 */
	public Collection getSessions() {
		return projects.values();
	}
	
	public int getSessionCount() {
		return projects.size();
	}

	/**
	 * @see SessionManager#getSession(String)
	 */
	public Session getSession(String uri) {
		return (Session) projects.get(uri);
	}

	/**
	 * @see SessionManager#addSession(Session)
	 */
	public void addSession(Session project) {
		projects.put(project.getURI(), project);
		propertyChangeSupport.firePropertyChange(SessionManager.PROPERTY_SESSION_ADDED, null, project);
	}

	/**
	 * @see SessionManager#createAndAddSession(String)
	 */
	public Session createAndAddSession(String uri) {
		Session pro = getSessionFactory().createSession(uri);
		addSession(pro);
		return pro;
	}

	/**
	 * @see SessionManager#removeSession(Session)
	 */
	public void removeSession(Session project) {
		if (projects.containsValue(project)) {
			projects.remove(project.getURI());
			propertyChangeSupport.firePropertyChange(SessionManager.PROPERTY_SESSION_REMOVED, null, project);
		}
	}

	/**
	 * @see SessionManager#setSessionFactory(SessionFactory)
	 */
	public void setSessionFactory(SessionFactory projectFactory) {
		this.projectFactory = projectFactory;
	}

	/**
	 * @see SessionManager#getSessionFactory()
	 */
	public SessionFactory getSessionFactory() {
		return projectFactory;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
}