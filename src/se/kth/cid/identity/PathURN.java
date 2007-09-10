/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.identity;


/** Represents a Path URN.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 *  @deprecated
 */
public class PathURN extends URN
{

  /** Creates a "file:" URL from the given string.
   *
   *  @param nuri the string to parse.
   *  @exception MalformedURIException if the string did not parse.
   */
  public PathURN(String nuri) throws MalformedURIException
    {
      super(nuri);

      if(uri.length() == secondColonLocation + 1)
	throw new MalformedURIException("Empty Path URN", uri);

      if(uri.charAt(secondColonLocation + 1) != '/')
	throw new MalformedURIException("Path URN has no '/': \""+
					uri + "\"", uri);

      if(!uri.regionMatches(colonLocation + 1, "path", 0, secondColonLocation - colonLocation - 1))
	throw new MalformedURIException("The identifier was no Path URN \""
					+ uri + "\".", uri);
    }

  
  /** Returns the <code>path</code> part of the URN.
   */
  public String getPath()
    {
      return super.getProtocolSpecific();
    }

  public String makeRelative(URI other, boolean allowDotDot) throws MalformedURIException
    {
      if(!(other instanceof PathURN))
	return other.toString();
      return genericMakeRelative(other, ((PathURN) other).secondColonLocation + 1, secondColonLocation + 1,
				 allowDotDot);
    }

  protected URI parseRelativeURI(String relstr) throws MalformedURIException
    {
      return new PathURN(genericParseRelativeURI(relstr, secondColonLocation + 1));
    }  
}

  
