/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.wizard.newsession;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.util.wizard.WizardComponentAdapter;

import com.ibm.icu.util.StringTokenizer;

/**
 * @author matthias
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpecifyContainer extends WizardComponentAdapter {

    public static final Integer CREATE_TAB = new Integer(0);
    public static final Integer LOADED_TAB = new Integer(1);
    
    public static final String INFO_CONTAINER_TAB = "information container tab";
    public static final String INFO_PATH = "information path";
    public static final String INFO_FILE_COUNTER = "information file name counter";
    public static final String INFO_CONTAINER_URI = "information container uri";

    public static final String PRES_CONTAINER_TAB = "presentation container tab";
    public static final String PRES_PATH = "presentation path";
    public static final String PRES_CONTAINER_URI = "presentation container uri";

    
    JTabbedPane tabbs;
    JComboBox loadedContainers;
    JPanel loadedTab;
    JPanel createTab;
    JTextField cp;
    JTextField createFileName;
    String type;
    int fileNameCounter;
    
    protected Timer timer;
    
    public void enter() {
        String ns = (String) passedAlong.get(SpecifyNameSpace.NAMESPACE);
        String name = (String) passedAlong.get(SpecifySessionName.SESSION_NAME);
        name = name.replace(' ', '_');

        if (!name.endsWith("/")) {
        	name = name+"/";
        }

        String path = getMaximumPath(ns+name);
        cp.setText("urn:path:"+path);
        createFileName.setText(getAcceptableNewFileName());                
    }
    
    
    
    static public class SpecifyInformationContainer extends SpecifyContainer {
        public SpecifyInformationContainer() {
            super("<html><body>Give the file where the information, <br>" +
                    "i.e. concepts and concept-relations will be stored.</body></html>", 
                    "Here be dragons", 
                    "information");
        }

        public void next() {
            if (tabbs.getSelectedComponent().equals(createTab)) {
                passedAlong.put(INFO_CONTAINER_TAB, CREATE_TAB);
            } else {
                passedAlong.put(INFO_CONTAINER_TAB, LOADED_TAB);
            }
            
            passedAlong.put(INFO_CONTAINER_URI, getNewURI());
        }

    }

    static public class SpecifyPresentationContainer extends SpecifyContainer {
        public SpecifyPresentationContainer() {
            super("<html><body>Give the file where the presentation, <br>" +
                    "i.e. map and its internals will be stored.",
                    "Here be dragons", 
                    "presentation");
        }

        public void next() {
            if (tabbs.getSelectedComponent().equals(createTab)) {
                passedAlong.put(PRES_CONTAINER_TAB, CREATE_TAB);
            } else {
                passedAlong.put(PRES_CONTAINER_TAB, LOADED_TAB);
            }
            
            passedAlong.put(PRES_CONTAINER_URI, getNewURI());
        }
    }

    public SpecifyContainer(String text, String helpText, String type) {
        super(text, helpText);
        this.type = type;
    }
    
    public boolean test() {
        if (tabbs.getSelectedComponent().equals(createTab)) {
            if (createFileName.getText().length() == 0) {
                return false;
            }

            try {
                URI uri = new URI(getNewURI());
                ConzillaKit.getDefaultKit().getResourceStore().checkCreateContainer(uri);
                return true;
            } catch (ComponentException e1) {
            } catch (URISyntaxException e2) {
            }
            return false;
        } else {
            return loadedContainers.getSelectedItem() != null;
        }
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponentAdapter#constructComponent()
     */
    protected JComponent constructComponent() {
        tabbs = new JTabbedPane(JTabbedPane.TOP);
     
        commonStuff();
        
        constructCreateTab();
        constructLoadedTab();
        
        tabbs.addTab("Create file", createTab);
        tabbs.addTab("Choose among loaded files", loadedTab);
        
        tabbs.setSelectedComponent(createTab);
        tabbs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setReady(test());
            }
        });
        
        
        return tabbs;
    }
    
    /**
     * 
     */
    private void commonStuff() {
        timer = new Timer(200, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setReady(test());
                timer.stop();
            }
        });
    }


    protected void constructLoadedTab() {
        loadedTab = new JPanel();
        loadedTab.setLayout(new BorderLayout());
        loadedContainers = new JComboBox();
        loadedContainers.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                    loadedContainers.getPreferredSize().height));
        refreshAvailableContainers();
        loadedTab.add(loadedContainers, BorderLayout.NORTH);
        loadedContainers.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setReady(test());
            }
        });
    }

    private void refreshAvailableContainers() {
        Vector containers =
            new Vector(
                ConzillaKit
                    .getDefaultKit()
                    .getResourceStore()
                    .getContainerManager()
                    .getContainers(Container.COMMON));
        loadedContainers.setModel(new DefaultComboBoxModel(containers));
        loadedContainers.setSelectedItem(null);
    }

    protected void constructCreateTab() {
        createTab = new JPanel();
        
        cp = new JTextField();
        cp.setEditable(false);
        
        //Path
        
        GridBagLayout gl = new GridBagLayout();
        createTab.setLayout(gl);
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        
        gc.fill = GridBagConstraints.NONE;
        gc.gridwidth = 1;
        gc.weightx = 0.0;
        createTab.add(new JLabel("Lookup path:"), gc);

        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.weightx = 1.0;
        createTab.add(cp, gc);
        
        //Filename
        
        createFileName = new JTextField();
        createFileName.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {
                timer.restart();
            }
        });
        
        gc.fill = GridBagConstraints.NONE;
        gc.gridwidth = 1;
        gc.weightx = 0.0;
        createTab.add(new JLabel("File name:"), gc);
        
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.weightx = 1.0;
        createTab.add(createFileName, gc);
    }
    
    String getAcceptableNewFileName() {
    	return type + ".rdf"; 
    }

    String getNewURI() {
        if (tabbs.getSelectedComponent().equals(createTab)) {
        	return cp.getText()+ "/" + createFileName.getText();   
        } else {
            return ((Container) loadedContainers.getSelectedItem()).getURI();
        }
    }
    
    String getMaximumPath(String ns) {
        try {
            URI uri = new URI(ns);
            String host = uri.getHost();
            String reversedHost = "";
            StringTokenizer st = new StringTokenizer(host, ".");
            while (st.hasMoreTokens()) {
                 reversedHost = "/" + (String) st.nextToken() + reversedHost;
            }
            String path = uri.getPath();
            if (path != null && path.length() > 0) {
                if (path.endsWith("/")) {
                    return reversedHost + path.substring(0, path.length()-1);
                } else {
                    return reversedHost + path;
                }
            } else {
                return reversedHost;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#hasFinish()
     */
    public boolean hasFinish() {
        return true;
    }
}