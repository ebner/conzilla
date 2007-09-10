/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.session;


public interface SessionFactory {

    /**
    * Creates a new Session.
    * @param uri this is a String that will be an identifier for the project, if
    * null the SessionFactory chooses a uri for you.
    * @return Session
    */
    Session createSession(String uri);
}
