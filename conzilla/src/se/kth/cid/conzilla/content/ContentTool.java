/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;
import se.kth.cid.conzilla.tool.Tool;

/**
 *
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public abstract class ContentTool extends Tool 
{
    protected int contentIndex;

    public ContentTool(String name, String resbundle)
    {
	super(name, resbundle);
    }  
    
    public final void update(int contentIndex)
    {
	this.contentIndex = contentIndex;
	setEnabled(updateEnabled());
    }
    
    protected boolean updateEnabled()
    {
	return true;
    }
}
