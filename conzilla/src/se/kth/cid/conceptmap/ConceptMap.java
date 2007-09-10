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
  String MIME_TYPE = "application/x-conceptmap";
  
  int FIRST_CONCEPTMAP_EDIT_CONSTANT = NeuronType.LAST_NEURONTYPE_EDIT_CONSTANT + 1;
  int DIMENSION_EDITED    = FIRST_CONCEPTMAP_EDIT_CONSTANT;
  int BACKGROUND_EDITED   = FIRST_CONCEPTMAP_EDIT_CONSTANT + 1;
  int NEURONSTYLE_ADDED   = FIRST_CONCEPTMAP_EDIT_CONSTANT + 2;
  int NEURONSTYLE_REMOVED = FIRST_CONCEPTMAP_EDIT_CONSTANT + 3;
  int LAST_CONCEPTMAP_ONLY_EDIT_CONSTANT = NEURONSTYLE_REMOVED;
  int LAST_CONCEPTMAP_EDIT_CONSTANT = AxonStyle.LAST_AXONSTYLE_EDIT_CONSTANT;


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

  class BoundingBox 
  {
    public Dimension dim;
    public Position pos;

    public BoundingBox(Dimension dim, Position pos)
      {
	this.dim = dim;
	this.pos = pos;
      }
  }
  
  
  /////////////ConceptMap/////////////
  Dimension     getDimension();
  void          setDimension(Dimension bg) throws ReadOnlyException;
  
  int           getBackgroundColor();
  void          setBackgroundColor(int color) throws ReadOnlyException;
  
  /////////////NeuronStyle////////////
  NeuronStyle[] getNeuronStyles();
  
  NeuronStyle   getNeuronStyle(String mapID);
  
  NeuronStyle   addNeuronStyle(String mapID, String neuronURI) throws ReadOnlyException, ConceptMapException, InvalidURIException;

}



