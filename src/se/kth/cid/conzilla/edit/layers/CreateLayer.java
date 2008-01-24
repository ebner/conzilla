/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.KeyStroke;
import javax.swing.event.MouseInputAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.LayoutUtils;
import se.kth.cid.conzilla.edit.layers.handles.HandledObject;
import se.kth.cid.conzilla.edit.menu.TypeMenu;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapMouseInputListener;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.conzilla.util.TreeTagNodeMenuListener;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.tree.TreeTagNode;
import se.kth.cid.util.AttributeEntryUtil;

import com.hp.hpl.jena.vocabulary.RDF;

/**
 * A user friendly layer for creation of concepts. Boxes are created via
 * clicking in the background, relations via clicking on two or more boxes or
 * relations of other types then the one you are trying to create. Clicking on a
 * existing concept of the same type will, if the concepttype has triples, allow
 * you to continue to add new triples on that concept (i.e. a relation). When
 * creating triples, clicking in thebackground will abort.
 * 
 * @author Matthias Palmer
 */
public class CreateLayer extends LayerComponent implements MapMouseInputListener, TreeTagNodeMenuListener {
	
	static Log log = LogFactory.getLog(CreateLayer.class);

	protected GridModel gridModel;

	protected MapEvent current;

	protected MapEvent oldcurrent;

	MouseInputAdapter createMenuListener;

	JMenu typeMenu;

	MapObject mapObject;

	MapEvent subjectMapEvent;

	DrawerLayout layout;

	ContextMap.Position startPoint;

	CreateStateControl stateControl;

	String drawName;

	Graphics savedg;

	int width;

	int oldwidth;

	protected static final KeyStroke space = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, 0);

	protected AbstractAction spaceListener;

	InputMap iMap;

	ActionMap aMap;

	ActionListener oneAtATimeAction;

	public CreateLayer(MapController controller, GridModel gridModel) {
		super(controller, true);
		this.gridModel = gridModel;
		stateControl = new CreateStateControl(controller);
		drawName = stateControl.getDrawName();
		width = 70;

		iMap = new InputMap();
		aMap = new ActionMap();
		spaceListener = new AbstractAction() {
			public void actionPerformed(ActionEvent ae) {
				draw(current);
			}
		};

		String spaceObject = "space";

		iMap.put(space, spaceObject);
		aMap.put(spaceObject, spaceListener);

		TreeTagNode ttn = ConzillaKit.getDefaultKit().getRootLibrary();

		typeMenu = new TypeMenu(ttn, this);
	}

	public void setCreateOneAtATimeAction(ActionListener al) {
		oneAtATimeAction = al;
	}

	public MapObject getCreatedMapObject() {
		return mapObject;
	}

	public void activate(MapScrollPane pane) {
//		MapDisplayer mapdisplayer = pane.getDisplayer();
//		ContextMap.Dimension dim = mapdisplayer.getStoreManager().getConceptMap().getDimension();

		InputMap iMapOld = pane.getDisplayer().getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap aMapOld = pane.getDisplayer().getActionMap();

		aMap.setParent(aMapOld);
		iMap.setParent(iMapOld);

		pane.getDisplayer().setInputMap(JComponent.WHEN_FOCUSED, iMap);
		pane.getDisplayer().setActionMap(aMap);
		revalidate();
	}

	public void deactivate(MapScrollPane pane) {
		InputMap iMapOld = pane.getDisplayer().getInputMap(JComponent.WHEN_FOCUSED).getParent();
		ActionMap aMapOld = pane.getDisplayer().getActionMap().getParent();
		pane.getDisplayer().setInputMap(JComponent.WHEN_FOCUSED, iMapOld);
		pane.getDisplayer().setActionMap(aMapOld);
		clearState();
	}

	private void clearState() {
		stateControl.clearState();
		startPoint = null;
	}

	private void draw(MapEvent m) {
		drawName = stateControl.getDrawName();
		oldcurrent = current;
		oldwidth = width;
		current = m;
		if (savedg != null)
			width = savedg.getFontMetrics().stringWidth(drawName);

		AffineTransform transform = controller.getView().getMapScrollPane().getDisplayer().getTransform();
		
		if (oldcurrent != null) {
			if (startPoint != null) {
				Rectangle rect1 = new Rectangle(startPoint.x, startPoint.y, oldcurrent.mapX - startPoint.x, oldcurrent.mapY - startPoint.y);
				rect1 = HandledObject.positiveRectangle(rect1);
				rect1 = new Rectangle(rect1.x -1, rect1.y -1, rect1.width +2, rect1.height+2);
				rect1 = transform.createTransformedShape(rect1).getBounds();
				repaint(rect1);
			}
			Rectangle rect = new Rectangle(oldcurrent.mapX-2, oldcurrent.mapY-14, oldwidth+6, 18);
			rect = transform.createTransformedShape(rect).getBounds();
			repaint(rect);
		}

		if (current != null) {
			if (startPoint != null) {
				if (stateControl.getState() == CreateStateControl.WAITING_FOR_OBJECT_STATE) {
					DrawerLayout firstEnd = subjectMapEvent.mapObject.getDrawerLayout();
					ContextMap.Position p = new ContextMap.Position(current.mapX, current.mapY);
					startPoint = LayoutUtils.findPosition_FirstFromBody(firstEnd, p, gridModel);
				}

				Rectangle rect2 = new Rectangle(startPoint.x, startPoint.y, current.mapX - startPoint.x, current.mapY - startPoint.y);
				rect2 = se.kth.cid.conzilla.edit.layers.handles.HandledObject.positiveRectangle(rect2);
				rect2 = new Rectangle(rect2.x -1, rect2.y -1, rect2.width +2, rect2.height+2);
				rect2 = transform.createTransformedShape(rect2).getBounds();
				
				repaint(rect2);
			}
			Rectangle rect = new Rectangle(current.mapX-2, current.mapY-14, width+6, 18);
			rect = transform.createTransformedShape(rect).getBounds();
			repaint(rect);
		}
	}

	private void undraw(MapEvent m) {
		oldcurrent = current;
		current = null;

		if (oldcurrent != null) {
			AffineTransform transform = controller.getView().getMapScrollPane().getDisplayer().getTransform();

			if (startPoint != null) {
				Rectangle rect1 = new Rectangle(startPoint.x, startPoint.y, oldcurrent.mapX - startPoint.x, oldcurrent.mapY - startPoint.y);
				rect1 = HandledObject.positiveRectangle(rect1);
				rect1 = new Rectangle(rect1.x -1, rect1.y -1, rect1.width +2, rect1.height+2);
				rect1 = transform.createTransformedShape(rect1).getBounds();
				repaint(rect1);
			}
			Rectangle rect = new Rectangle(oldcurrent.mapX-2, oldcurrent.mapY-14, oldwidth+6, 18);
			rect = transform.createTransformedShape(rect).getBounds();
			repaint(rect);
		}
	}

	public void mouseMoved(MapEvent m) {
		stateControl.changeState(m);
		draw(m);
	}

	public void mouseDragged(MapEvent m) {
		stateControl.changeState(m);
		draw(m);
	}

	public void mouseClicked(MapEvent m) {
		if (!stateControl.isReady()) {
			stateControl.changeState(m);
			draw(m);
			return;
		}
		controller.getConceptMap().getComponentManager().getUndoManager().startChange();

		boolean ctrlIsDown = (m.mouseEvent.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) > 0;
		switch (stateControl.changeState(m)) {
		case CreateStateControl.POSITION_CHOOSEN_FOR_CONCEPT:
			createConcept(m);
			startPoint = null;
			subjectMapEvent = null;

			if (!ctrlIsDown && oneAtATimeAction != null) {
				current = null;
				oneAtATimeAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "concept"));
			}
			break;
		case CreateStateControl.SUBJECT_CHOOSEN_FOR_TRIPLE:
			registerSubject(m);
			break;
		case CreateStateControl.OBJECT_CHOOSEN_FOR_TRIPLE:
			registerObject(m);
			startPoint = null;
			subjectMapEvent = null;
			if (!ctrlIsDown && oneAtATimeAction != null) {
				current = null;
				oneAtATimeAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "concept relation"));
			}
			break;
		case CreateStateControl.CREATE_NEW_LITERAL:
			registerLiteral(m);
			startPoint = null;
			subjectMapEvent = null;
			if (!ctrlIsDown && oneAtATimeAction != null) {
				current = null;
				oneAtATimeAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "literal"));
			}
			break;
		default:
			startPoint = null;
			subjectMapEvent = null;
		}
		controller.getConceptMap().getComponentManager().getUndoManager().endChange();
	}

	public void mousePressed(MapEvent m) {
		stateControl.changeState(m);
		draw(m);
	}

	public void mouseReleased(MapEvent m) {
		stateControl.changeState(m);
		draw(m);
	}

	public void mouseEntered(MapEvent m) {
		stateControl.changeState(m);
		draw(m);
	}

	public void mouseExited(MapEvent m) {
		stateControl.changeState(m);
		undraw(m);
	}

	public void layerPaint(Graphics2D g, Graphics2D original) {
		savedg = g;

		if (current != null) {
			if (startPoint != null)
				g.drawLine(startPoint.x, startPoint.y, current.mapX, current.mapY);
			g.setColor(Color.white);
			g.fillRect(current.mapX - 2, current.mapY - 12, width + 4, 14);
			g.setColor(Color.gray);
			g.drawRect(current.mapX - 2, current.mapY - 12, width + 4, 14);
			g.setColor(Color.black);
			g.drawString(drawName, current.mapX , current.mapY);
		}
	}

	public void selected(TreeTagNode ttn) {
		clearState();
		stateControl.setTemplateType(ttn);
		drawName = stateControl.getDrawName();
	}

	public void registerSubject(MapEvent mapEvent) {
		startPoint = new ContextMap.Position(mapEvent.mapX, mapEvent.mapY);
		subjectMapEvent = mapEvent;
		// firstTripleType = stateControl.getCurrentTripleTypeBeforeClick();
	}

	public void registerObject(MapEvent mapEvent) {
		Concept createConcept = createConcept(this, stateControl, false, false);
		if (createConcept == null) // Should never happen...
			return;
		// firstTriple = addTriple(createConcept,
		// subjectMapEvent.mapObject.getConcept(), firstTripleType);

		createConcept.createTriple(subjectMapEvent.mapObject.getConcept().getURI(), stateControl.getTypeURI().toString(),
				mapEvent.mapObject.getConcept().getURI(), false);

		DrawerLayout sdl = subjectMapEvent.mapObject.getDrawerLayout();
		DrawerLayout odl = mapEvent.mapObject.getDrawerLayout();
		layout = insertStatementInMap(createConcept, sdl, odl);
		layout.setBodyVisible(false);

		ContextMap.Position[] pos = new ContextMap.Position[2];
		pos[1] = startPoint;
		pos[0] = LayoutUtils.findPosition_FirstFromBody(odl, startPoint, gridModel);
		((StatementLayout) layout).setLine(pos);
	}

	public void registerLiteral(MapEvent mapEvent) {
		Concept createConcept = createConcept(this, stateControl, false, false);
		if (createConcept == null) // Should never happen...
			return;
		// firstTriple = addTriple(createConcept,
		// subjectMapEvent.mapObject.getConcept(), firstTripleType);

		createConcept.createTriple(subjectMapEvent.mapObject.getConcept().getURI(), stateControl.getTypeURI().toString(), "new literal",
				true);

		DrawerLayout sdl = subjectMapEvent.mapObject.getDrawerLayout();
		// DrawerLayout odl = mapEvent.mapObject.getDrawerLayout();
		layout = insertStatementInMap(createConcept, sdl, null);
		layout.setBodyVisible(false);

		java.awt.Dimension dim = new java.awt.Dimension(50, 20);

		((StatementLayout) layout).setLiteralBoundingBox(LayoutUtils.preferredBoxOnGrid(gridModel, mapEvent.mapX, mapEvent.mapY, dim));

		ContextMap.Position[] pos = new ContextMap.Position[2];
		pos[1] = startPoint;
		pos[0] = LayoutUtils.findPosition_FromLiteral((StatementLayout) layout, startPoint, gridModel);
		((StatementLayout) layout).setLine(pos);
	}

	private void createConcept(MapEvent mapEvent) {
		Concept createConcept = createConcept(this, stateControl, true, true);
		insertConceptInMap(mapEvent, createConcept);
	}

	private void insertConceptInMap(MapEvent mapEvent, Concept concept) {
		if (concept == null) // Should never happen...
			return;

		try {
			ConceptLayout ns = controller.getConceptMap().addConceptLayout(concept.getURI());
			mapObject = controller.getView().getMapScrollPane().getDisplayer().getMapObject(ns.getURI());
			Dimension dim = mapObject.getPreferredSize();
			if (dim.width == 0)
				dim.width = 50;
			if (dim.height == 0)
				dim.height = 20;

			ns.setBoundingBox(LayoutUtils.preferredBoxOnGrid(gridModel, mapEvent.mapX, mapEvent.mapY, dim));
		} catch (InvalidURIException ex) {
			log.error("Invalid URI", ex);
		}
	}

	public StatementLayout insertStatementInMap(Concept concept, DrawerLayout sdl, DrawerLayout odl) {
		ContextMap cmap = controller.getConceptMap();

		StatementLayout ns = null;
		try {
			ns = cmap.addStatementLayout(concept.getURI(), sdl.getURI(), odl != null ? odl.getURI() : null);
			mapObject = controller.getView().getMapScrollPane().getDisplayer().getMapObject(ns.getURI());
		} catch (InvalidURIException ex) {
			log.error("Invalid URI", ex);
		}

		return ns;
	}

	// Help functions below.
	protected static Concept createConcept(java.awt.Component jc, CreateStateControl sc, boolean includeType,
			boolean includeTitle) {
		ConzillaKit kit  = ConzillaKit.getDefaultKit();
		log.debug("createConcept 1");
		URI typeURI = null;
		if (includeType)
			typeURI = sc.getTypeURI();

		if (!sc.typeOK())
			return null;

//		URI uri = null;
//		Object[] createRet = null;
		Concept concept = null;

		try {
			concept = (Concept) kit.getResourceStore().getComponentManager().createConcept(null);
			
			//TODO set session...
			concept.addAttributeEntry(RDF.type.toString(), typeURI);
			//unset session.
			
			log.debug("Concept created as " + concept.getURI());
			if (includeTitle) {
				adjustMetaData(concept, sc, includeType);
			}
			// fixData(n, nType);
			// kit.getComponentStore().getCache().referenceComponent(concept);
			concept.setEdited(true);
		} catch (ComponentException e) {
			ErrorMessage.showError("Create Error", "Failed to create component.", e, jc);
			return null;
		}

		return concept;
	}

	protected static void adjustMetaData(Concept concept, CreateStateControl sc, boolean includeType) throws ReadOnlyException {
		String typeURI = null;
		if (includeType && sc.getTypeURI() != null)
			typeURI = sc.getTypeURI().toString();

		String uri;
		if (typeURI != null) {
			int slashpos = typeURI.lastIndexOf('/');
			uri = "New " + typeURI.substring(slashpos + 1);
		} else {
			uri = concept.getURI();
			int slashpos = uri.lastIndexOf('/');
			uri = uri.substring(slashpos + 1);
		}

		AttributeEntryUtil.newTitle(concept, uri);

		// More???
	}

	protected static URI guessURI(String base, int exponent, CreateStateControl sc) throws URISyntaxException {
		int guess = (int) (Math.random() * Math.pow(10.0, exponent));
		// Tracer.debug("trying with uri : "+base+sc.getTypeName()+guess);
		return new URI(base + sc.getTypeName() + guess);
	}

	public void popupMenu(MapEvent m) {
		if (m.mouseEvent.isPopupTrigger() && !m.isConsumed()) {
			MouseEvent e = m.mouseEvent;
			typeMenu.setSelected(true);
			typeMenu.getPopupMenu().show((java.awt.Component) e.getSource(), e.getX(), e.getY());
			;
			m.consume();
		}
	}
}
