/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;

import java.awt.GridBagConstraints;

import javax.swing.JComponent;


/** This interface creates components for a GridTable.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface GridTableComponentFactory
{
  /** Creates the component at the specified cell.
   *
   *  @param rowIndex the row of the cell.
   *  @param columnIndex the column of the cell.
   *  @param o the object to put in the cell.
   *  @return the component to put in the cell.
   */
  JComponent getComponentFor(int rowIndex, int columnIndex, Object o);

  class Constraints
  {
    public double weightx = 0.0;
    public double weighty = 0.0;
    public int    fill    = GridBagConstraints.BOTH;
    public int    anchor  = GridBagConstraints.CENTER;

    public Constraints()
      {
      }
  }

  Constraints getConstraintsFor(int rowIndex, int columnIndex);
}
