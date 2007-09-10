/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf.layout;

import java.net.URI;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import se.kth.cid.component.EditEvent;
import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.layout.BookkeepingDrawerLayout;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.rdf.CV;
import se.kth.cid.rdf.RDFContainerManager;
import se.kth.cid.rdf.RDFModel;
import se.kth.cid.style.LineStyle;
import se.kth.cid.util.Tracer;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.SelectorImpl;

/** 
 */
public class RDFDrawerLayout
    extends RDFResourceLayout
    implements DrawerLayout, BookkeepingDrawerLayout {
    // REMOVE: String concepturi;
    String detailedMap;
    ContextMap.BoundingBox bb;
    boolean bodyVisible;
    int boxLinePathType = LineStyle.PATH_TYPE_STRAIGHT;
    ContextMap.Position[] boxLine = new ContextMap.Position[0];
    int horizontalTextAnchor = CENTER;
    int verticalTextAnchor = CENTER;

    /** Loads an existing drawlayout from model.
     */
    //public RDFDrawerLayout(TotalModel totalModel, RDFModelManager mm, RDFConceptMap cMap, URI uri) 
    public RDFDrawerLayout(
    		URI uri,
    		RDFConceptMap cMap,
    		Resource nodeType,
    		String contentUri) {
    	//super(totalModel, mm, cMap, uri);
        super(uri, cMap, nodeType);
        //	loadDrawerLayout();
        if (contentUri != null) {
        	setValue(contentUri);
        }
      //  seekForChildren = false;
    }

    protected void updateFromModel(RDFModel m) {
    	super.updateFromModel(m);
    	Resource object = m.getResource(getURI());
    	//will have right model because of overloaded currentModel.

    	Statement stmt = object.getProperty(CV.hyperlink);
    	if (stmt != null) {
    		Resource hl = stmt.getResource();
    		detailedMap = hl.getURI();
    	}
    	if (object.hasProperty(CV.boxLinePathType, CV.LinePathType_Curve))
    		boxLinePathType = LineStyle.PATH_TYPE_CURVE;
    	else
    		boxLinePathType = LineStyle.PATH_TYPE_STRAIGHT;

    	if (object.hasProperty(CV.horizontalTextAnchor, CV.West))
    		horizontalTextAnchor = WEST;
    	else if (object.hasProperty(CV.horizontalTextAnchor, CV.East))
    		horizontalTextAnchor = EAST;
    	else
    		horizontalTextAnchor = CENTER;

    	if (object.hasProperty(CV.verticalTextAnchor, CV.North))
    		verticalTextAnchor = NORTH;
    	else if (object.hasProperty(CV.verticalTextAnchor, CV.South))
    		verticalTextAnchor = SOUTH;
    	else
    		verticalTextAnchor = CENTER;

    	Statement stmtLoc = object.getProperty(CV.location); 
    	StringTokenizer stloc = null;
    	if (stmtLoc != null) {
    		stloc = new StringTokenizer( stmtLoc.getString(),",");
    	}

    	Statement stmtDim = object.getProperty(CV.dimension); 
    	StringTokenizer stdim = null;
    	if (stmtDim != null) {
    		stdim = new StringTokenizer(stmtDim.getString(),",");
    	}

    	if (stloc != null && stdim != null 
    			&& stloc.countTokens() >= 2 
    			&& stdim.countTokens() >= 2)
    		bb =
    			new ContextMap.BoundingBox(
    					Integer.parseInt(stloc.nextToken()),
    					Integer.parseInt(stloc.nextToken()),
    					Integer.parseInt(stdim.nextToken()),
    					Integer.parseInt(stdim.nextToken()));

    	if (bb == null)
    		bodyVisible = false;
    	else if (object.hasProperty(CV.bodyVisible, "false"))
    		bodyVisible = false;
    	else
    		bodyVisible = true;

    	stmt = object.getProperty(CV.boxLine);
    	if (stmt != null) {
    		NodeIterator it =stmt.getSeq().iterator();
    		Vector vec = new Vector();
    		while (it.hasNext()) {
    			StringTokenizer loc =
    				new StringTokenizer(it.next().toString(), ",");
    			if (loc.countTokens() >= 2)
    				vec.add(
    						new ContextMap.Position(
    								Integer.parseInt(loc.nextToken()),
    								Integer.parseInt(loc.nextToken())));
    		}
    		it.close();
    		boxLine =
    			(ContextMap.Position[]) vec.toArray(
    					new ContextMap.Position[vec.size()]);
    	}
    }
    protected void removeFromModel(RDFModel model) {
        super.removeFromModel(model);
        try {
            //Remove the line
            setBoxLine(new ContextMap.Position[0], model);
            setDetailedMap(null, model);

            //since default is true, this means no triple is in the model.
            setBoundingBox(null, model);
            setBodyVisible(true, model);

            //since default is straight a possible is removed.
            setBoxLinePathType(LineStyle.PATH_TYPE_STRAIGHT);

            //Textanchor is default center.
            setVerticalTextAnchor(CENTER);
            setHorisontalTextAnchor(CENTER);

            //A zero length array removes the RDF property as well...
            setBoxLine(new ContextMap.Position[0]);

            //will have right model because of overloaded currentModel.
            Resource object = model.getResource(getURI());

            if (object.hasProperty(CV.displayResource)) {
                object.getProperty(CV.displayResource).remove();
            }
        } catch (Exception re) {}
    }

    public String getConceptURI() {
        return getValue();
    }

    public String getDetailedMap() {
        return detailedMap;
    }

    public void setDetailedMap(String uri)
        throws ReadOnlyException, InvalidURIException {
        setDetailedMap(uri, getLoadModel());
    }

    public void setDetailedMap(String uri, RDFModel model)
        throws ReadOnlyException, InvalidURIException {
        isEditable();
        try {
            if (model == null)
                model = getLoadModel();

            Resource object = model.getResource(getURI());

            if (detailedMap != null) {
                if (detailedMap.equals(uri))
                    return;

                Statement st = object.getProperty(CV.hyperlink);
                if (st != null) {
                	//Remove the hyperlink
                	st.remove();

                	//If automatic managment of include requests...
                	//Remove requestContainer if alone to hyperlink to this resource in this model.
                	Resource end = st.getResource();

                	// the following if-block is here and not in the SaveTool because
                	// we cannot remove the dependency if we remove a DetailedMap.
                	if (((RDFContainerManager) rcm.getContainerManager()).getIncludeRequestsAutomaticallyManaged()
                			&& !model.listSubjectsWithProperty(CV.hyperlink, end).hasNext()) {
                		Iterator it3 = model.getRequestedContainersForURI(end.getURI()).iterator();
                		while (it3.hasNext()) {
                			model.removeRequestedContainerForURI(end.getURI(), (String) it3.next());
                		}
                	}
                }
            }
            if (uri != null) {
                //FIXME: should check that uri is a valid uri????
                Resource r = model.getResource(uri);
                object.addProperty(CV.hyperlink, r);
                detailedMap = uri;
            } else {
                detailedMap = null;
            }
            
            getLoadModel().setEdited(true);
            setEdited(true);
            conceptMap.fireEditEvent(new EditEvent(conceptMap, this, DETAILEDMAP_EDITED, uri));
        } catch (Exception re) {
            Tracer.debug("Failed changing model." + re.getMessage());
        }
    }

    public ContextMap.BoundingBox getBoundingBox() {
        return bb;
    }

    public void setBoundingBox(ContextMap.BoundingBox rect) {
        setBoundingBox(rect, getLoadModel());
    }

    /** Sets the boundingbox to model and cache.
     *  If boundingbox is set to null the body is set to not visible.
     *  If the boundingbox was null before and now is set to something,
     *  the body is set to visible as well.... (as a curtency so you don't need to
     *  remember to call setBodyVisible all the time). This case is just a sensible
     *  default strategy that ofcourse can be overridden manually by calling 
     *  setBodyVisible afterwards.
     */
    public void setBoundingBox(ContextMap.BoundingBox rect, RDFModel model)
        throws ReadOnlyException {
        isEditable();
        try {
            if (model == null)
                model = getLoadModel();

            Resource object = model.getResource(getURI());

            //If the new boundingbox is the same as the old, do nothing.
            if (bb == rect
                || (bb != null
                    && rect != null
                    && bb.dim.width == rect.dim.width
                    && bb.dim.height == rect.dim.height
                    && bb.pos.x == rect.pos.x
                    && bb.pos.y == rect.pos.y))
                return;

            boolean oldbbEqToNull = bb == null;
            if (bb != null)
                try { //If no property in this model getProperty throws an exception.
                    Statement st1 = object.getProperty(CV.location);
                    Statement st2 = object.getProperty(CV.dimension);
                    if (st1 != null)
                        st1.remove();
                    if (st2 != null)
                        st2.remove();
                    bb = null;
                } catch (Exception re) {}

            if (rect != null) {
                bb = rect;
                String location =
                    Integer.toString(bb.pos.x)
                        + ","
                        + Integer.toString(bb.pos.y);
                String dimension =
                    Integer.toString(bb.dim.width)
                        + ","
                        + Integer.toString(bb.dim.height);
                object.addProperty(CV.location, location);
                object.addProperty(CV.dimension, dimension);

            }
            if (bb == null)
                setBodyVisible(false);
            //if no boundingbox then body not visible (obviously).
            else if (oldbbEqToNull)
                setBodyVisible(true);
            //if new boundingbox is set when no was before the body is
            //made visible.

            getLoadModel().setEdited(true);
            setEdited(true);

            conceptMap.fireEditEvent(
                new EditEvent(conceptMap, this, BOUNDINGBOX_EDITED, bb));
        } catch (Exception re) {
            Tracer.debug("Failed changing model." + re.getMessage());
        }
    }
    public boolean getBodyVisible() {
        return bodyVisible;
    }

    public void setBodyVisible(boolean visible) throws ReadOnlyException {
        setBodyVisible(visible, getLoadModel());
    }

    public void setBodyVisible(boolean visible, RDFModel model)
        throws ReadOnlyException {
        isEditable();
        if (bodyVisible == visible)
            return;

        try {
            if (model == null)
                model = getLoadModel();

            Resource object = model.getResource(getURI());

            Statement st = object.getProperty(CV.bodyVisible);
            //throws exception whenever there isn't one.

            if (st != null)
                st.remove();
        } catch (Exception re) {
            Tracer.debug(
                "Failed removing old hyperlink in model." + re.getMessage());
        }
        try {
            if (model == null)
                model = getLoadModel();

            Resource object = model.getResource(getURI());
            bodyVisible = visible;

            //If boundingbox is set, visibility is true by default.
            //If boundingbox is not set, visibility is false by default.
            //If boundingbox is not set, body isn't allowed to be visible.
            //Hence the only case is boundingbox is set and body is not visible.
            if (!visible && bb != null)
                object.addProperty(CV.bodyVisible, "false");
        } catch (Exception re) {
            Tracer.debug(
                "Failed removing old hyperlink in model." + re.getMessage());
        }
    }

    public ContextMap.Position[] getBoxLine() {
        return boxLine;
    }

    public void setBoxLine(ContextMap.Position[] line)
        throws ReadOnlyException {
        setBoxLine(line, getLoadModel());
    }

    protected void setBoxLine(ContextMap.Position[] line, RDFModel model)
        throws ReadOnlyException {
        //Do check if no changes are needed here....

        isEditable();

        if (setLine(line, model, CV.boxLine)) {
            boxLine = line;

            model.setEdited(true);
            setEdited(true);

            conceptMap.fireEditEvent(
                new EditEvent(conceptMap, this, BOXLINE_EDITED, line));
        }
    }

    /** Reuse for both boxLine and line (in RDFStatementLayout).
     */
    protected boolean setLine(
        ContextMap.Position[] line,
        RDFModel model,
        Property lineProp) {
        try {
            // -- Remove old line --

            StmtIterator si =
                model.listStatements(
                    new SelectorImpl(getResource(), lineProp, (RDFNode) null));

            if (si.hasNext()) //if an old line exists then remove the it.
                {
                Statement s = si.nextStatement();
                s.getResource().removeProperties();
                s.remove();
            }

            if (line.length == 0) //we are finished if the new line is empty.
                return true;

            // -- Set new line --

            //First add a statement to a seq
            Seq seq = getLoadModel().createSeq();
            Statement s =
                getLoadModel().createStatement(getResource(), lineProp, seq);
            getLoadModel().add(s);

            //Then add all points.
            for (int i = 0; i < line.length; i++)
                seq.add(
                    i + 1,
                    Integer.toString(line[i].x)
                        + ","
                        + Integer.toString(line[i].y));

            return true;
        } catch (Exception re) {
            Tracer.debug(
                "Failed setting new line in model..." + re.getMessage());
            re.printStackTrace();
        }

        return false;
    }

    public int getBoxLinePathType() {
        return boxLinePathType;
    }

    public void setBoxLinePathType(int pt) throws ReadOnlyException {
        setBoxLinePathType(pt, getLoadModel());
    }

    public void setBoxLinePathType(int pt, RDFModel model)
        throws ReadOnlyException {
        if (pt == boxLinePathType)
            return;

        isEditable();

        if (model == null)
        	model = getLoadModel();

        Resource object = model.getResource(getURI());

        //remove if there is one.
        object.getProperty(CV.boxLinePathType).remove();
        
        if (pt == LineStyle.PATH_TYPE_CURVE)
        	object.addProperty(CV.boxLinePathType, CV.LinePathType_Curve);

        boxLinePathType = pt;

        model.setEdited(true);
        setEdited(true);

        conceptMap.fireEditEvent(new EditEvent(conceptMap, this, BOXLINEPATHTYPE_EDITED, new Integer(boxLinePathType)));
    }

    public void setHorisontalTextAnchor(int value) throws ReadOnlyException {
        setHorisontalTextAnchor(value, getLoadModel());
    }

    public void setHorisontalTextAnchor(int value, RDFModel model)
        throws ReadOnlyException {
        if (value == horizontalTextAnchor)
            return;
        isEditable();

        if (model == null)
        	model = getLoadModel();

        Resource object = model.getResource(getURI());

        Statement st = object.getProperty(CV.horizontalTextAnchor);
        if (st != null) {
        	st.remove();
        }

        if (value == WEST)
        	object.addProperty(CV.horizontalTextAnchor, CV.West);
        else if (value == EAST)
        	object.addProperty(CV.horizontalTextAnchor, CV.East);

        horizontalTextAnchor = value;

        model.setEdited(true);
        setEdited(true);

        conceptMap.fireEditEvent(
        		new EditEvent(
        				conceptMap,
        				this,
        				HORIZONTAL_TEXT_ANCHOR_EDITED,
        				new Integer(horizontalTextAnchor)));
    }

    public void setVerticalTextAnchor(int value) throws ReadOnlyException {
        setVerticalTextAnchor(value, getLoadModel());
    }

    public void setVerticalTextAnchor(int value, RDFModel model)
        throws ReadOnlyException {
    	if (value == verticalTextAnchor)
    		return;

    	isEditable();

    	if (model == null)
    		model = getLoadModel();

    	Resource object = model.getResource(getURI());

    	Statement st = object.getProperty(CV.verticalTextAnchor);
    	if (st != null) {
    		st.remove();
    	}

    	if (value == NORTH)
    		object.addProperty(CV.verticalTextAnchor, CV.North);
    	else if (value == SOUTH)
    		object.addProperty(CV.verticalTextAnchor, CV.South);

    	verticalTextAnchor = value;

    	model.setEdited(true);
    	setEdited(true);

    	conceptMap.fireEditEvent(
    			new EditEvent(
    					conceptMap,
    					this,
    					VERTICAL_TEXT_ANCHOR_EDITED,
    					new Integer(verticalTextAnchor)));
    }

    public int getHorisontalTextAnchor() {
        return horizontalTextAnchor;
    }
    public int getVerticalTextAnchor() {
        return verticalTextAnchor;
    }

    /** Returns the data tags that should be visible in the body of this
     *  ConceptLayout.
     *
     *  @return the visible data tags. Never null.
     */
    public String[] getDataTags() {
        //Do this later...
        return new String[0];
    }

    /** Adds a visible data tag.
     *
     *  @param tag the tag that should be shown.
     */
    public void addDataTag(String tag) throws ReadOnlyException {
        if (!isEditable())
            throw new ReadOnlyException("Read only model");
        //do this later...
    }

    /** Removes a visible data tag.
     *
     *  @param tag the tag that should be removed.
     */
    public void removeDataTag(String tag) throws ReadOnlyException {
        if (!isEditable())
            throw new ReadOnlyException("Read only model");
        //do this later...
    }
    
}