/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;

/**
 * Commonly used file operations.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class FileOperations {
	
	public static void moveFile(URI temp, URI orig) {
		if (temp.equals(orig)) {
			return;
		}
		File tmpFile = new File(temp);
		File destFile = new File(orig);
		if ((tmpFile != null) && (destFile != null)) {
			Tracer.debug("Moving temporary file " + tmpFile + " to " + destFile);
			if (!tmpFile.renameTo(destFile)) {
				try {
					copyFile(tmpFile, destFile);
				} catch (IOException e) {
					Tracer.debug(e.getMessage());
				}
				if (!tmpFile.delete()) {
					Tracer.debug("Unable to delete temporary file");
				}
			}
		}
	}
	
    public static void copyFile(File src, File dst) throws IOException {
    	FileChannel sourceChannel = new FileInputStream(src).getChannel();
    	FileChannel destinationChannel = new FileOutputStream(dst).getChannel();
    	sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
        sourceChannel.close();
        destinationChannel.close();
    }
    
	/**
	 * Deletes all files in a given directory, but does not remove the directory
	 * itself.
	 * 
	 * @param dir
	 *            Directory to be cleaned.
	 * @return True if successful for all files.
	 */
	public static boolean deleteAllFilesInDir(File dir) {
		File file;
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				file = new File(dir, children[i]);
				if (file.isFile()) {
					if (!file.delete()) {
						return false;
					}
				}
			}
		}
		return true;
	}

}