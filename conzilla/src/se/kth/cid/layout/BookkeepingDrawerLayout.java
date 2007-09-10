/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout;

public interface BookkeepingDrawerLayout extends BookkeepingResourceLayout, DrawerLayout
{  
    void addObjectOfStatementLayout(StatementLayout as);

    StatementLayout[] getObjectOfStatementLayouts();

    void addSubjectOfStatementLayout(StatementLayout as);

    StatementLayout[] getSubjectOfStatementLayouts();

    void removeObjectOfStatementLayout(StatementLayout as);
    
    void removeSubjectOfStatementLayout(StatementLayout as);
}



