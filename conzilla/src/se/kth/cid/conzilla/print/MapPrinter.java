/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.print;

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

import javax.swing.KeyStroke;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.app.Extra;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.menu.DefaultMenuFactory;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.conzilla.util.ErrorMessage;

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
            p.setImageableArea(0, 0, p.getWidth(), p.getHeight());
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

            java.awt.Component vp = scrollPane.getViewport().getView();
            int w = vp.getSize().width;

            double scale = pf.getWidth() / w;
            ((Graphics2D) g).scale(scale, scale);

            vp.print(g);

            return Printable.PAGE_EXISTS;
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