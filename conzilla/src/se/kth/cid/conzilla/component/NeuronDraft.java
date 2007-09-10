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


package se.kth.cid.conzilla.component;
import javax.swing.*;
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.component.local.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.library.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.identity.*;
import se.kth.cid.conzilla.metadata.*;
import java.util.*;
import java.awt.event.*;


/** This class holds the basic functionality for creating new neurons,
 *  it is recomended to inherit from this class.
 *
 *  @see ComponentDraft
 */
public class NeuronDraft extends ComponentDraft implements MenuLibraryListener
{
  NeuronType  neuronType = null;

  Vector dataValues;
  
  URIField typeURIField;

  static Neuron lastTemplate;

  GenericLibraryMenuWrapper glmw;

  JMenu typeMenu;

  public NeuronDraft(ConzillaKit kit, java.awt.Component parent)
    {
      super(kit, parent);
      
      dataValues = new Vector();
      
      okButton.setEnabled(false);
      if(lastTemplate != null)
	hintsFromComponent(lastTemplate);
    }

  protected void setAttributes()
    {
      super.setAttributes();

      // -------------Neuron-Type----------------
      typeURIField = new URIField(30, null);
      typeURIField.setEditable(false);
      
      /*    typeURIField.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    typeURIField.getNextFocusableComponent().requestFocus();
	  }});
      */

      se.kth.cid.library.TemplateLibrary template = kit.getConzillaEnvironment().getRootLibrary().getTemplateLibrary();
      glmw = new GenericLibraryMenuWrapper(kit.getComponentStore(), "No type selected", template, this);
      typeMenu = glmw.getMenu();

      final JMenuBar mBar = new JMenuBar();
      mBar.add(typeMenu);
      typeMenu.setBorder(BorderFactory.createEtchedBorder());
      mBar.setBorder(null);
 
      panel.addPanel("Type", mBar);
      panel.addPanel("Type URI", typeURIField);
    }

  protected void setFocusBehaviour()
    {
      super.setFocusBehaviour();

      /*      componentURIField.setNextFocusableComponent(typeURIField);
      typeURIField.setNextFocusableComponent(okButton);
      */
    }

  public void hintsFromComponent(se.kth.cid.component.Component comp)
    {
      super.hintsFromComponent(comp);

      if (!(comp instanceof Neuron))
	return;

      Neuron neuron = (Neuron) comp;
      hintNeuronType(neuron);

      Neuron.DataValue[] v = neuron.getDataValues();
      for(int i = 0; i < v.length; i++)
	dataValues.add(v[i]);
      
    }

  public void hintNeuronType(Neuron n)
    {
      okButton.setEnabled(true);
      lastTemplate = n;
      String name = MetaDataUtils.getLocalizedString(n.getMetaData().get_metametadata_language(), 
						     n.getMetaData().get_general_title()).string;
      if (name.equals(""))
	name = "[Unknown type name]";

      typeMenu.setText(name);
      typeURIField.setText(n.getType());
    }

  public void hintData(String tag, String value)
    {
      dataValues.add(new Neuron.DataValue(tag, value));
    }
  
  public void selected(Neuron neuron)
  {
    hintNeuronType(neuron);
  }


  Component createComponent(URI uri, URI createURI, MIMEType type) throws ComponentException, MalformedURIException
    {
      URI typeURI = typeURIField.getURI();
      
      NeuronType nType = kit.getComponentStore().getAndReferenceNeuronType(typeURI);
      
      Neuron n = kit.getComponentStore().getHandler().createNeuron(uri, createURI, type, typeURI);
      fixData(n, nType);
      return n;
    }
  
  void fixData(Neuron n, NeuronType nt) throws ReadOnlyException
    {
      String[] tags = nt.getDataTags();
      
      for(int i = 0; i < dataValues.size();)
	{
	  boolean legal = false;
	  for(int j = 0; j < tags.length; j++)
	    if(tags[j].equals(((Neuron.DataValue) dataValues.get(i)).tag))
	      {
		legal = true;
		break;
	      }
	  if(legal)
	    i++;
	  else
	    dataValues.removeElementAt(i);
	}
	  
      Neuron.DataValue[] v = (Neuron.DataValue[]) dataValues.toArray(new Neuron.DataValue[dataValues.size()]);
      n.setDataValues(v);
    }
}
