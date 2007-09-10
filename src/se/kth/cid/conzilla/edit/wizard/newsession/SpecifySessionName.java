/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.wizard.newsession;

import java.util.Iterator;

import javax.swing.JOptionPane;

import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.util.wizard.SpecifyOneString;

/**
 * @author matthias
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpecifySessionName extends SpecifyOneString {
    public static final String SESSION_NAME = "session name";
        
    public SpecifySessionName() {
      super("Give the session a recognizable name:", "Here be dragons", "", SESSION_NAME);
      setReady(false);
    }
    
    
    
	protected boolean validString(String string) {
		string = string.trim();
		if (super.validString(string)) {
			Iterator it = ConzillaKit.getDefaultKit().getSessionManager().getSessions().iterator();
			while (it.hasNext()) {
				Session session = (Session) it.next(); 
				if (session.getTitle().equalsIgnoreCase(string)) {
					JOptionPane.showMessageDialog(getComponent(), "A session already exists with the name you typed,\n" +
							"please provide another name.");
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean hasFinish() {
		String ns = ConfigurationManager.getConfiguration().getString(Settings.CONZILLA_USER_NAMESPACE);
		return ns != null;
	}
}
