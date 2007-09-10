/* $Id$ */
/*
  This file is part of the Conzilla browser, designed for
  the Garden of Knowledge project.
  Copyright (C) 1999  CID (http://www.nada.kth.se/cid)
  
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/


package se.kth.cid.util;

/** This class is used for debugging and for putting trace printouts in code
 *  that may be controlled at runtime.
 *  <p>
 *  The printouts are controlled by a global log level
 *  When a trace is given to this class, it checks whether
 *  the trace is this log level or higher, and then
 *  proceeds to print it. Otherwise the trace is simply ignored. Debug traces
 *  cannot be turned off, though.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class Tracer
{
  /** Will always be reported.
   */
  public static final int DEBUG             = 0;
  
  /** Programming error of some kind, that is only caused by a bug in the program.
   *  Illegal arguments are included here.
   */
  public static final int BUG               = 1; 

  /** Serious error in the program, that may be difficult to recover from.
   *  but that can occur without programming errors.
   */
  public static final int ERROR             = 2;

  /** Less serious errors that will not cause immediate problems.
   */
  public static final int WARNING           = 3;

  /** Larger, externally visible event.
   */
  public static final int MAJOR_EXT_EVENT   = 4;

  /** Lesser, externally visible event (e.g. state change).
   */
  public static final int MINOR_EXT_EVENT   = 5;

  /** Larger, internally visible event (implementation dependent)
   */
  public static final int MAJOR_INT_EVENT   = 6;
  
  /** Lesser, internally visible event (implementation dependent)
   */
  public static final int MINOR_INT_EVENT   = 7;

  /** Details from the inner workings. The DETAIL + n levels are allowed,
   *  but are defined by the application, and are reported as "DETAILn".
   */
  public static final int DETAIL            = 8;



  
  /** Use this as log level to never get any traces (except DEBUGs)
   */
  public static final int NONE              = DEBUG;

  /** Use to get all traces.
   */
  public static final int ALL               = Integer.MAX_VALUE;

  /** Array of the strings that are used to report the above levels.
   */
  public static final String[] TRACESTR = {
    "DEBUG",
    "BUG",
    "ERROR",
    "WARNING",
    "MAJOR_EXT_EVENT",
    "MINOR_EXT_EVENT",
    "MAJOR_INT_EVENT",
    "MINOR_INT_EVENT",
    "DETAIL"
  };

  /** The log level. Default WARNING.
   */
  private static int logLevel = WARNING;
  
  private Tracer()
    {}

  /** Returns the loglevel represented by the string.
   *  @param nLogLevelStr a string representing a loglevel.
   *  @return the loglevel represented by the string, or -1.
   *  @exception IllegalArgumentException if the loglevel is invalid.
   */
  public static int parseLogLevel(String level)
    {
      for(int i = 0; i <= MINOR_INT_EVENT; i++) 
	if(level.equals(TRACESTR[i]))
	  return i;
      if(level.equals("NONE"))
	return NONE;
      if(level.equals("ALL"))
	return ALL;
      if(level.startsWith("DETAIL"))
	{
	  if(level.length() == 6)
	    return DETAIL;
	  
	  String levelStr = level.substring(6);
	  if(levelStr.charAt(0) == '-')
	    throw new IllegalArgumentException("Illegal logLevel: " +
					       level);
	  return DETAIL + Integer.parseInt(levelStr);
	}
      throw new IllegalArgumentException("Illegal logLevel: " +
					 level);
    }

  
  /** Used to change the log level.
   *  @param nlogLevel the new log level. Must be positive or zero.
   */
  public static void setLogLevel(int nlogLevel)
    {
      if(nlogLevel < 0)
	{
	  trace("Tracer: illegal logLevel: " + nlogLevel, BUG);
	  throw
	    new IllegalArgumentException("Tracer: illegal logLevel: "
					 + nlogLevel);
	}
      logLevel = nlogLevel;
    }

  /** Returns the trace string for the given log level,
   *  i.e. the string used as label for printouts for traces at this log level.
   *  @param nlogLevel the log level to convert to a String.
   *  @return A string representing the log level.
   */
  public static String getTraceStr(int nlogLevel)
    {
      if(nlogLevel < 0)
	{
	  trace("Tracer: illegal logLevel: " + nlogLevel, BUG);
	  throw
	    new IllegalArgumentException("Tracer: illegal logLevel: "
					 + nlogLevel);
	}
      if(nlogLevel < DETAIL)
	return TRACESTR[nlogLevel];
      else
	return "DETAIL" + (nlogLevel - DETAIL);
    }

  /** Used to trace program action.
   *  @param trace the string to display.
   *  @param thisLogLevel the loglevel of this trace.
   */
  public static void trace(String trace, int thisLogLevel)
    {
      if(thisLogLevel < 0)
	{
	  trace("Tracer: illegal logLevel: " + thisLogLevel, BUG);
	  throw
	    new IllegalArgumentException("Tracer: illegal logLevel: "
					 + thisLogLevel);
	}

      if(logLevel < thisLogLevel)
	return;

      System.err.print(getTraceStr(thisLogLevel) + ": " + trace + "\n");
    }

  /** Used when a serious error has occured, from which recovery is not foreseen.
   *  Will throw an Error with the given message, as well as issue a trace with level
   *  Tracer.ERROR.
   *
   *  @param trace the string to display.
   *  @exception Error always thrown.
   */
  public static void error(String trace) throws Error
    {
      trace(trace, ERROR);
      throw new Error(trace);
    }

  /** Used when a bug has been discovered.
   *  Will throw an Error with the given message, as well as issue a trace with level
   *  Tracer.BUG.
   *
   *  @param trace the string to display.
   *  @exception Error always thrown.
   */
  public static void bug(String trace) throws Error
    {
      trace(trace, BUG);
      throw new Error(trace);
    }
  
  /** Used to debug program action. Actually shorthand for <br>
   *  <code> Tracer.trace(trace, Tracer.DEBUG) </code>
   *  @param trace the string to display.
   */
  public static void debug(String trace)
    {
      trace(trace, DEBUG);
    }

}
