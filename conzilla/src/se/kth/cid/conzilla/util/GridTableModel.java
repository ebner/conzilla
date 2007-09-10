/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;


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
