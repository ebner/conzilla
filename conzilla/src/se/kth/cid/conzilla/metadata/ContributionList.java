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


public class ContributionList extends AbstractListDisplayer
{
  Vector roles;
  Vector entities;
  Vector dates;

  Vector panels;
  
  class GridModel implements ListGridTableModel
  {
    public GridModel()
      {
      }
    
    public int getRowCount()
      {
	return panels.size();
      }

    public int getColumnCount()
      {
	return 1;
      }

    public Object getValueAt(int row, int col)
      {
	return panels.get(row);
      }

    public void setValueAt(int row, int col, Object v)
      {
	panels.set(row, v);
      }

    public String getTitle(int col)
      {
	return "Contribution";
      }
    
  }

  public ContributionList(MetaData.Contribute[] cList,
			  boolean editable)
    {
      super(editable);

      roles    = new Vector();
      entities = new Vector();
      dates    = new Vector();
      panels   = new Vector();

      if(cList != null)
	for(int i = 0; i < cList.length; i++)
	  addItem(cList[i], i);
      
      setModel(new GridModel());      
    }

  public MetaData.Contribute[] getContributes()
    {
      if(panels.size() == 0)
        return null;

      MetaData.Contribute[] contributes
        = new MetaData.Contribute[panels.size()];
      
      for(int i = 0; i < contributes.length; i++)
	{
	  contributes[i] = new MetaData.Contribute(((LangStringComponent) roles.get(i)).getLangString(),
						   ((StringList) entities.get(i)).getStrings(),
						   ((DateEdit) dates.get(i)).getDateType());
	}
      
      return contributes;
    }

  protected void removeItemImpl(int index)
    {
      roles.remove(index);
      entities.remove(index);
      dates.remove(index);
      panels.remove(index);
    }
  
  protected void createItemImpl(int index)
    {
      addItem(new MetaData.Contribute(null, null, null), index);
    }
  
  void addItem(MetaData.Contribute cont, int index)
    {
      LangStringComponent lscomp = new LangStringComponent(cont.role, editable);
      StringList entity = new StringList("VCard", cont.entity, editable);
      
      DateEdit date = new DateEdit(cont.date, editable);
      
      roles.add(index, lscomp);
      entities.add(index, entity);
      dates.add(index, date);

      JPanel panel = new JPanel();

      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

      JPanel p1 = new JPanel();
      p1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      p1.add(new JLabel("Role: "));
      p1.add(lscomp);
      JPanel p2 = new JPanel();
      p2.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      p2.add(entity);
      JPanel p3 = new JPanel();
      p3.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      p3.add(new JLabel("Date: "));
      p3.add(date);

      panel.add(p1);
      panel.add(p2);
      panel.add(p3);
      panel.setBorder(BorderFactory.createLineBorder(Color.gray));
      panels.add(index, panel);
    }
  
}


