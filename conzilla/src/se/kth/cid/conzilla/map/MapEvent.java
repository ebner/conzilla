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


package se.kth.cid.conzilla.map;

import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.graphics.*;
import java.awt.event.*;

public class MapEvent 
{
  public final static int    HIT_NONE       = 0;
  public final static int    HIT_BOX        = 1;
  public final static int    HIT_BOXLINE    = 2;
  public final static int    HIT_AXONLINE   = 3;
  public final static int    HIT_AXONDATA   = 4;

  public MouseEvent   mouseEvent;
  public int          hitType;
  public MapObject    mapObject;
  public int          lineSegmentNumber;

  public MapDisplayer mapDisplayer;
  
  public MapEvent(MouseEvent mouseEvent, int hitType, MapObject mapObject,
		  int lineSegmentNumber, MapDisplayer mapDisplayer)
  {
    this.mouseEvent        = mouseEvent;
    this.hitType           = hitType;
    this.mapObject         = mapObject;
    this.mapDisplayer      = mapDisplayer;
    this.lineSegmentNumber = lineSegmentNumber;
  }

  public MapEvent(MouseEvent mouseEvent, MapDisplayer mapDisplayer)
    { 
      this.mouseEvent        = mouseEvent;
      this.hitType           = HIT_NONE;
      this.mapObject         = null;
      this.mapDisplayer      = mapDisplayer;
      this.lineSegmentNumber = -1;
    }
  
  public void consume()
    {
      mouseEvent.consume();
    }
  

  public boolean isConsumed()
    {
      return mouseEvent.isConsumed();
    }  
}
