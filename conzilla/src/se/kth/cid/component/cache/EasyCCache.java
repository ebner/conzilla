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
import  se.kth.cid.util.*;


import java.util.*;

/** Used to cache Components, keeps a reference count.
 *  It could be implemented in a number of ways.
 */
public class EasyCCache implements ComponentCache
{
  ComponentLoader componentloader;
  Hashtable cache;
  
  private class CompCount
  {
    private Component component;
    private int referencecount;
    
    CompCount(Component component)
      {
	this.component=component;
	referencecount=1;
      }
    private Component getComponent() {return component;}
    private int increaseCount() {return ++referencecount;}
    private int decreaseCount() {return --referencecount;}
    private int whatIsCount() {return referencecount;}
  }

  public EasyCCache(ComponentLoader complod)
    {
      componentloader=complod; 
      cache=new Hashtable();
    }

  /** Finds a component within the cache and returns it.
    * @param uri the URI of the component to find.
    * @returns a Component if it does exist, otherwise null.
    */
  public Component getComponent(URI uri)
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
  public Component loadComponent(URI uri, ComponentLoader recursiveLoader) throws ComponentException
    {
      CompCount cc;
      if ((cc = (CompCount) cache.get(uri)) != null ) {
	cc.increaseCount();
	return cc.getComponent();
      }
      cc = new CompCount(componentloader.loadComponent(uri, recursiveLoader));
      cache.put(uri,cc);

      return cc.getComponent();
    }
  
  /** Decreases the reference count by one,
    * if already zero it removes it from the cache.
    * @param uri belonging to the Component to purge
    * @returns Component , null when Component with URI uri 
    * not is in the cache.
    */
  public void releaseComponent(Component comp)
    {
      CompCount cc;
      String uri = comp.getURI();

      if ((cc = (CompCount) cache.get(uri)) != null ) 
	{ 
	  cc.decreaseCount();
	  comp=cc.getComponent();
	  if (cc.whatIsCount() <= 0 )
	    cache.remove(uri);
	}
    }
  public void renameComponent(URI olduri, URI newuri)
    {
      CompCount cc;
      if ((cc = (CompCount) cache.remove(olduri)) != null ) 
	{
	  cache.put(newuri,cc);
	  Tracer.debug("##########renamed "+olduri.toString());
	  if (cache.get(olduri.toString())!=null)
	    Tracer.debug("####### old uri isn't removed from cache");
	}
      else
	Tracer.debug("#######Failed to rename within cache.");
    }
  public void printStatus()
    {
      System.out.println("------print status of cache------");
      Enumeration en=cache.elements();
      for (;en.hasMoreElements();)
	{
	  CompCount cc=(CompCount) en.nextElement();
	  System.out.print("URI="+cc.getComponent().getURI().toString());
	  System.out.print(" with reference count ");
	  System.out.print(cc.whatIsCount());
	  System.out.println("");
	}
      System.out.println("---------------------------------");
    }
}

