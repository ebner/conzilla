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


package se.kth.cid.conzilla.install;

import se.kth.cid.identity.pathurn.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.properties.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

/* Needed resource structure:
 *
 * |-- components
 * |   `-- types
 * |       `-- library
 * |           `-- generic
 * |               |-- bookmarks
 * |               |-- root
 * |               `-- templates
 * `-- install
 *     |-- bookmarks
 *     |   `-- bookmarks
 *     |-- resolver.xml
 *     |-- local
 *     |   |-- Resources_map
 *     |   `-- Message
 *     |-- root
 *     |   `-- root
 *     `-- templates
 *         `-- templates
 *
 */


public class Installer implements Runnable
{
    public static final String LOCALRESOLVER  = "resolver.xml";
    
    public static final String GLOBALRESOLVER = "http://www.conzilla.org/resolver/resolver.xml";
    
    public static final String LOCALDIR  = "local";
    
    public static final String CONFIG_FILE      = "conzilla.properties";
  
    static URI configURI;  
    
    URI globalResolver;
    URI localResolver;
    URI rootLibrary;
    
    String version;

    static File configFilePath;
    
    public static URI getConfigURI()
    {
	if(configURI != null)
	    return configURI;
	
	String userDir = System.getProperty("user.home");
	String conzillaDir;
	
	if(File.separatorChar == '/') // UNIX etc.
	    conzillaDir = ".conzilla";
	else                          // Others
	    conzillaDir = "Conzilla";
	
	configFilePath = new File(userDir + File.separatorChar +
				  conzillaDir + File.separatorChar +
				  CONFIG_FILE);
	try {
	    configURI = FileURL.getFileURL(configFilePath.toString());
	} catch(MalformedURIException e)
	    {
		Tracer.bug("Could not make URI " + e.getURI());
	    }
	return configURI;
    } 

  public static File getConzillaDir()
    {
	getConfigURI();
	return configFilePath.getParentFile();
    }
  

  boolean stopInstall;
  JTextArea logArea;
  JButton   okBut;

  boolean startConzilla = false;

  private Installer(JTextArea logArea, JButton okBut)
    {
      this.logArea = logArea;
      this.okBut = okBut;
      stopInstall = false;
    }  

  public static void installOrExit(String why)
    {
      Object[] options = {"Yes, install",
			  "No, exit."};
      int result = JOptionPane.showOptionDialog(null,
						why + "\n\n" +
						"Conzilla would need to install files in the directory\n" +
						"\n" +
						getConzillaDir() + "\n" +
						"\n" +
						"to be able to start. Do you want to do this now?",
						
						"Install?",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,     //don't use a custom Icon
						options,  //the titles of buttons
						options[0]); //default button title

      if(result == JOptionPane.YES_OPTION)
	startInstall();
      else
	System.exit(0);
    }
  
  public static void startInstall()
    {
      final JDialog dialog = new JDialog((JFrame) null, "Install?", true);
      dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      
      dialog.setSize(new Dimension(500, 300));
      dialog.setLocationRelativeTo(null);


      
      Container panel = dialog.getContentPane();
      panel.setLayout(new BorderLayout());

      
      JTextArea logArea = new JTextArea();
      logArea.setEditable(false);

      JScrollPane scroll = new JScrollPane(logArea);
      scroll.setBorder(BorderFactory.createTitledBorder("Install log"));
      
      panel.add(scroll, BorderLayout.CENTER);



      JPanel buttons = new JPanel();
      buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));

      JButton ok     = new JButton("Start!");
      JButton cancel = new JButton("Exit now");

      ok.setEnabled(false);
      buttons.add(ok);
      buttons.add(cancel);

      panel.add(buttons, BorderLayout.SOUTH);


      final Installer install = new Installer(logArea, ok);
      final Thread installThread = new Thread(install, "Install");
      

      
      cancel.addActionListener(new AbstractAction() {
	  public void actionPerformed(ActionEvent action)
	    {
	      dialog.dispose();
	    }
	});
      ok.addActionListener(new AbstractAction() {
	  public void actionPerformed(ActionEvent action)
	    {
	      install.startConzilla = true;
	      dialog.dispose();
	    }
	});

      dialog.addWindowListener(new WindowAdapter() {
	  public void windowOpened(WindowEvent e)
	    {
	      installThread.start();
	    }
	});
      
      dialog.show();

      install.stopInstall(installThread);

      if(!install.startConzilla)
	System.exit(0);
    }
  
  synchronized void stopInstall(Thread installThread)
    {
      if(installThread.isAlive())
	{
	  stopInstall = true;
	  try {
	    wait(7000);
	  } catch(InterruptedException e)
	    {
	    }
	  installThread.interrupt();
	}
    }
  

  // Methods only used in install thread...
  
  
  public void run()
    {
      try {
	Thread.sleep(500);
	
	logArea.append("Starting install\n");
	
	File conzillaDir = getConzillaDir();
	makeDir(conzillaDir);
	
	makeDir(new File(conzillaDir, "root"));
	
	makeDir(new File(conzillaDir, "bookmarks"));
	
	makeDir(new File(conzillaDir, "templates"));
	
	makeDir(new File(conzillaDir, "local"));

	installFile("resolver.xml", new File(conzillaDir, LOCALRESOLVER));
	try {
	    URI localResolver = new FileURL(new File(conzillaDir, LOCALRESOLVER).toURL().toString());
	    String localDir = new File(conzillaDir, "local").toURL().toString();

	    ResolverTable table = new ResolverTable(localResolver);
	    ResolverTable.ResolverEntry [] entries = table.getEntries();
	    ResolverTable.ResolverEntry [] newEntries = new ResolverTable.ResolverEntry[entries.length+1];
	    newEntries[0] = table.newResolverEntry("/org/conzilla/local", localDir, MIMEType.XML);
	    for (int i = 0; i < entries.length; i++)
		newEntries[i + 1] = entries[i];
	    table.setEntries(newEntries);
	    table.saveTable();
	} catch (Exception e)
	    {
		Tracer.bug("Couldn't add /org/conzilla/local to the local resolvertable!!\n"+e.getMessage());
	    }
	
	installFile("root/root",
		    new File(new File(conzillaDir, "root"), "root"));
	installFile("templates/templates",
		    new File(new File(conzillaDir, "templates"), "templates"));
	installFile("bookmarks/bookmarks",
		    new File(new File(conzillaDir, "bookmarks"), "bookmarks"));

	File localDir = new File(conzillaDir, "local");
	installFile("local/Resources_map", new File(localDir, "Resources_map"));
	installFile("local/Message", new File(localDir, "Message"));
	
	logArea.append("Creating conzilla.properties...");
	File prop = new File(conzillaDir, "conzilla.properties");
	installFile("conzilla.properties", prop);
	try {
	  GlobalConfig config = GlobalConfig.getGlobalConfig();
	  InputStream is = new FileInputStream(prop);
	  config.loadConfig(is);
	  is.close();

	  config.setProperty(ConzillaKit.ROOT_LIBRARY_PROP,
			     URIClassifier.parseURI("root/root", getConfigURI()).toString());
	  config.setProperty(ConzillaAppEnv.LOCAL_RESOLVER_PROP,
			     URIClassifier.parseURI(LOCALRESOLVER, getConfigURI()).toString());

	  config.setProperty(ConzillaAppEnv.CONZILLA_VERSION_PROP, ConzillaAppEnv.CONZILLA_CONFIG_VERSION);

	  saveConfig();
	} catch(IOException e)
	  {
	    abort("Could not create config file.", e);
	  }
	catch(MalformedURIException e)
	  {
	    abort("Invalid URI\n\n" + e.getURI(), e);
	  }
	logArea.append("done.\n");
	checkStop();
	
	
	logArea.append("Install done!\n");
      
	done();
      } catch(InterruptedException e)
	{
	}
      }
    
    public static void saveConfig() throws IOException
    {
	URI configURI = getConfigURI();
	if(configURI instanceof FileURL)
	    {
		FileOutputStream os = new FileOutputStream(((FileURL) configURI).getJavaFile());
		GlobalConfig.getGlobalConfig().store(os);
		os.close();
	    }
	else
	    throw new IOException("Cannot store config to non-file URIs:\n " + configURI.toString());
    }

  synchronized void checkStop() throws InterruptedException
    {
      if(stopInstall)
	{
	  notifyAll();
	  throw new InterruptedException("");
	}
    }


  void makeDir(File dir) throws InterruptedException
    {
      logArea.append("Creating directory " + dir + "...");
      if(!dir.exists())
	{
	  if(!dir.mkdir())
	    abort("The directory\n\n"
		  + dir.toString()
		  + "\n\ncould not be created!", null);
	}
      
      if(!dir.isDirectory())
	abort("The directory\n\n"
	      + dir.toString()
	      + "\n\nalready exists but is no directory!", null);
      
	  
      if(!dir.canWrite())
	abort("The directory\n\n"
	      + dir.toString()
	      + "\n\ncannot be written to!", null);

      logArea.append("done.\n");
      checkStop();
    }

  void installFile(String res, File file) throws InterruptedException
    {
      logArea.append("Installing " + file + "...");
      try {
	FileOutputStream resFile = new FileOutputStream(file);
	java.net.URL resURL =
	  getClass().getClassLoader().getResource("install/" + res);
	if(resURL == null)
	  abort("Could not find resource\n\n" + res +
		"\n\nThis is an installer bug.", null);
	InputStream is = resURL.openStream();

	byte[] b = new byte[1024];
	int s;
	while((s = is.read(b)) != -1)
	  resFile.write(b, 0, s);
			       
	resFile.close();
      } catch(IOException e)
	{
	  abort("Could not install file\n\n"
		+ file,
		e);
	}
      logArea.append("done\n");
      checkStop();
    }

  void abort(String error, Exception e)  throws InterruptedException
    {
      logArea.append("\n" + error + "\n\nInstall aborted.");
      ErrorMessage.showError("Intallation Error",
			     error + 
			     "\n\nPlease correct this " +
			     "and re-run Conzilla", e, null);
      throw new InterruptedException("");
    }

  void done()
    {
      SwingUtilities.invokeLater(new Runnable()
	{
	  public void run()
	    {
	      okBut.setEnabled(true);
	    }
	});
    }
  
	  
}
