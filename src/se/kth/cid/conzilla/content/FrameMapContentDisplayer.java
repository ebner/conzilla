/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import se.kth.cid.component.Resource;
import se.kth.cid.util.Tracer;

public class FrameMapContentDisplayer extends AbstractContentDisplayer {
	
	JFrame frame;

	MapContentDisplayer displayer;

	public FrameMapContentDisplayer() {
		frame = new JFrame("Content");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				Tracer.debug("CLOSED!!!");
				try {
					setContent(null);
				} catch (ContentException e) {
					Tracer.trace("AbstractContentDisplayer threw exception!", Tracer.ERROR);
				}
			}
		});

		displayer = new MapContentDisplayer(frame.getContentPane(), BorderLayout.CENTER);
	}

	public void setContent(Resource c) throws ContentException {
		displayer.setContent(c);
		super.setContent(displayer.getContent());

		if (getContent() != null) {
			frame.pack();
			frame.setVisible(true);
			frame.invalidate();
			frame.repaint();
		} else
			frame.setVisible(false);
	}
	
}