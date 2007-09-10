/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.view;

import java.beans.PropertyChangeListener;

import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.util.AttributeEntryUtil;

public class InternalFrameView 
    extends DefaultView
    implements PropertyChangeListener{
	JInternalFrame frame = new JInternalFrame("", true, true, true, true);
    InternalFrameManager internalFrameManager;
    InternalFrameAdapter internalFrameAdapter;

    public InternalFrameView(
        InternalFrameManager internalFrameManager,
        MapController controller) {
        super(controller);
        this.internalFrameManager = internalFrameManager;
        frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);

        internalFrameAdapter = new InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent e) {
                InternalFrameView.this.internalFrameManager.close(
                    InternalFrameView.this,
                    true);
            }
            public void internalFrameActivated(InternalFrameEvent e) {
                InternalFrameView
                    .this
                    .internalFrameManager
                    .activateInternalFrame(
                    InternalFrameView.this);
                if (InternalFrameView.this.controller.getManager() != null) {
                	InternalFrameView.this.controller.getManager().gotFocus();
                }
            }
        };
        frame.addInternalFrameListener(internalFrameAdapter);

        frame.setContentPane(getMapPanel());
        getMapPanel().setVisible(true);
        updateTitle();
    }
    
    public JInternalFrame getFrame() {
    	return frame;
    }

    public void draw() {
        frame.show();
        frame.pack();
    }

    public void updateFonts() {
        SwingUtilities.updateComponentTreeUI(SwingUtilities.getRoot(frame));
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
        frame.dispose();        
    }    
}
