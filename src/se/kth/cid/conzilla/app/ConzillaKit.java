/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.app;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.collaboration.MetaDataCache;
import se.kth.cid.collaboration.MetaDataDiskCache;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.component.cache.SoftCache;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.bookmark.BookmarkStore;
import se.kth.cid.conzilla.browse.BrowseMapManagerFactory;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.conzilla.content.ContentDisplayer;
import se.kth.cid.conzilla.content.MultiContentDisplayer;
import se.kth.cid.conzilla.content.OpenMapContentDisplayer;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.menu.MenuFactory;
import se.kth.cid.conzilla.metadata.ConzillaLocaleManager;
import se.kth.cid.conzilla.session.SessionManager;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.identity.MIMEType;
import se.kth.cid.rdf.RDFComponentFactory;
import se.kth.cid.rdf.RDFContainerManager;
import se.kth.cid.style.StyleManager;
import se.kth.cid.tree.TreeTagNodeResource;
import se.kth.nada.kmr.shame.applications.util.FormletStoreSingleton;
import se.kth.nada.kmr.shame.formlet.FormletStore;

/** A kit of resources for the conzilla environment. */
public class ConzillaKit {

	Conzilla conzilla;

	ConzillaEnvironment environment;

	ResourceStore store;

	ContentDisplayer contentDisplayer;

	ConzillaLocaleManager localeManager;

	TreeTagNodeResource rootLibrary;

	StyleManager styleManager;

	//AgentManager agentManager;

	FormletStore formletStore;
	
	BookmarkStore bookmarkStore;
	
	SessionManager sessionManager;

	Hashtable<String, Extra> extras;

	MenuFactory menuFactory;
	
	MetaDataCache metaCache;
	
	Log log = LogFactory.getLog(ConzillaKit.class);

	static ConzillaKit defaultKit;

	public static ConzillaKit getDefaultKit() {
		return defaultKit;
	}
	
	public static void createMinimalKit(ConzillaEnvironment env) throws IOException {
		defaultKit = new ConzillaKit(env);
		defaultKit.initMinimalKit();
	}
	
	private void initMinimalKit() throws IOException {
		initFormletStore();
		extras = new Hashtable<String, Extra>();
		localeManager = new ConzillaLocaleManager();
		SoftCache cache = new SoftCache();
		metaCache = new MetaDataDiskCache();
		RDFContainerManager containerManager = new RDFContainerManager();
		store = new ResourceStore(cache, metaCache, new RDFComponentFactory(cache, containerManager));
		styleManager = new se.kth.cid.rdf.style.RDFStyleManager(store);
	}
	
	public static void createFullKit(ConzillaEnvironment env) throws IOException {
		createMinimalKit(env);
		defaultKit.initFullKit();
	}
	
	private void initFullKit() throws IOException {
		//agentManager = new AgentManager();
		bookmarkStore = new BookmarkStore("bookmarks.xml");

		// Add other content displayers here.
		MultiContentDisplayer md = new MultiContentDisplayer();
		contentDisplayer = md;

		md.addContentDisplayer(null, environment.getDefaultContentDisplayer());
		//md.addContentDisplayer(MIMEType.CONCEPTMAP, new FrameMapContentDisplayer());
		md.addContentDisplayer(MIMEType.CONCEPTMAP, new OpenMapContentDisplayer());

		conzilla = new Conzilla();
		conzilla.initConzilla(this);

		loadMenuFactory();
		// metaDataDisplayer = new FrameMetaDataDisplayer();
		// ((FrameMetaDataDisplayer) metaData).setLocation(300, 300);

		// conceptDisplayer = new FrameConceptDisplayer(this);

		// fixLibrary();
		loadExtras();
	}

	private ConzillaKit(ConzillaEnvironment env) throws IOException {
		this.environment = env;
	}

	void initFormletStore() {
		//formletStore = FormletStore.getInstance();
		formletStore = FormletStoreSingleton.getInstance();
	}

	void loadExtras() {
		List extras = ConfigurationManager.getConfiguration().getStringList(Settings.CONZILLA_EXTRAS, new ArrayList());
		Iterator extraIt = extras.iterator();

		while (extraIt.hasNext()) {
			String extra = (String)extraIt.next();

			if (extra == null) {
				log.warn("Extra is invalid: " + extra);
				continue;
			}
			Extra nextra;
			try {
				nextra = (Extra) Class.forName(extra).newInstance();
				registerExtra(nextra);
			} catch (ClassCastException e) {
				log.warn("This is not an Extra: " + extra, e);
			} catch (ClassNotFoundException e) {
				log.warn("Could not find Extra: " + extra, e);
			} catch (InstantiationException e) {
				log.warn("Could not instantiate Extra: " + extra, e);
			} catch (IllegalAccessException e) {
				log.warn("Could not instantiate Extra (illegal access): " + extra, e);
			}
		}
	}

	void loadMenuFactory() throws IOException {
		String menumanager = ConfigurationManager.getConfiguration().getString(Settings.CONZILLA_MENUFACTORY);
		if (menumanager == null || menumanager.length() == 0) {
			return;
		}

		try {
			menuFactory = (MenuFactory) Class.forName(menumanager).newInstance();
			menuFactory.initFactory(this);
		} catch (ClassNotFoundException e) {
			throw new IOException("Could not find MenuFactory: " + menumanager);
		} catch (InstantiationException e) {
			throw new IOException("Could not make MenuFactory: " + menumanager + "\n " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new IOException("Could not make MenuFactory: " + menumanager + "\n " + e.getMessage());
		} catch (ClassCastException e) {
			throw new IOException("Could not make MenuFactory: " + menumanager + "\n " + e.getMessage());
		}
	}

	public void registerExtra(Extra extra) {
		if (extra.initExtra(this)) {
			extras.put(extra.getName(), extra);
		} else {
			log.warn("Extra was not initialized: " + extra.getClass().getName());
		}
	}

	public Enumeration getExtras() {
		return extras.elements();
	}
	
	public Enumeration getExtraNames() {
		return extras.keys();
	}
	
	public BrowseMapManagerFactory getBrowseMapManagerFactory() {
		return (BrowseMapManagerFactory) extras.get("BrowseMapManagerFactory");
	}
	
	public Extra getExtra(String name) {
		return (Extra) extras.get(name);
	}

	public void extendMenu(ToolsMenu menu, MapController mc) {
		Enumeration en = getExtras();
		while (en.hasMoreElements())
			((Extra) en.nextElement()).extendMenu(menu, mc);
	}

	public void extend(MapController mc) {
		Enumeration en = getExtras();
		while (en.hasMoreElements())
			((Extra) en.nextElement()).addExtraFeatures(mc);
	}

	public Conzilla getConzilla() {
		return conzilla;
	}

	public ConzillaEnvironment getConzillaEnvironment() {
		return environment;
	}

	public ResourceStore getResourceStore() {
		return store;
	}
	
	public BookmarkStore getBookmarkStore() {
		return bookmarkStore;
	}
	
	public MetaDataCache getMetaDataCache() {
		return metaCache;
	}

	public ContentDisplayer getContentDisplayer() {
		return contentDisplayer;
	}

	public MenuFactory getMenuFactory() {
		return menuFactory;
	}

	/*
	 * public ComponentEdit getComponentEdit() { return componentEdit; }
	 */
	/*
	 * public FrameMetaDataDisplayer getMetaDataDisplayer() { return
	 * metaDataDisplayer; }
	 */
	/*
	 * public FilterFactory getFilterFactory() { return filterFactory; }
	 */

	/*
	 * public ResolverEdit getResolverEditor() { if(resolverEdit == null)
	 * resolverEdit = new ResolverEdit(environment.getResolverManager());
	 * 
	 * return resolverEdit; }
	 */
	public TreeTagNodeResource getRootLibrary() {
		return rootLibrary;
	}

	public void fetchRootLibrary(URI libraryURI) {
		try {
			TreeTagNodeResource library = store.getComponentManager().loadTree(libraryURI);
			if (library == null)
				log.debug("Root library not loaded...");
			else {
				log.debug("Root library loaded...");
				rootLibrary = library;
			}
		} catch (ComponentException ce) {
			log.debug("Failed to load the root library", ce);
		}
	}

	public StyleManager getStyleManager() {
		return styleManager;
	}

//	public AgentManager getAgentManager() {
//		return agentManager;
//	}

	public FormletStore getFormletStore() {
		return formletStore;
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
}
