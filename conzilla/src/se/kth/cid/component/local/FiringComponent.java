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


package se.kth.cid.component.local;
import se.kth.cid.component.*;

/** This interface adds two different updating functionalities for an 
 *  implementation of a component.
 *
 *  Either the component is updated from storage, hence it isn't edited 
 *  and shouldn't be marked as such (avoiding it to be saved).
 *  Or it is changed by the application and the changes should be saved
 *  in the end of the session (except if it is a CORBA component of course).
 *
 *  @author Matthias Palmer
 */
public interface FiringComponent extends Component
{   
  /** Fires an EditEvent to all listeners and marks the
   *  component as being edited.
   *
   *  @param e the event to fire.
   */
  void fireEditEvent(EditEvent e);

  /** Fires an EditEvent to all listeners without marking the component as being edited.
   *
   *  @param e the event to fire.
   */
  void fireEditEventNoEdit(EditEvent e);
}
