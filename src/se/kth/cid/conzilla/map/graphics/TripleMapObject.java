/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map.graphics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;

import se.kth.cid.component.EditEvent;
import se.kth.cid.concept.Concept;
import se.kth.cid.concept.Triple;
import se.kth.cid.conzilla.map.MapDisplayer;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;

public class TripleMapObject extends DrawerMapObject {
    StatementLayout tripleLayout;
    Triple triple;
    //  TripleType   tripleType;
    LineDrawer boxLineDrawer;

    boolean literalEditable = false;
    String invalidError;

    LineDrawer lineDrawer;
    HeadDrawer headDrawer;
    LiteralBoxDrawer literalBoxDrawer = null;
    LiteralDrawer literalDrawer = null;

    public TripleMapObject(
        StatementLayout tripleLayout,
        MapDisplayer displayer) {
        super(tripleLayout, displayer);
        this.tripleLayout = tripleLayout;

        if (concept != null)
            checkTriple();

        lineDrawer = new LineDrawer(this, true);
        headDrawer = new HeadDrawer(this, true);
        boxLineDrawer = new LineDrawer(this);

        if (tripleLayout.isLiteralStatement()) {
            literalBoxDrawer = new LiteralBoxDrawer(this);
            literalDrawer = new LiteralDrawer(this, displayer);
        }
        update();

    }
    public TitleDrawer getLiteralDrawer() {
        return literalDrawer;
    }

    public void setLiteralEditable(boolean editable, MapEvent e) {
        if (literalDrawer == null)
            return;
        if (editable)
            if (!drawLayout.isEditable())
                return;
        literalDrawer.setEditable(editable, e);
        this.literalEditable = editable;
    }

    void checkTriple() {
        Concept ne = getConcept();
        if (ne != null)
            triple = ne.getTriple();

        if (triple != null) {
            /*		ConceptType conceptType = getConceptType();
            if(conceptType != null)
                tripleType = conceptType.getTripleType(); //.getTripleType(triple.predicateURI());
            */
            // Check the end... 
            //do this later...
            /*
            ResourceLayout end = tripleLayout.getObject();
            if (end == null)
              return;
            ConceptMap map = end.getConceptMap();	  
            URI objectLayoutURI
            = URIClassifier.parseValidURI(end.getConceptURI(),
            			  map.getURI());
            
            URI tripleURI = URIClassifier.parseValidURI(triple.objectURI(),
            				    getConcept().getURI());
            
            if(! (tripleURI.equals(conceptLayoutURI)))
            {
              invalidError = "StatementLayout pointing to: '" + conceptLayoutURI + "', while Triple "
            + "pointing to '" + tripleURI + "'.";
              
              Tracer.trace(invalidError, Tracer.WARNING);
            }*/
        }
    }

    ///////// Update support //////////

    public void update() {
        ContextMap.Position[] layoutLine = tripleLayout.getLine();

        Point line[] = new Point[layoutLine.length];
        for (int i = 0; i < line.length; i++)
            line[i] = new Point(layoutLine[i].x, layoutLine[i].y);

        headDrawer.update(line);
        lineDrawer.update(line);
        updateBoxLine();
        
        if (literalBoxDrawer != null) {
            literalBoxDrawer.update(this);
            literalDrawer.updateTitle();
            literalDrawer.updateBox(literalBoxDrawer.getInnerBoundingBox());
        }
    }


    void updateBoxLine() {
        ContextMap.Position[] layoutLine = tripleLayout.getBoxLine();
        Point[] line = null;

        if (layoutLine != null) {
            line = new Point[layoutLine.length];
            for (int i = 0; i < line.length; i++)
                line[i] = new Point(layoutLine[i].x, layoutLine[i].y);
        }

        boxLineDrawer.update(line);
    }
    
    public void componentEdited(EditEvent e) {
        super.componentEdited(e);

        switch (e.getEditType()) {
            //	case TripleType.LINETYPE_EDITED:
            //	case TripleType.HEADTYPE_EDITED:
            case StatementLayout.LINE_EDITED :

                update();
                break;
            case StatementLayout.LITERAL_BOUNDINGBOX_EDITED :
                update();
                break;
            case Concept.TRIPLE_ADDED :
            case Concept.TRIPLE_REMOVED :

                if (tripleLayout.getURI().equals(e.getTarget()))
                    checkTriple();
                break;
            case DrawerLayout.BOXLINE_EDITED :
                updateBoxLine();
                break;
                /*	case ConceptType.TRIPLETYPE_ADDED:
                case ConceptType.TRIPLETYPE_REMOVED:
                
                checkTriple();
                break;
                */
                //	case TripleType.MINIMUMMULTIPLICITY_EDITED:
                //	case TripleType.MAXIMUMMULTIPLICITY_EDITED:
            case StatementLayout.DATATAG_ADDED :
            case StatementLayout.DATATAG_REMOVED :
                //	case TripleType.DATATAG_ADDED:
                //	case TripleType.DATATAG_REMOVED:
            case Triple.DATAVALUES_EDITED :
                break;
        }
    }

    /////////// Painting ///////////

    public void paint(Graphics g) {
    	super.paint(g);
    	if (lineDrawer != null) {
    		lineDrawer.coloredPaint(g, this);
    	}
    	if (headDrawer != null) {
    		headDrawer.coloredPaint(g, this);
    	}
    	if (boxLineDrawer != null) {
    		boxLineDrawer.coloredPaint(g, this);
    	}
    	if (literalBoxDrawer != null) {
    		literalBoxDrawer.coloredPaint(g, this);
    		literalDrawer.coloredPaint(g, this);
        }
    }

    public boolean checkAndFillHit(MapEvent m) {
        if (super.checkAndFillHit(m))
            return true;

        if (lineDrawer.checkAndFillHit(m)) {
            m.mapObject = this;
            m.hitType = MapEvent.HIT_TRIPLELINE;
            return true;
        }

        if (boxLineDrawer.checkAndFillHit(m)) {
            m.mapObject = this;
            m.hitType = MapEvent.HIT_BOXLINE;
            return true;
        }

        if (literalBoxDrawer != null && literalBoxDrawer.didHit(m)) {
            m.mapObject = this;
            if (literalDrawer.didHit(m)) {
                m.hitType = MapEvent.HIT_TRIPLELITERAL;
                return true;
            }
            m.hitType = MapEvent.HIT_TRIPLELITERALBOX;
            return true;
        }

        return false;
    }

    protected Collection getBoundingBoxesImpl() {
        Collection col = lineDrawer.getBoundingboxes();
        Rectangle r = headDrawer.getBoundingBox();
        if (r != null) {
            col.add(r);
        }
        boundingboxes.addAll(boxLineDrawer.getBoundingboxes());

        if (literalBoxDrawer != null)
            col.add(literalBoxDrawer.getBoundingBox());
        return col;
    }

    public void detachImpl() {
        super.detachImpl();
        lineDrawer = null;
        headDrawer = null;
        boxLineDrawer = null;
        
        if (literalBoxDrawer != null) {
            literalBoxDrawer = null;
            literalDrawer = null;
        }
    }
    
	public void setScale(double scale) {
		super.setScale(scale);
        if (literalBoxDrawer != null) {
            literalDrawer.setScale(scale);
        }
	}
}