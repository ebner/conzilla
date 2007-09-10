/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.tool;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.properties.ColorTheme;
import se.kth.cid.conzilla.properties.ConzillaResourceManager;

/**
 * This class is a menu that supports adding tools as buttons.
 * 
 * @author Mikael Nilsson
 * @version $Revision$
 */
public class ToolsBar extends JToolBar implements PropertyChangeListener {
    
	private static final long serialVersionUID = 1L;
	
	/**
     * Maps tool --> AbstractButton
     */
    Hashtable toolItems;

    /**
     * Constructs a ToolsBar with the given name.
     * 
     * @param title
     *            thetitle of this menu.
     */
    public ToolsBar(String title, String resbundle) {
        super(ConzillaResourceManager.getDefaultManager().getString(resbundle,
                title));

        toolItems = new Hashtable();
    }

    /**
     * Adds a tool to the menu.
     * 
     * @param tool
     *            the tool to add.
     * @return the menu item that represents the tool.
     */
    public AbstractButton addTool(final Tool tool) {
        AbstractButton button = null;

        if (tool instanceof ExclusiveStateTool) {
            final ExclusiveStateTool stateTool = (ExclusiveStateTool) tool;
            final JRadioButton stateButton = new JRadioButton(tool);
            stateButton.setSelected(stateTool.isActivated());

            button = stateButton;

            button.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED)
                        stateTool.setActivated(true);
                    else if (e.getStateChange() == ItemEvent.DESELECTED
                            && stateTool.isActivated())
                        stateButton.setSelected(true);
                }
            });
            add(button);
        } else if (tool instanceof StateTool) {
            final StateTool stateTool = (StateTool) tool;
            final JToggleButton stateButton = new JToggleButton(tool);
            stateButton.setSelected(stateTool.isActivated());

            button = stateButton;

            button.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        stateTool.setActivated(true);
                    } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                        stateTool.setActivated(false);
                    }
                }
            });
            add(button);
        } else {
            button = add(tool);
        }

        button.setEnabled(tool.isEnabled());

        customizeButton(button, tool);

        tool.addPropertyChangeListener(this);
        ConfigurationManager.getConfiguration().addPropertyChangeListener(ColorTheme.COLORTHEME, this);
        toolItems.put(tool, button);

        return button;
    }

    /**
     * Returns the menu item for a given tool.
     * 
     * @param tool
     *            the tool to search for.
     * @return the menu item for the given tool.
     */
    public AbstractButton getToolButton(Tool tool) {
        return (AbstractButton) toolItems.get(tool);
    }

    /**
     * Fetch all tools in this menu.
     */
    public Enumeration getTools() {
        return toolItems.keys();
    }

    /**
     * Removes a tool from this menu.
     * 
     * @param t
     *            the tool to remove.
     */
    public void removeTool(Tool t) {
        AbstractButton item = getToolButton(t);

        if (item == null)
            return;

        remove(item);
        t.removePropertyChangeListener(this);
        ConfigurationManager.getConfiguration().removePropertyChangeListener(ColorTheme.COLORTHEME, this);
        toolItems.remove(t);
    }

    /**
     * Removes all tools from this menu.
     */
    public void removeAllTools() {
        Enumeration tools = toolItems.keys();
        for (; tools.hasMoreElements();)
            removeTool((Tool) tools.nextElement());
    }

    public void propertyChange(PropertyChangeEvent e) {
    	if (e.getPropertyName().equals(ColorTheme.COLORTHEME)) {
    		Enumeration toolEnum = getTools();
    		while (toolEnum.hasMoreElements()) {
    			Tool t = (Tool)toolEnum.nextElement();
    			AbstractButton ab = getToolButton(t);
    			customizeButton(ab, t);
    		}
    	} else {
    		Tool tool = (Tool)e.getSource();
    		AbstractButton button = getToolButton(tool);

    		if (button == null) {
    			return;
    		}

    		if (e.getPropertyName().equals(StateTool.ACTIVATED)) {
    			button.setSelected(((Boolean) e.getNewValue()).booleanValue());
    		} else if (e.getPropertyName().equals(Tool.ENABLED)) {
    			button.setEnabled(((Boolean) e.getNewValue()).booleanValue());
    		}
    	}
    }

    protected void customizeButton(AbstractButton button, Tool tool) {
        if (tool.getIcon() != null) {
            button.setText(null);
        }

        if (tool.getSelectedIcon() != null) {
            button.setSelectedIcon(tool.getSelectedIcon());
        }
        
        button.setMargin(new Insets(0, 0, 0, 0));
        Dimension pref = button.getPreferredSize();
        pref.height = 26;
        if (tool.getIcon() != null) {
        	pref.width = 27;
        }
        button.setMaximumSize(pref);
        button.setPreferredSize(pref);
        button.setMinimumSize(pref);
    }
}