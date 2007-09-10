/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;
import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import se.kth.cid.layout.ContextMap;

/** 
 *  @author Matthias Palmer
 *  @version $version: $
 */
public abstract class AbstractHandle implements Handle
{
    static final BasicStroke thinStroke = new BasicStroke(1f, BasicStroke.CAP_ROUND,
						       BasicStroke.JOIN_ROUND);
    static final BasicStroke thickStroke = new BasicStroke(2f, BasicStroke.CAP_ROUND,
						       BasicStroke.JOIN_ROUND);
    public static int radius=4;

    ContextMap.Position originalPosition;
    Object parent=null;
    boolean edited=false;
    boolean selected;    

    //Some simple set/get/clear/query functions
    public void clearEdited()                        {originalPosition = getPosition();edited=false;}
    public boolean isEdited()                           {return edited;}
    public ContextMap.Position getOriginalPosition() {return originalPosition;}
    public void setSelected(boolean selected)        {this.selected=selected;}
    public boolean isSelected()                      {return selected;}
    public void setParent(Object parent)             {this.parent=parent;}
    public Object getParent()                        {return parent;}
    public static void setRadius(int r)              {radius=r;}
    public static int getRadius()                    {return radius;} 


    protected Collection followHandles=null;
        
    public Collection dragUnTied(int x, int y) {
        return dragForced(x, y);
    }
    
    
    /** If there are any followers, this function moves them, 
     *  inteded to be called from within the @link AbstractHandle.drag function.
     *
     *  @see AbstractHandle#dragFollowers(int, int)
     */
    public Collection dragFollowers(int x, int y)
    {
	Collection cols=new Vector();
	Collection col;
	if (followHandles != null)
	    {
		Iterator it=followHandles.iterator();
		while (it.hasNext())
		    {
			Handle followhandle = (Handle) it.next();
			if (followhandle==this)
			    continue;
			col = followhandle.dragForced(x, y);
			if (col!=null)
			    cols.add(col);
		    }
	    }
	return cols;
    }    

/*    public Collection setFollowersSelected(boolean select)
    {
	Collection cols=new Vector();
	Collection col;
	if (followHandles != null)
	    {
		Iterator it=followHandles.iterator();
		while (it.hasNext())
		    {
			Handle followhandle = (Handle) it.next();
			if (followhandle==this)
			    continue;
			cols.add(followhandle.getBounds());
			followhandle.setSelected(select);
			//			col = followhandle.setFollowersSelected(selected);
			//if (col!=null)
			//cols.addAll(col);
		    }
	    }
	return cols;
    }	*/
    /*    public void addFollowers(Collection fols)
    {
	if (followHandles!=null)
	    followHandles.addAll(fols);
	else
	    {
		followHandles= new HashSet();
		followHandles.addAll(fols);
	    }
	    }*/
    public void setFollowers(Collection fols)
    {
	followHandles = fols;
    }
    public Collection getFollowers()
    {
	return followHandles;
    }
    public Rectangle getBounds()
    {return null;}
}
