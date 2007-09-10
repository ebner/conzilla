/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout.generic;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import se.kth.cid.component.EditEvent;
import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.layout.BookkeepingConceptLayout;
import se.kth.cid.layout.BookkeepingConceptMap;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.layout.ContextMap.Position;
import se.kth.cid.util.TagManager;

//TODO: This class is not used..... keep it?
public class MemConceptLayout
    extends MemGroupLayout
    implements BookkeepingConceptLayout {

    String conceptURI; // might be relative

    String detailedMap; // Might be relative

    boolean bodyVisible;
    ContextMap.BoundingBox boundingBox;
    ContextMap.Position[] line;

    Vector dataTags;

    Hashtable triples;

    int pathType;
    int horisontalTextAnchor;
    int verticalTextAnchor;

    /** Creates a MemConceptLayout. Do not use.
     *  Use ConceptMap.addConceptLayout() instead.
     */
    public MemConceptLayout(
        String id,
        String conceptURI,
        BookkeepingConceptMap conceptMap,
        Object tag,
        TagManager manager)
        throws InvalidURIException {
        super(id, conceptMap, tag, manager);
        this.conceptURI = conceptURI;
        this.bodyVisible = true;
        this.detailedMap = null;
        boundingBox =
            new ContextMap.BoundingBox(
                new ContextMap.Dimension(0, 0),
                new ContextMap.Position(0, 0));
        line = null;

        triples = new Hashtable();
        dataTags = new Vector();
        horisontalTextAnchor = CENTER;
        verticalTextAnchor = CENTER;
    }

    protected void removeImpl() {
        int size = triples.size();

        for (; size > 0; size--) {
            Iterator it = triples.values().iterator();
            ((StatementLayout) it.next()).remove();
        }

        while (objectOfTriples.size() > 0)
            ((StatementLayout) objectOfTriples
                .elementAt(objectOfTriples.size() - 1))
                .remove();
    }

    public String getConceptURI() {
        return conceptURI;
    }

    public String getDetailedMap() {
        return detailedMap;
    }

    public void setDetailedMap(String uri)
        throws ReadOnlyException, InvalidURIException {
        BookkeepingConceptMap cMap = (BookkeepingConceptMap) getConceptMap();

        detailedMap = uri;

        cMap.fireEditEvent(new EditEvent(cMap, this, DETAILEDMAP_EDITED, uri));
    }

    public ContextMap.BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(ContextMap.BoundingBox rect)
        throws ReadOnlyException {
        BookkeepingConceptMap cMap = (BookkeepingConceptMap) getConceptMap();

        if (rect == null)
            throw new IllegalArgumentException("Null BoundingBox");

        boundingBox = rect;

        cMap.fireEditEvent(new EditEvent(cMap, this, BOUNDINGBOX_EDITED, rect));
    }

    public void setHorisontalTextAnchor(int value) throws ReadOnlyException {
        BookkeepingConceptMap cMap = (BookkeepingConceptMap) getConceptMap();

        horisontalTextAnchor = value;
        cMap.fireEditEvent(
            new EditEvent(cMap, this, HORIZONTAL_TEXT_ANCHOR_EDITED, new Integer(value)));
    }
    public void setVerticalTextAnchor(int value) throws ReadOnlyException {
        BookkeepingConceptMap cMap = (BookkeepingConceptMap) getConceptMap();

        verticalTextAnchor = value;
        cMap.fireEditEvent(
            new EditEvent(cMap, this, VERTICAL_TEXT_ANCHOR_EDITED, new Integer(value)));
    }

    public int getHorisontalTextAnchor() {
        return horisontalTextAnchor;
    }
    public int getVerticalTextAnchor() {
        return verticalTextAnchor;
    }

    public boolean getBodyVisible() {
        return bodyVisible;
    }

    public void setBodyVisible(boolean visible) throws ReadOnlyException {
        BookkeepingConceptMap cMap = (BookkeepingConceptMap) getConceptMap();

        this.bodyVisible = visible;

        cMap.fireEditEvent(
            new EditEvent(
                cMap,
                this,
                BODYVISIBLE_EDITED,
                new Boolean(visible)));
    }

    public String[] getDataTags() {
        return (String[]) dataTags.toArray(new String[dataTags.size()]);
    }

    public int getPathType() {
        return pathType;
    }
    public void setPathType(int pt) {
        pathType = pt;
    }

    public boolean isEditable() {
        //The mem variant is always editable.
        return true;
    }

    /**
     * @see se.kth.cid.layout.DrawerLayout#getBoxLine()
     */
    public Position[] getBoxLine() {
        return null;
    }

    /**
     * @see se.kth.cid.layout.DrawerLayout#setBoxLine(ContextMap.Position[])
     */
    public void setBoxLine(Position[] line) throws ReadOnlyException {
    }

    /**
     * @see se.kth.cid.layout.DrawerLayout#getBoxLinePathType()
     */
    public int getBoxLinePathType() {
        return 0;
    }

    /**
     * @see se.kth.cid.layout.DrawerLayout#setBoxLinePathType(int)
     */
    public void setBoxLinePathType(int pt) {
    }

    //START StatementLayout end managment...      
}

/*
  public StatementLayout[] getStatementLayouts()
    {
      return (StatementLayout[]) triples.values().toArray(new StatementLayout[triples.size()]);
    }

  public StatementLayout getStatementLayout(String id)
    {
      return (StatementLayout) triples.get(id);
    }


  public StatementLayout addStatementLayout(String tripleID, ConceptLayout object)
    throws ReadOnlyException, ConceptMapException
    {
	return addStatementLayout(tripleID, this.getURI(), object.getURI());
    }
  
  public StatementLayout addStatementLayout(String tripleID, String subjectlayouturi, String objectlayouturi)
    throws ReadOnlyException, ConceptMapException
    {
     BookkeepingConceptMap cMap = (BookkeepingConceptMap) getConceptMap();
      if (!cMap.isEditable())
	throw new ReadOnlyException("");

      //Cannot know if conceptlayout belongs to this map or not.....
      /*      if(!(object instanceof MemConceptLayout &&
	   object.getConceptMap() == cMap))
	   throw new ConceptMapException("Tried to add StatementLayout pointing to ConceptLayout in other map!");
      
      //      MemConceptLayout nso = (MemConceptLayout) object;
      //      MemConceptLayout nss = (MemConceptLayout) subject;

      if(triples.containsKey(tripleID))
	throw new ConceptMapException("Triple is already in map.");  

      StatementLayout as = new MemStatementLayout(tripleID, this, subjectlayouturi, objectlayouturi, cMap);
      
      triples.put(tripleID, as);
      
      //nso.objectOfTriples.add(as);
      //nss.subjectOfTriples.add(as);

      cMap.fireEditEvent(new EditEvent(cMap, this, TRIPLESTYLE_ADDED, tripleID));
      return as;
    }

  /** Removes the given StatementLayout.
   *  Do not use. Use StatementLayout.remove() instead.
   *
   *  @param triplelayout the StatementLayout to remove.

  protected void removeTriple(StatementLayout triplelayout) 
    {      
      triples.remove(triplelayout.getURI());
    }

  /** Removes the given StatementLayout from this ConceptLayout endOf-list..
   *  Do not use. Use StatementLayout.remove() instead.
   *
   *  @param triplelayout the StatementLayout to remove.

  public void removeObjectOfTriple(StatementLayout triplelayout) 
    {
      objectOfTriples.removeElement(triplelayout);
    }
   */
