/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.config;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Wrapper around Java's Properties.
 * Some methods have been simplified, others just wrapped.<br>
 * 
 * <p>
 * See the static methods of the class Configurations for wrappers around the
 * Config interface, e.g. to get synchronized view of the object.
 * 
 * <p>
 * If a key maps only to one value, it is done the standard way:<br>
 * <pre>key=value</pre>
 * 
 * <p>
 * If a key maps to multiple values, the key is numbered:<br>
 * <pre>key.1=value1</pre>
 * <pre>key.2=value2</pre>
 * 
 * @author Hannes Ebner
 * @version $Id$
 * @see Configurations
 * @see Config
 */
public class PropertiesConfiguration implements Config {

	/**
	 * The main resource in this object. Contains the configuration.
	 */
	private SortedProperties config;
	
	private PropertyChangeSupport pcs;
	
	private String configName;

	private boolean modified = false;

	/* Constructors */

	/**
	 * Initializes the object with an empty Configuration.
	 * 
	 * @param configName
	 *            Name of the configuration (appears as comment in the
	 *            configuration file).
	 */
	public PropertiesConfiguration(String configName) {
		this.configName = configName;
		config = new SortedProperties();
		pcs = new PropertyChangeSupport(this);
	}

	/* Generic helpers */

	/**
	 * Sets the modified status of this configuration.
	 * 
	 * @param modified
	 *            Status.
	 */
	private void setModified(boolean modified) {
		this.modified = modified;
	}
	
	private void checkFirePropertyChange(String key, Object oldValue, Object newValue) {
		if ((oldValue == null) && (newValue != null)) {
			pcs.firePropertyChange(key, oldValue, newValue);
		} else if ((oldValue != null) && (!oldValue.equals(newValue))) {
			pcs.firePropertyChange(key, oldValue, newValue);
		}
	}
	
	/* 
	 * List helpers
	 */
	
	private String numberedKey(String key, int number) {
		return key + "." + number;
	}
	
	private int getPropertyValueCount(String key) {
		int valueCount = 0;
		if (config.containsKey(key)) {
			valueCount = 1;
		} else {
			while (config.containsKey(numberedKey(key, valueCount + 1))) {
				valueCount++;
			}
		}
		return valueCount;
	}
	
	private synchronized void addPropertyValue(String key, Object value) {
		int valueCount = getPropertyValueCount(key);
		if ((valueCount == 1) && config.containsKey(key)) {
			String oldValue = config.getProperty(key);
			config.remove(key);
			config.setProperty(numberedKey(key, 1), oldValue);
			config.setProperty(numberedKey(key, 2), value.toString());
		} else if (valueCount > 1){
			config.setProperty(numberedKey(key, valueCount + 1), value.toString());
		} else if (valueCount == 0) {
			config.setProperty(key, value.toString());
		}
	}
	
	private void addPropertyValues(String key, List values) {
		addPropertyValues(key, values.iterator());
	}
	
	private synchronized void addPropertyValues(String key, Iterator it) {
		while (it.hasNext()) {
			addPropertyValue(key, it.next());
		}
	}
	
	private synchronized List getPropertyValues(String key) {
		int valueCount = getPropertyValueCount(key);
		List result = new ArrayList();
		if (valueCount == 1) {
			String value = config.getProperty(key);
			if (value == null) {
				value = config.getProperty(numberedKey(key, 1));
			}
			if (value != null) {
				result.add(value);
			}
		} else {
			for (int i = 1; i <= valueCount; i++) {
				result.add(config.getProperty(numberedKey(key, i)));
			}
		}
		return result;
	}
	
	private synchronized void clearPropertyValues(String key) {
		int valueCount = getPropertyValueCount(key);
		if (valueCount > 1) {
			for (int i = 1; i <= valueCount; i++) {
				config.remove(numberedKey(key, i));
			}
		}
		config.remove(key);
	}
	
	private void setPropertyValues(String key, List values) {
		setPropertyValues(key, values.iterator());
	}
	
	private synchronized void setPropertyValues(String key, Iterator it) {
		clearPropertyValues(key);
		addPropertyValues(key, it);
	}
	
	/*
	 * Interface implementation
	 */

	/* Generic */

	/**
	 * @see se.kth.cid.config.Config#clear()
	 */
	public void clear() {
		config.clear();
		setModified(true);
	}

	/**
	 * @see se.kth.cid.config.Config#isEmpty()
	 */
	public boolean isEmpty() {
		return config.isEmpty();
	}

	/**
	 * @see se.kth.cid.config.Config#isModified()
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * @see se.kth.cid.config.Config#load(java.net.URL)
	 */
	public void load(URL configURL) throws IOException {
		try {
			URI url = new URI(configURL.toString());
			File file = new File(url);
			config.load(new FileInputStream(file));
		} catch (URISyntaxException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * @see se.kth.cid.config.Config#save(java.net.URL)
	 */
	public void save(URL configURL) throws IOException {
		try {
			URI url = new URI(configURL.toString());
			File file = new File(url);
			config.store(new FileOutputStream(file), configName);
		} catch (URISyntaxException e) {
			throw new IOException(e.getMessage());
		}
		setModified(false);
	}
	
	/* Property Change Listeners */
	
	/**
	 * @see se.kth.cid.config.Config#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	/**
	 * @see se.kth.cid.config.Config#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(String key, PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(key, listener);
	}
	
	/**
	 * @see se.kth.cid.config.Config#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
	
	/**
	 * @see se.kth.cid.config.Config#removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String key, PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(key, listener);
	}

	/* Properties / Set Values */

	/**
	 * @see se.kth.cid.config.Config#clearProperty(java.lang.String)
	 */
	public void clearProperty(String key) {
		int valueCount = getPropertyValueCount(key);
		Object oldValue = null;
		if (valueCount == 0) {
			return;
		} else if (valueCount == 1) {
			oldValue = getString(key);
		} else if (valueCount > 1) {
			oldValue = getStringList(key);
		}
		clearPropertyValues(key);
		setModified(true);
		checkFirePropertyChange(key, oldValue, null);
	}

	/**
	 * @see se.kth.cid.config.Config#addProperty(java.lang.String,
	 *      java.lang.Object)
	 */
	public void addProperty(String key, Object value) {
		addPropertyValue(key, value);
		setModified(true);
		pcs.firePropertyChange(key, null, value);
	}

	/**
	 * @see se.kth.cid.config.Config#addProperties(java.lang.String,
	 *      java.util.List)
	 */
	public void addProperties(String key, List values) {
		addPropertyValues(key, values);
		setModified(true);
		pcs.firePropertyChange(key, null, values);
	}
	
	/**
	 * @see se.kth.cid.config.Config#addProperties(java.lang.String, java.util.Iterator)
	 */
	public void addProperties(String key, Iterator values) {
		addPropertyValues(key, values);
		setModified(true);
		pcs.firePropertyChange(key, null, values);
	}

	/**
	 * @see se.kth.cid.config.Config#setProperty(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setProperty(String key, Object value) {
		String oldValue = null;
		oldValue = getString(key);
		config.setProperty(key, value.toString());
		setModified(true);
		checkFirePropertyChange(key, oldValue, value);
	}

	/**
	 * @see se.kth.cid.config.Config#setProperties(java.lang.String,
	 *      java.util.List)
	 */
	public void setProperties(String key, List values) {
		List oldValues = getStringList(key);
		setPropertyValues(key, values);
		setModified(true);
		checkFirePropertyChange(key, oldValues, values);
	}
	
	/**
	 * @see se.kth.cid.config.Config#setProperties(java.lang.String, java.util.Iterator)
	 */
	public void setProperties(String key, Iterator values) {
		List oldValues = getStringList(key);
		setPropertyValues(key, values);
		setModified(true);
		checkFirePropertyChange(key, oldValues, values);
	}

	/* Keys */

	/**
	 * @see se.kth.cid.config.Config#containsKey(java.lang.String)
	 */
	public boolean containsKey(String key) {
		return config.containsKey(key);
	}

	/**
	 * @see se.kth.cid.config.Config#getKeyList()
	 */
	public List getKeyList() {
		return getKeyList(null);
	}

	/**
	 * @see se.kth.cid.config.Config#getKeyList(java.lang.String)
	 */
	public List getKeyList(String prefix) {
		Enumeration keyIterator = config.propertyNames();
		ArrayList result = new ArrayList();
		
		while (keyIterator.hasMoreElements()) {
			Object next = keyIterator.nextElement();
			if ((prefix != null) && !((String) next).startsWith(prefix)) {
				continue;
			}
			result.add(next);
		}
		return result;
	}

	/* Get Values */

	/**
	 * @see se.kth.cid.config.Config#getString(java.lang.String)
	 */
	public String getString(String key) {
		return config.getProperty(key);
	}

	/**
	 * @see se.kth.cid.config.Config#getString(java.lang.String,
	 *      java.lang.String)
	 */
	public String getString(String key, String defaultValue) {
		return config.getProperty(key, defaultValue);
	}

	/**
	 * @see se.kth.cid.config.Config#getStringList(java.lang.String)
	 */
	public List getStringList(String key) {
		return getPropertyValues(key);
	}

	/**
	 * @see se.kth.cid.config.Config#getStringList(java.lang.String,
	 *      java.util.List)
	 */
	public List getStringList(String key, List defaultValues) {
		List result = getPropertyValues(key);
		if (result == null) {
			result = defaultValues;
		}
		return result;
	}

	/**
	 * @see se.kth.cid.config.Config#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String key) {
		String strValue = config.getProperty(key);
		boolean boolValue = false;
		
		if (strValue != null) {
			boolValue = Boolean.valueOf(strValue).booleanValue();
		}
		
		return boolValue;
	}

	/**
	 * @see se.kth.cid.config.Config#getBoolean(java.lang.String, boolean)
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		String strValue = config.getProperty(key);
		boolean boolValue = false;
		
		if (strValue != null) {
			boolValue = Boolean.valueOf(strValue).booleanValue();
		} else {
			boolValue = defaultValue;
		}
		
		return boolValue;
	}

	/**
	 * @see se.kth.cid.config.Config#getByte(java.lang.String)
	 */
	public byte getByte(String key) {
		String strValue = config.getProperty(key);
		byte byteValue = 0;
		
		if (strValue != null) {
			byteValue = Byte.valueOf(strValue).byteValue();
		}
		
		return byteValue;
	}

	/**
	 * @see se.kth.cid.config.Config#getByte(java.lang.String, byte)
	 */
	public byte getByte(String key, byte defaultValue) {
		String strValue = config.getProperty(key);
		byte byteValue = 0;
		
		if (strValue != null) {
			byteValue = Byte.valueOf(strValue).byteValue();
		} else {
			byteValue = defaultValue;
		}
		
		return byteValue;
	}

	/**
	 * @see se.kth.cid.config.Config#getDouble(java.lang.String)
	 */
	public double getDouble(String key) {
		String strValue = config.getProperty(key);
		double doubleValue = 0;
		
		if (strValue != null) {
			doubleValue = Double.valueOf(strValue).doubleValue();
		}
		
		return doubleValue;
	}

	/**
	 * @see se.kth.cid.config.Config#getDouble(java.lang.String, double)
	 */
	public double getDouble(String key, double defaultValue) {
		String strValue = config.getProperty(key);
		double doubleValue = 0;
		
		if (strValue != null) {
			doubleValue = Double.valueOf(strValue).doubleValue();
		} else {
			doubleValue = defaultValue;
		}
		
		return doubleValue;
	}

	/**
	 * @see se.kth.cid.config.Config#getFloat(java.lang.String)
	 */
	public float getFloat(String key) {
		String strValue = config.getProperty(key);
		float floatValue = 0;
		
		if (strValue != null) {
			floatValue = Float.valueOf(strValue).floatValue();
		}
		
		return floatValue;
	}

	/**
	 * @see se.kth.cid.config.Config#getFloat(java.lang.String, float)
	 */
	public float getFloat(String key, float defaultValue) {
		String strValue = config.getProperty(key);
		float floatValue = 0;
		
		if (strValue != null) {
			floatValue = Float.valueOf(strValue).floatValue();
		} else {
			floatValue = defaultValue;
		}
		
		return floatValue;
	}

	/**
	 * @see se.kth.cid.config.Config#getInt(java.lang.String)
	 */
	public int getInt(String key) {
		String strValue = config.getProperty(key);
		int intValue = 0;
		
		if (strValue != null) {
			intValue = Integer.valueOf(strValue).intValue();
		}
		
		return intValue;
	}

	/**
	 * @see se.kth.cid.config.Config#getInt(java.lang.String, int)
	 */
	public int getInt(String key, int defaultValue) {
		String strValue = config.getProperty(key);
		int intValue = 0;
		
		if (strValue != null) {
			intValue = Integer.valueOf(strValue).intValue();
		} else {
			intValue = defaultValue;
		}
		
		return intValue;
	}

	/**
	 * @see se.kth.cid.config.Config#getLong(java.lang.String)
	 */
	public long getLong(String key) {
		String strValue = config.getProperty(key);
		long longValue = 0;
		
		if (strValue != null) {
			longValue = Long.valueOf(strValue).longValue();
		}
		
		return longValue;
	}

	/**
	 * @see se.kth.cid.config.Config#getLong(java.lang.String, long)
	 */
	public long getLong(String key, long defaultValue) {
		String strValue = config.getProperty(key);
		long longValue = 0;
		
		if (strValue != null) {
			longValue = Long.valueOf(strValue).longValue();
		} else {
			longValue = defaultValue;
		}
		
		return longValue;
	}

	/**
	 * @see se.kth.cid.config.Config#getShort(java.lang.String)
	 */
	public short getShort(String key) {
		String strValue = config.getProperty(key);
		short shortValue = 0;
		
		if (strValue != null) {
			shortValue = Short.valueOf(strValue).shortValue();
		}
		
		return shortValue;
	}

	/**
	 * @see se.kth.cid.config.Config#getShort(java.lang.String, short)
	 */
	public short getShort(String key, short defaultValue) {
		String strValue = config.getProperty(key);
		short shortValue = 0;
		
		if (strValue != null) {
			shortValue = Short.valueOf(strValue).shortValue();
		} else {
			shortValue = defaultValue;
		}
		
		return shortValue;
	}
	
	/**
	 * @see se.kth.cid.config.Config#getURI(java.lang.String)
	 */
	public URI getURI(String key) {
		try {
			String uri = config.getProperty(key);
			if (uri != null) {
				return new URI(uri);
			}
		} catch (URISyntaxException e) {
		}
		return null;
	}

	/**
	 * @see se.kth.cid.config.Config#getURI(java.lang.String, URI)
	 */
	public URI getURI(String key, URI defaultValue) {
		URI result = getURI(key);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}
	
	/**
	 * @see se.kth.cid.config.Config#getColor(java.lang.String)
	 */
	public Color getColor(String key) {
		Color result = null;
		String value = getString(key);

		if (value != null) {
			try {
				if (!value.startsWith("0x"))
					result = Color.decode(value);
				else {
					int rgb = Long.decode(value).intValue();
					result = new Color(rgb);
				}
			} catch (NumberFormatException nfe) {
			}
		}

        return result;
	}

	/**
	 * @see se.kth.cid.config.Config#getColor(java.lang.String, java.awt.Color)
	 */
	public Color getColor(String key, Color defaultValue) {
		Color result = getColor(key);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

}