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


package se.kth.cid.conzilla.app;


import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.install.*;

import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.event.*;

public class ConzillaApp extends ConzillaAppEnv
{
  public ConzillaApp()
    {

	Font menuFont = new Font("Lucida Sans", Font.BOLD, 10);
	UIManager.put("Button.font", menuFont);
	UIManager.put("ToggleButton.font", menuFont);
	UIManager.put("RadioButton.font", menuFont);
	//	  UIManager.put("CheckBox.font", font);
	//	  UIManager.put("ColorChooser.font", font);
	UIManager.put("ComboBox.font", menuFont);
	UIManager.put("Label.font", menuFont);
	//	  UIManager.put("List.font", font);
	UIManager.put("MenuBar.font", menuFont);
	UIManager.put("MenuItem.font", menuFont);
	//	  UIManager.put("RadioButtonMenuItem.font", font);
	//	  UIManager.put("CheckBoxMenuItem.font", font);
	UIManager.put("Menu.font", menuFont);
	//	  UIManager.put("PopupMenu.font", font);
	//	    UIManager.put("OptionPane.font", font);
	//	    UIManager.put("Panel.font", font);
	//	    UIManager.put("ProgressBar.font", font);
	//	    UIManager.put("ScrollPane.font", font);
	//	    UIManager.put("Viewport.font", font);
	UIManager.put("TabbedPane.font", menuFont);
	//	    UIManager.put("Table.font", font);
	//	    UIManager.put("TableHeader.font", font);

	Font textFont = new Font("Lucida Sans", Font.PLAIN, 12);

	UIManager.put("TextField.font", textFont);
	//	    UIManager.put("PasswordField.font", font);
	UIManager.put("TextArea.font", textFont);
	//	    UIManager.put("TextPane.font", font);
	//	    UIManager.put("EditorPane.font", font);
	UIManager.put("TitledBorder.font", menuFont);
	//	    UIManager.put("ToolBar.font", font);
	//	    UIManager.put("ToolTip.font", font);
	//	    UIManager.put("Tree.font", font);
    }

  protected void initDefaultContentDisplayer(PathURNResolver resolver)
    {
      defaultContentDisplayer = new ApplicationContentDisplayer(resolver);
    }
  
  
  public static void main(String[] argv)
    {
      if (argv.length >= 2 ||
	  (argv.length == 1 &&
	   (argv[0].equals("-h") || argv[0].equals("?") || argv[0].equals("?"))))
	{
	  System.out.print("Usage: Conzilla [ConceptMap]\n");
	  System.exit(-1);
	}

      ConzillaApp app = new ConzillaApp();

      ConzillaKit kit = new ConzillaKit(app);

      //      Tracer.setLogLevel(Tracer.ALL);

      URI startMap = null;
      if(argv.length == 1)
	{
	  try {
	    startMap = URIClassifier.parseURI(argv[0]);
	  } catch(MalformedURIException e)
	    {
	      System.out.print("Invalid start map: '" + argv[0] + "':\n " +
			       e.getMessage() + "\n\n");
	      System.exit(-1);
	    }
	}
      else 
	  {
	      try {
		  String strStartMap=app.getConfig().getProperty(ConzillaConfig.PROPERTY_STARTMAP);
		  if (strStartMap!=null)
		      startMap = URIClassifier.parseURI(strStartMap);
	      }
	      catch (MalformedURIException me)
		  { 
		      System.out.println("Invalid start map in conzilla.cnf");
		  }
	  }
      if (startMap==null)
	  startMap = URIClassifier.parseValidURI("urn:path:/org/conzilla/builtin/maps/default");
      
      if(!kit.getConzilla().openMap(startMap))
	{
	  startMap = URIClassifier.parseValidURI("urn:path:/org/conzilla/builtin/maps/default");
	  
	  if (!kit.getConzilla().openMap(startMap))
	      app.exit(1);
	}
    }
}
