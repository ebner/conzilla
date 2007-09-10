/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map.graphics;

import javax.swing.JComponent;

import se.kth.cid.concept.Concept;

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
            && concept.getTriple().isObjectLiteral())
            return drawerMapObject.getConcept().getTriple().objectValue();
        else
            return "";
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

    /*public void updateBox(Rectangle2D re) {
        bb = null;
        Dimension td = title.getPreferredSize();
        if (re == null)
            return;

        int ew = 0;
        int eh = 0;
        int dx = 0;
        int dy = 0;
        int x = 0, y = 0, width, height;

        if (td.width < (re.getWidth() + ew)) {
            width = (int) (re.getWidth() - (re.getWidth() - td.width) / 2);
            x = (int) (((re.getWidth() - td.width + ew) / 2) + dx + re.getX());
        } else {
            width = (int) re.getWidth() + ew;
            x = (int) re.getX() + dx;
        }
        if (td.height < (re.getHeight() + eh)) {
            height = (int) (re.getHeight() - (re.getHeight() - td.height) / 2);
            y =
                (int) (((re.getHeight() - td.height + eh) / 2)
                    + dy
                    + re.getY());
        } else {
            height = (int) re.getHeight() + eh;
            y = (int) re.getY() + dy;
        }

        bb = new Rectangle(x, y, width, height);
    }*/

}
