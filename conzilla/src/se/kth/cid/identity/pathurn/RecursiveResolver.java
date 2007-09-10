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


package se.kth.cid.identity.pathurn;
import se.kth.cid.identity.*;
import se.kth.cid.util.*;
import java.util.*;


/** A PathURNResolver that uses another resolver to resolve
 *  URIs recursively. It tries to resolve every Path URN
 *  in the result into URIs of other types, recursively.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class RecursiveResolver implements PathURNResolver
{

  /** The resolver.
   */
  PathURNResolver resolver;
  
  /** Constructs an RecursiveResolver using the given resolver.
   */
  public RecursiveResolver(PathURNResolver resolver)
    {
      this.resolver = resolver;
    }
  
  public void setResolver(PathURNResolver resolver)
    {
      this.resolver = resolver;
    }

  public PathURNResolver getResolver()
    {
      return resolver;
    }

  public ResolveResult[] resolve(PathURN urn) throws ResolveException
    {
      List resultList     = new Vector();
      
      addLocation(new ResolveResult(urn, null, null, null), resultList);

      return (ResolveResult[]) resultList.toArray(new ResolveResult[resultList.size()]);      
    }
  
  protected void resolveURN(PathURN urn, List resultList) throws ResolveException
    {
      ResolveResult[] res = resolver.resolve(urn);
      
      for(int i = 0; i < res.length; i++)
	addLocation(res[i], resultList);
    }

  protected void addLocation(ResolveResult res, List resultList) throws ResolveException
    {
      if(res.uri instanceof PathURN)
	{
	  resolveURN((PathURN) res.uri, resultList);
	  return;
	}

      resultList.add(res);
    }
}
