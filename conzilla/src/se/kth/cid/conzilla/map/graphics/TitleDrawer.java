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
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.metadata.*;
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

public class TitleDrawer extends MapDrawer
{
  MapTextArea title;
  DataDrawer data;
  
    //  MyJPanel box;
  
  Rectangle bb;
    //  Rectangle scaledbb;
  NeuronMapObject neuronMapObject;

  double scale;
  
  boolean settingTitle;

  JComponent editorLayer;

  boolean displayLanguageDiscrepancy;
  boolean foreignTitle;
  boolean visible;
  boolean hardvisible;
  CellRendererPane cellRendererPane;
    
  public TitleDrawer(NeuronMapObject neuronMapObject, JComponent editorLayer)
    {
      super(neuronMapObject.getDisplayer());
      this.neuronMapObject = neuronMapObject;
      this.editorLayer = editorLayer;
    
      settingTitle = false;
      displayLanguageDiscrepancy = false;
      
      scale = neuronMapObject.getDisplayer().getScale();

      cellRendererPane = new CellRendererPane();

      title = new MapTextArea(1.0);

      data = new DataDrawer(neuronMapObject);
      
      /*box = new MyJPanel(title);
	 
      box.setOpaque(false);
      box.setLayout(new NaiveBoxLayout());
      box.add(title);
      box.add(data);*/
      
      //      editorLayer.add(box);
      title.setEditable(false);
      visible = true;
      hardvisible = true;
    }
  
    public JTextArea getEditableTextComponent()
    {
	return title;
    }

    public Rectangle getTitleBounds()
    {
	return bb;
    }
    public void setTitleVisible(boolean bo)
    {
	hardvisible = bo;
	if (!hardvisible)
	    cellRendererPane.remove(title);
    }
  public Dimension getPreferredSize()
    {
	Dimension dim1 = title.getPreferredSize();
	Dimension dim2 = data.preferredSize();
	return dim1.width > dim2.width ? new Dimension(dim1.width, dim1.height + dim2.height) :
	    new Dimension(dim2.width, dim1.height + dim2.height);
    }
  
  public void detach()
    {
	//      editorLayer.remove(box);
      title = null;
      bb    = null;
      neuronMapObject = null;
    }
  
  public void setEditable(boolean editable, MapEvent m)
    {
      if (editable == title.isEditable())
	return;

      if(editable && neuronMapObject.getNeuron() == null)
	return;
      
      title.setEditable(editable);

      if(editable)
	{
	  Caret c = title.getCaret();
	  if(m == null)
	    {
	      c.setDot(0);
	      c.moveDot(title.getText().length());
	    }
	  else
	    {
	      c.setDot(title.viewToModel(SwingUtilities.convertPoint(editorLayer, m.mouseEvent.getX(), m.mouseEvent.getY(), title)));
	    }
	    title.requestFocus();
	}
      else
	{
	  setTitle();
	}
      
      //      title.repaint();
    }
  
  public boolean getErrorState()
    {
      return neuronMapObject.getErrorState();
    }
  

  public void setDisplayLanguageDiscrepancy(boolean b)
    {
      displayLanguageDiscrepancy = b;
      neuronMapObject.colorUpdate();
    }
  
  public void colorUpdate(Mark mark)
    {
      Mark myMark = getMark(mark);
      if(displayLanguageDiscrepancy && foreignTitle)
	title.setColor((new Mark(MapDisplayer.COLOR_NEURON_ERROR, null, null)).textColor);
      else
	title.setColor(myMark.textColor);
      data.setColor(myMark);
    }

  public void updateTitle()
    {
      if(settingTitle)
	return;

      String str = "";
      
      if(neuronMapObject.getNeuron() != null)
        {
           MetaData md=neuronMapObject.getNeuron().getMetaData();
	   MetaData.LangString lstr = MetaDataUtils.getLocalizedString(md.get_metametadata_language(), md.get_general_title());
	   str = lstr.string;
	   if(!MetaDataUtils.getLocale(lstr.language).equals(Locale.getDefault()))
	     foreignTitle = true;
	   else
	     foreignTitle = false;
	   neuronMapObject.colorUpdate();
	}
      
      title.setText(str);
    }


  public void updateData()
    {
      data.updateData();
    }
  

  public void updateBox(Rectangle2D re)
    {
	Dimension td = title.getPreferredSize();
	int ew = 0;
	    int eh = 0;
	    int dx = 0;
	    int dy = 0;
	int x=0, y=0, width, height;

	if (td.width < (re.getWidth()+ew))
	    {
		width = (int) (re.getWidth() - (re.getWidth()-td.width)/2);
		switch (neuronMapObject.getNeuronStyle().getHorisontalTextAnchor()) 
		    {
		    case NeuronStyle.WEST:
			x = (int) (dx + re.getX());
			break;
		    case NeuronStyle.CENTER:
			x = (int) (((re.getWidth() - td.width + ew)/2) + dx + re.getX());
			break;
		    case NeuronStyle.EAST:
			x = (int) ((re.getWidth() - td.width + ew) + dx + re.getX());
			break;
		    }
	    }
	else
	    {
		width = (int) re.getWidth()+ew;
		x = (int) re.getX() + dx;
	    }
	if (td.height < (re.getHeight()+eh))
	    {
		height = (int) (re.getHeight() - (re.getHeight()-td.height)/2);
		switch (neuronMapObject.getNeuronStyle().getVerticalTextAnchor()) 
		    {
		    case NeuronStyle.NORTH:
			y = (int) (dy +re.getY());
			break;
		    case NeuronStyle.CENTER:
			y = (int) (((re.getHeight() -td.height +eh)/2) +dy +re.getY());
			break;
		    case NeuronStyle.SOUTH:
			y = (int) ((re.getHeight() - td.height + eh) + dy + re.getY());
			break;
		    }

	    }
	else
	    {
		height = (int) re.getHeight()+eh;
		y = (int) re.getY() +dy;
	    }
		
	bb = new Rectangle(x, y, width, height);
	/*      bb = new Rectangle((int) re.getX()-6 , 2 + (int) re.getY(),
			 (int) re.getWidth()+4, (int) re.getHeight());
	*/
      if (visible != neuronMapObject.getNeuronStyle().getBodyVisible())
	  {
	      visible = !visible;
	      if (!visible)
		  setEditable(false, null);
	      else
		  resize();	      
	  }   
    }
  
  public void setScale(double scale)
    {
      this.scale = scale;
      //      title.setScale(scale);
      data.setScale(scale);
      resize();
    }
  
  
  void resize()
    {
	
	/*	scaledbb = new Rectangle((int)Math.round(bb.getX()) + 2,
				 (int)Math.round((bb.getY() + 1)*scale),
				 (int)Math.round(bb.getWidth()*scale) - 4,
				 (int)Math.round((bb.getHeight() - 1)*scale));      */
    }

  
  public void setTitle()
    {
      if(neuronMapObject.getNeuron() == null)
	return;
      
      MetaData md = neuronMapObject.getNeuron().getMetaData();

      settingTitle = true;

      String str = MetaDataUtils.getLocalizedString(md.get_metametadata_language(), md.get_general_title()).string;

      if (!str.equals(title.getText()))
	{
	  MetaData.LangString [] ls = md.get_general_title().langstring;
	  boolean done = false;
	  for (int i = 0; i < ls.length; i++)
	    if (MetaDataUtils.getLocale(ls[i].language).equals(Locale.getDefault()))
	      {
		Tracer.debug("setting an already existing title....(language="+ls[i].language+")");
		ls[i].string = title.getText();
		md.set_general_title(new MetaData.LangStringType(ls));
		done = true;
		break;
	      }
	  if (!done)
	    {
	      MetaData.LangString [] nls = new MetaData.LangString[ls.length+1];
	      for (int i = 0; i < ls.length; i++)
		nls[i] = ls[i];
	      nls[nls.length-1] = new MetaData.LangString(MetaDataUtils.getLanguageString(Locale.getDefault()),
							  title.getText());
	      md.set_general_title(new MetaData.LangStringType(nls));
	      Tracer.debug("Added new title: " + nls[nls.length-1].toString());
	    }
	}
      settingTitle = false;
      updateTitle();
    }
  public boolean didHit(MapEvent m)
    {
	//      Point p = SwingUtilities.convertPoint(editorLayer, m.mouseEvent.getX(), m.mouseEvent.getY(), title); 
      return bb.contains(m.mapX, m.mapY);
    }

  void doPaint(Graphics2D g)
    {
	//if (!neuronMapObject.getNeuronStyle().getBodyVisible())
	title.setColor(g.getColor());

	if (visible && hardvisible)
	    {
		//    next line is for debugging purposes
		//		g.drawRect(bb.x, bb.y, bb.width, bb.height);
		cellRendererPane.paintComponent(g, title, editorLayer, bb.x,bb.y, bb.width, bb.height);
	    }
	if (!data.empty() && bb.height > title.getPreferredSize().height)
	    cellRendererPane.paintComponent(g, data, editorLayer, bb.x,bb.y+title.getPreferredSize().height, bb.width, bb.height-title.getPreferredSize().height);
    }
}
