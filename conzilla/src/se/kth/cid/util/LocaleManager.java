/* $Id$ */
/*
  This file is part of the Conzilla browser, designed for
  the Garden of Knowledge project.
  Copyright (C) 1999  CID (http://www.nada.kth.se/cid)
  
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package se.kth.cid.util;

import java.util.*;
import java.beans.*;

public class LocaleManager
{
    public static final String DEFAULT_LOCALE_PROPERTY = "defaultLocale";
    public static final String LOCALES_PROPERTY = "locales";
    
    static class LocaleComparator implements Comparator
    {
	public int compare(Object o1, Object o2)
	{
	    Locale l1 = (Locale) o1;
	    Locale l2 = (Locale) o2;
	    return l1.getDisplayName().toLowerCase().compareTo(l2.getDisplayName().toLowerCase());
	}
    }
    
    Vector workingSet;
    
    Locale[] availableLocales;
    
    boolean needSortAvailable = true;
    
    PropertyChangeSupport pcs;
    
    LocaleComparator comparator;
    
    static LocaleManager manager;
    
    LocaleManager()
    {
	workingSet = new Vector();
	comparator = new LocaleComparator();
	pcs = new PropertyChangeSupport(this);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l)
    {
	pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
	pcs.removePropertyChangeListener(l);
    }
  
    public Locale[] getLocales()
    {
	return (Locale[]) workingSet.toArray(new Locale[workingSet.size()]);
    }
    
    public void addLocale(Locale l)
    {
	if(workingSet.contains(l))
	    return;
	
	workingSet.add(l);
	
	sortWorkingSet();
	
	pcs.firePropertyChange(LOCALES_PROPERTY, null, null);
    }

    public void removeLocale(Locale l)
    {
	if(!workingSet.contains(l))
	    return;
	
	workingSet.remove(l);
	
	sortWorkingSet();
	
	pcs.firePropertyChange(LOCALES_PROPERTY, null, null);
    }
    
    public void setDefaultLocale(Locale l)
    {
	Locale old = Locale.getDefault();
	
	Locale.setDefault(l);
	
	needSortAvailable = true;
	sortWorkingSet();
	
	pcs.firePropertyChange(DEFAULT_LOCALE_PROPERTY, old, l);
    }

  
    public Locale[] getAvailableLocales()
    {
	if(availableLocales == null)
	    availableLocales = Locale.getAvailableLocales();
	
	if(needSortAvailable)
	    {
		Collections.sort(Arrays.asList(availableLocales), comparator);
		needSortAvailable = false;
	    }
	
	return availableLocales;
    }
    
    void sortWorkingSet()
    {
	Collections.sort(workingSet, comparator);
    }
    
  
  public static LocaleManager getLocaleManager()
    {
	if(manager == null)
	    manager = new LocaleManager();
	return manager;
    }
}



