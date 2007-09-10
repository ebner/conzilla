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
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.util.AttributeEntryUtil;

public class MapTable extends AbstractTableModel {
	
	public String[] fromMapColumnToolTips = {
		    "Weather to move this context-map or not",
		    "Name of the context-map",
		    "Maps referring the selected concepts"};
	
	protected String[] toMapColumnToolTips = {
		    fromMapColumnToolTips[1],
		    fromMapColumnToolTips[2]};

	boolean check;
	ArrayList maps = new ArrayList();
	ArrayList mapTitles;
	ArrayList marked = new ArrayList();
	ArrayList selected;
	Container container;
	Color markedC;
	Color markedCDarker;
	
	public MapTable(boolean check) {
		this.check = check;
		markedC = Color.RED;
		markedCDarker = Color.RED.darker();
	}

	public Class getColumnClass(int columnIndex) {
		int col = getCorrectColumnIndex(columnIndex);
		switch (col) {
		case 0:
			return Boolean.class;
		case 1:
			return String.class;
		case 2:
			return Color.class;
		}
		return null;
	}

	public String getColumnName(int column) {
		int col = getCorrectColumnIndex(column);
		switch (col) {
		case 0:
			return "M";
		case 1:
			return "Context-map Label";
		case 2:
			return "S";
		}
		return null;
	}

	public boolean setSession(Session session) {
		ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
		try {
			URI presURI = new URI(session.getContainerURIForLayouts());
			container = store.getAndReferenceContainer(presURI);
		} catch (URISyntaxException e) {
			return false;
		} catch (ComponentException e) {
			return false;
		}
		maps = new ArrayList();
		mapTitles = new ArrayList();
		selected = new ArrayList();
		marked = new ArrayList();
		//Should be done differnently with Collaborilla?
		Iterator it = container.getDefinedContextMaps().iterator();
		while (it.hasNext()) {
			try {
				URI mapURI = new URI((String) it.next());
				maps.add(mapURI);
				ContextMap cMap = store.getAndReferenceConceptMap(mapURI);
				mapTitles.add(AttributeEntryUtil.getTitleAsString(cMap));
				selected.add(new Boolean(false));
				marked.add(null);
			} catch (URISyntaxException e) {
			} catch (ComponentException e) {
			}
		}
		fireTableDataChanged();
		return true;
	}
	
	public URI getMapURIAtRow(int rowNumber) {
		return (URI) maps.get(rowNumber);
	}
	
	public void markMap(URI map) {
		int index = maps.indexOf(map);
		if (index != -1) {
			if (check && !((Boolean) selected.get(index)).booleanValue()) {
				marked.set(index, markedCDarker);
			} else {
				marked.set(index, markedC);
			}
			fireTableCellUpdated(index, check ? 2 : 1);
		}
	}
	
	public void clearMark() {
		for (int i = 0; i<marked.size(); i++) {
			if (marked.get(i) != null) {
				marked.set(i, null);
				fireTableCellUpdated(i, check ? 2 : 1);
			}
		}
	}
	
	public int getColumnCount() {
		return check ? 3 : 2;
	}

	public int getRowCount() {
		return maps.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		int col = getCorrectColumnIndex(columnIndex);
		switch (col) {
		case 0:
			return selected.get(rowIndex); 
		case 1:
			return mapTitles.get(rowIndex);
		case 2:
			return marked.get(rowIndex);
		}
		return null;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		selected.set(rowIndex, aValue);
		if (marked.get(rowIndex) != null) {
			if (((Boolean) aValue).booleanValue()) {
				marked.set(rowIndex, markedC);
			} else {
				marked.set(rowIndex, markedCDarker);
			}
			fireTableCellUpdated(rowIndex, check ? 2 : 1);
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return check && columnIndex == 0;
	}
	
	private int getCorrectColumnIndex(int columnIndex) {
		if (check) {
			return columnIndex;
		} else {
			return columnIndex+1;
		}
	}
}