/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.metadata;

import se.kth.nada.kmr.shame.formlet.Formlet;
import se.kth.nada.kmr.shame.query.QueryTarget;

public interface PopupTrigger2QueryTarget {
	boolean isCollaborative(Object popupTrigger);
	QueryTarget getQueryTarget(Object popupTrigger);
	QueryTarget getCollaborativeQueryTarget(Object popupTrigger);
	Formlet getFormlet(Object popupTrigger);
}
