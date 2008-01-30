/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.JToolBar.Separator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.ContainerManager;
import se.kth.cid.component.Resource;
import se.kth.cid.component.UndoListener;
import se.kth.cid.component.UndoManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.app.FullScreenTool;
import se.kth.cid.conzilla.browse.BrowseMapManagerFactory;
import se.kth.cid.conzilla.browse.Highlighter;
import se.kth.cid.conzilla.clipboard.Clipboard;
import se.kth.cid.conzilla.clipboard.CopyEditMapTool;
import se.kth.cid.conzilla.clipboard.CopyMapTool;
import se.kth.cid.conzilla.clipboard.CutMapTool;
import se.kth.cid.conzilla.clipboard.PasteConceptMapTool;
import se.kth.cid.conzilla.content.ContentMenu;
import se.kth.cid.conzilla.content.ContentTool;
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
import se.kth.cid.conzilla.menu.DefaultMenuFactory;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.session.SessionChooserMenu;
import se.kth.cid.conzilla.session.SessionManager;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsBar;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.conzilla.view.View;
import se.kth.cid.rdf.RDFContainerManager;
import se.kth.cid.util.LocaleChooser;

public class EditMapManager extends LayerManager implements MapManager, PropertyChangeListener, UndoListener {
	Log log = LogFactory.getLog(EditMapManager.class);

	//Utilities
	public MapController controller;
	public GridModel gridModel;
	public Highlighter highlighter;
	public LocaleChooser localeChooser;
	public HandleStore handleStore;
	public Clipboard clipboard;

	//Tools
	public GridTool grid;
	public LineTool line;
	public TieTool tie;
	public Tool save;
	public Tool fullScreen;
	public Tool publish;
	public Tool contributionInfo;
	public Tool browseMode;
	public CreateTools create;
	public Tool undo;
	public Tool redo;
	public CopyEditMapTool copy;
	public CutMapTool cut;
	public PasteConceptMapTool paste;

	//Separators
	private Separator separator1;
	private Separator separator2;
	private Separator separator3;
	private Separator separator4;
	private Separator separator5;

	//Menues
	public SessionChooserMenu sessionMenu;
	public ToolsMenu editMenu;
    
    //Layers.
	public MoveLayer moveLayer;
	public GridLayer gridLayer;


    public EditMapManager(final MapController mapController, Clipboard clipboard) {
        this.controller = mapController;
        this.clipboard = clipboard;
        highlighter = new Highlighter(mapController);
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
        editMenu = new ToolsMenu(EditMapManagerFactory.EDIT_MENU, EditMapManagerFactory.class.getName());
        undo = new Tool("UNDO", EditMapManagerFactory.class.getName()) {
        	{setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK));
        	setIcon(Images.getImageIcon(Images.ICON_UNDO));}
            public void actionPerformed(ActionEvent ae) {
            	mapController.getConceptMap().getComponentManager().getUndoManager().undo();
            }
			protected boolean updateEnabled() {
				return mapController.getConceptMap().getComponentManager().getUndoManager().canUndo();
			}            
        };
        redo = new Tool("REDO", EditMapManagerFactory.class.getName()) {
        	{setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.SHIFT_MASK | Event.CTRL_MASK));
        	setIcon(Images.getImageIcon(Images.ICON_REDO));}
            public void actionPerformed(ActionEvent ae) {
            	mapController.getConceptMap().getComponentManager().getUndoManager().redo();
            }
			protected boolean updateEnabled() {
				return mapController.getConceptMap().getComponentManager().getUndoManager().canRedo();
			}            
        };

        editMenu.addTool(undo, 170);
        editMenu.addTool(redo, 180);
        mapController.getConceptMap().getComponentManager().getUndoManager().addUndoListener(this);
        copy = new CopyEditMapTool(mapController, clipboard);
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));
        cut = new CutMapTool(mapController, clipboard);
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK));
        paste = new PasteConceptMapTool(mapController, clipboard);
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK));
        
        editMenu.addSeparator(800);
        editMenu.addTool(copy, 805);
        editMenu.addTool(cut, 807);
        editMenu.addTool(paste, 810);
        
        ConzillaKit.getDefaultKit().extendMenu(editMenu, mapController);
        ContainerManager cm = ConzillaKit.getDefaultKit().getResourceStore().getContainerManager();
        sessionMenu = new SessionChooserMenu(mapController, (RDFContainerManager) cm);
        grid = new GridTool(gridModel);
        line = new LineTool();
        tie = new TieTool();
        save = new SaveTool(mapController);
        publish = new PublishTool(this, mapController, (SaveTool)save);
        contributionInfo = new ContributionInfoTool(mapController);
        fullScreen = new FullScreenTool(mapController);
        create = new CreateTools(this, mapController, gridModel);
        gridLayer = new GridLayer(mapController, gridModel);
        moveLayer = new MoveLayer(mapController, this);
        
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
        view.addMenu(sessionMenu, 35);
    
        //EditMenu
        view.addMenu(editMenu, 15);
        
        //ToolBar
        ToolsBar bar = view.getToolsBar();
        
       
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
        view.removeMenu(editMenu);

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
        log.debug("Got focus");
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

	public void undoStateChanged() {
		UndoManager um = controller.getConceptMap().getComponentManager().getUndoManager();
		undo.setEnabled(um.canUndo());
		redo.setEnabled(um.canRedo());
	}
}
