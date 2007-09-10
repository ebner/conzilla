/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.app;

import java.beans.PropertyChangeEvent;

import javax.swing.Icon;

import se.kth.cid.conzilla.browse.BrowseMapManagerFactory;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.StateTool;
import se.kth.cid.util.Tracer;

/**
 * Toggle Conzilla's online state.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class OnlineStateTool extends StateTool {

	private static final long serialVersionUID = 1L;

	public OnlineStateTool() {
		super("ONLINESTATE", BrowseMapManagerFactory.class.getName(), !isOnline());
		setIcon(getIcon());
		setSelectedIcon(getSelectedIcon());
		setToolTip();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ACTIVATED)) {
			boolean isOnline = ConzillaKit.getDefaultKit().getConzillaEnvironment().toggleOnlineState();
			if (isOnline) {
				Tracer.debug("Conzilla set to ONLINE mode");
			} else {
				Tracer.debug("Conzilla set to OFFLINE mode");
			}
			setToolTip();
		}
	}
	
	private void setToolTip() {
		if (isOnline()) {
			putValue(SHORT_DESCRIPTION, "You are online. Click the button to go offline.");
		} else {
			putValue(SHORT_DESCRIPTION, "You are offline. Click the button to go online.");
		}
	}
	
	public Icon getIcon() {
		return Images.getImageIcon(Images.ICON_ONLINESTATE);
	}

	private static boolean isOnline() {
		return ConzillaKit.getDefaultKit().getConzillaEnvironment().isOnline();
	}

}