/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.bookmark;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;

/**
 * Dialog to request information necessary for adding entries to the
 * BookmarkTree.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class BookmarkEntryDialog extends JDialog {
	
    private JButton buttonCancel;

	private JButton buttonOK;

	private JLabel labelDescription;

	private JLabel labelName;

	private JLabel labelURI;

	private JTextArea textAreaDescription;

	private JScrollPane textAreaDescriptionScrollPane;

	private JTextField textFieldName;

	private JTextField textFieldURI;

	private JLabel labelCreate;

	private javax.swing.JScrollPane scrollPane;

	private javax.swing.JSeparator separator;
    
    /* Helper variables */
    
    private int bookmarkInfoType;
    
    private boolean closedWithOK;
    
    private boolean showTree = false;
    
    private MapController controller;
    
    private BookmarkTree tree;
    
    /**
	 * @param title
	 *            Title of the dialog.
	 * @param bookmarkInfoType
	 *            The type of the queried information. Influences the appearance
	 *            of the dialog. A value of BookmarkInformation.TYPE_*.
	 * @param controller
	 *            MapController of the current map.
	 */
    public BookmarkEntryDialog(String title, int bookmarkInfoType, MapController controller) {
        super();
        setModal(true);
        this.bookmarkInfoType = bookmarkInfoType;
        this.controller = controller;
        setTitle(title);
        initComponents();
    }
    
    /**
	 * @param title
	 *            Title of the dialog.
	 * @param bookmarkInfo
	 *            Predefined BookmarkInformation. The fields of the dialog will
	 *            be filled accordingly.
	 * @param showTree
	 *            Whether or not to show the BookmarkTree (for selecting the
	 *            position for the newly created entry).
	 * @param controller
	 *            MapController.
	 * @param parent
	 *            Parent frame.
	 * @param modal
	 *            Modal state of the dialog.
	 */
    public BookmarkEntryDialog(String title, BookmarkInformation bookmarkInfo, boolean showTree, MapController controller, Frame parent, boolean modal) {
    	this(title, bookmarkInfo.getType(), controller);
    	if (bookmarkInfo.getName() != null) {
    		textFieldName.setText(bookmarkInfo.getName());
    	}
    	if (bookmarkInfo.getUri() != null) {
    		textFieldURI.setText(bookmarkInfo.getUri());
    	}
    	if (bookmarkInfo.getDescription() != null) {
    		textAreaDescription.setText(bookmarkInfo.getDescription());
    	}
    	this.showTree = showTree;
    }
    
    /**
     * Initialized the graphical components.
     */
    private void initComponents() {
        textFieldName = new JTextField();
        labelName = new JLabel();
        labelURI = new JLabel();
        labelDescription = new JLabel();
        textFieldURI = new JTextField();
        textAreaDescriptionScrollPane = new JScrollPane();
        textAreaDescription = new JTextArea();
        textAreaDescription.setLineWrap(true);
        buttonCancel = new JButton();
        buttonOK = new JButton();
        scrollPane = new JScrollPane();
        labelCreate = new JLabel();
        separator = new JSeparator();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        labelName.setText("Name:");

        labelURI.setText("URI:");

        labelDescription.setText("Description:");
        
        labelCreate.setText("Create bookmark at:");

        textAreaDescription.setColumns(20);
        textAreaDescription.setRows(5);
        textAreaDescriptionScrollPane.setViewportView(textAreaDescription);

        buttonCancel.setText("Cancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setVisible(false);
			}
		});

        buttonOK.setText("OK");
		buttonOK.addActionListener(new ActionListener() {
			private void hide() {
				closedWithOK = true;
				setVisible(false);
			}
			public void actionPerformed(ActionEvent evt) {
				if ((bookmarkInfoType == BookmarkInformation.TYPE_FOLDER) &&
						(textFieldName.getText().trim().length() > 0)) {
					hide();
				} else if ((bookmarkInfoType == BookmarkInformation.TYPE_CONTEXTMAP) &&
						(textFieldName.getText().trim().length() > 0) &&
						(textFieldURI.getText().trim().length() > 0)) {
					hide();
				} else {
					JOptionPane.showMessageDialog(BookmarkEntryDialog.this,
							"The supplied information is incomplete.", "Information incomplete",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(scrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, separator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(labelName)
                            .add(labelURI)
                            .add(labelDescription))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(textFieldURI, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                                    .add(textFieldName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)))
                            .add(layout.createSequentialGroup()
                                .add(12, 12, 12)
                                .add(textAreaDescriptionScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(buttonOK)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(buttonCancel))
                    .add(labelCreate))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labelName)
                    .add(textFieldName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labelURI)
                    .add(textFieldURI, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labelDescription)
                    .add(textAreaDescriptionScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(14, 14, 14)
                .add(separator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(labelCreate)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(buttonCancel)
                    .add(buttonOK))
                .addContainerGap())
        );
         
        if (bookmarkInfoType == BookmarkInformation.TYPE_FOLDER) {
        	labelURI.setVisible(false);
        	textFieldURI.setVisible(false);
        }
        
        // By default we don't show the tree.
        labelCreate.setVisible(false);
        scrollPane.setVisible(false);
        separator.setVisible(false);
    }
    
    /**
	 * @return Returns a new BookmarkInformation object filled with values from
	 *         the dialog.
	 */
	public BookmarkInformation getBookmarkInformation() {
		BookmarkInformation info = new BookmarkInformation();
		info.setName(textFieldName.getText());
		info.setUri(textFieldURI.getText());
		info.setType(bookmarkInfoType);
		info.setDescription(textAreaDescription.getText());

		return info;
	}

	/**
	 * @return Returns the selected node from the BookmarkTree. The value is
	 *         null if there was no node selected or whether there was no tree
	 *         shown.
	 */
    public BookmarkNode getSelectedNode() {
    	if (tree == null) {
    		return null;
    	}
    	return tree.getSelectedNode();
    }
    
	/**
	 * Shows the dialog.
	 * 
	 * @return Returns true if the dialog was closed via the OK button,
	 *         otherwise returns false.
	 */
	public boolean showDialog() {
		if (showTree) {
        	labelCreate.setVisible(true);
        	separator.setVisible(true);
        	tree = new BookmarkTree(ConzillaKit.getDefaultKit().getBookmarkStore().getTreeModel(), controller);
        	scrollPane.setViewportView(tree);
        	scrollPane.setVisible(true);
        }
		pack();
		setLocationRelativeTo(ConzillaKit.getDefaultKit().getConzilla().getViewManager().getWindow());
		setVisible(true);
		
		return closedWithOK;
	}

}