/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import se.kth.nada.kmr.shame.applications.util.MetaDataPanel;
import se.kth.nada.kmr.shame.form.impl.LanguageImpl;

public class LocaleManager {
    public static final String DEFAULT_LOCALE_PROPERTY = "defaultLocale";

    public static final String LOCALES_PROPERTY = "locales";

    static class LocaleComparator implements Comparator<Locale> {
        public int compare(Locale l1, Locale l2) {
            return l1.getDisplayName().toLowerCase().compareTo(
                    l2.getDisplayName().toLowerCase());
        }
    }

    Vector<Locale> workingSet;

    Locale[] availableLocales;

    boolean needSortAvailable = true;

    PropertyChangeSupport pcs;

    LocaleComparator comparator;

    static LocaleManager manager;

    LocaleManager() {
        workingSet = new Vector<Locale>();
        comparator = new LocaleComparator();
        pcs = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    public Locale[] getLocales() {
        return (Locale[]) workingSet.toArray(new Locale[workingSet.size()]);
    }

    public void addLocale(Locale l) {
        if (workingSet.contains(l))
            return;

        workingSet.add(l);

        sortWorkingSet();

        updateShame();
        
        pcs.firePropertyChange(LOCALES_PROPERTY, null, null);
    }

    public void removeLocale(Locale l) {
        if (!workingSet.contains(l))
            return;

        workingSet.remove(l);

        sortWorkingSet();

        updateShame();
        
        pcs.firePropertyChange(LOCALES_PROPERTY, null, null);
    }

    public void setDefaultLocale(Locale l) {
        Locale old = Locale.getDefault();

        // Remove this to always take the locale from the OS
        Locale.setDefault(l);

        needSortAvailable = true;
        sortWorkingSet();

        MetaDataPanel.setDefaultLocale(l);

        pcs.firePropertyChange(DEFAULT_LOCALE_PROPERTY, old, l);
    }

    public Locale[] getAvailableLocales() {
        if (availableLocales == null)
            availableLocales = Locale.getAvailableLocales();

        if (needSortAvailable) {
            Collections.sort(Arrays.asList(availableLocales), comparator);
            needSortAvailable = false;
        }

        return availableLocales;
    }

    void sortWorkingSet() {
        Collections.sort(workingSet, comparator);
    }

    public static LocaleManager getLocaleManager() {
        if (manager == null)
            manager = new LocaleManager();
        return manager;
    }

    public void updateShame() {
        ArrayList<LanguageImpl> list = new ArrayList<LanguageImpl>();
        for (Iterator langs = workingSet.iterator(); langs.hasNext();) {
            Locale l = (Locale) langs.next();
            list.add(new LanguageImpl(l.getLanguage(), l.getCountry(), l.getDisplayLanguage(), l.getDisplayLanguage()));
        }
        MetaDataPanel.setLanguages(list);
    }

}