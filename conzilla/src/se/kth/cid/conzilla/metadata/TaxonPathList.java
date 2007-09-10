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


public class TaxonPathList extends AbstractListDisplayer implements MetaDataFieldEditor
{
  Vector sources;
  Vector sourcePanels;
  Vector taxonpaths;
  
  class GridModel extends ListGridTableModel
  {
    public GridModel()
      {
      }
    
    public int getRowCount()
      {
	return sources.size();
      }

    public int getColumnCount()
      {
	return 2;
      }

    public Object getValueAt(int row, int col)
      {
	if(col == 0)
	  return sourcePanels.get(row);

	return taxonpaths.get(row);
      }

    public String getTitle(int col)
      {
	if(col == 0)
	  return "Source";

	return "Taxon Path";
      }

    public GridTableComponentFactory.Constraints getConstraintsFor(int row, int col)
      {
	if(col == 0)
	  defaultConstraints.fill = GridBagConstraints.BOTH;
	else
	  defaultConstraints.fill = GridBagConstraints.NONE;
	return defaultConstraints;
      }

    
  }

  public TaxonPathList(MetaData.TaxonPath[] tList,
		       boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      super(editable, editListener, metaDataField);

      sources       = new Vector();
      sourcePanels  = new Vector();
      taxonpaths    = new Vector();

      if(tList != null)
	for(int i = 0; i < tList.length; i++)
	  addItem(tList[i], i);
      
      setModel(new GridModel());      
    }

  public MetaData.TaxonPath[] getTaxonPaths(boolean resetEdited)
    {
      if(resetEdited)
	edited = false;

      if(sources.size() == 0)
        return null;

      MetaData.TaxonPath[] taxs
        = new MetaData.TaxonPath[sources.size()];
      
      for(int i = 0; i < taxs.length; i++)
	{
	  taxs[i] = new MetaData.TaxonPath(((StringPanel) sources.get(i)).getString(resetEdited, true),
					   ((TaxonList) taxonpaths.get(i)).getTaxons(resetEdited));
	}
      
      return taxs;
    }

  protected boolean isItemEdited()
    {
      for(int i = 0; i < sources.size(); i++)
	{
	  if(((StringPanel) sources.get(i)).isEdited() ||
	     ((TaxonList) taxonpaths.get(i)).isEdited())
	    return true;
	}
      return false;
    }

  
  protected void removeItemImpl(int index)
    {
      removeAndDetach(sources, index);
      removeAndDetach(taxonpaths, index);
      
      sourcePanels.remove(index);
	  
    }
  
  protected void createItemImpl(int index)
    {
      addItem(new MetaData.TaxonPath(null, null), index);
    }
  
  void addItem(MetaData.TaxonPath tp, int index)
    {
      StringPanel         sourceComp = new StringPanel(tp.source, false, editable, editListener, metaDataField);
      TaxonList           pathComp   = new TaxonList(tp.taxon, editable, editListener, metaDataField);

      sources.add(index, sourceComp);
      taxonpaths.add(index, pathComp);

      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createLineBorder(Color.gray));
      panel.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.NONE;
      c.anchor = GridBagConstraints.WEST;
      c.weightx = 1.0;
      panel.add(sourceComp, c);

      sourcePanels.add(index, panel);
    }
  
}


