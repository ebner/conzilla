//* $Id$ */
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


package se.kth.cid.conzilla.filter;
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import se.kth.cid.content.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


/** The filter is a tool for structuring content in a concept map.
 *  This is an abstract class.
 *
 *  @author Daniel Pettersson
 *  @version $Revision$
 */
public abstract class AbstractFilter implements Filter
{
    MapManager manager;
    MapController controller;
    String filter;
    ComponentLoader loader;
    FilterNode firstnode;
    Vector contents;
    Vector lifo;
    ContentDescription[] cds;
    ContentSet cSet;

    /** Constructs a Filter.
     *
     *  @param man the MapManager the filter is attached to.
     *  @param cont the controller controlling the manager.
     *  @param filter the URI as a string.
     */
    public AbstractFilter(MapManager man, MapController cont, String filter)
                          throws FilterException
    {
      this.filter = filter;
      manager = man;
      controller = cont;
      loader = cont.getComponentLoader();
      lifo = new Vector();
      try {
      URI filterURI = new URI(filter);
      se.kth.cid.component.Component comp=loader.loadComponent(filterURI, loader);
      if (comp instanceof Neuron)
        firstnode = new FilterNode(loader, (Neuron) comp, man, lifo);
      else
      {
        loader.releaseComponent(comp);
        throw new FilterException("Wrong URI, not a filter.");
      }
      } catch (MalformedURIException e)
      {
        throw new FilterException("Wrong URI, not a filter.");
      } catch (ComponentException e)
      {
        throw new FilterException("Wrong URI, not a filter.");
      }
    }

    public String getURI()
    {
        return filter;
    }

    public FilterNode getFilterNode()
    {
      return firstnode;
    }

    public void setContent(Neuron neuron) throws ControllerException
    {
      try {
      cSet = new ContentSet(neuron, loader);
      }
      catch(ComponentException e)
      {
        throw new ControllerException("Could not load content-set for neuron: "
				      + neuron + ":\n " + e.getMessage());
      }
      contents = cSet.getContents();
    }

    public void showContent(Vector contents)
    {
        cds = new ContentDescription[contents.size()];
        contents.copyInto(cds);
        controller.getContentSelector().selectContent(cds);
    }

    // Must be implemented in subclass!
    public abstract Vector filterContent(FilterNode node, Neuron neuron);
}
