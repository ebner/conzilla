/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.beans.PropertyChangeEvent;

import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.StateTool;

public class TieTool extends StateTool {
	
	private static final long serialVersionUID = 1L;

	public TieTool() {
		super("TIE", EditMapManagerFactory.class.getName(), true);
		setIcon(Images.getImageIcon(Images.ICON_TIE));
	}

	public void propertyChange(PropertyChangeEvent e) {
	}
	
}
