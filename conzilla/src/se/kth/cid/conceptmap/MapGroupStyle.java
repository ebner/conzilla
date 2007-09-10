/* $Id$ */
/*
  This file is part of the Conzilla browser, designed for
  the Garden of Knowledge project.
  Copyright (C) 1999  CID (http://www.nada.kth.se/cid)
  
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/


package se.kth.cid.conceptmap;
import se.kth.cid.util.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;
import java.util.*;

/** 
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public interface MapGroupStyle extends ObjectStyle
{
    int IGNORE_VISIBILITY = 0;
    int ONLY_VISIBLE = 1;
    int ONLY_INVISIBLE = 2;


    //******Access, creation and deletion of ObjectStyles.*******
    //-----------------------------------------------------------------------

    void addObjectStyle(ObjectStyle objectStyle, Object tag);

    boolean removeObjectStyle(ObjectStyle objectStyle);
   
    boolean recursivelyRemoveObjectStyle(ObjectStyle objectStyle);
        
    Vector getObjectStyles();
    
    ObjectStyle getObjectStyle(String id);

    ObjectStyle recursivelyGetObjectStyle(String id);

    MapGroupStyle getParent(ObjectStyle os);


    //****Manipulation of visibility and order of layers.*******
    //----------------------------------------------------------------------------

    void setObjectStyleHidden(String mapID, boolean hidden);

    boolean getObjectStyleHidden(String mapID);
    
    Object getObjectStyleTag(ObjectStyle os);
    
    void setTagVisible(Object tag, boolean visible);

    boolean getTagVisible(Object tag);

    Enumeration getTags();

    void lowerObjectStyle(ObjectStyle os);

    void raiseObjectStyle(ObjectStyle os);

    int getOrderOfObjectStyle(ObjectStyle os);

    void setOrderOfObjectStyle(ObjectStyle os, int position);


    //***********Conditional deep access to objectStyles*****************
    //-------------------------------------------------------------------

    Vector getObjectStyles(int visible, Class restrictToType);

    Hashtable getHashedObjectStyles(int visible, Class restrictToType);

    void getObjectStyles(Object collect, int visible, Class restrictedToType);


    //*******************Miscellaneous help functions.*******************
    //-------------------------------------------------------------------

    Set IDSet();

    void IDSet(Set set);
}
//     int getNumberOfObjectStyles();
