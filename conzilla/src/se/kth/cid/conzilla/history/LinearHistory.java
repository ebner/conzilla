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
import se.kth.cid.identity.*;
import java.util.*;

/** This class manages a linear history (a'la Netscape).
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class LinearHistory
{
  /** The history.
   */
  Vector history;

  /** Index of the current map.
   */
  int index;

  /** This class represents a moment in history.
   */
  class HistoryEntry
  {
    /** The map.
     */
    URI map;

    /** The title of this entry.
     */
    String title;
    
    public HistoryEntry(URI map, String title)
    {
      this.map       = map;
      this.title     = title;
    }
    
    public String toString()
    {
      return "HistoryEntry[map = " + map + "]";
    }
  }

  /** Contructs an empty LinearHistory.
   */
  public LinearHistory()
  {
    history = new Vector();
    index = -1;
  }

  /** Returns the map URI at the specified index in history.
   *
   * @param index the index of the entry.
   * @return the map at the given index.
   */
  public URI getMapURI(int index)
  {
    if(index >= 0 && index < history.size())
      return ((HistoryEntry) history.elementAt(index)).map;
    
    return null;
  }
  
  /** Returns the titles of the maps forward in history.
   *
   *  @return the titles of the maps forward in history.
   */
  public String[] getForwardMapTitles()
  {
    if(index + 1 < history.size())
      {
	String[] titles = new String[history.size() - index - 1];
	for(int i = 0; i < history.size() - index - 1; i++)
	  titles[i] = ((HistoryEntry) history.elementAt(i + index + 1)).
	    title;
	return titles;
      }
    return new String[0];
  }

  /** Returns the titles of the maps backward in history.
   *
   *  The first one is closest in time.
   *
   *  @return the titles of the maps backward in history.
   */
  public String[] getBackwardMapTitles()
  {
    if(index > 0)
      {
	String[] titles = new String[index];
	for(int i = 0; i < index; i++)
	  titles[i] = ((HistoryEntry) history.elementAt(index - 1 - i)).
	    title;

	return titles;
      }
    return new String[0];
  }

  /** Sets the index of the current map.
   *
   *  @param index the new index of the current map.
   */
  public void setIndex(int index)
  {
    if(index < 0 || index > history.size())
      return;
    this.index = index;
    Tracer.debug("Current index: " + index);
  }

  /** Returns the index of the current map.
   *
   *  @return the index of the current map.
   */
  public int getIndex()
  {
    return index;
  }


  /** Returns the size of the history.
   *
   *  @return the size of the history.
   */
  public int getSize()
  {
    return history.size();
  }

  public String toString()
  {
    return "LinearHistory[" + history.toString() +
      ", index = " + index + "]";
  }

  /** Adds a history event to this history.
   *
   *  All history entries later than the current are forgotten.
   *
   *  @param e the history event to add.
   */
  public void historyEvent(HistoryEvent e)
  {
    if(e.getType() == HistoryEvent.CONTENT)
      return;    

    if(index >= 0)
      history.setSize(index + 1);

    URI lastMap = getMapURI(index);

    history.addElement(new HistoryEntry(e.getDestinationURI(),
					e.getDestinationTitle()));
    index++;

    Tracer.debug("HistoryEvent: " + e.getDestinationURI());
    Tracer.debug("Current index: " + index);
  }
}
