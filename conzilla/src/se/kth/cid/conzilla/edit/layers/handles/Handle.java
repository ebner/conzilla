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


package se.kth.cid.conzilla.edit.layers.handles;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conceptmap.*;
import java.awt.*;
import java.util.*;

public interface Handle
{
    ConceptMap.Position getOriginalPosition();
    ConceptMap.Position getPosition();
    boolean contains(MapEvent m);
    ConceptMap.Position getOffset(MapEvent m);
    void paint(Graphics2D g);
    void simplePaint(Graphics2D g);
    Collection drag(int x, int y);
    Collection dragForced(int x, int y);
    boolean isSelected();
    void setSelected(boolean selected);

    boolean isEdited();
    void clearEdited();
    
    /** If there are any followers, this function moves them, 
     *  inteded to be called from within the @link AbstractHandle.drag function.
     *
     *  @see AbstractHandle.drag
     */
    Collection dragFollowers(int x, int y);    
    void setFollowers(Collection fols);
    Collection getFollowers();
    Rectangle getBounds();
}
