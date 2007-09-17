/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.config;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * Methods to handle a configuration. Basically a wrapping interface around
 * already existing configuration solutions. The main purpose is to make format
 * independent implementations possible, and to be able to switch the
 * configuration backend at a later point.
 * 
 * To get a synchronized view of a Config object, the static method
 * Configurations.synchronizedConfig can be called (similar to the Collections
 * framework).
 * 
 * <p>
 * Property = Key + Value(s)<br>
 * Key = Distinct name of a configuration setting<br>
 * <p>
 * A key may contain dots to indicate a hierarchical separation.
 * 
 * @author Hannes Ebner
 * @version $Id$
 * @see Configurations
 */
public interface Config {

	/* Global operations */

	/**
	 * Clears the whole configuration.
	 */
	public void clear();

	/**
	 * @return True if the configuration is empty.
	 */
	public boolean isEmpty();

	/**
	 * @return True if the configuration has been modified since the last time
	 *         it was saved.
	 */
	public boolean isModified();

	/**
	 * Saves the configuration at a given location.
	 * 
	 * @param configURL
	 *            URL of the location. Right now only local locations are
	 *            supported.
	 * @throws ConfigurationException
	 */
	public void save(URL configURL) throws IOException;

	/**
	 * Loads a configuration from a given location.
	 * 
	 * @param configURL
	 *            URL of the location. Right now only local locations are
	 *            supported.
	 * @throws ConfigurationException
	 */
	public void load(URL configURL) throws IOException;
	
	/* Property Change Listeners */
	
	/**
	 * Adds a PropertyChangeListener to the configuration.
	 * 
	 * @param listener PropertyChangeListener.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);
	
	/**
	 * Adds a PropertyChangeListener to the configuration.
	 * 
	 * @param key Property key.
	 * @param listener PropertyChangeListener.
	 */
	public void addPropertyChangeListener(String key, PropertyChangeListener listener);
	
	/**
	 * Adds a PropertyChangeListener from the configuration.
	 * 
	 * @param listener PropertyChangeListener.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);
	
	/**
	 * Adds a PropertyChangeListener from the configuration.
	 * 
	 * @param key Property key.
	 * @param listener PropertyChangeListener.
	 */
	public void removePropertyChangeListener(String key, PropertyChangeListener listener);

	/* Properties */

	/**
	 * Clears all values of a given key.
	 * 
	 * @param key
	 *            Property key.
	 */
	public void clearProperty(String key);

	/**
	 * Adds a property (key/value mapping) to the configuration. If the property
	 * already exists an additional value is added.
	 * 
	 * @param key
	 *            Property key.
	 * @param value
	 *            Value as Object. E.g. an int is added as new Integer(int).
	 */
	public void addProperty(String key, Object value);

	/**
	 * Adds a List of values to a property. See also addProperty(String,
	 * Object).
	 * 
	 * @param key
	 *            Property key.
	 * @param values
	 *            List of objects.
	 */
	public void addProperties(String key, List values);
	
	/**
	 * Adds a List of values to a property. See also addProperty(String,
	 * Object).
	 * 
	 * @param key
	 *            Property key.
	 * @param values
	 *            Iterator (e.g. from a List).
	 */
	public void addProperties(String key, Iterator values);

	/**
	 * Sets the value of a property. Already existing values are overwritten.
	 * 
	 * @param key
	 *            Property key.
	 * @param value
	 *            Value as Object. E.g. an int is set as new Integer(int).
	 */
	public void setProperty(String key, Object value);

	/**
	 * Sets a List of values of a property. See also setProperty(String,
	 * Object).
	 * 
	 * @param key
	 *            Property key.
	 * @param values
	 *            List of objects.
	 */
	public void setProperties(String key, List values);
	
	/**
	 * Sets a List of values of a property. See also setProperty(String,
	 * Object).
	 * 
	 * @param key
	 *            Property key.
	 * @param values
	 *            Iterator (e.g. from a List).
	 */
	public void setProperties(String key, Iterator values);

	/* Keys */

	/**
	 * Checks whether the configuration contains a given key.
	 * 
	 * @param key
	 *            Property key.
	 * @return True if the configuration contains the given key.
	 */
	public boolean containsKey(String key);

	/**
	 * Returns a list of all set configuration keys.
	 * 
	 * @return List of keys as string values.
	 */
	public List getKeyList();

	/**
	 * Returns a list of all set configuration keys under a given key.
	 * 
	 * @param prefix
	 *            Key prefix to set the point to start from in the configuration
	 *            tree.
	 * @return List of keys as string values.
	 */
	public List getKeyList(String prefix);

	/* Get Values */

	/**
	 * @param key
	 *            Property key.
	 * @return Returns a property value as String, or null if the property is not found.
	 */
	public String getString(String key);

	/**
	 * @param key
	 *            Property key.
	 * @param defaultValue
	 *            Default value if the given property does not exist.
	 * @return Returns a property value as String.
	 */
	public String getString(String key, String defaultValue);

	/**
	 * @param key
	 *            Property key.
	 * @return Returns property values as a List of strings.
	 */
	public List getStringList(String key);

	/**
	 * @param key
	 *            Property key.
	 * @param defaultValues
	 *            Default values if the given property does not exist.
	 * @return Returns a property value as a List of strings.
	 */
	public List getStringList(String key, List defaultValues);

	/**
	 * @param key
	 *            Property key.
	 * @return Returns a property value as boolean.
	 */
	public boolean getBoolean(String key);

	/**
	 * @param key
	 *            Property key.
	 * @param defaultValue
	 *            Default value if the given property does not exist.
	 * @return Returns a property value as boolean.
	 */
	public boolean getBoolean(String key, boolean defaultValue);

	/**
	 * @param key
	 *            Property key.
	 * @return Returns a property value as byte.
	 */
	public byte getByte(String key);

	/**
	 * @param key
	 *            Property key.
	 * @param defaultValue
	 *            Default value if the given property does not exist.
	 * @return Returns a property value as byte.
	 */
	public byte getByte(String key, byte defaultValue);

	/**
	 * @param key
	 *            Property key.
	 * @return Returns a property value as double.
	 */
	public double getDouble(String key);

	/**
	 * @param key
	 *            Property key.
	 * @param defaultValue
	 *            Default value if the given property does not exist.
	 * @return Returns a property value as double.
	 */
	public double getDouble(String key, double defaultValue);

	/**
	 * @param key
	 *            Property key.
	 * @return Returns a property value as float.
	 */
	public float getFloat(String key);

	/**
	 * @param key
	 *            Property key.
	 * @param defaultValue
	 *            Default value if the given property does not exist.
	 * @return Returns a property value as float.
	 */
	public float getFloat(String key, float defaultValue);

	/**
	 * @param key
	 *            Property key.
	 * @return Returns a property value as int.
	 */
	public int getInt(String key);

	/**
	 * @param key
	 *            Property key.
	 * @param defaultValue
	 *            Default value if the given property does not exist.
	 * @return Returns a property value as int.
	 */
	public int getInt(String key, int defaultValue);

	/**
	 * @param key
	 *            Property key.
	 * @return Returns a property value as long.
	 */
	public long getLong(String key);

	/**
	 * @param key
	 *            Property key.
	 * @param defaultValue
	 *            Default value if the given property does not exist.
	 * @return Returns a property value as long.
	 */
	public long getLong(String key, long defaultValue);

	/**
	 * @param key
	 *            Property key.
	 * @return Returns a property value as short.
	 */
	public short getShort(String key);

	/**
	 * @param key
	 *            Property key.
	 * @param defaultValue
	 *            Default value if the given property does not exist.
	 * @return Returns a property value as short.
	 */
	public short getShort(String key, short defaultValue);

	/**
	 * @param key
	 *            Property key.
	 * @return Returns a property value as URI.
	 */
	public URI getURI(String key);

	/**
	 * @param key
	 *            Property key.
	 * @param defaultValue
	 *            Default value if the given property does not exist.
	 * @return Returns a property value as URI.
	 */
	public URI getURI(String key, URI defaultValue);
	
	/**
	 * @param key
	 *            Property key.
	 * @return Returns a property value as Color.
	 */
	public Color getColor(String key);

	/**
	 * @param key
	 *            Property key.
	 * @param defaultValue
	 *            Default value if the given property does not exist.
	 * @return Returns a property value as Color.
	 */
	public Color getColor(String key, Color defaultValue);

}