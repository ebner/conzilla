/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout;

public interface BookkeepingStatementLayout
    extends StatementLayout, BookkeepingDrawerLayout {
      
    void setSubjectLayout(DrawerLayout subject);

    void setObjectLayout(DrawerLayout object);
}
