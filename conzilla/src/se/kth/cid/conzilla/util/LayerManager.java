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

public class LayerManager implements LayoutManager2
{
  MapDisplayer layeredpane;
  Dimension preferredsize;
  Point translate;
  
  public LayerManager(Container co)
  {
    System.out.println("creator.!");
    layeredpane=(MapDisplayer) co;
    preferredsize = co.getSize();
    translate=new Point(0,0);
  }

  public void addLayoutComponent(String str,Component comp)
  {}
  
  public void addLayoutComponent(Component co, Object ob)
  {
    System.out.println("addcomponent!!!!");
    preferredsize=new Dimension(0,0);
    Point p=co.getLocation();
    Dimension d=co.getSize();
    System.out.println("Dimension="+d.toString());
    System.out.println("Location="+p.toString());
    if (p.x <0)
      {
	translate.x-= p.x;
	p.x=0;
      }
    if (p.y <0)
      {
	translate.y-=p.y;
	p.y=0;
      }
    System.out.println("Translate="+translate.toString());
    preferredsize.width=p.x+co.getSize().width;
    preferredsize.height=p.y+co.getSize().height;

    Component components[]=layeredpane.getComponents();
    System.out.println(components.length);
    for (int i=0;components.length >i;i++)
      {
	Component nco= components[i];
	
	if (!(nco instanceof ResizeComponent))
	  {
	    Point tp=nco.getLocation();
	    tp.translate(translate.x,translate.y);
	    System.out.println("Translating component to"+tp.toString());
	    nco.setLocation(tp);
	    
	    Dimension di=nco.getPreferredSize();
	    di.width+=tp.x;
	    di.height+=tp.y;
	    if (di.width>preferredsize.width)
	      preferredsize.width=di.width;
	    if (di.height>preferredsize.height)
	      preferredsize.height=di.height;
	  }
      }    
    Tracer.debug("PreferredSize = " + preferredsize);
  }
  
  public void removeLayoutComponent(Component co)
  {
    System.out.println("removecomponent!!!!");
    Component components[]=layeredpane.getComponents();
    Rectangle biggest;
    if (components.length != 0 )
      {
	if (components.length == 1)
	  {
	    layeredpane.setSize(0,0);
	    preferredsize = new Dimension(0, 0);
	  }
	else
	  {
	    if (components[0]!=co)
	      biggest=components[0].getBounds();
	    else
	      biggest=components[1].getBounds();
	    
	    for (int i=0;components.length >i;i++)
	      if (components[i]!=co)
		if (!(components[i] instanceof ResizeComponent))
		  biggest.add(components[i].getBounds());
	    System.out.println("Biggest Bounds="+biggest.toString());
	    translate=new Point(-biggest.x,-biggest.y);
	    System.out.println("Translating back with"+translate.toString());
	    for (int i=0;components.length >i;i++)
	      {
		if (!(components[i] instanceof ResizeComponent))
		  {
		    Point p=components[i].getLocation();
		    p.translate(translate.x,translate.y);
		    System.out.println("Translating component to"+p.toString());
		    components[i].setLocation(p);
		  }
	      }
	    preferredsize = biggest.getSize();
	  }
      }
  }

  public Dimension preferredLayoutSize(Container co)
  {
    return preferredsize;
  }
  public Dimension minimumLayoutSize(Container co)
  {
    return preferredsize;
  }
  public Dimension maximumLayoutSize(Container target)
  {
    return preferredsize;
  }
  public float getLayoutAlignmentX(Container target)
  {
    return 0;
  }
  public float getLayoutAlignmentY(Container target)
  {
    return 0;
  }
  public void invalidateLayout(Container target)
  {}
  
  public void layoutContainer(Container co)
  {
    System.out.println("layout");
    Dimension viewsize = ((JViewport) layeredpane.getParent()).getExtentSize();
    if (viewsize.width<preferredsize.width)
      viewsize.width=preferredsize.width;
    if (viewsize.height<preferredsize.height)
      viewsize.height=preferredsize.height;
    
    //    System.out.println("layermanager resizes a bunch to size:"+viewsize.toString());
    Component components[]=layeredpane.getComponents();
    for (int i=0;components.length >i;i++)
      {
	Component nco= components[i];
	if (nco instanceof ResizeComponent)
	  {
	    nco.setSize(viewsize);
	    ((ResizeComponent) nco).layersTranslated(translate);
	  }
      }
    layeredpane.setSize(viewsize);
    
    Point pp=((JViewport) layeredpane.getParent()).getViewPosition();
    pp.translate(translate.x,translate.y);
    ((JViewport) layeredpane.getParent()).setViewPosition(pp);

    translate=new Point(0,0);
  }
}
