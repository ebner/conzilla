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
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.component.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.component.cache.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.menu.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.xml.*;


import java.util.*;
import javax.swing.*;
import java.net.*;
import java.awt.event.*;
import java.awt.*; 
import java.awt.print.*;
import javax.swing.border.*;

public class FontExtra implements Extra
{
    ConzillaKit kit;
    
  public FontExtra()
    {
    }


    public String getName()
    {
	return "FontExtra";
    }

    public boolean initExtra(ConzillaKit kit)
    {
	this.kit = kit;
	return true;
    }
    
    public void exitExtra() {}
    public void addExtraFeatures(MapController c, Object o, String loc, String hint) {}
    public void refreshExtra() {}
    public boolean saveExtra()
    {
	return true;
    }

    public void extendMenu(ToolsMenu menu, final MapController c)
    {
	if(menu.getName().equals(MenuFactory.SETTINGS_MENU))
	    {
		Tool t = new Tool("FONTS", FontExtra.class.getName())
		    {
			public void actionPerformed(ActionEvent ae)
			{
			    popupFontDialog();
			}
		    };
		menu.addTool(t, 700);
	    }
    }
    

    void popupFontDialog()
    {
	String[] sizess = {
	    "7",
	    "8",
	    "9",
	    "10",
	    "11",
	    "12",
	    "13",
	    "14",
	    "15",
	    "16",
	    "17",
	    "18",
	    "19"};
	JComboBox sizes = new JComboBox(sizess);
	sizes.setSelectedIndex(Integer.parseInt(GlobalConfig.getGlobalConfig().getProperty(Conzilla.FONT_SIZE_PROP, "10")) - 7);
	
	final JDialog d = new JDialog((Frame) null,
				      ConzillaResourceManager.getDefaultManager().getString(FontExtra.class.getName(),
											    "SELECT_FONT"), true);

	JPanel cp = new JPanel();

	cp.setBorder(new EmptyBorder(20, 20, 20, 20));
	cp.setLayout(new BorderLayout());
	JLabel fs = new JLabel(ConzillaResourceManager.getDefaultManager().getString(FontExtra.class.getName(),
										     "FONT_SIZE")+ ": ");
	JLabel ex1 = new JLabel(ConzillaResourceManager.getDefaultManager().getString(FontExtra.class.getName(),
										     "EXAMPLE_BUTTON")+ ": ");
	JLabel ex2 = new JLabel(ConzillaResourceManager.getDefaultManager().getString(FontExtra.class.getName(),
										     "EXAMPLE_TEXT")+ ": ");
	JLabel warning = new JLabel(ConzillaResourceManager.getDefaultManager().getString(FontExtra.class.getName(),
											  "WARNING"));

	final JButton but = new JButton("ABCabc"); 
	final JTextArea text = new JTextArea("ABCabc");
	
	JPanel labelPane = new JPanel();
	labelPane.setLayout(new GridLayout(0, 1));
	labelPane.add(fs);
	labelPane.add(ex1);
	labelPane.add(ex2);
	JPanel fieldPane = new JPanel();
	fieldPane.setLayout(new GridLayout(0, 1));
	fieldPane.add(sizes);
	fieldPane.add(but);
	fieldPane.add(text);

	cp.add(labelPane, BorderLayout.CENTER);
	cp.add(fieldPane, BorderLayout.EAST);
	cp.add(warning, BorderLayout.SOUTH);

	final String[] font = new String[1];
	
	sizes.addItemListener(new ItemListener()
	    {
		public void itemStateChanged(ItemEvent e)
		{
		    if(e.getStateChange() == ItemEvent.SELECTED)
			{
			    font[0] = (String) e.getItem();
			    int fs = Integer.parseInt(font[0]);
			    Font f = new Font("Lucida Sans", Font.BOLD, fs);
			    Font f2 = f.deriveFont(Font.PLAIN, (float) (f.getSize2D()*1.2));
			    but.setFont(f);
			    text.setFont(f2);
			}
		}
	    });

	JPanel contentPane = new JPanel();
	contentPane.setLayout(new BorderLayout());
	contentPane.add(cp, BorderLayout.CENTER);

	JPanel buts = new JPanel();
	buts.setLayout(new FlowLayout(FlowLayout.RIGHT));
	JButton ok = new JButton();
	ConzillaResourceManager.getDefaultManager().customizeButton(ok, FontExtra.class.getName(), "OK");
	JButton cancel = new JButton();
	ConzillaResourceManager.getDefaultManager().customizeButton(cancel, FontExtra.class.getName(), "CANCEL");
	buts.add(ok);	
	buts.add(cancel);
	
	contentPane.add(buts, BorderLayout.SOUTH);

	ok.addActionListener(new ActionListener()
	    {
		public void actionPerformed(ActionEvent e)
		{
		    d.hide();
		}
	    });
	cancel.addActionListener(new ActionListener()
	    {
		public void actionPerformed(ActionEvent e)
		{
		    font[0] = null;
		    d.hide();
		}
	    });
	d.setContentPane(contentPane);
	d.pack();
	d.show();
	d.dispose();
	if(font[0] != null)
	    {
		kit.getConzilla().setGlobalFontSize(Integer.parseInt(font[0]));
	    }
    }
}
