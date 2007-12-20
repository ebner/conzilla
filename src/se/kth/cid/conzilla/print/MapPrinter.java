/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.print;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.net.URI;

import javax.swing.JFrame;
import javax.swing.KeyStroke;

import se.kth.cid.component.ComponentException;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.app.Extra;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapDisplayer;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.map.MapStoreManager;
import se.kth.cid.conzilla.menu.DefaultMenuFactory;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.layout.ContextMap;

public class MapPrinter implements Extra {
    class Printer implements Printable, Pageable {
        MapScrollPane scrollPane;

        public Printer(MapScrollPane scrollPane) {
            this.scrollPane = scrollPane;
        }

        public int getNumberOfPages() {
            return 1;
        }

        public PageFormat getPageFormat(int i) {
            PageFormat pf = new PageFormat();
            Paper p = pf.getPaper();
            p.setImageableArea(28, 28, p.getWidth()-56, p.getHeight()-56); //1cm border everywhere, 
            // where 56 ~= 20 (mm) / 25.4 (mm/inch) * 72 (pixels/inch)
            pf.setPaper(p);
            return pf;
        }

        public Printable getPrintable(int i) {
            return this;
        }

        public int print(Graphics g, PageFormat pf, int pageIndex)
                throws PrinterException {
            if (pageIndex >= 1)
                return Printable.NO_SUCH_PAGE;

			try {
	            ConzillaKit kit = ConzillaKit.getDefaultKit();
	            ContextMap cMap = scrollPane.getDisplayer().getStoreManager().getConceptMap();
	            URI mapURI = URI.create(cMap.getURI());
				MapStoreManager manager = new MapStoreManager(mapURI, kit.getResourceStore(), 
						kit.getStyleManager(), null);
	            MapDisplayer mapD = new MapDisplayer(manager);
	            MapScrollPane mapSP = new MapScrollPane(mapD);
	            mapD.resizeMap();
	            Dimension dim = mapSP.getPreferredSize();
	            double scale = ((double) pf.getImageableWidth()) / ((double) dim.width);
	            mapSP.setScale(scale);
	            Dimension finalDim = mapSP.getPreferredSize();
	            mapSP.setSize(finalDim);
	            
	            JFrame frame = new JFrame();
	            frame.getContentPane().add(mapSP);
	            frame.pack();
	            frame.setVisible(true);
	            
	            g.translate((int) pf.getImageableX(), (int) pf.getImageableY());
	            g.setClip(0, 0, finalDim.width, finalDim.height);
	            mapSP.getViewport().getView().print(g);
	            frame.dispose();
	            return Printable.PAGE_EXISTS;

			} catch (ComponentException e) {
				e.printStackTrace();
			}
            return Printable.NO_SUCH_PAGE;
        }
    }

    public MapPrinter() {
    }

    public String getName() {
        return "MapPrinter";
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
            Tool t = new Tool("PRINT", MapPrinter.class.getName()) {
                public void actionPerformed(ActionEvent ae) {
                    try {
                        print(c.getView().getMapScrollPane());
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
            t.setIcon(Images.getImageIcon(Images.ICON_FILE_PRINT));
            t.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                    Event.CTRL_MASK));
            menu.addTool(t, 350);
            menu.addSeparator(351);
        }
    }

    public boolean print(MapScrollPane sp) throws PrinterException {
        // Get a PrinterJob
        PrinterJob job = PrinterJob.getPrinterJob();

        job.setPageable(new Printer(sp));

        // Put up the dialog box
        if (job.printDialog()) {
            // Print the job if the user didn't cancel printing
            job.print();
            return true;
        }
        return false;
    }

}