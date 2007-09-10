/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.util;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Modification of PipedInputStream for customizing the size of the internal
 * circular buffer of the pipe.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class CustomPipedInputStream extends PipedInputStream {

	/**
	 * @param bufferSize Size of the internal circular buffer.
	 * @see java.io.PipedInputStream#PipedInputStream()
	 */
	public CustomPipedInputStream(int bufferSize) {
		super();
		this.buffer = new byte[bufferSize];
	}

	/**
	 * @param src Data source as PipedOutputStream.
	 * @param bufferSize Size of the internal circular buffer.
	 * @throws IOException
	 * @see java.io.PipedInputStream#PipedInputStream(PipedOutputStream)
	 */
	public CustomPipedInputStream(PipedOutputStream src, int bufferSize) throws IOException {
		super(src);
		this.buffer = new byte[bufferSize];
	}

}