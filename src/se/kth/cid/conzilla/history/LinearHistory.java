/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.history;

import java.net.URI;
import java.util.Vector;

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

    public void copyHistory(LinearHistory oldhist)
    {
	history.clear();
	history.addAll(oldhist.history);
	index = oldhist.index;
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
  /** Returns the map URI at the specified index in history.
   *
   * @param index the index of the entry.
   * @return the map at the given index.
   */

  public String getMapTitle(int index)
  {
    if(index >= 0 && index < history.size())
      return ((HistoryEntry) history.elementAt(index)).title;
    
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
  
  public void removeHistoryEvent(int index) {
	  history.remove(index);
	  if (this.index >= index) {
		  this.index--;
	  }
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
    if(lastMap != null && lastMap.equals(e.getDestinationURI()))
	return;

    history.addElement(new HistoryEntry(e.getDestinationURI(),
					e.getDestinationTitle()));
    index++;

  }
}
