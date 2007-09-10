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

package se.kth.cid.protege;

import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.view.*;
import se.kth.cid.conzilla.layout.*;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.identity.*;
import se.kth.cid.component.tmp.*;
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import edu.stanford.smi.protege.widget.*;
import edu.stanford.smi.protege.model.*;
import javax.swing.border.*;

/** A TabWidget plugin to protege, launches conzilla with a SingleViewManager pane as tab.
 *  Uses LayoutViaGraphViz to do layout.
 *  Currently chooses a subset of all frames (OKBC notion) for display, 
 *  i.e. all non system classes and their own-slot-bindings 
 *  (only :DIRECT-TYPE and :DIRECT-SUPERCLASSES).
 *  The system classes :STANDARD-CLASS and :STANDARD-SLOT is also shown. 
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class ConzillaTabWidget extends AbstractTabWidget
{ 

    ConzillaKit kit;
    Conzilla conzilla;
    ComponentHandler handler;
    MIMEType tmp;
    int relationcount = 0;
    ProtegeHelper protegeHelper;
    Slot directTypeSlot;
    Slot directSubClasses;
    Slot directSuperClasses;
    Slot standardSlot;

    public ConzillaTabWidget()
    {
	ConzillaProtege cp = new ConzillaProtege();
	kit = cp.getConzillaKit();
	conzilla  = kit.getConzilla();
    }
 // startup code
    public void initialize() {
        // initialize the tab text
        setLabel("Conzilla Tab");
	
	tmp = TmpFormatHandler.TMP;
	URI cMapURI = URIClassifier.parseValidURI("tmp:/cmap1");
        handler = kit.getComponentStore().getHandler();
	ConceptMap cMap;
	try {
	    cMap = handler.createConceptMap(cMapURI, cMapURI, tmp);
	    kit.getComponentStore().getCache().referenceComponent(cMap);
	} catch (ComponentException ce)
	    {
		Tracer.debug("failed creating map. \n"+ce.getMessage());
		return;
	    }
	cMap.setDimension(new ConceptMap.Dimension(500,500));

	KnowledgeBase kb = getKnowledgeBase();
	protegeHelper = new ProtegeHelper(kb);     
	directTypeSlot = kb.getSlot(":DIRECT-TYPE");
	directSubClasses = kb.getSlot(":DIRECT-SUBCLASSES");
	directSuperClasses = kb.getSlot(":DIRECT-SUPERCLASSES");

	URI typeURI = URIClassifier.parseValidURI("urn:path:/org/conzilla/builtin/types/UML/ClassD/concept");
	//URI typeURI = URIClassifier.parseValidURI("urn:path:/org/conzilla/builtin/types/rdf/rdfs/Resource");

	JTextArea test = new JTextArea();
	test.setBorder(new EmptyBorder(0, 10, 0, 10));
	Hashtable neuron2frame = new Hashtable();
	
        Collection inss = kb.getInstances();
	Iterator it = inss.iterator();
	while (it.hasNext())
	    {
		Instance ins = (Instance) it.next();
		Tracer.debug("instance name "+ ins.getName());
		if (ins.isSystem())
		    continue;

		addNeuronToMap(ins,cMap, typeURI, neuron2frame);
	    }
	addNeuronToMap(kb.getInstance(":STANDARD-SLOT"), cMap, typeURI, neuron2frame);
	addNeuronToMap(kb.getInstance(":STANDARD-CLASS"), cMap, typeURI, neuron2frame);

	Enumeration en=neuron2frame.keys();
	while (en.hasMoreElements())
	    {
		Instance ins = (Instance) en.nextElement();
		Iterator it3 = getRelations(ins, protegeHelper.getURI(ins)).iterator();
		while (it3.hasNext())
		    {
			Neuron relation = (Neuron) it3.next();
			try {
			    NeuronStyle ns = cMap.addNeuronStyle(relation.getURI());
			    ns.setBodyVisible(false);
			    showAxons(cMap, ns, relation);
			} catch (InvalidURIException iue) {
			    Tracer.debug("failed creating neuronstyle for relation. \n"+iue.getMessage());
			    continue;
			}

		    } 
	    }
	
	Tracer.debug("Layout coming up!\n");
	SinglePaneManager viewManager = (SinglePaneManager) conzilla.getViewManager();

	try {
	    View view = kit.getConzilla().openMapInNewView(cMapURI, null);

	    Extra extra = null;
	    Layout layout = null;
	    Enumeration extras = kit.getExtras();
	    while (extras.hasMoreElements())
		{
		    extra = (Extra) extras.nextElement();
		    if (extra instanceof Layout)
			layout = (Layout) extra;
		}
	    if (layout != null)
		layout.layout(view.getController());
	} catch (ControllerException ce)
	    {}
        add(viewManager.getSinglePane());
    }

    protected void addNeuronToMap(Instance instance, ConceptMap cMap, URI typeURI, Hashtable neuron2frame)
    {
	URI uri = URIClassifier.parseValidURI(protegeHelper.getURI(instance));
	try {
	    Neuron neuron =  handler.createNeuron(uri, uri, tmp, typeURI);
	    neuron2frame.put(instance, neuron);
	    kit.getComponentStore().getCache().referenceComponent(neuron);
	    NeuronStyle ns = cMap.addNeuronStyle(uri.toString());
	    String name = instance.getName();
	    
	    ns.setBoundingBox(new ConceptMap.BoundingBox(new ConceptMap.Dimension(50, 30),
							 new ConceptMap.Position(0,0)));
	    ns.setBodyVisible(true);
	    MetaData.LangString [] langstrings = {new MetaData.LangString(null, name)};
	    neuron.getMetaData().set_general_title(new MetaData.LangStringType(langstrings));
	} catch (ComponentException ce) {
	    Tracer.debug("failed creating neuron. \n"+ce.getMessage());
	    return;
	} catch (InvalidURIException iue) {
	    Tracer.debug("failed creating neuron. \n"+iue.getMessage());
	    return;
	}
    }

    protected Collection getRelations(Instance instance, String subject)
    {
	Tracer.debug("start of getRelations....waiting to complete...");
	Vector vec = new Vector();
	URI typeURIGeneralization = URIClassifier.parseValidURI("urn:path:/org/conzilla/builtin/types/UML/ClassD/generalization");
	URI typeURIClassification = URIClassifier.parseValidURI("urn:path:/org/conzilla/builtin/types/UML/ClassD/classification");
	URI typeURI = null;
	String subjectEnd;
	String objectEnd;
	Collection slots = instance.getOwnSlots();
	Iterator it  = slots.iterator();

	while (it.hasNext())
	    {

		Slot slot = (Slot) it.next();
		ValueType vt = instance.getOwnSlotValueType(slot);
		if (vt == ValueType.INSTANCE || vt == ValueType.CLS)
		    {
			Instance ownSlotValue = (Instance) instance.getOwnSlotValue(slot);
			if (ownSlotValue == null)
			    continue;
			String key = protegeHelper.getURI(slot).toString();
			try {
			    if (slot == directTypeSlot)
				{
				    Tracer.debug("instance = "+instance.getName());
				    Tracer.debug("other end is "+ownSlotValue.getName());
				    Tracer.debug("Doing one slotbinding of "+slot.getName()+ "of "+instance.getOwnSlotValueCount(slot)+" many");
				    subjectEnd = "instance";
				    objectEnd = "class";
				    typeURI= typeURIClassification;
				}			    
			    else if (slot == directSuperClasses)
				{
				    Tracer.debug("instance = "+instance.getName());
				    Tracer.debug("other end is "+ownSlotValue.getName());
				    Tracer.debug("Doing one slotbinding of "+slot.getName()+ "of "+instance.getOwnSlotValueCount(slot)+" many");
				    subjectEnd = "special";
				    objectEnd = "general";
				    typeURI= typeURIGeneralization;
				}
			    else
				{
				    Tracer.debug("Skipping slotbinding of "+slot.getName()+ "of "+instance.getOwnSlotValueCount(slot)+" many");
				    continue; //for now...
				}
			    Tracer.debug("slot "+slot.getName() +" as an axon");
			    
			    URI uri = newRelationURI();
			    Neuron neuron =  handler.createNeuron(uri, uri, tmp, typeURI);
			    kit.getComponentStore().getCache().referenceComponent(neuron);
			    String name = instance.getName();

			    MetaData.LangString [] langstrings = {new MetaData.LangString(null, key)};
			    neuron.getMetaData().set_general_title(new MetaData.LangStringType(langstrings));
			    neuron.addAxon(subjectEnd, subject); //subject
			    //			    neuron.addAxon("predicate", key );  //predicate
			    neuron.addAxon(objectEnd, protegeHelper.getURI(ownSlotValue)); //object
			    vec.add(neuron);
			    Tracer.debug("done creating neuron");
			} catch (NeuronException ne) {
			    Tracer.debug(ne.getMessage());
			} catch (ComponentException ce) {
			    Tracer.debug("failed creating neuron for relation. \n"+ce.getMessage());
			    continue;
			} catch (InvalidURIException iue)
			    {
				iue.printStackTrace();
				Tracer.bug(iue.getMessage());
			    }
		    }
	    }
	Tracer.debug("start of getRelations....complete...");
	return vec;
    }
    protected URI newRelationURI()
    {
	relationcount++;
	return URIClassifier.parseValidURI("tmp:/relation"+relationcount);
    }

  protected void showAxons(ConceptMap cMap, NeuronStyle ns, Neuron neuron)
    {
	//	Tracer.debug("showing axons on neuron " + neuron.getURI());
	String baseuri1=neuron.getURI();
	NeuronStyle [] nss=cMap.getNeuronStyles();
	Axon [] as=neuron.getAxons();
	for (int i=0; i<as.length;i++)
	  if (ns.getAxonStyle(as[i].getID()) == null) 
	      {
		  URI uri1=URIClassifier.parseValidURI(as[i].getEndURI(),baseuri1);
		  boolean visible=false;
		  
		  for (int j=0;j<nss.length;j++)
		    {
			try {
			String baseuri2 =  cMap.getURI();
			URI uri2=URIClassifier.parseValidURI(nss[j].getNeuronURI(), baseuri2);
			if (uri2.equals(uri1))
			    {
				//				Tracer.debug("YES equal");
				
				ns.addAxonStyle(as[i].getID(), nss[j]);
				visible=true;
				//				Tracer.debug("YES2");
			    }
			} catch (ConceptMapException cme)
			    {}
		    }
		  if (!visible)
		      {
			  Tracer.debug("axon belonging to neuron "+neuron.getURI()+" isn't made visible...");
			  Tracer.debug("other end is: "+uri1.toString());
		      }
	    }
    }

// this method is useful for debugging
    public static void main(String[] args) {
	//        edu.stanford.smi.protege.Application.main(args);
    }
}
