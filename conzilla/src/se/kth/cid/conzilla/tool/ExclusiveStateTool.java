/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.tool;


public abstract class ExclusiveStateTool extends StateTool
{
    public ExclusiveStateTool(String name, boolean init)
    {
	super(name, init);
    }

    public ExclusiveStateTool(String name, String resbundle, boolean init)
    {
	super(name, resbundle, init);
    }
}
