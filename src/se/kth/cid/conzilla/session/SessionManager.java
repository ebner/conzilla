/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.session;

import java.beans.PropertyChangeListener;
import java.util.Collection;

/**
 * Manages a list of known sessions and the creation of new via a
 * {@link se.kth.cid.conzilla.session.SessionFactory}.
 * 
 * @author matthias
 */
public interface SessionManager {

	String SESSIONS = "sessions";
	
	String PROPERTY_SESSION_ADDED = "session.added";
	
	String PROPERTY_SESSION_REMOVED = "session.removed";
	
	String PROPERTY_SESSION_SELECTED = "session.selected";

	/**
	 * One session is active at a time. The current session may change due to
	 * focus changes or manual override.
	 * 
	 * @return Session
	 */
	Session getCurrentSession();

	/**
	 * Manual control of current session uses this method.
	 * 
	 * @param session
	 * @return true if the sessions containers could be loaded.
	 */
	boolean setCurrentSession(Session session);

	/**
	 * @return List of all sessions known.
	 */
	Collection getSessions();
	
	int getSessionCount();

	/**
	 * @param uri
	 *            a unique identifier for a session
	 * @return Session with the given uri, null if there is no session.
	 */
	Session getSession(String uri);

	/**
	 * Adds a session to the known sessions.
	 * 
	 * @param session
	 */
	void addSession(Session session);

	/**
	 * Creates a {@link Session} via the {@link SessionFactory} and adds ({@link #addSession(Session)})
	 * it to the list of known sessions.
	 * 
	 * @param uri
	 *            a URI for the session, if null the createfactory chooses one
	 *            for you.
	 * @see #getSessionFactory()
	 * @see #addSession(Session)
	 */
	Session createAndAddSession(String uri);

	/**
	 * Removes a session from the list of known sessions
	 * 
	 * @param session
	 */
	void removeSession(Session session);

	/**
	 * Sets the sessionFactory to use when creating new sessions.
	 * 
	 * @param sessionFactory
	 */
	void setSessionFactory(SessionFactory sessionFactory);

	/**
	 * Preferrably this sessionFactory should be used for session creation.
	 * 
	 * @return SessionFactory
	 */
	SessionFactory getSessionFactory();

	void saveSessions(String uri);

	void loadSessions(String uri);
	
	void addPropertyChangeListener(PropertyChangeListener listener);
	
	void removePropertyChangeListener(PropertyChangeListener listener);
	
}