/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.agent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import se.kth.cid.component.ComponentException;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.identity.MIMEType;
import se.kth.cid.rdf.CV;
import se.kth.cid.rdf.RDFModel;
import se.kth.cid.util.Tracer;
import se.kth.nada.kmr.shame.applications.util.Container;
import se.kth.nada.kmr.shame.container.EditContainer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Handles the personal information about yourself.
 * 
 * @version $Revision$, $Date$
 * @author matthias
 */
public class AgentManager implements EditContainer{
	static final public String AGENT = "agent";

	URI agentURI;

	RDFModel agentContainer;

	JFrame editor;

	AgentPane agentPane;

	public AgentManager() {
		loadAgentContainer();
		detectURI();
	}

	public URI getUri() {
		if (agentURI == null) {
			decideURIForAgent();
		}

		return agentURI;
	}

	public void editAgentInformation() {
		if (editor == null) {
			initEditor();
		}

		agentPane.edit(new Container(agentContainer, null), agentContainer.createResource(agentURI.toString()));
		editor.pack();
		editor.setVisible(true);
	}

	private void initEditor() {
		editor = new JFrame();
		editor.setSize(new Dimension(600, 400));
		editor.setLocation(0, 0);
		editor.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		editor.setTitle("Personal information");

		JPanel panel = new JPanel();
		agentPane = new AgentPane(panel);
		panel.setLayout(new BorderLayout());
		panel.add(agentPane, BorderLayout.CENTER);
		panel.add(new JButton(new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				editor.setVisible(false);
			}
		}), BorderLayout.SOUTH);
		editor.setContentPane(panel);
		WindowAdapter wa = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				saveAgent();
			}
		};
		editor.addWindowListener(wa);
	}

	private void loadAgentContainer() {
		ConzillaKit kit = ConzillaKit.getDefaultKit();
		URI uri = null;
		try {
			uri = new URI("urn:path:/org/conzilla/ont/defaultagent.rdf");
		} catch (URISyntaxException e2) {
			e2.printStackTrace();
		}
		
		try {
			agentContainer = (RDFModel) kit.getResourceStore().getAndReferenceContainer(uri);
			if (agentContainer != null) {
				Tracer.debug("The 'defaultagent.rdf' file already exists.");
			}
		} catch (ComponentException e) {
			// If the agent information file is missing, we try to create it.
			try {
				Tracer.debug("The file containing information on the agent does not exist; trying to create it.");
				// ComponentHandler handler =
				// kit.getComponentStore().getHandler();
				Object[] objs = kit.getResourceStore().checkCreateContainer(uri);
				agentContainer = (RDFModel) kit.getResourceStore().createContainer(uri, (URI) objs[0],
						(MIMEType) objs[1]);
			} catch (ComponentException e1) {
				e1.printStackTrace();
				throw new RuntimeException("No existing file with information on agent and cannot create new either!");
			}
		}
		agentContainer.setPurpose(AGENT);
	}

	private void detectURI() {
		StmtIterator stmtit = agentContainer.listStatements(null, RDF.type, (RDFNode) null);
		while (stmtit.hasNext()) {
			Statement st = stmtit.nextStatement();
			RDFNode object = st.getObject();
			if (object.equals(CV.Person) || object.equals(CV.Group) || object.equals(CV.Organization)
					|| object.equals((CV.Agent))) {
				agentURI = URI.create(st.getSubject().getURI());
			}
		}
	}

	private void decideURIForAgent() {
		AgentIdentifierDialog dialog = new AgentIdentifierDialog();
		dialog.setVisible(true);
		agentURI = URI.create(dialog.getURI());
		if (agentURI == null) {
			agentURI = null;
			throw new RuntimeException("No global identifier given for the agent!");
		} else {
			Resource agentResource = agentContainer.createResource(agentURI.toString());
			agentContainer.add(agentContainer.createStatement(agentResource, RDF.type, CV.Agent));
			agentContainer.setEdited(true);
		}
	}

	public boolean saveAgent() {
		try {
			// Clean up valuemodel in shame by forcing it to edit nothing.
			agentPane.finishEdit();
			// Mark the model as edited, otherwise we can't save it.
			agentContainer.setEdited(true);
			// Now, save it!
			// ConzillaKit.getDefaultKit().getResourceStore().getContainerManager().saveResource(agentContainer);
			ConzillaKit.getDefaultKit().getResourceStore().getComponentManager().saveResource(agentContainer);
		} catch (ComponentException e3) {
			e3.printStackTrace();
			return false;
		}
		return true;
	}

	public Model getModel() {
		return agentContainer;
	}

	public boolean isEdited() {
		return agentContainer.isEdited();
	}

	public void save() {
		saveAgent();
	}

	public void setEdited(boolean edited) {
		agentContainer.setEdited(agentContainer.isEdited() || edited);
	}
}
