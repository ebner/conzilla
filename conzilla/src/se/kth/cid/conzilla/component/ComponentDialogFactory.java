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


package se.kth.cid.conzilla.component;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.component.*;
import se.kth.cid.neuron.Neuron;
import se.kth.cid.util.*;
import se.kth.cid.conceptmap.ConceptMap;
import java.util.*;

/** There can be several different ComponentDialogs,
 *  this class provides methods to access
 *  different ComponentDialogs by a keyword, a (component)dialogname.
 *
 *  This class follows the programming pattern of an abstract factory
 *  implemented with prototypes.
 */
class ComponentDialogFactory
{
  /** A Hashtable of ComponentDialogs with dialognames as keys,
   *  typical there should be one called contentdescription.
   *  @see getPossibleDialogs
   */
  Hashtable componentDialogs;

  ConzillaKit kit;
  /** This initites the factory with a default ComponentDialog (a ComponentDialogAdapter),
   *  it can be requested via the command getComponentDialog(String) with a null
   *  or "default" String as argument.
   */
  public ComponentDialogFactory(ConzillaKit kit)
    {
      this.kit=kit;
      componentDialogs=new Hashtable();
      componentDialogs.put("default", new ComponentDialog(kit));
    }
  
  /** The ComponentDialogs Keys, e.g. (component)dialognames.
   *
   *  @return Enumeration contains the keys in the form of String's.
   */
  public Enumeration getPossibleDialogs()
    {
      return componentDialogs.keys();
    }
  
  /** Returns a new ComponentDialog specified by it's String key dialogname.
   *  The hashtable contains ComponentDialog-prototypes which are copied by
   *  the copy-functionality in ComponentType.
   *
   *  @see ComponentDialog
   *  @return ComponentDialog i.e. an object implementing this interface.
   */
  public ComponentDialog getComponentDialog(String dialogname)
    {
      if (dialogname==null)
	return ((ComponentDialog) componentDialogs.get("default")).copy();
      return ((ComponentDialog) componentDialogs.get(dialogname)).copy();
    }
  /** Same as getComponentDialog(String ) except that it selects
   *  wich dialog depending on the component and also connects the same
   *  with the dialog.
   */
  public ComponentDialog getComponentDialog(Component component, Class cl)
    {
      ComponentDialog cd=null;
      if (cl!=null)
	{
	  if (ComponentDialog.class.isAssignableFrom(cl))
	    {
	      if (cl==NeuronDialog.class)
		cd=new NeuronDialog(kit);
	      else if (cl==ConceptMapDialog.class)
		cd=new ConceptMapDialog(kit);
	      else if (cl==ContentDialog.class)
		cd=new ContentDialog(kit);
	    }
	  else if (ComponentDraft.class.isAssignableFrom(cl))
	    {
	      if (cl==NeuronDraft.class)
		cd=new NeuronDialog(kit);
	      else if (cl==ConceptMapDraft.class)
		cd=new ConceptMapDialog(kit);
	      else if (cl==ContentDraft.class)
		cd=new ContentDialog(kit);
	    }
	}
      else
	{
	  if (component instanceof Neuron)
	    cd=new NeuronDialog(kit);
	  else if (component instanceof ConceptMap)
	    cd=new ConceptMapDialog(kit);
	}

      if (cd==null)
	cd=new ComponentDialog(kit);
      cd.setComponent(component);
      return cd;
    }

  /** Adds a ComponentDialog.
   * @param nf is the ComponentDialog to add
   * @param dialogname  the key to the dialog.
   */
  public void addComponentDialog(ComponentDialog nd, String dialogname)
    {
      componentDialogs.put(dialogname, nd);
    }
  
  /** Removes the ComponentDialog with key dialogname if it exists.
   *
   *  @param dialogname is a String, i.e a key to the dialog to be removed.
   */
  public ComponentDialog removeComponentDialog(String dialogname)
    {
      return (ComponentDialog) componentDialogs.remove(dialogname);
    }
}


