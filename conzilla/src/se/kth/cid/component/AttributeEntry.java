/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

public interface AttributeEntry
{
 
    void remove() throws ReadOnlyException;
    
    Container getContainer();

    String getAttribute();

    Object getValueObject();
  
    void setValueObject(Object o);
  
    String getValue();
}
       
