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


package se.kth.cid.conceptmap.local;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;
import java.util.*;

/** 
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class LocalMapGroupStyle implements MapGroupStyle, LayerStyle
{

    
    Vector  order;
    Hashtable tags;
    Hashtable objectStyles;
    Hashtable id2os;
    Hashtable id2mgs;
    HashSet hideIDs;
    
    String id;
    ConceptMap cMap;


    public LocalMapGroupStyle(String id, ConceptMap cMap)
    {
	this.id = id;
	this.cMap = cMap;
	order = new Vector();
	objectStyles = new Hashtable();
	tags = new Hashtable();
	id2os = new Hashtable();
	id2mgs = new Hashtable();
	hideIDs = new HashSet();
    }    

    public String getURI()
    {
	return id;
    }

    public ConceptMap getConceptMap()
    {
	return cMap;
    }

    public void addObjectStyle(ObjectStyle objectStyle, Object tag)
    {
	//hashtables doesn't accept null, hence we take another specific object
	//to represent 'no tag'.
	if (tag == null)
	    tag = this;
	else if (!tags.contains(tag))
	    tags.put(tag, Boolean.TRUE);

	objectStyles.put(objectStyle, tag);
	order.add(objectStyle);
	id2os.put(objectStyle.getURI(), objectStyle);
	if (objectStyle instanceof MapGroupStyle)
	    id2mgs.put(objectStyle.getURI(), objectStyle);
    }

    public boolean removeObjectStyle(ObjectStyle objectStyle)
    {
	Object tag = objectStyles.get(objectStyle);
	if (tag == null)
	    return false;
	objectStyles.remove(objectStyle);
	order.remove(objectStyle);
	id2os.remove(objectStyle.getURI());
	hideIDs.remove(objectStyle.getURI());

	//Relevant only if objectStyle is a MapGroupStyle
	id2mgs.remove(objectStyle.getURI());

	if (tag!=this && !objectStyles.contains(tag))
	    tags.remove(tag);
	return true;
    }	
    
    public void setObjectStyleHidden(String mapID, boolean hidden)
    {
	if (getObjectStyle(mapID)==null)
	    return;
	if (hidden)
	    hideIDs.add(mapID);
	else
	    hideIDs.remove(mapID);
    }

    public boolean getObjectStyleHidden(String mapID)
    {
	if (getObjectStyle(mapID)==null)
	    return false;
	return hideIDs.contains(mapID);
    }
	
    public boolean recursivelyRemoveObjectStyle(ObjectStyle objectStyle)
    {
	if (removeObjectStyle(objectStyle))
	    return true;
	
	Enumeration en = id2mgs.elements();
	while (en.hasMoreElements())
	    {
		if(((MapGroupStyle) en.nextElement()).recursivelyRemoveObjectStyle(objectStyle))
		    return true;
	    }
	return false;
    }

    public MapGroupStyle getParent(ObjectStyle os)
    {
 	if (objectStyles.containsKey(os))
	    return this;

	Enumeration en = id2mgs.elements();
	while (en.hasMoreElements())
	    {
		MapGroupStyle mgs = ((MapGroupStyle) en.nextElement()).getParent(os);
		if (mgs != null)
		    return mgs;
	    }
	return null;
    }

    public Object getObjectStyleTag(ObjectStyle os)
    {
	return objectStyles.get(os);
    }

    public Vector getObjectStyles()
    {
	return order;
    }

    public int getNumberOfObjectStyles()
    {
	return order.size();
    }
    public ObjectStyle getObjectStyle(String id)
    {
	return (ObjectStyle) id2os.get(id);
    }

    public ObjectStyle recursivelyGetObjectStyle(String id)
    {
	ObjectStyle os = getObjectStyle(id);
	if (os == null)
	    {
		Enumeration en = id2mgs.elements();
		while (en.hasMoreElements())
		    {
			os = ((MapGroupStyle) en.nextElement()).recursivelyGetObjectStyle(id);
			if (os != null)
			    return os;
		    }
	    }
	return os;
    }

    public void setTagVisible(Object tag, boolean visible)
    {
	if (tags.containsKey(tag))
	    {
		tags.remove(tag);
		if (visible)
		    tags.put(tag, Boolean.TRUE);
		else
		    tags.put(tag, Boolean.FALSE);
	    }
    }

    public boolean getTagVisible(Object tag)
    {
	Boolean bo = (Boolean) tags.get(tag);
	if (bo != null)
	    return bo.booleanValue();
	return false;
    }

    public Enumeration getTags()
    {
	return tags.keys();
    }

    public void lowerObjectStyle(ObjectStyle os)
    {
	order.remove(os);
	order.add(os);
    }

    public void raiseObjectStyle(ObjectStyle os)
    {
	order.remove(os);
	order.insertElementAt(os, 0);
    }

    public int getOrderOfObjectStyle(ObjectStyle os)
    {
	return order.indexOf(os);
    }

    public void setOrderOfObjectStyle(ObjectStyle os, int position)
    {
	if (position >= order.size())
	    return;
	order.remove(os);
	order.insertElementAt(os, position);
    }

    public Vector getObjectStyles(int visible, Class restrictToType)
    {
	Vector collect = new Vector();
	getObjectStyles(collect, visible, restrictToType);
	return collect;
    }

    public Hashtable getHashedObjectStyles(int visible, Class restrictToType)
    {
	Hashtable collect = new Hashtable();
	getObjectStyles(collect, visible, restrictToType);
	return collect;
    }

    public void getObjectStyles(Object collect, int visible, Class restrictToType)
    {
	Dictionary dict = null;
	Collection col = null;
	if (collect instanceof Dictionary)
	    dict = (Dictionary) collect;
	else
	    col = (Collection) collect;

	Enumeration or = order.elements();
	while (or.hasMoreElements())
	    {
		ObjectStyle os = (ObjectStyle) or.nextElement();
		switch (visible)
		    {
		    case ONLY_VISIBLE:
			if (hideIDs.contains(os.getURI()) || 
			    tags.get(objectStyles.get(os)) == Boolean.FALSE)
			    continue;
			break;
		    case ONLY_INVISIBLE:
			if (!hideIDs.contains(os.getURI()) &&
			    tags.get(objectStyles.get(os)) == Boolean.TRUE)
			    continue;
			break;			
			// If IGNORE_VISIBILITY always show.
		    }
		if (restrictToType == null || restrictToType.isInstance(os))
		    if (dict == null)
			col.add(os);
		    else
			dict.put(os.getURI(), os);

		if (os instanceof MapGroupStyle)
		    ((MapGroupStyle) os).getObjectStyles(collect, visible, restrictToType);
	    }
    }

    public Set IDSet()
    {
	HashSet collect = new HashSet();
	IDSet(collect);
	return collect;	
    }

    public void IDSet(Set collect)
    {
	collect.addAll(id2os.keySet());
	Enumeration grp = id2mgs.elements();
	while (grp.hasMoreElements())
	    ((MapGroupStyle) grp.nextElement()).IDSet(collect);
    }
}


    /*    
    public boolean recursiveContainsObjectStyle(ObjectStyle os)
    {
	if (objectStyles.get(os)!=null)
	    return true;
	Enumeration en = objectStyles.keys();
	while (en.hasMoreElements())
	    {
		ObjectStyle ros = (ObjectStyle) en.nextElement();
		if (ros instanceof LocalMapGroupStyle && 
		    ((LocalMapGroupStyle) ros).recursiveContainsObjectStyle(os))
		    return true;
	    }
	return false;
    }
        public boolean containsObjectStyle(ObjectStyle os)
    {
	return objectStyles.get(os)!=null;
	}	*/
    
