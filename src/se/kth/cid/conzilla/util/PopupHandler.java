/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import se.kth.cid.conzilla.metadata.DescriptionPanel;
import se.kth.cid.conzilla.metadata.PopupTrigger2QueryTarget;

/**
 * This class handles the logic of displaying several descriptions. The
 * descriptions are wrapped in the DescriptionPanel class. How theese
 * DescriptionPanels are displayed need to be implemented, typically by
 * extending this class and implementing the needed abstract functions.
 * 
 * @see DescriptionPanel
 * @author Matthias Palmer
 */
public abstract class PopupHandler {

    /**
     * Locks, unlocks or shows the next description for a given component.
     */
    protected AbstractAction spaceListener;

    /**
     * Unlocks all locked descriptions.
     */
    protected AbstractAction escListener;

    /**
     * Extends the number of descriptions belonging to the same component that
     * is shown at the same time.
     * 
     * @see DescriptionPanel#expand
     */
    protected AbstractAction downListener;

    /**
     * Does the opposite as downListener
     * 
     * @see #downListener
     * @see DescriptionPanel#unExpand
     */
    protected AbstractAction upListener;

    public static final KeyStroke[] strokes = {
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, 0),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, 0),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN, 0),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PLUS, 0),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, 0),            
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0) };

    public static final String[] keys = { "Space", "Up", "Down", "Plus", "Minus", "Esc" };

    protected AbstractAction[] actions;

    /**
     * The current description has its focus due to this trigger.
     */
    protected Object originalTrigger;

    /**
     * The timer used for tooltip popup after some time.
     */
    protected Timer timer;

    /**
     * The scale.
     */
    protected double scale;

    /**
     * The current description.
     */
    protected DescriptionPanel description;

    /**
     * The locked descriptions as values together with their lookup objects as
     * keys.
     */
    protected Hashtable descriptions;

    //Three VERY private attributes.
    Object willBeNextComponent;

    Object willBeNextTrigger;

    boolean registeredKeyBoardActions = false;

    private PopupTrigger2QueryTarget pt2qt;

    /**
     * Implement this, if neccessary to update your container(s) for the
     * DescriptionPanels here.
     */
    public abstract void refresh();

    /**
     * Implement this function to tell if a trigger is valid.
     */
    protected abstract boolean isPopupTrigger(Object o);

    /**
     * Every object should refer to a Resource. Implement this depending on the
     * popupsource.
     * 
     * @return the component for which a description should be shown
     */
    protected abstract Object getComponentFromTrigger(
            Object o);

    /**
     * A description has a origin in the object, when they are stored they need
     * a unique lookupobject.
     * 
     * @return a object representing the description uniquely.
     */
    protected abstract Object getDescriptionLookupObject(Object o);

    /**
     * Here should the descriptions popupfashion be implemented. (Called by
     * showNewDescription)
     * 
     * @see #showNewDescription
     * @see #removeDescriptionImpl
     */
    protected abstract void showNewDescriptionImpl(DescriptionPanel desc);

    /**
     * Implement the reversal of showNewDescriptionImpl. (Called by
     * removeDescription.)
     * 
     * @see #removeDescription
     * @see #showNewDescriptionImpl
     */
    protected abstract void removeDescriptionImpl(DescriptionPanel desc);

    /**
     * Implement to be able to adjust position and size of a description when it
     * changes. (Called by, among others, the keyboardlisteners when they change
     * the descriptions apperance)
     */
    protected abstract void adjustPosition(JComponent comp, Object o);

    /**
     * Override to add behaviour such as move description to front etc.
     */
    protected void activateOldDescriptionImpl(DescriptionPanel desc) {
    }

    /**
     * Override to add behaviour such as move description to back etc.
     */
    protected void inActivateOldDescriptionImpl(DescriptionPanel desc) {
    }

    /**
     * The scale behaviour is highly specific, override to take care of it.
     */
    protected abstract void setScaleImpl(double newvalue, double oldvalue);

    //-----The following functions should not be needed to override.---
    
    /**
     * Initializes the timeout, listeners and such.
     */
    public PopupHandler(PopupTrigger2QueryTarget pt2qt) {
        this.pt2qt = pt2qt;
        descriptions = new Hashtable();

        timer = new Timer(800, new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                showNewDescription();
            }
        });
        timer.setRepeats(false);

        setListeners();
        willBeNextComponent = null;
        scale = 1.0;
    }

    /**
     * A helpfull function for registering the listeners with a Jcomponent.
     */
    protected void registerKeybordActions(JComponent comp) {
        //comp.requestFocus();
        InputMap iMap = new InputMap();
        ActionMap aMap = new ActionMap();
        InputMap iMapOld = comp.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap aMapOld = comp.getActionMap();
        
        for (int i = 0; i < actions.length; i++) {
            iMap.put(strokes[i], keys[i]);
            aMap.put(keys[i], actions[i]);
        }
        
        iMap.setParent(iMapOld);
        aMap.setParent(aMapOld);
        comp.setInputMap(JComponent.WHEN_FOCUSED, iMap);
        comp.setActionMap(aMap);
        registeredKeyBoardActions = true;
    }

    /**
     * A helpfull function for unregistering the listeners with a Jcomponent.
     */
    protected void unRegisterKeyboardActions(JComponent comp) {
        if (!registeredKeyBoardActions || comp == null) {
            return;
        }
        
        //The way to do it after java 1.3...
        InputMap iMapOld = comp.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap aMapOld = comp.getActionMap();
        
        if (iMapOld != null) {
        	comp.setInputMap(JComponent.WHEN_FOCUSED, iMapOld.getParent());
        }
        if (aMapOld != null) {
        	comp.setActionMap(aMapOld.getParent());
        }
        
        registeredKeyBoardActions = false;
    }

    /**
     * Removes all popups.
     */
    public void removeAllPopups() {
        removeDescription();
        Enumeration en = descriptions.elements();
        for (; en.hasMoreElements();)
            removeDescriptionImpl((DescriptionPanel) en.nextElement());

        descriptions = new Hashtable();
        refresh();
    }

    /**
     * Removes, changes, activates descriptions and restarts the timer.
     * 
     * @param o
     *            the trigger of the update.
     */
    protected void updateDescription(Object o) {
        timer.stop();

        willBeNextTrigger = o;
        willBeNextComponent = getComponentFromTrigger(o);

        Object o1 = getDescriptionLookupObject(originalTrigger);
        Object o2 = getDescriptionLookupObject(o);

        //If current active description is 'not locked'.
        if (description != null && o1 != null && descriptions.get(o1) == null)
            removeDescription();

        if (!isPopupTrigger(o))
            return;

        if (o1 != o2 || description == null)
            if (activateOldDescription(o))
                return;

        timer.restart();
    }

    /**
     * Activates the description currently in focus if any. I.e. an description
     * left on the pane locked (not to be removed) is taken control over again.
     */
    private boolean activateOldDescription(Object o) {
        //        Tracer.debug("activateOldDescription ");
        if (description != null)
            inActivateOldDescriptionImpl(description);
        description = null;
        originalTrigger = null;
        Object look = getDescriptionLookupObject(o);
        if (look != null) {
            description = (DescriptionPanel) descriptions.get(look);
            if (description != null) {
                originalTrigger = o;
                activateOldDescriptionImpl(description);
                return true;
            }
        }
        return false;
    }

    /**
     * The timer calls this function when the description should popup. It
     * removes the old description if unlocked and popups a new depending on the
     * last trigger.
     * 
     * @see #showNewDescriptionImpl
     */
    protected void showNewDescription() {
        if (description != null
                || willBeNextComponent == null
                || descriptions
                        .contains(getDescriptionLookupObject(willBeNextTrigger)))
            return;

        DescriptionPanel newDescription = new DescriptionPanel(
                willBeNextComponent, getScale(), pt2qt);

        if (!newDescription.getVisible())
            return;

        description = newDescription;

        showNewDescriptionImpl(newDescription);

        adjustPosition(newDescription, willBeNextTrigger);

        originalTrigger = willBeNextTrigger;

        refresh();
    }

    /**
     * Locks a description so it stays when you go on to next component.
     */
    protected void lockDescription() {
        if (description != null) {
            Object o = getDescriptionLookupObject(originalTrigger);
            if (o != null)
                descriptions.put(o, description);
        }
    }

    /**
     * Removes the current description, locked or not.
     */
    public void removeDescription() {
        timer.stop();
        if (description != null) {
            removeDescriptionImpl(description);
            refresh();
            Object o = getDescriptionLookupObject(originalTrigger);
            if (o != null)
                descriptions.remove(o);

            description = null;
        }
    }

    public void setScale(double newvalue, double oldvalue) {
        scale = newvalue;
        setScaleImpl(newvalue, oldvalue);
    }

    public double getScale() {
        return scale;
    }

    /**
     * Defines all default listeners.
     */
    protected void setListeners() {
        //FIXME handle zoom!
        spaceListener = new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                if (description != null) {
                    description.toggleVisibility();
                    adjustPosition(description, originalTrigger); //huh??
                    if (!description.getVisible())
                        removeDescription();
                } else {
                    showNewDescription();
                    lockDescription();
                }
            }
        };

        upListener = new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                if (description != null && description.isExpanded()) {
                    description.unExpand();
                    adjustPosition(description, originalTrigger);
                }
            }
        };
        downListener = new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                if (description != null && !description.isExpanded()) {
                    description.expand();
                    adjustPosition(description, originalTrigger);
                }
            }
        };
        escListener = new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                removeAllPopups();
            }
        };

        //FIXME conflicts with back/forward
        actions = new AbstractAction[6];
        actions[0] = spaceListener;
        actions[1] = upListener;
        actions[2] = downListener;
        actions[3] = downListener;
        actions[4] = upListener;
        actions[5] = escListener;
    }
}