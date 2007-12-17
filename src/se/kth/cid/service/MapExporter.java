package se.kth.cid.service;

import java.net.URI;

import org.json.JSONException;

import se.kth.cid.component.ComponentException;
import se.kth.cid.conzilla.map.MapStoreManager;
import se.kth.cid.json.Export;

public class MapExporter {
	
	private Service service;
	
	public MapExporter(Service service) {
		this.service = service;
	}
    
    public String getContextMapAsJSON(URI uri) throws ComponentException, JSONException {
    	MapStoreManager storeManager = service.getMapStoreManager(uri);
    	Export export = new Export(storeManager.getConceptMap(), storeManager.getConcepts());
    	return export.toString();
    }

}