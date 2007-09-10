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

package se.kth.cid.conzilla.content;
import se.kth.cid.content.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.util.*;
import java.applet.*;
import java.awt.*;
import java.net.*;
import java.io.*;

public class NetscapeContentDisplayer extends AbstractContentDisplayer
{
  public NetscapeContentDisplayer()
  {
  }

  public void setContent(ContentDescription cd) throws ContentException
  {
    if(cd == null)
      {
	super.setContent(null);
	return;
      }
    try {
      Tracer.debug("Netscape will show " + cd.getContentURI());
      String openURL = "openURL(" +  cd.getContentURI().toString() + ")";
      String[] command = {"netscape", "-remote", openURL};
      Runtime.getRuntime().exec(command);
      super.setContent(cd);
    } catch(IOException e)
      {
	throw new ContentException("Could not execute netscape:\n "
				   + e.getMessage(), cd);
      }
  }
}
