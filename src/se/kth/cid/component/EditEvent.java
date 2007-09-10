/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;


/** EditEvents are fired by components that are changed.
 *  They are intended to be exported over CORBA, something that will require
 *  some thought.
 *
 *  An EditEvent contains the component that has been edited, the type of edit
 *  as specified by a constant in the individual component class,
 *  and an object describing the targeted change, such as the new value.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class EditEvent
{
    Resource component;

  /** The object that has been edited.
   */
  Object editedObject;

  /** The type of edit.
   */
  int editType;

  /* The targeted change. See the relevant edit method in the component
   * of interest for details.
   *
   * This field will possibly cause trouble in a CORBA environment,
   * and thus needs fixing.
   */
  Object    target;

  /** Constructs an EditEvent.
   *
   * @param editedObject the component that has been edited.
   * @param editType the type of edit.
   * @param target the targeted change.
   */
  public EditEvent(Resource comp, Object editedObject, int editType, Object target)
  {
      this.component = comp;
    this.editedObject = editedObject;
    this.editType = editType;
    this.target = target;
  }

  /** Returns the component that has been edited.
   *
   * @return the component that has been edited.
   */
    public Resource getComponent()
    {
	return component;
    }
    
  public Object getEditedObject()
  {
    return editedObject;
  }

  /** Returns the type of edit.
   *
   * @return the type of edit.
   */
  public int getEditType()
  {
    return editType;
  }

  /** Returns the targeted change.
   *
   * @return the targeted change.
   */
  public Object getTarget()
  {
    return target;
  }

  public String toString()
  {
    return "EditEvent[" + editedObject + ", " + editType + ", " + target + "]";
  }
}

