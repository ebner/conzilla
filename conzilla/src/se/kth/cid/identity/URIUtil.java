/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.identity;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.server.UID;
import java.util.StringTokenizer;

/**
 * Utilities for generating URIs.
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public class URIUtil {

    static String host = null;
    static long lastTime = System.currentTimeMillis();

    /**
     * Does some mangling of the identifier provided by the UID class.
     * Partly due to an old bug in java.
     * 
     * @return a String as a long number of numbers.
     */
    public static String createUniqueID() {
        if (host == null) {
            UID u = new UID();
            StringTokenizer st = new StringTokenizer(u.toString(), ":");
            host = st.nextToken();
        }

        long newTime = System.currentTimeMillis();
        if (newTime != lastTime) {
            lastTime = newTime;
            return host + Long.toString(newTime, 16);
        } else {
            UID u = new UID();
            StringTokenizer st = new StringTokenizer(u.toString(), ":");
            return st.nextToken()
                + st.nextToken()
                + Integer.toString(
                    Short.parseShort(st.nextToken(), 16) - Short.MIN_VALUE,
                    16);
        }
    }
    
    public static String createUniqueURIFromBase(String base) {
        return base + "#" + createUniqueID();
    }
    
    public static String getParentDirectory(String uri) {
        if (uri.endsWith("/")) {
            int lastSlash = uri.substring(0,uri.length()).lastIndexOf('/');
            return uri.substring(0, lastSlash + 1);            
        } else {
            int lastSlash = uri.lastIndexOf('/');
            return uri.substring(0, lastSlash + 1);
        }
    }
    
	public static String getParentURI(String uri) {
		String tmp = uri;
		if (tmp.endsWith("/")) {
			tmp = tmp.substring(0, tmp.length() - 2);
		}
		if (tmp.indexOf("/") == tmp.lastIndexOf("/")) {
			return null;
		}
		return tmp.substring(0, tmp.lastIndexOf("/"));
	}
    
    public static URI getParentDirectory(URI uri) {
    	try {
			return new URI(getParentDirectory(uri.toString()));
		} catch (URISyntaxException e) {
		}
		return null;
    }
}
