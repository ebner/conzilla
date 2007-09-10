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
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import se.kth.cid.component.*;

public interface NeuronType extends Component
{
  int FIRST_NEURONTYPE_EDIT_CONSTANT = Neuron.LAST_NEURON_EDIT_CONSTANT + 1;
  int BOXTYPE_EDITED                 = FIRST_NEURONTYPE_EDIT_CONSTANT;
  int BOXCOLOR_EDITED                = FIRST_NEURONTYPE_EDIT_CONSTANT + 1;
  int LINETYPE_EDITED                = FIRST_NEURONTYPE_EDIT_CONSTANT + 2;
  int LINETHICKNESS_EDITED           = FIRST_NEURONTYPE_EDIT_CONSTANT + 3;
  int LINECOLOR_EDITED               = FIRST_NEURONTYPE_EDIT_CONSTANT + 4;
  int DATATAG_ADDED                  = FIRST_NEURONTYPE_EDIT_CONSTANT + 5;
  int DATATAG_REMOVED                = FIRST_NEURONTYPE_EDIT_CONSTANT + 6;
  int ROLETYPE_ADDED                 = FIRST_NEURONTYPE_EDIT_CONSTANT + 7;
  int ROLETYPE_REMOVED               = FIRST_NEURONTYPE_EDIT_CONSTANT + 8;
  int LAST_NEURONTYPE_EDIT_CONSTANT  = FIRST_NEURONTYPE_EDIT_CONSTANT + 8;


  ///////////BoxStyle////////////
  String  getBox();
  void    setBox(String box) throws ReadOnlyException;
  int     getBoxColor();
  void    setBoxColor(int color) throws ReadOnlyException;
  String  getLineType();
  void    setLineType(String linetype) throws ReadOnlyException;
  int     getLineThickness();
  void    setLineThickness(int thick) throws LineThicknessException, ReadOnlyException;
  int     getLineColor();
  void    setLineColor(int color) throws ReadOnlyException;

  String[] getDataTags();
  void     addDataTag(String tag) throws ReadOnlyException;
  void     removeDataTag(String tag) throws ReadOnlyException;
  

  ///////////RoleTypes//////////////
  int        getDegree();
  RoleType   getRoleType(String type);
  RoleType[] getRoleTypes();
  void       addRoleType(RoleType roletype) throws NeuronException, ReadOnlyException;
  void       removeRoleType(RoleType roletype) throws ReadOnlyException;
}



