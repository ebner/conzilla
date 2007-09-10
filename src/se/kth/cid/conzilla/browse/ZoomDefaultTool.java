/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;
import java.awt.event.ActionEvent;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.tool.Tool;


public class ZoomDefaultTool extends Tool
{
  MapController controller;

  public ZoomDefaultTool(MapController cont)
  {
    super("ZOOM_DEFAULT", MapController.class.getName());
    this.controller = cont;
  }
  
 public void actionPerformed(ActionEvent e)
    {
      controller.getView().setScale(1.0);
    }
  
}
