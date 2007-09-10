/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.bookmark;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Holds bookmark information.
 * 
 * @author Hannes Ebner
 * @version $Id$
 * @see BookmarkNode
 */
public class BookmarkInformation implements Transferable {
	
	/**
	 * The DataFlavor for Drag and Drop support.
	 */
	final public static DataFlavor INFO_FLAVOR = new DataFlavor(BookmarkInformation.class, "Bookmark Information");
	
	/* Type definitions */
	
	public static final int TYPE_UNDEFINED = 0;
	
	public static final int TYPE_CONTEXTMAP = 1;
	
	public static final int TYPE_CONCEPT = 2;
	
	public static final int TYPE_FOLDER = 101;
	
	public static final int TYPE_SEPARATOR = 102;
	
	/* Private values */
	
	private String uri;
	
	private String name;
	
	private int type;
	
	private String description;
	
	/* Constructors */
	
	public BookmarkInformation() {
		this.type = TYPE_UNDEFINED;
	}
	
	public BookmarkInformation(String uri, int type) {
		this();
		this.uri = uri;
		this.type = type;
	}
	
	public BookmarkInformation(String uri, String name, int type) {
		this(uri, type);
		this.name = name;
	}
	
	/* Overrides */
	
	public String toString() {
		if (name != null) {
			return name;
		} else {
			return uri;
		}
	}
	
	/* Getters/Setters */
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	/* Transferable */
	
	/**
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
	 */
	public boolean isDataFlavorSupported(DataFlavor df) {
		return df.equals(INFO_FLAVOR);
	}

	/**
	 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
	 */
	public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
		if (df.equals(INFO_FLAVOR)) {
			return this;
		} else {
			throw new UnsupportedFlavorException(df);
		}
	}

	/**
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor flavors[] = { INFO_FLAVOR };
		return flavors;
	}

}