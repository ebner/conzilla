/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.metadata;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.nada.kmr.shame.edit.impl.EditForm;
import se.kth.nada.kmr.shame.form.FormModel;
import se.kth.nada.kmr.shame.form.impl.AbstractForm;
import se.kth.nada.kmr.shame.form.impl.DisplayForm;
import se.kth.nada.kmr.shame.form.impl.FormModelImpl;
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
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * A SHAME FormContainer for displaying / editing metadata on 
 * a resource in a model according to a formlet.
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 * @deprecated replaced by se.kth.nada.kmr.shame.applications.util.MetaDataPanel
 */
public class MetaDataPanel extends SinglePanelFormContainer {

    private static final long serialVersionUID = 1L;
	
    static List<LanguageImpl> languages = new LinkedList<LanguageImpl>();
    static {
        languages.add(new LanguageImpl("sv", "Swedish", "The Swedish language"));
        languages.add(new LanguageImpl("en", "English", "The English language"));
        languages.add(new LanguageImpl("de", "German", "The German language"));
    }

    Formlet formlet;        
    AbstractForm form;
    String formletID;
    boolean editable;
    
    Resource resourceBeeingEdited;
    Model modelBeeingEdited;
         
    VariableBindingSet variableBindingSet = null;
    Model queryModel;
    String formName;

    public MetaDataPanel(String formName) {
        this.formName = formName;
    }
    
    public MetaDataPanel(String formName, String formletID) {
        this(formName);
        
        setFormletConfigurationId(formletID);
    }
    
    public void setFormletConfigurationId(String formletID) {
        if (this.formletID == formletID) {
            return;
        }
      this.formletID = formletID;

      if (resourceBeeingEdited != null) {
          launchForm(modelBeeingEdited, resourceBeeingEdited, editable);
      }
    }
    
    protected void fixForm(boolean editable) {
        if (form != null && formlet.getId() == formletID) {
            if (editable && form instanceof EditForm) {
                return;
            }
            if (!editable && form instanceof DisplayForm) {
                return;
            }
        }
        FormletStore store = ConzillaKit.getDefaultKit().getFormletStore();
        formlet = store.getFormlet(formletID);
        
        if (editable) {
        form =
            new EditForm(
                this,
                false,
                null,
                formName,
                formlet.getQueryModel());
        } else {
            form =
                new DisplayForm(
                    this,
                    false,
                    formName,
                    formlet.getQueryModel());            
        }
        // This sets the available languages in the editor.
        form.languages = languages;
        
        if (resourceBeeingEdited != null && modelBeeingEdited != null) {
            edit(modelBeeingEdited, resourceBeeingEdited);
        }
    }
    
    public void setModel(Model model) {
        if (modelBeeingEdited == model) {
            return;
        }
        
        modelBeeingEdited = model;
        if (resourceBeeingEdited != null) {
            launchForm(modelBeeingEdited, resourceBeeingEdited, editable);
        }
    }
  
    public void finishEdit() {
        if (variableBindingSet != null) {
            variableBindingSet = null;
            resourceBeeingEdited = null;
            modelBeeingEdited = null;       
        }
    }

    public void edit(Model model, Resource resource) {
        launchForm(model, resource, true);
    }
    
    public void present(Model model, Resource resource) {
        launchForm(model, resource, false);
    }

    public void launchForm(Model model, Resource resource, boolean editable) {
        this.editable = editable;
        finishEdit();
        fixForm(editable);
        resourceBeeingEdited = resource;
        modelBeeingEdited = model;
        
        List ontologies = new ArrayList();
        ontologies.addAll(formlet.getOntologies());
        if (queryModel != null) {
            ontologies.add(queryModel);
        }

        
        QueryTarget queryTarget = new JenaModelQueryTarget(
            model,
            resource,
            ontologies);

        RDFEngine engine = new JenaRDFEngine(null);
        QueryEngine queryEngine = new GraphPatternQueryEngine(engine);
        
        try {
            variableBindingSet = queryEngine.execute(
                    formlet.getQueryModel(),
                    queryTarget);
        } catch (UnsupportedQueryModelException e) {
            e.printStackTrace();
        } catch (UnsupportedQueryTargetException e) {
            e.printStackTrace();
        } catch (QueryExecutionException e) {
            e.printStackTrace();
        }

        // Here the FormModel is bound to the values supplied by the
        // VariableBindingSet.
        FormModel formModel = new FormModelImpl(variableBindingSet, formlet.getFormTemplate());
        form.create(formModel, null);
    }
    
}