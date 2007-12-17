
package se.kth.cid.json;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

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

public class Export {
	ContextMap map;
	private Collection concepts;
	String titleStr;
	String conceptsStr;
	String layoutsStr;
	String layersStr;
	public Export(ContextMap cMap, Collection concepts) {
		this.map = cMap;
		this.concepts = concepts;
		titleStr = "label: "+extractTitle(this.map);
		extractConcepts();
		extractLayersAndLayouts();
	}
	
	private String extractTitle(Component comp) {
		StringBuffer buf = new StringBuffer();
		buf.append("{");
		List atributes = comp.getAttributeEntry(CV.title);
        if (!atributes.isEmpty()) {
           for (Iterator attrs = atributes.iterator(); attrs.hasNext();) {
               AttributeEntry ae = (AttributeEntry) attrs.next();
               if (ae instanceof RDFAttributeEntry) {
                   RDFAttributeEntry rae = (RDFAttributeEntry) ae;
                   RDFNode node = rae.getObject();
                   if (node instanceof Literal) {
                       String actualLanguage = ((Literal) node).getLanguage();
                       boolean aLNull = actualLanguage == null || actualLanguage.length() == 0;
                       if (aLNull) {
                    	   buf.append("none: '"+ae.getValue()+"',");
                       } else
                    	   buf.append(actualLanguage+": '"+ae.getValue()+"',");
                       }
                   }
               }
           }
        if (buf.length() != 1) {
        	buf.deleteCharAt(buf.length()-1);
        }
        buf.append("}");
        return buf.toString();
	}
	
	private void extractConcepts() {
		StringBuffer buf = new StringBuffer();
		buf.append("concepts: {");
		Iterator it = concepts.iterator();
		while (it.hasNext()) {
			Concept c = (Concept) it.next();
			String md5 = "c"+Hashing.md5(c.getURI());
			buf.append(md5+": {label: "+extractTitle(c)+", cls: '"+getType(c)+"'},");
		}
        buf.deleteCharAt(buf.length()-1);
        buf.append("}");
        conceptsStr = buf.toString();
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

	private void extractLayersAndLayouts() {
		StringBuffer buf = new StringBuffer();
		buf.append("layers: {");
		StringBuffer buf2 = new StringBuffer();
		buf2.append("layouts: [");
		LayerManager lMan = map.getLayerManager();
		Iterator it = lMan.getLayers().iterator();
		while (it.hasNext()) {
			LayerLayout layer = (LayerLayout) it.next();
			String md5 = "l"+Hashing.md5(layer.getURI());
			buf.append(md5+": "+"{label: "+extractTitle(layer)+"},");
			//do the layouts
			Enumeration en = layer.children();
			while (en.hasMoreElements()) {
				DrawerLayout dl = (DrawerLayout) en.nextElement();
				String ref = "c"+Hashing.md5(dl.getConceptURI());
				String graphics = null;
				if (dl instanceof StatementLayout) {
					if (dl.getBodyVisible()) {
						graphics = "bounds: "+getBounds(dl)+", path: '"+getPath((StatementLayout) dl)+"'";						
					} else {
						graphics = "path: '"+getPath((StatementLayout) dl)+"'";
					}
				} else {
					graphics = "bounds: "+getBounds(dl);
				}
				buf2.append("{ref: '"+ref+"', layer: '"+md5+"', "+graphics+"},");
			}
			
		}
        buf.deleteCharAt(buf.length()-1);
        buf.append("}");
        buf2.deleteCharAt(buf2.length()-1);
        buf2.append("]");
		layersStr = buf.toString();
		layoutsStr = buf2.toString();
	}
	
	private String getPath(StatementLayout sl) {
		ContextMap.Position [] points = sl.getLine();
		StringBuffer buf = new StringBuffer();
		buf.append("M"+points[0].x+ " "+points[0].y);
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

	private String getBounds(DrawerLayout dl) {
		ContextMap.BoundingBox bb = dl.getBoundingBox();
		return "["+bb.pos.x+","+bb.pos.y+","+bb.dim.width+","+bb.dim.height+"]";
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("{"+titleStr+",");
		buf.append(conceptsStr+",");
		buf.append(layoutsStr+",");
		buf.append("metadata: {},");
		buf.append(layersStr+"}");
		return buf.toString().replaceAll("\\n", "\\\\n");
	}

}