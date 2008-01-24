/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.listeners.StatementListener;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.Statement;

public class ModelHistory {
	
	public class Change {
		
		
		public ArrayList<Statement> statements = new ArrayList<Statement>();
		public ArrayList<Boolean> added = new ArrayList<Boolean>();
		
		public boolean isEmpty() {
			return statements.isEmpty();
		}
	}

	private Change currentChange;
	private Model model;
	private ModelChangedListener mcl;
	private ModelHistoryListenerCentral central;

	public ModelHistory(Model model, ModelHistoryListenerCentral central) {
		this.model = model;
		this.central = central;
		//newChange();
		mcl = new StatementListener() {
    		public void addedStatement(Statement s) {
    			currentChange.statements.add(s);
    			currentChange.added.add(Boolean.TRUE);
    		}
			public void removedStatement(Statement s) {
				currentChange.statements.add(s);
    			currentChange.added.add(Boolean.FALSE);
			}
    	};
    	register();
    	currentChange = new Change();
	}
	
	public void undo(Change change) {
		central.disableAllListenersFor(model);
		for (int i=change.statements.size()-1; i>=0;i--) {
			if (change.added.get(i).booleanValue()) {
				model.remove(change.statements.get(i));
			} else {
				model.add(change.statements.get(i));
			}
		}
		central.enableAllListenersFor(model);
	}
	
	public void redo(Change change) {
		central.disableAllListenersFor(model);
		for (int i=0; i<change.statements.size();i++) {
			if (change.added.get(i).booleanValue()) {
				model.add(change.statements.get(i));
			} else {
				model.remove(change.statements.get(i));
			}
		}
		central.enableAllListenersFor(model);
	}
	
	public Change getChange() {
		Change change = currentChange;
		currentChange = new Change();
		return change;
	}

	public void register() {
		central.register(model, mcl);
	}

	public void unRegister() {
		central.unregister(model, mcl);
	}
}