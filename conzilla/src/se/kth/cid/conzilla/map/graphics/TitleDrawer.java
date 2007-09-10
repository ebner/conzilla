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
  Color textcolor;
  JTextArea text;
  Rectangle bb;
  NeuronStyle neuronstyle;
  int boxtype;
  
  public TitleDrawer(final NeuronStyle neuronstyle, final MapDisplayer.MapComponentDrawer parent)
  {
    this.neuronstyle=neuronstyle; 
    text=new JTextArea();

    //      text.setFont(new Font("Serif", Font.ITALIC, 16));
    text.setLineWrap(true);
    text.setWrapStyleWord(false);
    text.setOpaque(false);
    text.setBorder(new EmptyBorder(2,2,1,2)); // top, left, bottom, right insets.
    setEditable(false);  //importand to set this before addCaretListener
                        //since otherwise fix is called before fixFromNeuronStyle.
    parent.add(text);
    text.addCaretListener(this);
    text.addFocusListener(this);
  }

  public void detach()
  {
    text.removeCaretListener(this);
    text.removeFocusListener(this);
    text=null;
    bb=null;
    neuronstyle=null;
  }
  public void setEditable(boolean editable)
    {
    if (boxtype!=BoxDrawer.TYPE)
      {
	text.getCaret().setSelectionVisible(editable);
	text.setEditable(editable);	
      }
  }
  
  public void paint(Graphics g,Color over) 
  {
    if (over!=null)
      text.setDisabledTextColor(over);
    else
      text.setDisabledTextColor(textcolor);	  
  }

  public void fixFromNeuronStyle(Rectangle re,int boxtype)
  {
    
    bb=re;  // re can be null, don't use before you checked if boxtype==BoxDrawer.NONE
    this.boxtype=boxtype;
    if (neuronstyle.getNeuronType()!=null)
      textcolor=Color.black;
    else
      textcolor=Color.red;
    text.setDisabledTextColor(textcolor);
    if (boxtype==BoxDrawer.TYPE)
      text.setText("<<"+neuronstyle.getNeuronType().getMetaData().getValue("Title")+">>");
    else
      text.setText(neuronstyle.getTitle());
    fix();
  }
  
  public void fix()
  {
    if (boxtype!=BoxDrawer.NONE)
      {
	FontMetrics fm=text.getFontMetrics(text.getFont());
	text.setVisible(true);
	int len=fm.stringWidth(text.getText());
	int rows=text.getLineCount();  //((int) (len/bb.width + (len!=0%bb.width?1:0)));
	text.setRows(rows);
	text.setSize(bb.width,fm.getHeight()*rows+fm.getMaxDescent()+fm.getMaxAscent()-fm.getAscent());
	text.setLocation(bb.x,bb.y);
      }
    else
      text.setVisible(false);
    
  }      
  public Rectangle getFreeSpace()
  {
    if (boxtype!=BoxDrawer.NONE)
      return (new Rectangle(bb.x,bb.y+text.getSize().height,
			    bb.width,bb.height-text.getSize().height));
    return null;
  }
  
  public void caretUpdate(CaretEvent e)
  {
    fix();
  }
  public void focusGained(FocusEvent e) {}
  public void focusLost(FocusEvent e)
  {
    if (text.isEditable())
      if (boxtype!=BoxDrawer.TYPE)	  
	neuronstyle.setTitle(text.getText());
  }
  public boolean didHit(MapEvent m)
  {
    return text.contains(m.mouseevent.getX(),m.mouseevent.getY());
  }
}
