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
import se.kth.cid.component.MetaData;
import se.kth.cid.component.MetaDataUtils;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class LocationList extends AbstractListDisplayer implements MetaDataFieldEditor
{
  Vector textURIs;
  Vector locations;

  ItemListener listener;
  
  class GridModel extends ListGridTableModel
  {
    public GridModel()
      {
      }
    
    public int getRowCount()
      {
	return textURIs.size();
      }

    public int getColumnCount()
      {
	return 2;
      }

    public Object getValueAt(int row, int col)
      {
	if(col == 0)
	  return textURIs.get(row);

	return locations.get(row);
      }

    public String getTitle(int col)
      {
	if(col == 0)
	  return "Type";

	return "Location";
      }  
  }
  
  public LocationList(MetaData.Location[] locs,
		      boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      super(editable, editListener, metaDataField);

      listener = new ItemListener() {
	  public void itemStateChanged(ItemEvent e)
	    {
	      if(e.getStateChange() == ItemEvent.SELECTED)
		fireEdited();
	    }
	};
  
      textURIs  = new Vector();
      locations = new Vector();
      
      if(locs != null)
	for(int i = 0; i < locs.length; i++)
	  addItem(locs[i], i);
      
      setModel(new GridModel());      
    }
  
  public MetaData.Location[] getLocations(boolean resetEdited)
    {
      if(resetEdited)
	edited = false;

      if(textURIs.size() == 0)
        return null;

      MetaData.Location[] locs
        = new MetaData.Location[textURIs.size()];
      
      for(int i = 0; i < locs.length; i++)
	{
	  String textURI;
	  if(editable)
	    textURI = (String) ((JComboBox) textURIs.get(i)).getSelectedItem();
	  else
	    textURI = ((StringPanel) textURIs.get(i)).getString(resetEdited, false);
	  
	  locs[i] = new MetaData.Location(textURI, ((StringPanel) locations.get(i)).getString(resetEdited, false));
	}
      
      return locs;
    }

  protected boolean isItemEdited()
    {
      for(int i = 0; i < textURIs.size(); i++)
	{
	  if(((StringPanel) locations.get(i)).isEdited())
	    return true;
	}
      return false;
    }

  protected void removeItemImpl(int index)
    {
      if(editable)
	textURIs.remove(index);
      else
	removeAndDetach(textURIs, index);
      
      removeAndDetach(locations, index);
    }
  
  protected void createItemImpl(int index)
    {
      addItem(new MetaData.Location("URI", null), index);
    }
  
  void addItem(MetaData.Location loc, int index)
    {
      if(editable)
	{
	  JComboBox textURI = new JComboBox();
	  textURI.addItem("URI");
	  textURI.addItem("TEXT");
	  textURI.setSelectedItem(loc.type);
	  textURI.setRenderer(new StringRenderer());
	  textURI.setBackground(Color.white);
	  textURI.addItemListener(listener);
	  textURIs.add(index, textURI);
	}
      else
	{
	  StringPanel textURI = new StringPanel(loc.type, false, false, editListener, metaDataField);
	  textURIs.add(index, textURI);
	}
      
      StringPanel location = new StringPanel(loc.string, false, editable, editListener, metaDataField);
      
      locations.add(index, location);
    }
  
}


