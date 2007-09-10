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
import se.kth.cid.conzilla.map.*;
import se.kth.cid.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class FillLayout implements LayoutManager
{
  public FillLayout()
    {}
  
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
	  
	  if(dim.height > height)
	    height = dim.height;
	}

      return new Dimension(width, height);
    }
  
  public Dimension minimumLayoutSize(Container co)
    {
      int width = 0;
      int height = 0;
      
      Component[] comps = co.getComponents();
      
      for(int i = 0; i < comps.length; i++)
	{
	  Dimension dim = comps[i].getMinimumSize();
	  
	  if(dim.width > width)
	    width = dim.width;
	  
	  if(dim.height > height)
	    height = dim.height;
	}

      return new Dimension(width, height);
    }
  
  public void layoutContainer(Container co)
    {
      Component[] comps = co.getComponents();

      for(int i = 0; i < comps.length; i++)
	{
	  comps[i].setLocation(0, 0);
	  comps[i].setSize(co.getSize());
	}
    }
}
