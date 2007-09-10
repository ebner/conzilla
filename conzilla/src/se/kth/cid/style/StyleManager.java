/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.style;

import java.util.List;

import se.kth.cid.layout.DrawerLayout;

/** The stylemanager is responsible for keeping track of styles.
 *  The priority should be something like 
 *  (need investigation, compare with CSS* priorities).
 *  <ol>
 *   <li>From layout resource (context dependent view of concept)</li>
 *   <li>From conceptmap stylesheeting of class,
 *       all instances of that class in this conceptmap is styled accordingly.</li>
 *   <li>Global binding to concept.</li>
 *   <li>Global binding of class which affects all instances.</li>
 *  </ol>
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public interface StyleManager {
    
    String STYLES = "styles";

    List getStylesFromClass(String type);
    
    List getStylesFromProperty(String type);

    /** Fetches the styles (order is cascading like in CSS) for this drawerlayout.
     */
    List getStylesForDrawer(DrawerLayout dl);

    /** Fetches an attribute from the list of styles.
     */
    Object getAttributeValue(List styles, String attribute, Object def);

    //help functions.... maybe need them later.
    /*  int     getAttributeIntValue(List styles, String attribute, int default);
    double  getAttributeDoubleValue(List styles, String attribute, double default);
    boolean getAttributeBooleanValue(List styles, String attribute, boolean default);
    String  getAttributeStringValue(List styles, String attribute, String default);
    */
}
/** Returns a BoxStyle for the given DrawerLayout.

BoxStyle getBoxStyleForDrawer(DrawerLayout rl);

/** Returns a LineStyle for the given StatementLayout.
LineStyle getLineStyleForStatement(StatementLayout sl);
 */
