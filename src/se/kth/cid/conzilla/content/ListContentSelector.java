/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.Component;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.ComponentManager;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapDisplayer;
import se.kth.cid.conzilla.properties.ColorTheme;
import se.kth.cid.conzilla.properties.ConzillaResourceManager;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.conzilla.view.View;
import se.kth.cid.notions.ContentInformation;
import se.kth.cid.util.ContentInformationWithTitle;
import se.kth.cid.util.TagManager;

public class ListContentSelector extends JPanel implements ContentSelector, PropertyChangeListener {
    
	Log log = LogFactory.getLog(ListContentSelector.class);
	
	Vector contentInformation;

    Vector selectionListeners;
    ContentMenu contentMenu;
    PropertyChangeSupport pSupport;
    MapController controller;
    PropertyChangeListener colorListener;

    PopupContentInfo popup;

    double scale = 1.0;
    Font origFont;
    Font contentPathFont;

    JPanel titlePane;
    JList list;
    int selected;

	private ComponentManager componentManager;

	private Set contentSet;

    private void updateColors() {
        list.setBackground(ColorTheme.getBrighterColor(ColorTheme.Colors.CONTENT));
        list.setForeground(ColorTheme.getColor(ColorTheme.Colors.FOREGROUND));
        list.setSelectionBackground(ColorTheme.getColor(ColorTheme.Colors.FOREGROUND));
        list.setSelectionForeground(ColorTheme.getBrighterColor(ColorTheme.Colors.CONTENT));
    }

    public ListContentSelector() {
        /*        setFont(new Font("Lucida Sans", Font.PLAIN, 12));
                origFont = getFont();
                Tracer.debug("List: " + getFont());
                contentPathFont = new Font("Arial", Font.PLAIN, 10);
        */
        selectionListeners = new Vector();
        pSupport = new PropertyChangeSupport(this);

        list = new JList();
        origFont = list.getFont();
        //      list.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
        //      list.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));

        titlePane = new JPanel();
        titlePane.setLayout(new BoxLayout(titlePane, BoxLayout.Y_AXIS));

        colorListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                ListContentSelector.this.updateColors();
            }
        };
        ConfigurationManager.getConfiguration().addPropertyChangeListener(ColorTheme.COLORTHEME, colorListener);

        updateColors();

        setLayout(new BorderLayout());

        //      descriptionPopUp.getContentsetLayout(new BorderLayout());

        MouseInputAdapter mouseListener = new MouseInputAdapter() {
            public void mousePressed(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());

                if (index == -1)
                    return;

                if ((e.isPopupTrigger()
                    || SwingUtilities.isRightMouseButton(e))
                    && index != -1) {
                    list.setSelectedIndex(index);
                    popup.removeAllPopups();
                    contentMenu.showPopup(e, index);
                }
            }
            public void mouseClicked(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());

                if (e.getClickCount() == 2) {
                    popup.removeAllPopups();
                    select(index);
                }
            }
        };

        list.addMouseListener(mouseListener);

        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()
                    && contentInformation != null
                    && ((JList) lse.getSource()).getSelectedIndex() >= 0) {
                    ContentInformationWithTitle cit = (ContentInformationWithTitle) ((JList) lse.getSource()).getSelectedValue();
                    pSupport.firePropertyChange(
                        new PropertyChangeEvent(list, SELECTION, null, cit.getComponent()));
                }
            }
        });
        popup = new PopupContentInfo(this, list);
    }
    
    protected void finalize() {        
        ConfigurationManager.getConfiguration().removePropertyChangeListener(ColorTheme.COLORTHEME, colorListener);
    }

    void addAll() {
        add(titlePane, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane();
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setViewportView(list);
        scroll.getHorizontalScrollBar().setUnitIncrement(15);
        scroll.getVerticalScrollBar().setUnitIncrement(15);

        add(scroll, BorderLayout.CENTER);

        JButton close = new JButton();
        ConzillaResourceManager.getDefaultManager().customizeButton(close, ListContentSelector.class.getName(), "CLOSE");
        add(close, BorderLayout.SOUTH);

        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectContentFromSet(null, null);
                popup.deactivate();
                /*	      removeAll();
                  revalidate();
                  repaint();*/
            }
        });

        popup.activate();

        revalidate();
        repaint();
    }

    public void setContentPath(String[] contpath) {
        Color foreground = ColorTheme.getColor(ColorTheme.Colors.FOREGROUND);
        Color background = ColorTheme.getBrighterColor(ColorTheme.Colors.CONTENT);
        titlePane.removeAll();
        int inc = 2;
        JTextArea label = null;
        for (int i = contpath.length - 1; i >= 0; i--) {
            label = new JTextArea(contpath[i]);
            label.setEditable(false);
            label.setBorder(BorderFactory.createEmptyBorder(0, inc, 0, 2));
            label.setFont(
                origFont.deriveFont(
                    (float) (origFont.getSize2D() * scale * 1.3)));
            label.setForeground(foreground);
            label.setOpaque(false);
            titlePane.add(label);
            inc += 6;
        }
        if (label != null) {
            titlePane.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0,0,1,0,Color.BLACK),
                    BorderFactory.createMatteBorder(2,2,2,2, foreground)));
            titlePane.setBackground(background);
        }
    }

    public void setController(final MapController controller) {
        this.controller = controller;

        PropertyChangeListener zoomListener;
        zoomListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(View.ZOOM_PROPERTY))
                    setScale(
                        ((Double) evt.getNewValue()).doubleValue(),
                        ((Double) evt.getOldValue()).doubleValue());
            }
        };

        controller.addPropertyChangeListener(zoomListener);
        pSupport.addPropertyChangeListener(controller);

        contentMenu = new ContentMenu(controller);
    }

    public MapController getController() {
        return controller;
    }

    public void select(int select) {
        if (list.getSelectedIndex() != select)
            list.setSelectedIndex(select);
        try {
            Component c = getContent(select);
            if (c != null) {
                ConzillaKit.getDefaultKit().getContentDisplayer().setContent(c);
                //        controller.getHistoryManager().fireHistoryEvent(he);
                return;
            }
        } catch (ContentException e) {
        	log.error(e.getMessage(), e);
        }
        ErrorMessage.showError(
            "Failed to show content.",
            "Failed to show content.",
            null,
            list);
    }

    public Component getSelectedContent() {
        int selected = list.getSelectedIndex();
        if (contentInformation != null
            && selected >= 0
            && selected < contentInformation.size()) {
            return getContent(selected);
        }
        return null;
    }

    public Component getContent(int index) {        
        ContentInformationWithTitle cit = (ContentInformationWithTitle) contentInformation.elementAt(index);
        return cit.getComponent();
    }

    public void selectContentFromSet(Set content, ComponentManager componentManager) {
        Vector old = this.contentInformation;

        if (this.componentManager != componentManager) {
        	if (this.componentManager != null) {
        		this.componentManager.getTagManager().removePropertyChangeListener(this);
        	}

        	if (componentManager != null) {
        		this.componentManager = componentManager;
        		componentManager.getTagManager().addPropertyChangeListener(this);
        	}
        }
        
        this.contentSet = content;
        if (content == null) {
            this.contentInformation = null;
            list.setListData(new Vector());
        } else {
            TreeSet ts = new TreeSet();
            Set<URI> loadedContainers = this.componentManager.getLoadedRelevantContainers();
            for (Iterator contentIt = content.iterator(); contentIt.hasNext();) {
                ContentInformation ci = (ContentInformation) contentIt.next();
                URI ciCont = URI.create(ci.getContainer().getURI());
                if (this.componentManager == null
                		|| this.componentManager.getContainerVisible(ciCont)
                		|| !loadedContainers.contains(ciCont)) {
                	try {
                		ts.add(new ContentInformationWithTitle(ci));
                	} catch (ComponentException e) {
                		log.error("Cannot add ContentInformation since I cannot load the content Component", e);
                	}
                }
            }
            
            this.contentInformation = new Vector(ts);
            list.setListData(contentInformation);
            list.revalidate();
        }
        removeAll();
        popup.deactivate();

        if (content != null) {
            addAll();
            Dimension dim = getPreferredSize();
            if (dim.width > 200)
                setPreferredSize(new Dimension(200, dim.height));
            else if (dim.width < 30) {
                setPreferredSize(new Dimension(30, dim.height));
            } else {
                setPreferredSize(dim);
            }
        }
        
        pSupport.firePropertyChange(SELECTOR, old, content);
    }

    public JComponent getComponent() {
        return this;
    }

    public void addSelectionListener(
        String propertyName,
        PropertyChangeListener l) {
        pSupport.addPropertyChangeListener(propertyName, l);
    }

    public void removeSelectionListener(
        String propertyname,
        PropertyChangeListener l) {
        pSupport.removePropertyChangeListener(propertyname, l);
    }

    public void setScale(double newscale, double oldscale) {
        this.scale = newscale;
        
        if (origFont != null) {
            list.setFont(
                origFont.deriveFont((float) (origFont.getSize2D() * scale)));            
        }

        java.awt.Component[] comps = titlePane.getComponents();
        for (int i = 0; i < comps.length; i++)
            comps[i].setFont(
                origFont.deriveFont(
                    (float) (origFont.getSize2D() * scale * 1.3)));

        popup.setScale(newscale, oldscale);
                  
        revalidate();
        repaint();
    }
    
    
    /**
     * @see java.awt.Component#print(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        MapDisplayer.setRenderingHints(g);
        super.paint(g);
    }
    
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName(); 	
        if (prop.equals(TagManager.TAG_VISIBILITY_CHANGED)) {
        	selectContentFromSet(this.contentSet, this.componentManager);
        }
    }

}