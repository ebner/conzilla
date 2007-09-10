/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.manage;

import java.awt.Color;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.util.AttributeEntryUtil;

public class ConceptTable extends AbstractTableModel {
	

	public String[] conceptColumnToolTips = {
		    "Marked concepts occur in selected map",
			"Fix, the number of non marked refering context-maps in this session",
		    "Ignore, the number of referring context-maps outside of this session",
		    "Total, the number of referring context-maps, i.e. contextual neighbourhoods",
		    "Weather to move this concept/relation or not",
		    "C = Concept, R = Relation",
		    "The name of the concept/relation"};
	
	ResourceStore store;
	Container iContainer;
	ArrayList mapUris = new ArrayList();
	HashMap maps = new HashMap();
	HashMap map2clist = new HashMap();
	ArrayList concepts = new ArrayList();
	HashMap c2Title = new HashMap();
	HashMap c2Boolean = new HashMap();
	HashMap c2CN = new HashMap();
	HashMap c2CNInSessionNotMarked = new HashMap();
	HashMap c2CNNotInSession = new HashMap();
	URI marked;
	Color markedC = Color.BLUE;
	Color unmarkedC;
	
	public ConceptTable() {
		store = ConzillaKit.getDefaultKit().getResourceStore();
	}
	
	public void setSession(Session s) {
		try {
			iContainer = store.getAndReferenceContainer(
					new URI(s.getContainerURIForConcepts()));
		} catch (ComponentException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void addContextMap(URI uri) {
		if (mapUris.contains(uri)) {
			return;
		}
		
		ContextMap cMap = (ContextMap) maps.get(uri);
		if (cMap == null) {
			try {
				cMap = store.getAndReferenceConceptMap(uri);
			} catch (ComponentException e) {
				return;
			}
			maps.put(uri, cMap);
		}

		mapUris.add(uri);
		
		ArrayList clist = new ArrayList();
		DrawerLayout[] dls = cMap.getDrawerLayouts();
		for (int i = 0; i < dls.length; i++) {
			try {
				Concept c = store.getAndReferenceConcept(new URI(dls[i].getConceptURI()));
				clist.add(c);
				setTitle(c);
				setCN(c);
				setBoolean(c);
			} catch (ComponentException e) {
			} catch (URISyntaxException e) {
			}
		}
		map2clist.put(cMap,clist);
		update();
		fireTableDataChanged();
	}

	private void setBoolean(Concept c) {
		if (c.getLoadContainer().equals(iContainer.getURI())) {
			c2Boolean.put(c, new Boolean(true));
		} else {
			c2Boolean.put(c, new Boolean(false));
		}
	}
	
	private void setCN(Concept c) {
        Set neighbourHoodMapsNotInSession = new HashSet();
        Set neighbourHoodMapsInSession = new HashSet();
        ContainerManager containerManager = ConzillaKit.getDefaultKit().getResourceStore().getContainerManager();
        for (Iterator containers = containerManager.getContainers(Container.COMMON).iterator(); containers.hasNext();) {
        	Container container = (Container) containers.next();
        	if (container == iContainer) {
        		neighbourHoodMapsInSession.addAll(container.getMapsReferencingResource(c.getURI()));
        	} else {
        		neighbourHoodMapsNotInSession.addAll(container.getMapsReferencingResource(c.getURI()));
        	}
        }
        
        Set CN = new HashSet();
        CN.addAll(neighbourHoodMapsInSession);
        CN.addAll(neighbourHoodMapsNotInSession);
        c2CN.put(c, CN);
        c2CNNotInSession.put(c, neighbourHoodMapsNotInSession);
        
        int missing = neighbourHoodMapsInSession.size();
        for (Iterator muris = mapUris.iterator(); muris
				.hasNext();) {
			String uri = ((URI) muris.next()).toString();
			if (neighbourHoodMapsInSession.contains(uri)) {
				missing--;
			}
		}
        c2CNInSessionNotMarked.put(c, new Integer(missing));        
	}
	
	private void setTitle(Concept c) {
		if (c2Title.get(c) == null) {
			if (c.getTriple() != null) {
				String title = AttributeEntryUtil.getTitleAsString(c);
				if (title == null || title.length() == 0) {
					title = c.getTriple().predicateURI();
					int hcut = title.lastIndexOf('#');
					int scut = title.lastIndexOf('/');
					int cut = hcut > scut ? hcut : scut;
					title = title.substring(cut);
				}
				c2Title.put(c, title);			
			} else {
				c2Title.put(c, AttributeEntryUtil.getTitleAsString(c));
			}
		}
	}
	
	public void removeContextMap(URI uri) {
		if (mapUris.contains(uri)) {
			mapUris.remove(uri);
			update();
			Iterator it = ((List) map2clist.get(maps.get(uri))).iterator();
			while (it.hasNext()) {
				Concept c = (Concept) it.next();
				if (concepts.contains(c)) {
					Integer i = (Integer) c2CNInSessionNotMarked.get(c);
					i = new Integer(i.intValue() + 1);
					c2CNInSessionNotMarked.put(c, i);
				}
			}
			fireTableDataChanged();
		}
	}
	
	private void update() {
		concepts = new ArrayList();
		for (Iterator uris = mapUris.iterator(); uris.hasNext();) {
			URI uri = (URI) uris.next();
			ContextMap cMap = (ContextMap) maps.get(uri);
			List clist = (List) map2clist.get(cMap);
			for (Iterator cs = clist.iterator(); cs.hasNext();) {
				Concept c = (Concept) cs.next();
				if (!concepts.contains(c)) {
					concepts.add(c);
				}
			}
		}		
	}
	
	public Class getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case 0:
				return Color.class;
			case 1:
			case 2:
			case 3:
				return Integer.class;
			case 4:
				return Boolean.class;
			case 5:
			case 6:
				return String.class;
		}
		return null;
	}

	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "S";
		case 1:
			return "F";//Fix
		case 2:
			return "I"; //Ignore
		case 3:
			return "T";//Total
		case 4:
			return "M";
		case 5:
			return "R";
		case 6:
			return "Concept Label";
		}
		return null;
	}
	
	public int getColumnCount() {
		return 7;
	}

	public int getRowCount() {
		return concepts.size();
	}
	public Concept getConcept(int rowIndex) {
		return (Concept) concepts.get(rowIndex);
	}
	public Set getCN(int rowIndex) {
		Concept concept = (Concept) concepts.get(rowIndex);
		return (Set) c2CN.get(concept);
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		Concept concept = (Concept) concepts.get(rowIndex);
		switch (columnIndex) {
			case 0:
			if ( marked != null && mapUris.contains(marked)
					&& ((List) map2clist.get(maps.get(marked))).contains(concept)) {
				return markedC;
			} else {
				return unmarkedC;
			}
			case 1:
				return c2CNInSessionNotMarked.get(concept);
			case 2:
				return new Integer(((Set) c2CNNotInSession.get(concept)).size());
			case 3:
				return new Integer(((Set) c2CN.get(concept)).size());
			case 4:
				return c2Boolean.get(concept);
			case 5:
				return concept.getTriple() == null ? "C" : "R";
			case 6:
				return c2Title.get(concept);
		}
		return null;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Concept concept = (Concept) concepts.get(rowIndex);
		c2Boolean.put(concept, aValue);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		Concept concept = (Concept) concepts.get(rowIndex);
		return columnIndex == 4 && concept.getLoadContainer().equals(iContainer.getURI());
	}
	
	public void setMarkedMap(URI mapURI) {
		marked = mapURI;
		fireTableChanged(new TableModelEvent(this, 0, getRowCount(), 6));
	}
	
	public void clearMark() {
		marked = null;
		fireTableChanged(new TableModelEvent(this, 0, getRowCount(), 6));
	}
}