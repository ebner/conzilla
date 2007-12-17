package se.kth.cid.service;

import java.io.IOException;
import java.net.URI;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.map.MapStoreManager;

public class Service {
	
	private static Object mutex = new Object();
	
	private static ConzillaKit kit; 
	
	public Service() {
		synchronized (mutex) {
			if (kit == null) {
				init();
			}
		}
	}
	
	private void init() {
		try {
			ConzillaKit.createMinimalKit(new ServiceEnvironment());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		kit = ConzillaKit.getDefaultKit();
	}
	
    public MapStoreManager getMapStoreManager(URI uri) throws ComponentException {
		MapStoreManager storeManager = new MapStoreManager(uri, kit.getResourceStore(), kit.getStyleManager(), null);
		Container lc = kit.getResourceStore().getAndReferenceContainer(URI.create(storeManager.getConceptMap().getLoadContainer()));
		for (String containers : lc.getURIsWithRequestedContainers()) {
			for (String container : lc.getRequestedContainersForURI(containers)) {
				try {
					kit.getResourceStore().getAndReferenceContainer(URI.create(container));
				} catch (ComponentException ce) {
					ce.printStackTrace();
				}
			}
		}
		
		return storeManager;
	}
    
    public ConzillaKit getConzillaKit() {
    	return kit;
    }

}