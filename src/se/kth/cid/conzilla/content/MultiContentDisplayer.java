/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.Resource;
import se.kth.cid.identity.MIMEType;
import se.kth.cid.layout.ContextMap;

public class MultiContentDisplayer extends AbstractContentDisplayer {
    Hashtable contentDisplayers;

    ContentDisplayer defaultContentDisplayer;

    ContentDisplayer currentContentDisplayer;

    PropertyChangeListener listener;
    
    Log log = LogFactory.getLog(MultiContentDisplayer.class);

    public MultiContentDisplayer() {
        contentDisplayers = new Hashtable();
        listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals("content"))
                    contentChange();
            }
        };
    }

    public void addContentDisplayer(MIMEType type, ContentDisplayer displayer) {
        if (type == null) {
            defaultContentDisplayer = displayer;
        } else {
            contentDisplayers.put(type, displayer);
        }
    }

    public void setContent(Resource c) throws ContentException {
        if (c == null) {
            deactivate();
            super.setContent(null);
            return;
        }
        
        if (c instanceof ContextMap 
                && contentDisplayers.containsKey(MIMEType.CONCEPTMAP)) {
            activate((ContentDisplayer) (contentDisplayers.get(MIMEType.CONCEPTMAP)), c);
        } else {
            activate(defaultContentDisplayer, c);
        }
    }

    void deactivate() throws ContentException {
        if (currentContentDisplayer != null) {
            currentContentDisplayer.removePropertyChangeListener(listener);
            try {
                currentContentDisplayer.setContent(null);
            } catch (ContentException e) {
                currentContentDisplayer.addPropertyChangeListener(listener);
                e.fillInStackTrace();
                throw e;
            }
            currentContentDisplayer = null;
        }
    }

    void activate(ContentDisplayer disp, Resource cd) throws ContentException {
        if (disp != currentContentDisplayer) {
            deactivate();
            currentContentDisplayer = disp;
            currentContentDisplayer.addPropertyChangeListener(listener);
        }
        currentContentDisplayer.setContent(cd);
    }

    void contentChange() {
        try {
            super.setContent(currentContentDisplayer.getContent());
        } catch (ContentException e) {
        	log.error("AbstractContentDisplayer threw exception!", e);
        }
    }
}