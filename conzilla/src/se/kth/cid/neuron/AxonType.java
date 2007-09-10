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


package se.kth.cid.neuron;
import se.kth.cid.util.*;
import se.kth.cid.component.*;

public interface AxonType
{
  int FIRST_AXONTYPE_EDIT_CONSTANT
  = NeuronType.LAST_NEURONTYPE_ONLY_EDIT_CONSTANT + 1;
  
  int MINIMUMMULTIPLICITY_EDITED     = FIRST_AXONTYPE_EDIT_CONSTANT;
  int MAXIMUMMULTIPLICITY_EDITED     = FIRST_AXONTYPE_EDIT_CONSTANT + 1;
  int LINETYPE_EDITED                = FIRST_AXONTYPE_EDIT_CONSTANT + 2;
  int LINETHICKNESS_EDITED           = FIRST_AXONTYPE_EDIT_CONSTANT + 3;
  int LINECOLOR_EDITED               = FIRST_AXONTYPE_EDIT_CONSTANT + 4;
  int HEADTYPE_EDITED                = FIRST_AXONTYPE_EDIT_CONSTANT + 5;
  int HEADFILLED_EDITED              = FIRST_AXONTYPE_EDIT_CONSTANT + 6;
  int HEADSIZE_EDITED                = FIRST_AXONTYPE_EDIT_CONSTANT + 7;
  int DATATAG_ADDED                  = FIRST_AXONTYPE_EDIT_CONSTANT + 8;
  int DATATAG_REMOVED                = FIRST_AXONTYPE_EDIT_CONSTANT + 9;
  
  int LAST_AXONTYPE_EDIT_CONSTANT    = DATATAG_REMOVED;

  NeuronType getNeuronType();
  
  String  getType();

  String[] getDataTags();
  void     addDataTag(String tag) throws ReadOnlyException;
  void     removeDataTag(String tag) throws ReadOnlyException;

  int     getMinimumMultiplicity();
  void    setMinimumMultiplicity(int mult) throws ReadOnlyException, NeuronException;    

  int     getMaximumMultiplicity();
  void    setMaximumMultiplicity(int mult) throws ReadOnlyException, NeuronException;    
  
  String  getLineType();
  void    setLineType(String type);

  int     getLineThickness();
  void    setLineThickness(int thickness);

  int     getLineColor();
  void    setLineColor(int color);


  String  getHeadType();
  void    setHeadType(String type);

  boolean getHeadFilled();
  void    setHeadFilled(boolean filled);

  int     getHeadSize();
  void    setHeadSize(int size);
}
