/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map.graphics;

import javax.swing.JComponent;

import se.kth.cid.concept.Concept;
import se.kth.cid.layout.ConceptLayout;

/**
 * @author matthias
 */
public class LiteralDrawer extends TitleDrawer {

    /**
     * Constructor for LiteralDrawer.
     */
    public LiteralDrawer(
        TripleMapObject tripleMapObject,
        JComponent editorLayer) {
        super(tripleMapObject, editorLayer);
    }

    protected String fetchString() {
        Concept concept = drawerMapObject.getConcept();
        if (concept != null
            && concept.getTriple() != null
            && concept.getTriple().isObjectLiteral()) {
        	String t = drawerMapObject.getConcept().getTriple().objectValue();
        	System.out.println("Found Literal:" + t);
        	return t;
        } else {
            return "";
        }
    }

    protected void fixVisibility() {
    	visible = true;
    }

	public void setTitle() {
        Concept concept = drawerMapObject.getConcept();
        if (concept == null
            || concept.getTriple() == null
            || !concept.getTriple().isObjectLiteral())
            return;

        settingTitle = true;

        String str = fetchString();
        String newstr = title.getText();
        if (!str.equals(newstr))
            concept.getTriple().setObjectValue(newstr);
        settingTitle = false;
        updateTitle();
    }

    protected int getVerticalAnchor() {
    	return ConceptLayout.CENTER;
    }
    
    protected int getHorizontalAnchor() {
    	return ConceptLayout.CENTER;
    }
}
