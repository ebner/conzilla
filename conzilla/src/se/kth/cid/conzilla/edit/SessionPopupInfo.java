/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JWindow;
import javax.swing.tree.TreePath;

import se.kth.cid.conzilla.util.PopupWindowHandler;

/**
 * Support meta-data popups for a session's Context-Maps and Contributions.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class SessionPopupInfo extends PopupWindowHandler {
	
    public SessionPopupInfo(SessionTree tree) {
        super(new SessionNode2JenaQueryTarget(), tree);
    }

    /**
     * @see se.kth.cid.conzilla.util.PopupHandler#getComponentFromTrigger(java.lang.Object)
     */
    protected Object getComponentFromTrigger(Object o) {
    	if (!(o instanceof MouseEvent)) {
            return null;
        }
        MouseEvent m = (MouseEvent) o;
        if (m.getComponent() instanceof SessionTree) {
        	SessionTree tree = (SessionTree) m.getComponent();
        	TreePath path = tree.getPathForLocation(m.getX(), m.getY());
        	if (path != null) {
        		SessionNode node = (SessionNode) path.getLastPathComponent();
        		if (!((node.getType() == SessionNode.TYPE_CONTEXTMAP) ||
        				(node.getType() == SessionNode.TYPE_CONTRIBUTION) ||
        				(node.getType() == SessionNode.TYPE_SESSION))) {
        			return null;
        		}
        		return node;
        	}
        }
        
        return null;
    }

    /**
     * @see se.kth.cid.conzilla.util.PopupWindowHandler#getAdjustPosition(java.awt.event.MouseEvent, javax.swing.JWindow)
     */
    protected Point getAdjustPosition(MouseEvent m, JWindow window) {
        return new Point(m.getComponent().getWidth() + 4, m.getY());
	}

	/**
	 * @see se.kth.cid.conzilla.util.PopupWindowHandler#getComponentPosition(java.awt.Component)
	 */
	protected Point getComponentPosition(Component comp) {
		return comp.getLocation();
	}
	
}