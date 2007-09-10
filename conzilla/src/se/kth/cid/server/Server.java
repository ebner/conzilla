/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server;

public interface Server{
    public void initialize();
    public void start();
    public void stop();
    public void await();
    public int getPort();
    public String getShutdown();
}
