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

import java.util.*;
import se.kth.cid.component.*;
import se.kth.cid.content.*;
import se.kth.cid.util.*;
import java.beans.*;

public class MultiContentDisplayer extends AbstractContentDisplayer
{
  Hashtable contentDisplayers;
  ContentDisplayer defaultContentDisplayer;
  ContentDisplayer currentContentDisplayer;

  PropertyChangeListener listener;
  
  public MultiContentDisplayer(ComponentLoader loader)
  {
    contentDisplayers = new Hashtable();
    listener = new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent e)
	{
	  if(e.getPropertyName().equals("content"))
	    contentChange();
	}
      };
  }

  public void addContentDisplayer(MIMEType type, ContentDisplayer displayer)
  {
    if(type == null)
      defaultContentDisplayer = displayer;
    else
      contentDisplayers.put(type, displayer);
  }

  
  public void setContent(ContentDescription cd)
    throws ContentException
  {
    if(cd == null)
      {
	deactivate();
	super.setContent(null);
	return;
      }
    
    ContentDisplayer disp =
      (ContentDisplayer) contentDisplayers.get(cd.getContentType());

    if(disp == null)
      disp = defaultContentDisplayer;

    if(disp == null)
      {
	throw new ContentException("Cannot show MIME Type " +
				   cd.getContentType(), cd);
      }

    activate(disp, cd);
  }

  void deactivate() throws ContentException
  {
    if(currentContentDisplayer != null)
      {
	currentContentDisplayer.removePropertyChangeListener(listener);
	try {
	  currentContentDisplayer.setContent(null);
	} catch(ContentException e)
	  {
	    currentContentDisplayer.addPropertyChangeListener(listener);
	    e.fillInStackTrace();
	    throw e;
	  }
	currentContentDisplayer = null;
      }
  }
  
  void activate(ContentDisplayer disp, ContentDescription cd)
    throws ContentException
  {
    if(disp != currentContentDisplayer)
      {
	deactivate();
	currentContentDisplayer = disp;
	currentContentDisplayer.addPropertyChangeListener(listener);
      }
    currentContentDisplayer.setContent(cd);
  }

  void contentChange()
  {
    try {
      super.setContent(currentContentDisplayer.getContent());
    } catch(ContentException e)
      {
	Tracer.trace("AbstractContentDisplayer threw exception!", Tracer.ERROR);
      }
  }
}
