/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;

import se.kth.cid.component.ContainerManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.app.FullScreenTool;
import se.kth.cid.conzilla.browse.Highlighter;
import se.kth.cid.conzilla.clipboard.Clipboard;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.controller.MapManager;
import se.kth.cid.conzilla.controller.MapManagerFactory;
import se.kth.cid.conzilla.edit.layers.GridLayer;
import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.conzilla.edit.layers.LayerManager;
import se.kth.cid.conzilla.edit.layers.MoveLayer;
import se.kth.cid.conzilla.edit.layers.handles.HandleStore;
import se.kth.cid.conzilla.edit.toolbar.CreateTools;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.session.SessionChooserMenu;
import se.kth.cid.conzilla.session.SessionManager;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsBar;
import se.kth.cid.conzilla.view.View;
import se.kth.cid.rdf.RDFContainerManager;
import se.kth.cid.util.LocaleChooser;
import se.kth.cid.util.Tracer;

public class EditMapManager extends LayerManager implements MapManager, PropertyChangeListener {
	
	GridTool grid;

	LineTool line;

	TieTool tie;

	Tool save;

	Tool fullScreen;

	Tool publish;

	Tool contributionInfo;
	
	Tool browseMode;

	GridModel gridModel;

	CreateTools create;

	SessionChooserMenu sessionMenu;

	Highlighter highlighter;

	MapController controller;

	LocaleChooser localeChooser;

	private HandleStore handleStore;

	private Clipboard clipboard;

    
    // Two default layers.
    protected MoveLayer moveLayer;
    
    protected GridLayer gridLayer;

	private Separator separator1;
	private Separator separator2;
	private Separator separator3;
	private Separator separator4;
	private Separator separator5;
    
//    private Tool containerInspector;

    public EditMapManager(MapController controller, Clipboard clipboard) {
        this.controller = controller;
        this.clipboard = clipboard;
        highlighter = new Highlighter(controller);
        localeChooser = new LocaleChooser();
        gridModel = new GridModel(6);
        handleStore = new HandleStore(gridModel);
        
        browseMode = new Tool("BROWSE_MODE", EditMapManagerFactory.class.getName()) {
			public void actionPerformed(ActionEvent ev) {
				Enumeration extras = ConzillaKit.getDefaultKit().getExtras();
				while (extras.hasMoreElements()) {
					Object nextExtra = extras.nextElement();
					if (nextExtra instanceof MapManagerFactory) {
						MapManagerFactory mmf = (MapManagerFactory) nextExtra;
						if (mmf.getName().equals("BrowseMapManagerFactory")) {
							ConzillaKit.getDefaultKit().getConzilla().changeMapManagerFactory(EditMapManager.this.controller, mmf);
						}
					}
				}
            }
        };
        browseMode.setIcon(Images.getImageIcon(Images.ICON_FILE_BROWSE));
    }
    
    public MoveLayer getMoveLayer() {
        return moveLayer;
    }
    
    public Clipboard getClipBoard() {
        return clipboard;
    }
    
    public void install() {
        super.install(controller);
        gotFocus();

        View view = controller.getView();
        
        highlighter.install();
        controller.addPropertyChangeListener(this);

        //SessionMenu
        ContainerManager cm = ConzillaKit.getDefaultKit().getResourceStore().getContainerManager();
        sessionMenu = new SessionChooserMenu(controller, (RDFContainerManager) cm);
        view.addMenu(sessionMenu, 35);
    
        //ToolBar
        ToolsBar bar = view.getToolsBar();
        
        grid = new GridTool(gridModel);
        line = new LineTool();
        tie = new TieTool();
        save = new SaveTool(view.getController());
        publish = new PublishTool(this, controller, (SaveTool)save);
        contributionInfo = new ContributionInfoTool(controller);
        fullScreen = new FullScreenTool(view.getToolsBar());
        create = new CreateTools(this, controller, gridModel);
        gridLayer = new GridLayer(controller);
        moveLayer = new MoveLayer(controller, line, tie, this);

        //GridLayer should be at the bottom, then moveLayer next.
        push(gridLayer);
        push(moveLayer);
        
        //Switch to browse mode
        bar.addTool(browseMode);
        
        separator1 = new JToolBar.Separator(null);
        bar.add(separator1);
        
        bar.addTool(save);
        bar.addTool(publish);
        bar.addTool(contributionInfo);
        
        separator2 = new JToolBar.Separator(null);
        bar.add(separator2);
        
        grid.installYourself(bar);
        bar.addTool(line);
        bar.addTool(tie);
        
        separator3 = new JToolBar.Separator(null);
        bar.add(separator3);
        
        bar.addTool(highlighter.sHigh);
        bar.addTool(highlighter.vHigh);
        bar.addTool(highlighter.iHigh);
        
        separator4 = new JToolBar.Separator(null);
        bar.add(separator4);
        
        bar.addTool(fullScreen);
        
        separator5 = new JToolBar.Separator(null);
        bar.add(separator5);
        
        bar.add(localeChooser);
        //bar.addTool(create);
        view.addToRight(create, "Create Types", null);
        create.revalidate();

        //Initialize layers.
        install(view.getMapScrollPane());

        view.getMapScrollPane().getDisplayer().setDisplayLanguageDiscrepancy(true);
        //view.addToLeft(view.getController().getContainerEntries(), "Contributions", null);
    }

    public GridModel getGridModel() {
        return gridModel;
    }

    public void deInstall() {
    	View view = controller.getView();
    	if (view != view) {
            //install never worked or wrong somehow
            return;
        }
    	view.removeFromLeft(view.getController().getContainerEntries());
    	controller.removePropertyChangeListener(this);
        
        view.removeMenu(sessionMenu);
        
        //uninstall layers.
        uninstall(view.getMapScrollPane());

        //ToolsMenu tmenu = controller.getMenu(MenuFactory.TOOLS_MENU);
      //  tmenu.removeTool(containerInspector);
        
        ToolsBar bar = view.getToolsBar();
        
        bar.removeTool(browseMode);
        browseMode.detach();

        bar.removeTool(save);
        save.detach();

        bar.removeTool(publish);
        publish.detach();
        
        bar.removeTool(contributionInfo);
        contributionInfo.detach();
        
        grid.removeYourself(bar);
        grid.detach();

        bar.removeTool(line);
        line.detach();

        bar.removeTool(tie);
        tie.detach();

        bar.removeTool(highlighter.sHigh);
        bar.removeTool(highlighter.vHigh);
        bar.removeTool(highlighter.iHigh);
        bar.removeTool(fullScreen);
        bar.remove(localeChooser);

        //bar.removeTool(create);
        //create.detach();
        view.removeFromRight(create);

        view.getMapScrollPane()
            .getDisplayer()
            .setDisplayLanguageDiscrepancy(
            false);
        highlighter.deInstall();
        bar.remove(separator1);
        bar.remove(separator2);
        bar.remove(separator3);
        bar.remove(separator4);
        bar.remove(separator5);
        
//        bar.removeAll(); // to remove separators as well
    }

    public void gotFocus() {
    	SessionManager sessionManager = ConzillaKit.getDefaultKit().getSessionManager();
    	Session session = controller.getConceptMap().getComponentManager().getEditingSesssion();
    	if (session != null) { //Just in case
    		sessionManager.setCurrentSession(session);
    	}
        Tracer.debug("got Focus in EditMapManager");
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(MapController.MAP_PROPERTY)) {
            if (e.getNewValue() != null) {
                uninstall((MapScrollPane) e.getOldValue());
                install((MapScrollPane) e.getNewValue());
            }
        }
    }

    public HandleStore getHandleStore() {
        return handleStore;
    }

	public JComponent embeddMap(MapScrollPane map) {
		return map;
	}
}
