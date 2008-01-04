/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout.generic;
import se.kth.cid.component.ComponentManager;
import se.kth.cid.layout.BookkeepingConceptMap;
import se.kth.cid.layout.GroupLayout;
import se.kth.cid.layout.LayerLayout;
import se.kth.cid.util.TagManager;

/** 
 *
 *  @author Matthias Palmer
 *  @version $Revision$<
 */
public class MemGroupLayout extends MemResourceLayout implements GroupLayout, LayerLayout
{

    
    public MemGroupLayout(String id, BookkeepingConceptMap cMap, 
			  Object tag,
			  TagManager manager)
    {
	super(id, cMap, tag, manager);
    }    

    public boolean isLeaf()
    {
	return false;
    }
    
    public String getConceptUri()
    {
        return "";
    }

	public ComponentManager getComponentManager() {
		return null;
	}
}
    
