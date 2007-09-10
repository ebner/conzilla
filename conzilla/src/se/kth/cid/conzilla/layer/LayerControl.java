/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.layer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.TreeNode;

import se.kth.cid.component.Component;
import se.kth.cid.component.Container;
import se.kth.cid.component.EditEvent;
import se.kth.cid.component.EditListener;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.map.graphics.Mark;
import se.kth.cid.conzilla.metadata.EditPanel;
import se.kth.cid.conzilla.properties.ColorTheme;
import se.kth.cid.conzilla.properties.ColorTheme.Colors;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.session.SessionManager;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.LayerEvent;
import se.kth.cid.layout.LayerLayout;
import se.kth.cid.layout.LayerListener;
import se.kth.cid.layout.LayerManager;
import se.kth.cid.tree.TreeTagNode;
import se.kth.cid.util.AttributeEntryUtil;

/**
 * @author Matthias Palmer.
 */
public class LayerControl extends JPanel implements PropertyChangeListener, ChangeListener, LayerListener {

    public final static String LAYER_CONTROL_MENU = "LAYER_CONTROL_MENU";

    JSlider layerSlider;

    LayerEntries layerEntries;

    MapController controller;

    LayerManager lMan;

    boolean commit;

    boolean lock;

    PopupLayerInfo pli;

	private boolean editEnabled = false;
    
    public class LayerEntries extends JPanel implements EditListener {
        /*
         * Contains the layers in oposite order compared to the LayerManager
         * order. This is due to the graphical presentation.
         */
        Vector layerEntries;

        LayerEntry current;
        
        int currentIndex;

        LayerEntry choosenLayerEntry;

		private MouseInputAdapter mia;
		
        ToolsMenu popup;
        
		private Tool edit;

		private Tool remove;

		private Tool lower;

		private Tool raise;

		private Tool create;

		private Tool activate;

        
        public LayerEntries() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(Color.white);
            layerEntries = new Vector();
            
            mia = new MouseInputAdapter() {
            	private void handleMouseEvent(MouseEvent me) {
            		System.out.println(me);
                	if (!(me.getComponent() instanceof LayerEntry))
                        return;
                	LayerEntry oldLayerEntry = choosenLayerEntry;
                    choosenLayerEntry = (LayerEntry) me.getComponent();
                	if (editEnabled && me.isPopupTrigger()) {
                		updateEditableSensitiveTools();
                		JPopupMenu menu = popup.getPopupMenu(); 
                		menu.show(me.getComponent(), me.getX(), me.getY());
                		return;
                	}
                	
                    if (choosenLayerEntry == oldLayerEntry) {
                    	choosenLayerEntry.setHighlighted(!choosenLayerEntry.isHighlighted());
                    } else {
                    	choosenLayerEntry.setHighlighted(true);
                        if  (oldLayerEntry != null) {
                        	oldLayerEntry.setHighlighted(false);
                        }
                    }     
            	}
                public void mousePressed(MouseEvent me) {
                	handleMouseEvent(me);
                }
                public void mouseReleased(MouseEvent me) {
                	handleMouseEvent(me);
                }
                public void mouseClicked(MouseEvent me) {
                	handleMouseEvent(me);
                }
            };
            addTools();
        }
        
        
        private void updateEditableSensitiveTools() {
        	LayerLayout ll = choosenLayerEntry.getLayerLayout();
        	String loadContainer = ll.getLoadContainer();
        	Container container = ll.getComponentManager()
        		.getContainer(URI.create(loadContainer));
        	SessionManager sm = ConzillaKit.getDefaultKit().getSessionManager();
        	if (container.isEditable() && sm!= null && sm.getCurrentSession() != null
        			&& sm.getCurrentSession().getContainerURIForLayouts().equals(loadContainer)) {
        		edit.setEnabled(true);
        		remove.setEnabled(ll.getChildCount()==0);
        		TreeNode parent = ll.getParent();
        		lower.setEnabled(parent.getIndex(ll)+1 != parent.getChildCount());
        		raise.setEnabled(parent.getIndex(ll)!=0);
        	} else {
        		edit.setEnabled(false);
        		remove.setEnabled(false);
        		lower.setEnabled(false);
        		raise.setEnabled(false);
        	}
        	create.setEnabled(ll.getConceptMap().getComponentManager().getEditingSesssion()!= null);
    		activate.setEnabled(ll != ll.getConceptMap().getLayerManager().getEditGroupLayout());
        }
        
        private void addTools() {
        	
            popup = new ToolsMenu("LAYER", LayerControl.class.getName());
            edit = new Tool("EDIT", LayerControl.class.getName()) {
				public void actionPerformed(ActionEvent e) {
					choosenLayerEntry.getLayerLayout().addEditListener(LayerEntries.this);
					EditPanel.launchEditPanelInFrame(choosenLayerEntry.getLayerLayout());
				}
            };
            popup.addTool(edit, 100);
            remove = new Tool("REMOVE", LayerControl.class.getName()) {
				public void actionPerformed(ActionEvent e) {
					choosenLayerEntry.getLayerLayout().remove();
					refresh(choosenLayerEntry.getLayerLayout().getConceptMap().getLayerManager());
				}
            };
            popup.addTool(remove, 200);
            lower = new Tool("LOWER", LayerControl.class.getName()) {
				public void actionPerformed(ActionEvent e) {
					LayerLayout ll = choosenLayerEntry.getLayerLayout();
					TreeTagNode parent = (TreeTagNode) ll.getParent();
					parent.setIndex(ll, parent.getIndex(ll)+1);
					refresh(choosenLayerEntry.getLayerLayout().getConceptMap().getLayerManager());
				}
            };
            popup.addTool(lower, 300);
            raise = new Tool("RAISE", LayerControl.class.getName()) {
				public void actionPerformed(ActionEvent e) {
					LayerLayout ll = choosenLayerEntry.getLayerLayout();
					TreeTagNode parent = (TreeTagNode) ll.getParent();
					parent.setIndex(ll, parent.getIndex(ll)-1);
					refresh(choosenLayerEntry.getLayerLayout().getConceptMap().getLayerManager());
				}
            };
            popup.addTool(raise, 300);
            create = new Tool("CREATE", LayerControl.class.getName()) {
				public void actionPerformed(ActionEvent e) {
					LayerLayout ll = choosenLayerEntry.getLayerLayout();
					ContextMap cMap = ll.getConceptMap();
					Session session = cMap.getComponentManager().getEditingSesssion();
					LayerLayout nll = ll.getConceptMap().getLayerManager().createLayer(null, 
							URI.create(session.getContainerURIForLayouts()), cMap);
					nll.addEditListener(LayerEntries.this);
		            EditPanel.launchEditPanelInFrame(nll);
				}
            };
            popup.addTool(create, 300);
            activate = new Tool("ACTIVATE", LayerControl.class.getName()) {
				public void actionPerformed(ActionEvent e) {
					LayerLayout ll = choosenLayerEntry.getLayerLayout();
					ContextMap cMap = ll.getConceptMap();
					cMap.getLayerManager().setEditGroupLayout(ll.getURI());
					choosenLayerEntry.refreshEditGroupLayout();
					refreshGroupLayouts();
				}
            };
            popup.addTool(activate, 300);
        }

        private void refreshGroupLayouts() {
			for (Iterator lei = layerEntries.iterator(); lei.hasNext();) {
				LayerEntry le = (LayerEntry) lei.next();
				le.refreshEditGroupLayout();
			}
			LayerEntries.this.repaint();        	
        }
        
        private void addLabelText(String text) {
    		JLabel label = new JLabel(text);
    		JPanel labelPanel = new JPanel();
    		labelPanel.setOpaque(false);
    		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
    		labelPanel.add(Box.createHorizontalGlue());
    		labelPanel.add(label);
    		labelPanel.add(Box.createHorizontalGlue());
    		labelPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
    		add(labelPanel);
        }

        public void refresh(LayerManager lMan) {
        	clear();
        	if (layerEntries != null && !layerEntries.isEmpty()) {
        		for (Iterator iter = layerEntries.iterator(); iter.hasNext();) {
					LayerEntry le = (LayerEntry) iter.next();
					le.removeMouseListener(mia);
					le.removeMouseMotionListener(pli.mouseListener);
					le.removeMouseListener(pli.mouseListener);
				}
        	}
        	
        	layerEntries = new Vector();
        	add(Box.createVerticalStrut(7));
    		addLabelText("ContextMap Layers");

            Enumeration en = lMan.getLayers().elements();
            while (en.hasMoreElements())
                addLayerEntry((LayerLayout) en.nextElement());
            add(Box.createVerticalGlue());
            revalidate();
        }
        
        private void addLayerEntry(LayerLayout ls) {
            LayerEntry entry = new LayerEntry(ls);
            entry.addMouseListener(mia);
            entry.addMouseMotionListener(pli.mouseListener);
			entry.addMouseListener(pli.mouseListener);

            layerEntries.insertElementAt(entry, 0);
            add(entry);
        }

        public void selectTo(int range) {
            for (int i = 0; i < layerEntries.size(); i++)
                ((LayerEntry) layerEntries.elementAt(i))
                        .setLayerVisible(i >= range);
        }

        protected void setLayerChoosen(LayerEntry entry) {
            if (entry == null)
                return;
            if (choosenLayerEntry != null)
                if (choosenLayerEntry == entry)
                    return;
                else
                    choosenLayerEntry.setHighlighted(false);

            entry.setHighlighted(true);
            choosenLayerEntry = entry;
        }
        
        public Vector getLayers() {
            return layerEntries;
        }

        public void clear() {
            removeAll();
            layerEntries.clear();
        }

		public void componentEdited(EditEvent e) {
			if (e.getEditType() == Component.ATTRIBUTES_EDITED) {
				e.getComponent().removeEditListener(this);
				for (Iterator lei = layerEntries.iterator(); lei.hasNext();) {
					LayerEntry le = (LayerEntry) lei.next();
					le.refreshLabel();
				}
				repaint();
			}
		}
    }

    public class LayerEntry extends JPanel {
        LayerLayout ls;
        JLabel label;
        JCheckBox cb;
		private boolean highlighted;

        public LayerEntry(LayerLayout layout) {
    		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    		setOpaque(true);    		
    		setBackground(Color.white);
            ls = layout;
    		label = new JLabel();
    		refreshLabel();
    		label.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            cb = new JCheckBox();
    		cb.setBackground(Color.white);
            LayerManager lm = layout.getConceptMap().getLayerManager();
    		cb.setSelected(lm.getLayerVisible(layout.getURI()));
    		refreshEditGroupLayout();
            add(cb);
            add(label);
            add(Box.createHorizontalGlue());
            addListeners();
        }

        public void refreshEditGroupLayout() {
        	LayerLayout egl = ls.getConceptMap().getLayerManager().getEditGroupLayout();
        	if (LayerControl.this.editEnabled && ls == egl) {
        		label.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 5, Color.green));
        	} else {
        		label.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));        		
        	}
        }
        
        public void refreshLabel() {
			String labelString = AttributeEntryUtil.getTitleAsString(ls);
            if (labelString != null) {
            	label.setText(labelString);
            } else {
            	label.setText(ls.getURI().substring(ls.getURI().lastIndexOf('/')+1));
            }
        }

        public JLabel getLabel() {
            return label;
        }

        void addListeners() {
            ItemListener il;
            il = new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    LayerControl lc = LayerControl.this;

                    if (!lc.commit)
                        return;

                    lc.lock = true;
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        controller.getConceptMap().getLayerManager().setLayerVisible(ls.getURI(), true);
                        lc.controller.getView().getMapScrollPane().getDisplayer()
                                .repaint();
                    } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                        controller.getConceptMap().getLayerManager().setLayerVisible(ls.getURI(), false);
                        lc.controller.getView().getMapScrollPane().getDisplayer()
                        .repaint();
                    }
                    lc.lock = false;
                }
            };
            cb.addItemListener(il);
        }

        public void setLayerVisible(boolean bo) {
            cb.setSelected(bo);
        }

    	public void setHighlighted(boolean b) {
    		if (highlighted != b) {
    			highlighted = b;
    			if (highlighted) {
    				setBackground(ColorTheme.getBrighterColor(Colors.CONCEPT_FOCUS));
    		        Mark overMark= new Mark(Colors.CONCEPT_FOCUS, null, null);

    		        Vector children = ls.getChildren();
    		        Iterator mapObjects = controller.getView().getMapScrollPane().getDisplayer().getMapObjects().iterator();
    		        while (mapObjects.hasNext()) {
    		            MapObject mo = (MapObject) mapObjects.next();
    		            DrawerLayout dl = mo.getDrawerLayout();
    		            if (children.contains(dl)) {
    						mo.pushMark(overMark, this);
    		            }
    		        }
    			} else {
    				setBackground(Color.white);
    		        Iterator mapObjects = controller.getView().getMapScrollPane().getDisplayer().getMapObjects().iterator();
    		        while (mapObjects.hasNext()) {
    		            MapObject mo = (MapObject) mapObjects.next();
    		            mo.popMark(this);
    		        }
    			}
    			repaint();
    		}
    	}
    	
    	public boolean isHighlighted() {
    		return highlighted;
    	}

        public LayerLayout getLayerLayout() {
            return ls;
        }
    }

    public LayerControl(MapController controller) {
        this.controller = controller;
        setBackground(Color.white);
        commit = true;
        lock = false;
    	pli = new PopupLayerInfo(this);

        controller.addPropertyChangeListener(this);
        layerEntries = new LayerEntries();
        layerEntries.setBackground(Color.white);

        layerSlider = new JSlider();
        layerSlider.setOrientation(JSlider.VERTICAL);
        layerSlider.setMajorTickSpacing(1);
        layerSlider.setMinorTickSpacing(1);
        //	layerSlider.setPaintLabels(true);
        layerSlider.setSnapToTicks(true);
        layerSlider.setPaintTicks(false);
        layerSlider.setInverted(true);

        layerSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        setLayout(new BorderLayout());
        add(layerEntries, BorderLayout.CENTER);
        add(layerSlider, BorderLayout.EAST);
        
    }

    public void activate() {
    	pli.activate();
        fix();
        layerSlider.addChangeListener(this);
    }
    
    public void deactivate() {
    	layerSlider.removeChangeListener(this);
    }
    
    protected void fix() {
        LayerManager lMapNew = controller.getConceptMap().getLayerManager();
        
        if (lMan != lMapNew) {
            if (lMan != null)
                lMan.removeLayerListener(this);
            lMan = lMapNew;
            lMan.addLayerListener(this);
        }

        layerEntries.refresh(lMan);
        
        layerSlider.setMinimum(0);
        layerSlider.setMaximum(layerEntries.getLayers().size());
        layerSlider.setValue(0);
    }

    public void stateChanged(ChangeEvent e) {
        lock = true;
        int nr = (int) layerSlider.getValue();

        layerEntries.selectTo(nr);

        lock = false;
    }

    public void layerChange(LayerEvent le) {
        if (!lock)
            fix();
    }
    
    public void setEditingEnabled(boolean ee) {
    	editEnabled = ee;
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(MapController.MAP_PROPERTY))
            fix();
        if (e.getPropertyName().equals(MapController.MAPMANAGER_PROPERTY)) {
        	setEditingEnabled(controller.getConceptMap().getComponentManager().getEditingSesssion() != null);
        	layerEntries.refreshGroupLayouts();
        }
    }
}