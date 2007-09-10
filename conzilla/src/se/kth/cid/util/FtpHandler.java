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
import java.net.*;
import java.io.*;
import java.util.*;
import com.fooware.net.*;

/** This class caches ftpconnections avoiding the hassle of conntecting
 *  and authorizing each time a file is requested.
 *  All work regarding individual connections including the connect and authorize
 *  process is done by the FtpClientWrapper class.
 *
 *  @see FtpClientWrapper
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class FtpHandler
{
    
    /** The individual connections, in form of FtpClientWrapper. */
    Vector wrappers;

    public FtpHandler()
    {
	wrappers=new Vector();
    }
    
    /** A request for an OutputStream succeeds if an ftp-session can be opened
     *  from the information in the url (if not already opened).
     *
     * @returns a OutputStream, don't forget to close it when done, otherwise written information
     *          may not be saved and further requests to this ftp-connection may fail.
     */
    public OutputStream getOutputStream(URL url) throws IOException
    {
	FtpClientWrapper fcw=getFtpClientWrapperFor(url);	
	return fcw.getFtpOutputStream(url.getFile());	
    }
    
    /** A request for an InputStream succeeds if an ftp-session can be opened
     *  from the information in the url (if not already opened).
     *
     * @returns a InputStream, don't forget to close it when done, otherwise 
     *            further requests to this ftp-connection may fail.
     */
    public InputStream getInputStream(URL url) throws IOException
    {
	FtpClientWrapper fcw=getFtpClientWrapperFor(url);
	return fcw.getFtpInputStream(url.getFile());	
    }
    
    /** Tells how much of the path is valid, the rest has to be created with
     *  a call to makePathValid.
     * 
     *  @returns two Lists (in a array) where List[0] is a 
     *           List containing the part of the path that is valid, 
     *           List[1] is a List containing the rest,
     *           returns null if path never can be valid.
     */
    public List[]  tellValidPath(URL url)
    {
	try {
	    FtpClientWrapper fcw=getFtpClientWrapperFor(url);
	    Vector path=extractPath(url);
	    Vector validPath=fcw.tellValidPath(path);
	    if (validPath==null)
	      return null;
	    path.removeAll(validPath);
	    List [] list = new List[2];
	    list[0]=validPath;
	    list[1]=path;
	    return list;
	} catch (IOException io)
	    {
		return null;
	    }
    }
    
    /** Creates directorys giving you a valid path where creation of components
     *  is legitimate.
     *
     *  @returns a List containg the path that acctually where created.
     */
    public List makePathValid(URL url, List validPath, List makeValidPath)
    {
	try {
	    FtpClientWrapper fcw=getFtpClientWrapperFor(url);
	    return fcw.makePathValid(validPath, makeValidPath);
	} catch (IOException io)
	    {
		return null;
	    }
    }
    	
    /** A simpler version of function tellValidPath.
     *
     *  @returns true if the path in the url is valid, i.e. all the directorys exists.
     */
    public boolean isValidPath(URL url)
    {
	try {
	    FtpClientWrapper fcw=getFtpClientWrapperFor(url);
	    Vector path=extractPath(url);
	    Vector validPath=fcw.tellValidPath(path);
	    if (validPath==null)
		return false;
	    return path.size()==validPath.size();
	} catch (IOException io)
	    {
		return false;
	    }
    }
    
    /** Checks wheather the url can be written upon,
     *  if so the function suceeds silently, otherwise it throws 
     *  an IOException.
     */
    public void isWritable(URL url) throws IOException
    {
	FtpClientWrapper fcw=getFtpClientWrapperFor(url);
	fcw.isWritable(url.getFile());
    }

    /** This class is a workaround ta allow URL's to be compared 
     *  with FtpClientWrapper in a vector.
     *
     *  When the function contains is called on a Vector, the objects within 
     *  is given as arguments to the euquals method on the object given as argument 
     *  to contains. If the argument is a URL the comparision with the objects 
     *  within the Vector (FtpClientWrappers) will fail since the URL's equals method
     *  isn't adapted to the situation.
     *  So, you say, override the URL's equals method. 
     *  Well, then I would have to change all the code that creates URL's and create
     *  this inherited class instead.
     *  Bad idea! better to just use this class to wrap the URL when given to the
     *  contains function in Vector. This class does the equals method 'the other way around'.
     *  I.e. uses the FtpClientWrapper equals method with the url as a input instead.
     *
     *  (This has to be the longest explanation for such a minor detail ever given by me. :-)
     */
    class Around
    {
	Object po;
	public Around(Object po)
	{
	    this.po=po;
	}
	public boolean equals(Object o)
	{
	    return o.equals(po);
	}
    }

    private Vector extractPath(URL url)
    {
	Vector path=new Vector();
	String pathAndFile=url.getFile().substring(1);
	int indexOfSlash=pathAndFile.indexOf('/');
	while (indexOfSlash!=-1)
	    {
		path.addElement(pathAndFile.substring(0,indexOfSlash));
		pathAndFile=pathAndFile.substring(indexOfSlash+1);
		indexOfSlash=pathAndFile.indexOf('/');
	    }
	return path;
    }		

    private FtpClientWrapper getFtpClientWrapperFor(URL url) throws IOException
    {
	Around aurl=new Around(url);
	FtpClientWrapper fcw;
	int i=wrappers.indexOf(aurl);
	if (i==-1)
	    {
		fcw=new FtpClientWrapper(url);
		wrappers.addElement(fcw);
	    }
	else
	    fcw= (FtpClientWrapper) wrappers.get(i);
	return fcw;
    }
}
