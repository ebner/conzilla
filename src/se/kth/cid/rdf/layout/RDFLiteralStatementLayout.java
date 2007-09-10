/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf.layout;

import java.net.URI;
import java.util.StringTokenizer;

import se.kth.cid.component.EditEvent;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.rdf.CV;
import se.kth.cid.rdf.RDFModel;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * @author matthias
 */
public class RDFLiteralStatementLayout extends RDFStatementLayout {

    /** In memory representation of literal boundingbox.
     */
    ContextMap.BoundingBox literalBb;

    /**
     * Constructor for both load and create.
     */
    public RDFLiteralStatementLayout(URI uri,
    		RDFConceptMap cMap,
    		String tripleuri,
    		String subjectLayoutURI) {
        super(uri, cMap, tripleuri, subjectLayoutURI, uri.toString(), CV.LiteralStatementLayout);
    }

    protected void initUpdate() {
        super.initUpdate();

        Resource object = getLoadModel().getResource(getURI());

        StringTokenizer stloc =
        	new StringTokenizer(
        			object.getProperty(CV.literalLocation).getString(),
        			",");
        StringTokenizer stdim =
        	new StringTokenizer(
        			object.getProperty(CV.literalDimension).getString(),
        			",");
        if (stloc.countTokens() >= 2 && stdim.countTokens() >= 2)
        	literalBb =
        		new ContextMap.BoundingBox(
        				Integer.parseInt(stloc.nextToken()),
        				Integer.parseInt(stloc.nextToken()),
        				Integer.parseInt(stdim.nextToken()),
        				Integer.parseInt(stdim.nextToken()));

    }
    
    
  public void removeFromModel(RDFModel model) throws ReadOnlyException {
		super.removeFromModel(model);
		setLiteralBoundingBox(null);
  }
 
    public boolean isLiteralStatement() {
        return true;
    }

    /** Returns the bounding box of the literal of this StatementLayout.
     *
     *  @return  the  bounding box of the literal of this StatementLayout. Only
     * null if {@link #isLiteralStatement()} returns false.
     */
    public ContextMap.BoundingBox getLiteralBoundingBox() {
        return literalBb;
    }

    /** Sets the bounding box of the literal of this StatementLayout.
     *  Must never be set to null.
     * 
     *  @param rect the bounding box of the body of this StatementLayout.
     */
    public void setLiteralBoundingBox(ContextMap.BoundingBox rect)
        throws ReadOnlyException {
        setLiteralBoundingBox(rect, getLoadModel());
    }

    /** Sets the literals boundingbox to model and cache.
     */
    public void setLiteralBoundingBox(
        ContextMap.BoundingBox rect,
        RDFModel model)
        throws ReadOnlyException {
    	isEditable();
    	if (model == null)
    		model = getLoadModel();

    	Resource object = model.getResource(getURI());

    	//If the new boundingbox is the same as the old, do nothing.
    	if (literalBb == rect
    			|| (literalBb != null
    					&& rect != null
    					&& literalBb.dim.width == rect.dim.width
    					&& literalBb.dim.height == rect.dim.height
    					&& literalBb.pos.x == rect.pos.x
    					&& literalBb.pos.y == rect.pos.y))
    		return;

//  	boolean oldbbEqToNull = literalBb == null;
    	if (literalBb != null) { //If no property in this model getProperty throws an exception.
    		Statement st1 = object.getProperty(CV.literalLocation);
    		Statement st2 = object.getProperty(CV.literalDimension);
    		if (st1 != null)
    			st1.remove();
    		if (st2 != null)
    			st2.remove();
    		literalBb = null;
    	}

    	if (rect != null) {
    		literalBb = rect;
    		String location =
    			Integer.toString(literalBb.pos.x)
    			+ ","
    			+ Integer.toString(literalBb.pos.y);
    		String dimension =
    			Integer.toString(literalBb.dim.width)
    			+ ","
    			+ Integer.toString(literalBb.dim.height);
    		object.addProperty(CV.literalLocation, location);
    		object.addProperty(CV.literalDimension, dimension);

    	}

    	getLoadModel().setEdited(true);
    	setEdited(true);

    	conceptMap.fireEditEvent(
    			new EditEvent(
    					conceptMap,
    					this,
    					LITERAL_BOUNDINGBOX_EDITED,
    					literalBb));
    }
}
