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


package se.kth.cid.conzilla.layer;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class VerticalListLayout implements LayoutManager
{
  Vector components;
  
  public VerticalListLayout(Vector components)
    {
	this.components = components;
    }
  
  public void addLayoutComponent(String str,Component comp)
    {}

  public void removeLayoutComponent(Component co)
    {}
  
  public Dimension preferredLayoutSize(Container co)
    {
      int width = 0;
      int height = 0;
      
      Component[] comps = co.getComponents();
      
      for(int i = 0; i < comps.length; i++)
	{
	  Dimension dim = comps[i].getPreferredSize();
	  
	  if(dim.width > width)
	    width = dim.width;
	  
	  height += dim.height;
	}

      return new Dimension(width+5, height);
    }
  
  public Dimension minimumLayoutSize(Container co)
    {
      return preferredLayoutSize(co);
    }
  
    public void layoutContainer(Container co)
    {
      Dimension dim = co.getSize();
      Component [] comps = co.getComponents();
      
      if (comps.length <= 0)
	  return;
      else if (comps.length == 1)
	  {
	      comps[0].setLocation(5,0);
	      comps[0].setSize(comps[0].getPreferredSize());
	  }
      else
	  for(int i = 0; i < comps.length; i++)
	      {
		  int index  = components.indexOf(comps[i]);
		  if (index != -1)
		      comps[i].setLocation(5,index*((dim.height-25)/(components.size())));
		  else
		      comps[i].setLocation(5,dim.height-25);
		  comps[i].setSize(comps[i].getPreferredSize());
	      }
    }
}
