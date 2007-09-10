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
import java.lang.ref.*;

/** Component cache.
 */
public class SoftCache implements ComponentCache
{
  public static final int CHECK_COUNT_INTERVAL = 100;
  public static final long CHECK_TIME_INTERVAL = 15*60*1000;
  public static final long CACHE_RELEASE_TIME  = 15*60*1000;

  /** The table mapping URIs (String) -> ComponentRef
   */
  Hashtable cache;

  ReferenceQueue queue;

  long lastCheckedTime;
  int  checkCount;
  
  private class ComponentRef extends SoftReference
  {
    String    compURI;
    int       refs;
    long      referredTime;
    Component self;
    
    ComponentRef(Component component, ReferenceQueue queue)
      {
	super(component, queue);

	self = component;
	compURI = component.getURI();
	refs = 0;
	referredTime = System.currentTimeMillis();
      }

    String getURI()
      {
	return compURI;
      }
    
    Component getComponent()
      {
	return (Component) get();
      }

    int getReferences()
      {
	return refs;
      }

    long getReferredTime()
      {
	return referredTime;
      }
    
    void referred()
      {
	referredTime = System.currentTimeMillis();
	refSelf();
	++refs;
      }

    void unrefSelf()
      {
	self = null;
      }

    void refSelf()
      {
	self = (Component) get();
      }
  }

  public SoftCache()
    {
      cache = new Hashtable();
      queue = new ReferenceQueue();
      lastCheckedTime = System.currentTimeMillis();
      checkCount = 0;
    }

  protected void checkQueue()
    {
      Reference r;
      while((r = queue.poll()) != null)
	{
	  Tracer.debug("Removing from cache: " + ((ComponentRef) r).getURI());
	  cache.remove(((ComponentRef) r).getURI());
	}
    }


  public void checkCache()
    {
      checkQueue();

      if(++checkCount < CHECK_COUNT_INTERVAL)
	return;

      long time = System.currentTimeMillis();

      if(time - lastCheckedTime < CHECK_TIME_INTERVAL)
	return;

      checkCount = 0;
      lastCheckedTime = time;

      doCheckCache(time);
    }

  protected void doCheckCache(long time)
    {
      Iterator iter = cache.values().iterator();
      while(iter.hasNext())
	{
	  ComponentRef r = (ComponentRef) iter.next();
	  if(time - r.getReferredTime() > CACHE_RELEASE_TIME)
	    r.unrefSelf();
	}
    }
  
  public Component getComponent(String uri)
    {
      checkCache();
      
      ComponentRef cr = (ComponentRef) cache.get(uri);

      if(cr != null)
	{
	  cr.referred();
	  return cr.getComponent();
	}
      
      return null;
    }

  public void referenceComponent(Component comp)
    {
      checkCache();

      String uri = comp.getURI();
      
      ComponentRef cr = (ComponentRef) cache.get(uri);
      
      if(cr != null)
	{
	  cr.referred();
	  return;
	}

      cache.put(comp.getURI(), new ComponentRef(comp, queue));
    }

  public void clear()
    {
      cache.clear();
      queue = new ReferenceQueue(); 
    }

  public String toString()
    {
      StringBuffer b = new StringBuffer();
      b.append("se.kth.cid.component.cache.SoftCache[");
      
      Iterator iter = cache.values().iterator();
      while(iter.hasNext())
	{
	  ComponentRef r = (ComponentRef) iter.next();
	  b.append("(" + r.getURI() + "," + r.getComponent() + ")");
	}
      b.append("]");
      return b.toString();
    }
  
}

