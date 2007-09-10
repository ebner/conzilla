/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.tool;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import se.kth.cid.config.Config;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.config.Settings;

public abstract class StateTool extends Tool implements PropertyChangeListener {
	
	public static final String ACTIVATED = "activated";

	boolean activated = true;
	
	boolean rememberState = false;

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
	
	public StateTool(String name, String resbundle, boolean defaultState, boolean rememberState) {
		super(name, resbundle);
		if (rememberState) {
			setActivated(getStoredStatus(defaultState));
		} else {
			setActivated(defaultState);
		}
		this.rememberState = rememberState;
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

	/**
	 * This tool does not work like an action. Therefore this method is final.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public final void actionPerformed(ActionEvent e) {
		if (rememberState) {
			storeStatus();
		}
	}
	
	private String getConfigurationKey() {
		String name = getName().trim().toLowerCase().replaceAll(" ", "-");
		return Settings.CONZILLA_TOOLS + "." + name + ".activated";
	}
	
	private boolean getStoredStatus(boolean defaultValue) {
		Config config = ConfigurationManager.getConfiguration();
		return config.getBoolean(getConfigurationKey(), defaultValue);
	}
	
	public void storeStatus() {
		Config config = ConfigurationManager.getConfiguration();
		config.setProperty(getConfigurationKey(), new Boolean(activated));
	}
	
	public void detach() {
		super.detach();
		removePropertyChangeListener(this);
	}

}