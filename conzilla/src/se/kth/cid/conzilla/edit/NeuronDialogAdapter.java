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
import java.awt.*;


/** This class holds the basic functionality for NeuronDialogs,
 *  it is recomended to inherit from this class instead of
 *  directly implement the NeuronDialog interface.
 *
 *  @see NeuronDialog
 */
public class NeuronDialogAdapter extends JPanel implements NeuronDialog 
{
  Neuron neuron=null;
  NeuronType neuronType=null;
  
  protected String directoryExtension="/ne/";
  String uri;

  MetaData metaData;
  MetaData data;
  MetaDataDisplayerComponent mdisplayer;
  DataDisplayerComponent ddisplayer;
  JTextArea uriArea;
  JTextArea typeUriArea;
  
  ConzKit kit;
  EditListener editListener;

  public NeuronDialogAdapter(ConzKit kit)
    {
      this.kit=kit;
      metaData=(MetaData) new DummyMetaData();
      data=(MetaData) new DummyMetaData();           //exactly the same functionality as data should have.
      mdisplayer=new MetaDataDisplayerComponent();
      ddisplayer=new DataDisplayerComponent();
      uriArea=new JTextArea("");
      typeUriArea=new JTextArea("");
      setLayout(new GridBagLayout());
      
      GridBagConstraints c = new GridBagConstraints();
      c.gridx = 0;
      c.fill = GridBagConstraints.BOTH;
    
      add(new JLabel("URI:"), c);
      add(uriArea, c);
      add(new JLabel("TypeURI:"),c);
      add(typeUriArea, c);
      add(new JLabel("MetaData:"), c);
      add(mdisplayer, c);
      add(new JLabel("Data:"), c);
      add(ddisplayer, c);

      mdisplayer.showMetaData(null);
      ddisplayer.showData(null, null);
      editListener = new EditListener()
	{
	  public void componentEdited(EditEvent e)
	    {
	      if(e.getEditType() ==
		 se.kth.cid.component.Component.EDITABLE_CHANGED)
		updateEditable();
	    }
	};
    }
  private void updateEditable()
    {
      if (neuron.isEditable())
	{
	  //do something.
	}
    }
  
  public String extractBaseUri(URI uri)
    {
      //A not very nice try to find the base-uri for the neurons in the conceptmap.
      //Needs some forma of standardization of directory structure....
      //Should maybee be separated into a separate configurable class.
      String suri=uri.toString();
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
  
  public void hintBaseUri(String buri)
    {
      uri=buri;
    }

  public void hintMetaData(String tag, String value)
    {
      try{
	metaData.setValue(tag, value);
      } catch (ReadOnlyException e)
	{
	  Tracer.trace("Unable to set MetaData on neuron " + uri +
		       "!" + e.getMessage(), Tracer.MINOR_INT_EVENT);
	}
    }
  public void hintData(String tag, String value)
    {
      try{
	data.setValue(tag, value);
      } catch (ReadOnlyException e)
	{
	  Tracer.trace("Unable to set data on neuron " + uri +
		       "!" + e.getMessage(), Tracer.MINOR_INT_EVENT);
	}
    }
  
  public Neuron getNeuron()
    {
      return neuron;
    }

  public URI    getNeuronURI()
    {
      if (neuron==null)
	return null;
      try { return new URI(neuron.getURI());
      } catch (MalformedURIException e) { return null; }
    }
  
  public void setNeuron(Neuron neuron)
    {      
      neuronType=null;
      if (neuron!=null)
	{
	  uriArea.setText(neuron.getURI());
	  typeUriArea.setText(neuron.getType());
	  try{
	    URI uri=new URI(neuron.getType());
	    se.kth.cid.component.Component comp=kit.loader.loadComponent(uri, kit.loader);
	    if (comp instanceof NeuronType)
	      neuronType=(NeuronType) comp;
	  } catch (Exception e) {}
	}
      if (this.neuron!=null)
	neuron.removeEditListener(editListener);
      this.neuron=neuron;
      neuron.addEditListener(editListener);
      mdisplayer.showMetaData(neuron);
      ddisplayer.showData(neuron, neuronType);
      updateEditable();
    }
  public NeuronDialog copy()
    {
      NeuronDialogAdapter nda=new NeuronDialogAdapter(kit);
      //copy stuff...
      return nda;
    }
}
