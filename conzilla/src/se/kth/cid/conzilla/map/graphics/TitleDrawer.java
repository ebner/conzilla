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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class TitleDrawer implements CaretListener, FocusListener
{
  Color     textColor;
  JTextArea text;
  Rectangle bb;
  NeuronMapObject neuronMapObject;
  int boxType;

  String oldText;
  
  public TitleDrawer(NeuronMapObject neuronMapObject, MapDisplayer.MapComponentDrawer parent)
  {
    this.neuronMapObject = neuronMapObject; 
    text = new JTextArea();

    //      text.setFont(new Font("Serif", Font.ITALIC, 16));
    text.setLineWrap(true);
    text.setWrapStyleWord(false);
    text.setOpaque(false);
    text.setBorder(new EmptyBorder(2,2,1,2)); // top, left, bottom, right insets.
    setEditable(false);		//important to set this before addCaretListener
				//since otherwise update() is called before
				//update(re, bt) .
    parent.add(text);
    text.addCaretListener(this);
    text.addFocusListener(this);
  }

  public void detach()
    {
      text.removeCaretListener(this);
      text.removeFocusListener(this);
      text = null;
      bb   = null;
      neuronMapObject = null;
    }
  
  public void setEditable(boolean editable)
    {
      if (boxType != BoxDrawer.TYPE)
	{
	  text.getCaret().setSelectionVisible(editable);
	  text.setEditable(editable);
	}
    }
  
  public void paint(Graphics g, Color over) 
    {
      if (over != null)
	text.setDisabledTextColor(over);
      else
	text.setDisabledTextColor(textColor);	  
    }
  
  public void update(Rectangle re, int boxtype)
  {
    bb = re;  // re can be null, don't use before you checked if boxtype==BoxDrawer.NONE
    this.boxType = boxType;

    textColor = Color.black;

    text.setDisabledTextColor(textColor);

    //    if (boxType == BoxDrawer.TYPE)
    //      text.setText("<<" + neuronMapObject.getNeuronType().getMetaData().getValue("Title")+">>");
    //    else

    oldText = neuronMapObject.getNeuronStyle().getTitle();
    text.setText(oldText);

    update();
  }
  
  public void update()
    {
      if (boxType != BoxDrawer.NONE)
	{
	  FontMetrics fm = text.getFontMetrics(text.getFont());
	  text.setVisible(true);
	  int len = fm.stringWidth(text.getText());
	  int rows = text.getLineCount();  //((int) (len/bb.width + (len!=0%bb.width?1:0)));
	  text.setRows(rows);
	  text.setSize(bb.width, fm.getHeight()*rows + fm.getMaxDescent() + fm.getMaxAscent() - fm.getAscent());
	  text.setLocation(bb.x, bb.y);
	}
      else
	text.setVisible(false);
    }

  public Rectangle getFreeSpace()
    {
      if (boxType != BoxDrawer.NONE)
	return (new Rectangle(bb.x, bb.y + text.getSize().height,
			      bb.width, bb.height - text.getSize().height));
      return null;
    }
  
  public void caretUpdate(CaretEvent e)
    {
      update();
    }

  public void focusGained(FocusEvent e) {}
  public void focusLost(FocusEvent e)
    {
      if (text.isEditable() && !(text.getText().equals(oldText)))
	//	if (boxype != BoxDrawer.TYPE)
	{
	  oldText = text.getText();
	  neuronMapObject.getNeuronStyle().setTitle(oldText);
	}
    }
  
  
  public boolean didHit(MapEvent m)
    {
      return text.contains(m.mouseEvent.getX(), m.mouseEvent.getY());
    }
}
