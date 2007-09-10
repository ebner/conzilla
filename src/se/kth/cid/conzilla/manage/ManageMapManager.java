/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.manage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.controller.MapManager;
import se.kth.cid.conzilla.map.MapDisplayer;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.map.graphics.Mark;
import se.kth.cid.conzilla.properties.ColorTheme.Colors;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.session.SessionManager;

public class ManageMapManager implements MapManager {

	MapController controller;
	JPanel panel;
	JPanel mapPane;
	MapTable fromTable;
	MapTable toTable;
	SessionManager sessionManager;
	JComboBox fromSessions;
	JComboBox toSessions;
	private JPanel control;
	Mark overMark;


	private class ColumnTable extends JTable {
		String [] columnToolTip;
		
		public ColumnTable(TableModel tb, String [] cTT) {
			super(tb);
			columnToolTip = cTT;
		}
		
		//Implement table header tool tips.
	    protected JTableHeader createDefaultTableHeader() {
	        return new JTableHeader(columnModel) {
	            public String getToolTipText(MouseEvent e) {
//	                String tip = null;
	                Point p = e.getPoint();
	                int index = columnModel.getColumnIndexAtX(p.x);
	                int realIndex = 
	                        columnModel.getColumn(index).getModelIndex();
	                return columnToolTip[realIndex];
	            }
	        };
	    }		
	}
	
	private class MapTableListener implements ListSelectionListener {
		private MapTable mt;
		private ListSelectionModel lsm;
		private ConceptTable ct;
		
		public MapTableListener(MapTable mapTable, ConceptTable conceptT, JTable table1, JTable table2) {
			table1.getSelectionModel().addListSelectionListener(this);
			this.mt = mapTable;
			this.ct = conceptT;
			this.lsm = table2.getSelectionModel();
		}
		
		public void valueChanged(ListSelectionEvent e) {
	        if (e.getValueIsAdjusting()) {
	        	return;
	        }

	        ListSelectionModel lsmodel =
	            (ListSelectionModel)e.getSource();
	        if (!lsmodel.isSelectionEmpty()) {
	            int selectedRow = lsmodel.getMinSelectionIndex();
	            try {
	            	lsm.clearSelection();
					controller.showMap(mt.getMapURIAtRow(selectedRow));
		            ct.setMarkedMap(fromTable.getMapURIAtRow(selectedRow));
				} catch (ControllerException e1) {
					e1.printStackTrace();
				}
	        }
		}			
	}
 
	
	public ManageMapManager(MapController mapController, SessionManager sessionManager) {
		this.controller = mapController;
		this.sessionManager = sessionManager;
		buildManager();
	}
	
	private void buildManager() {
		if (panel != null) {
			return;
		}
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JPanel inner = new JPanel();
		inner.setLayout(new GridLayout(2,2));
		
		final ConceptTable conceptTable = new ConceptTable();
		JTable cTJTable = new ColumnTable(conceptTable, conceptTable.conceptColumnToolTips);
		JScrollPane conceptScrollTable = new JScrollPane(cTJTable);
		conceptScrollTable.setBorder(BorderFactory.createTitledBorder("2. Select concepts to move"));
		cTJTable.getColumnModel().getColumn(0).setMaxWidth(15);
		cTJTable.getColumnModel().getColumn(1).setMaxWidth(15);
		cTJTable.getColumnModel().getColumn(2).setMaxWidth(15);
		cTJTable.getColumnModel().getColumn(3).setMaxWidth(15);
		cTJTable.getColumnModel().getColumn(4).setMaxWidth(15);
		cTJTable.getColumnModel().getColumn(5).setMaxWidth(15);
		cTJTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cTJTable.setSelectionBackground(Color.RED);
		cTJTable.setDefaultRenderer(Integer.class, new IntegerRenderer(Color.RED.darker()));
		cTJTable.setDefaultRenderer(Color.class, new ColorRenderer());
		
		overMark = new Mark(Colors.CONCEPT_FOCUS, null, null);
		overMark.setLineWidth((float) 3.5);
		
		fromTable = new MapTable(true) {
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				super.setValueAt(aValue, rowIndex, columnIndex);
				if (((Boolean) aValue).booleanValue()) {
					conceptTable.addContextMap(fromTable.getMapURIAtRow(rowIndex));
				} else {
					conceptTable.removeContextMap(fromTable.getMapURIAtRow(rowIndex));
				}
				fromTable.clearMark();
				toTable.clearMark();
			}
		};
		JTable fromJTable = new ColumnTable(fromTable, fromTable.fromMapColumnToolTips);
		fromJTable.setDefaultRenderer(Color.class, new ColorRenderer());
		toTable = new MapTable(false);
		JTable toJTable = new ColumnTable(toTable, toTable.toMapColumnToolTips);
		toJTable.setDefaultRenderer(Color.class, new ColorRenderer());
		
		fromJTable.setSelectionBackground(Color.BLUE);
		toJTable.setSelectionBackground(Color.BLUE);
		fromJTable.getColumnModel().getColumn(0).setMaxWidth(15);
		fromJTable.getColumnModel().getColumn(2).setMaxWidth(15);
		toJTable.getColumnModel().getColumn(1).setMaxWidth(15);
		

		JScrollPane fromScrollTable = new JScrollPane(fromJTable);
		fromScrollTable.setBorder(BorderFactory.createTitledBorder("1. Select context-maps to move"));
		fromJTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		new MapTableListener(fromTable, conceptTable, fromJTable, toJTable);
				
		JScrollPane toScrollTable = new JScrollPane(toJTable);
		toScrollTable.setBorder(BorderFactory.createTitledBorder("Maps already in session"));
		toJTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		new MapTableListener(toTable, conceptTable, toJTable, fromJTable);
		
		inner.add(fromScrollTable);
		inner.add(toScrollTable);
		inner.add(conceptScrollTable);
		mapPane = new JPanel();
		mapPane.setLayout(new BorderLayout());
		inner.add(mapPane);
		
		fromSessions = new JComboBox(new Vector(sessionManager.getSessions()));
		fromSessions.setSelectedItem(null);
		fromSessions.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Session s = (Session) e.getItem();
					fromTable.setSession(s);
					conceptTable.setSession(s);
				}
			}
		});
		toSessions = new JComboBox(new Vector(sessionManager.getSessions()));
		toSessions.setSelectedItem(null);
		toSessions.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					toTable.setSession((Session) e.getItem());
				}
			}
		});
		
		addConceptListeners(cTJTable, conceptTable, fromTable, toTable);
		
		control = new JPanel();
		control.setLayout(new GridLayout(1,2));
		JPanel pair1 = new JPanel();
		pair1.setLayout(new FlowLayout());
		JPanel pair2 = new JPanel();
		pair2.setLayout(new FlowLayout());
		pair1.add(new JLabel("Select session to move from: "));
		pair2.add(new JLabel("Select session to move to: "));
		pair1.add(fromSessions);
		pair2.add(toSessions);
		control.add(pair1);
		control.add(pair2);
		panel.add(control);
		panel.add(inner);
	}
	
	private void addConceptListeners(JTable cTJTable,final ConceptTable conceptTable, final MapTable fromMapTable, final MapTable toMapTable) {
		cTJTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			private Set concepts = new HashSet();

			public void valueChanged(ListSelectionEvent e) {
				 if (e.getValueIsAdjusting()) {
			        	return;
			        }

			        ListSelectionModel lsmodel =
			            (ListSelectionModel)e.getSource();
			        if (!lsmodel.isSelectionEmpty()) {
			            int selectedRow = lsmodel.getMinSelectionIndex();
			            MapDisplayer mapDisplayer = controller.getView().getMapScrollPane().getDisplayer();
			            mapDisplayer.popMark(concepts, this);
			            concepts.clear();
			            concepts.add(conceptTable.getConcept(selectedRow).getURI());
			            mapDisplayer.pushMark(concepts, overMark, this);
			            fromMapTable.clearMark();
			            toMapTable.clearMark();
			            Iterator it = conceptTable.getCN(selectedRow).iterator();
			            while (it.hasNext()) {
							String uri = (String) it.next();
							try {
								URI URI = new URI(uri);
								fromMapTable.markMap(URI);
								toMapTable.markMap(URI);
							} catch (URISyntaxException e1) {
							}
						}
			        }
			}	
		});
	}
	
	public void deInstall() {
		controller.getView().getToolsBar().revalidate();
	}

	public void gotFocus() {
	}

	public void install() {
	}

	public JComponent embeddMap(MapScrollPane map) {
		mapPane.removeAll();
		mapPane.add(map, BorderLayout.CENTER);
		return panel;
	}

}
