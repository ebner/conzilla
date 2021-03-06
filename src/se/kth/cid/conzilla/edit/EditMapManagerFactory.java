/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.component.Resource;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.conzilla.app.ConzillaEnvironment;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.app.Extra;
import se.kth.cid.conzilla.browse.BrowseMapManagerFactory;
import se.kth.cid.conzilla.clipboard.Clipboard;
import se.kth.cid.conzilla.clipboard.CopyMapTool;
import se.kth.cid.conzilla.content.ContentMenu;
import se.kth.cid.conzilla.content.ContentTool;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.controller.MapManager;
import se.kth.cid.conzilla.controller.MapManagerFactory;
import se.kth.cid.conzilla.edit.wizard.newmap.NewMapWizard;
import se.kth.cid.conzilla.menu.DefaultMenuFactory;
import se.kth.cid.conzilla.menu.MenuFactory;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.session.SessionManager;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsBar;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.rdf.RDFContainerManager;
import se.kth.cid.rdf.RDFSessionManager;

/** This class creates EditMapManagers for a single MapController.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class EditMapManagerFactory implements MapManagerFactory {
	
	Log log = LogFactory.getLog(EditMapManagerFactory.class);
	
    public static final String PROJECTS_URI = "conzilla://ont/sessions.rdf";

    public static final String EDIT_CONCEPT_MENU = "EDIT_CONCEPT_MENU";

	public static final String EDIT_RELATION_MENU = "EDIT_RELATION_MENU";

	public static final String EDIT_MAP_MENU = "EDIT_MAP_MENU";

	public static final String EDIT_CONTENT_MENU = "EDIT_CONTENT_MENU";
	
	public static final String EDIT_MENU = "EDIT_MENU";

	ConzillaKit kit;

	NewMapWizard newMap;

	Clipboard clipboard;

    public EditMapManagerFactory() {
    }

    public String getName() {
        return "EditMapManagerFactory";
    }

    public boolean initExtra(ConzillaKit kit) {
        this.kit = kit;
        
        ContainerManager cm = kit.getResourceStore().getContainerManager();
		SessionManager sessionManager = new RDFSessionManager((RDFContainerManager) cm, kit.getResourceStore());
        sessionManager.loadSessions(PROJECTS_URI);
        kit.setSessionManager(sessionManager);
        
        newMap = new NewMapWizard(this, cm, sessionManager);
        
        //FIXME: really ugly, I load all containers from sessions in 
        //advance to simplify for the users to load maps created earlier.
        //Update: this is deactivated because we don't have the map chooser anymore
//        ResourceStore store = kit.getResourceStore();
//        for (Iterator iter = sessionManager.getSessions().iterator(); iter.hasNext();) {
//            Session session = (Session) iter.next();
//            try {
//                store.getAndReferenceContainer(URI.create(session.getContainerURIForLayouts()));
//            } catch (ComponentException e) {
//            }
//        }
        
        clipboard = new Clipboard();
        
        return true;
    }

    public void extendMenu(ToolsMenu menu, final MapController mc) {
        String menuName = menu.getName();

    	if (menu.getName().equals(MenuFactory.FILE_MENU)) {
            menu.addTool(new Tool("NEW_MAP", EditMapManagerFactory.class.getName()) {
                {setIcon(Images.getImageIcon(Images.ICON_FILE_NEW));}
                public void actionPerformed(ActionEvent ae) {
                    log.debug("Create a new map in new window");
                    newMap.openNewMapInNewView(mc);
                }
            }, 150);
            //menu.addTool(new SessionBrowsingTool(sessionManager, mc), 180);
        } else if (menuName.equals(DefaultMenuFactory.TOOLS_MENU)) {
            menu.addTool((Tool) mc.get("SessionTool"), 400);
        } else if (menuName.equals(BrowseMapManagerFactory.BROWSE_MENU)) {
            menu.addSeparator(800);
            menu.addTool(new CopyMapTool(mc, clipboard), 810);
        } else if (menu.getName().equals(ContentMenu.CONTENT_MENU)) {
            final ContentMenu cm = (ContentMenu) menu;

            cm.addTool(new ContentTool("COPY", Clipboard.class.getName()) {
                public void actionPerformed(ActionEvent e) {
                    Resource comp = mc.getContentSelector().getContent(
                            contentIndex);
                    clipboard.setResource(comp);
                }
            }, 400);
        }
    }

    public void addExtraFeatures(MapController c) {
        SessionsTool sessionTool = new SessionsTool(c);
    	c.put("SessionTool", sessionTool);
		ToolsBar bar = c.getView().getToolsBar();
		bar.addTool(sessionTool);
    }

    public void refreshExtra() {
    }

    public boolean saveExtra() {
        //sessionManager.saveSessions(PROJECTS_URI);
        ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
                ContainerManager cm = store.getContainerManager();
                for (Iterator containersIt = cm.getContainers().iterator(); containersIt.hasNext();) {
                    Container container = (Container) containersIt.next();
                    if (container.isEdited()) {
                        String [] options = {"Save Changes", "Discard Changes", "Cancel"};
                        int option = JOptionPane.showOptionDialog(null, "Container with URI:\n"
                            +container.getURI()
                            +"\nloaded from:\n"
                            +container.getLoadURI()
                            +"\nis not saved.",
                            "Save before exit?", 
                            JOptionPane.YES_NO_CANCEL_OPTION, 
                            JOptionPane.QUESTION_MESSAGE, 
                            null, 
                            options, 
                            "Save Changes");
                        switch (option) {
                            case 0:
                                try {
                                    store.getComponentManager().saveResource(container);
                                } catch (ComponentException e) {
                                }
                                break;
                            case 1:
                                break;
                            case 2:
                                return false;
                        };
                    }
                }            
        return true;
    }

    /**
     * @return true if the uri represents a ContextMap.
     */
    public boolean canManage(MapController controller, URI uri) {
    	try {
            ResourceStore store =
                ConzillaKit.getDefaultKit().getResourceStore();
            ContextMap cMap = store.getAndReferenceConceptMap(uri);
            return cMap.getComponentManager().getEditingSesssion() == null && !uri.toString().equals(ConzillaEnvironment.DEFAULT_BLANKMAP);
        } catch (ComponentException ce) {
        }
        return false;
    }

    public void exitExtra() {
    }

    public MapManager createManager(MapController controller) {
        return new EditMapManager(controller, clipboard);
    }

    public boolean requiresSession() {
		return true;
	}
}
