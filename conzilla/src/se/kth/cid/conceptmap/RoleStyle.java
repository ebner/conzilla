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
import se.kth.cid.util.*;
import java.awt.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;


public class RoleStyle
{
  public static final int FIRST_ROLESTYLE_EDIT_CONSTANT = NeuronStyle.LAST_NEURONSTYLE_EDIT_CONSTANT + 1;
  public static final int LINE_EDITED                   = FIRST_ROLESTYLE_EDIT_CONSTANT;
  public static final int LAST_ROLESTYLE_EDIT_CONSTANT  = FIRST_ROLESTYLE_EDIT_CONSTANT;
  
  public class RoleStyleEditObject 
  {
    public RoleStyle   roleStyle;
    public Object      target;
    public RoleStyleEditObject(RoleStyle rs, Object o)
    {
      roleStyle = rs;
      target = o;
    }
    public String toString()
    {
      return "RoleStyleEditObject[" + roleStyle + "," + target + "]";
    }

  }

  private Role        role;
  private RoleType    roletype;
  private NeuronStyle roleowner;
  private NeuronStyle roleplayer;
  private Point[]     line;
  private Object     appobject;
  ConceptMap         conceptmap;
  
  public RoleStyle(Role role, RoleType roletype, NeuronStyle roleowner, NeuronStyle roleplayer, ConceptMap map) 
    {
      this.role=role;
      this.roletype=roletype;
      this.roleowner=roleowner;
      this.roleplayer=roleplayer;
      this.conceptmap = map;
      line=new Point[0];
      appobject=null;
    }

  public void disconnect()  throws ReadOnlyException
    {
      if (!conceptmap.isEditable())
	throw new ReadOnlyException("");
      roleowner.removeRole(this);
      roleplayer.removePlaysRole(this);
      conceptmap.fireEditEvent(new EditEvent(conceptmap, ConceptMap.ROLESTYLE_REMOVED, this));
    }

  public Point[] getLine()
    {
      return line;
    }

  public void setLine(Point[] line) throws ReadOnlyException, NullPointerException
    {
      if (!conceptmap.isEditable())
	throw new ReadOnlyException("");
      if(line==null)
	throw new NullPointerException("");
      this.line=line;
      conceptmap.fireEditEvent(new EditEvent(conceptmap, LINE_EDITED,
					     new RoleStyleEditObject(this, line)));
    }

  public Role getRole()
    {
      return role;
    }

  public RoleType getRoleType()
    {
      return roletype;
    }

  public NeuronStyle getRoleOwner()
    {
      return roleowner;
    }

  public NeuronStyle getRolePlayer()
    {
      return roleplayer;
    }

  public Object  getAppObject()
    {
      return appobject;
    }

  public void    setAppObject(Object ob)
    {
      appobject=ob;
    }
}

