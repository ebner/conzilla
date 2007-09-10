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
import se.kth.cid.component.*;

import java.beans.*;

public abstract class AbstractContentDisplayer implements ContentDisplayer
{
  protected ComponentStore store;
  
  protected Component currentContent;
  protected PropertyChangeSupport changeSupport;
  
  public AbstractContentDisplayer(ComponentStore store)
    {
      changeSupport = new PropertyChangeSupport(this);
      this.store = store;
    }
  
  public void setContent(Component c) throws ContentException
    {
      Component oldContent = currentContent;
      currentContent = c;
      if(oldContent != null)
	store.getCache().dereferenceComponent(c.getURI());
      if(currentContent != null)
	store.getCache().referenceComponent(c);
      changeSupport.firePropertyChange("content", oldContent, currentContent);
    }
  
  public Component getContent()
    {
      return currentContent;
    }
  
  public void addPropertyChangeListener(PropertyChangeListener l)
    {
      changeSupport.addPropertyChangeListener(l);
    }
  
  public void removePropertyChangeListener(PropertyChangeListener l)
    {
      changeSupport.removePropertyChangeListener(l);
    }
}
