/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import se.kth.cid.collaboration.CollaborillaReader;
import se.kth.cid.collaboration.CollaborillaSupport;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.ComponentManager;
import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.component.EditEvent;
import se.kth.cid.component.EditListener;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.browse.BrowseMapManager;
import se.kth.cid.conzilla.collaboration.ContainerEntries;
import se.kth.cid.conzilla.content.ContentSelector;
import se.kth.cid.conzilla.history.HistoryEvent;
import se.kth.cid.conzilla.history.HistoryListener;
import se.kth.cid.conzilla.history.HistoryManager;
import se.kth.cid.conzilla.history.LinearHistory;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.map.MapStoreManager;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.session.SessionChooserDialog;
import se.kth.cid.conzilla.view.View;
import se.kth.cid.layout.ContextMap;

/**
 * There is a single MapController for every View.
 * @author matthias
 *
 */
public class MapController implements PropertyChangeListener, EditListener {

	public final static String MAP_PROPERTY = "map";
	
	public final static String MAPMANAGER_PROPERTY = "mapmanager_changed";
	
	public final static String MAP_LOADING = "map_loading";
	
	public final static String MAP_LOADING_FAILED = "map_loading_failed";

	HistoryManager historyManager;
	MapManager manager;
	MapStoreManager mapStoreManager;
	ContentSelector selector;
	LinearHistory linearHistory;
	PropertyChangeSupport propSupport;
	CollaborillaReader collaborillaReader;
	ContainerEntries containerEntries;
	View view;
	HashMap stuff = new HashMap();
	private String mmfName;

	public MapController(ContentSelector selector) {
		propSupport = new PropertyChangeSupport(this);
		this.historyManager = new HistoryManager(ConzillaKit.getDefaultKit().getResourceStore());
		linearHistory = new LinearHistory();
		CollaborillaSupport cs = new CollaborillaSupport(ConfigurationManager.getConfiguration());
		collaborillaReader = new CollaborillaReader(cs);
		
		historyManager.addHistoryListener(new HistoryListener() {
			public void historyEvent(HistoryEvent e) {
				switch (e.getType()) {
				case HistoryEvent.MAP:
					linearHistory.historyEvent(e);
				}
			}
		});

		this.selector = selector;
		selector.setController(this);
		containerEntries = new ContainerEntries(this);
	}

	public void setView(View view) {
		this.view = view;
	}
	
	public View getView() {
		return view;
	}
	
	public CollaborillaReader getCollaborillaReader() {
		return collaborillaReader;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l) {
		if (propSupport != null) {
			propSupport.addPropertyChangeListener(l);
		}
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		if (propSupport != null) {
			propSupport.removePropertyChangeListener(l);
		}
	}

	public void changeMapManager(MapManagerFactory managerFactory) {
		ComponentManager cManager = getConceptMap().getComponentManager();
		if (managerFactory.requiresSession() ) {
			if (cManager.getEditingSesssion() == null) {
				ContainerManager cm = ConzillaKit.getDefaultKit().getResourceStore().getContainerManager();
				String uri = getConceptMap().getURI();
				SessionChooserDialog pcd = new SessionChooserDialog(cm);
				Session session = pcd.findSession(uri);
				if (session == null) {
					//Cancel pressed, to not change mapManager.
					return;
				}
				if (session != null) {
					ConzillaKit.getDefaultKit().getSessionManager().setCurrentSession(session);
				}
				cManager.setLockForEditing(this, session);
			}
		} else if (cManager.getEditingSesssion() != null){
			//Release lock when session not needed.
			cManager.setLockForEditing(this, null);
		}
		
		if (this.manager != null) {
			this.manager.deInstall();
			view.getMapScrollPane().getDisplayer().reset();
		}
		this.selector.selectContentFromSet(null);
		this.manager = managerFactory.createManager(this);
		this.manager.install();

		view.embeddMapPanel();
		String oldMMFName = mmfName;
		mmfName = managerFactory.getName();

		firePropertyChange(MAPMANAGER_PROPERTY, oldMMFName, mmfName);
	}

	public void changeSessionTo(Session session) {
		ComponentManager cManager = getConceptMap().getComponentManager();
		cManager.setLockForEditing(this, session);
	}
	
	public HistoryManager getHistoryManager() {
		return historyManager;
	}

	public LinearHistory getLinearHistory() {
		return linearHistory;
	}

	public ContentSelector getContentSelector() {
		return selector;
	}

	public ContainerEntries getContainerEntries() {
		return containerEntries;
	}
	
	public MapManager getManager() {
		return manager;
	}

	public MapStoreManager getMapStoreManager() {
		return mapStoreManager;
	}
	
	public ContextMap getConceptMap() {
		if (view != null
				&& view.getMapScrollPane() != null
				&& view.getMapScrollPane().getDisplayer()!= null
				&& view.getMapScrollPane().getDisplayer().getStoreManager() != null) {
			return view.getMapScrollPane().getDisplayer().getStoreManager()
					.getConceptMap();
		}
		return null;
	}


	public void reload() throws ControllerException {
		URI map = URI.create(getConceptMap().getURI());
		showMap(map);
	}

	public void showMap(final URI mapURI) throws ControllerException {
		showMap(mapURI, false);
	}

	public void showHyperlinkedMap(URI mapURI) throws ControllerException {
		showMap(mapURI, true);
	}
	
	public void refresh() throws ControllerException {
		showMap(URI.create(getConceptMap().getURI()), false);
	}
	
	private Container determineLoadContainer(URI uri) {
		ConzillaKit kit = ConzillaKit.getDefaultKit();
		ResourceStore store = kit.getResourceStore();
		Collection sessions = kit.getSessionManager().getSessions();
		for (Iterator it = sessions.iterator(); it.hasNext(); ) {
			Session session = (Session) it.next();
			Container container;
			try {
				container = store.getAndReferenceContainer(URI.create(session.getContainerURIForLayouts()));
			} catch (ComponentException e) {
				continue;
			}
			if (container != null) {
				List maps = container.getDefinedContextMaps();
				for (Iterator mapIt = maps.iterator(); mapIt.hasNext(); ) {
					String mapURI = (String) mapIt.next();
					if (uri.toString().equals(mapURI)) {
						ContextMap map = null;
						try {
							map = store.getAndReferenceLocalContextMap(URI.create(mapURI), session);
							Container loadContainer = store.getAndReferenceContainer(URI.create(map.getLoadContainer())); 
							return loadContainer;
						} catch (ComponentException e1) {
							return null;
						}
					}
				}
			}
		}
		return null;
	}
	
	private void showMap(URI mapURI, boolean contextLoad) throws ControllerException {
		firePropertyChange(MAP_LOADING, null, null);
		
		ContextMap oldContextMap = getConceptMap();
		MapScrollPane oldPane = view != null ? view.getMapScrollPane() : null;
		
		ConzillaKit kit = ConzillaKit.getDefaultKit();
		try {
			if (contextLoad && view.getMapScrollPane() != null) {
				ResourceStore store = kit.getResourceStore();
				Container lc = store.getAndReferenceContainer(URI.create(getConceptMap().getLoadContainer()));
				mapStoreManager = new MapStoreManager(mapURI, kit.getResourceStore(), kit.getStyleManager(), lc);
			} else {
				try {
					mapStoreManager = new MapStoreManager(mapURI, kit.getResourceStore(), kit.getStyleManager(), null);
				} catch (ComponentException ce) {
					Container lc = determineLoadContainer(mapURI);
					mapStoreManager = new MapStoreManager(mapURI, kit.getResourceStore(), kit.getStyleManager(), lc);
				}
			}
		} catch (ComponentException e) {
			firePropertyChange(MAP_LOADING_FAILED, null, e);
			throw new ControllerException("Unable to load map <" + mapURI + ">.\n" + e.getMessage() +
					"\n\nMake sure you entered a correct URI, that the collaboration settings are\n" +
					"configured properly, and that your Internet connection is working.");
		}
		
		// We have to change to browse mode, otherwise we end
		// up in edit mode without a session being set
		MapController controller = view.getController();
		MapManager manager = controller.getManager();
		if ((manager != null) && !(manager instanceof BrowseMapManager)) {
			controller.changeMapManager(ConzillaKit.getDefaultKit().getBrowseMapManagerFactory());
		}
		
		view.setMap(mapStoreManager);
		if (oldContextMap != null) {
			this.selector.selectContentFromSet(null);
			oldContextMap.removeEditListener(this);
		}
		ContextMap contextMap = getConceptMap();
		contextMap.addEditListener(this);
		if (!contextMap.getComponentManager().isCollaborative()) {
			contextMap.getComponentManager().setCollaborative(true);
		}
		
		if (isMapRemote(mapURI.toString())) {
			containerEntries.update();
		} else {
			containerEntries.clear();
		}
		
		firePropertyChange(MAP_PROPERTY, oldPane, view.getMapScrollPane());
	}
	
	private boolean isMapRemote(String uri) {
		if (uri.toLowerCase().startsWith("conzilla:/")) {
			return false;
		}
		if (uri.toLowerCase().startsWith("urn:path:/org/conzilla/local/")) {
			return false;
		}
		if (uri.toLowerCase().startsWith("urn:path:/org/conzilla/builtin/")) {
			return false;
		}
		return true;
	}

	public void detach() {
		if (getConceptMap() != null) {
			ComponentManager cManager = getConceptMap().getComponentManager();
			if (cManager.getEditingSesssion() != null){
				//Release lock when session not needed.
				cManager.setLockForEditing(this, null);
			}
		}
		
		if (selector != null) {
			selector.selectContentFromSet(null);
		}

		if (manager != null) {
			manager.deInstall();
		}
		
		if (view != null) {
			view.detach();
		}
		
		if (mapStoreManager != null) {
			mapStoreManager.detach();
		}
		
		mapStoreManager = null;
		propSupport = null;
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		propSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ContentSelector.SELECTOR)) {
			if (evt.getNewValue() == null) {
				view.removeFromRight(selector.getComponent());
			} else {
				view.addToRight(selector.getComponent(), "Content", null);
			}
		}
	}

	/**
	 * @see se.kth.cid.component.EditListener#componentEdited(se.kth.cid.component.EditEvent)
	 */
	public void componentEdited(EditEvent e) {
		if (e.getEditType() == se.kth.cid.component.Component.ATTRIBUTES_EDITED) {
			firePropertyChange(MAP_PROPERTY, null, null);
		}
	}
	
	public void put(Object key, Object value) {
		stuff.put(key, value);
	}
	
	public Object get(Object key) {
		return stuff.get(key);
	}
}