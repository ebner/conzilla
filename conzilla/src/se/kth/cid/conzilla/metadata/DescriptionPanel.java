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

package se.kth.cid.conzilla.metadata;
import se.kth.cid.component.Component;
import se.kth.cid.component.MetaData;
import se.kth.cid.component.MetaDataUtils;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.BorderLayout;
import se.kth.cid.conzilla.map.graphics.MapTextArea;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.util.*;
import java.awt.*;
import java.awt.geom.*;

public class DescriptionPanel extends JPanel
{
  private static ArrowBorder up=new ArrowBorder(6,0);
  private static ArrowBorder both=new ArrowBorder(6,6);
  private static ArrowBorder down=new ArrowBorder(0,6);
  private static ArrowBorder none=new ArrowBorder(0,0);

  public MapTextArea text;

  MetaData metaData;
  JPanel panel;
    
  int showingIndex = -1;
  int expanded=1;
  double scale;
  double lscale;
  int ox;
  int oy;
  public static final int xoffset = 10, yoffset = 10;

  public DescriptionPanel(Component comp, double scale)
    {
      super();

      metaData = comp.getMetaData();
      
      setOpaque(false);
      setLayout(new BorderLayout());

      panel = new JPanel();
      panel.setOpaque(false);
      panel.setLayout(new BorderLayout());

      text = new MapTextArea(1.0);
      setScale(scale);
      
      showNext();

      text.setEditable(false);
      text.setRows(0);

      panel.add(text);
      add(panel);
    }  

    public void setLocation(int x, int y)
    {
	this.ox = x + (int) (xoffset/lscale);
	this.oy = y + (int) (yoffset/lscale);
	setLocation();
    }

    private void setLocation()
    {
	super.setLocation((int) (ox*lscale), (int) (oy*lscale));
    }

    public void expand()
    {
	MetaData.LangStringType[] desc = metaData.get_general_description();
	if (showingIndex+expanded < desc.length)
	    {
		expanded++;
		show(desc);
	    }
    }
    public void unExpand()
    {
	MetaData.LangStringType[] desc = metaData.get_general_description();
	if (expanded>1)
	    {
		expanded--;
		show(desc);
	    }
    }
    public void showNext()
    {
	expanded=1;
	MetaData.LangStringType[] desc = metaData.get_general_description();
	if (desc != null && ++showingIndex < desc.length)
	    show(desc);
	else
	    text.setText("");
    }
    public void showPrev()
    {
	expanded=1;
	MetaData.LangStringType[] desc = metaData.get_general_description();
	if (desc != null && --showingIndex < desc.length && showingIndex >= 0)
	    show(desc);
	else
	    text.setText("");
    }
    
    public void showLast()
    {
	expanded=1;
	MetaData.LangStringType[] desc = metaData.get_general_description();
	if (desc != null)
	    {
		showingIndex = desc.length-1;
		show(desc);		
	    }
    }

    void show(MetaData.LangStringType[] desc) 
    {
	updateBorder(desc);
	String sum=MetaDataUtils.getLocalizedString(metaData.get_metametadata_language(),
							    desc[showingIndex]).string;
	for (int i=1;i<expanded;i++)
	    sum=sum+"\n"+MetaDataUtils.getLocalizedString(metaData.get_metametadata_language(),
							    desc[showingIndex+i]).string;
	text.setText(sum);
    }
    
  public String getText()
    {
      return text.getText();
    }

  private void updateBorder(MetaData.LangStringType[] desc)
    {
	if (showingIndex > 0)
	    {
		if (showingIndex + expanded < desc.length)
		    panel.setBorder(both);
		else
		    panel.setBorder(up);
	    }
	else
	    {
		if (showingIndex + expanded < desc.length)
		    panel.setBorder(down);
		else
		    panel.setBorder(none);
	    }
    }

    public void setScale(double scale)
    {
	if (this.scale == scale*0.85)
	    return;
	this.lscale=scale;
	this.scale=scale*0.85;
	
	text.setTransform(AffineTransform.getScaleInstance(scale*0.85, scale*0.85));
	setArrowScale(scale*0.85);
	setLocation();
	MetaData.LangStringType[] desc = metaData.get_general_description();
	if (desc==null)
	    return;
	updateBorder(desc);
	setSize(getPreferredSize());
    }	

    private static void setArrowScale(double scale)
    {
	up=new ArrowBorder((int) (scale*6),0); 
	both=new ArrowBorder((int) (scale*6),(int) scale*6);      
	down=new ArrowBorder(0,(int) (scale*6));
    }

}

