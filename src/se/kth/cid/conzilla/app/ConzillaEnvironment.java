/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.app;

import se.kth.cid.conzilla.content.ContentDisplayer;

public interface ConzillaEnvironment {
	
	public static final String DEFAULT_STARTMAP = "urn:path:/org/conzilla/builtin/maps/default";
	public static final String DEFAULT_BLANKMAP = "urn:path:/org/conzilla/builtin/maps/blank";

	ContentDisplayer getDefaultContentDisplayer();

	boolean isOnline();

	void setOnline(boolean state);

	/**
	 * @return Returns the toggled online state.
	 */
	boolean toggleOnlineState();

	boolean hasLocalDiskAccess();

	void exit(int result);
	
}