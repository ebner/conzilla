package se.kth.cid.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.component.Component;
import se.kth.cid.concept.Concept;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.LayerLayout;
import se.kth.cid.layout.LayerManager;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.rdf.CV;
import se.kth.cid.rdf.RDFAttributeEntry;
import se.kth.cid.style.LineStyle;
import se.kth.cid.util.Hashing;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class ExportNew {
	
	private ContextMap map;
	
	private Collection concepts;
	
	JSONObject json;
	
	public ExportNew(ContextMap map, Collection concepts) throws JSONException {
		this.map = map;
		this.concepts = concepts;
		json = new JSONObject();
		json.put("label", extractTitles(map));
		json.put("concepts", extractConcepts());
		json.put("layers", extractLayers());
		json.put("layouts", extractLayouts());
		json.put("metadata", new JSONObject());
	}
	
	private JSONObject extractTitles(Component comp) throws JSONException {
		JSONObject titles = new JSONObject();
		List attributes = comp.getAttributeEntry(CV.title);
		if (!attributes.isEmpty()) {
			for (Iterator attrs = attributes.iterator(); attrs.hasNext();) {
				AttributeEntry ae = (AttributeEntry) attrs.next();
				if (ae instanceof RDFAttributeEntry) {
					RDFAttributeEntry rae = (RDFAttributeEntry) ae;
					RDFNode node = rae.getObject();
					if (node instanceof Literal) {
						String actualLanguage = ((Literal) node).getLanguage();
						boolean aLNull = actualLanguage == null || actualLanguage.length() == 0;
						if (aLNull) {
							titles.put("none", ae.getValue());
						} else {
							titles.put(actualLanguage, ae.getValue());
						}
					}
				}
			}
		}
        return titles;
	}
	
	private JSONObject extractConcepts() throws JSONException {
		JSONObject jsonConcepts = new JSONObject();
		Iterator it = concepts.iterator();
		while (it.hasNext()) {
			Concept c = (Concept) it.next();
			JSONObject jsonC = new JSONObject();
			jsonC.put("label", extractTitles(c));
			jsonC.put("cls", getType(c));
			String md5 = "c" + Hashing.md5(c.getURI());
			jsonConcepts.put(md5, jsonC);
		}
		
        return jsonConcepts;
	}
	
	private String getType(Concept c) {
		String type = null;
		if (c.getTriple() == null) {
			type = c.getType();
		} else {
			type = c.getTriple().predicateURI();			
		}
		int cut = type.lastIndexOf('#');
		if (cut == -1) {
			cut = type.lastIndexOf('/');
		}
		return type.substring(cut+1).toLowerCase();
	}
	
	private JSONObject extractLayers() throws JSONException {
		JSONObject layers = new JSONObject();
		LayerManager lMan = map.getLayerManager();
		Iterator it = lMan.getLayers().iterator();
		while (it.hasNext()) {
			LayerLayout layer = (LayerLayout) it.next();
			String md5 = "l" + Hashing.md5(layer.getURI());
			JSONObject jsonLayer = new JSONObject();
			jsonLayer.put("label", extractTitles(layer));
			layers.put(md5, jsonLayer);
		}
		return layers;
	}
	
	private List<JSONObject> extractLayouts() throws JSONException {
		List<JSONObject> layouts = new ArrayList<JSONObject>();
		LayerManager lMan = map.getLayerManager();
		Iterator it = lMan.getLayers().iterator();
		while (it.hasNext()) {
			LayerLayout layer = (LayerLayout) it.next();
			Enumeration en = layer.children();
			String md5 = "l" + Hashing.md5(layer.getURI());
			JSONObject layoutInfo = new JSONObject();
			while (en.hasMoreElements()) {
				DrawerLayout dl = (DrawerLayout) en.nextElement();
				String ref = "c" + Hashing.md5(dl.getConceptURI());
				if (dl instanceof StatementLayout) {
					if (dl.getBodyVisible()) {
						layoutInfo.put("bounds", getBounds(dl));
						layoutInfo.put("path", getPath((StatementLayout) dl));
					} else {
						layoutInfo.put("path", getPath((StatementLayout) dl));
					}
				} else {
					layoutInfo.put("bounds", getBounds(dl));
				}
				layoutInfo.put("ref", ref);
				layoutInfo.put("layer", md5);
			}
			layouts.add(layoutInfo);
		}
		return layouts;
	}	
	
	private String getPath(StatementLayout sl) {
		ContextMap.Position [] points = sl.getLine();
		StringBuffer buf = new StringBuffer();
		buf.append("M" + points[0].x + " " + points[0].y);
		if (sl.getPathType() == LineStyle.PATH_TYPE_CURVE) {
			for (int i = 1; i < points.length; i+=3) {
				buf.append(" C"+points[i].x+ " "+points[i].y);
				buf.append(" "+points[i+1].x+ " "+points[i+1].y);
				buf.append(" "+points[i+2].x+ " "+points[i+2].y);
			}
		} else {
			for (int i = 1; i < points.length; i++) {
				buf.append(" L"+points[i].x+ " "+points[i].y);
			}
		}
		return buf.toString();
	}

	private List<Integer> getBounds(DrawerLayout dl) {
		ContextMap.BoundingBox bb = dl.getBoundingBox();
		List<Integer> bounds = new ArrayList<Integer>();
		bounds.add(bb.pos.x);
		bounds.add(bb.pos.y);
		bounds.add(bb.dim.width);
		bounds.add(bb.dim.height);
		
		return bounds;
	}
	
	public String toString() {
		return json.toString();
	}

}