/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;

import se.kth.cid.conzilla.map.MapDisplayer;

public class FillLayout implements LayoutManager {
	public FillLayout() {
	}

	public void addLayoutComponent(String str, Component comp) {
	}

	public void removeLayoutComponent(Component co) {
	}

	public Dimension preferredLayoutSize(Container co) {
		int width = 0;
		int height = 0;

		Component[] comps = co.getComponents();

		for (int i = 0; i < comps.length; i++) {
			Point p = comps[i].getLocation();
			Dimension dim = comps[i].getPreferredSize();

			if (dim.width + p.x > width)
				width = dim.width + p.x;

			if (dim.height + p.y > height)
				height = dim.height + p.y;
		}

		return new Dimension(width, height);
	}

	public Dimension minimumLayoutSize(Container co) {
		return preferredLayoutSize(co);
	}

	public void layoutContainer(Container co) {
		Component[] comps = co.getComponents();
		Dimension dim = co.getSize();

		for (int i = 0; i < comps.length; i++) {
			Point p = comps[i].getLocation();
			Dimension d = comps[i].getPreferredSize();
			if (comps[i] instanceof MapDisplayer) {
				int diffX = dim.width - d.width;
				int diffY = dim.height - d.height;
				comps[i].setSize(d.width+(diffX / 2), d.height + (diffY / 2));
				((MapDisplayer) comps[i]).setOffset(new Point((int) (diffX / 2), (int) (diffY / 2)));
			}
			comps[i].setSize(new Dimension(dim.width - p.x, dim.height - p.y));
		}
	}
}
