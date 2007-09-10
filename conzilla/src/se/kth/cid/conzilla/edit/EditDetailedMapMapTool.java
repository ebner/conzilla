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


package se.kth.cid.conzilla.edit;
import se.kth.cid.util.*;
import se.kth.cid.library.ClipboardLibrary;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.identity.*;
import se.kth.cid.component.*;
import javax.swing.*;
import javax.swing.border.*;
import java.beans.*; //Property change stuff
import java.awt.*;
import java.awt.event.*;

/** 
 *  @author Matthias Palmèr
 *  @version $Revision$
 */

public class EditDetailedMapMapTool extends ActionMapMenuTool
{
  class DMDialog extends JDialog {

    URIField textField;
    MapObject mapObject;

    public DMDialog()
      {
	super(JOptionPane.getFrameForComponent(controller.getMapScrollPane()),
	      "DetailedMap edit", true);

	
	JPanel content = new JPanel();
	content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
	JLabel l1 = new JLabel("Please type a URI for the detailed map.");
	l1.setForeground(Color.black);
	JLabel l2 = new JLabel(" ");
	JLabel l3 = new JLabel("Leaving this field empty means that the");
	JLabel l4 = new JLabel("component will have no detailed map.");
	JLabel l5 = new JLabel("Note that relative URIs do work.");
	JLabel l6 = new JLabel(" ");
	content.setBorder(new EmptyBorder(20, 20, 20, 20));

	textField = new URIField(30, null);
	Dimension pref = textField.getPreferredSize();
	textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));
	content.add(l1);
	content.add(l2);
	content.add(l3);
	content.add(l4);
	content.add(l5);
	content.add(l6);
	content.add(new MetaDataFieldPanel("URI: ", textField));
	content.add(Box.createVerticalGlue());
	

	JButton ok = new JButton("Ok");
	JButton cancel = new JButton("Cancel");
	JButton revert = new JButton("Revert");
	JButton paste = new JButton("Paste");
	JButton test = new JButton("Test");

	JToolBar bar = new JToolBar();
	bar.setFloatable(false);
	bar.setBorder(new EmptyBorder(10, 30, 10, 30));
	bar.add(revert);
	bar.add(paste);
	bar.add(test);
	bar.add(Box.createHorizontalGlue());
	bar.add(ok);
	bar.add(cancel);
	

	JPanel cPane = new JPanel();
	setContentPane(cPane);
	cPane.setLayout(new BorderLayout());
	cPane.add(content, BorderLayout.CENTER);
	cPane.add(bar, BorderLayout.SOUTH);
	cPane.setMinimumSize(cPane.getPreferredSize());
	
	
	setDefaultCloseOperation(HIDE_ON_CLOSE);

	pack();

	// A return is the same as 'Set'.
	textField.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      set();
	    }
	  });
	  
	ok.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      set();
	    }});
	cancel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      hide();
	    }});
	revert.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      setMapObject(mapObject);
	    }});
	paste.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      ClipboardLibrary cl = controller.getConzillaKit().getRootLibrary().getClipboardLibrary();
	      if (cl.getComponent() != null)
		textField.setText(cl.getComponent().getURI());
	    }});
	test.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      if(textField.getText().equals(""))
		{
		  ErrorMessage.showError("URI Error",
					 "Cannot show empty URI!",
					 null, DMDialog.this);
		  return;
		}
	      
	      try {
		controller.getConzillaKit().getConzilla().openMapInNewView(textField.getURI(), controller);	
	      } catch(MalformedURIException me)
		{
		  ErrorMessage.showError("Parse Error", "Invalid URI", me,
					 DMDialog.this);
		  return;
		}
	      catch(ControllerException me)
		  {
		      ErrorMessage.showError("Cannot load map", "Cannot load map", me,
					     DMDialog.this);
		      return;
		  }
	    }});
      }

    public void show()
      {
	setLocationRelativeTo(controller.getMapScrollPane());
	Tracer.debug("Loc: " + getLocation());
	pack();
	super.show();
      }
    
    void set()
      {
	try {
	  if(textField.getText().equals(""))
	    mapObject.getNeuronStyle().setDetailedMap(null);
	  else
	    {
	      try {
		String reluri = textField.getRelativeURI(false);
		mapObject.getNeuronStyle().setDetailedMap(reluri);
	      } catch(MalformedURIException e)
		{
		  ErrorMessage.showError("Parse Error", "Invalid URI", e, this);
		  return;
		}
	    }
	  hide();
	} catch(InvalidURIException e)
	  {
	    Tracer.bug("Invalid URI: " + e.getMessage());
	  }
      }
    
    public void setMapObject(MapObject mapObject)
      {
	this.mapObject = mapObject;
	textField.setText(mapObject.getNeuronStyle().getDetailedMap());
	textField.setBaseURI(URIClassifier.parseValidURI(mapObject.getNeuronStyle().getConceptMap().getURI()));
      }
  }

  DMDialog dialog;

  public EditDetailedMapMapTool(MapController cont)
    {
      super("EDIT_DETAILEDMAP", EditMapManagerFactory.class.getName(), cont);
      dialog=new DMDialog();
    }
  
  protected boolean updateEnabled()
    {
      if (mapEvent.hitType != MapEvent.HIT_NONE)
	  return true;
      return false;
    }

  public void actionPerformed(ActionEvent e)
    {
      dialog.setMapObject(mapObject);
      dialog.show();
    }
}

