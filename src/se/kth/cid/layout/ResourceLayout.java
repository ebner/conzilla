/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout;
import se.kth.cid.component.Component;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.tree.TreeTagNode;

public interface ResourceLayout extends Component, TreeTagNode
{

  /** Returns the ConceptMap this ResourceLayout is located in.
   *
   *  @return the ConceptMap this ResourceLayout is located in.
   */
  ContextMap getConceptMap();  

   /** Removes this ResourceLayout from the ConceptMap. 
    *  Inherited classes may extend the behaviour, e.g.
    *  a ConceptLayout removes all StatementLayouts it is connected to, 
    *  either as subject or object (should it??).
    */
  void remove() throws ReadOnlyException;
  
  /** Checks whether this ResourceLayout is editable. This state can in general
   *  not be changed, as it depends on from which Container it is loaded. Also,
   *  this state is not expected to change during the usage of the component.
   *
   *  @return true if this component is editable, false otherwise.
   */
  boolean isEditable();
}



