/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;

import java.awt.Component;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import se.kth.cid.conzilla.properties.ConzillaResourceManager;

/**
 * @author Mikael Nilsson
 * @version $Revision$
 */
public abstract class PriorityMenu extends JMenu {
	boolean needSort = false;

	static final String PRIORITY_PROP = "priority";

	static class PrioComparator implements Comparator {
		public int compare(Object a, Object b) {
			JComponent ac = (JComponent) a;
			JComponent bc = (JComponent) b;

			Integer aprio = (Integer) ac.getClientProperty(PRIORITY_PROP);
			Integer bprio = (Integer) bc.getClientProperty(PRIORITY_PROP);

			int ap = Integer.MAX_VALUE;
			int bp = Integer.MAX_VALUE;
			if (aprio != null)
				ap = aprio.intValue();
			if (bprio != null)
				bp = bprio.intValue();

			return ap - bp;
		}
	}

	static PrioComparator comparator = new PrioComparator();

	public PriorityMenu(String formalname, String resbundle) {
		setName(formalname);
		if (resbundle == null)
			resbundle = getClass().getName();
		ConzillaResourceManager.getDefaultManager().customizeButton(this,
				resbundle, getName());
		getPopupMenu().addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {
				//updateAfterPopup();
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				updateAfterPopup();
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				updateBeforePopup();
			}
		});
	}
	
	abstract public void updateBeforePopup();

	abstract public void updateAfterPopup();

	public PriorityMenu(String formalname) {
		this(formalname, null);
	}

	public int getPriority(JComponent c) {
		Integer prio = (Integer) c.getClientProperty(PRIORITY_PROP);
		return prio.intValue();
	}

	public void setPriority(JComponent c, int prio) {
		c.putClientProperty(PRIORITY_PROP, new Integer(prio));
		needSort = true;
	}

	protected void sortMenu() {
		if (!needSort) {
			return;
		}
		needSort = false;

		Component[] comps = getPopupMenu().getComponents();

		removeAll();
		Arrays.sort(comps, comparator);
		for (int i = 0; i < comps.length; i++) {
			add(comps[i]);
		}

	}

	public void addSeparator(int prio) {
		JSeparator js = new JSeparator();
		add(js);
		setPriority(js, prio);
	}
}
