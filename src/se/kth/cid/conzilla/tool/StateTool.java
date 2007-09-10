/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.tool;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

public abstract class StateTool extends Tool implements PropertyChangeListener {
	
	public static final String ACTIVATED = "activated";

	boolean activated = true;

	public StateTool(String name, boolean init) {
		super(name);
		setActivated(init);
		addPropertyChangeListener(this);
	}

	public StateTool(String name, String resbundle, boolean init) {
		super(name, resbundle);
		setActivated(init);
		addPropertyChangeListener(this);
	}

	public void setActivated(boolean b) {
		if (activated == b)
			return;
		boolean oldValue = activated;
		activated = b;
		firePropertyChange(ACTIVATED, new Boolean(oldValue), new Boolean(b));
	}

	public boolean isActivated() {
		return activated;
	}

	public final void actionPerformed(ActionEvent e) {
		// This tool does not work like an action.
	}

}
