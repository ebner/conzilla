/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import se.kth.cid.component.ComponentManager;
import se.kth.cid.component.Container;
import se.kth.cid.component.EditEvent;
import se.kth.cid.component.EditListener;
import se.kth.cid.component.Resource;
import se.kth.cid.component.UndoListener;
import se.kth.cid.component.UndoManager;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.rdf.layout.RDFConceptMap;

public class RDFUndoManager implements UndoManager, EditListener {

	public class UndoEvent {
		
		private Object event;

		private ModelHistory.Change infoChange;

		private ModelHistory.Change presChange;
		
		public UndoEvent(Object event, ModelHistory mHI, ModelHistory mHP) {
			this.event = event;
			this.infoChange = mHI!= null ? mHI.getChange() : null;
			this.presChange = mHP!= null ? mHP.getChange() : null;
		}
		
		public boolean isEmpty() {
			return ((infoChange == null || infoChange.isEmpty()) &&
					(presChange == null || presChange.isEmpty()));
		}

	}
	
	private ArrayList changes = new ArrayList();
	private int changePosition = 0;
	boolean grouping = false;
	private RDFConceptMap contextMap;
	private ModelHistory mhInfo;
	private ModelHistory mhPres;
	private ArrayList<UndoListener> undoListeners = new ArrayList<UndoListener>();
	
	public RDFUndoManager(RDFConceptMap cMap) {
		this.contextMap = cMap;
	}
	
	public void startRecording() {
		mhInfo = null;
		mhPres = null;
		ComponentManager cMan = contextMap.getComponentManager();
		Container contInfo = cMan.getContainer(URI.create(cMan.getEditingSesssion().getContainerURIForConcepts()));
		Container contPres = cMan.getContainer(URI.create(cMan.getEditingSesssion().getContainerURIForLayouts()));
		if (contPres instanceof RDFModel) {
			mhPres = ((RDFModel) contPres).getModelHistory();
		}
		if (contPres != contInfo && contInfo instanceof RDFModel) {
			mhInfo = ((RDFModel) contInfo).getModelHistory();
		}
		contextMap.addEditListener(this);
	}
	
	public void stopRecording() {
		contextMap.removeEditListener(this);
	}
	
	public boolean canRedo() {
		return changes.size() != changePosition;
	}

	public boolean canUndo() {
		return changePosition > 0;
	}

	public void undo() {
		if (!canUndo()) {
			return;
		}
		Object undoObj = changes.get(changePosition-1);
		if (undoObj instanceof List) {
			for (Iterator iter = ((List) undoObj).iterator(); iter.hasNext();) {
				UndoEvent ee = (UndoEvent) iter.next();
				if (!undo(ee)) {
					undoBreakMessage(ee);
					return;
				}
				
			}
		} else {
			UndoEvent ee = (UndoEvent) undoObj; 
			if (!undo(ee)) {
				undoBreakMessage(ee);
				return;
			}
		}
		changePosition--;
		contextMap.refresh();
		fireUndoEvent();
	}	

	public void redo() {
		if (!canRedo()) {
			return;
		}
		Object redoObj = changes.get(changePosition);
		if (redoObj instanceof List) {
			for (Iterator iter = ((List) redoObj).iterator(); iter.hasNext();) {
				UndoEvent ee = (UndoEvent) iter.next();
				redo(ee);
			}
		} else {
			UndoEvent ee = (UndoEvent) redoObj; 
			redo(ee);
		}
		changePosition++;
		contextMap.refresh();
		fireUndoEvent();
	}

	private void undoBreakMessage(UndoEvent ee) {
		// TODO Auto-generated method stub		
	}

	private boolean undo(UndoEvent ue) {
		if (mhInfo != null) {
			mhInfo.undo(ue.infoChange);
		}
		if (mhPres != null) {
			mhPres.undo(ue.presChange);
		}
		return true;
	}

	private void redo(UndoEvent ue) {
		if (mhInfo != null) {
			mhInfo.redo(ue.infoChange);
		}
		if (mhPres != null) {
			mhPres.redo(ue.presChange);
		}		
	}

	public void makeChange() {
		makeChange(null);
	}
	
	private void makeChange(Object obj) {
		UndoEvent ue = new UndoEvent(obj, mhInfo, mhPres);
		if (!ue.isEmpty()) {
			forgetFuture();
			if (grouping) {
				((List) changes.get(changePosition-1)).add(ue);			
			} else {
				changes.add(ue);
				changePosition++;
			}
			fireUndoEvent();
		}
	}
	
	private void forgetFuture() {
		//If in the middle of history (undo has been called)
		//throw away the future, we continue edit from here.
		while (changes.size() > changePosition) {
			changes.remove(changePosition);
		}
	}

	public void startChange() {
		makeChange(null);
		forgetFuture(); //throw away future changes if not at present already.
		grouping = true;
		changes.add(new ArrayList());
		changePosition++;
	}
	
	public void endChange() {
		makeChange(null);
		grouping = false;
		if (((List) changes.get(changePosition-1)).size() == 0) {
			changePosition--;
			changes.remove(changePosition);
		}
	}
	
	public void componentEdited(EditEvent e) {
		if (e.getEditType() == ContextMap.CONTEXTMAP_REFRESHED ||
				e.getEditType() == Resource.EDITED) {
			return;
		}
		makeChange(e);
	}

	public void addUndoListener(UndoListener undoListener) {
		undoListeners.add(undoListener);
	}

	public void removeUndoListener(UndoListener undoListener) {
		undoListeners.remove(undoListener);
	}
	
	protected void fireUndoEvent() {
		Iterator<UndoListener> it = undoListeners.iterator();
		while(it.hasNext()) {
			it.next().undoStateChanged();
		}
	}

	public void forgetLastChange() {
		UndoEvent ue = new UndoEvent(null, mhInfo, mhPres);
		if (!ue.isEmpty()) {
			undo();
		}
		forgetFuture();
		fireUndoEvent();
	}
}
