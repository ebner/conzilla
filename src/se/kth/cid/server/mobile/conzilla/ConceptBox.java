/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.mobile.conzilla;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Font; 

public class ConceptBox extends Concept {
    /** Attributes */
    private int currentFace = Font.FACE_SYSTEM;
    //private int form;
    private String form;

    /** Associations */
    private Relation relation;
    
    /**
     * Operation
     *
     */
    public ConceptBox (  ){
    }
    
    /**
     * Operation
     *
     * @param uri
     * @param title
     * @param pos
     * @param Titlepos
     * @param surf_uri
     * @param desc
     * @param form
     * @param content
     * @param id
     */
    public ConceptBox (String uri,String title,int[] pos,int[] Titlepos, String cn, String du, String desc,String form, String c){
	setURI(uri);
	setTitle(title);
	setBoxPos(pos);
	setBoxTitlePos(Titlepos);
	set_grann_URIs(cn);
	detailURI = du;
	setInfo(desc);
	setForm(form);//print("DESC= " + desc);
	//setID(id);
	this.contents = c;
    }
    
    /**
     * Operation
     *
     * @return Relation
     */
    public Relation getRelation (  ){
	return relation;
    }
    
    /**
     * Operation
     *
     * @param boxes
     */
    public void setBoxRelBox ( String[] boxes ){
	
    }
    
     /**
     * Operation
     *
     * @param f
     */
    public void setForm(String f){
 	form = f;
    }
    
    /**
     * Operation
     *
     * @return String
     */
     public String getForm (  ){
	 return form;
     }

    /**
     * Operation
     *
     * @param g
     * @param w : Breden av displ.
     * @param h : H?jden av displ.
     * @param coMap // true om anropp kommer fr?n ContextMap
     */
    public void paint (Graphics g ,int w, int h, int zoom){
	//print("Box paint:");
	String title = getTitle();
	int x, y, al, lo, bx, by,bb, bh;
	int[] pos;
	int[] titlePos;
	//print("I drawHierarchy");
	pos = getBoxPos();
	x=pos[0]; bx=pos[0]; by=pos[1];y=pos[1]; bb=pos[2]; bh=pos[3];
	titlePos = getBoxTitlePos();
	//x = titlePos[0]; 
	//y = titlePos[1]; 
	al = titlePos[2]; 
	lo = titlePos[3];
	int tmpColor = g.getColor();
	Font f = g.getFont();
	int bredd;// = (f.stringWidth(title))/2;
	int hojd;// = (f.getHeight())/2;
	int c = 0;
	boolean langd=false;
	if(zoom > 0){//zoonIn
	    g.setFont(f.getFont(currentFace, 
			       Font.STYLE_PLAIN, 
			       Font.SIZE_LARGE));
	    bredd = (f.stringWidth(title))/2;
	    hojd = (f.getHeight())/2;
	    x=((x+bb/2)-bredd)*zoom;
	    y=((y+bh/2)-hojd)*zoom;
	    bx = bx*zoom; by = by*zoom; bb = bb*zoom; bh = bh*zoom;
	    //print("ZOOM ?R = " + zoom + " x= " + x + " & y= " + y + " & al= " + al + " & lo= " + lo);
	    g.setColor(0xFFFFFF);
	    g.fillRect(bx,by,bb,bh);
	    g.setColor(0x000000);
	    g.drawRect(bx,by,bb,bh);
	    g.setColor(tmpColor);
	    g.drawString(title, x, y, al | lo);
	}else if(zoom < 0){//zoomOut
	    g.setFont(f.getFont(currentFace, 
			       Font.STYLE_PLAIN, 
			       Font.SIZE_SMALL));
	    bredd = f.stringWidth(title);
	    hojd = f.getHeight();
	    while(bredd >= (bb/(-zoom))-6){
		title = title.substring(0,title.length()-1);
		bredd = f.stringWidth(title);
		langd = true;
	    }
	    if(langd)
		title = title.concat("..");
	    x=2+x/(-zoom);//+bredd);
	    y=((y+bh/2)-(hojd/2+5))/(-zoom);
	    //x=((x+bb/2)-(bredd+5))/(-zoom);
	    //y=((y+bh/2)-(hojd+5))/(-zoom);
	    bx = bx/(-zoom); by = by/(-zoom); bb = bb/(-zoom); bh = bh/(-zoom);
	    	    
	    //print("ZOOM ?R = " + zoom + " x= " + x + " & y= " + y + " & al= " + al + " & lo= " + lo);
	    g.setColor(0xFFFFFF);
	    g.fillRect(bx,by,bb,bh);
	    g.setColor(0x000000);
	    g.drawRect(bx,by,bb,bh);
	    g.setColor(tmpColor);
	    if(zoom < -2) 
		return;
	    else{ 
		g.drawString(title, x, y, g.TOP|g.LEFT);
	    }
	}else{
	    bredd = (f.stringWidth(title))/2;
	    hojd = (f.getHeight())/2;
	
	    x=x+(bb/2)-bredd;
	    y=y+(bh/2)-hojd;
	    g.setColor(0xFFFFFF);
	    g.fillRect(bx,by,bb,bh);
	    g.setColor(0x000000);
	    g.drawRect(bx,by,bb,bh);
	    g.drawRect(pos[0],pos[1],pos[2],pos[3]);
	    g.setColor(tmpColor);
	    g.drawString(title, x, y, al | lo);

	    //print("ZOOM ?R = " + zoom + " x= " + x + " & y= " + y + " & al= " + al + " & lo= " + lo);
	}
	//print("\n");
    }
    
    public String arrayStr(){
	//int ant;
	int pos;
	String str="Arrayen f?r Boxen '" + getTitle() +  "'= ";
	for(int i=0; i<4; i++){
	    pos = getBoxPos()[i];
	    //print("I arrayStr & pos[" + i + "]= " + pos);
	    str = str.concat("," + pos + ",");
	}
	return str;
    }
}
