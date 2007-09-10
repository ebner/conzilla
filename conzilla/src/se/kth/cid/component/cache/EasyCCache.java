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


package se.kth.cid.component.cache;
import  se.kth.cid.component.*;
import  se.kth.cid.identity.*;
import  se.kth.cid.util.*;


import java.util.*;

/** Used to cache Components, keeps a reference count.
 */
public class EasyCCache implements ComponentCache
{
  Hashtable cache;
  
  private class CompCount
  {
    Component component;
    int referencecount;
    
    CompCount(Component component)
      {
	this.component=component;
	referencecount=1;
      }
    Component getComponent() {return component;}
    int increaseCount() {return ++referencecount;}
    int decreaseCount() {return --referencecount;}
    int getCount() {return referencecount;}
  }

  public EasyCCache()
    {
      cache = new Hashtable();
    }

  /** Finds a component within the cache and returns it.
    * @param uri the URI of the component to find.
    * @returns a Component if it does exist, otherwise null.
    */
  public Component getComponent(String uri)
    {
      CompCount cc = (CompCount) cache.get(uri);

      if(cc != null)
	return cc.getComponent();
      return null;
    }

  /** Loads a component, if it already exists in cache 
    * the referencecount is increased by one.
    * Throws out something if the cache is full.
    * @param uri the URI of the Component to load.
    * @returns a Component if it can be loaded, otherwise null. 
    */
  public int referenceComponent(Component comp)
    {
      CompCount cc;
      if ((cc = (CompCount) cache.get(comp.getURI())) != null )
	return cc.increaseCount();
      else
	{
	  cache.put(comp.getURI(), new CompCount(comp));
	  return 1;
	}
    }
  
  /** Decreases the reference count by one,
    * if already zero it removes it from the cache.
    * @param uri belonging to the Component to purge
    */
  public int dereferenceComponent(String uri)
    {
      CompCount cc;

      if ((cc = (CompCount) cache.get(uri)) != null ) 
	{ 
	  cc.decreaseCount();
	  if (cc.getCount() <= 0 )
	    cache.remove(uri);
	  return cc.getCount();
	}
      else
	throw new IllegalStateException("No such Component in cache: '"
					+ uri + "'");
    }

  public void printStatus()
    {
      System.out.println("------print status of cache------");
      Enumeration en=cache.elements();
      for (;en.hasMoreElements();)
	{
	  CompCount cc=(CompCount) en.nextElement();
	  System.out.print("URI="+cc.getComponent().getURI());
	  System.out.print(" with reference count ");
	  System.out.print(cc.getCount());
	  System.out.println("");
	}
      System.out.println("---------------------------------");
    }
}

