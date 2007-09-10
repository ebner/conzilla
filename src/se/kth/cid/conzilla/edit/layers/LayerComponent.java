/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapScrollPane;

public abstract class LayerComponent extends JComponent {
	public boolean hasFixedLevel() {
		return false;
	}

	public Integer getFixedLevelForLayer() {
		return null;
	}

	protected MapController controller;

	boolean autoScale;

	LayerComponent(MapController controller, boolean autoScale) {
		this.controller = controller;
		this.autoScale = autoScale;
	}

	public abstract void activate(MapScrollPane pane);

	public abstract void deactivate(MapScrollPane pane);

	public void paint(Graphics g) {
		Graphics2D gr = (Graphics2D) g;
		Graphics2D original = (Graphics2D) gr.create();
		if (!autoScale)
			layerPaint(gr, original);
		else {
			AffineTransform transform = controller.getView().getMapScrollPane()
					.getDisplayer().getTransform();
			Shape clip = gr.getClip();
			AffineTransform f = gr.getTransform();

			gr.transform(transform);

			layerPaint(gr, original);
			super.paint(gr);
			gr.setClip(clip);
			gr.setTransform(f);
		}
	}

	public abstract void layerPaint(Graphics2D g, Graphics2D original);

	public abstract void popupMenu(MapEvent me);
}
