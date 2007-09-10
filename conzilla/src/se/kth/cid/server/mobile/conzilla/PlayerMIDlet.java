/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.mobile.conzilla;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
//import javax.microedition.media.Control;
import javax.microedition.media.Player;
import javax.microedition.media.MediaException;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.Manager;
//import javax.microedition.media.;
import javax.microedition.media.control.VideoControl;
import javax.microedition.midlet.*;

public class PlayerMIDlet implements CommandListener, PlayerListener, Runnable {
    Displayable prev;
    
    private Display display;
    //private Form form;
    Form form,v;
    private TextField url;
    private Command stop = new Command("Stop",  Command.SCREEN, 2);
    private Player player;
    Forms F;  
    public PlayerMIDlet(Forms fs, String URL) {
	//display = Display.getDisplay(this);
	display = fs.display;
	form = new Form("Demo Player");
	F = fs;
	String image1 = "http://java.sun.com/products/java-media/mma/media/test-wav.wav";
	String image2 = "http://java.sun.com/products/java-media/mma/media/test-mpeg.mpg";
	String image3 = "http://libpng.sourceforge.net/pngbar.png";
	url = new TextField("Enter URL:", URL, 100, TextField.URL);
	//url = new TextField("Enter URL:", image1, 100, TextField.URL);
	form.append(url);
	form.addCommand(F.GO);
	form.addCommand(stop);
	form.addCommand(F.BACK);
	form.setCommandListener(this);
	//display.setCurrent(form);

	v = new Form("Video");
	v.addCommand(F.BACK);
	v.setCommandListener(this);
    }

    protected void startApp() {
	try {
	    if(player != null && player.getState() == Player.PREFETCHED) {
		player.start();
	    } else {
		defplayer();
		display.setCurrent(form);
	    }
	} 
	catch(MediaException me) {
	    reset();
	}
    }

    protected void pauseApp() {
	try {
	    if(player != null && player.getState() == 
	       Player.STARTED) {
		player.stop();
	    } else {
		defplayer();
	    }
	} 
	catch(MediaException me) {
	    reset();
	}
    }

    protected void destroyApp(
			      boolean unconditional) {
	form = null;
	try {
	    defplayer();
	} 
	catch(MediaException me) {
	}
    }

    public void playerUpdate(Player player, 
			     String event, Object data) {
	if(event == PlayerListener.END_OF_MEDIA) {
	    try {
		defplayer();
	    } 
	    catch(MediaException me) {
	    }
	    reset();
	}
    }
    
   

    public void commandAction(Command c, Displayable d) {
	F.print("Command: " + c.toString());
	if(c == F.GO) {       
	    start();
	    prev = F.previous;
	} else if(c == stop) {
	    stopPlayer();
	}else if(c == F.BACK) {
	    if(d == form){
		display.setCurrent(prev);
		prev = null;
	    }else if(d == v){
		display.setCurrent(form);
	    }
	}
	System.gc();
    }

    public void start() {
	F.print("i start: ");
	Thread t = new Thread(this);
	t.start();
    }

    // to prevent blocking, all communication should 
    // be in a thread
    // and not in commandAction
    public void run() {
	play(getURL());
    }

    String getURL() { 
	return url.getString();
    }

    void play(String url) {
	F.print("i play");
	try {
	    F.print("i play Try 1");
	    VideoControl vc;
	    defplayer();
	    F.print("i play Try 2");
	    // create a player instance
	    player = Manager.createPlayer(url);
	    F.print("i play Try 3");
	    player.addPlayerListener(this);
	    F.print("i play Try 4");
	    // realize the player
	    player.realize();
	    F.print("i play Try 5");
	    vc = (VideoControl)player.getControl("VideoControl");
	    F.print("i play Try 6");
	    if(vc != null) {
		F.print("i play Try if");
		Item video = (Item)vc.initDisplayMode(vc.USE_GUI_PRIMITIVE, null);
            
		StringItem si = new StringItem("Status: ","Playing...");
	    
		v.append(si);
		v.append(video);
		display.setCurrent(v);
	    }else{
		F.print("i play Try if else");
		F.message("Video Doesn't work");
	    }
	    player.prefetch();
	    player.start();
	}
	catch(Throwable t) {
	    F.print("i play Try catch");
	    reset();
	}
    }

    void defplayer() throws MediaException {
	if (player != null) {
	    if(player.getState() == Player.STARTED) {
		player.stop();
	    }
	    if(player.getState() == Player.PREFETCHED) {
		player.deallocate();
	    }
	    if(player.getState() == Player.REALIZED || 
	       player.getState() == Player.UNREALIZED) {
		player.close();
	    }
	}
	player = null;
    }

    void reset() {
	player = null;
    }

    void stopPlayer() {
	try {
	    defplayer();
	} 
	catch(MediaException me) {
	}
	reset();
    }
}