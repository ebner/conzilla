/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.beans.PropertyChangeEvent;

import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.StateTool;

public class LineTool extends StateTool {
	
	private static final long serialVersionUID = 1L;

	public LineTool() {
		super("HANDLE", EditMapManagerFactory.class.getName(), false);
		setIcon(Images.getImageIcon(Images.ICON_HANDLE));
	}

	public void propertyChange(PropertyChangeEvent e) {
	}
	
}
