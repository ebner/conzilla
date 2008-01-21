/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.properties;

import java.awt.Color;
import java.net.URL;

import javax.swing.ImageIcon;

import se.kth.cid.conzilla.util.TransparencyImageFilter;

/**
 * Helps managing the images used within Conzilla. The locations of the images
 * are defined in String constants and can be accessed via static methods.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class Images {
	
	/* ICONS */

	public final static String ICON_BOOKMARK_ENVIRONMENT = "/graphics/BookmarkKit/BookmarkEnvironment.gif";

	public final static String ICON_BOOKMARK_ENVIRONMENT_SELECTED = "/graphics/BookmarkKit/BookmarkEnvironmentSelected.gif";

	public final static String ICON_BOOKMARK_CONTEXTMAP = "/graphics/BookmarkKit/ContextMapBookmark.gif";

	public final static String ICON_BOOKMARK_CONTEXTMAP_SELECTED = "/graphics/BookmarkKit/ContextMapBookmarkSelected.gif";
	
	public final static String ICON_BOOKMARK = "/graphics/menu/Bookmark16.png";
	
	public final static String ICON_BOOKMARK_FOLDER_CLOSED = "/graphics/menu/BookmarkFolderClosed16.png";
	
	public final static String ICON_BOOKMARK_FOLDER_OPEN = "/graphics/menu/BookmarkFolderOpen16.png";
	
	public final static String ICON_CONTAINER_MANAGER = "/graphics/menu/Containers16.png";

	public final static String ICON_CONTEXTUALIZE = "/graphics/toolbarButtonGraphics/general/Contextualize16.png";

	public final static String ICON_CONTENT = "/graphics/toolbarButtonGraphics/general/ContentAttached16.png";
	
	public final static String ICON_CONTEXT_MAP = "/graphics/menu/ContextMap16.png";
	
	public final static String ICON_CONTRIBUTION = "/graphics/menu/Contribution16.png";
	
	public final static String ICON_CONTRIBUTIONS = "/graphics/menu/Contributions16.png";
	
	public final static String ICON_CONZILLA_16 = "/graphics/logo/conzilla16.png";
	
	public final static String ICON_CONZILLA_32 = "/graphics/logo/conzilla32.png";
	
	public final static String ICON_CONZILLA_64 = "/graphics/logo/conzilla64.png";
	
	public final static String ICON_CONZILLA_128 = "/graphics/logo/conzilla128.png";
	
	public final static String ICON_EDIT_FORM = "/graphics/toolbarButtonGraphics/general/EditForm16.png";
	
	public final static String ICON_EDIT_PAGE = "/graphics/toolbarButtonGraphics/general/EditForm16.png";
	
	public final static String ICON_EXIT = "/graphics/menu/Exit16.png";
	
	public final static String ICON_FILE_BROWSE = "/graphics/menu/FileBrowse16.png";
	
	public final static String ICON_FILE_CLOSE = "/graphics/menu/FileClose16.png";
	
	public final static String ICON_FILE_EDIT = "/graphics/menu/FileEdit16.png";
	
	public final static String ICON_FILE_EXPORT = "/graphics/menu/FileExport16.png";
	
	public final static String ICON_FILE_NEW = "/graphics/menu/FileNew16.png";
	
	public final static String ICON_FILE_OPEN = "/graphics/menu/FileOpen16.png";
	
	public final static String ICON_FILE_PRINT = "/graphics/menu/FilePrint16.png";

	public final static String ICON_FULLSCREEN = "/graphics/toolbarButtonGraphics/general/FullScreen16.png";

	public final static String ICON_GRID = "/graphics/edit/Grid16.gif";

	public final static String ICON_HANDLE = "/graphics/edit/Handle16.gif";

	public final static String ICON_HOME = "/graphics/toolbarButtonGraphics/navigation/Home16.png";

	public final static String ICON_INFORMATION = "/graphics/toolbarButtonGraphics/general/Information16.png";
	
	public final static String ICON_LAYERS = "/graphics/menu/Layers16.png";

	public final static String ICON_LINK = "/graphics/browse/Link16.png";
	
	public final static String ICON_MAP_PUBLISHED = "/graphics/menu/MapPublished16.png";
	
	public final static String ICON_MAP_PUBLISHED_GREY = "/graphics/menu/MapPublishedGrey16.png";

	public final static String ICON_NAVIGATION_BACK = "/graphics/toolbarButtonGraphics/navigation/Back16.png";

	public final static String ICON_NAVIGATION_FORWARD = "/graphics/toolbarButtonGraphics/navigation/Forward16.png";
	
	public final static String ICON_NEW_WINDOW = "/graphics/toolbarButtonGraphics/general/NewWindow16.png";

	public final static String ICON_ONLINESTATE = "/graphics/browse/Offline16.png";

	public final static String ICON_POPUP = "/graphics/toolbarButtonGraphics/general/Popup16.png";

	public final static String ICON_PUBLISH = "/graphics/toolbarButtonGraphics/general/Publish16.png";
	
	public final static String ICON_REDO = "/graphics/edit/Redo16.png";
	
	public final static String ICON_REFRESH = "/graphics/browse/Refresh16.png";
	
	public final static String ICON_RESOLVER_TABLE = "/graphics/menu/ResolverTable16.png";

	public final static String ICON_SAVE = "/graphics/toolbarButtonGraphics/general/Save16.png";
	
	public final static String ICON_SESSIONS_BROWSE = "/graphics/menu/BrowseSessions16.png";
	
	public final static String ICON_SESSION_OPEN = "/graphics/menu/FolderPage16.png";
	
	public final static String ICON_SESSION_CLOSED = "/graphics/menu/Folder16.png";
	
	public final static String ICON_SETTINGS_COLLABORATION = "/graphics/menu/CollaborationSettings16.png";
	
	public final static String ICON_SETTINGS_COLLABORATION_IMPORT = "/graphics/menu/FolderStar16.png";
	
	public final static String ICON_SETTINGS_COLOR_THEMES = "/graphics/menu/ColorThemes16.png";
	
	public final static String ICON_SETTINGS_FONT_SIZE = "/graphics/menu/FontSize16.png";
	
	public final static String ICON_SETTINGS_PERSONAL_INFO = "/graphics/menu/PersonalInfo16.png";

	public final static String ICON_TIE = "/graphics/edit/Tie16.gif";
	
	public final static String ICON_UNDO = "/graphics/edit/Undo16.png";
	
	public final static String ICON_ZOOM = "/graphics/menu/Zoom16.png";

	public final static String ICON_ZOOM_IN = "/graphics/toolbarButtonGraphics/general/ZoomIn16.png";

	public final static String ICON_ZOOM_OUT = "/graphics/toolbarButtonGraphics/general/ZoomOut16.png";

	/* IMAGES */

	public final static String IMAGE_LOGO_ABOUT = "/graphics/logo/conzilla-logo-about.png";

	public final static String IMAGE_LOGO = "/graphics/logo/conzilla-logo.png";

	private Images() {
	}

	/**
	 * Reads an image out of the application resources and returns it as an
	 * ImageIcon.
	 * 
	 * @param icon
	 *            Location of the to be loaded ImageIcon. Should be a constant
	 *            (ICON_* and IMAGE_*) defined in this class.
	 * @return Returns an ImageIcon object.
	 */
	public static ImageIcon getImageIcon(String icon) {
		URL resource = Images.class.getResource(icon);
		if (resource == null) {
			throw new IllegalArgumentException("Requested icon could not be found.");
		}
		return new ImageIcon(resource);
	}

	/**
	 * Adds a background color to an ImageIcon.
	 * 
	 * @param icon
	 *            ImageIcon.
	 * @param bgColor
	 *            Color.
	 * @return New ImageIcon with a background color.
	 */
	public static ImageIcon addBackgroundColor(ImageIcon icon, Color bgColor) {
		return new ImageIcon(TransparencyImageFilter.createFilteredImage(icon.getImage(), bgColor));
	}

}