/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.bookmarkrdf;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.nada.kmr.shame.edit.impl.EditForm;
import se.kth.nada.kmr.shame.form.impl.LanguageImpl;
import se.kth.nada.kmr.shame.form.impl.SinglePanelFormContainer;
import se.kth.nada.kmr.shame.formlet.Formlet;
import se.kth.nada.kmr.shame.formlet.FormletStore;
import se.kth.nada.kmr.shame.query.QueryEngine;
import se.kth.nada.kmr.shame.query.QueryExecutionException;
import se.kth.nada.kmr.shame.query.QueryTarget;
import se.kth.nada.kmr.shame.query.RDFEngine;
import se.kth.nada.kmr.shame.query.UnsupportedQueryModelException;
import se.kth.nada.kmr.shame.query.UnsupportedQueryTargetException;
import se.kth.nada.kmr.shame.query.VariableBindingSet;
import se.kth.nada.kmr.shame.query.impl.GraphPatternQueryEngine;
import se.kth.nada.kmr.shame.query.impl.JenaModelQueryTarget;
import se.kth.nada.kmr.shame.query.impl.JenaRDFEngine;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * TODO: Description
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public class FormItemPane extends SinglePanelFormContainer {
    
	private static final long serialVersionUID = 1L;

	static {
        FormletStore.requireFormletConfigurations("formlets/formlets.rdf");
        FormletStore.requireFormletConfigurations("formlets/Simple_Dublin_Core/formlets.rdf");
        FormletStore.requireFormletConfigurations("formlets/annotea/formlets.rdf");
        FormletStore.requireFormletConfigurations("formlets/bookmarks/formlets.rdf");
    }
    
    EditForm bookmarkForm;
    Formlet bookmarkFormlet;
    EditForm bookmarkFolderForm;
    Formlet bookmarkFolderFormlet;
    EditForm conceptalonebookmarkForm;
    Formlet conceptalonebookmarkFormlet;
    EditForm conceptincontextbookmarkForm;
    Formlet conceptincontextbookmarkFormlet;

    String bookmarkFormletId =
        "http://kmr.nada.kth.se/shame/bookmarks/formlet#ContextMapBookmark";
    String bookmarkFolderFormletId =
        "http://kmr.nada.kth.se/shame/bookmarks/formlet#BookmarkFolder";
    String conceptalonebookmarkFormletId =
        "http://kmr.nada.kth.se/shame/bookmarks/formlet#conceptalonebookmark";
    String conceptincontextbookmarkFormletId =
        "http://kmr.nada.kth.se/shame/bookmarks/formlet#conceptincontextbookmark";

    VariableBindingSet variableBindingSet = null;
    Model queryModel;
    TreeNode currentNode;

    public FormItemPane() {

        FormletStore store = ConzillaKit.getDefaultKit().getFormletStore();

        bookmarkFormlet = store.getFormlet(bookmarkFormletId);
        bookmarkFolderFormlet = store.getFormlet(bookmarkFolderFormletId);
        conceptalonebookmarkFormlet =
            store.getFormlet(conceptalonebookmarkFormletId);
        conceptincontextbookmarkFormlet =
            store.getFormlet(conceptincontextbookmarkFormletId);

        bookmarkForm =
            new EditForm(
                this,
                false,
                null,
                "ContextMap Bookmark editor",
                bookmarkFormlet.getQueryModel());

        bookmarkFolderForm =
            new EditForm(
                this,
                false,
                null,
                "BookmarkFolder editor",
                bookmarkFolderFormlet.getQueryModel());

        conceptalonebookmarkForm =
            new EditForm(
                this,
                false,
                null,
                "Concept Bookmark editor",
                conceptalonebookmarkFormlet.getQueryModel());

        conceptincontextbookmarkForm =
            new EditForm(
                this,
                false,
                null,
                "Concept-in-Context Bookmark editor",
                conceptincontextbookmarkFormlet.getQueryModel());

        // This sets the available languages in the editor.
        List langs = new LinkedList();
        langs.add(new LanguageImpl("sv", "Swedish", "The Swedish language"));
        langs.add(new LanguageImpl("en", "English", "The English language"));
        langs.add(new LanguageImpl("de", "German", "The German language"));
        bookmarkForm.languages = langs;
        bookmarkFolderForm.languages = langs;
        conceptalonebookmarkForm.languages = langs;
        conceptincontextbookmarkForm.languages = langs;
    }

    public void setQueryOntology(Model model) {
        this.queryModel = model;
        if (currentNode != null) {
            editFormItem(currentNode);
        }
    }

    public void editFormItem(TreeNode node) {
        currentNode = node;
        Formlet formlet = null;
        EditForm editForm = null;

        if (node == null) {
            removeAll();
            revalidate();
            return;
        }

        if (node.isContextMapBookmark()) {
            formlet = bookmarkFormlet;
            editForm = bookmarkForm;
        } else if (node.isBookmarkEnvironmentItem()) {
            formlet = bookmarkFolderFormlet;
            editForm = bookmarkFolderForm;
        } else if (node.isConceptAloneBookmark()) {
            formlet = conceptalonebookmarkFormlet;
            editForm = conceptalonebookmarkForm;
        } else if (node.isConceptInContextBookmark()) {
            formlet = conceptincontextbookmarkFormlet;
            editForm = conceptincontextbookmarkForm;
        } else {
            throw new RuntimeException(
                "Bookmarktree contains something strange, the uri is "
                    + node.getResource().getURI());
        }

        List ontologies = new ArrayList();
        ontologies.addAll(formlet.getOntologies());
        if (queryModel != null) {
            ontologies.add(queryModel);
        }

        QueryTarget queryTarget =
            new JenaModelQueryTarget(
                node.node.getModel(),
                node.node,
                ontologies);

        RDFEngine engine = new JenaRDFEngine(null);
        QueryEngine queryEngine = new GraphPatternQueryEngine(engine);

        try {
            variableBindingSet = queryEngine.execute(formlet.getQueryModel(), queryTarget);
        } catch (UnsupportedQueryModelException e) {
            e.printStackTrace();
        } catch (UnsupportedQueryTargetException e) {
            e.printStackTrace();
        } catch (QueryExecutionException e) {
            e.printStackTrace();
        }

        // Here the FormModel is bound to the values supplied by the
        // VariableBindingSet.
        formlet.getFormModel().setVariableBindingSet(variableBindingSet);
        formlet.getFormModel().setQueryTarget(queryTarget);
        editForm.create(formlet.getFormModel(), null);
    }
}
