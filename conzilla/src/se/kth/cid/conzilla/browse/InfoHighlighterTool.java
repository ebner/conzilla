/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.beans.PropertyChangeEvent;

import se.kth.cid.component.Component;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.StateTool;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.util.AttributeEntryUtil;

/**
 * @author matthias
 *
 */
public class InfoHighlighterTool extends StateTool {
    
	private static final long serialVersionUID = 1L;
	
	Highlighter highlighter;
    
    public InfoHighlighterTool(Highlighter highlighter) {
        super("INFOABLE", BrowseMapManagerFactory.class.getName(), true);
        this.highlighter = highlighter;
        setActivated(false);
        
        setIcon(Images.getImageIcon(Images.ICON_INFORMATION));
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {        
        highlighter.reMarkAll();
    }
    
    public static boolean isInfoable(MapObject mo) {
        //HashSet contentInformations = new HashSet();
        Component component = mo.getConcept();
        if (component == null) {
            component = mo.getDrawerLayout().getConceptMap();
        }
        //Discovers information from a given set of properties...
        //Currently only via the description or definition attributes.
        if (component.getAttributeEntry("http://purl.org/dc/elements/1.1/description").isEmpty()
                && component.getAttributeEntry("http://kmr.nada.kth.se/rdf/ulm#definition").isEmpty()
                && component.getAttributeEntry("http://kmr.nada.kth.se/rdf/ulm#targetGroup").isEmpty()
                && component.getAttributeEntry("http://kmr.nada.kth.se/rdf/ulm#purpose").isEmpty()) {
            if (mo.getDrawerLayout() instanceof StatementLayout &&
                    !mo.getDrawerLayout().getBodyVisible()) {
                //If there is a non-visible title the concept is viewable.
                return AttributeEntryUtil.getTitleAsString(component) != null;
            } else {
                //No information to see
                return false;
            }
        }
        //Some of the attributes above returned a non-empty list, 
        //i.e. there is information that can be viewed.
        return true;
   }
}