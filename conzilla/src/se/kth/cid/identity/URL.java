/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.identity;

/** Represents an URL. Only supports URLs of the form:
 *  scheme://host[:port]/path/file#fragment.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 *  @deprecated
 */
public class URL extends URI
{
  /** The port of the URL, or -1.
   */
  int port;

  /** The end of the host name part.
   */
  int portColonLocation;
  
  /** The location of the slash indicating the path.
   */
  int pathLocation;

  /** Creates a URL from the given string.
   *
   *  @param nurl the string to parse.
   *  @exception MalformedURIException if the string did not parse.
   */
  public URL(String nurl) throws MalformedURIException
    {
      super(nurl);

      if(uri.length() < colonLocation + 4)
	throw new MalformedURIException("URL is incomplete: \""+
					uri + "\"", uri);
      
      if((uri.charAt(colonLocation + 1) != '/') ||
	 (uri.charAt(colonLocation + 2) != '/'))
	throw new MalformedURIException("URL does not begin with '://': \"" +
					uri + "\"", uri);
      portColonLocation = uri.indexOf(':', colonLocation + 3);
      pathLocation      = uri.indexOf('/', colonLocation + 3);

      if(pathLocation == colonLocation + 3)
	throw new MalformedURIException("URL has no host: \""+
					uri + "\"", uri);
      if(pathLocation == -1)
	throw new MalformedURIException("URL has no '/': \""+
					uri + "\"", uri);

      if(pathLocation > fragmentLocation)
	throw new MalformedURIException("URL has no '/' before fragment: \""+
					uri + "\"", uri);
	
      port = -1;
      if(portColonLocation >= 0 && portColonLocation < pathLocation)
	{
	  try {
	    port = Integer.parseInt(uri.substring(portColonLocation + 1,
						  pathLocation), 10);
	  }
	  catch(NumberFormatException e)
	    {
	      throw new MalformedURIException("Invalid port number \"" +
					      uri.substring(portColonLocation + 1,
							     pathLocation)
					      + "\" in \"" + uri + "\"",
					      uri);
	    }
	  if(port <= 0 || port > 65535)
	    throw new MalformedURIException("Invalid port number \"" + port +
					    "\" in \"" + uri + "\"",
					    uri);
	}
      else
	portColonLocation = pathLocation;
    }
  
  
  /** Returns the <code>host</code> part of the URL.
   */
  public String getHost()
    {
      return uri.substring(colonLocation + 3, portColonLocation);
    }
  
   /** Returns the <code>port</code> part of the URL.
   */
  public int getPort()
    {
      return port;
    }
  
  /** Returns the <code>path</code> part of the URL.
   */
  public String getPath()
    {
      return uri.substring(pathLocation, fragmentLocation);
    }

  public String makeRelative(URI other, boolean allowDotDot) throws MalformedURIException
    {
      if(!(other instanceof URL))
	return other.toString();
      return genericMakeRelative(other, ((URL) other).pathLocation, pathLocation, allowDotDot);
    }
  
  protected URI parseRelativeURI(String relstr) throws MalformedURIException
    {
      return new URL(genericParseRelativeURI(relstr, pathLocation));
    }
}

  
