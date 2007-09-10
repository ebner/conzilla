/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.util.AttributeEntryUtil;

public class FrameView extends DefaultView {
    
	FrameManager frameManager;
    
	protected ConzillaSplitPane conzillaSplitPane;
    
	JFrame frame = new JFrame();

    public FrameView(FrameManager frameManager, MapController controller) {
    	super(controller);
        this.frameManager = frameManager;
                
        //setGlassPane(frameManager.constructGlassPane());
        //getGlassPane().setVisible(true);
        
        final FrameManager fm = frameManager;
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        WindowAdapter wa = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                fm.close(FrameView.this, true);
            }

            public void windowGainedFocus(WindowEvent e) {
            	if (FrameView.this.controller != null) {
            		FrameView.this.controller.getManager().gotFocus();
            	}
            }
        };

        //To be able to catch closing events
        frame.addWindowListener(wa);

        //To be able to catch focus events
        frame.addWindowFocusListener(wa);

        conzillaSplitPane = new ConzillaSplitPane();
        conzillaSplitPane.setPanes(getLeftPanel(), getMapPanel(), getRightPanel(),
                this);
        conzillaSplitPane.setToolBar(getToolsBar());
        conzillaSplitPane.setLocationField(controller.getView().getLocationField());
        frame.setContentPane(conzillaSplitPane);

        getMapPanel().setVisible(true);
        updateTitle();
        frame.setJMenuBar(frameManager.makeMenuBar(this, false));
    }
    
    public JFrame getFrame() {
    	return frame;
    }

    public void updateFonts() {
        SwingUtilities.updateComponentTreeUI(SwingUtilities.getRoot(frame));
    }

    public void draw() {
    	frame.setVisible(true);
        frame.pack();
        conzillaSplitPane.fixDividerLocation();
    }

    protected void updateTitle() {
        String title = "(none)";
        if (getMapScrollPane() != null) {
            ContextMap map = controller.getConceptMap();

            title = AttributeEntryUtil.getTitleAsString(map);
            if (title == null) {
                title = "(none)";
            }
        }
        frame.setTitle("Conzilla - " + title);
    }

    public void detach() {
    	super.detach();
        conzillaSplitPane.detach();
        frame.dispose();
    }

}