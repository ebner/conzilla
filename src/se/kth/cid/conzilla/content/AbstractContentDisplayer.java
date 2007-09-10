/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import se.kth.cid.component.Resource;

public abstract class AbstractContentDisplayer implements ContentDisplayer {

	protected Resource currentContent;

	protected PropertyChangeSupport changeSupport;

	public AbstractContentDisplayer() {
		changeSupport = new PropertyChangeSupport(this);
	}

	public void setContent(Resource c) throws ContentException {
		Resource oldContent = currentContent;
		currentContent = c;
		changeSupport.firePropertyChange("content", oldContent, currentContent);
	}

	public Resource getContent() {
		return currentContent;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changeSupport.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changeSupport.removePropertyChangeListener(l);
	}
	
}