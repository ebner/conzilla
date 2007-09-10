/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.mobile.conzilla;

import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Font;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.Canvas;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Graphics;
import java.io.PrintStream;
import java.util.Vector;


public class Map extends Canvas implements CommandListener {
    
    ContextMap coMap = null;
    //HttpConn ht = null;
    Concept currCpt;
    ConceptBox box = null;
    Relation rel = null;
    Parser pars = null;
    static Font font;
    Graphics g;
    public Display disp=null;
    boolean  popup=false;
    Forms F; //Welcome wcome;
    private int zoom=0;
    HttpConn hc;
    boolean mouseOverBox = false;
    static final long toPopupDelay = 500;
    static final long popupLifetime = 2000;
    
    Timer timer1, timer2;
    MyTimerTask1 timerTask1;
    MyTimerTask2 timerTask2;

    //private double zoom=1;
    Displayable prev;
    //MIDlet midlet;
    int w, h, chosenBox;//w=display width & h=higth, chosenBox=Fokus p? box med mus
    int xdir = 0;
    int ydir = 0;
    private Hashtable concCMD=null;	
    //String URI,url, rel_url, title, desc;
    String mapURI, conceptURI, conceptDetailURI, title;
    
            
    static final Command CONCEPT = new Command("Concept(s)", Command.SCREEN, 1);
    //static final Command FORWARD = new Command("Forward", Command.SCREEN, 0);
    static final Command REL = new Command("Relation(s)", Command.SCREEN, 1);		
    static final Command BOKMARK = new Command("Bokmarks...", Command.SCREEN, 1);		
    
    static final Command LINE = new Command("....................", Command.SCREEN, 1);	
    static final Command LINE_1 = new Command(".........", Command.SCREEN, 1);	

    static final Command DESC = new Command("Description...", Command.SCREEN, 1);		
    static final Command ADDtoBOK = new Command("Add this to bokmarks",Command.SCREEN, 1);

    static final Command ZOOMIN = new Command("Zoom In", Command.SCREEN, 1);	
    static final Command ZOOMOUT = new Command("Zoom Out", Command.SCREEN, 1);	

    Vector currCMDs =  new Vector(20,5);//Ha Koll !!!!
    Command CMD=null;
    int size=0;
    int mx, my;//Mouse x, y -coord
    static int nyy=0;
    static int nyx=0;

    public Map (Display dis, Forms f){
	F = f;
	//url = F.url;
	disp = dis;
	coMap = F.coMap;
	title = coMap.getTitle();
	initiate();
	//currCpt = coMap;//Kan inte ty coMap ?r ej rn concept
	disp.setCurrent(this);
    }	


    void fixCMDs(){
	addCommand(LINE); //addCommand(ZOOMIN); addCommand(ZOOMOUT);
	addCommand(F.OPEN);    addCommand(F.GOTO);
	addCommand(F.SAVE);    addCommand(ADDtoBOK);
	addCommand(F.BOKMARK); addCommand(F.BACK);
    }
    
    void initiate(){
	mapList();
	fixCMDs();
	h = getHeight();
	w = getWidth();
	mx = w/2; 
	my = h/2;
	setCommandListener(this);
	//print("init() KLAR!: wide = " + w + " hight= " + h + "\n");	
    }

    void mapList(){
	Content conts;
	int nrBox = coMap.getnrBox();
	int nrRel = coMap.getnrRel();
	int contNr;
	String cmd = "";
	
	Command com=null;
	concCMD = new Hashtable(nrBox);//+nrRel);
	addCommand(ZOOMIN); addCommand(ZOOMOUT); addCommand(LINE_1);//addCommand(FORWARD);
	for(int i=0; i<nrBox; i++){
	    box = coMap.getBox(i);
	    
	    //contentList.setCommandListener(this);contentList
	    cmd = box.getTitle();
	    if(cmd.length()!=0){//(cmd !=null){//cmd.equals(" ") ? "node" + i : cmd;//If no title
		com = new Command(cmd, Command.SCREEN,1);
		concCMD.put(com,box);
		currCMDs.addElement(com);
		addCommand(com);
	    }
	}
	size = currCMDs.size();//print("MapList size= " + size + "\n");
    }

    public void commandAction(Command c, Displayable d) {
	//print(" CMD = " + c.getLabel());
	//print("URI 1= " + URI);
	if (c == F.BACK) {
	    F.back = true;
	    //F.botton = true;
	    disp.setCurrent(F.backOrForth('b'));
	}else if(c == ZOOMIN){
	    zoom("in");
	}else if(c == ZOOMOUT){
	    zoom("out");
	    //}else if(c == FORWARD){
	    //print("Forward");
	    //disp.setCurrent(F.backOrForth('f'));
	}else if(c == F.OPEN){ 
	    //F.historyBuffer.addElement(F.openFile);
	    //F.stackPointer++;
	    F.current = F.openFile;
	    disp.setCurrent(F.openFile);
	    //F.openFile.prev = this;//cleanMouse();
	}else if(c == F.GOTO){
	    F.tf.setString("http://");
	    //F.historyBuffer.addElement(F.wForm);
	    //F.stackPointer++;
	    F.current = F.wForm;
	    disp.setCurrent(F.wForm);
	}else if(c == F.SAVE){
	    //title = coMap.getTitle();
	    F.files.put(title, F.protocol);//cleanMouse();
	    F.openFile.append(title,null);
	    F.msgAl.setString("File '" + title + "' saved!");
	    disp.setCurrent(F.msgAl, this);
	}else if(c == F.BOKMARK){
	    //F.bokmarkList.prev = this;
	    //F.historyBuffer.addElement(F.bokmarkList);
	    //F.stackPointer++;
	    F.current = F.bokmarkList;
	    disp.setCurrent(F.bokmarkList);
	}else if(c == ADDtoBOK){
	    //print("& AddtoBok url= " + URI);
	    F.bokmarksVec.addElement(mapURI);//	    F.bokmarksVec.addElement(URI);
	    F.bokmarkList.append(mapURI,null);// F.bokmarkList.append(URI,null);//cleanMouse();
	    F.msgAl.setString("Bokmark '" + mapURI + "' saved!");//F.msgAl.setString("Bokmark '" + URI + "' saved!");
	    disp.setCurrent(F.msgAl,this);
	}else if(c == LINE){
	    return;
	}else if(concCMD.containsKey(c)){
	    currCpt = (Concept)concCMD.get(c);//Anv. i paint
	    //url = currCpt.getURI();	
	    //F.box = (ConceptBox)currCpt;
	    //F.url = currCpt.getURI();
	    //F.rel_url = currCpt.get_grann_URI();
	    //print("grann_uri= " + rel_url);
	    //desc = currCpt.getInfo();//print("desc= " + desc);
	    F.descBox.setString(currCpt.getInfo());
	    //F.removeHistory();
	    //F.historyBuffer.addElement(F.descBox);
	    //F.stackPointer++;
	    //print("I Map & L?gger in descBox i bufferten");
	    F.current = F.descBox;
	    disp.setCurrent(F.descBox);
	}
	    /**
	       }else if(concCMD.containsKey(c)){
	       currCpt = (Concept)concCMD.get(c);//Anv. i paint
	       //url = currCpt.getURI();	
	       F.box = (ConceptBox)currCpt;
	       F.url = currCpt.getURI();
	       F.rel_url = currCpt.get_grann_URI();
	       //print("grann_uri= " + rel_url);
	       desc = currCpt.getInfo();//print("desc= " + desc);
	       F.descBox.setString(desc);
	       //F.removeHistory();
	       //F.historyBuffer.addElement(F.descBox);
	       //F.stackPointer++;
	       //print("I Map & L?gger in descBox i bufferten");
	       F.current = F.descBox;
	       disp.setCurrent(F.descBox);
	       }
	    **/
	cleanMouse();
	System.gc();
    }
    
    public void zoom(String z){
	if(z.equals("in")){
	    if(zoom + 2 >= 4){ zoom = 4; return ;}else{zoom += 2; repaint();}// dvs *2
	}else{
	    if(zoom - 2 <= -6){ zoom = -6; return ;}else{zoom -= 2; repaint();}// dvs /2
	}
    }

    public void cleanScreen(Graphics g){
	g.setColor(0x00CCFFFF);
	g.fillRect(0, 0, w, h);
	g.setColor(0x00000000);
    }
    
    void rmCMDs(){
	//print("Remove size= " + size);
	for(int i=0; i<size; i++){
	    CMD = (Command)currCMDs.elementAt(i);
	    removeCommand(CMD);
	    //print("ermove size= " + CMD.getLabel());
	}
	currCMDs.removeAllElements();
	size = currCMDs.size();
	//print("Remove size= " + size + "\n");
    }
  
    // sT?DS ej av programmet
    protected void keyRepeated(int key){//keyRepeated(int key) {
	int action = getGameAction(key);
	int dir = 0;
	//print("Key= " + key + " & getGameAc(key)= " + action);
	switch (key) {
	case 49: 	
	    mx = w/2 - 2; 
	    my = h/2 - 2;
	    break;
	case 51: 	 
	    mx = w/2 + 2;
	    my = h/2 - 2;
	    break;
	case 55: 	
	    mx = w/2 - 2;
	    my = h/2 + 2;
	    break;
	case 57:
	    mx = w/2 + 2;
	    my = h/2 + 2;
	    break;
	default:
	    return;
	    //disp.callSerially(this);	
	}
	repaint();
    }  

    protected void keyPressed(int key) {
	int mv = 4;
	if(key >= -4 && key <= -1){
	    setXYDir(key);
	}else if(key >= 49 && key <= 57){
	    switch (key){
	    case 48: //0
		print("Key=0 == Forward");
		//disp.setCurrent(F.backOrForth('f'));
		break;
	    case 49: //1	
		moveMouse(49, -mv, -mv, w/2, h/2);
		break;
	    case 50: 	//2
		moveMouse(50, 0, -mv, 0, h/2);
		break;
	    case 51://3 	 
		moveMouse(51, mv, -mv, -w/2, h/2);
		break;
	    case 52://4 	 
		moveMouse(52, -mv, 0, w/2, 0);
		break;
	    case 53://5 
		//print("Knapp=5");
		detailSurf();
		//print("Efter knapp 5");
		break;
	    case 54://6 	
		moveMouse(54, mv, 0, -w/2, 0);
		break;
	    case 55://7 	
		moveMouse(55, -mv, mv, w/2, -h/2);
		break;
	    case 56://8 
		moveMouse(56, 0, mv, 0, -h/2);
		break;
	    case 57://9
		moveMouse(57, mv, mv, -w/2, -h/2);
		break;
	    default:
		return;
	    }
	}
	repaint();
	findBox();
    }
    
    void moveMouse(int key, int x , int y, int wid, int hei){
	mx += x; my += y;
	moveXY(wid, hei, key);
    }

    void mouse(Graphics g){
	//g.fillTriangle(mx,my,mx+10,my+8,mx+8,my+10);
	g.drawLine(mx,my,mx+8,my+5);
	g.drawLine(mx+8,my+5,mx+5,my+8);
	g.drawLine(mx+5,my+8,mx,my);
	//g.drawString("(" + mx + "," + my + ")" , mx, my, 0);
    }
    
    public void cleanMouse(){
	xdir=0 ; ydir=0 ; mx=w/2 ; my=h/2 ; nyx=0 ; nyy=0 ;
    }
 

    public void findBox(){
	int[] pos = coMap.getAllBoxPos();
        int x, y, xx, yy;// br, ho;
	mouseOverBox = false;
	if (timer1 != null) {
 	    timer1.cancel();
 	    timer1 = null;
	}
 	if (timer2 != null) {
 	    timer2.cancel();
	    timer2 = null;
 	}
	popup = false;
	for(int k =0; k<(pos.length/4); k++){
	    x = pos[4*k]; y=pos[4*k+1]; xx=pos[4*k+2]; yy=pos[4*k+3];
	    if(zoom > 0){
		x = x*zoom; y=y*zoom; xx=xx*zoom; yy=yy*zoom;
	    }else if(zoom < 0){
		x = x/(-zoom); y=y/(-zoom); xx=xx/(-zoom); yy=yy/(-zoom);
	    }
	    //print("Box nr: " + k + " :x= " + x + " :y= " + y + " :xx= " + xx + " :yy= " + yy + " & mx= " + mx + " :my= " + my + "\n");
	    //print("Vilkor::: mx>=x: " + (mx >= x) + "   :mx<=x+xx: " + (mx <= x+xx) + 
	    //"   :my>=y: " + (my >= y) + "   :my<=y+yy: " + (my <= y+yy) + "   ::Res= " +
	    //print("Inne i Box:" + (mx >= x && mx <= x+xx && my >= y && my <= y+yy) + "\n");
	    if(mx >= x && mx <= x+xx && my >= y && my <= y+yy){
		//print("Inne i Box: true");
		chosenBox = k;
		box = coMap.getBox(k);
		title = box.getTitle();
		mouseOverBox = true;
		timer1 = new Timer();
		timerTask1 = new MyTimerTask1();
		timer1.schedule(timerTask1, toPopupDelay);
		repaint();
		break;
	    }
	}
	//print("Fokus ?r p?= " + " " + box.getTitle());
	//return choose;
    }

    public void detailSurf(){
	if(mouseOverBox){
	    //F.removeHistory();
	    String uri = box.grann_URIs;
	    //String uri = box.getURI();
	    String url = box.getURI();
	    //hc = new HttpConn(uri);
	    //F.initConn(uri);//initCanvas(url);
	    //print("Surf url= " + url);
	    //print("Surf rel_url= " + uri);
	    //F.initConn(uri);
	    F.initConn(box.getDetailURI());//F.initConn(uri);
	    //F.back = false;
	    F.initMap();
	    
	    //prev = descBox;
	}
    }

    
    public class MyTimerTask1 extends TimerTask{
	
	public void run(){
	    popup=true;
	    timer2 = new Timer();
	    timerTask2 = new MyTimerTask2();
	    timer2.schedule(timerTask2, popupLifetime);
	    repaint();
	}
    }

    public class MyTimerTask2 extends TimerTask{
	
	public void run(){
	    popup=false;
	    repaint();
	}
    }

    public void boxAction(String t){//Graphics g){
	
	//box = coMap.getBox(chosenBox);
	//desc = box.getInfo();
	//title = box.getTitle();
	g.drawRect(5,5,w-10,h/2);
	g.drawString(t, (w/2)-10, (h/2)-h/4, 0 | 0);
    }

    public void drawPopup(){
	//print("I draw & title= " + title + "\n"); 
	//x=x+(bb/2)-bredd;
	//y=y+(bh/2)-hojd;
	int br = (font.stringWidth(title))/2;     
	int ho = (font.getHeight())/2;            
	//int Tx=(((nyx+w-10)/2)-br);              
	//int Ty=(nyy+(h/3)-ho);                   
	int Tx = nyx+w/2-br;
	int Ty = nyy+h/2-ho;
	int tmpColor = g.getColor();              
	g.setColor(0xFFFFFF);                     
	//g.fillRect(nyx+w/4,nyy+h/4,(w-10),(h/3));             
	g.fillRect(nyx+10,nyy+h/4,(w-20),(h/3));             
	g.setColor(0x000000);                     
	//g.drawRect(nyx+w/4,nyy+h/4,(w-10),(h/3));             
	g.drawRect(nyx+10,nyy+h/4,(w-20),(h/3));             
	g.setColor(tmpColor); 
	g.setFont(Font.getFont(Font.FACE_SYSTEM, 
			       Font.STYLE_PLAIN, 
			       Font.SIZE_LARGE));
	g.drawString(title, Tx, Ty, 0 | 0);    
	//try{
	//(new Object()).wait(3000);
	//}catch(InterruptedException i){ print("Fick interrupptedExc");}
    }

    public void setXYDir(int x){
	int mapX = coMap.mapArr[0];
	int mapY = coMap.mapArr[1];
	int fri = 20;
	switch (x) {
	case -4: if((xdir - w/2) <= -mapX) xdir = -mapX-fri; else xdir -= w/2; mx += w/2; nyx += w/2;  break; //Pil H?ger
	case -3: if((xdir + w/2) >= mapX) xdir = mapX+fri; else xdir += w/2; mx -= w/2; nyx -= w/2; break; //Pil V?nster
	case -1: if((ydir + h/2) >= mapY) ydir = mapY+fri; else ydir += h/2; my -=h/2; nyy -= h/2; break;//Pil upp
	case -2: if((ydir - h/2) <= -mapY) ydir = -mapY-fri; else ydir -= h/2;my += h/2; nyy += h/2; break; //Pil ner

	case 49: xdir += w/2; ydir += h/2; break;//Knapp=1
	case 50: ydir += h/2; break;//knapp=2    
	case 51: xdir -= w/2; ydir += h/2; break;//Knapp=3
	case 52: xdir += w/2; break;//knapp=4    
	case 54: xdir -= w/2; break;//knapp=6    
	case 55: xdir += w/2; ydir -= h/2; break;//Knapp=7
	case 56: ydir -= h/2; break;//knapp=8    
	case 57: xdir -= w/2; ydir -= h/2; break;//Knapp=9
	default:
	    return;
	}
    }
    
    /** BEH?VS EJ
       Avser musens r?relse i x-led
    public void moveX(int qx, int key){
	if((mx < -nyx || mx > (w-nyx))){
	    nyx += qx;
	    setXYDir(key);
	    
	}
    }
    
      // Avser musens r?relse i y-led
    public void moveY(int qy, int key){
	if((my < -nyy || my > (h - nyy))){
	    //print("moveY key= " + key + " & if=true Ty: " + -nyy + " < " + my + " < " + (h-nyy) + "\n");
	    nyy += qy;
	    setXYDir(key);
	}
    }
    */    

    /* Operation Avser musens r?relse i xy-led
     *
     * @param x=w/2, y=h/2, kry=tangentknapp
     */
    public void moveXY(int x, int y, int key){
	//if((mx < -nyx || mx > (w-nyx)) && (my < -nyy || my > (h - nyy))){
	if((mx < nyx || mx > (w+nyx)) && ((my < nyy) || my > (h + nyy))){//Move X & Y  
	    nyx += x; nyy += y;
	    setXYDir(key);
	}else if(mx < nyx || mx > (w+nyx)){
	    nyx += x;    
	    setXYDir(key);
	}else if(my < nyy || my > (h + nyy)){	  
	    nyy += y; 
	    setXYDir(key);
	}
    }

    public void paint(Graphics gr) {
	g=gr;
	font = gr.getFont();
	cleanScreen(gr);
	gr.translate(xdir,ydir);
	coMap.paint(gr,w,h,zoom);
	//setTitle(coMap.getTitle());//Av ngn anlednig funkar inte detta
	mouse(gr);
	if(popup)
	    drawPopup();
    }

    
    void print(String s){
	System.out.println("Map:" + s);
    }
}
