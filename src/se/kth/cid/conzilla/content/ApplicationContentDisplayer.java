/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;
import java.net.URL;

public class ApplicationContentDisplayer extends BrowserContentDisplayer
{  
  protected boolean showDocument(URL uri) throws ContentException
    {
               try {
                   String os = (String) System.getProperties().get("os.name");
                   String [] command = null;
                   if (os.toLowerCase().matches(".*windows.*")) {
                       command = new String[3];
                       command[0] = "rundll32";
                       command[1] = "url.dll,FileProtocolHandler";
                       command[2] = uri.toString();
                   } else if (os.toLowerCase().matches(".*mac.*")) {
                       command = new String[2];
                       command[0] = "open";
                       command[1] = uri.toString();
                   } else if (os.toLowerCase().matches(".*linux.*")) {
                       //command = new String[3];
                       //command[0] = "netscape";
                       //command[1] = "-remote";
                       //command[2] = "openURL(" + uri.toString() + ")";
                       Process p = Runtime.getRuntime().exec("mozilla -remote ping()");
                       int ping = p.waitFor();
                       if (ping == 0) {
                           command = new String[3];
                           command[0] = "mozilla";
                           command[1] = "-remote";
                           command[2] = "openURL("+uri+")";
                       } else {
                           command = new String[2];
                           command[0] = "mozilla";
                           command[1] = uri.toString();
                       }
                   }
                   if (command != null) {
                       Runtime.getRuntime().exec(command);
                   }
               } catch (Exception e) {
                   e.printStackTrace();
                   return false;
               }
               return true;
        
/*      String [] command;
      //      if (com.apple.mrj.MRJApplicationUtils.isMRJToolkitAvailable())

      //Mac support.
      //if (System.getProperty("mrj.version") != null)
      //    com.apple.mrj.MRJFileUtils.openURL(url.toString());
      //else 
      if (File.separatorChar == '/')
	{
	  command = new String[3];
	  command[0] = "";
	  command[1] = "-remote";
	  command[2] = "openURL(" + url.toString() + ")";
	}
      else
	{
	  command=new String[2];
	  command[0] = "browser.bat";
	  command[1] = url.toString();
	}
      try {
	Runtime.getRuntime().exec(command);
      } catch(IOException e)
	{
	  throw new ContentException("Could not execute browser:\n "
				     + e.getMessage(), null);
	}
      return true;*/
    }
}
