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

import se.kth.cid.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

/** This class implements a simple table using a grid of components.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class GridTable extends JPanel
{
  /** The model to use.
   */
  protected GridTableModel model;

  /** The component factory to use.
   */
  GridTableComponentFactory factory;

  /** The components.
   *
   *  This vector contains the rows, as vectors.
   */
  Vector components;

  /** Constructs an empty grid table.
   */
  public GridTable()
  {
    setLayout(new GridBagLayout());
    components = new Vector();
  }

  /** Sets the model of this grid table.
   *
   *  @param model the model to use.
   */
  public void setModel(GridTableModel model)
  {
    this.model = model;
    update();
  }

  /** Returns the model of this grid table.
   *
   *  @return the model of this grid table.
   */
  public GridTableModel getModel()
  {
    return model;
  }

  /** Returns the component factory of this grid table.
   *
   *  @return the component factory of this grid table.
   */
  public GridTableComponentFactory getFactory()
  {
    return factory;
  }

  /** Returns the component at the given cell.
   *
   *  @param row the row of the component.
   *  @param column the column of the component.
   *  @return the component int the specified cell.
   */
  public JComponent getComponentAtCell(int row, int column)
  {
    return (JComponent) ((Vector) components.elementAt(row)).elementAt(column);
  }
  
  /** Sets the component factory of this grid table.
   *
   *  @param factory the component factory to use.
   */
  public void setFactory(GridTableComponentFactory factory)
  {
    this.factory = factory;
    update();
  }

  /** Removes allold components, creating new for all cells.
   *
   */
  public void update()
  {
    removeAll();

    components = new Vector();
    
    if(model == null || factory == null)
      return;

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    for(int i = 0; i < model.getRowCount(); i++)
      {
	c.gridy = i;
	Vector thisRow = new Vector();
	components.addElement(thisRow);
	for(int j = 0; j < model.getColumnCount(); j++)
	  {
	    c.gridx = j;
	    GridTableComponentFactory.Constraints constr = factory.getConstraintsFor(i, j);
	    c.fill    = constr.fill;
	    c.anchor  = constr.anchor;
	    c.weightx = constr.weightx;
	    c.weighty = constr.weighty;

	    JComponent comp = factory.getComponentFor(i,j, model.getValueAt(i, j));
	    add(comp, c);
	    thisRow.addElement(comp);
	  }
      }
    revalidate();
    repaint();
  }
}

