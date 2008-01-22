/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.app;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.beans.PropertyChangeEvent;

import javax.swing.Icon;
import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.browse.BrowseMapManagerFactory;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.StateTool;

/**
 * Toggle Conzilla's full screen mode.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class FullScreenTool extends StateTool {

	Log log = LogFactory.getLog(FullScreenTool.class);
	
	private Component toolBar;

	public FullScreenTool(Component toolBar) {
		super("FULLSCREEN", BrowseMapManagerFactory.class.getName(), isFullScreen());
		
		this.toolBar = toolBar;
		setIcon(getIcon());
		setSelectedIcon(getSelectedIcon());
		setToolTip();
		
		String os = (String) System.getProperty("os.name");
		if ((os == null) || (os.toLowerCase().matches(".*mac.*"))) {
			setEnabled(false);
			putValue(SHORT_DESCRIPTION, "Fullscreen mode is not supported on your system.");
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ACTIVATED)) {
			JFrame frame = null;
			try {
				frame = (JFrame)getEnclosingFrame(this.toolBar);
			} catch (NoSuchMethodException e) {
				log.warn("Unable to get parent frame, full screen mode not supported");
			}

			if (frame != null) {
				GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				if (device != null) {
					frame.setVisible(false);
					frame.removeNotify();
					if (isFullScreen()) {
						frame.getJMenuBar().setVisible(true);
						frame.setUndecorated(false);
						device.setFullScreenWindow(null);
						frame.setResizable(true);
					} else {
						frame.getJMenuBar().setVisible(false);
						frame.setUndecorated(true);
						device.setFullScreenWindow(frame);
						frame.setResizable(false);
						if (ConfigurationManager.getConfiguration().getBoolean(Settings.CONZILLA_PACK, false)) {
							ConfigurationManager.getConfiguration().setProperty(Settings.CONZILLA_PACK, new Boolean(false));
						}
					}
					frame.addNotify();
					frame.setVisible(true);
				} else {
					log.warn("Unable to access graphics device, full screen mode not supported");
				}
			}
			
			setToolTip();
		}
	}
	
    public Frame getEnclosingFrame(Component c) throws NoSuchMethodException {
		Component p = c;
		Class fClass = java.awt.Frame.class;
		while (!fClass.isInstance(p)) {
			p.getClass().getMethod("getParent", (Class[]) null);
			p = ((Component) p).getParent();
		}
		return (Frame) p;
	}
	
	private void setToolTip() {
		if (isFullScreen()) {
			putValue(SHORT_DESCRIPTION, "Click the button to leave full screen mode.");
		} else {
			putValue(SHORT_DESCRIPTION, "Click the button to enter full screen mode.");
		}
	}
	
	public Icon getIcon() {
		return Images.getImageIcon(Images.ICON_FULLSCREEN);
	}

	private static boolean isFullScreen() {
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		return (device.getFullScreenWindow() != null);
	}

}