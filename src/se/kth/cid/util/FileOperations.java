/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Commonly used file operations.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class FileOperations {
	
	static Log log = LogFactory.getLog(FileOperations.class);
	
	public static void moveFile(URI temp, URI orig) {
		if (temp.equals(orig)) {
			return;
		}
		File tmpFile = new File(temp);
		File destFile = new File(orig);
		if ((tmpFile != null) && (destFile != null)) {
			log.debug("Moving temporary file " + tmpFile + " to " + destFile);
			if (!tmpFile.renameTo(destFile)) {
				try {
					copyFile(tmpFile, destFile);
				} catch (IOException e) {
					log.error(e);
				}
				if (!tmpFile.delete()) {
					log.warn("Unable to delete temporary file");
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
    
    public static void copyFile(InputStream is, OutputStream os) {
    	BufferedInputStream bis = null;
    	BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(os);
			
			byte[] b = new byte[2048];
			int s;
			while ((s = bis.read(b)) != -1) {
				bos.write(b, 0, s);
			}
		} catch (IOException e) {
			log.error("Unable to copy file", e);
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (Exception ignored) {}
		}
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
						log.warn("Could not delete file " + file);
						return false;
					}
					log.debug("Deleted file " + file);
				}
			}
		}
		return true;
	}

}