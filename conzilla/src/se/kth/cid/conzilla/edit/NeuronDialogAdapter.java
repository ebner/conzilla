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
import java.awt.event.*;


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

  MetaDataDisplayerComponent mdisplayer;
  DataDisplayerComponent ddisplayer;
  JTextField neuronUriField;
  JTextField typeUriField;
  
  ConzKit kit;
  EditListener editListener;

  boolean isEditable=false;
  
  public NeuronDialogAdapter(ConzKit kit)
    {
      this.kit=kit;

      mdisplayer=new MetaDataDisplayerComponent();
      ddisplayer=new DataDisplayerComponent();
      neuronUriField=new JTextField("");
      typeUriField=new JTextField("");
      setLayout(new GridBagLayout());
      
      GridBagConstraints c = new GridBagConstraints();
      c.gridx = 0;
      c.fill = GridBagConstraints.BOTH;
    
      add(new JLabel("URI:"), c);
      add(neuronUriField, c);
      add(new JLabel("TypeURI:"),c);
      add(typeUriField, c);
      add(new JLabel("MetaData:"), c);
      add(mdisplayer, c);
      final JLabel label=new JLabel("Data:");
      label.addMouseListener(new MouseAdapter()
	    {
	      public void mousePressed(MouseEvent e)
	      {
		if(e.isPopupTrigger() && isEditable)
		  {
		    ddisplayer.getMenu().show(label, e.getX(), e.getY());
		    e.consume();
		  }
	      }
	    });
      add(label, c);
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
      isEditable=neuron.isEditable();
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
	  neuronUriField.setText(neuron.getURI());
	  typeUriField.setText(neuron.getType());
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
      //maybe copy stuff...
      return nda;
    }
}
