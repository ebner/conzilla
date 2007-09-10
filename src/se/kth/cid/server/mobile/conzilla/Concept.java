/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.mobile.conzilla;

import javax.microedition.lcdui.Graphics;

abstract class Concept{


    /** Attributes */
    private int ID;
    private int type, strokeStyle;
    //private float zoom;
    //A concept has 3-URIs:Concept itself=URI, detail-mapURI and a list of contextual neibourhoods=grann_URIs
    String title,info,URI,detailURI;

    String grann_URIs, contents;
    //private String form;
    //private int UMLSymbol;
    private char dir;
    private int[] Bpos;//Array f\uffffr Boxpositioner & titelpositioner
    private int[] TBoxpos;
    private int[] Rpos;//Array f\uffffr Rel.pos. & titelpositioner
    private int[] TRelpos;
    private int[] formStart;
    //Graphics g;


    /**
     * Operation
     *
     * @param c
     */
    /**
     * Operation
     *
     * @return String
     */
    public String getInfo (  ){
        //print("Desc= " + info);
        return info;
    }

    /**
     * Operation
     *
     * @param info
     */
    public void setInfo (String info){
        this.info = info;
    }

    /**
     * Operation
     *
     * @param d
     */
    public void setDir(char d){
        dir = d;
    }

    /**
     * Operation
     *
     * @return char
     */
    public char getDir(){
        return dir;
    }


    /**
     * Operation:
     *
     * @return String
     * The URI of the concept
     */
    public String getURI (  ){
        return URI;
    }

    /**
     * Operation
     *
     * @param url
     */
    public void setURI ( String u ){
        URI = u;
    }

    public String getDetailURI (  ){
        return detailURI;
    }

    public void setDetailURI ( String u ){
        detailURI = u;
    }

    /**
     * Operation
     *
     *
     * @return String
     * A list of URIs of contextual neibourhoods of a concept.
     */
    public String get_grann_URIs (  ){
        return grann_URIs;
    }

    /**
     * Operation
     *
     * @param url
     */
    public void set_grann_URIs ( String u ){
        grann_URIs = u;
    }

    public String get_content (  ){
        return contents;
    }

    public void set_contents ( String c ){
        contents = c;
    }

    /**
     * Operation
     *
     * @return String
     */
    public String getTitle (){
        return title;
    }


    /**
     * Operation
     *
     * @param g
     * @param w
     * @param h
     * @param b
     *
     */
    public void paint (Graphics g, int w, int h, int zoom){ }

    /**
     * Operation
     *
     * @return int
     */
    public int getType (  ){
        return type;
    }

    /**
     * Operation
     *
     * @param t
     */
    public void setType ( int t ){
        type = t;
    }

    /**
     * Operation
     *
     * @param ss
     */
    public void setStrokeStyle(int ss){
        strokeStyle = ss;
    }

    /**
     * Operation
     *
     * @return int
     */
    public int getStrokeStyle(){
        return strokeStyle;
    }

    /**
     * Operation
     *
     * @param t
     */
    public void setTitle(String t){
        this.title = t;
    }

    /**
     * Operation
     *
     * @return int
     */
    public int getID (){
        return ID;
    }

    /**
     * Operation
     *
     * @param id
     * @return
     */
    public void setID (int id ){
        ID = id;
    }


    /**
     * Operation
     *
     * @return int[];
     */
    public int[] getBoxPos(){
        return Bpos;
    }

    /**
     * Operation
     *
     * @param p
     * @return
     */
    public void setBoxPos(int[] p){
        Bpos = p;
    }

    /**
     * Operation
     *
     * @param
     * @return int[];
     */
    public int[] getFormStart(){
        return formStart;
    }

    /**
     * Operation
     *
     * @param f[]
     * @return
     */
    public void setFormStart(int[] f){
        formStart = f;
    }

    /**
     * Operation
     *
     * @param p[]
     * @return
     */
    public void setRelPos(int[] p){
        Rpos = p;
        for(int i=0; i<p.length; i++){
            //print("v\uffffrde["+i+"]= " +p[i]);
        }
    }

    /**
     * Operation
     *
     * @param
     * @return int[];
     */
    public int[] getRelPos(){
        return Rpos;
    }

    /**
     * Operation
     *
     * @param tp[]
     * @return
     */
    public void setBoxTitlePos(int[] tp){
        TBoxpos = tp;
    }

    /**
     * Operation
     *
     * @param
     * @return int[]
     */
    public int[] getBoxTitlePos(){
        return TBoxpos;
    }

    /**
     * Operation
     *
     * @param tp[]
     * @return
     */
    public void setRelTitlePos(int[] tp){
        TRelpos = tp;
    }

    /**
     * Operation
     *
     * @param
     * @return int[]
     */
    public int[] getRelTitlePos(){
        return TRelpos;
    }

     /**
     * Operation
     *
     * @param s
     * @return
     */
    public void print(String s){
        System.out.println("I Concept: " + s);
    }
}
