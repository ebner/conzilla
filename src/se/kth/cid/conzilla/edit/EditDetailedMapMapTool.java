/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.InvalidURIException;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.metadata.MetaDataFieldPanel;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.util.ErrorMessage;

/** 
 *  @author Matthias Palmr
 *  @version $Revision$
 */

public class EditDetailedMapMapTool extends Tool {
	
	Log log = LogFactory.getLog(EditDetailedMapMapTool.class);
	
    class DMDialog extends JDialog {

    	JTextField textField;
        MapObject mapObject;

        public DMDialog() {
            super(
                JOptionPane.getFrameForComponent(mcontroller.getView().getMapScrollPane()),
                "DetailedMap edit",
                true);

            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            JLabel l1 = new JLabel("Please type a URI for the detailed map.");
            l1.setForeground(Color.black);
            JLabel l2 = new JLabel(" ");
            JLabel l3 = new JLabel("Leaving this field empty means that the");
            JLabel l4 = new JLabel("component will have no detailed map.");
            JLabel l5 = new JLabel("Note that relative URIs do work.");
            JLabel l6 = new JLabel(" ");
            content.setBorder(new EmptyBorder(20, 20, 20, 20));

            textField = new JTextField(40);
            Dimension pref = textField.getPreferredSize();
            textField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, pref.height));
            content.add(l1);
            content.add(l2);
            content.add(l3);
            content.add(l4);
            content.add(l5);
            content.add(l6);
            content.add(new MetaDataFieldPanel("URI: ", textField));
            content.add(Box.createVerticalGlue());

            JButton ok = new JButton("Ok");
            JButton cancel = new JButton("Cancel");
            JButton revert = new JButton("Revert");
            JButton paste = new JButton("Paste");
            JButton test = new JButton("Test");

            JToolBar bar = new JToolBar();
            bar.setFloatable(false);
            bar.setBorder(new EmptyBorder(10, 30, 10, 30));
            bar.add(revert);
            bar.add(paste);
            bar.add(test);
            bar.add(Box.createHorizontalGlue());
            bar.add(ok);
            bar.add(cancel);

            JPanel cPane = new JPanel();
            setContentPane(cPane);
            cPane.setLayout(new BorderLayout());
            cPane.add(content, BorderLayout.CENTER);
            cPane.add(bar, BorderLayout.SOUTH);
            cPane.setMinimumSize(cPane.getPreferredSize());

            setDefaultCloseOperation(HIDE_ON_CLOSE);

            pack();

            // A return is the same as 'Set'.
            textField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    set();
                }
            });

            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    set();
                }
            });
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	setVisible(false);
                }
            });
            revert.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setMapObject(mapObject);
                }
            });
            paste.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Transferable trans =
                        Toolkit
                            .getDefaultToolkit()
                            .getSystemClipboard()
                            .getContents(
                            null);
                    try {
                        String uri =
                            (String) trans.getTransferData(
                                DataFlavor.stringFlavor);
                        if (uri != null)
                            textField.setText(uri);
                    } catch (UnsupportedFlavorException ue) {} catch (IOException ie) {}
                }
            });
            test.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (textField.getText().equals("")) {
                        ErrorMessage.showError(
                            "URI Error",
                            "Cannot show empty URI!",
                            null,
                            DMDialog.this);
                        return;
                    }

                    try {
                        ConzillaKit.getDefaultKit()
                            .getConzilla()
                            .openMapInNewView(
                            new URI(textField.getText()),
                            mcontroller);
                    } catch (URISyntaxException me) {
                        ErrorMessage.showError(
                            "Parse Error",
                            "Invalid URI",
                            me,
                            DMDialog.this);
                        return;
                    } catch (ControllerException me) {
                        ErrorMessage.showError(
                            "Cannot load map",
                            "Cannot load map",
                            me,
                            DMDialog.this);
                        return;
                    }
                }
            });
        }

        public void showDialog() {
            setLocationRelativeTo(mcontroller.getView().getMapScrollPane());
            log.debug("Loc: " + getLocation());
            pack();
            super.setVisible(true);
        }

        void set() {
            try {
                if (textField.getText().equals(""))
                    mapObject.getDrawerLayout().setDetailedMap(null);
                else {
                    try {
                        //Never send a relative uri anymore, RDF...
                    	String uri = new URI(textField.getText()).toString();
                    	log.debug("Absolute URI is " + uri);
                        mapObject.getDrawerLayout().setDetailedMap(uri);
                    } catch (URISyntaxException e) {
                    	log.error("Invalid URI", e);
                        ErrorMessage.showError("Parse Error", "Invalid URI", e, this);
                        return;
                    }
                }
                setVisible(false);
            } catch (InvalidURIException e) {
                log.error("Invalid URI", e);
            }
        }

        public void setMapObject(MapObject mapObject) {
            this.mapObject = mapObject;
            textField.setText(mapObject.getDrawerLayout().getDetailedMap());
        }
    }

    DMDialog dialog;

    public EditDetailedMapMapTool(MapController cont) {
        super("EDIT_DETAILEDMAP", EditMapManagerFactory.class.getName(), cont);
        dialog = new DMDialog();
    }

    protected boolean updateEnabled() {
        if (mapEvent.hitType != MapEvent.HIT_NONE 
            && mapEvent.mapObject.getDrawerLayout().isEditable())
            return true;
        return false;
    }

    public void actionPerformed(ActionEvent e) {
        dialog.setMapObject(mapObject);
        dialog.showDialog();
    }
}
