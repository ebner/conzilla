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
import se.kth.cid.conzilla.map.*;
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import java.awt.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class MapTextArea extends JTextArea
{
  Font origFont;
  Vector mouseListeners;  
  Vector mouseMotionListeners;

  Color saveColor = Color.black;
  
  void maybeMakeListeners()
    {
      if(mouseListeners == null)
	{
	  mouseListeners = new Vector();
	  mouseMotionListeners = new Vector();
	}
    }
  
  public MapTextArea(double fontScale) 
    {
      maybeMakeListeners();
      
      //Make sure we catch some extra clicks...
      setBorder(new EmptyBorder(0, 10, 0, 10));

      int bl = getCaret().getBlinkRate();
      setCaret(new DefaultCaret() {
      	  protected  void adjustVisibility(Rectangle nloc)
      	    {
      	    }
      	});
      getCaret().setBlinkRate(bl);

      origFont = getFont().deriveFont((float) (getFont().getSize2D() * fontScale));
      
      setAutoscrolls(false);
      setOpaque(false);
      setColumns(0);
    }

  
  public void addMouseMotionListener(MouseMotionListener l)
    {
      maybeMakeListeners();
      
      if(isEditable())
	super.addMouseMotionListener(l);

      mouseMotionListeners.addElement(l);
    }

  public void addMouseListener(MouseListener l)
    {
      maybeMakeListeners();
      
      if(isEditable())
	super.addMouseListener(l);

      mouseListeners.addElement(l);
    }

  public void removeMouseMotionListener(MouseMotionListener l)
    {
      if (isEditable())
	super.removeMouseMotionListener(l);

      mouseMotionListeners.remove(l);
  }

  public void removeMouseListener(MouseListener l)
    {
      if (isEditable())
	super.removeMouseListener(l);

      mouseListeners.remove(l);
    }
  
  public void setEditable(boolean editable)
    {
      if(isEditable() == editable)
	return;
      
      super.setEditable(editable);

      Caret c = getCaret();

      // setEditable called from JTextComponent.<init> ignored here.
      if(c == null)
	return;
      
      c.setVisible(editable);
      c.setSelectionVisible(editable);

      setEnabled(editable);

      if(editable)
	{
	  saveColor = getForeground();
	  doSetColor(Color.magenta);
	}
      else
	doSetColor(saveColor);
      
      
      Enumeration en = mouseMotionListeners.elements();
      while(en.hasMoreElements())
	if(editable)
	  super.addMouseMotionListener((MouseMotionListener) en.nextElement());
	else
	  super.removeMouseMotionListener((MouseMotionListener) en.nextElement());
      
      en = mouseListeners.elements();
      while(en.hasMoreElements())
	if(editable)
	  super.addMouseListener((MouseListener) en.nextElement());
	else
	  super.removeMouseListener((MouseListener) en.nextElement());

      repaint();
    }

  
  public void setColor(Color c)
    {
      saveColor = c;

      if(!isEditable())
	doSetColor(c);
    }

  void doSetColor(Color c)
    {
      setDisabledTextColor(c);
      setForeground(c);
    }
  
  public void setScale(double scale)
    {
      setFont(origFont.deriveFont((float) (origFont.getSize2D() * scale)));
    }

  public void paintComponent(Graphics g)
    {
      if(false && !isEditable())
	MapDisplayer.setRenderingHints(g);
      super.paintComponent(g);
    }
  
}
