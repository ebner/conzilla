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
import java.io.*;
import java.util.*;
import java.net.*;
import com.fooware.net.*;



/** This class wraps the FtpClient package from fooware.
 *  It blocks functionality and takes care or errors to some extent.
 *
 *   This class uses the com.fooware.net package.
 *   According to it's copyright notice (GPL) it's alright to use and add  
 *   functionality as long as the product is also within the GPL.
 * 
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class FtpClientWrapper
{
    
    String host;
    String user;
    String passwd;
    String home;

    /** An FtpClient.*/
    com.fooware.net.FtpClient ftp;
    FtpResponse resp;
    FtpOutputStream fos;
    

    /** The wrapper only accepts a wellformed url and information enough to 
     *  perform an authorization.
     */
    public FtpClientWrapper(URL url) throws IOException
    {
	//	Tracer.debug("FtpClientWrapper konstruktor.......");
	dissolveURL(url);
	
	ftp=new com.fooware.net.FtpClient();
	connect();

	//Try to set the permissions to readable for the world.
	//Only works on unix-type systems...
	try {
	    ftp.siteParameters("UMASK 022");
	} catch (IOException io)
	    {}  //If umask isn't supported, we probably don't need to do it.
	//try to figure the home directory...
	//	ftp.printWorkingDirectory();
	//	resp=ftp.getResponse();
	//	if (!resp.isPositiveCompletion())
	//	    throw new IOException("Couldn't execute pwd, need to know home directory.");
	//	Tracer.debug("pwd, for figuring the home directory.");
	//	Tracer.debug(resp.getMessage());
	
	//	int lastSlashPosition=resp.getMessage().substring(5).indexOf("\"");
	//	home=resp.getMessage().substring(5,5+lastSlashPosition);
	//	Tracer.debug("home:="+home);			
    }
    
    /** This function is used in Vectors and such.
     */
    public boolean equals(Object o)
    {
	if (o instanceof FtpClientWrapper)
	    {
		FtpClientWrapper fcw=(FtpClientWrapper) o;
		return fcw.host.equals(host) && fcw.user.equals(user);
	    }
	else if (o instanceof URL)
	    {
		String [] parts = getDissolvedURL((URL) o);
		return parts[0].equals(host) && parts[1].equals(user);
	    }
	return false;
    }
    
    
    /** Creates a file on the remote side and returns an Outputstream on it.
     *  Retries to connect and authorize if needed, throws an IOException on failure.
     *  Fails if path to file doesn't exist, call function tellValidPath and makePathValid
     *  to check and fix.
     */
    public FtpOutputStream getFtpOutputStream(String file) throws IOException
    {
	assureConnected();  //If we already know that we are not connected, connect..
	
	if (resp.isPositivePreliminary())   //REMOVE ME??????
	    {
		Tracer.bug("Not ready for next command, old command still in progress:\n"+
			   "(forgot to close inputstream? or non synchronized threads?) \n" + resp.getMessage());
		throw new IOException("Can't open new inputstream, old command not yet finished.");
	    }

	FtpOutputStream fos;	

	ftp.dataPort();
	if (!assureConnected())  //If server terminated connection and notification came on next (row above) command....
	   {
	       ftp.dataPort();
	       resp=ftp.getResponse();
	       if (!resp.isPositiveCompletion())
		   throw new IOException("Couldn't open FtpInputStream on file: "+
					 file+"\nError:"+resp.getMessage());
	   }

	fos=ftp.storeStream(file);
	resp = ftp.getResponse();
	if (resp.isTransientNegativeCompletion() || resp.isPermanentNegativeCompletion())
	    throw new IOException(resp.getMessage());

	return fos;
    }

    /** Opens the file on the server side and returns an InputStream to it.
     *  Retries to connect and authorize if needed, throws an IOException on failure.
     *  Fails if file doesn't exist or is unreadable.
     */
    public FtpInputStream getFtpInputStream(String file) throws IOException
    {
	assureConnected();
	
	if (resp.isPositivePreliminary())   //REMOVE ME??????
	    {
		Tracer.bug("Not ready for next command, old command still in progress:\n"+
			   "(forgot to close inputstream? or non synchronized threads?) \n"+resp.getMessage());
		throw new IOException("Can't open new inputstream, old command not yet finished.");
	    }

	FtpInputStream fis;

	ftp.dataPort();
	if (!assureConnected())  //If server terminated connection and notification came on next (row above) command....
	   {
	       ftp.dataPort();
	       resp=ftp.getResponse();
	       if (!resp.isPositiveCompletion())
		   throw new IOException("Couldn't open FtpInputStream on file: "+
					 file+"\nError:"+resp.getMessage());
	   }

	fis=ftp.retrieveStream(file);
	resp = ftp.getResponse();
	if (resp.isTransientNegativeCompletion() || resp.isPermanentNegativeCompletion())
	    throw new IOException(resp.getMessage());
	
	return fis;
    }
    
    /** Use this function to find out how much of a path that is valid.
     *  Retries to connect and authorize if needed, throws an IOException on failure.
     *  
     *  @returns a Vector that contains the part of the path that is valid as a number of Strings.
     */ 
    public Vector tellValidPath(List path) throws IOException
    {
	assureConnected();
	if (resp.isPositivePreliminary())   //REMOVE ME??????
	    {
		Tracer.bug("Not ready for next command, old command still in progress:\n"+
			   "(forgot to close inputstream? or non synchronized threads?) \n"+resp.getMessage());
		throw new IOException("Can't open new inputstream, old command not yet finished.");
	    }
	ftp.noOp();
	assureConnected();

	ftp.changeWorkingDirectory("/");
	resp=ftp.getResponse();
	if (!resp.isPositiveCompletion())
	    {
		disconnect();
		return null;
	    }
	
	Vector ret=new Vector();
	Iterator it=path.iterator();

	while (it.hasNext())
	    {
		String dir=(String) it.next();
		ftp.changeWorkingDirectory(dir);
		resp = ftp.getResponse();
		
		if (resp.isTransientNegativeCompletion())
		    {
			Tracer.debug(resp.getMessage());
			return null;
		    }

		if (resp.isPermanentNegativeCompletion())
		    return ret;
		    /*		    {
			ftp.changeWorkingDirectory("/");
			resp=ftp.getResponse();
			if (!resp.isPositiveCompletion())
			    {
				disconnect();
				return null;
			    }
			return ret;
			}*/
		ret.addElement(dir);
	    }
	/*	ftp.changeWorkingDirectory(home);
	resp=ftp.getResponse();
	if (!resp.isPositiveCompletion())
	    {
		disconnect();
		return null;
		}*/
	return ret;
    }

    /** Use this function to create part of a path that tellValidPath function reported didn't exist.
     *
     *  @returns the part of the path that actually were created.
     */
    public Vector makePathValid(List validPath, List makeValidPath) throws IOException
    {	
	Tracer.debug("makepathValid: validpath="+validPath+" \nmakeValidPath="+makeValidPath);

	//First put together the valid path.
	Iterator it=validPath.iterator();
	String path=new String("/");
	while (it.hasNext())
	    path=path+((String) it.next())+"/";
	
	//Now make the new directorys.
	it=makeValidPath.iterator();	
	while (it.hasNext())
	    {
		path=path+((String) it.next())+"/";
		if (!makeDirectory(path))
		    break;
	    }

	//If failed creating the whole path.
	if (it.hasNext())
	    {
		Vector failed=new Vector(makeValidPath);
		while (it.hasNext())
			failed.remove(it.next());
		Tracer.debug(failed.toString());
		return failed;
	    }
	return new Vector(makeValidPath);
    }

    /** Use this function to create a directory.
     *
     *  @param path to investigate
     *  @returns a boolean to indicate sucess or failure.
     */
    public boolean makeDirectory(String path) throws IOException
    {
	assureConnected();
	
	if (resp.isPositivePreliminary())   //REMOVE ME??????
	    {
		Tracer.bug("Not ready for next command, old command still in progress:\n"+
			   "(forgot to close inputstream? or non synchronized threads?) \n"+resp.getMessage());
		throw new IOException("Can't open new inputstream, old command not yet finished.");
	    }
	
	ftp.makeDirectory(path);
	if (!assureConnected())  //If server terminated connection and notification came on next (row above) command....
	    {
		Tracer.debug(resp.getMessage());
		ftp.makeDirectory(path);
		resp=ftp.getResponse();
		Tracer.debug(resp.getMessage());
		return resp.isPositiveCompletion();
	    }
	return resp.isPositiveCompletion();
    }
	
    /** Checks wheather the file can be written upon.
     *  Right now this function only assures that the connection is up
     *  and authorized.
     *  Throws an IOException on faillure.
     */
    public void isWritable(String file) throws IOException
    {
	assureConnected();
    }
    /** Dissconect this connection. */
    public boolean disconnect()
    {
	try {
	    resp = ftp.getResponse();
	    if (resp.getReturnCode().equals("421") || resp.getReturnCode().equals("221"))
		return true;
	    if (!resp.isPositiveCompletion() && resp.isRegardingConnection())
		return false;
	    ftp.logout();
	    resp = ftp.getResponse();
	} catch (IOException io)     //I assume that an IOException when trying to close means already closed.
 	    {
		Tracer.bug("Treated returncodes badly, ioException isn't acceptable here."+
			   "assuming connection already closed:"+
			   io.getMessage());
	    }
	return true;
    }

    private void dissolveURL(URL url)
    {
	String [] parts=getDissolvedURL(url);

	host   = parts[0];
	user   = parts[1];
	passwd = parts[2];
    }    

    private String [] getDissolvedURL(URL url)
    {
	String [] parts=new String[3];
	
	parts[0]=url.toString().substring(6,url.toString().indexOf('/',7));
	int p=parts[0].indexOf('@');
	if (p!=-1)
	    {
		parts[1]=parts[0].substring(0,p);
		parts[0]=parts[0].substring(p+1);
		p=parts[1].indexOf(':');
		if (p!=-1)
		    {
			parts[2]=parts[1].substring(p+1);
			parts[1]=parts[1].substring(0,p);
		    }
		else   //FIXME: This is degenerate... should ask user.
		    parts[2]="humty@dumty";
	    }
	else
	    {
		parts[1]="anonymous";
		parts[2]="humty@dumty";
	    }
	return parts;
    }
    
    private void connect() throws IOException
    {
	ftp.connect(host);
	resp = ftp.getResponse();
	if (!resp.isPositiveCompletion())
	    {
		Tracer.debug("Response from connect:"+
			     resp.getMessage());
		throw new IOException(resp.getMessage());
	    }
	Tracer.debug("connected to "+host);       //REMOVE ME
	login();
    }

    private void login() throws IOException
    {
	//sending user
	ftp.userName(user);
	resp = ftp.getResponse();
	if (resp.isTransientNegativeCompletion() || 
	    resp.isPermanentNegativeCompletion())
	    {
		Tracer.debug("Failed logging in as "+user+"to host.\n"+
			     resp.getMessage());
		throw new IOException(resp.getMessage());
	    }
	//	Tracer.debug("as "+user);                 //REMOVE ME
	
	//Password requested.
	if (resp.isPositiveIntermediary())
	    {
		ftp.password(passwd);
		resp = ftp.getResponse();
		if (!resp.isPositiveCompletion()) 
		    throw new IOException(resp.getMessage()); //Cannot handle failure of password...
		//		Tracer.debug("with "+passwd + " as password");  //REMOVE ME
	    }
    }

    private boolean assureConnected() throws IOException
    {
	resp = ftp.getResponse();
	if (resp.isPositiveCompletion() && !resp.getReturnCode().equals("221"))
	    return true;
	if (resp.getReturnCode().equals("221") || resp.getReturnCode().equals("421"))
	    connect();
	else if (resp.isRegardingAuthentication() && resp.isPermanentNegativeCompletion())
	    login();
	else
	    return true;
	return false;
    }

}
