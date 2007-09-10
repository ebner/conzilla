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

public class NaiveBoxLayout implements LayoutManager
{
  public NaiveBoxLayout()
    {}
  
  public void addLayoutComponent(String str,Component comp)
    {}
  
  public void removeLayoutComponent(Component co)
    {}
  
  public Dimension preferredLayoutSize(Container co)
    {
      Component[] comps = co.getComponents();

      int height = 0;
      int width = 0;
      
      for(int i = 0; i < comps.length; i++)
	{
	  Dimension dim = comps[i].getPreferredSize();
	  height += dim.height;
	  if(dim.width > width)
	    width = dim.width;
	}
      return new Dimension(width, height);
    }
  
  public Dimension minimumLayoutSize(Container co)
    {
      return new Dimension(0, 0);
    }
  
  public void layoutContainer(Container co)
    {
      Component[] comps = co.getComponents();
      Dimension codim = co.getSize();

      int height = 0;
      for(int i = 0; i < comps.length; i++)
	height += comps[i].getPreferredSize().height;
      
      int yPos = 0;
      for(int i = 0; i < comps.length; i++)
	{
	  Dimension dim = comps[i].getPreferredSize();
	  comps[i].setBounds((int)Math.round(0.5*(codim.width - dim.width)),
			     yPos + (int)Math.round(0.5*(codim.height - height)),
			     dim.width, dim.height);
	  yPos += dim.height;
	}
    }
}
