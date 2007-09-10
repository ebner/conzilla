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


public class DateEdit extends JPanel implements MetaDataFieldEditor
{
  StringPanel datetime;

  LangStringList description;

  
  public DateEdit(MetaData.DateType date, boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      GridBagConstraints c = new GridBagConstraints();
      c.fill   = GridBagConstraints.NONE;
      c.anchor = GridBagConstraints.WEST;
      
      setLayout(new GridBagLayout());

      if(date == null)
	date = new MetaData.DateType(null, null);

      LabelField dateTitle = new LabelField("Date/Time");
      add(dateTitle, c);

      c.gridx = 1;
      LabelField descTitle = new LabelField("Description");
      add(descTitle, c);
      
      c.gridy = 1;
      c.gridx = 0;
      datetime = new StringPanel(date.datetime, false, editable, editListener, metaDataField);
      add(datetime, c);
      
      c.gridx = 1;
      description = new LangStringList(date.description, true, editable, editListener, metaDataField);
      add(description, c);
    }

  public boolean isEdited()
    {
      return datetime.isEdited() || description.isEdited();
    }

  public void detach()
    {
      datetime.detach();
      description.detach();
    }
  
  public MetaData.DateType getDateType(boolean resetEdited)
    {
      String datetimeStr = datetime.getString(resetEdited, true);
      MetaData.LangStringType lStr = description.getLangStringType(resetEdited);

      if(datetimeStr == null && lStr == null)
	return null;
      
      return new MetaData.DateType(datetimeStr, lStr);
    }  
}
