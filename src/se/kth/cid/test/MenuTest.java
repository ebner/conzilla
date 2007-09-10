/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


/**
 * @author matthias
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MenuTest {
    public static class SimpleMenuItem extends JMenuItem {

        public class SimpleBox extends JComponent {
               public SimpleBox() {
                   setSize(500,200);
                   setMinimumSize(new Dimension(500,200));
                   setPreferredSize(new Dimension(500,200));
                   setVisible(true);
                   setLocation(0,0);               
               }
           
               public void paintComponent(Graphics g) {
                   g.drawRect(5,5,40,10);
                   if (isArmed()) {
                       g.drawString("armed",10,10);   
                   }
               }
           }

        
        SimpleBox box = new SimpleBox();
        
        public SimpleMenuItem() {
            super();
            add(box);
            setPreferredSize(box.getPreferredSize());
        }

        /* (non-Javadoc)
         * @see javax.swing.MenuElement#menuSelectionChanged(boolean)
         */
        public void menuSelectionChanged(boolean arg0) {
            if (arg0) {
                box.setBackground(Color.BLUE);
            } else {
                box.setBackground(Color.WHITE);
            }

            super.menuSelectionChanged(arg0);
        }

}
    
       
   public static void main(String [] argv) {
       JFrame frame = new JFrame("hepp");
       
       SimpleMenuItem box = new SimpleMenuItem();
       JMenu menu = new JMenu("File");
       menu.add(box);
       JMenuBar bar = new JMenuBar();
       bar.add(menu);
       frame.setJMenuBar(bar);
       frame.setVisible(true);
   }
}
