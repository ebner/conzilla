/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.EditMapManager;
import se.kth.cid.conzilla.map.MapDisplayer;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.properties.ColorTheme;
import se.kth.cid.util.Tracer;

public class GridLayer extends LayerComponent implements PropertyChangeListener {
	private static final long serialVersionUID = 1L;

	protected GridModel gridModel;

	protected MapController controller;

	protected BasicStroke stroke;

	public GridLayer(MapController controller) {
		super(controller, false);
		this.controller = controller;
		if (!(controller.getManager() instanceof EditMapManager))
			Tracer
					.bug("MapManager in controller isn't a EditMapManager despite the fact that we are in edit mode");
		this.gridModel = ((EditMapManager) controller.getManager())
				.getGridModel();
		float[] arr = { 0, 3 };
		stroke = new BasicStroke(1, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND, 0f, arr, 0f);
	}

	public boolean hasFixedLevel() {
		return true;
	}

	public Integer getFixedLevelForLayer() {
		return new Integer(MapScrollPane.MAP_LAYER.intValue() - 5);
	}

	public void activate(MapScrollPane pane) {
		ConfigurationManager.getConfiguration().addPropertyChangeListener(
				ColorTheme.COLORTHEME, this);

		gridModel.addPropertyChangeListener(this);
		revalidate();
	}

	public void deactivate(MapScrollPane pane) {
		gridModel.removePropertyChangeListener(this);
		ConfigurationManager.getConfiguration().removePropertyChangeListener(
				ColorTheme.COLORTHEME, this);
	}

	public void layerPaint(Graphics2D g, Graphics2D original) {
		if (gridModel.getGridLayout() == GridModel.STYLE_INVISIBLE
				|| !gridModel.isGridOn())
			return;
		g.setColor(ColorTheme.getColor(ColorTheme.Colors.FOREGROUND));
		switch (gridModel.getGridLayout()) {
		case GridModel.STYLE_CONTINUOUS:
			doPaintContinuous(g);
			break;
		case GridModel.STYLE_CORNERS:
			doPaintCorners(g);
			break;
		case GridModel.STYLE_DOTTED:
			Graphics2D g2 = (Graphics2D) g;
			Stroke s = g2.getStroke();
			g2.setStroke(stroke);
			doPaintContinuous(g2);
			g2.setStroke(s);
			break;
		default:
			doPaintContinuous(g);
		}
	}

	protected void doPaintContinuous(Graphics g) {
		MapDisplayer disp = controller.getView().getMapScrollPane().getDisplayer();
		double scale = disp.getScale();
		int gran = gridModel.getGranularity();
		Point origo = disp.getOriginLocation();
		gran = (int) (gran*scale);
		origo = new Point(origo.x%gran, origo.y%gran);

		Rectangle rect = getBounds();

		int step = origo.x;
		for (int i = 0; step <= rect.width; i++) {
			step = origo.x + (int) (i * gran);
			g.drawLine(step, 0, step, rect.height);
		}
		step = origo.y;
		for (int i = 0; step <= rect.height; i++) {
			step = origo.y + (int) (i * gran);
			g.drawLine(0, step, rect.width, step);
		}
	}

	protected void doPaintCorners(Graphics g) {
		MapDisplayer disp = controller.getView().getMapScrollPane().getDisplayer();
		double scale = disp.getScale();
		double gran = gridModel.getGranularity();
		Point origo = disp.getOriginLocation();
		gran = gran*scale;
		
		origo = new Point(origo.x-(int) (gran*(int) (origo.x/gran)), origo.y-(int) (gran*(int) (origo.y/gran)));

		Rectangle rect = getBounds();

		int stepx = origo.x;
		for (int i = 0; stepx <= rect.width; i++) {
			int stepy = origo.y;
			for (int j = 0; stepy <= rect.height; j++) {
				stepy = origo.y + (int) (j * gran);
				g.drawLine(stepx, stepy, stepx, stepy);
			}
			stepx = origo.x + (int) (i * gran);
		}
	}

	public void popupMenu(MapEvent m) {
	}

	public void propertyChange(PropertyChangeEvent pce) {
		MapDisplayer mapdisplayer = controller.getView().getMapScrollPane()
				.getDisplayer();
		mapdisplayer.repaint();
	}
}
