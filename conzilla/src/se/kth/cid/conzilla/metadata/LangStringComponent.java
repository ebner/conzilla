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


public class LangStringComponent extends JPanel
{
  LanguageBox languageBox;

  JTextArea  stringArea;
  
  public LangStringComponent(MetaData.LangString str, boolean editable)
    {
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.BOTH;
      
      setLayout(new GridBagLayout());
      
      languageBox = new LanguageBox(str.language, editable);
      add(languageBox, c);
      
      stringArea = new JTextArea(1, 6);
      stringArea.setText(str.string);
      stringArea.setBorder(BorderFactory.createLoweredBevelBorder());
      c.gridx = 1;
      add(stringArea, c);

      stringArea.setEditable(editable);
    }

  public MetaData.LangString getLangString()
    {
      return new MetaData.LangString(languageBox.getLanguage(), stringArea.getText());
    }  
}
