/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;

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

