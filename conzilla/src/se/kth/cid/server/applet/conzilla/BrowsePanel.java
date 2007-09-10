/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.applet.conzilla;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author enok
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BrowsePanel extends JPanel {

    JButton[] jbs;

    public BrowsePanel() {
        super();
        FlowLayout fl = new FlowLayout();
        this.setLayout(fl);
        JButton back = new JButton("Back");
        back.setActionCommand("back");
        JButton forward = new JButton("Forward");
        forward.setActionCommand("forward");
        JButton home = new JButton("Home");
        home.setActionCommand("home");
        this.add(back);
        this.add(forward);
        forward.setEnabled(false);
        back.setEnabled(false);
        this.add(home);
        jbs = new JButton[3];
        jbs[0] = back;
        jbs[1] = forward;
        jbs[2] = home;

    }

    public JButton[] getButtons() {
        return jbs;
    }

}
