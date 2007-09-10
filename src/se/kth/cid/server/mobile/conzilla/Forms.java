/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.mobile.conzilla;

import java.io.PrintStream;
import javax.microedition.io.HttpConnection; 
import java.util.Hashtable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Alert; 
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Choice;
import javax.microedition.midlet.MIDletStateChangeException;
import java.util.Vector;

public class Forms implements CommandListener {
    //Form= wForm, svaeFile, openFile, errorMsg, addBokmark
    

    Display display;
    Content conts;
    MIDlet midlet;
    HttpConn ht;
    Form wForm, errorForm, addBokmark, conzInfo;
    List bokmarkList, contentList, openFile, NeibList;
    TextBox descBox;
    Alert msgAl;
    StringItem  si;
    TextField   tf, bokmarkTF,ntf,connTF;
    String mapURI,detailURI, uri, file, title, mime, contentURL;//uri avser concept eller content-URI
    String save_url=""; //URI = contextMaps URI
    String protocol="";
    String[] contentMime;String[] titleArray;
    int idx;
    boolean connMsg=true;
    boolean back = false;//D? man backar i browsen
    boolean botton = false;//D? man backar i browsen
    Parser parse;
    ContextMap coMap;
    Map map;
    //ConceptBox box;
    Displayable current, previous;//
    Hashtable files = new Hashtable();
    Vector historyBuffer, bokmarksVec, openFileVec, NeibVec;//contentListVec, 
    int bufferSize = 5; // max ant. obj. i historyBuffer
    static int stackPointer = -1;//Pointer to historyBuffer pointing out the current object
    static int vecSize = 0;

    //Def. bokmarks
    String url_1 = "http://id-2713.nada.kth.se:8081/"; 
    String url_2 = "http://localhost:8080/"; 
     
    //Def contents cont_1 is a context amp
    //String cont_1 = "http://id-2713.nada.kth.se:8081/"; 
    //String cont_2 = "http://localhost:8080/"; 
    
    static final Command BACK = new Command("Back", Command.BACK, 1);
    static final Command SAVE = new Command("Save file", Command.SCREEN, 1);
    static final Command NEXT = new Command("Next", Command.SCREEN, 1);
    static final Command CANCEL = new Command("Cancel", Command.CANCEL, 1);
    static final Command STOP = new Command("Stop", Command.STOP, 1);

    static final Command EXIT = new Command("Exit", Command.EXIT, 2);    
    static final Command INFO = new Command("Info",Command.SCREEN, 1);
    static final Command GO = new Command("Go",Command.SCREEN, 1);
    static final Command OPEN = new Command("Open file...", Command.SCREEN, 1);
    static final Command GOTO = new Command("Go to...",Command.SCREEN, 1);
    static final Command ADDBOK = new Command("Add bokmarks",Command.SCREEN, 1);
    static final Command DELETE = new Command("Delete bokmark",Command.SCREEN, 1);
    static final Command DELFILE = new Command("Delete file",Command.SCREEN, 1);
    static final Command BOKMARK = new Command("Bokmarks...", Command.SCREEN, 1);		
    //static final Command VIEW = new Command("Contents...", Command.SCREEN, 1);		
    static final Command SURF = new Command("Surf", Command.SCREEN, 1);		
    static final Command DETAIL = new Command("Detail map", Command.SCREEN, 1);		
    static final Command CONTENT = new Command("Contents...", Command.SCREEN, 1);		

    
    public Forms(MIDlet mid) {
	midlet = mid;
	display = Display.getDisplay(midlet);
	historyBuffer = new Vector(bufferSize);
	init();
    }

    public void init(){
	//print("init 1");

	wForm = new Form("MobileConceptBrowser");
	errorForm = new Form("Error Message...");
	addBokmark= new Form("Add Bokmark...");
	conzInfo = new Form("About Conzilla");
	
	descBox = new TextBox("Description", "No description!", 500, TextField.ANY);
	
	openFile = new List("Select a file!", Choice.EXCLUSIVE);
	contentList = new List("Select a content", Choice.EXCLUSIVE);
	bokmarkList = new List("Select a bokmark!", Choice.EXCLUSIVE);
	NeibList = new List("ContextualNeibours!", Choice.EXCLUSIVE);
	bokmarksVec = new Vector(10);
	//contentListVec = new Vector(10);
	openFileVec = new Vector(10);
	NeibVec = new Vector(5);
	
	bokmarksVec.addElement(url_1);
	bokmarksVec.addElement(url_2);
	
	msgAl = new Alert("Message Alert", "Message", null, null);
	msgAl.setTimeout(2000);

	NeibList.addCommand(GO);
	NeibList.addCommand(BACK);
	NeibList.addCommand(GOTO);
	
	contentList.addCommand(GO);
	contentList.addCommand(BACK);
	contentList.addCommand(GOTO);
	
	wForm.addCommand(GO);
	wForm.addCommand(BOKMARK);
	wForm.addCommand(INFO);
	wForm.addCommand(OPEN);
	wForm.addCommand(EXIT);
	//tf = new TextField("Enter URL:", "http://localhost:8082/", 40, TextField.URL);
	//tf = new TextField("Enter URL:", "http://130.237.228.131:8081", 40, TextField.URL);//EnoksAdress
	//tf = new TextField("Enter URL:", "http://130.237.228.42:8081", 40, TextField.URL);//Min
	tf = new TextField("Enter URL:", "", 40, TextField.URL);
	wForm.append(tf);
	wForm.setCommandListener(this);
	
	addBokmark.addCommand(SAVE);
	//addBokmark.addCommand(GOTO);
	addBokmark.addCommand(BACK);
	addBokmark.append("Enter URI");
	bokmarkTF = new TextField("AddBokmark", "",100, TextField.URL);
	addBokmark.append(bokmarkTF);
	addBokmark.setCommandListener(this);
	
	errorForm.addCommand(BACK);
	errorForm.setCommandListener(this);
	
	descBox.addCommand(SURF);	
	descBox.addCommand(DETAIL);
	descBox.addCommand(CONTENT);
	descBox.addCommand(OPEN);
	descBox.addCommand(GOTO);
	descBox.addCommand(BOKMARK);
	descBox.addCommand(BACK);
	descBox.setCommandListener(this);
	
	conzInfo.addCommand(BACK);
	conzInfo.setCommandListener(this);
	
	bokmarkList.addCommand(GO);
	bokmarkList.addCommand(ADDBOK);
	bokmarkList.addCommand(GOTO);
	bokmarkList.addCommand(DELETE);
	bokmarkList.addCommand(BACK);

	for(int i=0; i<bokmarksVec.size(); i++)
	    bokmarkList.append((String)bokmarksVec.elementAt(i), null);
	bokmarkList.setCommandListener(this);
	
	openFile.addCommand(GO);
	openFile.addCommand(GOTO);
	openFile.addCommand(DELFILE);
	openFile.addCommand(BACK);
	for(int i=0; i<openFileVec.size(); i++)
	    openFile.append((String)openFileVec.elementAt(i), null);
	openFile.setCommandListener(this);
	
	//ht = new HttpConn("http://id-2713.nada.kth.se:8081/");
        ht = new HttpConn("http://knowware.nada.kth.se:8095/mywar/HelloServlet");

	//ht = new HttpConn(null);//(url);
	ht.run();
    }

    /**
    void prepareSurfList(int antEle, int ele){
	String[] tmp = new String[10];
	int size;
	if(antEle==2){// en contextual. neibourArray
	tmp = map.currCpt.get_grann_URI();
	size = tmp.lenght;//(String[])map.currCpt.get_grann_URI().lenght;
	for(int k=ele; k < size; k=k+antEle){
	NeibList.append((String)map.currCpt.grann_URI[k], null);
	}
	
	}else if(antEle==2){ // en content array
	//tmp = new (String)map.currCpt.get_content());
	tmp = map.currCpt.get_content();
	size = tmp.lenght;
	size = (String[])map.currCpt.get_content().lenght;
	for(int k=ele; k < size; k=k+antEle){
	contentList.append((String)map.currCpt.get_content[k], null);
	contentList.setCommandListener(this);
	}
	}
	}
    **/
	
    public void initConn(String u){
	print("URI:n ?ro: "+ u);
	StringBuffer sb;
	//String sb;
	ht.uri = u;
	ht.run();
	sb = ht.getBuffer();
	//sb = ht.staticPage();
	//ht.buffer = null;
	if(sb != null){
	    parse = new Parser(sb.toString());
	    parse.parse();
	    coMap = parse.getMap();	
	    this.mapURI = coMap.getURI();//mapURI = coMap.getURI();
	    sb = null;
	}
    }
    
    void initMap(){
	map = new Map(display, this);
	map.mapURI =coMap.getURI();//kanske on?digt d? (this.mapURI = coMap.getURI()) s?tts i initConn
	if(back)
	    removeHistory();
	historyBuffer.addElement(map);
	stackPointer++;
	display.setCurrent(map);
	print("I initMap ");
    }

    public void initCanvas(String pro){
	parse = new Parser(pro);
	parse.parse();
	coMap = parse.getMap();
	mapURI = coMap.getURI();//mapURI = coMap.getURI();
	//title = coMap.getTitle();
    }

    /**
       Backar i navigations historin
       Objekten l?ggs i vec s? att karta/Form nr 1 har index=0, karta/Form har index = 1 osv
       tills bufferSize.
       Pekaren pekar alltid p? karta/Form som ?r aktuell
       Backar man i historian och sen Surfar s? kommer alla kartor/Forms med l?gre index att 
       raderas och Kartor/Forms med H?gre index att flyttas ner?t i vec
    */
    public Displayable backOrForth(char c){
	vecSize = historyBuffer.size();
	print(historyBuffer.toString());
	print("F?rst stackPointer= " + stackPointer);
	print("historyBuffer.size= " + stackPointer);
	Displayable dp = null;
	print("I backOr dp = null");
	if(c == 'b'){
	    print("I backOr if(c == 'b')");
	    if(stackPointer == 0){//Finns bara en displayable kvar
		print("I bORforth stPoit=NOLL dvs= " + stackPointer);
		message("History stack empty");
		dp = null;
	    }else if(stackPointer > 0 && stackPointer < vecSize){
		print("I bORforth stPoit= " + stackPointer);
		stackPointer--;
		dp = (Displayable)historyBuffer.elementAt(stackPointer);
		print("I BACK: stackPointer= " + stackPointer + " :: vec.size= " + vecSize);
		print("I BACK: Displayable= " + dp.getClass());
		print("I BACK: Displayable= " + dp.toString());
	    }
	}else if(c == 'f'){
	    print("I backOr if(c == 'f')");
	    if(stackPointer < vecSize){
		stackPointer++;
		dp = (Displayable)historyBuffer.elementAt(stackPointer);
	    }else if(stackPointer == vecSize -1){
		back = false;//Man har kommit till aktuella siadan
		dp = (Displayable)historyBuffer.elementAt(stackPointer);
	    }
	}else
	    print("ngt fel i backOrForth & cmd= " + c);
	return dp;
    }
    
    /**
       Vid backning i historian och sen surfning tar bort alla ele. som ligger
       vid stackPointer och fram?t
    */
    public void removeHistory(){
	if(back){
	    print("I removeHist & back=botton=true");
	    for(int i=stackPointer+1; i<historyBuffer.size(); i++)
		historyBuffer.removeElementAt(i);
	    back = false;
	}
    }
    
    /**
     * Operation
     * 
     * @param c
     * @return String
     * A list of ContxtNeib or contents.
     **/
     public void arrangeList(String c, int nrItem){
	NeibList.deleteAll();contentList.deleteAll();
	print("I arrangeList 1 str=" + c);
	String str;// = new String(c);
	//int n = Integer.parseInt(str.substring(0,1));
	int n = Integer.parseInt(c.substring(0,1));
	//str = str.substring(2);
	c = c.substring(2);
	print("c : " + c);
	int k=1;
	//contentMime = new String[n];
	//titleArray = new String[n];
	print("Klar med arrayer");
	for(int i=1; i<n+1; i++){
	    if(n==1)
		str = c;
	    else
		str = parse.getSubPart(c,"?",i);
	    //str = parse.getSubPart(c,",",i);
	    print("I for 1 + str= " + str + " i = " + i);
	    if(nrItem == 2){
		NeibList.append(str, null);//Tillf?llig ?tg?rd
		print("4 appendar ctxtNeib= " + str);
	    }else if(nrItem == 3){
		contentList.append(str, null);//Tillf?llig ?tg?rd
		//mime = parse.getSubPart(c,",",++k);
		//contentMime[i]=mime;//anv. f?r o ladda contentMime
		print("5 appendar content = " + str);
	    }
	}
	if(nrItem == 2)
	    NeibList.setCommandListener(this);
	else if(nrItem == 3)
	    contentList.setCommandListener(this);
     }
    

    public void message(String msg){
	msgAl.setString(msg);
	display.setCurrent(msgAl, current);
    }
    
    /**
       Returnerar fram?t mappen i historien
    */
    /**        
    public void setContents(){
	int nr = box.contents.length;
	
	contentList.addCommand(GO);
	contentList.addCommand(GOTO);
	contentList.addCommand(BACK);
	contentListVec.addElement(cont_4);
	for(int j=0; j<nr; j++){
	    conts = box.contents[j];
	    title = conts.getName();
	    //print("conts 1= " + conts);
	    contentList.append(title, null);
	    contentListVec.addElement(conts);
	    //print("conts= " + contentList.vec.elementAt(j));
	}
	contentList.setCommandListener(this);
    }
    **/    
    
    public void commandAction(Command c, Displayable s) {
	previous = current;
	if (s instanceof Form) {
	    //previous = current;
	    print("I wForm 1");
	    if(s == wForm){
		current = wForm;
		print("I wForm 2");
		if(c == EXIT){
		    //midlet.exit(true);//destroyApp(true);
		    historyBuffer.removeAllElements();
		    stackPointer = -1;
		    midlet.notifyDestroyed();
		}else if(c == GO){
		    initConn(tf.getString());
		    initMap();
		}else if(c == OPEN){
		    //historyBuffer.addElement(wForm);
		    //stackPointer++;
		    //previous = current;
		    //current = openFile;
		    display.setCurrent(openFile);	
		}else if(c == BOKMARK){
		    //historyBuffer.addElement(wForm);
		    //stackPointer++;
		    //previous = current;
		    //current = bokmarkList;
		    display.setCurrent(bokmarkList);
		}
	    }else if(s == addBokmark){
		//previous = current;//Kanske: bort
		current = addBokmark;
		if(c == SAVE){//FIXA
		    bokmarkList.append(bokmarkTF.getString(), null);
		    msgAl.setString("Bokmark saved!");
		    display.setCurrent(msgAl, bokmarkList);
		}else if(c == BACK){
		    //back = true;
		    //botton = true;
		    //display.setCurrent(msgAl, addBokmark.prev);//bokmarkList | map;
		    display.setCurrent(previous);//Kanske ska ha: current
		    //currentDisplayable = addBokmark;
		}
	    }
	}else if(s instanceof List){
	    //previous = current;
	    //String title, uri=null;
	    if(s == NeibList){
		current = NeibList;
		print("I NeibList");
		if(c == GO){
		    print("Inne i GO");
		    idx = NeibList.getSelectedIndex();
		    uri=NeibList.getString(idx);//Tillf?llig l?sning:uri=title+uri
		    uri = parse.getSubPart(uri,",",2);//Bara uri
		    print("uri= " + uri);
		    initConn(uri);
		    initMap();
		    //map.setTitle(map.currCpt.get_grann_URI[idx*2+1]);//Title F?r senare hantering
		    //title = titleArray[idx];
		}else if(c == GOTO){
		    tf.setString("http://");
		    display.setCurrent(wForm);
		}else if(c == BACK){
		    display.setCurrent(previous);
		}
	    }else if(s == bokmarkList){
		current = bokmarkList;
		print("I bokMarkList");
		if(c == GO){
		    if(bokmarksVec.isEmpty()){
			return;
		    }else{
			idx = bokmarkList.getSelectedIndex();
			initConn(bokmarkList.getString(idx));//initCanvas(url);
			initMap();
		    }
		}else if(c == ADDBOK){	
		    //current = 
		    //historyBuffer.addElement(bokmarkList);
		    //stackPointer++;
		    display.setCurrent(addBokmark);
		}else if(c == GOTO){
		    tf.setString("http://");
		    //historyBuffer.addElement(bokmarkList);
		    //stackPointer++;
		    display.setCurrent(wForm);
		}else if(c == DELETE){
		    if(bokmarksVec.isEmpty()){
			return;
		    }else{
			idx = bokmarkList.getSelectedIndex();
			uri = bokmarkList.getString(idx);
			bokmarkList.delete(idx);
			bokmarksVec.removeElement(uri);
			msgAl.setString("Bokmark '" + uri + "' is removed!");
			display.setCurrent(msgAl, bokmarkList);
		    }
		}else if(c == BACK){
		    //back = true;
		    //botton = true;
		    //print("Back I bokMarkList");
		    display.setCurrent(previous);
		    //currentDisplayable = bokmarkList;
		}
	    }else if (s == openFile){
		current = openFile;
		//print("I openFile");
		if(c == GO){//S?tt lista med filer
		    if(files.isEmpty()){
			msgAl.setString("File list empty!");
			display.setCurrent(msgAl, previous);
		    }else{
			idx = openFile.getSelectedIndex();
			file = openFile.getString(idx);
			initCanvas((String)files.get(file));
			initMap();
		    }
		}else if(c == GOTO){
		    tf.setString("http://");
		    //historyBuffer.addElement(openFile);
		    //stackPointer++;
		    display.setCurrent(wForm);
		}else if(c == DELFILE){
		    if(files.isEmpty()){
			msgAl.setString("File list empty!");
			display.setCurrent(msgAl, previous);//return;
		    }else{
			idx = openFile.getSelectedIndex();
			file = openFile.getString(idx);
			openFile.delete(idx);
			files.remove(file);
			msgAl.setString("File '" + file + "' deleted!");
			display.setCurrent(msgAl, openFile);
		    }
		}else if(c == BACK){
		    back = true;
		    botton = true;
		    print("Back I openFile");
		    display.setCurrent(previous);
		}
	    }else if(s == contentList){
		current = contentList;
		print("I ContList");
		String titleMimeURI;
		if(c == GO){
		    idx = contentList.getSelectedIndex();
		    titleMimeURI = contentList.getString(idx);//ger title+mime+URI
		    //mime = contentMime[idx];
		    //title = titleArray[idx];
		    title = parse.getSubPart(titleMimeURI,",",1);//Tillf?llig l?sning
		    mime = parse.getSubPart(titleMimeURI,",",2);//Tillf?llig l?sning
		    contentURL = parse.getSubPart(titleMimeURI,",",3);//Tillf?llig l?sning
		    print("Mime: " + mime + "\n uri: " + contentURL + "\n title: " + title);
		    conts = new Content(contentURL,title,mime);
		    conts.initiate(this);
		    //display.setCurrent(conts);// hanteras av Content klassen
		}else if(c == GOTO){
		    tf.setString("http://");
		    display.setCurrent(wForm);
		}else if(c == BACK){
		    display.setCurrent(previous);
		}
	    } 
	}else if(s instanceof TextBox){
	    
	    print("I DescBox");
	    if(s == descBox){					
		current = descBox;
		if(c == DETAIL){
		    print("Detalj karta uri = " + map.currCpt.getDetailURI());
		    initConn(map.currCpt.getDetailURI());
		    initMap();
		}else if(c == SURF){
		    //prepareSurfList(2,0);
		    print("Grannar= " + map.currCpt.get_grann_URIs());
		    arrangeList(map.currCpt.get_grann_URIs(),2);
		    display.setCurrent(NeibList);
		}else if(c == OPEN){
		    //historyBuffer.addElement(descBox);
		    //stackPointer++;
		    display.setCurrent(openFile);
		}else if(c == GOTO){
		    tf.setString("http://");
		    //historyBuffer.addElement(descBox);
		    //stackPointer++;
		    display.setCurrent(wForm);
		}else if(c == BOKMARK){
		    //historyBuffer.addElement(descBox);
		    //stackPointer++;
		    display.setCurrent(bokmarkList);
		}else if(c == CONTENT){//
		    //prepareSurfList(3,0);//setContents();
		    arrangeList(map.currCpt.get_content(),3);
		    //historyBuffer.addElement(descBox);
		    //stackPointer++;
		    display.setCurrent(contentList);
		}else if(c == BACK){
		    //back = true;
		    //botton = true;
		    //print(" Back i DescBox:"); 
		    display.setCurrent(previous);
		}
	    }
	}
	//removeHistory();
	System.gc();	
    }
    
    public List setChoice(List d){
	//print("setChoice 1");
	d = new List("Select a bokmark!", Choice.EXCLUSIVE);
	//print("DIsp name= " + d.getTitle());
	d.addCommand(OPEN);
	d.addCommand(ADDBOK);
	d.addCommand(BACK);
	d.setCommandListener(this);
	//print("Kan adda kommandon! d.toString" + d.toString());
	d.append(url_1, null);d.append(url_2, null);
	return d;
    }

    void print(String str){
	System.out.println("\nForms: " + str);
    }
	

}
