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
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.identity.*;
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import se.kth.cid.library.*;
import se.kth.cid.component.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class GenericLibraryMenuWrapper
{

    class GenericLibraryMenuNode extends JMenu implements MenuListener
    {
	Neuron neuron;
	ComponentStore store;
	MenuLibraryListener listener;

	public GenericLibraryMenuNode(ComponentStore store, String name, Neuron neuron, MenuLibraryListener listener)
	{
	    super(name);
	    this.neuron=neuron;
	    this.store=store;
	    this.listener=listener;
	    
	    addMenuListener(this);
	}
	public void fetchLibrary()
	{
	    try {
		setLibrary(new GenericLibrary(store, URIClassifier.parseValidURI(neuron.getURI())));
	    } catch (LibraryException le) {
		Tracer.debug("Couldn't load sub generic library. Menu will be empty."+
			     le.getMessage());
	    }
	}

	public void setLibrary(GenericLibrary gl)
	{
	    removeAll();
	    Neuron [] neurons=gl.getNeurons();
	    for (int i=0;i<neurons.length;i++)
		{
		  final Neuron n = neurons[i];
		  MetaData md = n.getMetaData();
		  String language = md.get_metametadata_language();
		  String name=MetaDataUtils.getLocalizedString(language, 
							       md.get_general_title()).string;
		    if (name.equals(""))
			name = n.getType();
		    AbstractAction aa = new AbstractAction(name) {
			public void actionPerformed(ActionEvent e)
			    {
				listener.selected(n);
			    }};
		    JMenuItem mi = add(aa);

		    MetaData.LangStringType[] desc = md.get_general_description();
		    if (desc != null && desc.length > 0)
			mi.setToolTipText(MetaDataUtils.getLocalizedString(language, desc[0]).string);
		}
	    neurons=gl.getSubLibraries();
	    for (int i=0;i<neurons.length;i++)
		{
		    String name=MetaDataUtils.getLocalizedString(neurons[i].getMetaData().get_metametadata_language(), 
								 neurons[i].getMetaData().get_general_title()).string;
		    add(new GenericLibraryMenuNode(store,name, neurons[i], listener));
		}
	}
	public void menuSelected(MenuEvent e)
	{
	    fetchLibrary();
	}
	public void menuDeselected(MenuEvent e) {}
	public void menuCanceled(MenuEvent e) {}
    }


    GenericLibraryMenuNode glmn;
    
    public GenericLibraryMenuWrapper(ComponentStore store,String name, 
				     GenericLibrary gl, 
				     MenuLibraryListener listener)
    {
	glmn=new GenericLibraryMenuNode(store, name, gl.getLibraryNeuron(), listener);
    }

    public JMenu getMenu()
    {
	return glmn;
    }
}
	
