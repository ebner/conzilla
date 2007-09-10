/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.beans.PropertyChangeEvent;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.layers.CreateLayer;
import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.conzilla.tool.StateTool;


/** Controlls the createlayer and which type that should be created next.
 *
 *  @author Matthias Palmer.
 */
public class CreateTool extends StateTool
{
  
    GridModel gridModel;
    EditMapManager edit;
    protected CreateLayer createLayer;
    boolean layerpushed=false;

 public CreateTool(MapController controller, EditMapManager edit, GridModel gridModel)
  {
      super("CREATE", EditMapManagerFactory.class.getName(), false);
      this.edit = edit;

      createLayer = new CreateLayer(controller, gridModel);

      //      setIcon(new ImageIcon(GridTool.class.getResource("/graphics/edit/Grid16.gif")));

      this.gridModel=gridModel;
  }   

    public void propertyChange(PropertyChangeEvent e)
    {
	if(e.getPropertyName().equals(StateTool.ACTIVATED))
	    activated(((Boolean) e.getNewValue()).booleanValue());
    }
    
    void activated(boolean b)
    {
	if(b!=layerpushed)
	    {
		if (b)
		    edit.push(createLayer);
		else
		    edit.pop(createLayer);
		layerpushed = b;
	    }
    }
}
