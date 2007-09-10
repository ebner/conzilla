/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.app;

import se.kth.cid.conzilla.print.MapPrinter;
import se.kth.cid.conzilla.view.CanvasTabManager;

/**
 * This class depends on a typicall setup for Applet Conzilla.
 * Usefull as a starting point for dependency calculation.
 * 
 * @author matthias
 */
public class ConzillaAppletDependencies {

    public static void main(String[] args) {
        new ConzillaApplet();
        new MapPrinter();
        new CanvasTabManager();
    }
}
