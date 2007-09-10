/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.view;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

public class ConzillaSplitPane extends JPanel {
    JSplitPane rightSplitter;
    JSplitPane leftSplitter;
    
    PropertyChangeListener selectionl;
    JToolBar bar;
    JPanel locationField;
    JPanel top;
	private View view;

    public ConzillaSplitPane() {
        super();
        setLayout(new BorderLayout());
        top = new JPanel();
        top.setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        
        leftSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
        leftSplitter.setOneTouchExpandable(true);
        rightSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
        rightSplitter.setOneTouchExpandable(true);
        rightSplitter.setLeftComponent(leftSplitter);
        rightSplitter.setResizeWeight(1d);
        add(rightSplitter,BorderLayout.CENTER);
    }

    public void setToolBar(JToolBar newBar) {
        if (this.bar != null) {
            top.remove(this.bar);
        }
        this.bar = newBar;
        top.add(newBar,BorderLayout.NORTH);
        bar.repaint();
    }
    

    public void setLocationField(JPanel newLocationField) {
        if (this.locationField != null) {
            top.remove(this.locationField);
        }
        this.locationField = newLocationField;
        top.add(newLocationField,BorderLayout.SOUTH);
        locationField.repaint();
    }
    
    /**
     * This funtion sets the two panes of the splitpane.
     */
    public void setPanes(JComponent left, JComponent middle, final JComponent right,final View view) {
    	this.view = view;
    	
    	if (selectionl != null) {
        	view.getController().removePropertyChangeListener(selectionl);
        }

        selectionl = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                
                if (e.getPropertyName().equals(View.RIGHT_PANE_PROPERTY)) {
                	SwingUtilities.invokeLater(new Runnable() {
                		public void run() {
                			fixRightDividerLocation();
                		}
                    });
                }
                if (e.getPropertyName().equals(View.LEFT_PANE_PROPERTY)) {
                	SwingUtilities.invokeLater(new Runnable() {
                		public void run() {
                			fixLeftDividerLocation();
                		}
                    });
                }
            }
        };
        view.getController().addPropertyChangeListener(selectionl);

        leftSplitter.setLeftComponent(left);
        leftSplitter.setRightComponent(middle);
        rightSplitter.setRightComponent(right);
        fixDividerLocation();
    }

    public void fixDividerLocation() {
    	fixRightDividerLocation();
    	fixLeftDividerLocation();
    }
    
    public void fixRightDividerLocation() {
    	int rightDivididerLocation;
    	
		//Calculates and sets the new rightdividerLocation.
        if (!view.getRightPanel().isEmpty() && view.getRightPanel().getPreferredSize().width != 0)
            rightDivididerLocation = rightSplitter.getWidth()
                    - rightSplitter.getRightComponent().getPreferredSize().width
                    - rightSplitter.getDividerSize() - 5;
        else
            rightDivididerLocation = rightSplitter.getWidth();
        rightSplitter.setDividerLocation(rightDivididerLocation);
		rightSplitter.revalidate();
    }
    
    public void fixLeftDividerLocation() {
		//Calculates and sets the new leftdividerLocation.
		int leftDividerLocation;
        if (!view.getLeftPanel().isEmpty() && leftSplitter.getLeftComponent().getPreferredSize().width != 0) {
            leftDividerLocation = leftSplitter.getLeftComponent().getPreferredSize().width + 5;
            if (leftDividerLocation < 200) {
            	leftDividerLocation = 200;
            }
        } else {
            leftDividerLocation = 0;
        }
        leftSplitter.setDividerLocation(leftDividerLocation);
		leftSplitter.revalidate();
    }

    public void detach() {
    	if (selectionl != null) {
        	view.getController().removePropertyChangeListener(selectionl);
        }
    }
    
}