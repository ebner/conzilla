/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.util;


/**
 * This class is used for debugging and for putting trace printouts in code that
 * may be controlled at runtime.
 * <p>
 * The printouts are controlled by a global log level When a trace is given to
 * this class, it checks whether the trace is this log level or higher, and then
 * proceeds to print it. Otherwise the trace is simply ignored. Debug traces
 * cannot be turned off, though.
 * 
 * @author Mikael Nilsson
 * @version $Revision$
 * @deprecated Replaced by Apache Commons Logging. Supposed to be used like<br>
 *             <pre>Log log = LogFactory.getLog(CLASS.class);</pre><br>
 *             after the class declaration.<br>
 */
public class Tracer {

    private Tracer() {
    }

}