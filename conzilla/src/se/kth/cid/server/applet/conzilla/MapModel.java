/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.applet.conzilla;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author enok
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MapModel {

    private LinkedList maps;
    private int index;

    public MapModel(Map map) {
        maps = new LinkedList();
        maps.add(map);
        index = 0;
    }

    public MapModel() {
        maps = new LinkedList();
    }

    public Map getCurrentMap() {
        if (maps.size() <= 0) {
            loadMap(null);
            index = 0;
        }
        return (Map) maps.get(index);
    }
    /*
     * Changes the model by deleting the whole forward-path.
     */
    public void loadMap(String URI) {
        System.out.println("Laddar karta: " + URI);
        LCPConnector con = new LCPConnector();
        con.setRequestMethod("GETMAP");
        con.setURI(URI);
        con.sendAndRecieveRequest();
        Map tmp = new Map(con.getString());
        if (maps.size() > 0) {
            if (((Map) maps.get(index)).getURI() == tmp.URI
                || ((Map) maps.get(index)).URI.equals(tmp.URI))
                return;
        }
        maps.add(tmp);
        index++;
    }

    public static void main(String[] argv) {
        (new MapModel()).loadMap("");
    }

    public Map getPrevious() {
        if (index <= 0) {
            index = 0;
            return null;
        }
        index--;
        return (Map) maps.get(index);
    }

    public Map getNext() {
        if (index >= maps.size() - 1) {
            index = maps.size() - 1;
            return null;
        }
        index++;
        return (Map) maps.get(index);
    }

    public boolean isCurrentFirst() {
        return index == 0;
    }

    public boolean isCurrentLast() {
        return index == maps.size() - 1;
    }

    public void removeForward() {
        for (int i = index; i < maps.size(); i++)
            maps.removeLast();
    }
}
