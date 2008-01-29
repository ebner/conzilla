/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.tool.MapToolsMenu;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.DrawerLayout;


public class TextAnchorMapTool extends MapToolsMenu
{  


  class AnchorMenuTool extends Tool {
      public AnchorMenuTool(final String name, final int value, MapController cont, final boolean vertical) {
	  super(name, EditMapManagerFactory.class.getName(), cont);

	  final JRadioButtonMenuItem mi = new JRadioButtonMenuItem();
	  setJMenuItem(mi);
	  mi.addItemListener(new ItemListener() {
		  public void itemStateChanged(ItemEvent e)
		  {
		      if(e.getStateChange() == ItemEvent.SELECTED && drawerLayout !=null)
			  if (vertical)
			      drawerLayout.setVerticalTextAnchor(value);
			  else
			      drawerLayout.setHorisontalTextAnchor(value);
		  }});
      }

	public void actionPerformed(ActionEvent e) {
	}
  }

    AnchorMenuTool north;
    AnchorMenuTool south;
    AnchorMenuTool hcenter;
    AnchorMenuTool vcenter;
    AnchorMenuTool west;
    AnchorMenuTool east;
    DrawerLayout drawerLayout;

  /** Constructs an DataVisibilityMapTool.
   */
  public TextAnchorMapTool(MapController cont)
  {
    super("TEXT_ANCHOR", EditMapManagerFactory.class.getName(), cont);


    ButtonGroup vgroup = new ButtonGroup();
    north = new AnchorMenuTool("NORTH", ConceptLayout.NORTH, cont, true);
    vgroup.add((AbstractButton) north.getJMenuItem());
    addTool(north, 100);

    vcenter = new AnchorMenuTool("CENTER", ConceptLayout.CENTER, cont, true);
    vgroup.add((AbstractButton) vcenter.getJMenuItem());
    addTool(vcenter, 200);

    south = new AnchorMenuTool("SOUTH", ConceptLayout.SOUTH, cont, true);
    vgroup.add((AbstractButton) south.getJMenuItem());
    addTool(south, 300);

    addSeparator(399);
    
    ButtonGroup hgroup = new ButtonGroup();
    west = new AnchorMenuTool("WEST", ConceptLayout.WEST, cont, false);
    hgroup.add((AbstractButton) west.getJMenuItem());
    addTool(west, 400);

    hcenter = new AnchorMenuTool("CENTER", ConceptLayout.CENTER, cont, false);
    hgroup.add((AbstractButton) hcenter.getJMenuItem());
    addTool(hcenter, 500);

    east = new AnchorMenuTool("EAST", ConceptLayout.EAST, cont, false);
    hgroup.add((AbstractButton) east.getJMenuItem());
    addTool(east, 600);
  }

  public void update(MapEvent mapEvent)
    {
        if (!mapEvent.mapObject.getDrawerLayout().isEditable()) {
            setEnabled(false);
            return;
        }
        
	if (!getPopupMenu().isVisible() && 
	    mapEvent.mapObject != null &&
	    mapEvent.mapObject.getDrawerLayout() != null)
	    {
		drawerLayout = null;
		DrawerLayout ns = mapEvent.mapObject.getDrawerLayout();
		switch (ns.getHorisontalTextAnchor())
		    {
		    case ConceptLayout.WEST:
			west.getJMenuItem().setSelected(true);
			break;
		    case ConceptLayout.CENTER:
			hcenter.getJMenuItem().setSelected(true);
			break;
		    case ConceptLayout.EAST:
			east.getJMenuItem().setSelected(true);
			break;
		    }
		switch (ns.getVerticalTextAnchor())
		    {
		    case ConceptLayout.NORTH:
			north.getJMenuItem().setSelected(true);
			break;
		    case ConceptLayout.CENTER:
			vcenter.getJMenuItem().setSelected(true);
			break;
		    case ConceptLayout.SOUTH:
			south.getJMenuItem().setSelected(true);
			break;
		    }
		setEnabled(true);
		drawerLayout = ns;
	    }
      else
	  setEnabled(false);
      
      super.update(mapEvent);
  }
}


