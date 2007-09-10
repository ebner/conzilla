/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf.layout;

import java.net.URI;
import java.util.StringTokenizer;
import java.util.Vector;

import se.kth.cid.component.EditEvent;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.layout.BookkeepingStatementLayout;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.rdf.CV;
import se.kth.cid.rdf.RDFModel;
import se.kth.cid.style.LineStyle;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFException;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

/** This class wraps RDF-reifications into triples.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class RDFStatementLayout
    extends RDFDrawerLayout
    implements BookkeepingStatementLayout {
    ContextMap.Position[] line;
    int linePathType = LineStyle.PATH_TYPE_STRAIGHT;
    DrawerLayout subject;
    DrawerLayout object;
    protected String subjectLayoutURI;
    protected String objectLayoutURI;

    public RDFStatementLayout(URI uri, RDFConceptMap cMap) {
    	this(uri, cMap, null, null, null);
    }
    
    public RDFStatementLayout(URI uri, RDFConceptMap cMap, String statementURI, 
    		String subjectLayoutURI, String objectLayoutURI) {
    	super(uri, cMap, CV.StatementLayout, statementURI);
    	this.subjectLayoutURI = subjectLayoutURI;
    	this.objectLayoutURI = objectLayoutURI;
        line = new ContextMap.Position[0];
    }
    
    protected void initializeInModel(RDFModel model) {
        super.initializeInModel(model);

        try {
            Resource object = model.getResource(getURI());
//            object.addProperty(RDF.type, CV.StatementLayout);

            object.addProperty(
                CV.subjectLayout,
                model.getResource(subjectLayoutURI));
            if (objectLayoutURI != null) {
                object.addProperty(
                        CV.objectLayout,
                        model.getResource(objectLayoutURI));
            }
        } catch (RDFException re) {}
    }

    /** Removes the StatementLayout from the two connecting ConceptLayouts.
     *  This will destroy the StatementLayout.
     */
    public void removeFromModel(RDFModel model) throws ReadOnlyException {
        if (!isEditable())
            throw new ReadOnlyException("Read only!");

        setPathType(LineStyle.PATH_TYPE_STRAIGHT);
        Resource object = model.getResource(getURI());

        Statement st1 = object.getProperty(CV.subjectLayout);
        if (st1 != null) {
        	st1.remove();
        }
            
        Statement st2 = object.getProperty(CV.objectLayout);
        if (st2 != null)
        	st2.remove();

        //remove the type
        Statement st3 = object.getProperty(RDF.type);
        if (st3 != null)
        	st3.remove();
        //Remove the line
        setLine(new ContextMap.Position[0]);
    }

    protected void initUpdate() {
        super.initUpdate();

		Resource object = getLoadModel().getResource(getURI());

		if (object.hasProperty(CV.statementLinePathType, CV.LinePathType_Curve))
			linePathType = LineStyle.PATH_TYPE_CURVE;
		else
			linePathType = LineStyle.PATH_TYPE_STRAIGHT;

		Statement sLayout = object.getProperty(CV.subjectLayout);
		Statement oLayout = object.getProperty(CV.objectLayout);
		subjectLayoutURI = sLayout != null ? sLayout.getResource().toString()
				: "";
		objectLayoutURI = oLayout != null ? oLayout.getResource().toString()
				: "";

		Statement stl = object.getProperty(CV.statementLine);
		if (stl != null) {
			Seq seq = stl.getSeq();

			NodeIterator it = seq.iterator();
			Vector vec = new Vector();
			while (it.hasNext()) {
				StringTokenizer loc = new StringTokenizer(it.next().toString(),
						",");
				if (loc.countTokens() >= 2)
					vec.add(new ContextMap.Position(Integer.parseInt(loc
							.nextToken()), Integer.parseInt(loc.nextToken())));
			}
			it.close();
			line = (ContextMap.Position[]) vec
					.toArray(new ContextMap.Position[vec.size()]);
		} else {
			line = null;
		}

    }

    public ContextMap.Position[] getLine() {
        return line;
    }

    public void setLine(ContextMap.Position[] line) throws ReadOnlyException {
        // Do check if no changes are needed here....

        isEditable();

        RDFModel model = getLoadModel();

        if (setLine(line, model, CV.statementLine)) {
            this.line = line;

            model.setEdited(true);
            setEdited(true);

            conceptMap.fireEditEvent(
                new EditEvent(conceptMap, this, LINE_EDITED, this.line));
        }
    }

    public int getPathType() {
        return linePathType;
    }

    public void setPathType(int pt) throws ReadOnlyException {
        if (pt == linePathType)
            return;

        isEditable();

            RDFModel model = getLoadModel();

		Resource object = model.getResource(getURI());

		// remove if there is one.
		Statement stmt = object.getProperty(CV.statementLinePathType);
		if (stmt != null) {
			stmt.remove();
		}

		if (pt == LineStyle.PATH_TYPE_CURVE) {
			object.addProperty(CV.statementLinePathType, CV.LinePathType_Curve);
		}

		linePathType = pt;

		model.setEdited(true);
		setEdited(true);

		conceptMap.fireEditEvent(new EditEvent(conceptMap, this,
				LINEPATHTYPE_EDITED, new Integer(linePathType)));
    }

    /**
	 * Returns the subjet of this StatementLayout.
	 * 
	 * @return the ConceptLayout that is the end of this StatementLayout.
	 */
    public DrawerLayout getSubjectLayout() {
        return subject;
    }

    /**
	 * Returns the object of this StatementLayout.
	 * 
	 * @return the ConceptLayout that is the end of this StatementLayout.
	 */
    public DrawerLayout getObjectLayout() {
        return object;
    }

    public String getObjectLayoutURI() {
        return objectLayoutURI;
    }

    public String getSubjectLayoutURI() {
        return subjectLayoutURI;
    }

    //do in RDF    
    public void setSubjectLayout(DrawerLayout subject) {
        this.subject = subject;
    }

    //do in RDF    
    public void setObjectLayout(DrawerLayout object) {
        this.object = object;
    }

    public boolean getAllowsChildren() {
        return false;
    }

    public boolean isLeaf() {
        return true;
    }

    public boolean isLiteralStatement() {
        return false;
    }

    /** Returns the bounding box of the literal of this StatementLayout.
     *
     *  @return  the  bounding box of the literal of this StatementLayout. Only
     * null if {@link #isLiteralStatement()} returns false.
     */
    public ContextMap.BoundingBox getLiteralBoundingBox() {
        return null;
    }

    /** Sets the bounding box of the literal of this StatementLayout.
     *  Must never be set to null.
     * 
     *  @param rect the bounding box of the body of this StatementLayout.
     */
    public void setLiteralBoundingBox(ContextMap.BoundingBox rect)
        throws ReadOnlyException {}
}
