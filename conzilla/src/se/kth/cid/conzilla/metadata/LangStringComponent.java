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


public class LangStringComponent extends JPanel implements MetaDataFieldEditor
{
  LanguageBox languageBox;

  StringPanel  stringArea;

  
  public LangStringComponent(MetaData.LangString str, boolean textFlow, boolean editable, MetaDataEditListener editListener, String metaDataField)
    {      
      setLayout(new GridBagLayout());

      String lang = null;
      if(str != null)
	lang = str.language;
      
      languageBox = new LanguageBox(lang, editable, editListener, metaDataField);

      String string = null;
      if(str != null)
	string = str.string;
      
      stringArea = new StringPanel(string, textFlow, editable, editListener, metaDataField);

      
      GridBagConstraints c = new GridBagConstraints();
      c.gridx = c.gridy = 0;
      c.fill = GridBagConstraints.NONE;
      c.anchor = GridBagConstraints.WEST;
      add(languageBox, c);

      c.gridx = 1;
      c.weightx = 1.0;
      add(stringArea, c);

      stringArea.setEditable(editable);
    }

  public boolean isEdited()
    {
      return languageBox.isEdited() || stringArea.isEdited();
    }


  public void detach()
    {
      languageBox.detach();
      stringArea.detach();
    }
  
  public MetaData.LangString getLangString(boolean resetEdited)
    {
      String str = stringArea.getString(resetEdited, false);
      String lang = languageBox.getLanguage(resetEdited);

      if(str.length() == 0 && lang == null)
	return null;
      
      return new MetaData.LangString(lang, str);
    }
}
