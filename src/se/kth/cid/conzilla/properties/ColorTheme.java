/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.properties;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.util.Tracer;

/**
 * Class to handle various sets of colors, called color themes.<br>
 * Conzilla color themes are stored in the main configuration file.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class ColorTheme {

	/**
	 * Should be used for property change listeners to listen on. Contains the
	 * identifier of the currently used color theme.
	 */
	public static final String COLORTHEME = "conzilla.colortheme";

	public static final String NAME_KEY = "name";

	public static final String DEFAULT_THEME_ID = "default";

	private static final String AVAILABLE_THEMES_KEY = COLORTHEME + ".theme";

	private static final String THEME_DEFINITIONS_KEY = COLORTHEME + ".theme-definitions";

	/**
	 * String constants that should be used to access the colors in this class.
	 * <p>
	 * Sample usage:<br>
	 * 
	 * <pre>
	 * Color newColor = ColorTheme.getColor(ColorTheme.Colors.FOREGROUND);
	 * </pre>
	 * 
	 * @author Hannes Ebner
	 */
	public class Colors {

		public static final String FOREGROUND = "foreground";

		public static final String MAP_BACKGROUND = "map-background";

		public static final String CONCEPT_BACKGROUND = "concept-background";

		public static final String CONCEPT_FOCUS = "concept-focus";

		public static final String CONTENT = "content";

		public static final String CONTEXT = "context";

		public static final String CONTEXTANDCONTENT = "context-and-content";

		public static final String INFORMATION = "information";

		private Colors() {
		}

	}

	/* Private */

	/**
	 * Holds the name of the currently used theme.
	 */
	private static String currentTheme;

	static {
		currentTheme = ConfigurationManager.getConfiguration().getString(COLORTHEME, DEFAULT_THEME_ID);
	}

	/**
	 * This class is accessed in a static way.
	 */
	private ColorTheme() {
	}

	/* Public */

	/**
	 * @return Returns the identifiers of all available color themes.
	 */
	public static List getColorThemeIDs() {
		return ConfigurationManager.getConfiguration().getStringList(AVAILABLE_THEMES_KEY);
	}

	/**
	 * @return Returns the names (titles) of all available color themes.
	 */
	public static List getColorThemeNames() {
		List result = new ArrayList();
		List themeIds = getColorThemeIDs();
		Iterator themeIterator = themeIds.iterator();
		while (themeIterator.hasNext()) {
			result.add(getColorThemeName((String) themeIterator.next()));
		}
		return result;
	}

	/**
	 * @param identifier
	 *            Identifier of the color theme.
	 * @return Returns the name (title) of a color theme identified by the
	 *         parameter.
	 */
	public static String getColorThemeName(String identifier) {
		return ConfigurationManager.getConfiguration().getString(
				THEME_DEFINITIONS_KEY + "." + identifier + "." + NAME_KEY);
	}

	/**
	 * @return Returns the identifier of the currently used color theme.
	 */
	public static String getCurrentColorThemeID() {
		return currentTheme;
	}

	/**
	 * Sets the currently used color theme.
	 * 
	 * @param themeID
	 *            Identifier of the color theme which is to be used.
	 */
	public static void setColorTheme(String themeID) {
		if (currentTheme == themeID) {
			// nothing changed, nothing to do
			return;
		}
		if (!getColorThemeIDs().contains(themeID)) {
			throw new IllegalArgumentException("Theme \"" + themeID + "\" is not defined");
		}
		currentTheme = themeID;
		// setProperty also fires an event on the key COLORTHEME
		ConfigurationManager.getConfiguration().setProperty(COLORTHEME, currentTheme);
	}

	public static boolean colorExists(String colorKey) {
		Color result = ConfigurationManager.getConfiguration().getColor(
				THEME_DEFINITIONS_KEY + "." + currentTheme + "." + colorKey);
		return (result != null);
	}

	/**
	 * Returns a Color object, depending on the color key supplied as parameter.
	 * 
	 * @param colorKey
	 *            A color key value, defined in the class ColorTheme.Colors.
	 * @return A Color object, may return Color.BLACK if the key does not exist.
	 */
	public static Color getColor(String colorKey) {
		Color result = ConfigurationManager.getConfiguration().getColor(
				THEME_DEFINITIONS_KEY + "." + currentTheme + "." + colorKey);
		if (result == null) {
			result = Color.BLACK;
			Tracer.debug("Color theme \"" + currentTheme + "\" not complete, unable to find key \"" + COLORTHEME + "."
					+ currentTheme + "." + colorKey + "\", setting color to black instead");
		}
		return result;
	}

	/**
	 * Returns a Color object with a translucent color.
	 * 
	 * @param colorKey
	 *            A color key value, defined in the class ColorTheme.Colors.
	 * @return A translucent Color object.
	 */
	public static Color getTranslucentColor(String colorKey) {
		return getTranslucentColor(getColor(colorKey));
	}

	/**
	 * Returns a Color object with a translucent color.
	 * 
	 * @param col
	 *            A valid Color object to be made translucent.
	 * @return A translucent Color object.
	 */
	public static Color getTranslucentColor(Color col) {
		int red = col.getRed();
		int green = col.getGreen();
		int blue = col.getBlue();
		return new Color(red, green, blue, 200);
	}

	/**
	 * Returns a Color object with a lighter color than the supplied parameter.
	 * 
	 * @param colorKey
	 *            A color key value, defined in the class ColorTheme.Colors.
	 * @return A lighter Color object.
	 */
	public static Color getBrighterColor(String colorKey) {
		String brightKey = colorKey + ".brighter";
		if (colorExists(brightKey)) {
			return getColor(brightKey);
		} else {
			return getBrighterColor(getColor(colorKey));
		}
	}

	/**
	 * Returns a Color object with a lighter color than the supplied parameter.
	 * 
	 * @param col
	 *            A valid Color object to be made lighter.
	 * @return A lighter Color object.
	 */
	public static Color getBrighterColor(Color col) {
		int red = col.getRed();
		int green = col.getGreen();
		int blue = col.getBlue();
		float[] hsb = new float[3];
		Color.RGBtoHSB(red, green, blue, hsb);
		return Color.getHSBColor(hsb[0], hsb[1] * 0.3f, (1 - hsb[2]) * 0.7f + hsb[2]);
	}

}