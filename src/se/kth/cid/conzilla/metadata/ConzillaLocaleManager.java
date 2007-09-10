/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.metadata;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import se.kth.cid.config.Config;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.util.LocaleManager;

public class ConzillaLocaleManager {

	LocaleManager manager;

	Config config;

	public ConzillaLocaleManager() throws IOException {
		config = ConfigurationManager.getConfiguration();
		manager = LocaleManager.getLocaleManager();
		initLocales();
	}

	void initLocales() throws IOException {
		LocaleManager locMan = LocaleManager.getLocaleManager();

		List defaultLocale = new ArrayList();
		defaultLocale.add("en");
		List locales = config.getStringList(Settings.CONZILLA_LOCALES, defaultLocale);
		Iterator localeIt = locales.iterator();
		while (localeIt.hasNext()) {
			String locale = (String)localeIt.next();
			if (locale != null) {
				locMan.addLocale(parseLocale(locale));
			}
		}

		locMan.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				saveLocale();
			}
		});
		String defStr = config.getString(Settings.CONZILLA_LOCALE_DEFAULT);
		if (defStr != null) {
			manager.setDefaultLocale(parseLocale(defStr));
		}
	}

	public static Locale parseLocale(String locale) throws IOException {
		String lang = "";
		String country = "";
		String variant = "";

		StringTokenizer st = new StringTokenizer(locale, "_");
		if (!st.hasMoreTokens())
			throw new IOException("Invalid locale: " + locale);

		lang = st.nextToken();
		if (st.hasMoreTokens())
			country = st.nextToken();
		if (st.hasMoreTokens())
			variant = st.nextToken();

		return new Locale(lang, country, variant);
	}

	public void saveLocale() {
		Locale[] locales = manager.getLocales();
		List localeList = new ArrayList();
		for (int i = 0; i < locales.length; i++) {
			localeList.add(locales[i].toString());
		}
		config.setProperties(Settings.CONZILLA_LOCALES, localeList);
		config.setProperty(Settings.CONZILLA_LOCALE_DEFAULT, Locale.getDefault().toString());
	}

}
