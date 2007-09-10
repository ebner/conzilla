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

package se.kth.cid.conzilla.library;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.library.*;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.beans.*; //Property change stuff
import java.awt.*;
import java.awt.event.*;

class CreateNeuronDialog extends JDialog {

  private JOptionPane optionPane;
  private TemplateCommandTool destination;
  private NeuronStyle neuronstyle;
  private MapController controller;
  private Object[] array=null;
  private JTextField title;
  private JTextField baseuri;
  private JTextField uriname;
  private JLabel ltitle;
  private JLabel lbaseuri;
  private JLabel luriname;

  
  public void setNeuronStyle(NeuronStyle ns)
    {
      String str=ns.getTitle();
      setTitle("Create neuron from template "+ str);
      neuronstyle=ns;

      String uri=baseuri.getText();
      
      int index=uri.lastIndexOf("/cd/");
      if (index!=-1 && str.toLowerCase().indexOf("contentdescription")==-1)
	baseuri.setText(uri.substring(0,index)+"/ne/");

      index=uri.lastIndexOf("/ne/");
      if (index!=-1 && str.toLowerCase().indexOf("contentdescription")!=-1)
	baseuri.setText(uri.substring(0,index)+"/cd/");


	
      pack();
    }
  public void abortDialog()
    {
      pack();
      validate();
      setVisible(false);
      optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
    }
  
  public CreateNeuronDialog(Frame aFrame, String uri, TemplateCommandTool tct,
			    MapController contr) {
    super(aFrame, false);
    setLocationRelativeTo(aFrame);
    destination=tct;
    neuronstyle=null;
    controller=contr;

    int index=uri.lastIndexOf("/cm/");
    if (index!=-1)
      uri=uri.substring(0,index)+"/ne/";
    
    title = new JTextField("", 10);
    baseuri = new JTextField(uri, 10);
    uriname = new JTextField("", 10);
    ltitle=new JLabel("Title:");
    lbaseuri=new JLabel("URIBase:");
    luriname=new JLabel("URIName:");
    
    array = new Object[7];
    array[0]="Template choosen! \n";
    array[1]="Now type in a suitable Title for it.";
    array[2]="If nothing is given in the neuron-field";
    array[3]="a suitable string will be generated";
    final JPanel ptitle=new JPanel();
    ptitle.setLayout(new BorderLayout());
    ptitle.add(ltitle, BorderLayout.WEST);
    ptitle.add(title, BorderLayout.CENTER);
    array[4]=ptitle;
    final JPanel pbaseuri=new JPanel();
    pbaseuri.setLayout(new BorderLayout());
    pbaseuri.add(lbaseuri, BorderLayout.WEST);
    pbaseuri.add(baseuri, BorderLayout.CENTER);
    array[5]=pbaseuri;

    final JPanel puriname=new JPanel();
    puriname.setLayout(new BorderLayout());
    puriname.add(luriname, BorderLayout.WEST);
    puriname.add(uriname, BorderLayout.CENTER);
    array[6]=puriname;

    
    final String btnString1 = "OK";
    final String btnString2 = "Cancel";
    Object[] options = {btnString1, btnString2};

    optionPane = new JOptionPane(array, 
				 JOptionPane.QUESTION_MESSAGE,
				 JOptionPane.YES_NO_OPTION,
				 null,
				 options,
				 options[0]);
    setContentPane(optionPane);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    pack();
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
	optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
      }
    });

    title.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	String str=title.getText();
	uriname.setText(str.replace(' ', '_'));
	baseuri.requestFocus();
	baseuri.setCaretPosition(baseuri.getText().length());
	
      }});

    baseuri.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	uriname.requestFocus();
	uriname.setCaretPosition(uriname.getText().length());	
      }
    });

    uriname.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	optionPane.setValue(btnString1);
      }
    });
    
    optionPane.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent pe) {
	String prop = pe.getPropertyName();

	if (isVisible() 
	    && (pe.getSource() == optionPane)
	    && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
		prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
	  Object value = optionPane.getValue();

	  if (value == JOptionPane.UNINITIALIZED_VALUE) {
	    //ignore reset
	    return;
	  }
	  if (value.equals(btnString2))
	      {
		destination.adoptNeuron(null);
		abortDialog();
		return;	
	      }
	  if (value.equals(btnString1)) {
	    Tracer.debug(btnString1+" pressed");
	    try {
	      URI newuri=new URI(baseuri.getText()+uriname.getText());
	      
	      neuronstyle.setURI(newuri);
	      ComponentSaver csaver=controller.getComponentSaver();
	      if (neuronstyle.getNeuron().isEditingPossible() && csaver!=null &&
		  csaver.isComponentSavable(neuronstyle.getNeuron()))
		{
		  //checking if uri already exists...
		  //This is really ugly!!!!!!!
		  if (!csaver.doComponentExist(newuri))
		    {
		      Tracer.debug("CreatNeuronDialog, everything is ok.");
		      neuronstyle.setTitle(title.getText());
		      neuronstyle.getNeuron().getMetaData().setValue("title", title.getText());
		      destination.adoptNeuron(neuronstyle);
		      abortDialog();
		      return;
		    }
		  else
		    {
		    JOptionPane.showMessageDialog(CreateNeuronDialog.this,
						  "This URI already exists."
						  +"Try another one!","Error message",
					  JOptionPane.ERROR_MESSAGE);
		    }
		}
	      else
		{
		  JOptionPane.showMessageDialog(CreateNeuronDialog.this,
						"This URI can't be saved."+
						"Try another one.","Error message",
						JOptionPane.ERROR_MESSAGE);		  
		}
	    } catch (MalformedURIException me)
	      {
		JOptionPane.showMessageDialog(CreateNeuronDialog.this,
					      "String is not a qualified URI."+
					      "Try another one!","Error message",
					  JOptionPane.ERROR_MESSAGE);			      
	      }
	    // reset the JOptionPane's value
	    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
	  } else
	    Tracer.debug("CreatNeuronDialog, something is seriously wrong here.");
	}
      }
    });
  }
}

