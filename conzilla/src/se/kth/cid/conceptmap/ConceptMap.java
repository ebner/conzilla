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


package se.kth.cid.conceptmap;
import se.kth.cid.identity.*;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.local.*;


public interface ConceptMap extends Component
{
  int FIRST_CONCEPTMAP_EDIT_CONSTANT = NeuronType.LAST_NEURONTYPE_EDIT_CONSTANT + 1;
  int DIMENSION_EDITED    = FIRST_CONCEPTMAP_EDIT_CONSTANT;
  int NEURONSTYLE_ADDED   = FIRST_CONCEPTMAP_EDIT_CONSTANT + 1;
  int NEURONSTYLE_REMOVED = FIRST_CONCEPTMAP_EDIT_CONSTANT + 2;
  int LAST_CONCEPTMAP_ONLY_EDIT_CONSTANT = NEURONSTYLE_REMOVED;
  int LAST_CONCEPTMAP_EDIT_CONSTANT = AxonStyle.LAST_AXONSTYLE_EDIT_CONSTANT;

  /** This class fills the same function as java.awt.Dimension, but will be
   *  exported over CORBA.
   */
  class Dimension
  {
    public int width;
    public int height;

    public Dimension(int width, int height)
      {
	this.width = width;
	this.height = height;
      }

  }

  /** This class fills the same function as java.awt.Point, but will be
   *  exported over CORBA.
   */
  class Position 
  {
    public int x;
    public int y;

    public Position(int x, int y)
      {
	this.x = x;
	this.y = y;
      }
  }

  /** This class fills the same function as java.awt.Rectangle, but will be
   *  exported over CORBA.
   */
  class BoundingBox 
  {
    public Dimension dim;
    public Position pos;

    public BoundingBox(Dimension dim, Position pos)
      {
	this.dim = dim;
	this.pos = pos;
      }
    
    public BoundingBox(int x, int y, int width, int height)
      {
	this(new Dimension(width, height),
	     new Position(x,y));
      }
    
  }
  
  
  /////////////ConceptMap/////////////

  /** Returns the size of this ConceptMap. It thus defines the units
   *  for all other coordinates.
   *
   *  @return the size of this ConceptMap. Never null.
   */
  Dimension     getDimension();

  /** Sets the size of this ConceptMap.
   *
   *  @param dim the size of this ConceptMap. Must never be null.
   */
  void          setDimension(Dimension dim) throws ReadOnlyException;



  
  /////////////NeuronStyle////////////


  /** Returns the NeuronStyles in this ConceptMap.
   *
   *  @return the NeuronStyles in this ConceptMap. Never null,
   *          but may be empty.
   */  
  NeuronStyle[] getNeuronStyles();

  /** Returns the NeuronStyle in this ConceptMap with the given ID.
   *  Null if no such NeuronStyle could be found.
   *
   *  @param mapID the ID of the searched NeuronStyle.
   *  @return the NeuronStyle in this ConceptMap with the given ID.
   */  
  NeuronStyle   getNeuronStyle(String mapID);

  /** Adds a NeuronStyle to this ConceptMap.
   *  May fail if the Neuron does not exist, but not necessarily.
   *  An ID for the NeuronStyle will be generated.
   *
   *  @param neuronURI the URI of the Neuron to be represented.
   *
   *  @return the new NeuronStyle.
   *  @exception ReadOnlyException if this ConceptMap was not editable.
   *  @exception InvalidURIException if the URI was not valid.
   */  
  NeuronStyle   addNeuronStyle(String neuronURI)
    throws ReadOnlyException, InvalidURIException;
  
}



