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


package se.kth.cid.conzilla.filter;
import se.kth.cid.util.*;
import se.kth.cid.content.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/** This is a class for creating a node representing a filterneuron.
 *
 *  @author Daniel Pettersson
 *  @version $Revision$
 */
public class FilterNode{
    String filtertag[];
    Vector refines;
    Vector lifo;
    FilterNode topnode;
    FilterAction action;

    URI filterURI;
    Neuron filterneuron;
    Role roles[];


    /** Constructs a FilterNode.
     *
     *  @param loader the ComponentLoader to use.
     *  @param filterneuron the Neuron associated with this FilterNode.
     *  @param man the MapManager attached to this FilterNode.
     *  @param lifo the loop vector associated with this FilterNode.
     *  @exception FilterException if the filter is looped.
     */
    public FilterNode(ComponentLoader loader, Neuron filterneuron, MapManager man, Vector lifo)
                      throws FilterException
    {
        this.filterneuron = filterneuron;
        refines = new Vector();
        filtertag = filterneuron.getDataValues("FILTERTAG");
        if (filtertag.length != 0)
          action = new FilterAction(this, filtertag[0], man);
        for (int i=0; i < lifo.size(); i++)
          if(filtertag.equals(lifo.elementAt(i)))
            throw new FilterException("Loop in filter.");
        lifo.addElement(filtertag);

        roles = filterneuron.getRolesOfType("refine");
        if (roles.length != 0)
        {
          for (int i=0; i < roles.length; i++)
          {
            try {
              filterURI = new URI(roles[i].neuronuri);
              se.kth.cid.component.Component comp=loader.loadComponent(filterURI, loader);
              if (comp instanceof Neuron)
                refines.addElement(new FilterNode(loader,(Neuron) comp, man, lifo));
              else
              {
                loader.releaseComponent(comp);
                throw new ComponentException("Wrong URI, not a filter.");
              }
            } catch (MalformedURIException e)
            {
              Tracer.debug("Bad URI to filter in map.");
            } catch (ComponentException e)
            {
              Tracer.debug("Bad component in filter");
            } catch (FilterException e)
            {
              Tracer.debug("Aspects in filter are looped!");
            }
          }
        }
        for (int i=0; i < refines.size(); i++)
          ((FilterNode) refines.elementAt(i)).setTop(this);
        lifo.removeElement(filtertag);
    }

  /** Returns the filtertag of this node.
   *
   *  @return the filtertag of this node.
   */
    public String getFilterTag()
    {
	return filtertag[0];
    }

  /** Returns the refine at given index of this node.
   *
   *  @return the refine at given index of this node.
   */
    public FilterNode getRefine(int index)
    {
        return (FilterNode) refines.elementAt(index);
    }

  /** Returns the action associated with this node.
   *
   *  @return the action associated with this node.
   */
    public FilterAction getAction()
    {
        return action;
    }

  /** Returns the number of refines attached to this node.
   *
   *  @return the number of refines attached to this node.
   */
    public int numOfRefines()
    {
      return refines.size();
    }

  /** Returns the node above this node.
   *
   *  @return the node above this node.
   */
    public FilterNode getTop()
    {
        return topnode;
    }

  /** Sets the node above this node.
   *
   *  @param node the node above this node.
   */
    public void setTop(FilterNode node)
    {
        topnode = node;
    }
}

