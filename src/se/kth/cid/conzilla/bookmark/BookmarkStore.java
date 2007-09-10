/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.bookmark;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

import se.kth.cid.conzilla.InfoMessageException;
import se.kth.cid.conzilla.install.Installer;
import se.kth.cid.util.FileOperations;
import se.kth.cid.util.Tracer;

/**
 * Takes care of loading and storing the TreeModel of the BookmarkTree.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class BookmarkStore {

	/**
	 * The marker for detecting whether to store the TreeModel or not.
	 */
	private boolean modified = false;
	
	private static String storeName = "BookmarkStore";
	
	private File dataFile;
	
	private TreeModel model;
	
	private long flushingInterval = 10000; // 10 seconds
	
	private int bufferSize = 8192; // 8kB
	
	/**
	 * Flushes the data to disk.
	 * 
	 * @author Hannes Ebner
	 */
	private class StoreFlusher extends TimerTask implements Runnable {
		public void run() {
			BookmarkStore.this.flushModel();
		}
	}
	
	/**
	 * @param storeName
	 *            The name of this store. Used for debug().
	 * @param dataFile
	 *            File to load/store the data from/to. Without path.
	 */
	public BookmarkStore(String file) {
		this.dataFile = new File(getIndexFilePath(file));
		
		if (dataFile.exists()) {
			loadModel(dataFile);
		} else {
			createModel();
		}
		
		model.addTreeModelListener(new TreeModelListener() {
			public void treeNodesChanged(TreeModelEvent e) {
				BookmarkStore.this.setModified(true);
			}
			public void treeNodesRemoved(TreeModelEvent e) {
				BookmarkStore.this.setModified(true);
			}
			public void treeStructureChanged(TreeModelEvent e) {
				BookmarkStore.this.setModified(true);
			}
			public void treeNodesInserted(TreeModelEvent e) {
				BookmarkStore.this.setModified(true);
			}
		});
		
		debug("Setting up flushing timer and shutdown hook");
		new Timer().schedule(new StoreFlusher(), flushingInterval, flushingInterval);
		Runtime.getRuntime().addShutdownHook(new Thread(new StoreFlusher()));
		
		debug("Started");
	}
	
	/**
	 * Loads the TreeModel from an XML file.
	 * 
	 * @param modelFile
	 *            File to load the data from.
	 */
	private void loadModel(File modelFile) {
		BufferedInputStream bis = null;
		XMLDecoder input = null;
		try {
			debug("Loading data from " + modelFile);
			bis = new BufferedInputStream(new FileInputStream(modelFile), bufferSize);
			try {
				input = new XMLDecoder(bis);
				model = (BookmarkTreeModel) input.readObject();
			} catch (Exception e) {
				debug(e.getMessage() + ": creating new model");
				createModel();
			}
		} catch (IOException ioe) {
			throw new InfoMessageException("Unable to load cache index", ioe);
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}
	
	/**
	 * Creates the TreeModel.
	 */
	private void createModel() {
		//BookmarkInformation root = new BookmarkInformation("root", "Bookmarks", BookmarkInformation.TYPE_FOLDER);
		//model = new DefaultTreeModel(new BookmarkNode(root));
		model = new BookmarkTreeModel();
	}
	
	private void debug(String message) {
		Tracer.debug(storeName + ": " + message);
	}
	
	private static String getIndexFilePath(String file) {
		String dirHelper = Installer.getConzillaDir().getAbsolutePath();
		if (!dirHelper.endsWith(File.separator)) {
			dirHelper = dirHelper.concat(File.separator);
		}
		return dirHelper.concat(file);
	}
	
	/**
	 * Writes the TreeModel to disk.
	 */
	private synchronized void flushModel() {
		if (modified) {
			File tmpDataFile = new File(dataFile.toString().concat("~"));
			BufferedOutputStream bos = null;
			XMLEncoder encoder = null;
			try {
				bos = new BufferedOutputStream(new FileOutputStream(tmpDataFile), bufferSize);
				encoder = new XMLEncoder(bos);
				try {
					encoder.writeObject(model);
				} catch (Exception e) {
					debug("Error occured while saving bookmarks");
					return;
				}
			} catch (FileNotFoundException fnfe) {
				throw new InfoMessageException(fnfe);
			} finally {
				if (encoder != null) {
					encoder.close();
				}
			}
			FileOperations.moveFile(tmpDataFile.toURI(), dataFile.toURI());
			modified = false;
			debug("Wrote data to disk");
		}
	}
	
	/**
	 * @return Returns the TreeModel.
	 */
	public TreeModel getTreeModel() {
		return model;
	}
	
	private void setModified(boolean modified) {
		this.modified = modified;
	}
	
}