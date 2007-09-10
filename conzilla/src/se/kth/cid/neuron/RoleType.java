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

public class RoleType
{
  public String  type;
  public String  linetype;
  public int     linethickness;
  public int     linecolor;
  public int     lowmultiplicity;
  public int     highmultiplicity;
  public String  headtype;
  public boolean filled;
  public int     headsize;

  public RoleType() {}
  
  public RoleType(String type)
    {
      this.type=type;
    }
  
  public RoleType(String type, String linetype, int linethickness, int linecolor,
	   int lowmultiplicity, int highmultiplicity,
	   String headtype, boolean filled, int headsize)
    {
      this.type=type;
      this.linetype=linetype;
      this.linethickness=linethickness;
      this.linecolor=linecolor;
      this.lowmultiplicity=lowmultiplicity;
      this.highmultiplicity=highmultiplicity;
      this.headtype=headtype;
      this.filled=filled;
      this.headsize=headsize;
    }  

  public boolean equals(Object ob)
    {
      if (ob==null || !(ob instanceof RoleType))
	return false;
      return ((RoleType) ob).type.equals(type);
    }
}
