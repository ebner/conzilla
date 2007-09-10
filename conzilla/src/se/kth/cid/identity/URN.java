/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.identity;


/** Represents an URN.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 *  @deprecated
 */
public class URN extends URI
{

  /** The location of the second colon.
   */
  protected int secondColonLocation;

  /** Creates a URN from the given string.
   *
   *  @param nurn the string to parse.
   *  @exception MalformedURIException if the string did not parse.
   */
  public URN(String nurn) throws MalformedURIException
    {
      super(nurn);
      secondColonLocation = nurn.indexOf(':', colonLocation + 1);

      if(secondColonLocation == -1
	 || secondColonLocation > fragmentLocation
	 || secondColonLocation == colonLocation + 1)
	throw new MalformedURIException("No protocol part in URN \""
					+ nurn + "\".", nurn);
      if(!nurn.regionMatches(0, "urn", 0, colonLocation))
	throw new MalformedURIException("The identifier was no URN \""
					+ nurn + "\".", nurn);
    }
  

  /** Returns the <code>URN protocol</code> part of the URN.
    */
  public String getProtocol()
    {
      return uri.substring(colonLocation + 1, secondColonLocation);
    }

  
  /** Returns the <code>protocol specific</code> part of the URN.
    */
  public String getProtocolSpecific()
    {
      return uri.substring(secondColonLocation + 1, fragmentLocation);
    }

  public String makeRelative(URI other) throws MalformedURIException
    {
      throw new MalformedURIException("Generic URNs do not support relative URIs", other.toString());
    }

  protected URI parseRelativeURI(String relstr) throws MalformedURIException
    {
      throw new MalformedURIException("Generic URNs do not support relative URIs", uri);
      
    }
    
}

  
