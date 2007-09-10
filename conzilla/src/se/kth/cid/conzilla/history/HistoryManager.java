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

package se.kth.cid.conzilla.history;

import se.kth.cid.util.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.component.*;
import se.kth.cid.identity.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.controller.*;
import java.util.*;

public class HistoryManager
{
  Vector historyListeners;
  ComponentStore store;
  
  public HistoryManager(ComponentStore store)
    {
      this.store = store;
      historyListeners = new Vector();
    }


  URI getURI(Component comp)
    {
      if(comp == null)
	return null;
      
      return URIClassifier.parseValidURI(comp.getURI());
    }

  URI getURI(NeuronStyle ns)
    {
      return URIClassifier.parseValidURI(ns.getNeuronURI(),
					 ns.getConceptMap().getURI());
    }

  URI getDetailedMapURI(NeuronStyle ns)
    {
      return URIClassifier.parseValidURI(ns.getDetailedMap(),
					 ns.getConceptMap().getURI());
    }

  String getTitle(Component comp)
    {
      if(comp == null)
	return null;
      
      MetaData md = comp.getMetaData();
      return MetaDataUtils.getLocalizedString(md.get_metametadata_language(), md.get_general_title()).string;
    }

  String getTitle(NeuronStyle ns)
    {
      return getTitle(store.getCache().getComponent(getURI(ns).toString()));
    }
  
  
  public void fireDetailedMapEvent(MapController source, NeuronStyle ns)
    {
      ConceptMap map = ns.getConceptMap();
      URI destURI = getDetailedMapURI(ns);
      
      fireHistoryEvent(new HistoryEvent(HistoryEvent.MAP, source,
					getURI(map), getTitle(map),
					getURI(ns), getTitle(ns),
					destURI, getTitle(store.getCache().getComponent(destURI.toString()))));
    }

  public void fireOpenNewMapEvent(MapController source, ConceptMap oldMap,
				  URI newMap)
    {
      fireHistoryEvent(new HistoryEvent(HistoryEvent.MAP, source,
					getURI(oldMap), getTitle(oldMap),
					null, null,
					newMap,
					getTitle(store.getCache().getComponent(newMap.toString()))));
    }

  public void fireContentViewEvent(MapController source, NeuronStyle ns,
				   URI content)
    {
      ConceptMap map = source.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
      
      fireHistoryEvent(new HistoryEvent(HistoryEvent.CONTENT, source,
					getURI(map), getTitle(map),
					getURI(ns), getTitle(ns),
					content,
					getTitle(store.getCache().getComponent(content.toString()))));
    }

  
						     
						     
  public void addHistoryListener(HistoryListener l)
    {
      historyListeners.addElement(l);
    }
  
  public void removeHistoryListener(HistoryListener l)
    {
      historyListeners.removeElement(l);
    }
  
  public void fireHistoryEvent(HistoryEvent e)
    {
      for(int i = 0; i < historyListeners.size(); i++)
	{
	  ((HistoryListener) historyListeners.elementAt(i)).historyEvent(e);
	}
    }
  
}
