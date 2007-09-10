/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.print;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.RepaintManager;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.app.Extra;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.menu.DefaultMenuFactory;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.conzilla.util.ErrorMessage;

public class Export implements Extra {

    public Export() {
    }

    public String getName() {
        return "SVGExport";
    }

    public boolean initExtra(ConzillaKit kit) {
        return true;
    }

    public void exitExtra() {
    }

    public void addExtraFeatures(MapController c) {
    }

    public void refreshExtra() {
    }

    public boolean saveExtra() {
        return true;
    }

    public void extendMenu(ToolsMenu menu, final MapController c) {
        if (menu.getName().equals(DefaultMenuFactory.FILE_MENU)) {
            Tool t = new Tool("EXPORT", Export.class.getName()) {
                public void actionPerformed(ActionEvent ae) {
                    try {
                        export(c.getView().getMapScrollPane());
                    } catch (PrinterException e) {
                        ErrorMessage.showError("Print Error",
                                "Could not print map\n\n"
                                        + c.getView().getMapScrollPane().getDisplayer()
                                                .getStoreManager()
                                                .getConceptMap().getURI(), e,
                                null);
                    }
                }
            };
            t.setIcon(Images.getImageIcon(Images.ICON_FILE_EXPORT));
            t.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                    Event.CTRL_MASK));
            menu.addTool(t, 345);
            }
    }

    public boolean export(MapScrollPane sp) throws PrinterException {

    	// Get a DOMImplementation
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document
        Document document = domImpl.createDocument(null, "svg", null);

        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        java.awt.Component vp = sp.getViewport().getView();
        RepaintManager rm = RepaintManager.currentManager(vp);
        rm.setDoubleBufferingEnabled(false);
        vp.paint(svgGenerator);
        rm.setDoubleBufferingEnabled(true);
        boolean useCSS = true; // we want to use CSS style attribute
        
		try {
			JFileChooser fc = new JFileChooser();
			if (fc.showDialog(sp, "Export") == JFileChooser.APPROVE_OPTION) {
				FileOutputStream fo = new FileOutputStream(fc.getSelectedFile());
				Writer out = new OutputStreamWriter(fo);
		        svgGenerator.stream(out, useCSS);
			}
		} catch (SVGGraphics2DIOException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        return true;
    }

}