/*  $Id: $
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;

/**
 * Keeps track of all listeners to models and allows them to be enabled or 
 * disabled at will.
 * 
 * Especially useful when undo is called to avoid new history to be 
 * generated. This can happen if there are other maps in the same session 
 * that are edited simultaneously, i.e. they listen to the same model.
 * 
 * @author matthias
 *
 */
public class ModelHistoryListenerCentral {
	private HashMap<Model, ArrayList<ModelChangedListener>> model2listeners = 
		new HashMap<Model, ArrayList<ModelChangedListener>>();
	
	public void register(Model model, ModelChangedListener listener) {
		model.register(listener);
		
		//Add to ListenerCentral
		ArrayList<ModelChangedListener> alist = model2listeners.get(model);
		if (alist == null) {
			alist = new ArrayList<ModelChangedListener>();
			model2listeners.put(model, alist);
		}
		alist.add(listener);
	}
	
	public void unregister(Model model, ModelChangedListener listener) {
		model.unregister(listener);
		
		//Remove from ListenerCentral
		ArrayList<ModelChangedListener> alist = model2listeners.get(model);
		if (alist != null) {
			alist.remove(listener);
			if (alist.isEmpty()) {
				model2listeners.remove(model);
			}
		}
	}
	
	public void disableAllListenersFor(Model model) {
		ArrayList<ModelChangedListener> alist = model2listeners.get(model);
		if (alist != null) {
			for(Iterator<ModelChangedListener> i = alist.iterator();i.hasNext();) {
				model.unregister(i.next());
			}
		}
	}

	public void enableAllListenersFor(Model model) {
		ArrayList<ModelChangedListener> alist = model2listeners.get(model);
		if (alist != null) {
			for(Iterator<ModelChangedListener> i = alist.iterator();i.hasNext();) {
				model.register(i.next());
			}
		}
	}
}
