/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.applet.conzilla;

/**
 * @author enok
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Concept {

    String URI;
    String title;
    String description;
    int width = 0;
    int height = 0;
    int xPos = 0;
    int yPos = 0;
    String type;
    String surfMap;
    String[] neighbourhood;
    //boolean highlight = false;
    //PopupMenu pm;

    public Concept(String URI, String title, String description) {
        this.URI = URI;
        this.title = title;
        this.description = description;
        //this.addMouseListener(this);
        //this.setVisible(true);
        //pm = new PopupMenu();
        //add(pm);
    }

    public String getURI() {
        return URI;
    }

    public void setTitle(String s) {
        title = s;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String s) {
        description = s;
    }

    public String getDescription() {
        return description;
    }

    public int getXpos() {
        return xPos;
    }

    public void setXpos(int x) {
        xPos = x;
    }

    public int getYpos() {
        return yPos;
    }

    public void setYpos(int y) {
        yPos = y;
    }

    public void setHeight(int h) {
        height = h;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int w) {
        width = w;
    }

    public int getWidth() {
        return width;
    }

    public String[] getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String[] s) {
        neighbourhood = s;
    }

    public String getType() {
        return type;
    }

    public void setType(String s) {
        type = s;
    }

    public String getSurfMap() {
        return surfMap;
    }

    public void setSurfMap(String s) {
        surfMap = s;
    }
}
//Sparar detta till senare
/*public void ActionPerformed(ActionEvent e){
   System.out.println("Ngt h?nder...");
}
   public void mouseEntered(MouseEvent e){;
	  highlight = true;   
	  this.repaint();
   }
   public void mouseExited(MouseEvent e){
	  highlight = false;
	  this.repaint();
   }
   public void mouseClicked(MouseEvent e){
	  //System.out.println("Jahapp!");
   }
		
   public void mousePressed(MouseEvent e){
	  if(e.isPopupTrigger()){
		 System.out.println("Pressed i Concept");
		pm.show(e.getComponent(),e.getX(),e.getY());
	 }
   }
			
   public void mouseReleased(MouseEvent e){}
   
public void init(){
   addMouseListener(this);
   //System.out.println("L?ngd f?r lyssnare"+getMouseListeners().length);
}
   
public MouseListener getMouseListener(){
   return (MouseListener) this;
}
   
public void paint(Graphics g){
   setLocation(xPos,yPos);
   Dimension d = this.getSize();
   if(!highlight)
	  g.setColor(Color.white);
   else
	  g.setColor(Color.yellow);   
   g.fillRect(xPos,yPos,d.width,d.height);
   g.setColor(Color.black);
   g.drawRect(xPos,yPos,d.width,d.height);
   drawTitle(g,d);
   g.setClip(null);
   //super.paint(g);
}
   
public void drawTitle(Graphics g,Dimension d){
	  FontMetrics f = g.getFontMetrics(getFont());
	  int fontwidth = f.stringWidth(title);
	  int fontheight = f.getHeight();
	  int yl = yPos+(fontheight+height)/2;
	  int xl = xPos+(width/2-fontwidth/2);
	  g.drawString(title,xl,yl);
}*/
