/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.mobile.conzilla;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Display;

public class Initiate extends MIDlet {
    
    Forms f;
    
    public Initiate() {
	f = new Forms(this);
    }
    
    public void startApp() {
	f.current = f.wForm;
	Display.getDisplay(this).setCurrent(f.wForm);
    }
    
    public void pauseApp() { }
    
    public void destroyApp(boolean unc) { 
	//exit();
    }
    
    public void exit(boolean b){
	destroyApp(b);
    }
    
}