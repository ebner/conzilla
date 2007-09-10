/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.mobile.conzilla;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;

public class ContextMap{
  
    /** Attributes */
    private String mapURI, title;
    private int Rel_nr;//Antalet Relationer
    private int Box_nr;//antalet Boxar
    protected static int[] mapArr;//Size of the map (x,y)Anger h?gra nedre pkten. 
    private int[] allBoxPos;
    //private float zoom=1;
    /** Associations */
    
    private Vector _rel;
    //private Header _head;
    private Vector _box;
    private ConceptBox box;
    private Relation rel;
    /**
     * Operation
     *
     * @param box
     * @param rel
     * @param info
     */
    public ContextMap (String t, String uri, int b, int r, int[] p){
	mapURI = uri;//setURI(uri);
	title = t;//setTitle(t); 
	//set_grann_URI(uri);//Context map har inga grannURI bara concepten:Om contextMap best?r av en box
	//_head = new Header(vers);
	Box_nr = b; 	
	Rel_nr = r; 
	_rel = new Vector(r);
	_box = new Vector(b);
	this.mapArr = p;
    }
    
    
    /**
     * Operation
     *
     * @param info
     */
    void setURI(String u){
	mapURI = u;
    }
    
    String getURI(){
	return mapURI;
    }
    
    void setTitle(String t){
	title = t;
    } 
    
    String getTitle(){
	return title;
    } 
    
    /**
     * Operation
     *
     */
    public void paint (Graphics g, int w, int h, int zoom){
	//zoom = z;
	//print("I comap");
	g.setColor(0x00CCFFFF);
	g.fillRect(0, 0, w, h);
	g.setColor(0x00000000);
        g.drawRect(2,2,mapArr[0]-2,mapArr[1]);
	int st = mapArr[0]; int end = mapArr[1]; 
	//Kartan hamnar mitt p? sk?rmen
	//g.drawRect(-st/2, -end/2, st+st/2,end+end/2);
	//g.translate((w-st)/2, (h-end)/2);
	//print("st= " + st + " & end= " + end);
	
	int relNr=getnrRel();
	for(int j=0; j<relNr; j++){
	    rel = getRelation(j);
	    rel.paint(g,w,h, zoom);
	}
	for(int i=0; i<getnrBox(); i++){
	    box = getBox(i);
	    box.paint(g,w,h, zoom);
	}
	//print("\n");
	System.gc();
    }
    
    /**
     * Operation
     *
     * @param id
     * @return ConceptBox
     */
    public ConceptBox getBox ( int id ){ 
	return (ConceptBox)_box.elementAt(id);
    }
    
    /**
     * Operation
     *
     * @param id
     * @return Relation
     */
    public Relation getRelation ( int id ){
	return (Relation)_rel.elementAt(id);
    }
    
    /**
     public Header getHead (  ){ 
     return _head;
     }
     
     public void setHead ( Header head ){ 
     _head=head;
     }
    **/
    
    /**
     * Operation
     *
     * @param box
     */
    public void setBox ( ConceptBox box ){ 
	int length=4;
	_box.addElement(box);
       if(allBoxPos == null){
	   allBoxPos = new int[4];
       }else{
	   int[] tmp = allBoxPos;
	   length += allBoxPos.length;
           allBoxPos = new int[length];
           System.arraycopy(tmp,0,allBoxPos,0,tmp.length);
       }
       allBoxPos[length-4] = box.getBoxPos()[0];
       allBoxPos[length-3] = box.getBoxPos()[1];
       allBoxPos[length-2] = box.getBoxPos()[2];
       allBoxPos[length-1] = box.getBoxPos()[3];
    }
    
    /**
     * Operation
     *
     * @param rel
     */
    public void setRelation(Relation rel){ 
	_rel.addElement(rel);
    }
   
    /**
     * Operation
     *
     * @return int
     */
    public int getnrBox ( ){ 
	return _box.size();
    }    
    
    /**
     * Operation
     *
     * @return int
     */
    public int getnrRel ( ){ 
	return _rel.size();
    }
    
    public int[] getAllBoxPos(){
	return allBoxPos;
    }
    
    
    /**
     * Operation
     *
     * @param
     */
    public void boundary (  ){ }
    
    /**
     * Operation
     *
     * @param size
     */
    public void zoom ( int size ){ }  
    
    /**
     * Operation
     *
     * @param box
     */
    public void zoomBox ( int box ){ }
    
    /**
     * Operation
     *
     * @return Vector
     */
    public Vector getBoxVec (  ){
	return _box; 
    }
    
    /**
     * Operation
     *
     * @return Vector
     */
    public Vector getRelVec (  ){
	return _rel; 
    }
}
