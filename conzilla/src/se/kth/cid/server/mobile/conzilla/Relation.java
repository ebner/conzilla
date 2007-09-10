/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.mobile.conzilla;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class Relation extends Concept {
    /** Attributes */
    
    private int obj, sub, ULMSymbol;
    
    
    /**
     * Operation
     *
     */
    public Relation (  ){
    }

    /**
     * Operation
     *
     *
     * @param uri
     * @param title
     * @param pos
     * @param surf_uri
     * @param desc
     * @param form
     * @param rikt
     * @param obj
     * @param sub
     * @param id
     */
    public Relation (String uri,String title,int[] pos,int[] Tpos,int strokeStyle, String grann_uri,String desc,int uml,
		     char rikt,int[] start,int obj, int sub){
	setURI(uri);
	setRelPos(pos);
	//print("pos.length = "+pos.length);
	setRelTitlePos(Tpos);
	setStrokeStyle(strokeStyle);
	setFormStart(start);
	grann_URIs = grann_uri;
	setInfo(desc);
	setULMForm(uml);//print("UML_Symbol= "+ uml);
	setDir(rikt);//print("rikt= "+ rikt);
	this.obj=obj;
	this.sub=sub;
	setTitle(title);
    }

    /**
     * Operation
     *
     * @param i
     */
    public void setObj(int i){
	obj = i;
    }

    /**
     * Operation
     *
     * @param
     * @return int;
     */
    public int getObj(){
	return obj;
    }

  
    /**
     * Operation
     *
     * @param i
     */
    public void setSub(int i){
	sub = i;
    }

    /**
     * Operation
     *
     * @param
     * @return int;
     */
    public int getSub(){
	return sub;
    }
    
    public String arrayStr(){
	int len = getRelPos().length;
	int pos;
	String str="Arrayen f?r Relationen '" + getTitle() + "'= o har l?ngden=" + len + " :";
	for(int i=0; i< len; i++){
	    pos = getRelPos()[i];
	    str = str.concat("," + pos + ",");
	}
	return str;
    }

     /**
     * Operation
     *
     * @param uml
     */
    public void setULMForm(int uml){
 	ULMSymbol = uml;
    }
    
    /**
     * Operation
     *
     * @return int
     */
     public int getULMForm (  ){
	 return ULMSymbol;
     }

    /**
     * Operation
     *
     */
    public void paint (Graphics g, int w, int h, int zoom){
	//print("I paint rel");
	/**
	if(!coMap){
	    g.setColor(0x00F0F8FF);
	    g.fillRect(0, 0, w, h);
	    g.setColor(0x00000000);
	    //g.fillTriangle(40,40,20,80,80,80);
	}
	*/
	int x, y, al, lo;
	int[] pos;
	int[] titlePos;
	pos = getRelPos();
	int size = pos.length;//
	//int form = getULMForm();//print("I paint form= " + form);
	g.setStrokeStyle(getStrokeStyle());//Graphics.SOLID);
	
	g.setColor(0x00000000);
	if(ULMSymbol == 3)
	    g.setStrokeStyle(1);
	if(zoom > 0){
	    for(int k=0; k<(size/2)-1; k++){
		//g.setColor(0x00000000);
		g.drawLine((pos[2*k])*zoom,(pos[2*k+1])*zoom,(pos[2*k+2])*zoom,(pos[2*k+3])*zoom);
	    }
	}else if(zoom < 0){
	    for(int k=0; k<(size/2)-1; k++){
		//g.setColor(0x00000000);
		g.drawLine((pos[2*k])/(-zoom),(pos[2*k+1])/(-zoom),(pos[2*k+2])/(-zoom),(pos[2*k+3])/(-zoom));
	    }
	}else{
	    for(int k=0; k<(size/2)-1; k++){
		//g.setColor(0x00000000);
		g.drawLine((pos[2*k]),(pos[2*k+1]),(pos[2*k+2]),(pos[2*k+3]));
	    }
	}
	//titlePos =getRelTitlePos();
	//x = titlePos[0]; y = titlePos[1]; al = titlePos[2]; lo = titlePos[3];
	//g.drawString(getTitle(),x, y, al | lo);
	drawUML(g,zoom);
    }

    public void drawUML(Graphics g, int z){
	int eg = 8;
	int x = getFormStart()[0]; int y = getFormStart()[1];
	if(z > 0){
	    eg = 8*z;
	    x = x*z;  y = y*z;
	}else if(z < 0){
	    eg = 8/(-z);
	    x = x/(-z); y = y/(-z);
	}
	char dir = getDir();
	if(ULMSymbol !=0){
	    switch (ULMSymbol){
		//case 0: drawAss(x,y,eg); break;//Association
	    case 1: drawAgg(g,x,y,dir,eg); break;//Aggregation
	    case 2: drawGen(g,x,y,dir,eg); break;//Generalisation
	    case 3: drawClass(g,x,y,dir,eg); break;//Classification
	    case 4: drawEx(g,x,y,dir,eg); break;//Example_of
	    default:
		print("Not defined"); drawX(g,eg); break;// unknown
	    }
	}drawTriangle(g);
    }
    
    void drawAss(Graphics g, int x, int y, char dir, int eg){}
    
    void drawAgg(Graphics g, int x, int y, char dir, int eg){
	eg = eg-1;
	if(dir == 'n'){
	    g.drawLine(x,y,x+eg,y+eg);g.drawLine(x+eg,y+eg,x,y+2*eg);
	    g.drawLine(x,y+2*eg,x-eg,y+eg);g.drawLine(x-eg,y+eg,x,y);
	}else if(dir == 's'){
	    g.drawLine(x,y,x+eg,y-eg);g.drawLine(x+eg,y-eg,x,y-2*eg);
	    g.drawLine(x,y-2*eg,x-eg,y-eg);g.drawLine(x-eg,y-eg,x,y);
	}else if(dir == 'w'){	
	    g.drawLine(x,y,x+eg,y-eg);g.drawLine(x+eg,y-eg,x+2*eg,y);
	    g.drawLine(x+2*eg,y,x+eg,y+eg);g.drawLine(x+eg,y+eg,x,y);	
	}else if(dir == 'e'){
	    g.drawLine(x,y,x-eg,y+eg);g.drawLine(x-eg,y+eg,x-2*eg,y);
	    g.drawLine(x-2*eg,y,x-eg,y-eg);g.drawLine(x-eg,y-eg,x,y);
	}
    }
    
    void drawGen(Graphics g, int x, int y, char dir, int eg){
	if(dir == 'n'){
	    g.setColor(0x00CCFFFF);
	    //g.fillTriangle(x,y,x+eg,y+eg,x-eg,y+eg);
	    g.setColor(0x000000);
	    g.drawLine(x,y,x+eg,y+eg);g.drawLine(x+eg,y+eg,x-eg,y+eg);g.drawLine(x-eg,y+eg,x,y);
	}else if(dir == 's'){
	    g.setColor(0xFFFFFF);
	    //g.fillTriangle(x,y,x+eg,y-eg,x-eg,y-eg);
	    g.setColor(0x000000);
	    g.drawLine(x,y,x+eg,y-eg);g.drawLine(x+eg,y-eg,x-eg,y-eg);g.drawLine(x-eg,y-eg,x,y);
	}else if(dir == 'w'){
	    g.setColor(0xFFFFFF);
	    //g.fillTriangle(x,y,x+eg,y-eg,x+eg,y+eg);
	    g.setColor(0x000000);
	    g.drawLine(x,y,x+eg,y-eg);g.drawLine(x+eg,y-eg, x+eg,y+eg);g.drawLine(x+eg,y+eg,x,y);
	}else if(dir == 'e'){
	    g.setColor(0xFFFFFF);
	    //g.fillTriangle(x,y,x-eg,y+eg,x-eg,y-eg);
	    g.setColor(0x000000);
	    g.drawLine(x,y,x-eg,y+eg);g.drawLine(x-eg,y+eg,x-eg,y-eg);g.drawLine(x-eg,y-eg,x,y);
	}
	
    }

    //FIXA CLASSIFICATION
    void drawClass(Graphics g, int x, int y, char dir, int eg){
	if(dir == 'n'){
	    g.drawLine(x,y,x+eg,y+eg);g.drawLine(x-eg,y+eg,x,y);
	    //g.fillTriangle(x,y,x+eg,y+eg,x-eg,y+eg);
	}else if(dir == 's'){
	    g.drawLine(x,y,x+eg,y-eg);g.drawLine(x-eg,y-eg,x,y);
	    //g.fillTriangle(x,y,x+eg,y-eg,x-eg,y-eg);
	}else if(dir == 'w'){
	    g.drawLine(x,y,x+eg,y-eg);g.drawLine(x+eg,y+eg,x,y);
	    //g.fillTriangle(x,y,x+eg,y-eg,x+eg,y+eg);
	}else if(dir == 'e'){
	    g.drawLine(x,y,x-eg,y+eg);g.drawLine(x-eg,y-eg,x,y);
	    //g.fillTriangle(x,y,x-eg,y+eg,x-eg,y-eg);
	}
    }
    
    void drawEx(Graphics g, int x, int y, char dir, int eg){
	if(dir == 'n'){
	    g.drawLine(x,y,x+eg,y+eg);g.drawLine(x-eg,y+eg,x,y);
	}else if(dir == 's'){
	    g.drawLine(x,y,x+eg,y-eg);g.drawLine(x-eg,y-eg,x,y);
	}else if(dir == 'w'){
	    g.drawLine(x,y,x+eg,y-eg);g.drawLine(x+eg,y+eg,x,y);
	}else if(dir == 'e'){
	    g.drawLine(x,y,x-eg,y+eg);g.drawLine(x-eg,y-eg,x,y);
	}
    }
    
    void drawX(Graphics g, int eg){}
    
    void drawTriangle(Graphics g){
	int width = 8; int height = 8;
	int x = 50; int y= 50;int eg = 8;
	Graphics ig;
	Image triangle = Image.createImage(width,height);
	ig = triangle.getGraphics();
	ig.drawLine(x,y,x-eg,y+eg);
	ig.drawLine(x-eg,y-eg,x,y);
	//Sprite sp = new Sprite(triangle);
	//sp.paint(ig);
	
    }
}
