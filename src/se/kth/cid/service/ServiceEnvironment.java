package se.kth.cid.service;

import se.kth.cid.conzilla.app.ConzillaEnvironment;
import se.kth.cid.conzilla.content.ContentDisplayer;

public class ServiceEnvironment implements ConzillaEnvironment {

	public void exit(int result) {
		System.exit(result);
	}

	public ContentDisplayer getDefaultContentDisplayer() {
		return null;
	}

	public boolean hasLocalDiskAccess() {
		return true;
	}

	public boolean isOnline() {
		return true;
	}

	public void setOnline(boolean state) {
	}

	public boolean toggleOnlineState() {
		return true;
	}

	public void loadContextMap(String ccm, boolean newView) {
		// stub
	}

}