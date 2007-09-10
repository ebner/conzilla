/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.identity;



/** This class represents "res:" URLs.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 *  @deprecated
 */
public class ResourceURL extends URI
{
  /** Creates a "res:" URL from the given string.
   *
   *  @param nuri the string to parse.
   *  @exception MalformedURIException if the string did not parse.
   */
  public ResourceURL(String nuri) throws MalformedURIException
    {
      super(nuri);

      if(uri.length() == colonLocation + 1)
	throw new MalformedURIException("Empty path", null);
      
      if(uri.charAt(colonLocation + 1) != '/')
	throw new MalformedURIException("No leading '/' in \""+ uri + "\"",
					uri);
      if(!uri.regionMatches(0, "res", 0, colonLocation))
	throw new MalformedURIException("The identifier was no Resource URI \""
					+ uri + "\".", uri);
    }

  
  public String getResourceName()
    {
      return super.getSchemeSpecific().substring(1);
    }
  
  
  /** Returns a Java URL object pointing to the file represented by this
   *  ResourceURL.
   *
   *  @return a Java URL object.
   */
  public java.net.URL getJavaURL() throws java.net.MalformedURLException 
    {
      java.net.URL url = getClass().getClassLoader().getResource(this.getResourceName());
      if(url == null)
	throw new java.net.MalformedURLException("No such resource found: " + getResourceName());
      
      return url;
    }

  public String makeRelative(URI other, boolean allowDotDot) throws MalformedURIException
    {
      if(!(other instanceof ResourceURL))
	return other.toString();
      return genericMakeRelative(other, 4, 4, allowDotDot);
    }

  protected URI parseRelativeURI(String relstr) throws MalformedURIException
    {
      return new ResourceURL(genericParseRelativeURI(relstr, 4));
    }

}
