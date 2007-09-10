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

package se.kth.cid.conzilla.metadata;

import se.kth.cid.util.*;

import java.util.*;
import java.awt.*;

public class LocaleManager
{
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
  
  Vector listeners;
  
  LocaleComparator comparator;

  static final LocaleManager manager = new LocaleManager();
  
  LocaleManager()
    {
      workingSet = new Vector();
      listeners = new Vector();
      comparator = new LocaleComparator();
      
    }

  public void addLocaleListener(LocaleListener l)
    {
      listeners.add(l);
    }

  public void removeLocaleListener(LocaleListener l)
    {
      listeners.remove(l);
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
      
      fireAddLocale(l);
    }

  public void removeLocale(Locale l)
    {
      Tracer.debug("removing " + l);
      if(!workingSet.contains(l))
	return;

      workingSet.remove(l);

      sortWorkingSet();
      
      fireRemoveLocale(l);
      Tracer.debug("done");
    }
  
  public void setDefaultLocale(Locale l)
    {
      Locale.setDefault(l);

      needSortAvailable = true;
      sortWorkingSet();

      fireSetDefaultLocale(l);
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
  
  void fireAddLocale(Locale l)
    {
      LocaleEvent e = new LocaleEvent(l);

      for(int i = 0; i < listeners.size(); i++)
	{
	  ((LocaleListener) listeners.get(i)).localeAdded(e);
	}
    }

  void fireRemoveLocale(Locale l)
    {
      LocaleEvent e = new LocaleEvent(l);

      for(int i = 0; i < listeners.size(); i++)
	{
	  ((LocaleListener) listeners.get(i)).localeRemoved(e);
	}
    }

  void fireSetDefaultLocale(Locale l)
    {
      LocaleEvent e = new LocaleEvent(l);

      for(int i = 0; i < listeners.size(); i++)
	{
	  ((LocaleListener) listeners.get(i)).setDefaultLocale(e);
	}
    }

  public void displayEditor(Component over)
    {
      LocaleEditor editor = new LocaleEditor(over);

      editor.show();
    }
  
  public static LocaleManager getLocaleManager()
    {
      return manager;
    }
}



