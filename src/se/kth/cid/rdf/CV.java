/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;

/** Conzilla Vocabulary class.
 */
public class CV {
	
	static Log log = LogFactory.getLog(CV.class);

    /** The <B>notions</B> namespace encompasses properties and vocabularies
     *  regarding abstract notions such as Concepts, Contexts and Content.
     */
    static final String NNS = "http://conzilla.org/model/notions#";

    /** The <B>layout</B> namespace encompasses properties and vocabularies
     *  regarding positiong.
     */
    static final String LNS = "http://conzilla.org/model/graphic#";

    /** The <B>style</B> namespace encompasses box and linestyles as well as
     *  the properties that are used to style an instance, an entire class or 
     *  prpoerties.
     */
    static final String SNS = "http://conzilla.org/model/style#";

    /** The <B>navigation</B> namespace contains navigational properties
     *  that code for occurence, hyperlink and includemodel.
     */
    static final String NavNS = "http://conzilla.org/model/navigation#";

    /** The <B>Session</B> namespace contains properties dealing with
     *  editing sessions.
     */
    static final String PNS = "http://conzilla.org/model/session#";
    
	/** The <B>content</B> namespace contains content and bookmark related properties.
	 */
	static final String CNS = "http://conzilla.org/model/bookmark#";
    
    //static final String  CBNS = "http://www.conzilla.org/rdf/profile#";
    static final String  FOAF = "http://xmlns.com/foaf/0.1/";
    
	public static final String title = "http://purl.org/dc/elements/1.1/title";
	public static final String description = "http://purl.org/dc/elements/1.1/description";

    // TODO: Move this class to a better namespace:
    public static Resource ProjectionVariable = null;

    //NOTIONS
    public static Resource Content;
    public static Resource RetrievableContent;
    public static Property contains;
    public static Property includes;
    public static Resource ContentInContext;
    public static Resource Concept;

    //NAVIGATION
    public static Property hyperlink = null;
    public static Property includeContainer = null;

    //LAYOUT
    public static Resource ContextMap = null;
    public static Resource NodeLayout = null;
    public static Resource ConceptLayout = null;
    public static Resource StatementLayout = null;
    public static Resource LiteralStatementLayout = null;
    public static Property subLayerOf = null;
    public static Property displayResource = null;
    public static Property location = null;
    public static Property dimension = null;
    public static Property literalLocation = null;
    public static Property literalDimension = null;
    public static Property bodyVisible = null;
    public static Property subjectLayout = null;
    public static Property objectLayout = null;
    public static Property inNodeLayout = null;
    public static Property inContextMap = null;
    public static Property priority = null;
    
    public static Property statementLine = null;
    public static Property statementLinePathType = null;
    public static Property boxLine = null;
    public static Property boxLinePathType = null;
    public static Resource LinePathType_Straight = null;
    public static Resource LinePathType_Curve = null;

    public static Property horizontalTextAnchor = null;
    public static Property verticalTextAnchor = null;
    public static Resource Center = null;
    public static Resource North = null;
    public static Resource South = null;
    public static Resource East = null;
    public static Resource West = null;
    
    //STYLE
    public static Property styleInstance = null;
    public static Property styleClass = null;
    public static Property boxStyle = null;
    public static Property boxFilled = null;
    public static Property boxBorderStyle = null;
    public static Property boxBorderThickness = null;
    public static Property lineStyle = null;
	public static Property lineThickness = null;
	public static Property lineHeadInLineEnd = null;
    public static Property lineHeadStyle = null;
    public static Property lineHeadFilled = null;
    public static Property lineHeadWidth = null;
    public static Property lineHeadLength = null;
    public static Property lineHeadLineThickness = null;
    public static Property boxLineStyle = null;
    public static Property boxLineThickness = null;

		//PROJECT
	public static Resource Project = null;

	public static Resource ConceptBaseURI = null;
	public static Resource LayoutBaseURI = null;		
  	public static Property useBaseURI = null;				  

	public static Resource ConceptContainer = null;
  	public static Resource LayoutContainer = null;
  	public static Property useContainer = null;
    public static Property managed = null;

	//CONTENT & BOOKMARKS
    public static Resource Bookmark;
    public static Resource BookmarkFolder;
    public static Resource ContextMapBookmark;
    public static Resource ConceptBookmark;
    public static Resource ConceptInContextBookmark;


    //PROFILE
    public static Resource Agent = null;
    public static Resource Person = null;
    public static Resource Group = null;
    public static Resource Organization = null;

    static {
        try {
            // TODO: Move this class to a better namespace:
            ProjectionVariable =
                new ResourceImpl(LNS, "ProjectionVariable", null);

            // properties:

            //NOTIONS
            Content = new ResourceImpl(NNS+"Content");
            RetrievableContent = new ResourceImpl(NNS+"RetrievableContent");
            contains = new PropertyImpl(NNS, "contains", null);
            includes = new PropertyImpl(NNS, "includes", null);
            Concept =  new ResourceImpl(NNS + "Concept");
            ContentInContext = new ResourceImpl(NNS + "ContentInContext");
          

            //NAVIGATION
            hyperlink = new PropertyImpl(NavNS, "hyperlink", null);
            includeContainer = new PropertyImpl(NavNS, "includeContainer", null);

            //LAYOUT
            ContextMap = new ResourceImpl(LNS, "ContextMap", null);
            NodeLayout = new ResourceImpl(LNS, "NodeLayout", null);
            ConceptLayout = new ResourceImpl(LNS, "ConceptLayout", null);
            StatementLayout = new ResourceImpl(LNS, "StatementLayout", null);
            LiteralStatementLayout =
                new ResourceImpl(LNS, "LiteralStatementLayout", null);
            subLayerOf = new PropertyImpl(LNS, "subLayerOf", null);
            displayResource = new PropertyImpl(LNS, "displayResource", null);
            location = new PropertyImpl(LNS, "location", null);
            dimension = new PropertyImpl(LNS, "dimension", null);
            literalLocation = new PropertyImpl(LNS, "literalLocation", null);
            literalDimension = new PropertyImpl(LNS, "literalDimension", null);
            bodyVisible = new PropertyImpl(LNS, "bodyVisible", null);
            subjectLayout = new PropertyImpl(LNS, "subjectLayout", null);
            objectLayout = new PropertyImpl(LNS, "objectLayout", null);

            statementLine = new PropertyImpl(LNS, "statementLine", null);
            statementLinePathType =
                new PropertyImpl(LNS, "statementLinePathType", null);
            boxLine = new PropertyImpl(LNS, "boxLine", null);
            boxLinePathType = new PropertyImpl(LNS, "boxLinePathType", null);
            LinePathType_Curve =
                new ResourceImpl(LNS, "LinePathType_Curve", null);
            LinePathType_Straight =
                new ResourceImpl(LNS, "LinePathType_Straight", null);

            horizontalTextAnchor =
                new PropertyImpl(LNS, "horizontalTextAnchor", null);
            verticalTextAnchor =
                new PropertyImpl(LNS, "verticalTextAnchor", null);
            Center = new ResourceImpl(LNS, "Center", null);
            North = new ResourceImpl(LNS, "North", null);
            South = new ResourceImpl(LNS, "South", null);
            East = new ResourceImpl(LNS, "East", null);
            West = new ResourceImpl(LNS, "West", null);
            inNodeLayout = new PropertyImpl(LNS, "inNodeLayout", null);
            inContextMap = new PropertyImpl(LNS, "inContextMap", null);
            priority = new PropertyImpl(LNS, "priority", null);
            
            //STYLE
            styleInstance = new PropertyImpl(SNS, "styleInstance", null);
            styleClass = new PropertyImpl(SNS, "styleClass", null);
            boxStyle = new PropertyImpl(SNS, "boxStyle", null);
            boxFilled = new PropertyImpl(SNS, "boxFilled", null);
            boxBorderStyle = new PropertyImpl(SNS, "borderStyle", null);
            boxBorderThickness = new PropertyImpl(SNS, "borderThickness", null);
            lineStyle = new PropertyImpl(SNS, "lineStyle", null);
			lineThickness = new PropertyImpl(SNS, "lineThickness", null);
			lineHeadInLineEnd = new PropertyImpl(SNS, "lineHeadInLineEnd", null);
            lineHeadStyle = new PropertyImpl(SNS, "lineHeadStyle", null);
            lineHeadFilled = new PropertyImpl(SNS, "lineHeadFilled", null);
            lineHeadWidth = new PropertyImpl(SNS, "lineHeadWidth", null);
            lineHeadLength = new PropertyImpl(SNS, "lineHeadLength", null);
            lineHeadLineThickness = new PropertyImpl(SNS, "lineHeadLineThickness", null);
            boxLineStyle = new PropertyImpl(SNS, "boxLineStyle", null);
            boxLineThickness = new PropertyImpl(SNS, "boxLineThickness", null);

            //Project
          	Project = new ResourceImpl(PNS, "Session", null);
			ConceptBaseURI = new ResourceImpl(PNS, "ConceptBaseURI", null);
          	LayoutBaseURI = new ResourceImpl(PNS, "LayoutBaseURI", null);		
          	useBaseURI = new PropertyImpl(PNS, "useBaseURI", null);

          	ConceptContainer = new ResourceImpl(PNS, "ConceptContainer", null);
          	LayoutContainer = new ResourceImpl(PNS, "LayoutContainer", null);
          	useContainer = new PropertyImpl(PNS, "useContainer", null);
            managed = new PropertyImpl(PNS, "managed", null);
            
            //BOOKMARK
            Bookmark = new ResourceImpl(CNS + "Bookmark");
            BookmarkFolder = new ResourceImpl(CNS + "BookmarkFolder");
            ContextMapBookmark = new ResourceImpl(CNS + "ContextMapBookmark");
            ConceptBookmark =  new ResourceImpl(CNS + "ConceptBookmark");
            ConceptInContextBookmark =  new ResourceImpl(CNS + "ConceptInContextBookmark");
           
            
            Agent = new ResourceImpl(FOAF + "Agent");
            Person = new ResourceImpl(FOAF + "Person");
            Group = new ResourceImpl(FOAF + "Group");
            Organization = new ResourceImpl(FOAF + "Organization");
            
        } catch (Exception e) {
            // shouldn't happen
            log.error("Error while creating vocabulary", e);
            throw new RuntimeException("RDF Exception while creating vocabulary: " + e);
        }
    }
    
    
    
    
    
    /**
     *  Namespace
     */
    public final static String namespace = "http://www.imsproject.org/xsd/ims_cp_rootv1p1#";

    /**
     *  Description of the Field
     */
    public static Property Manifest;

    /**
     *  Description of the Field
     */
    public static Property organizations;

    /**
     *  Description of the Field
     */
    public static Property organization;

    /**
     *  Description of the Field
     */
    //public static Property Hierarchy;

    /**
     *  Description of the Field
     */
    public static Property Item;

    /**
     *  Description of the Field
     */
    public static Property content;

    static {
        try {
            Manifest = new PropertyImpl(namespace, "Manifest");
            organizations = new PropertyImpl(namespace, "organizations");
            organization = new PropertyImpl(namespace, "organization");
            //Hierarchy = new PropertyImpl(namespace, "Hierarchy");
            Item = new PropertyImpl(namespace, "Item");
            content = new PropertyImpl(namespace, "content");

        } catch (Exception e) {
            System.err.println("Exception: " + e);
        }
    }
}
