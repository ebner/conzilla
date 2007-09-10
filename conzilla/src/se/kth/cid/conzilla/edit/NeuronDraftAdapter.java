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
import javax.swing.*;
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.component.local.*;
import se.kth.cid.conzilla.center.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.data.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;


/** This class holds the basic functionality for NeuronDrafts,
 *  it is recomended to inherit from this class instead of
 *  directly implement the NeuronDraft interface.
 *
 *  @see NeuronDraft
 */
public class NeuronDraftAdapter extends JPanel implements NeuronDraft
{
  Neuron neuron=null;
  URI    neuronURI=null;
  URI    typeURI=null;

  Vector neuronDraftListeners;

  protected String directoryExtension="/ne/";

  TagValue metaData;
  TagValue data;
  final JTextField nameTipField;
  final JLabel nameTipLabel;
  final JTextField neuronUriField;
  final JTextField typeUriField;
  final JLabel neuronUriLabel, typeUriLabel;

  JButton okButton, cancelButton;

  ConzKit kit;

  public NeuronDraftAdapter(ConzKit kit)
    {
      this.kit=kit;

      neuronDraftListeners=new Vector();
      metaData=new TagValue();
      data=new TagValue();
      neuronUriLabel=new JLabel("URI:");
      typeUriLabel=new JLabel("TypeURI:");
      neuronUriLabel.setForeground(Color.red);
      typeUriLabel.setForeground(Color.red);

      nameTipLabel=new JLabel("Title sugestion");

      nameTipField=new JTextField("");

      neuronUriField=new JTextField("");
      neuronUriField.addFocusListener(new FocusAdapter()
			       {public void focusLost(FocusEvent e)
				 {investigateNeuronURI();}
			       public void focusGained(FocusEvent e)
				 {neuronUriLabel.setForeground(Color.yellow);}});
      typeUriField=new JTextField("");
      typeUriField.addFocusListener(new FocusAdapter()
				    {
				      public void focusLost(FocusEvent e)
					{investigateTypeURI();}
				      public void focusGained(FocusEvent e)
					{typeUriLabel.setForeground(Color.yellow);}});


      nameTipField.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  final String str=nameTipField.getText();
	  NeuronDraftAdapter.this.metaData.removeTag("Title");
	  NeuronDraftAdapter.this.metaData.setValue("Title", str);
	  neuronUriField.setText(neuronUriField.getText()+str.replace(' ', '_'));
	  neuronUriField.requestFocus();
	  neuronUriField.setCaretPosition(neuronUriField.getText().length());

      }});



      okButton=new JButton("OK");
      okButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e)
	  {
	    makeNeuron();}});
      cancelButton=new JButton ("Cancel");
      cancelButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e)
	  {cancel();}});


      setLayout(new GridBagLayout());

      GridBagConstraints c = new GridBagConstraints();
      c.gridx = 0;
      c.gridwidth=GridBagConstraints.REMAINDER;
      c.fill = GridBagConstraints.BOTH;
      add(nameTipLabel, c);
      add(nameTipField, c);
      add(neuronUriLabel, c);
      add(neuronUriField, c);
      add(typeUriLabel,c);
      add(typeUriField, c);
      c.gridwidth=1;
      add(cancelButton, c);
      c.gridx = 1;
      add(okButton, c);
    }
  protected void finalize() throws Throwable
    {
      super.finalize();
      if (neuron!=null)
	kit.loader.releaseComponent(neuron);
    }

  protected URI investigateNeuronURI()
    {
      neuronUriLabel.setForeground(Color.red);
      try {
	URI uri=new URI(neuronUriField.getText());
	if (kit.saver.isURISavable(uri) &&
	    !kit.saver.doComponentExist(uri))
	  {
	    neuronUriLabel.setForeground(Color.green);
	    return uri;
	  }
      } catch (MalformedURIException me) {}
      return null;
    }

  protected URI investigateTypeURI()
    {
      typeUriLabel.setForeground(Color.red);
      try{
	URI uri=new URI(typeUriField.getText());
	se.kth.cid.component.Component comp=kit.loader.loadComponent(uri, kit.loader);
	kit.loader.releaseComponent(comp);
	if (!(comp instanceof NeuronType))
	  return null;
	typeUriLabel.setForeground(Color.green);
	return uri;
      } catch (MalformedURIException me) {
      } catch (ComponentException ce) {}
      return null;
    }
  public void    addListener(NeuronDraftListener ndl)
    {
      neuronDraftListeners.addElement(ndl);
    }
  public boolean removeListener(NeuronDraftListener ndl)
    {
      return neuronDraftListeners.removeElement(ndl);
    }

  public void hintsFromNeuron(Neuron ne)
    {
      hintBaseURI(ne.getURI(), true);
      hintNeuronType(ne.getType());
      String [] tags=ne.getDataTags();
      for (int i=0;i<tags.length;i++)
	{
	  String [] values=ne.getDataValues(tags[i]);
	  for (int j=0;j<values.length;j++)
	    data.setValue(tags[i], values[j]);
	}
      MetaData md=ne.getMetaData();
      tags=md.getTags();
      for (int k=0;k<tags.length;k++)
	metaData.setValue(tags[k],md.getValue(tags[k]));
      Enumeration en=metaData.getValues("Title");
      if (en.hasMoreElements())
	{
	  nameTipField.setText((String) en.nextElement());
	  nameTipField.requestFocus();
	  nameTipField.selectAll();
	  nameTipField.setCaretPosition(nameTipField.getText().length());
	}

    }
  public void hintBaseURI(String buri, boolean stripToBase)
    {
      if (stripToBase)
	neuronUriField.setText(extractBaseUri(buri));
      else
	neuronUriField.setText(buri);
      investigateNeuronURI();
    }
  public void hintNeuronType(String type)
    {
      typeUriField.setText(type);
      if (type.indexOf("contentdescription")!=-1)
	directoryExtension="/cd/";
      investigateTypeURI();
    }
  public void hintMetaData(String tag, String value)
    {
      metaData.setValue(tag, value);
    }
  public void hintData(String tag, String value)
    {
      data.setValue(tag, value);
    }
  public Neuron getNeuron()
    {
      if (neuron!=null)
	return neuron;

      neuronURI=investigateNeuronURI();
      typeURI=investigateTypeURI();
      if (neuronURI==null || typeURI==null)
	return null;
      // Now, this is not nice, maybe a neuronfactory somewhere..
      neuron=(Neuron) new se.kth.cid.neuron.local.LocalNeuron();
      try {
	neuron.setType(typeURI.toString());
	neuron.setURI(neuronURI.toString());
	fixMetaData(neuron.getMetaData());
	fixData(neuron);
	kit.saver.saveComponent(neuron);
	//Now we throw away the saved neuron, and hencefort
	//use the loader to fetch it the proper way.
	//Sensible?
	//Alternatively a new function in ComponentLoader
	//would be neded, prefferably called addComponent.
	//(Needed for the cases when the loader is a cash.)
	neuron= (Neuron) kit.loader.loadComponent(neuronURI, kit.loader);
	return neuron;
      } catch (ReadOnlyException re) {
      } catch (MalformedURIException me) {
      } catch (ComponentException ce) {}
      neuron=null;
      return null;
    }
  public URI getNeuronURI()
    {
      return neuronURI;
    }
  public URI getTypeURI()
    {
      return typeURI;
    }

  private void fixMetaData(MetaData md) throws ReadOnlyException
    {
      Enumeration en=metaData.getTags();
      for (;en.hasMoreElements();)
	{
	  String tag=(String) en.nextElement();
	  Enumeration ten=metaData.getValues(tag);
	  if (ten.hasMoreElements())
	    {
	      String value=(String) ten.nextElement();
	      md.setValue(tag, value);
	    }
	}
    }
  private void fixData(Neuron tn) throws ReadOnlyException
    {
      Tracer.debug("fixData");
      Enumeration en=data.getTags();
      for (;en.hasMoreElements();)
	{
	  Tracer.debug("fixData2");
	  String tag= (String) en.nextElement();
	  Enumeration ten=data.getValues(tag);
	  if (ten.hasMoreElements())
	    tn.addDataValue(tag, (String) ten.nextElement());
	}

    }
  protected void makeNeuron()
    {
      if (neuron!=null)  // Listeners should only be notifyed
	return;          // once per created neuron.
      if (getNeuron()==null)
	return;

      Object [] fulimul=new Object[neuronDraftListeners.size()];
      neuronDraftListeners.copyInto(fulimul);
      for (int i=0;i<fulimul.length;i++)
	  ((NeuronDraftListener) fulimul[i]).madeNeuron(this);
    }
  public void cancel()
    {
      Object [] fulimul=new Object[neuronDraftListeners.size()];
      neuronDraftListeners.copyInto(fulimul);
      for (int i=0;i<fulimul.length;i++)
	((NeuronDraftListener) fulimul[i]).neuronCanceled(this);
    }

  protected String extractBaseUri(String suri)
    {
      //TODO(MP):
      //A not very nice try to find the base-uri for the neurons in the conceptmap.
      //Needs some forma of standardization of directory structure....
      //Should maybee be separated into a separate configurable class.
      int slashpos2=suri.lastIndexOf('/');
      int slashpos1=suri.substring(0,slashpos2-1).lastIndexOf('/');
      if (suri.substring(slashpos1+1,slashpos2).equals("cm") ||
	  suri.substring(slashpos1+1,slashpos2).equals("cd") ||
	  suri.substring(slashpos1+1,slashpos2).equals("ne") ||
	  suri.substring(slashpos1+1,slashpos2).equals("nt"))
	suri=suri.substring(0,slashpos1)+directoryExtension;
      else
	suri=suri.substring(0,slashpos2+1);
      return suri;
    }
}
