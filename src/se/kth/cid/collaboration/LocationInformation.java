/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.collaboration;

/**
 * Holds publishing location information.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class LocationInformation {

	/**
	 * Location title.
	 */
	private String title;

	/**
	 * Location description.
	 */
	private String description;

	/**
	 * Location to publish to.
	 */
	private String publishingLocation;

	/**
	 * Location to read from. (Public access.)
	 */
	private String accessLocation;

	/**
	 * Initializes the values of the object with an empty String.
	 */
	public LocationInformation() {
		this.title = new String();
		this.description = new String();
		this.publishingLocation = new String();
		this.accessLocation = new String();
	}

	/**
	 * Initializes the object with predefined values.
	 * 
	 * @param title
	 *            Location title.
	 * @param description
	 *            Location description.
	 * @param publishinglocation
	 *            Location to publish to.
	 * @param accessLocation
	 *            Location to read from. (Public access.)
	 */
	public LocationInformation(String title, String description, String publishinglocation, String accessLocation) {
		this.title = title;
		this.description = description;
		this.publishingLocation = publishinglocation.trim();
		this.accessLocation = accessLocation.trim();
	}

	/**
	 * @return Title of the location.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            Title of the location.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return Description of the location.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            Description of the location.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Location to publish to.
	 */
	public String getPublishingLocation() {
		return publishingLocation.trim();
	}

	/**
	 * @param location
	 *            Location to publish to.
	 */
	public void setPublishingLocation(String location) {
		this.publishingLocation = location.trim();
	}

	/**
	 * @return Location to read from. (Public access.)
	 */
	public String getPublicAccessLocation() {
		return accessLocation.trim();
	}

	/**
	 * @param location
	 *            Location to read from. (Public access.)
	 */
	public void setPublicAccessLocation(String location) {
		this.accessLocation = location.trim();
	}

	/**
	 * Returns the title of the location. This override is necessary for the use
	 * with list models in Swing.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return title;
	}
	
	/**
	 * Constructs a tooltip out of the available information in this object.
	 * 
	 * @return Tooltip as String.
	 */
	public String getToolTip() {
    	StringBuffer tooltip = new StringBuffer();
    	
    	tooltip.append("<html>");
    	if (description != null) {
    		tooltip.append("<b>Description</b><br>" + description + "<br><br>");
    	}
    	if (publishingLocation != null) {
    		tooltip.append("<b>Publishing Location</b><br>" + publishingLocation + "<br><br>");
    	}
    	if (accessLocation != null) {
    		tooltip.append("<b>Public Access Location</b><br>" + accessLocation);
    	}
    	tooltip.append("</html>");
    	
		return tooltip.toString();
	}

}