/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component.cache;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.Component;
import se.kth.cid.component.EditEvent;
import se.kth.cid.component.EditListener;

/** Resource cache.
 */
public class SoftCache implements ComponentCache, EditListener {
	
	Log log = LogFactory.getLog(SoftCache.class);
	
    public static final int CHECK_COUNT_INTERVAL = 100;
    public static final long CHECK_TIME_INTERVAL = 15 * 60 * 1000;
    public static final long CACHE_RELEASE_TIME = 15 * 60 * 1000;

    /** The table mapping URIs (String) -> ComponentRef
     */
    Hashtable cache;

    Vector globalEditListeners;

    ReferenceQueue queue;

    long lastCheckedTime;
    int checkCount;

    private class ComponentRef extends SoftReference {
        String compURI;
        int refs;
        long referredTime;
        Component self;

        ComponentRef(Component component, ReferenceQueue queue) {
            super(component, queue);

            self = component;
            compURI = component.getURI();
            refs = 0;
            referredTime = System.currentTimeMillis();
        }

        String getURI() {
            return compURI;
        }

        Component getComponent() {
            return (Component) get();
        }

        int getReferences() {
            return refs;
        }

        long getReferredTime() {
            return referredTime;
        }

        void referred() {
            referredTime = System.currentTimeMillis();
            refSelf();
            ++refs;
        }

        void unrefSelf() {
            self = null;
        }

        void refSelf() {
            self = (Component) get();
        }
    }

    public SoftCache() {
        globalEditListeners = new Vector();
        cache = new Hashtable();
        queue = new ReferenceQueue();
        lastCheckedTime = System.currentTimeMillis();
        checkCount = 0;
    }

    protected void checkQueue() {
        Reference r;
        while ((r = queue.poll()) != null) {
            log.debug("Removing from cache: " + ((ComponentRef) r).getURI());
            cache.remove(((ComponentRef) r).getURI());
        }
    }

    public void checkCache() {
        checkQueue();

        if (++checkCount < CHECK_COUNT_INTERVAL)
            return;

        long time = System.currentTimeMillis();

        if (time - lastCheckedTime < CHECK_TIME_INTERVAL)
            return;

        checkCount = 0;
        lastCheckedTime = time;

        doCheckCache(time);
    }

    protected void doCheckCache(long time) {
        Iterator iter = cache.values().iterator();
        while (iter.hasNext()) {
            ComponentRef r = (ComponentRef) iter.next();
            if (time - r.getReferredTime() > CACHE_RELEASE_TIME)
                r.unrefSelf();
        }
    }

    public Component getComponent(String uri) {
        checkCache();

        ComponentRef cr = (ComponentRef) cache.get(uri);

        if (cr != null) {
            cr.referred();
            return cr.getComponent();
        }

        return null;
    }

    public void addGlobalEditListener(EditListener l) {
        globalEditListeners.add(l);
    }

    public void removeGlobalEditListener(EditListener l) {
        globalEditListeners.remove(l);
    }

    public void componentEdited(EditEvent e) {
        if (!e.getComponent().isEdited())
            return;
        for (int i = 0; i < globalEditListeners.size(); i++) {
            ((EditListener) globalEditListeners.get(i)).componentEdited(e);
        }
    }

    public void referenceComponent(Component comp) {
        checkCache();

        String uri = comp.getURI();

        ComponentRef cr = (ComponentRef) cache.get(uri);

        if (cr != null) {
            cr.referred();
            return;
        }
        comp.addEditListener(this);
        cache.put(comp.getURI(), new ComponentRef(comp, queue));
    }

    public void clear() {
        cache.clear();
        queue = new ReferenceQueue();
    }

    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("se.kth.cid.component.cache.SoftCache[");

        Iterator iter = cache.values().iterator();
        while (iter.hasNext()) {
            ComponentRef r = (ComponentRef) iter.next();
            b.append("(" + r.getURI() + "," + r.getComponent() + ")");
        }
        b.append("]");
        return b.toString();
    }

}
