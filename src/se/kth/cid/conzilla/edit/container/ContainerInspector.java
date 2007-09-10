/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.container;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.util.AttributeEntryUtil;

/**
 * @author matthias
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ContainerInspector implements ChangeListener, ListSelectionListener, ActionListener {
    
    private static ContainerInspector instance;

    public static ContainerInspector getInstance() {
        if (instance == null) {
            instance = new ContainerInspector();
        }
        return instance;
    }

    private JFrame frame;

    private JSplitPane split;

    private JPanel vertical;

    private JPanel globalButtons;

    private JComboBox containerChoice;

    private JTabbedPane explorer;
    
    private Container container;

    private ContextMap contextMap;
    
    private HashMap cm2title;
    
    private JList choices;

    /**
     * Since ContainerInspector is a singleton only a private constructor is
     * implemented.
     */
    private ContainerInspector() {
        initLayout();
    }
    
    public void show() {
        refreshContainerChoices();
        frame.pack();
        frame.setVisible(true);
    }

    public void selectForMap(ContextMap conceptMap) {
        Container c = ConzillaKit.getDefaultKit().getResourceStore().getContainerManager().getContainer(conceptMap.getLoadContainer());
        setSelectedContainer(c);
        setSelectedMap(conceptMap);
    }

    public void setSelectedContainer(Container container) {
        this.container = container;
        containerChoice.setSelectedItem(container);
        containerChoice.revalidate();
        refreshExplorer();
        refreshDetails();
    }
    
    public void setSelectedMap(ContextMap cMap) {
        if (container == null
                || cm2title == null) {
            return;
        }
        choices.setSelectedValue(cMap, true);
    }
    
    /**
     *  
     */
    private void initLayout() {
        //Init toplevel Swing components.
        frame = new JFrame();
        vertical = new JPanel();
        vertical.setLayout(new BorderLayout());
        split = new JSplitPane();
        globalButtons = new JPanel();
        globalButtons.setLayout(new BoxLayout(globalButtons, BoxLayout.X_AXIS));
        containerChoice = new JComboBox();
        containerChoice.setEditable(false);
        containerChoice.addActionListener(this);
        explorer = new JTabbedPane();
        explorer.addChangeListener(this);
        
        initButtons();
        
        //Put the Swing components together.
        split.setLeftComponent(explorer);
        vertical.add(split, BorderLayout.CENTER);
        vertical.add(globalButtons, BorderLayout.SOUTH);
        vertical.add(containerChoice, BorderLayout.NORTH);
        frame.setContentPane(vertical);
    }

    private void initButtons() {
        JButton remove = new JButton(new AbstractAction("Remove") {
            public void actionPerformed(ActionEvent e) {
                remove();
            }
        });
        globalButtons.add(remove);
    }
    
    private void remove() {
        if (contextMap != null) {
            int answer = JOptionPane.showConfirmDialog(frame, "Remove the ContextMap "
                    +(String) cm2title.get(contextMap)
                    +"from all containers?", "Remove ConceptMap", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                contextMap.remove();
            }
        }
    }
    
    private void refreshContainerChoices() {
        List containers = ConzillaKit.getDefaultKit().getResourceStore().getContainerManager().getContainers(Container.COMMON);
        containerChoice.setModel(new DefaultComboBoxModel(new Vector(containers)));
        if (container != null) {
            containerChoice.setSelectedItem(container);
        }
    }
    
    private void refreshExplorer() {
        explorer.removeAll();
        if (container == null) {
            return;
        }
        Vector cMaps = new Vector();
        cm2title = new HashMap();
        for (Iterator iter = container.getDefinedContextMaps().iterator(); iter.hasNext();)
            try {
                String uri = (String) iter.next();
                ContextMap conceptMap = ConzillaKit.getDefaultKit().getResourceStore()
                    .getAndReferenceConceptMap(new URI(uri));
                cMaps.add(conceptMap);
                cm2title.put(conceptMap, AttributeEntryUtil.getTitleAsString(conceptMap));
            } catch (ComponentException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        choices = new JList(cMaps);
        choices.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, cm2title.get(value), index, isSelected, cellHasFocus);
            }
        });
        choices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        choices.addListSelectionListener(this);
        if (contextMap != null 
                && cMaps.contains(contextMap)) {
            choices.setSelectedValue(contextMap, true);
        }
        explorer.add("ContextMaps", choices);
        //explorer.add("ContextMaps", new ComponentList(container, ContainerManager.CONTEXTMAP));
//        explorer.add("Concepts", new ComponentList(container, ContainerManager.CONCEPT));
    }

    private void refreshDetails() {
        if (contextMap != null) {
            split.setRightComponent(new MapInspector(container, contextMap));
            return;
        }
    }
   
    /**
     * Captures when the list is changed (via the tab).
     */
    public void stateChanged(ChangeEvent e) {
    }

    /**
     * Captures when a contextmap has been selected in the list.
     */
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        contextMap = (ContextMap) ((JList) e.getSource()).getSelectedValue();
        refreshDetails();
    }

    /**
     * Captures when the container is changed.
     */
    public void actionPerformed(ActionEvent e) {
        container = (Container) ((JComboBox) e.getSource()).getSelectedItem();
        refreshExplorer();
        refreshDetails();                
    }
 }