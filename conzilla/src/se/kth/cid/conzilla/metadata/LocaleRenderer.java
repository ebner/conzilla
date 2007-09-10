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

import java.util.*;
import se.kth.cid.util.*;
import javax.swing.*;
import java.awt.*;

// Watch out! Using this as renderer for large lists
// will decrease performance notably!


public class LocaleRenderer extends StringRenderer implements ListCellRenderer
{
  public LocaleRenderer()
    {
    }
  
  
  public Component getListCellRendererComponent(JList list,
						Object value,
						int index,
						boolean isSelected,
						boolean cellHasFocus)
    {
      return super.getListCellRendererComponent(list, getString((Locale) value),
						index, isSelected, cellHasFocus);
    }

  String getString(Locale l)
    {
      if(l.getLanguage().length() == 0)
	return "(none)";
      else
	return l.getDisplayName();
    }
}

