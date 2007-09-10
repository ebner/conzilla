/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import se.kth.cid.config.Config;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.menu.MenuFactory;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.ToolsMenu;

public abstract class AbstractViewManager implements ViewManager {

    ArrayList views;

    PropertyChangeSupport pcs;

    public AbstractViewManager() {
    }

    public void initManager() {
        pcs = new PropertyChangeSupport(this);
        views = new ArrayList();
    }

    public void detachManager() {
    	saveProperties();
        closeViews();
        pcs = null;
        views = null;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    public void fireViewsChanged() {
        pcs.firePropertyChange(VIEWS_PROPERTY, null, null);
    }

    protected void addView(View view) {
        views.add(view);
        fireViewsChanged();
    }

    public View getView(MapController c) {
        Iterator e = getViews();
        while (e.hasNext()) {
            View v = (View) e.next();
            if (v.getController() == c)
                return v;
        }
        return null;
    }

    public Iterator getViews() {
        return views.iterator();
    }

    public void close(View view, boolean closeController) {
    	if (view.getMapScrollPane() != null) {
    		view.getMapScrollPane().getDisplayer().reset();
    	}
    	
    	closeView(view, closeController);
        views.remove(view);
        fireViewsChanged();
    }

    public void closeViews() {
        Iterator e = getViews();
        while (e.hasNext()) {
            close((View) e.next(), true);
            e = getViews();
        }
    }

    protected abstract void closeView(View v, boolean closeController);

    protected JMenuBar makeMenuBar(View view, boolean includeBar) {
        if (ConzillaKit.getDefaultKit().getMenuFactory() == null) {
            return null;
        }
        
        JMenuBar mb = new JMenuBar();

        ToolsMenu[] menus = view.getMenus();
        ToolsMenu help = null;

        for (int i = 0; i < menus.length; i++) {
            if (menus[i].getName().equals(MenuFactory.HELP_MENU))
                help = menus[i];
            else
                mb.add(menus[i]);
        }

        if (includeBar) {
            view.getToolsBar().setFloatable(false);
            mb.add(Box.createRigidArea(new Dimension(20, 10)));
            mb.add(view.getToolsBar());
        }

        // mb.setHelpMenu(help); Yields an exception, not yet implemented.

        if (help != null) {
            mb.add(help);
        }

        mb.add(Box.createHorizontalGlue());

        if (logo == null) {
            logo = Images.getImageIcon(Images.IMAGE_LOGO);
        }
        JLabel label = new JLabel(logo);
        label.setOpaque(false);
        /*label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(1,3,1,3)));
        label.setBackground(Color.WHITE);*/
        mb.add(label);

        return mb;
    }
    
    public static ImageIcon logo;
    
    public JComponent constructGlassPane() {
        if (logo == null) {
            logo = Images.getImageIcon(Images.IMAGE_LOGO);
        }

        JPanel gp = new JPanel();
        gp.setBorder(BorderFactory.createEtchedBorder());
        gp.setOpaque(false);
        gp.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.NORTHEAST;
        gc.weightx = 1f;
        gc.weighty = 1f;
        JLabel label = new JLabel(logo);
        label.setMaximumSize(label.getPreferredSize());
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        gp.add(label, gc);
        gp.setVisible(true);
        return gp;
    }
    
    protected void restoreSizeAndLocation(JFrame frame) {
        final Config config = ConfigurationManager.getConfiguration();
        int width = config.getInt(Settings.CONZILLA_VIEW_FRAME_WIDTH, 700);
        int height = config.getInt(Settings.CONZILLA_VIEW_FRAME_HEIGHT, 600);
        int posX = config.getInt(Settings.CONZILLA_VIEW_POSITION_X, 0);
        int posY = config.getInt(Settings.CONZILLA_VIEW_POSITION_Y, 0);
        
        frame.setSize(width, height);
        frame.setLocation(posX, posY);
    }
    
    protected void saveSizeAndLocation(JFrame frame) {
    	final Config config = ConfigurationManager.getConfiguration();
    	config.setProperty(Settings.CONZILLA_VIEW_FRAME_WIDTH, new Integer(frame.getWidth()));
    	config.setProperty(Settings.CONZILLA_VIEW_FRAME_HEIGHT, new Integer(frame.getHeight()));
    	config.setProperty(Settings.CONZILLA_VIEW_POSITION_X, new Integer(frame.getX()));
    	config.setProperty(Settings.CONZILLA_VIEW_POSITION_Y, new Integer(frame.getY()));
    }
    
    public boolean closeable() {
    	int viewCount = getViewCount();
    	if (viewCount > 1) {
    		int result = JOptionPane.showConfirmDialog(null,
    				"You are about to close " + viewCount + " maps. Are you sure you want to continue?",
    				"Confirm close",
    				JOptionPane.YES_NO_OPTION,
    				JOptionPane.QUESTION_MESSAGE);
    		if (result != JOptionPane.YES_OPTION) {
    			return false;
    		}
    	}
    	return true;
    }
    
    public void revalidate() {
    }
    
}