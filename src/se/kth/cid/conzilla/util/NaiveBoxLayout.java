/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

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
