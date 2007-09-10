/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.mobile.conzilla;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Command;

public class Content{

    String uri=null;
    String name=null;
    String mime=null;
   
    public Content(String u, String n, String m){
	uri=u;
	name = n;
	mime = m;
    }

    String getName(){
	return name;
    }

    String getURL(){
	return uri;
    }

    String getMimeType(){
	return mime;
    }

    void initiate(Forms F){
	if(mime.equals("wav") || mime.equals("mpg")){//Video:MPEG-1
	    //Ordna klaasen PlayerMIDlet
	    PlayerMIDlet play = new PlayerMIDlet(F,uri);
	    F.display.setCurrent(play.form);//OBS:s?tter till descBox ty jag t?mmer contentList.vec.removeAllElements(); varje gng
	    //F.message("Not supported");
	}else if(mime.equals("wcl")){//wireless concept language
	    F.initConn(uri);//F.initConn(uri);
	    F.initMap();
	}else
	    F.message("Not supported");
    }

}