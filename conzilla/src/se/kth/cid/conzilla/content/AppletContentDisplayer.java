/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;
import java.applet.AppletContext;
import java.net.URL;

public class AppletContentDisplayer extends BrowserContentDisplayer
{
  String frame;
  AppletContext context;

  public AppletContentDisplayer(AppletContext context, String frame)
    {
      this.context = context;
      this.frame = frame;
    }
  
  protected boolean showDocument(URL url)
    {
      context.showDocument(url, frame);
      return true;
    }
}
