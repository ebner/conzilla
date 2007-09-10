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


package se.kth.cid.identity;


import java.io.File;
/** 
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class FileURL extends URI
{
  public FileURL(String nuri) throws MalformedURIException
    {
      super(nuri);

      if(uri.length() == colonLocation + 1)
	throw new MalformedURIException("Empty path", null);
      
      if(uri.charAt(colonLocation + 1) != '/')
	throw new MalformedURIException("No leading '/' in \""+ uri + "\"",
					uri);
      if(!uri.regionMatches(0, "file", 0, colonLocation))
	throw new MalformedURIException("The identifier was no File URI \""
					+ uri + "\".", uri);
    }

  
  public String getPath()
    {
      return super.getSchemeSpecific();
    }
  
  /** Constructs a 'file' URL from a local filename.
   *  <p><emph>WARNING</emph><br>
   *  Please note that this class uses System.getProperty on properties
   *  inaccessible from a browser. This class should therefore only be
   *  used in standalone applications.
   *  @param file the filename to use.
   *  @return a URL pointing to the file.
   *  @exception MalformedURLException if the URL became unvalid.
   */
  public static FileURL toURL(String file) throws MalformedURIException
    {
      String curDir = System.getProperty("user.dir");
      
      String fileSep = System.getProperty("file.separator");
      curDir = curDir.replace(fileSep.charAt(0), '/') + '/';
      if (curDir.charAt(0) != '/')
	{
	  curDir = "/" + file;
	}
      FileURL baseURI = new FileURL(curDir);
      return (FileURL) baseURI.makeRelativeURI(file);
    }

  public File getJavaFile()
    {
      return new File(getPath());
    }
  
  protected URI makeRelativeURI(String relstr) throws MalformedURIException
    {
      String res;
      
      if(relstr.charAt(0) == '/')
	res = "file:" + relstr;
      else 
	{
	  int sl = uri.lastIndexOf('/', fragmentLocation);
	  res = uri.substring(0, sl + 1) + relstr;
	}
      return new FileURL(res);
    }

}
