/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import se.kth.cid.component.Component;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.metadata.InfoPanel;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsMenu;

public class ContentMenu extends ToolsMenu
{
    public static final String CONTENT_MENU = "CONTENT_MENU";
    //  MapMenuTool info;
    //  MapMenuTool store;
    
    MapController controller;
    
  public ContentMenu(MapController cont)
  {
    super(CONTENT_MENU, ContentMenu.class.getName());

    controller = cont;
    addTool(new ContentTool("VIEW", ContentMenu.class.getName()) {
	    public void actionPerformed(ActionEvent e)
	    {
		controller.getContentSelector().select(contentIndex);
	    }}, 100);
      addTool(new ContentTool("INFO", ContentMenu.class.getName()) {
             public void actionPerformed(ActionEvent e)
             {
                 Component content = controller.getContentSelector().getContent(contentIndex);
                 InfoPanel.launchInfoPanelInFrame(content);
             }}, 200);
    //FIXME to extra!
    /*    addTool(new ContentTool("INFO", ContentMenu.class.getName()) {
	    public void actionPerformed(ActionEvent e) {
		Component comp = controller.getContentSelector().getContent(contentIndex);
		controller.getConzillaKit().getMetaDataDisplayer().showMetaData(comp);	
	    }}, 200);
    */
    ConzillaKit.getDefaultKit().extendMenu(this, controller);
  }
    
  public MapController getController()
  {
    return controller;
  }
    
  public void showPopup(MouseEvent ev, int index)
  {
      if (!getPopupMenu().isVisible())
	  {
	      Enumeration e = getTools();
	      while(e.hasMoreElements())
		  {
		      Tool t = (Tool) e.nextElement();
		      if(t instanceof ContentTool)
			  ((ContentTool) t).update(index);
		  }
	  }
      getPopupMenu().show((java.awt.Component) ev.getSource(),
			  ev.getX(),
			  ev.getY());   
  }
}
