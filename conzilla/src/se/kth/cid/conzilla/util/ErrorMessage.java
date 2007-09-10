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
import java.awt.event.*;
import java.io.*;
import java.util.*;

/** This class is used to bring up long error messages.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ErrorMessage 
{
  static final int ROW_LENGTH = 40;
  
  private ErrorMessage()
  {
  }

  /** Shows an error dialog with the specified message.
   *
   *  @param title the title of the dialog.
   *  @param errorMessage the error message to show (helping the user).
   *  @param ex the exception causing the error.
   *  @param parent the component that is the parent of the message.
   */
  public static void showError(String title, String errorMessage,
			       Exception ex, Component parent)
    {
      Object[] options = null;
      int okOpt = 0;
      if(ex != null)
	{
	  options = new Object[]{"Details", "Ok"};
	  okOpt = 1;
	}
      else
	{
	  options = new Object[]{"Ok"};
	  okOpt = 0;
	}
	
      int result = JOptionPane.showOptionDialog(parent,
						breakString(title + ":\n\n" +
							    errorMessage),
						title,
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.ERROR_MESSAGE,
						null,
						options,
						options[okOpt]);

    if(result == okOpt || result == JOptionPane.CLOSED_OPTION)
      return;

    detailDialog(title, errorMessage, ex, parent);
  }
  
  static void detailDialog(String title, String errorMessage,
			   Exception ex, Component parent)
    {
      final JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(parent),
					 title, true);

      dialog.getContentPane().setLayout(new BorderLayout());
      
      if(ex != null)
	{
	  JTabbedPane tabPane = new JTabbedPane();
	  tabPane.addTab("Description", makeDescription(title, errorMessage, ex));
	  tabPane.addTab("Exception", makeExceptionMessage(title, errorMessage, ex));
	  dialog.getContentPane().add(tabPane, BorderLayout.CENTER);
	}
      else
	dialog.getContentPane().add(makeDescription(title, errorMessage, null),
				    BorderLayout.CENTER);

      JButton close = new JButton("Close");

      close.addActionListener(new ActionListener()
	{
	  public void actionPerformed(ActionEvent e)
	    {
	      dialog.dispose();
	    }
	});
      
      Box box = new Box(BoxLayout.X_AXIS);

      box.add(Box.createHorizontalGlue());

      box.add(close);

      dialog.getContentPane().add(box, BorderLayout.SOUTH);

      dialog.pack();
      dialog.setLocationRelativeTo(parent);
      dialog.show();
      
    }
  

  static JComponent makeDescription(String title, String errorMessage, Exception ex)
  {
    JTextArea area = new JTextArea();
    area.setEditable(false);
    area.setLineWrap(false);

    area.setText(title + ":\n\n" +
		 errorMessage + "\n\n" +
		 "Details:\n\n " +
		 ex.getMessage());
    JScrollPane pane = new JScrollPane(area);

    pane.setMinimumSize(new Dimension(400, 150));
    pane.setPreferredSize(new Dimension(400, 150));
    return pane;
  }

  static JComponent makeExceptionMessage(String title, String errorMessage, Exception ex)
  {
    JTextArea area = new JTextArea();
    area.setEditable(false);
    area.setLineWrap(false);

    StringWriter writer = new StringWriter(1024);

    ex.printStackTrace(new PrintWriter(writer));

    area.setText(writer.toString());
    
    JScrollPane pane = new JScrollPane(area);

    pane.setMinimumSize(new Dimension(400, 150));
    pane.setPreferredSize(new Dimension(400, 150));
    return pane;
  }

  static String breakString(String s)
    {
      StringBuffer out = new StringBuffer();

      int start = 0;
      int next = 0;
      while(start < s.length())
	{
	  next = s.indexOf('\n', start);
	  if(next == -1)
	    next = s.length();
	  
	  String line = s.substring(start, next);
	  
	  int l = line.length();

	  for(int i = 0; i <= l; i += ROW_LENGTH)
	    {
	      out.append(line.substring(i, Math.min(i + ROW_LENGTH, l)));
	      out.append('\n');
	    }
	  start = next + 1;
	}
      return out.toString();
    }
	  
	  
}
