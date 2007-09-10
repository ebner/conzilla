/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.mobile.conzilla;

import java.io.PrintStream;
import java.lang.Integer;
import java.util.Vector;

public class Parser {
    /** Attributes */
    int boxs, rels;
    private boolean connMsg=true;
    
    /** Associations */
    private java.lang.StringBuffer ff; //FetchedFile
    //private Header head;
    private ContextMap coMap;
    //private StringBuffer buffer;
    private String buffer;
    
    public Parser(String buff){ 
	buffer = buff;
    }

    /**
     * @param buffer
     **/
    public void parse(){
        String str = buffer;
        String[] mapParts = new String[4];
        int prev = 0;
        int next = 0;
        int i = 1;

        next = str.indexOf("\r\n\r\n", prev + 1);
        if (next > 0)
            mapParts[0] = str.substring(prev, next).trim();
        else
            mapParts[0] = str;
        prev = next;
        next = str.indexOf("\r\n\r\n", prev + 3);
	while (next > 0) {
            mapParts[i] = str.substring(prev, next).trim();
            prev = next;
            next = str.indexOf("\r\n\r\n", prev + 3);
            while (!mapParts[i].endsWith(">") || mapParts[i].endsWith("\\>")) {
                mapParts[i] += str.substring(prev, next).trim();
                prev = next;
                next = str.indexOf("\r\n\r\n", prev + 3);
            }
            mapParts[i] += "\r\n";
            i++;
            //if(i>3)
            //   return;
        }
        contextMap(mapParts[1]);
	boxPart(mapParts[2]);
        relPart(mapParts[3]);
      }
    
    /*public boolean getConnMsg(){
	return connMsg;
	}*/
    
    /**
     * Operation
     *
     * @param s
     */
    private void contextMap(String s){
        Vector v = new Vector();
	String[] tmp1 = parseAngleBrackets(s);
	for(int i=0;i<tmp1.length;i++){
	    String[] tmp2 = parseSemicolon(tmp1[i]);
	    for(int j=0;j<tmp2.length;j++){
		v.addElement(tmp2[j]);
	    }
	}
        if(v.size()>=4){
	    if(v.size()>4){
		System.out.println("Ngt fel p? protokoll, f?rs?ker iaf");
	    }
	    String uri = (String) v.elementAt(0);
            String title = (String) v.elementAt(1);
	    int [] mapArr=new int[2];
            boxs = 0;//Integer.parseInt(getSubPart(s,";",4));
	    rels = 0;//Integer.parseInt(getSubPart(s,";",5));
	    mapArr[0] = Integer.parseInt((String) v.elementAt(2));
            mapArr[1] = Integer.parseInt((String) v.elementAt(3));
	    coMap = new ContextMap(title,uri,boxs,rels,mapArr);
	}else{
	    System.out.println("Funkar ej!");
	}
    }

    /**
     * Operation
     *
     * @param s
     */
    private void boxPart(String s){//H?r ?r s= Str?ngen som inneh?ller alla Boxar
	String b_uri="", detailURI="", title="", Bpos, Tpos, surf_uri, desc="",form="", contents;
        String NeibourInfo="";
	ConceptBox box;
	int[] boxPos={0};
        int[] titlePos = {0,0,0,0};
         
        String[] tmp1=parseNewLine(s);
        String[] tmp2;
        String[] tmp3;

        for(int i=0;i<tmp1.length;i++){
	    tmp2 = parseAngleBrackets(tmp1[i]);
	    if(tmp2!=null && tmp2.length==4){
		tmp3 = parseSemicolon(tmp2[0]);
		if(tmp3!=null && tmp3.length==3){
		    b_uri=tmp3[0];
                    title=tmp3[1];
                    desc=tmp3[2];
		}
		tmp3=parseSemicolon(tmp2[1]);
                if(tmp3!=null && tmp3.length==2){
		    String[] nr = parseComma(tmp3[0]);
                    int[] boxPosition = {Integer.parseInt(nr[0]),Integer.parseInt(nr[1]),
					 Integer.parseInt(nr[2]),Integer.parseInt(nr[3])};
                    boxPos = boxPosition;
		    form = tmp3[1];
		}
		tmp3 = parseSemicolon(tmp2[2]);
		if(tmp3!=null && tmp3.length==2){
		    detailURI = tmp3[0];
		}
		box=new ConceptBox(b_uri, title , boxPos, titlePos, detailURI, detailURI, desc, form,"");
		coMap.setBox(box);
	    }
	}
    }

    /**
     * Operation
     *
     * @param s
     */
    //Har ej implememtrat contexualNeibours lista o contents
    private void relPart(String s){
	String r_uri="",title="",style="",nrPos="",RPos,TPos,surf_uri="",desc="",ULM_rikt="";
	String ULM,rikt,xy;
	Relation rel;
	char dir='f';
	//print("I RelPart: 1");
	int obj=0, sub=0, posToInt, ULM_Symbol=0, strokeStyle=0;
	int[] posArr={}, titlePos=new int[4], ULMStart=new int[2];
        
        String[] tmp1=parseNewLine(s);
        String[] tmp2;
        String[] tmp3;
        String[] tmp4;

	for(int i=0;i<tmp1.length;i++){
	    tmp2=parseAngleBrackets(tmp1[i]);
	    if(tmp2!=null && tmp2.length==4){
		tmp3=parseSemicolon(tmp2[0]);
		if(tmp3!=null && tmp3.length==5){
		    r_uri=tmp3[0];
		    //String reification = tmp3[1];
		    title = tmp3[3];
		    desc=tmp3[4];
		    tmp4=parseComma(tmp3[2]);
		    if(tmp4!=null && tmp4.length==2){
			sub=Integer.parseInt(tmp4[0]);
			obj=Integer.parseInt(tmp4[1]);
		    }
		}
		tmp3=parseSemicolon(tmp2[1]);
		if(tmp3!=null && tmp3.length==5){
		    tmp4=parseComma(tmp3[0]);
		    posArr=new int[tmp4.length];
		    for(int j=0;j<posArr.length;j++){
			posArr[j]=Integer.parseInt(tmp4[j]);
			if(j>=0 && j<2)
			    ULMStart[j]=posArr[j];
		    }
		    tmp4=parseComma(tmp3[1]);
		    if(tmp4!=null && tmp4.length>=4){
			for(int j=0;j<3;j++){
			    titlePos[j]=Integer.parseInt(tmp4[j]);
			}
		    }//Rest of the graphical information will be parsed here 
		}
		tmp3=parseSemicolon(tmp2[2]);
		if(tmp3!=null && tmp3.length==2){
		    surf_uri=tmp3[0];
		    //Parse the contextual neighbourhood here in tmp3[1]
		}
	    }
	    rel = new Relation(r_uri,title,posArr,titlePos,strokeStyle,surf_uri,desc,ULM_Symbol,dir,ULMStart,obj,sub);
	    coMap.setRelation(rel);
	}
    }

    /**
     * Operation
     *
     * @param pos,posInt
     * @return int[]
     */
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
            v.addElement(ss);
        }
        String[] sts = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            sts[i] = (String) v.elementAt(i);
	    //((String) v.get(i)).replaceAll("\\>", ">").replaceAll(
	    //      "\\<","<");
        } 
        return sts;
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
            v.addElement(ss);
            s = s.substring(prev + 1).trim();
            prev = 0;
            next = s.indexOf(",", prev);
        }
        v.addElement(s.trim());
        String[] sts = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            sts[i] = (String) v.elementAt(i);//((String) v.get(i)).replaceAll("\\,", ",");
            //System.out.println("sts["+i+"]"+sts[i]);
        }
        return sts;
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
            v.addElement(ss);
            s = s.substring(prev + 1);
            prev = 0;
            next = s.indexOf("\r\n", prev);
            //System.out.println("s="+s);
            //System.out.println("ss="+ss);
        }
        //v.add(ss);
        String[] sts = new String[v.size()];
        for (int i = 0; i < v.size(); i++)
            sts[i] = ((String) v.elementAt(i)).trim();
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
            v.addElement(ss);
            s = s.substring(prev + 1).trim();
            prev = 0;
            next = s.indexOf(";", prev);
        }
        v.addElement(s);
        String[] sts = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            sts[i] = (String)v.elementAt(i);//((String) v.get(i)).replaceAll("\\;", ";");
            //System.out.println("sts["+i+"]"+sts[i]);
        }
        return sts;
    }

    /**
     * Operation
     * Seperating specific part 'idx' (idx = 1,2,...,n: n=nr. of line)of a line in a one 
     * line string 's' using delimiter del(=';' in our protocoll).
     *  
     * @param s
     * @String
     */
     
    public static String getSubPart(String s, String del, int idx){
	//print("I getSubPart str= " + s);
	int start=0; int end=0; 
	for(int i=0; i<idx; i++){
	    start = end;
	    end = s.indexOf(del,start+1);
	}
	//print("I getSubPart idx= "+ idx + " start= " + ((idx > 1)? start+1 : start) +" end= "+ end);
	//print("I getSubPart Utstr= "+ s.substring(((idx > 1)? start+1 : start),end));
	return s.substring(((idx > 1)? start +1 : start),end);
    }
    
    /**
     * Operation
     *
     * @param s
     */

    public ContextMap getMap(){
	return coMap;
    }


}
