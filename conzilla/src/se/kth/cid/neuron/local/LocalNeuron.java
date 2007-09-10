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


package se.kth.cid.neuron.local;
import se.kth.cid.component.*;
import se.kth.cid.component.local.*;
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import java.util.*;

public class LocalNeuron extends LocalComponent implements Neuron
{
  private URI neurontype;

  private Hashtable vroles;
  // Cache
  private Hashtable roles;
  private String[] roletypes;

  private Vector vplaysrolesin;
  // Cache
  private String[] playsrolesin;
  
  private Hashtable vdatavalues;
  // Cache
  private Hashtable datavalues;
  private String[] datatags;

  public LocalNeuron()
  {
    roles=new Hashtable();
    vroles=new Hashtable();

    vplaysrolesin=new Vector();

    vdatavalues = new Hashtable();
    datavalues = new Hashtable();
  }
  
  
  public String  getType()
  {
    if (neurontype!=null)
      return neurontype.toString();
    else
      return null;
  }

  public void setType(String uri) throws ReadOnlyException, MalformedURIException
  {
    if (!isEditable())
      throw new ReadOnlyException("");
    neurontype=new URI(uri);
    fireEditEvent(new EditEvent(this, TYPE_EDITED, uri));
  }
  

  public String[] getDataTags()
  {
    if (datatags==null)
      {
	datatags=new String[vdatavalues.size()];
	Enumeration en=vdatavalues.keys();
	for (int i=0; en.hasMoreElements();i++)
	  datatags[i]= (String) en.nextElement();
      }
    return datatags;
  }

  public String[] getDataValues(String tag)
  {
    String[] savedValues = (String[]) datavalues.get(tag);
    
    if (savedValues == null)
      {
	Vector dataValueVector = (Vector) vdatavalues.get(tag);

	if(dataValueVector == null)
	  return new String[0];

	savedValues = new String[dataValueVector.size()];
	dataValueVector.copyInto(savedValues);

	datavalues.put(tag, savedValues);
      }
    return savedValues;
  }

  
  public void addDataValue(String tag, String value) throws ReadOnlyException
  {
    if(!isEditable())
      throw new ReadOnlyException("This Neuron is Read-Only!");


    Vector values=(Vector) vdatavalues.get(tag);

    if (values==null)
      {
	values=new Vector();
	vdatavalues.put(tag, values);
	datatags=null;
      }
    else
      datavalues.remove(tag);
    
    values.addElement(value);
    fireEditEvent(new EditEvent(this, DATAVALUE_ADDED, tag));
  }

  
  public void removeDataValue(String tag, String value)
    throws ReadOnlyException
  {    
    if(!isEditable())
      throw new ReadOnlyException("This Neuron is Read-Only!");

    int index;
    Vector values=(Vector) vdatavalues.get(tag);
    if (values != null && (index = values.indexOf(value)) != -1)
      {
	values.removeElementAt(index);
	datavalues.remove(tag);
	
	if (values.isEmpty())
	  {
	    vdatavalues.remove(tag);
	    datatags=null;
	  }
	fireEditEvent(new EditEvent(this, DATAVALUE_REMOVED, tag));
      }
  }
  
  /** Returns the roletypes in this neuron.
   * If the array isn't up to date it is regenerated.
   * @see getRolesOfType
   * @returns an String array of roletypes.
   */
  public String[] getRoleTypes()
  {
    if (roletypes==null)
      {
	roletypes=new String[vroles.size()];
	Enumeration en=vroles.keys();
	for (int i=0; en.hasMoreElements();i++)
	  roletypes[i]= (String) en.nextElement();
      }
    return roletypes;
  }
    
  public Role[] getRolesOfType(String type)
  {
    Role[] savedRoles = (Role[]) roles.get(type);

    if (savedRoles == null)
      {
	Vector rolesOfType = (Vector) vroles.get(type);

	if(rolesOfType == null)
	  return new Role[0];

	savedRoles=new Role[rolesOfType.size()];
	rolesOfType.copyInto(savedRoles);

	roles.put(type, savedRoles);
      }
    return savedRoles;
  }
  
  public void removeRole(Role role) throws ReadOnlyException
    {
    if (!isEditable())
      throw new ReadOnlyException("");
    int index;
    Vector nvr=(Vector) vroles.get(role.type);
    if (nvr != null && (index=nvr.indexOf(role)) != -1)
      {
	nvr.removeElementAt(index);  //Remove role from typespecific role-Vector.
	roles.remove(role.type); //Array-mirror of special type needs updating!!
	if (nvr.isEmpty())
	  {
	    vroles.remove(role.type); //Remove type from hashtable.
	    roletypes=null;                //Array-mirror needs updating!!
	  }
	fireEditEvent(new EditEvent(this, ROLE_REMOVED, role));
      }
  }  

  public void addRole(Role role) throws NeuronException, ReadOnlyException
  {
    if (!isEditable())
      throw new ReadOnlyException("");

    if (role==null)
      throw new NullPointerException("");
      
    Vector nvr=(Vector) vroles.get(role.type);
    if (nvr==null)
      {
	nvr=new Vector();
	vroles.put(role.type,nvr);
	roletypes=null;                //Array-mirror of types needs updating.
      }
    else
      {
	if(nvr.contains(role))
	  throw new NeuronException("Already have Role with type \""
				    + role.type + "\" and uri: "  + role.neuronuri);
	roles.remove(role.type);   //Array-mirror of specific type needs updating.
      }
    
    nvr.addElement(role);
    fireEditEvent(new EditEvent(this, ROLE_ADDED, role));
  }
    
  public String[] getPlaysRolesIn()
  { 
    if (playsrolesin==null)
      {
	playsrolesin=new String[vplaysrolesin.size()];
	Enumeration en=vplaysrolesin.elements();
	for (int i=0;en.hasMoreElements();i++)
	  playsrolesin[i]=((URI) en.nextElement()).toString();
      }
    return playsrolesin; 
  }
    
  public void removePlaysRoleIn(String uri) throws ReadOnlyException, MalformedURIException
  {
    if (!isEditable())
      throw new ReadOnlyException("");

    int index;
    if((index=vplaysrolesin.indexOf(new URI(uri))) != -1)
      {
	vplaysrolesin.removeElementAt(index); //Remove uri from Vector..
	playsrolesin=null;                    //Array-mirror needs updating.
	fireEditEvent(new EditEvent(this, PLAYSROLESIN_ADDED, uri));
      }
  }
	
  public void addPlaysRoleIn(String uri) throws ReadOnlyException, MalformedURIException
  {
    if (!isEditable())
      throw new ReadOnlyException("");
      
    vplaysrolesin.addElement(new URI(uri));
    playsrolesin=null;
    fireEditEvent(new EditEvent(this, PLAYSROLESIN_REMOVED, uri));
  }

}
