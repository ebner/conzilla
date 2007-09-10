/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JWindow;

import se.kth.cid.conzilla.metadata.DescriptionPanel;
import se.kth.cid.conzilla.util.PopupWindowHandler;

/**
 * Extends PopupWindowHandler so that descriptions are displayed based on 
 * listitems in the ContentSelector.
 * 
 * @author Matthias Palmer
 */
public class PopupContentInfo extends PopupWindowHandler {
	ContentSelector selector;
	private int lastIndex;
	private JList list;
	
    public PopupContentInfo(ContentSelector sel, JList selComp) {
        super(new Content2JenaQueryTarget(), selComp);
        selector = sel;
        list = selComp;
    }

	protected Point getAdjustPosition(MouseEvent m, JWindow window) {
		return new Point(-4 -window.getSize().width, m.getY());
	}

	protected Point getComponentPosition(Component comp) {
		Point p = comp.getLocation();
		p.translate(comp.getSize().width, 0);
		return p;
	}

	protected Object getComponentFromTrigger(Object o) {
	    if (!(o instanceof MouseEvent))
	        return null;
	    lastIndex = list.locationToIndex(((MouseEvent) o).getPoint());
	    if (lastIndex != -1)
	        return selector.getContent(lastIndex);

	    return null;
	}
	protected void showNewDescriptionImpl(DescriptionPanel desc) {
		super.showNewDescriptionImpl(desc);
	    list.setSelectedIndex(lastIndex);
	}

	protected void activateOldDescriptionImpl(DescriptionPanel desc) {
		super.activateOldDescriptionImpl(desc);
	    list.setSelectedIndex(lastIndex);
	}
}