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
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.RepaintManager;
import javax.swing.filechooser.FileFilter;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.json.JSONException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import se.kth.cid.config.Config;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.app.Extra;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.map.MapStoreManager;
import se.kth.cid.conzilla.menu.DefaultMenuFactory;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsMenu;

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
	
	private class BMPFileFilter extends FileFilter {
        public boolean accept(File f) {
        	return f.getName().toLowerCase().endsWith(".bmp") || f.isDirectory();
        }
        public String getDescription() {
            return "Bitmap (*.bmp)";
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

	private class JSONFileFilter extends FileFilter {
        public boolean accept(File f) {
            return f.getName().toLowerCase().endsWith(".json") || f.isDirectory();
        }
        public String getDescription() {
            return "JavaScript Object Notation (*.json)";
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
    				export(c.getView().getMapScrollPane());
    			}
    		};
    		t.setIcon(Images.getImageIcon(Images.ICON_FILE_EXPORT));
    		t.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));
    		menu.addTool(t, 345);
    	}
    }
    
    private boolean supportsFormat(String extension) {
    	List<String> formats = Arrays.asList(ImageIO.getWriterFormatNames());
    	if (formats.contains(extension.toLowerCase())) {
    		return true;
    	}
    	return false;
    }

    private String getLocation() {
    	Config config = ConfigurationManager.getConfiguration();
    	String location = config.getString(Settings.CONZILLA_EXPORT_PATH);
    	if (location != null) {
    		File file = new File(location);
    		if (!file.exists() || !file.isDirectory()) {
    			location = null;
    		}
    	}
    	return location;
    }

    private void storeLocation(String path) {
    	Config config = ConfigurationManager.getConfiguration();
    	config.setProperty(Settings.CONZILLA_EXPORT_PATH, path);
    }

    public void export(MapScrollPane sp) {
    	try {
			JFileChooser fc = new JFileChooser(getLocation());
            
			if (supportsFormat("bmp")) {
				fc.addChoosableFileFilter(new BMPFileFilter());
			}
			
			if (supportsFormat("jpg")) {
				fc.addChoosableFileFilter(new JPGFileFilter());
			}
			
			JSONFileFilter jsonFilter = new JSONFileFilter();
			fc.addChoosableFileFilter(jsonFilter);
						
			SVGFileFilter svgFilter = new SVGFileFilter();
			fc.addChoosableFileFilter(svgFilter);
			
			if (supportsFormat("png")) {
				PNGFileFilter pngFilter = new PNGFileFilter();
				fc.addChoosableFileFilter(pngFilter);
				fc.setFileFilter(pngFilter);
			}
	        
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
					if (canWriteFile(file)) {
						exportToSVG(file, comp);
					}
				} else if (selectedFilter instanceof PNGFileFilter) {
					if (!fileString.endsWith(".png")) {
						file = new File(fileString + ".png");
					}
					if (canWriteFile(file)) {
						exportToBitmap(file, comp, "png");
					}
				} else if (selectedFilter instanceof JPGFileFilter) {
					if (!fileString.endsWith(".jpg") && !fileString.endsWith(".jpeg")) {
						file = new File(fileString + ".jpg");
					}
					if (canWriteFile(file)) {
						exportToBitmap(file, comp, "jpg");
					}
				} else if (selectedFilter instanceof BMPFileFilter) {
					if (!fileString.endsWith(".bmp")) {
						file = new File(fileString + ".bmp");
					}
					if (canWriteFile(file)) {
						exportToBitmap(file, comp, "bmp");
					}
				} else if (selectedFilter instanceof JSONFileFilter) {
					if (!fileString.endsWith(".json")) {
						file = new File(fileString + ".json");
					}
					if (canWriteFile(file)) {
						exportToJSON(file, sp);
					}
				}
				
				String parentFolder = file.getParent();
				if (parentFolder != null) {
					storeLocation(parentFolder);
				}
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			showError(fnfe.getMessage());
		} catch (JSONException jsone) {
			jsone.printStackTrace();
			showError(jsone.getMessage());
		}
    }
    
    private boolean canWriteFile(File file) {
		if (file.exists()) {
			int overwrite = JOptionPane.showConfirmDialog(null,
					"A file named \"" + file.getName() + "\" already exists.\nDo you want to replace it?",
					"Overwrite file?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (overwrite != JOptionPane.YES_OPTION) {
				return false;
			}
		}
		return true;
    }
    
    private void exportToSVG(File file, Component component) throws FileNotFoundException {
    	FileOutputStream out = new FileOutputStream(file);
    	BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(out));
    	
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
			svgGenerator.stream(bf, useCSS);
		} catch (SVGGraphics2DIOException e) {
			e.printStackTrace();
			showError(e.getMessage());
		} finally {
			try {
				bf.close();
			} catch (IOException ignored) {
			}
		}
    }
    
    private void exportToBitmap(File file, Component comp, String format) throws FileNotFoundException {
    	OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
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
		} finally {
			try {
				out.close();
			} catch (IOException ignored) {
			}
		}
    }

    private void exportToJSON(File file, MapScrollPane spane) throws FileNotFoundException, JSONException {
    	FileOutputStream out = new FileOutputStream(file);
    	BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(out));
    	MapStoreManager sman = spane.getDisplayer().getStoreManager();
    	try {
        	bf.write(new se.kth.cid.json.Export(sman.getConceptMap(), sman.getConcepts()).toString());
    	} catch (IOException e) {
    		e.printStackTrace();
    		showError(e.getMessage());
		} finally {
			try {
    			bf.close();
    		} catch (IOException ignored) {
    		}
		}
    }

    private void showError(String message) {
    	JOptionPane.showMessageDialog(null, message, "Export failed", JOptionPane.ERROR_MESSAGE);
    }

}