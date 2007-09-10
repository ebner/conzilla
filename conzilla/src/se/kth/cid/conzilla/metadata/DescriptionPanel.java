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

public class DescriptionPanel extends JPanel
{
  static private ArrowBorder up=new ArrowBorder(6,0);
  static private ArrowBorder both=new ArrowBorder(6,6);
  static private ArrowBorder down=new ArrowBorder(0,6);
  static private double lastscale=1.0;

  public MapTextArea text;

  MetaData metaData;
    
  int showingIndex = -1;
  int expanded=1;

  public DescriptionPanel(Component comp, double scale)
    {
      super();
      metaData = comp.getMetaData();
      
      setOpaque(false);
      setLayout(new BorderLayout());
      
      text = new MapTextArea(1.0);
      setScale(scale);
      
      showNext();

      text.setEditable(false);
      // Why?????
      //      text.setColumns(10);
      text.setRows(0);
      //      text.setColor(Color.blue);  //move away!!!!
      add(text);
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
		    text.setBorder(both);
		else
		    text.setBorder(up);
	    }
	else
	    {
		if (showingIndex + expanded < desc.length)
		    text.setBorder(down);
		else
		    text.setBorder(null);
	    }
    }

    public void setScale(double scale)
    {
	text.setScale(scale);
	setArrowScale(scale);
	MetaData.LangStringType[] desc = metaData.get_general_description();
	if (desc==null)
	    return;
	updateBorder(desc);
    }

    private static void setArrowScale(double scale)
    {
	if (scale == lastscale)
	    return;
	lastscale=scale;
	up=new ArrowBorder((int) scale*6,0);  
	both=new ArrowBorder((int) scale*6,(int) scale*6);      
	down=new ArrowBorder(0,(int) scale*6);
    }
}

