/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.service.lcp;

import java.util.Vector;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import se.kth.cid.component.Component;
//import se.kth.cid.component.MetaData;
//import se.kth.cid.component.local.GenericComponent;
//import se.kth.cid.component.local.LocalMetaData;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.map.MapStoreManager;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.rdf.CV;
//import se.kth.cid.rdf.RDFAttributeEntry;
import se.kth.cid.rdf.RDFConcept;
import se.kth.cid.rdf.RDFResource;
import se.kth.cid.server.service.Request;
import se.kth.cid.style.StyleManager;
//import se.kth.cid.util.Tracer;

public class LCPMapResponse implements LCPResponse {

    public static Property predicate = null;

    MapStoreManager msm;

    String ResponseString = "";

    final String DefaultLanguage = "en";

    Request req;

    Vector ConceptsURI;

    StyleManager sm;

    ContextMap cpm;

    String delimiter;

    String innerdelimiter;

    public LCPMapResponse(MapStoreManager msm, Request req) {
        this.msm = msm;
        this.req = req;
        cpm = msm.getConceptMap();
        sm = LCPRequestHandler.getStyleManager();
        ConceptsURI = new Vector();
        delimiter = ";";
        innerdelimiter = ",";
    }

    public Object createResponse() throws Exception {
        Concept tmp = msm.getConcept(cpm.getURI());
        //H???mtar concept f???r kartan	
        DrawerLayout[] dl = msm.getConceptMap().getDrawerLayouts();
        RDFConcept[] cp = new RDFConcept[dl.length];
        //MetaData[] md = new MetaData[dl.length];

        String mapHead = "";
        String concepts = "";
        String relations = "";

        int nrOfConcepts = 0;
        Vector rel = new Vector();

        //G???r igenom samtliga concept/relationer i kartan
        for (int i = 0; i < dl.length; i++) {
            cp[i] = (RDFConcept) msm.getConcept(dl[i].getURI());
            //md[i] = new LocalMetaData((GenericComponent)cp[i]);//cp[i].getMetaData();
        }

        //Fixa huvudet
        mapHead += "LCP/" + getProtocolVersion() + " GETMAP" + "\r\n\r\n";
        //Allm???n information om kartan
        if (tmp != null) { //Om metadata hittats f???r kartan
            mapHead += "<" + getAbstractDataforConceptMap(tmp) + ">";
        } else {
            mapHead += "<" + cpm.getURI() + delimiter + cpm.getURI() + ">";
        }
        mapHead += "<" + getMapSize(cpm) + ">" + "\r\n";

        //Sortera ut concept och concept-relationer
        for (int i = 0; i < dl.length; i++) {
            if (dl[i] instanceof StatementLayout) { //True if Concept-relation
                rel.add(new Integer(i)); //Is saved for processing later on	
            } else { //Concept and not a concept-relation
                ConceptsURI.add(dl[i]);
                //The string for the Concepts.
                //Abstract information
                concepts += "<"
                    + dl[i].getConceptURI()
                    + delimiter
                    + getTitle(cp[i])
                    + delimiter
                    + getDescription(cp[i])
                    + ">";
                //graphical information
                concepts += "<"
                    + getBoxSizeAndPosition(dl[i])
                    + delimiter
                    + getTypeOfConcept(dl[i])
                    + ">";
                //navigational information
                concepts += "<"
                    + getSurfURI(dl[i])
                    + delimiter
                    + getNeighbourhood(cp[i])
                    + ">";
                //Content information
                concepts += "<" + getContent(cp[i]) + ">\r\n";
            }
        }

        //Object[] statementlayouts = rel.toArray();
        for (int i = 0; i < rel.size(); i++) {
            int jj = ((Integer) rel.get(i)).intValue();

            StatementLayout stl = (StatementLayout) dl[jj];
            //Str???ngen f???r relationerna
            //abstract information
            relations += "<"
                + stl.getConceptURI()
                + delimiter
                + getPredicate(stl)
                + delimiter
                + getSubjectNr(stl)
                + innerdelimiter
                + getObjectNr(stl)
                + delimiter
                + getTitle(cp[jj])
                + delimiter
                + getDescription(cp[jj])
                + ">";
            //graphical information
            relations += "<"
                + getLine(stl)
                + delimiter
                + relationTitlePosition(stl)
                + delimiter
                + 
            //FIXME
            getTypeOfRelation(stl)
                + innerdelimiter
                + getTypeOfArrow(stl)
                + delimiter
                + getLineHeadDirection(stl)
                + 
            //">";
            //        OBS! Inte originalprotokoll!!!
            //        Till f?r att klara splines
            delimiter + getTypeOfLine(stl) + ">";
            //navigational information
            relations += "<"
                + getSurfURI(dl[jj])
                + delimiter
                + getNeighbourhood(cp[jj])
                + ">";
            //content information
            relations += "<" + getContent(cp[jj]) + ">\r\n";
        }

        ResponseString += mapHead
            + "\r\n"
            + concepts
            + "\r\n"
            + relations
            + "\r\n";

        return ResponseString;
    }
    /*
      Ny metod att ta fram titeln, d??? metadata inte l???ngre kommer att finnas kvar.
      Dock inte fullt implementerad
    */

    private String getTitle(RDFResource r) {
        String returnString = "";
        StmtIterator v = r.getProperties(DC.title);
        if (v != null && v.hasNext()) {
            try {
                returnString = v.nextStatement().getString();
            } catch (Exception e) {
            }
        }
        return returnString.replace('\n', ' ');
    }

    private String getMapSize(ContextMap cmpp) {
        ContextMap.Dimension cd = cmpp.getDimension();
        return cd.width + delimiter + cd.height;
    }

    /*private String addNrOfLines(String s){
       int j=0;
       int nrOfLines = 0;
       while (j>-1){
          j=s.indexOf("\r\n",j+1);
          nrOfLines++;
       }
       return nrOfLines+";\r\n"+s;
    }*/
    //TODO: Hur f???r vi fram titel?
    private String getAbstractDataforConceptMap(Concept conceptMap) {
        return conceptMap.getURI() + delimiter + conceptMap.getURI();
    }

    private String startPosition(StatementLayout sl) { //FIXME
        ContextMap.Position[] cop = sl.getLine();
        if (cop.length > 0) {
            return cop[0].x + "," + cop[0].y + ",;0";
        } else
            return "0,0;0";
    }

    private String getLineHeadDirection(StatementLayout sl) { //FIXME!!
        java.util.List stack = sm.getStylesForDrawer(sl);
        String returnString;
        String lineHeadInLineEnd = CV.lineHeadInLineEnd.toString();
        String typen =
            (String) sm.getAttributeValue(
                stack,
                lineHeadInLineEnd,
                (String) null);
        returnString = typen;
        //System.out.println("Direction:"+typen);
        if (returnString == null)
            returnString = "f";
        else if (returnString.trim().startsWith("b"))
            returnString = "b";
        else
            returnString = "f";
        //System.out.println("arrowtypen == "+returnString);
        return returnString;
    }

    private int[] getBoundingBox(DrawerLayout dl) {
        ContextMap.BoundingBox cmb = dl.getBoundingBox();
        int[] box = new int[4];
        if (cmb == null) {
            return null;
        } else {
            box[0] = cmb.pos.x;
            box[1] = cmb.pos.y;
            box[2] = cmb.dim.width;
            box[3] = cmb.dim.height;
        }
        return box;
    }

    private int[] getLiteralBoundingBox(StatementLayout sl) {
        ContextMap.BoundingBox cmb = sl.getLiteralBoundingBox();
        int[] box = new int[4];
        if (cmb != null) {
            box[0] = cmb.pos.x;
            box[1] = cmb.pos.y;
            box[2] = cmb.dim.width;
            box[3] = cmb.dim.height;
        }
        return box;
    }

    private String getBoxSizeAndPosition(DrawerLayout dl) {
        int[] boxen = getBoundingBox(dl);
        if (boxen == null) {
            return "0,0,0,0";
        } else {
            String returnString = "";
            for (int i = 0; i < boxen.length; i++) {
                if (i == boxen.length - 1)
                    returnString += boxen[i];
                else
                    returnString += boxen[i] + innerdelimiter;
            }
            return returnString;
        }
    }

    public String getProtocolVersion() {
        return "0.1";
    }

    private String getSurfURI(DrawerLayout dl) {
        String s = dl.getDetailedMap();
        if (s == null)
            return "";
        else
            return s;
    }

    private String getDescription(RDFResource r) {
        String returnString = getTitle(r);
        StmtIterator v = r.getProperties(DC.description);

        if (v != null && v.hasNext()) {
            try {
                returnString = v.nextStatement().getString();
                //System.out.println("v="+returnString);
            } catch (Exception e) {
            }
        }
        return returnString.replace('\n', ' ');
    }

    private String getContent(Component c) {
        //The content cannot be extracted, since there is currently no support 
        //for it in Conzilla vocabulary, this will simply return the same content every 
        //time
        return "http://www.google.com/" + innerdelimiter + "text/html";
    }

    private String getTypeOfConcept(DrawerLayout dl) {
        java.util.List stack = sm.getStylesForDrawer(dl);
        String boxType = CV.boxStyle.toString();
        String type =
            (String) sm.getAttributeValue(stack, boxType, (String) null);
        if (type == null)
            return "rectangle";
        //System.out.println("type == "+type);
        //System.out.println("boxType == "+boxType);
        return type; //FIXME!!
    }

    private String getLine(StatementLayout sl) {
        ContextMap.Position[] cop = sl.getLine();

        if (cop == null) {
            return null;
        } else {
            String returnString = "";
            for (int i = 0; i < cop.length; i++) {
                if (cop.length - 1 == i)
                    returnString += cop[i].x + "," + cop[i].y;
                else
                    returnString += cop[i].x + "," + cop[i].y + ",";
            }
            return returnString;
        }
    }

    private String getTypeOfRelation(StatementLayout sl) { //FIXME!!
        java.util.List stack = sm.getStylesForDrawer(sl);
        String lineType = CV.lineStyle.toString();
        String type =
            (String) sm.getAttributeValue(stack, lineType, (String) null);
        if (type == null) {
            type = "continous";
        }
        //Tracer.debug("type of rel == "+type);
        return type;
        //if (lpt == 0) return "ass"; osv

    }

    private String getTypeOfArrow(StatementLayout sl) {
        java.util.List stack = sm.getStylesForDrawer(sl);
        String returnString = "none";
        String lineHeadType = CV.lineHeadStyle.toString();
        String typen =
            (String) sm.getAttributeValue(stack, lineHeadType, (String) null);
        returnString = typen;
        //System.out.println("arrowtypen == "+returnString);
        return returnString;
    }

    private int getLinePathType(StatementLayout sl) {
        return sl.getPathType();
    }

    private String relationTitlePosition(StatementLayout sl) { //FIXME
        int[] bBox = getLiteralBoundingBox(sl);

        if (bBox != null) {
            String returnString = "";
            for (int i = 0; i < bBox.length; i++) {
                if (bBox.length - 1 == i)
                    returnString += bBox[i];
                else
                    returnString += bBox[i] + ",";
            }
            return returnString;
        } else {
            return "0,0,0,0";
        }
    }

    private String getSubjectNr(StatementLayout sl) {
        DrawerLayout dl = sl.getSubjectLayout();
        int jj = 0;
        if (dl == null)
            return "-1";
        while (!dl.equals(ConceptsURI.get(jj))) {
            jj++;
            if (jj >= ConceptsURI.size())
                return "-1";
        }
        return jj + "";
    }

    private String getPredicate(StatementLayout stl) {
        //System.out.println("stl.getURI="+stl.getURI());
        Concept c = msm.getConcept(stl.getURI());
        if (c.getTriple() != null)
            return c.getTriple().predicateURI().toString();
        else
            return "";
    }

    private String getObjectNr(StatementLayout sl) {
        DrawerLayout dl = sl.getObjectLayout();
        int jj = 0;
        if (dl == null)
            return "-1";
        while (!dl.equals(ConceptsURI.get(jj))) {
            jj++;
            if (jj >= ConceptsURI.size())
                return "-1";
        }
        return jj + "";
    }

    private String getNeighbourhood(RDFResource r) {
        return "";
    }

    private String getTypeOfLine(StatementLayout statm) {
        return statm.getPathType() + "";
    }
}
