/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.properties;

import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;

import se.kth.cid.util.Tracer;

/**
 * @author Mikael Nilsson
 * @version $Revision$
 */
public class ConzillaResourceManager {
	static ConzillaResourceManager defaultManager;

	Locale defaultLocale;

	HashMap bundles;

	ConzillaResourceManager() {
		bundles = new HashMap();
		defaultLocale = Locale.getDefault();
	}

	ResourceBundle getBundle(String basename) {
		// null allowed...

		ResourceBundle rb = (ResourceBundle) bundles.get(basename);

		if (rb != null)
			return rb;

		if (bundles.containsKey(basename))
			return null;

		try {
			rb = ResourceBundle.getBundle(basename, defaultLocale);
		} catch (MissingResourceException e) {
			// Do nothing
		}
		bundles.put(basename, rb);
		return rb;
	}

	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	public String getString(String resourceBundle, String key) {
		ResourceBundle b = getBundle(resourceBundle);

		if (b == null) {
			Tracer.debug("ResourceBundle " + resourceBundle + " not found");
			return null;
		}

		try {
			return b.getString(key);
		} catch (MissingResourceException e) {
			Tracer.debug("resource " + key + " not found in RB " + resourceBundle);
			return null;
		}
	}

	public void customizeButton(AbstractButton but, String resourceBundle, String prop) {
		String buts = getString(resourceBundle, prop);
		String buttooltip = getString(resourceBundle, prop + "_TOOL_TIP");

		but.setText(buts != null ? buts : prop);
		if (buttooltip != null)
			but.setToolTipText(buttooltip);
	}

	public static ConzillaResourceManager getDefaultManager() {
		if (defaultManager == null)
			defaultManager = new ConzillaResourceManager();

		return defaultManager;
	}
}
