/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout.generic;
import java.util.Vector;

import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.layout.BookkeepingConceptMap;
import se.kth.cid.layout.BookkeepingResourceLayout;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.ResourceLayout;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.tree.generic.MemTreeTagNodeComponent;
import se.kth.cid.util.TagManager;

public abstract class MemResourceLayout extends MemTreeTagNodeComponent implements ResourceLayout, BookkeepingResourceLayout
{
  BookkeepingConceptMap     conceptmap;

  Vector objectOfTriples;
  Vector subjectOfTriples;

  /** 
   */
    protected MemResourceLayout(String URI, BookkeepingConceptMap map, Object tag, TagManager manager)
    {
      super(URI, tag, manager);
      this.conceptmap = map;
      subjectOfTriples = new Vector();
      objectOfTriples = new Vector();
    }
  
  public final void remove()  throws ReadOnlyException
    {
      removeImpl();
      conceptmap.removeResourceLayout(this);
    }

  protected void removeImpl() {}

  
  public ContextMap getConceptMap()
   {
     return conceptmap;
   }

  public void addObjectOfStatementLayout(StatementLayout as)
    {
	objectOfTriples.add(as);
    }

  public void addSubjectOfStatementLayout(StatementLayout as)
    {
	subjectOfTriples.add(as);
    }
  public void removeObjectOfStatementLayout(StatementLayout as)
    {
	objectOfTriples.remove(as);
    }

  public void removeSubjectOfStatementLayout(StatementLayout as)
    {
	subjectOfTriples.remove(as);
    }

  public StatementLayout[] getObjectOfStatementLayouts()
    {
	return (StatementLayout[]) objectOfTriples.toArray(new StatementLayout[objectOfTriples.size()]);
    }

  public StatementLayout[] getSubjectOfStatementLayouts()
    {
	return (StatementLayout[]) subjectOfTriples.toArray(new StatementLayout[subjectOfTriples.size()]);
    }
}

