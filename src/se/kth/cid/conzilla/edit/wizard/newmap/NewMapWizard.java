/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.wizard.newmap;

import java.net.URI;
import java.util.ArrayList;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.controller.MapManagerFactory;
import se.kth.cid.conzilla.metadata.EditPanel;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.session.SessionManager;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.conzilla.util.wizard.Wizard;
import se.kth.cid.conzilla.util.wizard.WizardComponent;
import se.kth.cid.identity.MalformedURIException;
import se.kth.cid.layout.ContextMap;

/**
 * @author matthias
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class NewMapWizard extends Wizard {
	MapManagerFactory mmf;

	SessionManager sessionManager;

	public NewMapWizard(MapManagerFactory mmf, ContainerManager containerManager, SessionManager sessionManager) {
		this.mmf = mmf;
		this.sessionManager = sessionManager;
		ArrayList<WizardComponent> list = new ArrayList<WizardComponent>();
		list.add(new ChooseSession(sessionManager));
		list.add(new SpecifyMapID(containerManager));
		initWizardComponents(list);
	}

	public void openNewMapInNewView(MapController mapController) {
		showWizard();
		if (!wizardFinishedSuccessfully()) {
			return;
		}
		try {
			URI map = openNewMap(mapController);
			if (map == null)
				return;
			ContextMap cMap = ConzillaKit.getDefaultKit().getResourceStore().getComponentManager().createContextMap(map);
			ConzillaKit.getDefaultKit().getConzilla().openMapInNewView(map, mmf, mapController);
			EditPanel.launchEditPanelInFrame(cMap);
		} catch (MalformedURIException me) {
			ErrorMessage.showError("Parse Error", "Invalid URI\n\n", me, null);
		} catch (ControllerException ce) {
			ErrorMessage.showError("Load Error", "Failed to open map\n\n", ce, null);
		} catch (ComponentException e) {
			e.printStackTrace();
		}
	}

	public URI openNewMap(MapController controller) throws MalformedURIException {
		Session session = (Session) data.get(ChooseSession.CHOOSEN_SESSION);
		URI uri = (URI) data.get(SpecifyMapID.MAP_ID);
		if (session instanceof Session) {
			if (!sessionManager.setCurrentSession((Session) session)) {
				return null;
			}
			((Session) session).addManaged(uri.toString());
			return uri;
		}
		return null;
	}
}
