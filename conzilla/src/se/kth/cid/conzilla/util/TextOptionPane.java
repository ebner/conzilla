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
import javax.swing.border.*;
import java.awt.*;

/** This class is used to bring up long error messages.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class TextOptionPane extends JOptionPane 
{
  private TextOptionPane()
  {
  }

  /** Shows an error dialog with the specified message.
   *
   *  @param parent the component that is the parent of the message.
   *  @param error the error to show.
   */
  public static void showError(Component parent, String error)
  {

    JOptionPane.showMessageDialog(parent, makeTextArea(error), "Error", ERROR_MESSAGE);
  }

  /** Returns a component that contains the given text.
   *
   *  @param s the text to show.
   *  @return a component that contains the given text.
   */
  static Object makeTextArea(String s)
  {
    JTextArea area = new JTextArea(s);
    area.setEditable(false);
    area.setLineWrap(true);
    area.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
						      BorderFactory.createEmptyBorder(3,3,3,3)));
    return area;
  }
}
