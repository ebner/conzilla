/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.util;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JToolBar;

/** This is a variant of a toolbar that has some control over it's
 *  drag-out appearance. Call the pack() method after changing anything.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class CToolBar extends JToolBar
{
  /** Constructs a CToolBar with the given name.
   *
   * @param name the name of the toolbar.
   */
  public CToolBar(String name)
  {
    super();
    setName(name);
  }

  /** Sets the name of the toolbar.
   *
   * @param name the name of the toolbar.
   */
  public void setName(String name)
  {
    super.setName(name);
    updateUI();
  }

  public void pack()
  {
    Container cont = getTopLevelAncestor();
    
    if(cont instanceof JFrame)
      {
	JFrame frame = (JFrame) cont;
	if(getName() != null && getName().equals(frame.getTitle()))
	  frame.pack();
      }
  }
}
