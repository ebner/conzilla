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

package se.kth.cid.conzilla.util;

import javax.swing.*;

/** This interface is the model for a grid table.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface GridTableModel
{
  /** Returns the number of rows in the table.
   *
   *  @return the number of rows in the table.
   */
  int getRowCount();
  
  /** Returns the number of columns in the table.
   *
   *  @return the number of columns in the table.
   */
  int getColumnCount();

  /** Returns the value in the specified cell.
   *
   *  @param rowIndex the row of the cell.
   *  @param columnIndex the column of the cell.
   *  @return the object to put in the cell.
   */
  Object getValueAt(int rowIndex, int columnIndex);
}
