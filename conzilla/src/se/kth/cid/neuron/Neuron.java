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

public interface Neuron extends Component
{
  int FIRST_NEURON_EDIT_CONSTANT = LAST_COMPONENT_EDIT_CONSTANT + 1;
  int TYPE_EDITED                = FIRST_NEURON_EDIT_CONSTANT;
  int DATAVALUE_ADDED            = FIRST_NEURON_EDIT_CONSTANT + 1;
  int DATAVALUE_REMOVED          = FIRST_NEURON_EDIT_CONSTANT + 2;
  int ROLE_ADDED                 = FIRST_NEURON_EDIT_CONSTANT + 3;
  int ROLE_REMOVED               = FIRST_NEURON_EDIT_CONSTANT + 4;
  int PLAYSROLESIN_ADDED         = FIRST_NEURON_EDIT_CONSTANT + 5;
  int PLAYSROLESIN_REMOVED       = FIRST_NEURON_EDIT_CONSTANT + 6;
  int LAST_NEURON_EDIT_CONSTANT  = FIRST_NEURON_EDIT_CONSTANT + 6;

  //////////// NeuronType ////////////////////
  String       getType();
  void      setType(String uri)
    throws ReadOnlyException, MalformedURIException;

  //////////// Data //////////////////////////
  String[] getDataTags();
  String[] getDataValues(String tag);
  void     addDataValue(String tag, String value) throws ReadOnlyException;
  void     removeDataValue(String tag, String value) throws ReadOnlyException;

  //////////// Roles /////////////////////////
  String[]  getRoleTypes();
  Role[]    getRolesOfType(String type);
  void      removeRole(Role role) throws ReadOnlyException;
  void      addRole(Role role) throws ReadOnlyException, NeuronException;

  //////////// PlaysRoleIn ///////////////////
  String[]     getPlaysRolesIn();
  void      removePlaysRoleIn(String uri)
    throws ReadOnlyException, MalformedURIException;
  void      addPlaysRoleIn(String uri)
    throws ReadOnlyException, MalformedURIException;
}



