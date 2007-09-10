/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;
import java.awt.Component;

import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import se.kth.cid.tree.TreeTagNode;

public class RDFCellRenderer implements TreeCellRenderer
{
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
	return new JTextField(((TreeTagNode) value).getURI());
    }
}
