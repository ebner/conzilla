/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.bookmark;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Node for the BookmarkTree. The node holds BookmarkInformation as user object.
 * 
 * @author Hannes Ebner
 * @version $Id$
 * @see BookmarkInformation
 */
public class BookmarkNode extends DefaultMutableTreeNode {

	// We need an empty constructor for XMLEncoder
	public BookmarkNode() {
		userObject = new BookmarkInformation();
	}

	/**
	 * Initializes the node with a user object.
	 * 
	 * @param info
	 *            BookmarkInformation object.
	 */
	public BookmarkNode(BookmarkInformation info) {
		super(info);
	}

	/**
	 * @see javax.swing.tree.DefaultMutableTreeNode#isLeaf()
	 */
	public boolean isLeaf() {
		return !(((BookmarkInformation) getUserObject()).getType() == BookmarkInformation.TYPE_FOLDER);
	}

	/**
	 * @see javax.swing.tree.DefaultMutableTreeNode#getAllowsChildren()
	 */
	public boolean getAllowsChildren() {
		return (((BookmarkInformation) getUserObject()).getType() == BookmarkInformation.TYPE_FOLDER);
	}

	/**
	 * @return Returns tooltip text built out of the information in the user
	 *         object.
	 */
	public String getToolTipText() {
		BookmarkInformation info = (BookmarkInformation) getUserObject();
		if (info.getName() == null) {
			return null;
		}

		if (info.getType() == BookmarkInformation.TYPE_FOLDER) {
			if ((info.getDescription() == null) || (info.getDescription().trim().length() < 1)) {
				return null;
			}
		}

		String tooltip = new String();
		tooltip += "<html>";
		tooltip += "<b>" + info.getName() + "</b><br>";
		if ((info.getUri() != null) && (info.getUri().trim().length() > 0)) {
			tooltip += "<br><b>URI:</b> " + info.getUri();
		}
		if ((info.getDescription() != null) && (info.getDescription().trim().length() > 0)) {
			tooltip += "<br><b>Description:</b> " + info.getDescription();
		}
		tooltip += "</html>";

		return tooltip;
	}
	
	/**
	 * @return Returns the casted getUserObject.
	 */
	public BookmarkInformation getBookmarkInformation() {
		return (BookmarkInformation) getUserObject();
	}

}