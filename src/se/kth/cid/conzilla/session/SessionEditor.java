/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.session;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;

/**
 * @author matthias
 */
public class SessionEditor extends JPanel implements ItemListener, ActionListener{
    Session project;
    ContainerManager manager;
    /*JLabel label_uri = new JLabel("Project URI");
    JTextField uri = new JTextField();*/
    JLabel labelTitle = new JLabel("Title:");
    JTextField title = new JTextField();
    JLabel labelContainerForConcepts = new JLabel("Container for concepts:");
    JComboBox containerForConcepts = new JComboBox();
    JLabel labelContainerForLayouts = new JLabel("Container for layouts:");
    JComboBox containerForLayouts = new JComboBox();
    JLabel labelConceptBaseURI = new JLabel("Base URI for Concepts:");
    JComboBox conceptBaseURI = new JComboBox();
    JLabel labelLaytouBaseURI = new JLabel("Base URI for Layouts:");
    JComboBox layoutBaseURI = new JComboBox();
    JButton cancel = new JButton("Cancel");
    JButton ok = new JButton("ok");
    JButton copy = new JButton("As above");
    int result;
    
    public SessionEditor(Session project, SessionManager pm, ContainerManager manager) {
        this.project = project;
        this.manager = manager;
        
        //containerForConcepts.setEditable(true);
        //containerForLayouts.setEditable(true);
        conceptBaseURI.setEditable(true);
        layoutBaseURI.setEditable(true);
        containerForConcepts.setEditable(true);
        containerForLayouts.setEditable(true);
        
        cancel.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                result = JOptionPane.CANCEL_OPTION;
            }
        });
        ok.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                result = JOptionPane.OK_OPTION;
                saveToProject();
            }
        });
        ok.setEnabled(false);
        copy.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                copy();
            }
            
        });
        
        initCombos();
        
        loadContainers(pm);
        loadBaseURIs(pm);
        loadFromProject();
        
        init();
    }
    
    
    protected void updateButtons() {
        String cbu = (String) conceptBaseURI.getEditor().getItem();
        boolean cbuOK = cbu != null && cbu.length() != 0; //check if correct URI...
        String lbu = (String) layoutBaseURI.getEditor().getItem();
        boolean lbuOK = lbu != null && lbu.length() != 0; //check if correct URI...
        boolean titleOK = title.getText() != null && title.getText().length() != 0;
        ok.setEnabled(containerForLayouts.getSelectedItem() != null && 
        containerForConcepts.getSelectedItem() != null &&
        cbuOK && lbuOK && titleOK);
    }
    
    protected void copy() {
        containerForLayouts.setSelectedItem(containerForConcepts.getSelectedItem());
        layoutBaseURI.setSelectedItem(conceptBaseURI.getSelectedItem());
        updateButtons();
    }
    
    protected void loadContainers(SessionManager sm) {
        Vector ccVec = new Vector();
        
//        modelVec.add("New container");
        for (Iterator models = manager.getContainers(Container.COMMON).iterator(); models.hasNext();) {
            Container model = (Container) models.next();
            if (model.isEditable()) {
                ccVec.add(model.getURI());
            }
        }
        
        Vector clVec = new Vector(ccVec);
        
        Iterator it = sm.getSessions().iterator();
        while(it.hasNext()) {
        	Session s = (Session) it.next();
        	String cufc = s.getContainerURIForConcepts();
        	String cufl = s.getContainerURIForLayouts();
        	if (!ccVec.contains(cufc)) {
        		ccVec.add(cufc);
        	}
        	if (!clVec.contains(cufl)) {
        		clVec.add(cufl);
        	}
        }

		containerForConcepts.setModel(new DefaultComboBoxModel(ccVec));
		containerForLayouts.setModel(new DefaultComboBoxModel(clVec));
    }
    
    protected void loadBaseURIs(SessionManager pm) {
		HashSet cu = new HashSet();
		HashSet lu = new HashSet();
        
        for (Iterator projects = pm.getSessions().iterator();projects.hasNext();) {
            Session project = (Session) projects.next();
            cu.add(project.getBaseURIForConcepts());
            lu.add(project.getBaseURIForLayouts());
        }
        
        conceptBaseURI.setModel(new DefaultComboBoxModel(new Vector(cu)));
        layoutBaseURI.setModel(new DefaultComboBoxModel(new Vector(lu)));
    }
    
    protected void loadFromProject() {
        title.setText(project.getTitle());
        containerForConcepts.setSelectedItem(project.getContainerURIForConcepts());
        containerForLayouts.setSelectedItem(project.getContainerURIForLayouts());
        conceptBaseURI.setSelectedItem(project.getBaseURIForConcepts());
        layoutBaseURI.setSelectedItem(project.getBaseURIForLayouts());
    }
    
    protected void saveToProject() {
        project.setTitle(title.getText());
        project.setContainerURIForConcepts((String) containerForConcepts.getSelectedItem());
        project.setContainerURIForLayouts((String) containerForLayouts.getSelectedItem());
        project.setBaseURIForConcepts((String) conceptBaseURI.getSelectedItem());
        project.setBaseURIForLayouts((String) layoutBaseURI.getSelectedItem()); 
    }

    protected void initCombos() {
          containerForConcepts.addItemListener(this);
          containerForLayouts.addItemListener(this);
    }
    
    protected void init() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.X_AXIS));
        titleBox.add(labelTitle);
        titleBox.add(title);
        add(titleBox);
        add(Box.createVerticalStrut(15));
        JPanel grouping1 = new JPanel();
        grouping1.setLayout(new GridBagLayout());
        grouping1.setBorder(BorderFactory.createTitledBorder("Concepts"));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.RELATIVE;
        grouping1.add(labelContainerForConcepts, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        grouping1.add(containerForConcepts, c);
        c.gridwidth = GridBagConstraints.RELATIVE;
        grouping1.add(labelConceptBaseURI, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        grouping1.add(conceptBaseURI, c);
        add(grouping1);
        add(Box.createVerticalStrut(15));
        
        JPanel grouping2 = new JPanel();
        grouping2.setLayout(new GridBagLayout());
        grouping2.setBorder(BorderFactory.createTitledBorder("Layouts"));
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        grouping2.add(copy, c);
        c.gridwidth = GridBagConstraints.RELATIVE;
        grouping2.add(labelContainerForLayouts, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        grouping2.add(containerForLayouts, c);
        c.gridwidth = GridBagConstraints.RELATIVE;
        grouping2.add(labelLaytouBaseURI, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        grouping2.add(layoutBaseURI, c);
        add(grouping2);
        add(Box.createVerticalStrut(15));
        add(Box.createHorizontalGlue());

        Box buttons = Box.createHorizontalBox();
        buttons.add(Box.createVerticalGlue());
        buttons.add(cancel);
        buttons.add(ok);
        add(buttons);
        
        KeyListener kl = new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateButtons();
                    }
                });
            }
        };
        
        title.addKeyListener(kl);
        layoutBaseURI.getEditor().getEditorComponent().addKeyListener(kl);
        layoutBaseURI.addActionListener(this);
        conceptBaseURI.getEditor().getEditorComponent().addKeyListener(kl);
        conceptBaseURI.addActionListener(this);
    }
    
    public void itemStateChanged(ItemEvent e) {
        updateButtons();
    }
    
    public static int launchProjectEditorDialog(Session project, SessionManager pm, ContainerManager manager) {
        final JDialog dialog = new JDialog();
        dialog.setModal(true);
        SessionEditor pe = new SessionEditor(project, pm, manager);
        dialog.setContentPane(pe);
        
        //Make sure the dialog is hidden when ok or cancel is pressed.
        AbstractAction aa = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        };
        pe.cancel.addActionListener(aa);
        pe.ok.addActionListener(aa);
        
        //Launch the model dialog and wait for ok or cancel.
        dialog.pack();
        dialog.setVisible(true);
        
        return pe.result;
    }


    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        updateButtons();
    }
}
