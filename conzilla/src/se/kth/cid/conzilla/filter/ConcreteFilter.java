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
 *  It reads from a filterneuron that comes with a neuron or a concept map.
 *
 *  @author Daniel Pettersson
 *  @version $Revision$
 */
public class ConcreteFilter extends AbstractFilter
{
    /** Constructs a ConcreteFilter.
     *
     *  @param man the MapManager the filter is attached to.
     *  @param cont the controller controlling the manager.
     *  @param filter the URI as a string.
     */
    public ConcreteFilter(MapManager man, MapController cont, String filter)
           throws FilterException
    {
      super(man, cont, filter);
    }

   /** Filters the content of given neuron with given node.
    *
    *  @param node the filternode to use.
    *  @param neuron the neuron whose content will be filtered.
    *  @return a vector with filtered content.
    */
    public Vector filterContent(FilterNode node, Neuron neuron)
    {
      String keywords = null;

      try {
      setContent(neuron);
      } catch(ControllerException e)
      {
      Tracer.debug("Could not load content-set for neuron");
      }

      return recursiveContent(node, keywords);
    }

   /** Recursive filter function to aid filterContent.
    *
    *  @param node the filternode to use.
    *  @param neuron the neuron whose content will be filtered.
    *  @return a vector with filtered content.
    */
    private Vector recursiveContent(FilterNode node, String keywords)
    {
      if (node.getTop() != null)
      {
         contents = recursiveContent(node.getTop(), keywords);

         for (int i=0; i < contents.size(); i++)
         {
           keywords = ((ContentDescription) contents.elementAt(i)).getNeuron().getMetaData().getValue("keywords");

           if (keywords == null)
              contents.removeAllElements();
           else if (keywords.indexOf(node.getFilterTag()) == -1)
           {
             contents.removeElementAt(i);
             i = i - 1;
           }
         }
         return contents;
      }
      else
         return contents;
    }
}
