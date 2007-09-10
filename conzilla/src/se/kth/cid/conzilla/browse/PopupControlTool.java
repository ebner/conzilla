/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.awt.Color;
import java.beans.PropertyChangeEvent;

import javax.swing.Icon;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.ColorTheme;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.properties.ColorTheme.Colors;
import se.kth.cid.conzilla.tool.StateTool;

/**
 * @author matthias
 *
 */
public class PopupControlTool extends StateTool {
    private static final long serialVersionUID = 1L;
	MapController controller;
    PopupLayer popup;
    
    String originalToolTip;
    
    public PopupControlTool(MapController controller, PopupLayer popup) {
        super("INFO_CONTROL", BrowseMapManagerFactory.class.getName(), true);
        this.controller = controller;
        this.popup = popup;
        this.originalToolTip = getToolTip();
        setIcon(getIcon());
        setSelectedIcon(getSelectedIcon());
        setCustomToolTip();
    }
    
    public void setCustomToolTip() {
    	Color col = ColorTheme.getColor(Colors.INFORMATION);
    	String hexColor = Integer.toHexString(col.getRed()) + Integer.toHexString(col.getGreen()) + Integer.toHexString(col.getBlue());
    	setToolTip("<html><table border=\"0\"<tr><td bgcolor=#\"" + hexColor + "\" width=30px></td><td>" + originalToolTip + "</td></tr></table></html>");
    }
    
    public Icon getIcon() {
    	return Images.getImageIcon(Images.ICON_POPUP);
    }
    
    public Icon getSelectedIcon() {
    	setCustomToolTip(); // hack
    	return getIcon();
//    	Color browseColor = ColorTheme.getColor(Colors.INFORMATION);
//    	ImageIcon imgIcon = (ImageIcon) getIcon();
//    	return new ImageIcon(TransparencyImageFilter.createFilteredImage(imgIcon.getImage(), browseColor));
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (!evt.getPropertyName().equals(StateTool.ACTIVATED)) {
            return;
        }
        
        if (((Boolean) evt.getNewValue()).booleanValue()) {
            popup.activate(controller.getView().getMapScrollPane());            
        } else {
            popup.deactivate(controller.getView().getMapScrollPane());            
        }
    }
}