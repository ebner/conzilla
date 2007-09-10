/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

///*
// *  $Id$
// *
// *  Copyright (c) 1999, KTH (Royal Institute of Technology)
// *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
// */
//
//package se.kth.cid.config;
//
//import java.awt.Color;
//import java.beans.PropertyChangeListener;
//import java.beans.PropertyChangeSupport;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//import org.apache.commons.configuration.ConfigurationException;
//import org.apache.commons.configuration.ConversionException;
//import org.apache.commons.configuration.FileConfiguration;
//
//import se.kth.cid.util.Tracer;
//
///**
// * Wrapper around Apache Configuration's implementations of FileConfiguration.
// * Some methods have been simplified, others just wrapped.<br>
// * Stores and loads configurations in various formats, depending on the
// * constructor's argument.
// * 
// * <p>
// * See the static methods of the class Configurations for wrappers around the
// * Config interface, e.g. to get synchronized view of the object.
// * 
// * <p>
// * Examples for Configuration formats: XMLConfiguration,
// * PropertiesConfiguration, PropertyListConfiguration, ...
// * 
// * @author Hannes Ebner
// * @version $Id$
// * @see Configurations
// * @see Config
// * @deprecated Replaced by PropertiesConfiguration
// */
//public class PersistentConfiguration implements Config {
//
//	/**
//	 * The main resource in this object. Contains the configuration.
//	 */
//	private FileConfiguration config;
//	
//	private PropertyChangeSupport pcs;
//
//	private boolean modified = false;
//
//	/* Constructors */
//
//	/**
//	 * Initializes the object with an empty Configuration.
//	 */
//	public PersistentConfiguration(FileConfiguration fileConfig) {
//		this.config = fileConfig;
//		this.pcs = new PropertyChangeSupport(this);
//	}
//
//	/* Helpers */
//
//	/**
//	 * Sets the modified status of this configuration.
//	 * 
//	 * @param mod
//	 *            Status.
//	 */
//	private void setModified(boolean mod) {
//		this.modified = mod;
//	}
//	
//	private void checkFirePropertyChange(String key, Object oldValue, Object newValue) {
//		if ((oldValue == null) && (newValue != null)) {
//			this.pcs.firePropertyChange(key, oldValue, newValue);
//		} else if ((oldValue != null) && (!oldValue.equals(newValue))) {
//			this.pcs.firePropertyChange(key, oldValue, newValue);
//		}
//	}
//	
//	/*
//	 * Interface implementation
//	 */
//
//	/* Generic */
//
//	/**
//	 * @see se.kth.cid.config.Config#clear()
//	 */
//	public void clear() {
//		this.config.clear();
//		this.setModified(true);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#isEmpty()
//	 */
//	public boolean isEmpty() {
//		return this.config.isEmpty();
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#isModified()
//	 */
//	public boolean isModified() {
//		return this.modified;
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#load(java.net.URI)
//	 */
//	public void load(URL config) {
//		try {
//			this.config.load(config);
//		} catch (ConfigurationException e) {
//			Tracer.debug(e.getMessage());
//		}
//		//this.setModified(true);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#save(java.net.URI)
//	 */
//	public void save(URL config) {
//		try {
//			this.config.save(config);
//		} catch (ConfigurationException e) {
//			Tracer.debug(e.getMessage());
//		}
//		this.setModified(false);
//	}
//	
//	/* Property Change Listeners */
//	
//	/**
//	 * @see se.kth.cid.config.Config#addPropertyChangeListener(java.beans.PropertyChangeListener)
//	 */
//	public void addPropertyChangeListener(PropertyChangeListener listener) {
//		this.pcs.addPropertyChangeListener(listener);
//	}
//	
//	/**
//	 * @see se.kth.cid.config.Config#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
//	 */
//	public void addPropertyChangeListener(String key, PropertyChangeListener listener) {
//		this.pcs.addPropertyChangeListener(key, listener);
//	}
//	
//	/**
//	 * @see se.kth.cid.config.Config#removePropertyChangeListener(java.beans.PropertyChangeListener)
//	 */
//	public void removePropertyChangeListener(PropertyChangeListener listener) {
//		this.pcs.removePropertyChangeListener(listener);
//	}
//	
//	/**
//	 * @see se.kth.cid.config.Config#removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
//	 */
//	public void removePropertyChangeListener(String key, PropertyChangeListener listener) {
//		this.pcs.removePropertyChangeListener(key, listener);
//	}
//
//	/* Properties / Set Values */
//
//	/**
//	 * @see se.kth.cid.config.Config#clearProperty(java.lang.String)
//	 */
//	public void clearProperty(String key) {
//		String oldValue = this.getString(key);
//		this.config.clearProperty(key);
//		this.setModified(true);
//		this.checkFirePropertyChange(key, oldValue, null);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#addProperty(java.lang.String,
//	 *      java.lang.Object)
//	 */
//	public void addProperty(String key, Object value) {
//		this.config.addProperty(key, value);
//		this.setModified(true);
//		// since this is an add we want to fire in any case
//		this.pcs.firePropertyChange(key, null, value);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#addProperties(java.lang.String,
//	 *      java.util.List)
//	 */
//	public void addProperties(String key, List values) {
//		Iterator valueIterator = values.iterator();
//		while (valueIterator.hasNext()) {
//			this.config.addProperty(key, valueIterator.next());
//		}
//		this.setModified(true);
//		// since this is an add we want to fire in any case
//		this.pcs.firePropertyChange(key, null, values);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#setProperty(java.lang.String,
//	 *      java.lang.Object)
//	 */
//	public void setProperty(String key, Object value) {
//		String oldValue = null;
//		try {
//			oldValue = this.getString(key);
//		} catch (ConversionException ce) {
//		}
//		this.config.setProperty(key, value);
//		this.setModified(true);
//		this.checkFirePropertyChange(key, oldValue, value);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#setProperties(java.lang.String,
//	 *      java.util.List)
//	 */
//	public void setProperties(String key, List values) {
//		List oldValues = this.getStringList(key);
//		this.config.clearProperty(key);
//		this.addProperties(key, values);
//		this.setModified(true);
//		this.checkFirePropertyChange(key, oldValues, values);
//	}
//
//	/* Keys */
//
//	/**
//	 * @see se.kth.cid.config.Config#containsKey(java.lang.String)
//	 */
//	public boolean containsKey(String key) {
//		return this.config.containsKey(key);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getKeyList()
//	 */
//	public List getKeyList() {
//		return this.getKeyList(null);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getKeyList(java.lang.String)
//	 */
//	public List getKeyList(String prefix) {
//		ArrayList result = new ArrayList();
//		Iterator keyIterator = (prefix != null) ? this.config.getKeys(prefix) : this.config.getKeys();
//		while (keyIterator.hasNext()) {
//			result.add(keyIterator.next());
//		}
//		return result;
//	}
//
//	/* Get Values */
//
//	/**
//	 * @see se.kth.cid.config.Config#getString(java.lang.String)
//	 */
//	public String getString(String key) {
//		return this.config.getString(key);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getString(java.lang.String,
//	 *      java.lang.String)
//	 */
//	public String getString(String key, String defaultValue) {
//		return this.config.getString(key, defaultValue);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getStringList(java.lang.String)
//	 */
//	public List getStringList(String key) {
//		return this.config.getList(key);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getStringList(java.lang.String,
//	 *      java.util.List)
//	 */
//	public List getStringList(String key, List defaultValues) {
//		return this.config.getList(key, defaultValues);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getBoolean(java.lang.String)
//	 */
//	public boolean getBoolean(String key) {
//		return this.config.getBoolean(key);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getBoolean(java.lang.String, boolean)
//	 */
//	public boolean getBoolean(String key, boolean defaultValue) {
//		return this.config.getBoolean(key, defaultValue);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getByte(java.lang.String)
//	 */
//	public byte getByte(String key) {
//		return this.config.getByte(key);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getByte(java.lang.String, byte)
//	 */
//	public byte getByte(String key, byte defaultValue) {
//		return this.config.getByte(key, defaultValue);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getDouble(java.lang.String)
//	 */
//	public double getDouble(String key) {
//		return this.config.getDouble(key);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getDouble(java.lang.String, double)
//	 */
//	public double getDouble(String key, double defaultValue) {
//		return this.config.getDouble(key, defaultValue);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getFloat(java.lang.String)
//	 */
//	public float getFloat(String key) {
//		return this.config.getFloat(key);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getFloat(java.lang.String, float)
//	 */
//	public float getFloat(String key, float defaultValue) {
//		return this.config.getFloat(key, defaultValue);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getInt(java.lang.String)
//	 */
//	public int getInt(String key) {
//		return this.config.getInt(key);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getInt(java.lang.String, int)
//	 */
//	public int getInt(String key, int defaultValue) {
//		return this.config.getInt(key, defaultValue);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getLong(java.lang.String)
//	 */
//	public long getLong(String key) {
//		return this.config.getLong(key);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getLong(java.lang.String, long)
//	 */
//	public long getLong(String key, long defaultValue) {
//		return this.config.getLong(key, defaultValue);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getShort(java.lang.String)
//	 */
//	public short getShort(String key) {
//		return this.config.getShort(key);
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getShort(java.lang.String, short)
//	 */
//	public short getShort(String key, short defaultValue) {
//		return this.config.getShort(key, defaultValue);
//	}
//	
//	/**
//	 * @see se.kth.cid.config.Config#getURI(java.lang.String)
//	 */
//	public URI getURI(String key) {
//		try {
//			String uri = this.config.getString(key);
//			if (uri != null) {
//				return new URI(uri);
//			}
//		} catch (URISyntaxException e) {
//		}
//		return null;
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getURI(java.lang.String, URI)
//	 */
//	public URI getURI(String key, URI defaultValue) {
//		URI result = getURI(key);
//		if (result == null) {
//			return defaultValue;
//		}
//		return result;
//	}
//	
//	/**
//	 * @see se.kth.cid.config.Config#getColor(java.lang.String)
//	 */
//	public Color getColor(String key) {
//		Color result = null;
//		String value = getString(key);
//
//		if (value != null) {
//			try {
//				if (!value.startsWith("0x"))
//					result = Color.decode(value);
//				else {
//					int rgb = Long.decode(value).intValue();
//					result = new Color(rgb);
//				}
//			} catch (NumberFormatException nfe) {
//			}
//		}
//
//        return result;
//	}
//
//	/**
//	 * @see se.kth.cid.config.Config#getColor(java.lang.String, java.awt.Color)
//	 */
//	public Color getColor(String key, Color defaultValue) {
//		Color result = getColor(key);
//		if (result == null) {
//			return defaultValue;
//		}
//		return result;
//	}
//
//	public void addProperties(String key, Iterator values) {
//		// TODO Auto-generated method stub
//	}
//
//	public void setProperties(String key, Iterator values) {
//		// TODO Auto-generated method stub
//	}
//
//}