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
package se.kth.cid.conzilla.map.graphics;
import  se.kth.cid.conzilla.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


public class DataComponentFactory implements GridTableComponentFactory
{
  Color     textColor;
  GridTable gridTable;
  
  public DataComponentFactory(GridTable gridTable)
    {
      this.gridTable = gridTable;
      textColor      = Color.black;
    }

  public void setTextColor(Color col)
    {
      textColor = col;
    }
  public JComponent getComponentFor(final int rowIndex, final int columnIndex, Object o)
    {
      String str = (String) o;
      final JTextArea area = new JTextArea(str);
      area.setEditable(false);
      area.setDisabledTextColor(textColor);
      area.setBorder(makeBorder());
      return area;
    }
  private Border makeBorder()
    {
      return BorderFactory.createCompoundBorder(
		      BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                      BorderFactory.createEmptyBorder(2,2,2,2));
    }
}
