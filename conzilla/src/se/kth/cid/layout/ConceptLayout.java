/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout;

public interface ConceptLayout extends DrawerLayout
{

  ////////////StatementLayout/////////////
}



  
  /** Returns the StatementLayouts that this ConceptLayout is the owner of.
   *
   *  @return the StatementLayouts that this ConceptLayout is the owner of. Never null.

  StatementLayout[] getStatementLayouts();
   */

  /** Returns the StatementLayout with the given ID.
   *
   *  @param id the ID of the wanted StatementLayout.
   *  @return the StatementLayout with the given ID.

  StatementLayout getStatementLayout(String id);
   */
  /** Adds an StatementLayout to this ConceptLayout AND WITH THIS
   *  conceptlayout as subject as well.
   *  
   *  @deprecated use <code>addStatementLayout(String, ConceptLayout, ConceptLayout)</code> instead.
   *  @param tripleID the Triple ID of the StatementLayout within the Concept.
   *  @param object the ConceptLayout the StatementLayout will point to.

  StatementLayout addStatementLayout(String tripleID, ConceptLayout object)
    throws ReadOnlyException, ConceptMapException;
   */

  /** Adds an StatementLayout to this ConceptLayout.
   *  
   *  @param tripleID the Triple ID of the StatementLayout within the Concept.
   *  @param subject the ConceptLayout the StatementLayout will point from.
   *  @param object the ConceptLayout the StatementLayout will point to.

  StatementLayout addStatementLayout(String tripleID, String subjectlayouturi, String objectlayouturi)
    throws ReadOnlyException, ConceptMapException;
   */
