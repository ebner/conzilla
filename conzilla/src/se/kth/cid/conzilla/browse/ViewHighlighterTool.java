/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;

import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.properties.ColorTheme;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.properties.ColorTheme.Colors;
import se.kth.cid.conzilla.tool.StateTool;

/**
 * @author matthias
 *
 */
public class ViewHighlighterTool extends StateTool {
    private static final long serialVersionUID = 1L;
	
    Highlighter highlighter;
    
    String originalToolTip;
    
    public ViewHighlighterTool(Highlighter highlighter) {
        super("VIEWABLE", BrowseMapManagerFactory.class.getName(), true);
        this.highlighter = highlighter;
        this.originalToolTip = getToolTip();
        setIcon(getIcon());
        setSelectedIcon(getSelectedIcon());
        setCustomToolTip();
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {        
        highlighter.reMarkAll();
    }
    
    public void setCustomToolTip() {
    	Color col = ColorTheme.getColor(Colors.CONTENT).brighter();
    	String hexColor = Integer.toHexString(col.getRed()) + Integer.toHexString(col.getGreen()) + Integer.toHexString(col.getBlue());
    	setToolTip("<html><table border=\"0\"<tr><td bgcolor=#\"" + hexColor + "\" width=30px></td><td>" + originalToolTip + "</td></tr></table></html>");
    }
    
    public Icon getIcon() {
    	return Images.getImageIcon(Images.ICON_CONTENT);
    }
    
    public Icon getSelectedIcon() {
    	setCustomToolTip(); // hack
    	return getIcon();
//    	Color browseColor = ColorTheme.getColor(Colors.CONTENT).brighter();
//    	ImageIcon imgIcon = (ImageIcon) getIcon();
//    	return new ImageIcon(TransparencyImageFilter.createFilteredImage(imgIcon.getImage(), browseColor));
    }
    
    public boolean isViewable(MapObject mo) {
        HashSet contentInformations = new HashSet();
        Concept concept;
        if ((concept = mo.getConcept()) != null) {
            //Adds content that are given on this concept via a list of
            // known 'content' attributes.
            //Currently only via the {@link CV#contains} attribute.
            Set contentOnConcept = concept.getContentInformation();
            Set contentInContext = mo.getDrawerLayout()
                    .getConceptMap().getContentInContextForConcept(
                            concept.getURI());
            contentInformations.addAll(contentOnConcept);
            contentInformations.addAll(contentInContext);

            return !contentInformations.isEmpty();
        }
        return false;
   }
}