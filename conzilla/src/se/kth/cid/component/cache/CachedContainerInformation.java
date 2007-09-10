/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component.cache;

import java.io.Serializable;
import java.util.Date;

/**
 * Helper class to be used as counterpart to keys in a Map. Contains information
 * on cached containers.
 * <p>
 * This class follows Java Bean standards (constructor without parameter,
 * getters/setters), which makes XML serialization via XMLEncoder/XMLDecoder
 * possible.
 * 
 * @author Hannes Ebner
 * @version $Id$
 * @see se.kth.cid.component.cache.DiskContainerCache
 */
public class CachedContainerInformation implements Serializable {

	// Not needed if deserialized via XMLDecoder
	private static final long serialVersionUID = -1890582140408083449L;

	private String fileName;

	private long localFileSize;

	private Date lastModificationDate;

	private Date cachedDate;

	/**
	 * Constructor without arguments. Needed for Java Beans compatibility, if we
	 * want to serialize this using XMLEncoder.
	 */
	public CachedContainerInformation() {
	}

	/**
	 * @return File name of the cached container.
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * @param cachedFileName
	 *            File name of the cached container.
	 */
	public void setFileName(String cachedFileName) {
		this.fileName = cachedFileName;
	}

	/**
	 * Returns the file size of the cached file. Since the cached file might be
	 * compressed, this value can be smaller than the actual size of the
	 * uncompressed container file.
	 * 
	 * @return File size of the cached file.
	 */
	public long getLocalFileSize() {
		return this.localFileSize;
	}

	/**
	 * @param cachedFileSize
	 *            File size of the cached file.
	 */
	public void setLocalFileSize(long cachedFileSize) {
		this.localFileSize = cachedFileSize;
	}

	/**
	 * Returns the date of the last modification. This value is typically
	 * retrieved from the remote location.
	 * 
	 * @return Date of the last modification.
	 */
	public Date getLastModificationDate() {
		return this.lastModificationDate;
	}

	/**
	 * @param lastModificationDate
	 *            Date of the last modification.
	 */
	public void setLastModificationDate(Date lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	/**
	 * Returns the date and time when the container was cached.
	 * 
	 * @return Date of caching.
	 */
	public Date getCachedDate() {
		return cachedDate;
	}

	/**
	 * @param cachedDate
	 *            Date of caching.
	 */
	public void setCachedDate(Date cachedDate) {
		this.cachedDate = cachedDate;
	}

}