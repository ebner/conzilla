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

public class TitleDrawer extends MapDrawer implements LocaleListener
{
  MapTextArea title;
  DataDrawer data;
  
  JPanel box;
  
  Rectangle2D bb;
  NeuronMapObject neuronMapObject;

  double scale;
  
  boolean settingTitle;

  JComponent editorLayer;

  boolean displayLanguageDiscrepancy;
  boolean foreignTitle;
  
  public TitleDrawer(NeuronMapObject neuronMapObject, JComponent editorLayer)
    {
      super(neuronMapObject.getDisplayer());
      this.neuronMapObject = neuronMapObject;
      this.editorLayer = editorLayer;

      settingTitle = false;
      displayLanguageDiscrepancy = false;
      
      scale = neuronMapObject.getDisplayer().getScale();

      title = new MapTextArea(1.0);

      data = new DataDrawer(neuronMapObject);
      
      box = new JPanel();
      box.setOpaque(false);
      box.setLayout(new NaiveBoxLayout());
      box.add(title);
      box.add(data);
      
      editorLayer.add(box);
      title.setEditable(false);
      LocaleManager.getLocaleManager().addLocaleListener(this);
    }

  public Dimension getPreferredSize()
    {
      return box.getPreferredSize();
    }
  
  public void detach()
    {
      editorLayer.remove(box);
      title = null;
      bb    = null;
      neuronMapObject = null;
      LocaleManager.getLocaleManager().removeLocaleListener(this);
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
      
      title.repaint();
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
	title.setColor((new Mark(ColorManager.MAP_NEURON_ERROR, null, null)).textColor);
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
      bb = re;

      boolean visible = neuronMapObject.getNeuronStyle().getBodyVisible();
      if (box.isVisible() != visible)
	{
	  box.setVisible(visible);
	  if (!visible)
	    setEditable(false, null);
	}
      if(visible)
	resize();
    }
  
  public void setScale(double scale)
    {
      this.scale = scale;
      title.setScale(scale);
      data.setScale(scale);
      resize();
    }
  
  
  void resize()
    {
      box.setBounds((int)Math.round(bb.getX()*scale) + 2,
		    (int)Math.round((bb.getY() + 1)*scale),
		    (int)Math.round(bb.getWidth()*scale) - 4,
		    (int)Math.round((bb.getHeight() - 1)*scale));      

      box.revalidate();
      box.repaint();
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
      Point p = SwingUtilities.convertPoint(editorLayer, m.mouseEvent.getX(), m.mouseEvent.getY(), title); 
      return title.contains(p.x, p.y);
    }

  public void localeAdded(LocaleEvent e)
    {}
  public void localeRemoved(LocaleEvent e)
    {}
  public void setDefaultLocale(LocaleEvent e)
    {
      updateTitle();
    }
}
