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
package se.kth.cid.conzilla.util;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.beans.*;


/** This class handles the logic of displaying several 
 *  descriptions. 
 *  The descriptions are wrapped in the DescriptionPanel class.
 *  How theese DescriptionPanels are displayed need to 
 *  be implemented, typically by extending this class and
 *  implementing the needed abstract functions.
 *
 *  @see DescriptionPanel
 *  @author Matthias Palmer
 */
public abstract class PopupHandler 
{
    /** @see #spaceListener
     */
    protected KeyStroke spaceStroke;

    /** @see #downListener
     */
    protected KeyStroke expandSpaceStroke;

    /** @see #escListener
     */
    protected KeyStroke escStroke;    

    /** @see #downListener
     */
    protected KeyStroke downStroke;

    /** @see #upListener
     */
    protected KeyStroke upStroke;

    /** @see #leftListener
     */
    protected KeyStroke leftStroke;

    /** @see #rightListener
     */
    protected KeyStroke rightStroke;

    /** Locks, unlocks or shows the next description 
     *  for a given component.
     *
     *  @see DescriptionPanel#showNext
     */
    protected AbstractAction spaceListener;
    
    /** Unlocks all locked descriptions.
     */
    protected AbstractAction escListener;

    /** Extends the number of descriptions belonging
     *  to the same component that is shown at the same time.
     *
     *  @see DescriptionPanel#expand
     */
    protected AbstractAction downListener;

    /** Does the opposite as downListener
     *
     *  @see #downListener
     *  @see DescriptionPanel#unExpand
     */
    protected AbstractAction upListener;
    
    /** Does the same as SpaceListener
     *
     *  @see #spaceListener
     */
    protected AbstractAction rightListener;

    /** Shows the previous description belonging to 
     *  the current component.
     *
     *  @see DescriptionPanel#showPrev
     */
    protected AbstractAction leftListener;

    public final static KeyStroke [] strokes = {KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE ,0),
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP ,InputEvent.ALT_MASK),
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN ,InputEvent.ALT_MASK),
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE ,InputEvent.ALT_MASK),
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT ,InputEvent.ALT_MASK),
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT ,InputEvent.ALT_MASK),
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE ,0)};

    public final static String [] keys = {"Space",
					  "Up",
					  "Down",
					  "Expand",
					  "Left",
					  "Right",
					  "Esc"};
    
    protected AbstractAction [] actions;
    /** The current description has its focus due
     *  to this trigger.
     */
    protected Object originalTrigger;
    
    /** The timer used for tooltip popup after some time.
     */
    protected Timer timer;

    /** The current description.
     */
    
    /* The scale.
     */
    protected double scale;

    protected DescriptionPanel description;
    
    /** The locked descriptions as values together with their 
     *  lookup objects as keys.
     */
    protected Hashtable descriptions;

    //Three VERY private attributes.
    se.kth.cid.component.Component willBeNextComponent;
    Object willBeNextTrigger;
    boolean registeredKeyBoardActions=false;

  /** Implement this funtion to set listeners on your popupsource.
   *  (Use the help-function registerKeyboardListeners())
   *
   *  @see #registerKeyboardActions
   */
  public abstract void activate();

  /** Implement this funtion to undo what is done in activate.
   *  (Consider if a call to removeAllPopups should be done.)
   *
   *  @see #unRegisterKeyboardActions 
   *  @see #removeAllPopups
   */
  public abstract void deactivate();

  /** Implement this, if neccessary to update your container(s)
   *  for the DescriptionPanels here.
   */
  public abstract void refresh();

  /** Implement this function to tell if a trigger is valid.
   */
  protected abstract boolean isPopupTrigger(Object o);
 
  /** Every object should refer to a Component.
   *  Implement this depending on the popupsource.
   *
   *  @return the component for which a description should be shown
   */  
  protected abstract se.kth.cid.component.Component getComponentFromTrigger(Object o);

    /** A description has a origin in the object, 
     *  when they are stored they need a unique lookupobject.
     *
     *  @return a object representing the description uniquely.
     */  
  protected abstract Object getDescriptionLookupObject(Object o);

  /** Here should the descriptions popupfashion be implemented.
   *  (Called by showNewDescription)
   *
   *  @see #showNewDescription
   *  @see #removeDescriptionImpl
   */
  protected abstract void showNewDescriptionImpl(DescriptionPanel desc);

  
  /** Implement the reversal of showNewDescriptionImpl.
   *  (Called by removeDescription.)
   *
   *  @see #removeDescription
   *  @see #showNewDescriptionImpl
   */
  protected abstract void removeDescriptionImpl(DescriptionPanel desc); 

  /** Implement to be able to adjust position and size 
   *  of a description when it changes.
   *  (Called by, among others, the keyboardlisteners when they change 
   *   the descriptions apperance)
   */
  protected abstract void adjustPosition(JComponent comp, Object o);
    
  /** Override to add behaviour such as move description to front etc.
   */
  protected void activateOldDescriptionImpl(DescriptionPanel desc)
    {
    }

  /** Override to add behaviour such as move description to back etc.
   */
  protected void inActivateOldDescriptionImpl(DescriptionPanel desc)
    {
    }

  /** The scale behaviour is highly specific, override to take care of it.
   */
  protected abstract void setScaleImpl(double newvalue, double oldvalue);

  //-----The following functions should not be needed to override.---

  /** The default Construcotr initializes the timeout, 
   *  listeners and such.
   */
  public PopupHandler()
    {
	descriptions=new Hashtable();
	
	timer=new Timer(800,new AbstractAction() {
		public void actionPerformed(ActionEvent ae)
		{
		    showNewDescription();
		}
	    });
	timer.setRepeats(false);

	setListeners();
	willBeNextComponent=null;
	scale=1.0;
    }

    /** A helpfull function for registering the listeners
     *  with a Jcomponent.
     */
  protected void registerKeybordActions(JComponent comp)
    {
	try {
	    //FIXME: Awkward for backward compability to jdk1.2.2
	    //Do statically when 1.3 is standard.
	    //Should actually work anyway..... exceptions from strange places in class ListContentSelector
	    ClassLoader loader=Class.class.getClassLoader();
	    Class inputMapClass = loader.loadClass("javax.swing.InputMap");
	    Class actionMapClass = loader.loadClass("javax.swing.ActionMap");
	    Object iMap = inputMapClass.newInstance();
	    Object aMap= actionMapClass.newInstance();

	    Object iMapOld=comp.getInputMap(JComponent.WHEN_FOCUSED);
	    Object aMapOld=comp.getActionMap();

	    Class [] ic={KeyStroke.class, String.class}, ac= {String.class, AbstractAction.class};
	    Object [] io=new Object[2], ao=new Object[2];

	    
	    for (int i=0; i<actions.length;i++)
		{
		    io[0]=strokes[i];
		    io[1]=keys[i];
		    ao[0]=keys[i];
		    ao[1]=actions[i];
		    inputMapClass.getMethod("put", ic).invoke(iMap, io);
		    actionMapClass.getMethod("put", ac).invoke(aMap, ao);
		}
	    
	    /*	    iMap.put(spaceStroke, "Space");
	    aMap.put("Space", spaceListener);
	    
	    iMap.put(upStroke, "Up");
	    aMap.put("Up", upListener);
		    
	    iMap.put(downStroke, "Down");
	    aMap.put("Down", downListener);

	    iMap.put(expandSpaceStroke, "Expand");
	    aMap.put("Expand", downListener);

	    iMap.put(leftStroke, "Left");
	    aMap.put("Left", leftListener);

	    iMap.put(rightStroke, "Right");
	    aMap.put("Right", rightListener);

	    iMap.put(escStroke, "Esc");
	    aMap.put("Esc", escListener);

	    */

	    Class [] c1args={actionMapClass}, c2args={Integer.TYPE, inputMapClass};
	    Object [] args1=new Object[1], args2=new Object[2];
	    args1[0]=aMapOld;
	    actionMapClass.getMethod("setParent", c1args).invoke(aMap,args1);
	    //aMap.setParent(aMapOld);	    

	    args1[0]=iMapOld;
	    inputMapClass.getMethod("setParent", c1args).invoke(iMap,args1);
	    //iMap.setParent(iMapOld);

	    args2[0]=new Integer(JComponent.WHEN_FOCUSED);
	    args2[1]=iMap;
	    args1[0]=aMap;
	    JComponent.class.getMethod("setInputMap",c2args).invoke(comp,args2);
	    //	    comp.setInputMap(JComponent.WHEN_FOCUSED, iMap); 
	    JComponent.class.getMethod("setActionMap",c1args).invoke(comp,args1);
	    //	    comp.setActionMap(aMap);

	} catch (Exception e) {
	    //JDK1.2.2
	    for (int i=0; i<actions.length;i++)
		comp.registerKeyboardAction(actions[i], keys[i],
					    strokes[i],
					    JComponent.WHEN_FOCUSED);		    
	    
	}
	registeredKeyBoardActions=true;
    }

    /** A helpfull function for unregistering the listeners
     *  with a Jcomponent.
     */
  protected void unRegisterKeyboardActions(JComponent comp)
    {
	if (!registeredKeyBoardActions)
	    return;
	try {
	    //FIXME: Awkward for backward compability to jdk1.2.2
	    //Do statically when 1.3 is standard.
	    ClassLoader loader=Class.class.getClassLoader();
	    Class inputMapClass = loader.loadClass("javax.swing.InputMap");
	    Class actionMapClass = loader.loadClass("javax.swing.ActionMap");

	    Class [] arg_int={Integer.TYPE};
	    Object [] args1=new Object[1], args2=new Object[2];

	    args1[0]=new Integer(JComponent.WHEN_FOCUSED);	    
	    Object iMap = JComponent.class.getMethod("getInputMap",arg_int).invoke(comp,args1);
	    Object iMapOld = inputMapClass.getMethod("getParent",null).invoke(iMap, null);
	    //InputMap iMapOld=comp.getInputMap(JComponent.WHEN_FOCUSED).getParent();
	    Object aMap = JComponent.class.getMethod("getActionMap",null).invoke(comp,null);
	    Object aMapOld = actionMapClass.getMethod("getParent",null).invoke(aMap, null);
	    //ActionMap aMapOld=comp.getActionMap().getParent();


	    args2[0]=new Integer(JComponent.WHEN_FOCUSED);
	    args2[1]=iMapOld;
	    args1[0]=aMapOld;
	    Class [] carg_int_IM={Integer.TYPE, inputMapClass}; 
	    JComponent.class.getMethod("setInputMap",carg_int_IM).invoke(comp,args2);
	    //comp.setInputMap(JComponent.WHEN_FOCUSED, iMapOld); 
	    Class [] carg_AM={actionMapClass};
	    JComponent.class.getMethod("setActionMap",carg_AM).invoke(comp,args1);
	    //comp.setActionMap(aMapOld);
	} catch (Exception e) {
	    //Jdk1.2.2
	    for (int i=0; i<actions.length;i++)
		comp.unregisterKeyboardAction(strokes[i]);
	}
	registeredKeyBoardActions=false;
    }

  /** Removes all popups.
   */
  public void removeAllPopups()
  {
      removeDescription();
      Enumeration en=descriptions.elements();
      for (;en.hasMoreElements();)
	  removeDescriptionImpl((DescriptionPanel) en.nextElement());

      descriptions=new Hashtable();
      refresh();
  }

   
  /** Removes, changes, activates descriptions and restarts the timer.
   *
   * @param o the trigger of the update.
   */ 
  protected void updateDescription(Object o)
  {
    timer.stop();

    willBeNextTrigger=o;
    willBeNextComponent=getComponentFromTrigger(o);

    Object o1=getDescriptionLookupObject(originalTrigger);
    Object o2=getDescriptionLookupObject(o);
	
    //If current active description is 'not locked'.
    if (description!=null && o1!=null && 
	descriptions.get(o1)==null) 
	removeDescription();

    if (!isPopupTrigger(o))
	return;

    if (o1!=o2 || description==null)
	if (activateOldDescription(o))
	    return;
    
    timer.restart();
  }

  /** Activates the description currently in focus if any.
   *  I.e. an description left on the pane locked (not to be removed)
   *  is taken control over again.
   */
  private boolean activateOldDescription(Object o)
    {
	if (description!=null)
	    inActivateOldDescriptionImpl(description);
	description=null;
	originalTrigger=null;
	Object look=getDescriptionLookupObject(o);
	if (look!=null)
	    {
		description = (DescriptionPanel) descriptions.get(look);
		if (description!=null)
		    {
			/*			removeDescriptionImpl(description);
			showNewDescription(description);
			adjustPosition(description, o);
			refresh();
			*/
			originalTrigger=o;
			activateOldDescriptionImpl(description);
			return true;
		    }
	    }
	return false;
    }
	
    /** The timer calls this function when the description 
     *  should popup. It removes the old description if unlocked 
     *  and popups a new depending on the last trigger.
     *
     *  @see #showNewDescriptionImpl
     */
  protected void showNewDescription()
  {
    if (description!=null || willBeNextComponent==null || 
	descriptions.contains(getDescriptionLookupObject(willBeNextTrigger)))
	return;

    DescriptionPanel newDescription = new DescriptionPanel(willBeNextComponent,
							   getScale());

    if(newDescription.getText().equals(""))
	return;

    description=newDescription;
 
    showNewDescriptionImpl(newDescription);

    adjustPosition(newDescription, willBeNextTrigger);

    originalTrigger = willBeNextTrigger;

    refresh();
  }
  
    /** Locks a description so it stays when you go on to 
     *  next component.
     */
  protected void lockDescription()
  {
      if (description != null)
	  {
	      Object o=getDescriptionLookupObject(originalTrigger);
	      if (o!=null)
		  descriptions.put(o, description);
	  }		  
  }

  /** Removes the current description, locked or not.
   */
  public void removeDescription()
  {
    timer.stop();
    if (description!=null)
      {
	  removeDescriptionImpl(description);
	  refresh();
	  Object o=getDescriptionLookupObject(originalTrigger);
	  if (o!=null)
	      descriptions.remove(o);
	  
	  description=null;
      }
  }
  public void setScale(double newvalue, double oldvalue)
    {
	scale=newvalue;
	setScaleImpl(newvalue, oldvalue);
    }

  public double getScale()
    {
	return scale;
    }

  /** Defines all default listeners.
   */
  protected void setListeners()
  {      
      //FIXME handle zoom!
      spaceListener = new AbstractAction() {
	      public void actionPerformed(ActionEvent ae) {
		  if(description != null)
		      {
			  description.showNext();
			  adjustPosition(description, originalTrigger); //huh??
			  if(description.getText().equals(""))
			      removeDescription();
		      }
		  else
		      {
			  showNewDescription();
			  lockDescription();
		      }
	      }};
      leftListener = new AbstractAction() {
	      public void actionPerformed(ActionEvent ae) {
		  if(description != null)
		      {
			  description.showPrev();
			  adjustPosition(description, originalTrigger);
			  if(description.getText().equals(""))
			      removeDescription();
		      }
		  else
		      {
			  showNewDescription();
			  if(description != null)
			      {
				  description.showLast();
				  lockDescription();
			      }
		      }
	      }};
      rightListener = spaceListener;

      upListener = new AbstractAction() {
	      public void actionPerformed(ActionEvent ae) {
		  if(description != null)
		      {
			  description.unExpand();
			  adjustPosition(description, originalTrigger);
		      }
	      }};
      downListener = new AbstractAction() {
	      public void actionPerformed(ActionEvent ae) {
		  if(description != null)
		      {
			  description.expand();
			  adjustPosition(description, originalTrigger);
		      }
	      }};
      escListener =new AbstractAction() {
	      public void actionPerformed(ActionEvent ae) {
		  removeAllPopups();
	      }};
		  
      actions = new AbstractAction[7];
      actions[0] = spaceListener;
      actions[1] = upListener;
      actions[2] = downListener;
      actions[3] = downListener;
      actions[4] = leftListener;
      actions[5] = rightListener;
      actions[6] = escListener;	       
  }  
}
