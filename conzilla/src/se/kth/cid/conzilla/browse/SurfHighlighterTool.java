/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Icon;

import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.properties.ColorTheme;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.properties.ColorTheme.Colors;
import se.kth.cid.conzilla.tool.StateTool;

/**
 * @author matthias
 *
 */
public class SurfHighlighterTool extends StateTool {
    private static final long serialVersionUID = 1L;
    
    private String originalToolTip;
	
    Highlighter highlighter;
    
    public SurfHighlighterTool(Highlighter highlighter) {
        super("BROWSABLE", BrowseMapManagerFactory.class.getName(), true);
        this.highlighter = highlighter;
        originalToolTip = getToolTip();
        setIcon(getIcon());
        setSelectedIcon(getSelectedIcon());
        setCustomToolTip();
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
    	if (evt.getPropertyName().equals(ACTIVATED)) {
			highlighter.reMarkAll();
		}
    }
    
    public void setCustomToolTip() {
    	Color col = ColorTheme.getColor(Colors.CONTEXT).brighter();
    	String hexColor = Integer.toHexString(col.getRed()) + Integer.toHexString(col.getGreen()) + Integer.toHexString(col.getBlue());
    	setToolTip("<html><table border=\"0\"<tr><td bgcolor=#\"" + hexColor + "\" width=30px></td><td>" + originalToolTip + "</td></tr></table></html>");
    }
    
    public Icon getIcon() {
    	return Images.getImageIcon(Images.ICON_LINK);
    }
    
    public Icon getSelectedIcon() {
    	setCustomToolTip(); // hack
    	return getIcon();
    }
    
    public boolean isSurfable(MapObject mo) {
        //detailedmap
        String dm = mo.getDrawerLayout().getDetailedMap();
        if (dm != null && dm.length() != 0) {
            return true;
        }
        
        //neighbourhood maps
        
        Set neighbourHoodMaps = new HashSet();
        if (mo.getConcept() != null) {
            ContainerManager containerManager = ConzillaKit.getDefaultKit().getResourceStore().getContainerManager();
            for (Iterator containers = containerManager.getContainers(Container.COMMON).iterator(); containers.hasNext();) {
                Container container = (Container) containers.next();
                neighbourHoodMaps.addAll(container.getMapsReferencingResource(mo.getConcept().getURI()));
            }
            neighbourHoodMaps.remove(mo.getDrawerLayout().getConceptMap().getURI());
        }
        
        return !neighbourHoodMaps.isEmpty();
    }

}