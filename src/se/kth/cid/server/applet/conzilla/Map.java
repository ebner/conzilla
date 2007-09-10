/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.applet.conzilla;

import java.util.LinkedList;
import java.util.Vector;
/**
 * @author enok
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Map {
    /**
     * Hela kartan!
     */
    String parseString;

    String URI;
    String title;
    int width;
    int height;

    Vector concepts = new Vector();
    Vector relations = new Vector();

    public Map(String s) {
        parseString = s;
        parse();
    }

    public void addConcept(Concept c) {
        //System.out.println(c.getURI());
        concepts.add(c);
    }

    public Vector getConcepts() {
        return concepts;
    }

    public Vector getRelations() {
        return relations;
    }

    public String getTitle() {
        return title;
    }

    public String getURI() {
        return URI;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void parse() {

        String[] mapParts = new String[4];
        int prev = 0;
        int next = 0;
        int i = 1;

        next = parseString.indexOf("\r\n\r\n", prev + 1);
        if (next > 0)
            mapParts[0] = parseString.substring(prev, next).trim();
        else
            mapParts[0] = parseString;
        prev = next;
        next = parseString.indexOf("\r\n\r\n", prev + 3);
        while (next > 0) {
            mapParts[i] = parseString.substring(prev, next).trim();
            prev = next;
            next = parseString.indexOf("\r\n\r\n", prev + 3);
            while (!mapParts[i].endsWith(">") || mapParts[i].endsWith("\\>")) {
                mapParts[i] += parseString.substring(prev, next).trim();
                prev = next;
                next = parseString.indexOf("\r\n\r\n", prev + 3);
            }
            mapParts[i] += "\r\n";
            i++;
            //if(i>3)
            //   return;
        }
        parseMap(mapParts[1]);
        parseConcepts(mapParts[2]);
        parseRelations(mapParts[3]);
    }

    private void parseMap(String s) {
        //String URI;
        //String title;
        Vector v = new Vector();

        String[] ss = parseAngleBrackets(s);
        for (int i = 0; i < ss.length; i++) {
            String[] tmp = parseSemicolon(ss[i]);
            for (int j = 0; j < tmp.length; j++) {
                v.add(tmp[j]);
            }
        }
        Object[] o = v.toArray();
        if (o.length == 4) {
            String tmp = (String) o[0];
            this.URI = (String) o[0];
            this.title = (String) o[1];
            width = Integer.parseInt((String) o[2]);
            height = Integer.parseInt((String) o[3]);

        } else {
            System.out.println("Fel p? ngt...");
        }
    }

    private void parseConcepts(String s) {

        String[] ss = parseNewLine(s);
        String[] st;
        String[] tt;
        Concept c;

        for (int i = 0; i < ss.length; i++) {
            st = parseAngleBrackets(ss[i]);
            if (st != null && st.length == 4) {
                tt = parseSemicolon(st[0]);
                if (tt != null && tt.length == 3) {
                    c = new Concept(tt[0], tt[1], tt[2]);
                    tt = parseSemicolon(st[1]);
                    if (tt != null && tt.length == 2) {
                        String[] nr = parseComma(tt[0]);
                        c.setXpos(Integer.parseInt(nr[0]));
                        c.setYpos(Integer.parseInt(nr[1]));
                        c.setWidth(Integer.parseInt(nr[2]));
                        c.setHeight(Integer.parseInt(nr[3]));
                        String type = tt[1];
                        c.setType(type);
                        tt = parseSemicolon(st[2]);
                        if (tt != null && tt.length == 2) {
                            if (tt[0].length() > 2) {
                                c.setSurfMap(tt[0]);
                                System.out.println("St?mmer bra!");
                            }
                        }
                    }
                    addConcept(c);
                } else {
                }
            }
        }
    }

    private String[] parseComma(String s) {
        String ss = "";
        Vector v = new Vector();
        int prev = 0;
        int next;
        next = s.indexOf(",");
        while (next > -1) {
            ss = s.substring(prev, next);
            prev = next;
            next = s.indexOf(",", prev + 1);
            while (next > 0 && ss.endsWith("\\,")) {
                ss = s.substring(prev, next);
                prev = next;
                next = s.indexOf(",");
            }
            v.add(ss);
            s = s.substring(prev + 1).trim();
            prev = 0;
            next = s.indexOf(",", prev);
        }
        v.add(s.trim());
        String[] sts = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            sts[i] = ((String) v.get(i)).replaceAll("\\,", ",");
            //System.out.println("sts["+i+"]"+sts[i]);
        }
        return sts;
    }

    private void parseRelations(String s) {
        String[] ss = parseNewLine(s);
        String[] st;
        String[] tt;
        //System.out.println("Parsar relationer");
        Relation tmp;

        for (int i = 0; i < ss.length; i++) {
            st = parseAngleBrackets(ss[i]);
            if (st != null && st.length == 4) {
                tt = parseSemicolon(st[0]);
                if (tt.length == 5) {
                    String[] objsub = parseComma(tt[2]);
                    tmp =
                        new Relation(
                            tt[0],
                            tt[1],
                            Integer.parseInt(objsub[0]),
                            Integer.parseInt(objsub[1]),
                            tt[3],
                            tt[4]);
                    tt = parseSemicolon(st[1]);
                    if (tt != null && tt.length >= 5) {
                        int[] line = parseIntComma(tt[0]);
                        String[] types = parseComma(tt[2]);
                        String lineType = types[0];
                        String arrowType = types[1];
                        String direction = tt[3];
                        String lineDrawType = tt[4];
                        tmp.setLineType(lineType);
                        tmp.setHeadType(arrowType);
                        tmp.setForwardDirection("f".equals(direction));
                        tmp.setPath(line);
                        tmp.setLineDrawType(Integer.parseInt(lineDrawType));
                    }
                    relations.add(tmp);
                }
            }
        }
    }

    private int[] parseIntComma(String s) {
        String[] ss = parseComma(s);
        int[] ret = new int[0];
        if (ss != null) {
            ret = new int[ss.length];
            for (int i = 0; i < ss.length; i++)
                ret[i] = Integer.parseInt(ss[i]);
        }
        return ret;
    }
    private String[] parseNewLine(String s) {
        String ss = "";
        String tmp;
        Vector v = new Vector();
        int prev = 0;
        int next;
        next = s.indexOf("\r\n");
        while (next > -1) {
            ss = s.substring(prev, next);
            prev = next;
            next = s.indexOf("\r\n", prev + 1);
            while (next > 0 && !ss.endsWith(">")) {
                ss = s.substring(prev, next);
                prev = next;
                next = s.indexOf("\r\n");
            }
            v.add(ss);
            s = s.substring(prev + 1);
            prev = 0;
            next = s.indexOf("\r\n", prev);
            //System.out.println("s="+s);
            //System.out.println("ss="+ss);
        }
        //v.add(ss);
        String[] sts = new String[v.size()];
        for (int i = 0; i < v.size(); i++)
            sts[i] = ((String) v.get(i)).trim();
        return sts;
    }

    private String[] parseAngleBrackets(String s) {
        int prev = 0;
        int next = 0;
        s.trim();
        Vector v = new Vector();
        String ss;
        if (!s.startsWith("<") && !s.endsWith(">")) {
            return null;
        }
        next = s.indexOf(">", prev);
        while (next > 0 && s.startsWith("<")) {
            ss = s.substring(prev + 1, next);
            prev = next;
            next = s.indexOf(">", prev);
            while (ss.endsWith("\\>") && next > 0) {
                ss += s.substring(prev, next);
                prev = next;
                next = s.indexOf(">", prev);
            }
            s = s.substring(prev + 1).trim();
            prev = 0;
            next = s.indexOf(">", prev);
            v.add(ss);
        }
        String[] sts = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            sts[i] =
                ((String) v.get(i)).replaceAll("\\>", ">").replaceAll(
                    "\\<",
                    "<");
        }
        return sts;
    }

    private String[] parseSemicolon(String s) {
        String ss = "";
        Vector v = new Vector();
        int prev = 0;
        int next;
        next = s.indexOf(";");
        while (next > -1) {
            ss = s.substring(prev, next);
            prev = next;
            next = s.indexOf(";", prev + 1);
            while (next > 0 && ss.endsWith("\\;")) {
                ss = s.substring(prev, next);
                prev = next;
                next = s.indexOf(";");
            }
            v.add(ss);
            s = s.substring(prev + 1).trim();
            prev = 0;
            next = s.indexOf(";", prev);
        }
        v.add(s);
        String[] sts = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            sts[i] = ((String) v.get(i)).replaceAll("\\;", ";");
            //System.out.println("sts["+i+"]"+sts[i]);
        }
        return sts;
    }
}
