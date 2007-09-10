/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map;

import java.awt.event.MouseEvent;

public class MapEvent 
{
  public static final int    HIT_NONE       = 0;
  public static final int    HIT_BOX        = 1;
  public static final int    HIT_BOXTITLE   = 2;
  public static final int    HIT_BOXDATA    = 3;
  public static final int    HIT_BOXLINE    = 4;
  public static final int    HIT_TRIPLELINE   = 5;
  public static final int    HIT_TRIPLELITERAL   = 6;
  public static final int    HIT_TRIPLELITERALBOX   = 7;
  public static final int    HIT_TRIPLEDATA   = 8;

  public MouseEvent   mouseEvent;
  public int          hitType;
  public int          mapX;
  public int          mapY;
  public MapObject    mapObject;
  public MapObject    parentMapObject;
  public int          lineSegmentNumber;

  public MapDisplayer mapDisplayer;

  public static MapEvent Null=new MapEvent(null, -1, 0, 0, null, -1, null);
  
  public MapEvent(MouseEvent mouseEvent, int hitType, int mapX,
		  int mapY, MapObject mapObject,
		  int lineSegmentNumber, MapDisplayer mapDisplayer)
    {
      this.mouseEvent        = mouseEvent;
      this.hitType           = hitType;
      this.mapX              = mapX;
      this.mapY              = mapY;
      this.mapObject         = mapObject;
      this.mapDisplayer      = mapDisplayer;
      this.lineSegmentNumber = lineSegmentNumber;
    }

  public MapEvent(MouseEvent mouseEvent, int mapX, int mapY,
		  MapDisplayer mapDisplayer)
    { 
      this.mouseEvent        = mouseEvent;
      this.hitType           = HIT_NONE;
      this.mapX              = mapX;
      this.mapY              = mapY;
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
