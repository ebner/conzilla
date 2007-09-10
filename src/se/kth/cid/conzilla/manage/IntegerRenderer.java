/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.manage;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class IntegerRenderer extends JLabel
                           implements TableCellRenderer {
	
    public IntegerRenderer(Color backColor) {
        setBackground(backColor);        
    }

    public Component getTableCellRendererComponent(
                            JTable table, Object object,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
    	Integer integer = ((Integer) object);
    	if (integer.intValue() > 0) {
        	setText(((Integer) object).toString());
        	if (column == 1) {
        		setOpaque(true); //MUST do this for background to show up.
        	}
        } else {
        	setText("");
        	setOpaque(false);
        }        
        return this;
    }
}
