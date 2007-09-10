
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
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/** The filter is a tool for structuring content in a concept map.
 *  It reads from a filtermap that comes with every concept map.
 *
 *  @author Daniel Pettersson
 *  @version $Revision$
 */
public interface Filter
{
  /** Returns the first FilterNode of this filter.
   *
   *  @return the first FilterNode of this filter.
   */
   FilterNode getFilterNode();

  /** Returns the URI string of this filter.
   *
   *  @return the URI string of this filter.
   */
   String getURI();

  /** Sets the content for current neuron.
   *
   *  Throws a ControllerException.
   *
   *  @param neuron the current neuron to filter.
   */
   void setContent(Neuron neuron) throws ControllerException;

  /** Displays the given content.
   *
   *  @param contents the contents to display.
   */
   void showContent(Vector contents);

  /** Filters the content of given neuron with given node.
   *
   *  Only abstract implementation in AbstractFilter.
   *
   *  @param node the filternode to use.
   *  @param neuron the neuron whose content will be filtered.
   *  @return a vector with filtered content.
   */
   Vector filterContent(FilterNode node, Neuron neuron);
}

