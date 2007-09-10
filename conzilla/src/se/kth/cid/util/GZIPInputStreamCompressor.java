/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Compresses an InputStream and provides the compressed data as new
 * InputStream. No intermediate GZIPOutputStream for compression is necessary.
 * 
 * @author Hannes Ebner
 * @version $Id$
 * @see java.util.zip.GZIPInputStream
 * @see java.util.zip.GZIPOutputStream
 */
public class GZIPInputStreamCompressor extends Thread {

	/**
	 * Original InputStream.
	 */
	private InputStream input;

	/**
	 * Piped stream, returned by getInputStream().
	 */
	private PipedInputStream pipedIn;

	/**
	 * Stream to write to.
	 */
	private OutputStream output;

	/**
	 * Amount of read bytes.
	 */
	private long byteCount;

	/**
	 * The size of the stream buffers.
	 */
	private int bufferSize;

	/**
	 * Holds information whether the thread is done.
	 */
	private boolean finished;

	/**
	 * Reports whether getInputStream() has been called.
	 */
	private boolean usable = true;

	/**
	 * Initializes the object with an InputStream and a custom buffer size. The
	 * buffer size is used for all internal streams which allow for setting the
	 * size of the buffer.
	 * 
	 * @param is
	 *            Original InputStream.
	 * @param bufferSize
	 *            Size of the stream buffers.
	 * @throws IOException
	 */
	public GZIPInputStreamCompressor(InputStream is, int bufferSize) throws IOException {
		this.bufferSize = bufferSize;
		input = is;
		byteCount = 0;
		pipedIn = new CustomPipedInputStream(bufferSize);
		PipedOutputStream pipedOut = new PipedOutputStream(this.pipedIn);
		output = new GZIPOutputStream(pipedOut, bufferSize);
	}

	/**
	 * Initializes the object with an InputStream and configures a default
	 * buffer size.
	 * 
	 * @param is
	 *            Original InputStream.
	 * @throws IOException
	 */
	public GZIPInputStreamCompressor(InputStream is) throws IOException {
		this(is, 4096);
	}

	/**
	 * Tells whether the reading/writing of the main-thread has finished.
	 * 
	 * @return True if the thread is finished, false if the work is in progress
	 *         or if it has not started yet.
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Starts the internal thread and returns a new InputStream.
	 * 
	 * @return InputStream
	 */
	public synchronized InputStream getInputStream() {
		start();
		return pipedIn;
	}

	/**
	 * Returns the amount of bytes which have been read from the InputStream.
	 * 
	 * @return Byte count.
	 */
	public long getByteCount() {
		return byteCount;
	}
	
	private void checkUsable() {
		if (usable) {
			throw new IllegalStateException("The source has already been read.");
		}
	}

	/**
	 * Reads from an uncompressed InputStream and writes to an OutputStream
	 * compressor which is piped to a new PipedInputStream.
	 */
	public void run() {
		checkUsable();
		
		byte[] buffer = new byte[bufferSize];
		int readCount;
		byteCount = 0;
		usable = false;

		try {
			while (true) {
				// we read from the InputStream and write into all OutputStreams
				if ((readCount = input.read(buffer)) != -1) {
					output.write(buffer, 0, readCount);
					byteCount += readCount;
				} else {
					// EOF
					output.flush();
					output.close();
					break;
				}
			}
		} catch (IOException ioe) {
			// This should be replaced with something different...
			Tracer.error(ioe.getMessage());
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException ioe) {
			}
		}

		finished = true;
	}

}