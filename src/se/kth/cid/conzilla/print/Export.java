/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.print;

import java.awt.Component;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.RepaintManager;
import javax.swing.filechooser.FileFilter;

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
	
	private class JPGFileFilter extends FileFilter {
		public boolean accept(File f) {
			return f.getName().toLowerCase().endsWith(".jpg") || f.getName().toLowerCase().endsWith(".jpeg") || f.isDirectory();
		}
		public String getDescription() {
			return "Joint Photographic Experts Group (*.jpg, *.jpeg)";
		}
	}
	
	private class PNGFileFilter extends FileFilter {
        public boolean accept(File f) {
        	return f.getName().toLowerCase().endsWith(".png") || f.isDirectory();
        }
        public String getDescription() {
            return "Portable Network Graphics (*.png)";
        }
	}
	
	private class SVGFileFilter extends FileFilter {
        public boolean accept(File f) {
            return f.getName().toLowerCase().endsWith(".svg") || f.isDirectory();
        }
        public String getDescription() {
            return "Scalable Vector Graphics (*.svg)";
        }
	}

    public Export() {
    }

    public String getName() {
        return "Export";
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
    		t.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));
    		menu.addTool(t, 345);
    	}
    }

    public boolean export(MapScrollPane sp) throws PrinterException {
    	try {
			JFileChooser fc = new JFileChooser();
            
			PNGFileFilter pngFilter = new PNGFileFilter();
			JPGFileFilter jpgFilter = new JPGFileFilter();
			SVGFileFilter svgFilter = new SVGFileFilter();
			
            fc.addChoosableFileFilter(pngFilter);
	        fc.addChoosableFileFilter(svgFilter);
	        fc.addChoosableFileFilter(jpgFilter);
	        fc.setFileFilter(pngFilter);
	        
	        fc.setAcceptAllFileFilterUsed(false);
	        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	        
			if (fc.showDialog(sp, "Export") == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				String fileString = file.toString();
				FileFilter selectedFilter = fc.getFileFilter();
				Component comp = sp.getViewport().getView();
				
				if (selectedFilter instanceof SVGFileFilter) {
					if (!fileString.endsWith(".svg")) {
						file = new File(fileString + ".svg");
					}
					exportToSVG(file, comp);
				} else if (selectedFilter instanceof PNGFileFilter) {
					if (!fileString.endsWith(".png")) {
						file = new File(fileString + ".png");
					}
					exportToBitmap(file, comp, "png");
				} else if (selectedFilter instanceof JPGFileFilter) {
					if (!fileString.endsWith(".jpg") && !fileString.endsWith(".jpeg")) {
						file = new File(fileString + ".jpg");
					}
					exportToBitmap(file, comp, "jpg");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			showError(e.getMessage());
		}
        return true;
    }
    
    private void exportToSVG(File file, Component component) throws FileNotFoundException {
    	FileOutputStream out = new FileOutputStream(file);
    	
    	// Get a DOMImplementation
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document
        Document document = domImpl.createDocument(null, "svg", null);

        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        RepaintManager rm = RepaintManager.currentManager(component);
        rm.setDoubleBufferingEnabled(false);
        component.paint(svgGenerator);
        rm.setDoubleBufferingEnabled(true);
        boolean useCSS = true; // we want to use CSS style attribute
        try {
			svgGenerator.stream(new OutputStreamWriter(out), useCSS);
		} catch (SVGGraphics2DIOException e) {
			e.printStackTrace();
			showError(e.getMessage());
		}
    }
    
    private void exportToBitmap(File file, Component comp, String format) throws FileNotFoundException {
    	FileOutputStream out = new FileOutputStream(file);
    	BufferedImage bImage = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bImage.getGraphics();
        
        RepaintManager rm = RepaintManager.currentManager(comp);
        rm.setDoubleBufferingEnabled(false);
        comp.paint(g);
        rm.setDoubleBufferingEnabled(true);

       	try {
			ImageIO.write(bImage, format, out);
		} catch (IOException e) {
			e.printStackTrace();
			showError(e.getMessage());
		}
    }
    
    private void showError(String message) {
    	JOptionPane.showMessageDialog(null, message, "Export failed", JOptionPane.ERROR_MESSAGE);
    }

}