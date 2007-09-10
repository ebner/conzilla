/* $Id$ */
/*
  This file is part of the Conzilla browser, designed for
  the Garden of Knowledge project.
  Copyright (C) 1999  CID (http://www.nada.kth.se/cid)
  
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package se.kth.cid.conzilla.metadata;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import se.kth.cid.component.Component;


public class PanelMetaDataDisplayer extends JPanel
{
  public static final int GENERAL        = 0;
  public static final int LIFECYCLE      = 1;
  public static final int METAMETADATA   = 2;
  public static final int TECHNICAL      = 3;
  public static final int EDUCATIONAL    = 4;
  public static final int RIGHTS         = 5; 
  public static final int RELATION       = 6;
  public static final int ANNOTATION     = 7;
  public static final int CLASSIFICATION = 8;

  public static final int TAB_COUNT      = 9;

  public static final int[] TAB_LENGTH = {
    8,
    3,
    4,
    7,
    11,
    1,
    1,
    1,
    1};

  public static final String[] TAB_TITLE = {
    "General",
    "Life Cycle",
    "Meta-metadata",
    "Technical",
    "Educational",
    "Rights",
    "Relation",
    "Annotation",
    "Classification"};

  public static final String[] TAB_NAME = {
    "general",
    "lifecycle",
    "metametadata",
    "technical",
    "educational",
    "rights",
    "relation",
    "annotation",
    "classification"};

  Component component;
  MetaData  metaData;
  boolean   isEditable;

  boolean isEdited;
  
  JTabbedPane tabPane;

  Vector               editListeners;
  MetaDataEditListener metaDataEditListener;
  EditListener         editListener;
  
  MetaDataFieldEditor[][] fieldEditors;

  boolean[] needUpdate;
  
  LangStringList        general_title;
  CatalogEntryList      general_catalogentry;
  LanguageList          general_language;
  LangStringTypeList    general_description;
  LangStringTypeList    general_keywords;
  LangStringTypeList    general_coverage;
  LangStringList        general_structure;
  IntCombo              general_aggregationlevel;

  LangStringComponent   lifecycle_version;
  LangStringList        lifecycle_status;
  ContributionList      lifecycle_contribute;
  
  CatalogEntryList      metametadata_catalogentry;
  ContributionList      metametadata_contribute;
  StringList            metametadata_metadatascheme;
  LanguageBox           metametadata_language;

  LangStringList        technical_format;
  IntField              technical_size;
  LocationList          technical_location;
  RequirementsList      technical_requirements;
  LangStringComponent   technical_installationremarks;
  LangStringComponent   technical_otherplatformrequirements;
  DateEdit              technical_duration;
  
  LangStringList        educational_interactivitytype;
  LangStringList        educational_learningresourcetype;
  IntCombo              educational_interactivitylevel;
  IntCombo              educational_semanticdensity;
  LangStringTypeList    educational_intendedenduserrole;
  LangStringList        educational_learningcontext;
  LangStringList        educational_typicalagerange;
  IntCombo              educational_difficulty;
  DateEdit              educational_typicallearningtime;
  LangStringList        educational_description;
  LanguageList          educational_language;

  RightsPanel           rights;

  RelationList          relation;

  AnnotationList        annotation;

  ClassificationList    classification;
  
  public PanelMetaDataDisplayer(Component comp, boolean isEditable)
    {
      this.component = comp;
      this.metaData  = comp.getMetaData();

      if(isEditable && !comp.isEditable())
	Tracer.bug("Cannot edit Component!!!");

      this.isEditable = isEditable;

      setLayout(new BorderLayout());
      
      tabPane = new JTabbedPane();
      add(tabPane, BorderLayout.CENTER);

      editListeners = new Vector();
      
      editListener = new EditListener()
	{
	  public void componentEdited(EditEvent e)
	    {
	      switch(e.getEditType())
		{
		case Component.METADATA_EDITED:
		  updateMetaData((String) e.getTarget());
		  break;
		default:
		}
	    }
	};
      component.addEditListener(editListener);

      metaDataEditListener = new MetaDataEditListener()
	{
	  public void fieldEdited(MetaDataEditEvent e)
	    {
	      fireEdited(e);	      
	    }
	};

      tabPane.addChangeListener(new ChangeListener()
	{
	  public void stateChanged(ChangeEvent e)
	    {
	      ensureTabVisible(tabPane.getSelectedIndex());
	    }
	});

      
      fieldEditors = new MetaDataFieldEditor[TAB_COUNT][];
      needUpdate = new boolean[TAB_COUNT];
      
      for(int i = 0; i < TAB_COUNT; i++)
	tabPane.addTab(TAB_TITLE[i], new JPanel());

      ensureTabVisible(0);
    }

  // Be sure to call!
  public void detach()
    {
      for(int i = 0; i < TAB_COUNT; i++)
	if(fieldEditors[i] != null)
	  for(int j = 0; j < TAB_LENGTH[i]; j++)
	    fieldEditors[i][j].detach();

      component.removeEditListener(editListener);
    }

  
  // Beware! If anyone else edits metadata, the edited state may change
  // to false without any event being generated.
  //
  public void addMetaDataEditListener(MetaDataEditListener e)
    {
      editListeners.add(e);
    }

  public void removeMetaDataEditListener(MetaDataEditListener e)
    {
      editListeners.remove(e);
    }

  void fireEdited(MetaDataEditEvent e)
    {
      isEdited = true;

      e.metaData = metaData;
      e.metaDataDisplayer = this;
      
      for(int i = 0; i < editListeners.size(); i++)
	{
	  ((MetaDataEditListener) editListeners.get(i)).fieldEdited(e);
	}
    }

  void updateMetaData(String target)
    {
      for(int i = 0; i < TAB_COUNT; i++)
	if(target.startsWith(TAB_NAME[i]))
	  {
	    if(fieldEditors[i] == null)
	      return;

	    needUpdate[i] = true;

	    SwingUtilities.invokeLater(new Runnable()
	      {
		public void run()
		  {
		    doUpdate();
		  }
	      });
	    
	    return;
	  }
    }

  void doUpdate()
    {
      for(int i = 0; i < TAB_COUNT; i++)
	{
	  if(needUpdate[i])
	    {	      	    
	      emptyTab(i);
	      
	      if(tabPane.getSelectedIndex() == i)
		fillTab(i);

	      needUpdate[i] = false;
	    }
	}
      updateEdited();
    }

  public boolean isEdited()
    {
      return isEdited;
    }
  
  void updateEdited()
    {
      isEdited = false;
      
      for(int i = 0; i < TAB_COUNT; i++)
	if(fieldEditors[i] != null)
	  for(int j = 0; j < TAB_LENGTH[i]; j++)
	    if(fieldEditors[i][j].isEdited())
	      isEdited = true;
    }

  
  void ensureTabVisible(int tabno)
    {
      if(fieldEditors[tabno] == null)
	fillTab(tabno);
    }


  void emptyTab(int tabno)
    {
      if(fieldEditors[tabno] != null)
	for(int j = 0; j < TAB_LENGTH[tabno]; j++)
	  fieldEditors[tabno][j].detach();
      

      fieldEditors[tabno] = null;
      tabPane.setComponentAt(tabno, new JPanel());      

      switch(tabno)
	{
	case GENERAL:

	  general_title            = null;
	  general_catalogentry     = null;
	  general_language         = null;
	  general_description      = null;
	  general_keywords         = null;
	  general_coverage         = null;
	  general_structure        = null;
	  general_aggregationlevel = null;
	  
	  break;
	  
	case LIFECYCLE:
	  
	  lifecycle_version      = null;
	  lifecycle_status       = null;
	  lifecycle_contribute   = null;
	    
	  break;

	case METAMETADATA:
	  
	  metametadata_catalogentry   = null;
	  metametadata_contribute     =	null;
	  metametadata_metadatascheme = null;
	  metametadata_language       = null;

	  break;

	case TECHNICAL:
	  
	  technical_format                    = null;
	  technical_size                      = null;
	  technical_location                  =	null;
	  technical_requirements              =	null;
	  technical_installationremarks       =	null;
	  technical_otherplatformrequirements =	null;
	  technical_duration                  = null;

	  break;

	case EDUCATIONAL:

	  educational_interactivitytype    = null;
	  educational_learningresourcetype = null;
	  educational_interactivitylevel   = null;
	  educational_semanticdensity      = null;
	  educational_intendedenduserrole  = null;
	  educational_learningcontext      = null;
	  educational_typicalagerange      = null;
	  educational_difficulty           = null;
	  educational_typicallearningtime  = null;
	  educational_description          = null;
	  educational_language             = null;

	  break;

	case RIGHTS:
	  
	  rights = null;
      
	  break;

	case RELATION:
	  
	  relation = null;
      
	  break;

	case ANNOTATION:
      
	  annotation = null;
      
	  break;

	case CLASSIFICATION:
	  
	  classification = null;
      
	  break;

	default:
	  Tracer.bug("Invalid tab number: " + tabno);
	}
    }
  
      
  void fillTab(int tabno)
    {
      MetaDataPanel tab = new MetaDataPanel();

      fieldEditors[tabno] = new MetaDataFieldEditor[TAB_LENGTH[tabno]];

      int index = 0;
      
      switch(tabno)
	{
	case GENERAL:

	  general_title = new LangStringList(metaData.get_general_title(), true,
					     isEditable, metaDataEditListener, "general_title");
	  addToTab(general_title, "Title", tab, tabno, index++);
      

	  general_catalogentry = new CatalogEntryList(metaData.get_general_catalogentry(),
						      isEditable, metaDataEditListener, "general_catalogentry");
	  addToTab(general_catalogentry, "Catalog Entry", tab, tabno, index++);
      

	  general_language = new LanguageList(metaData.get_general_language(),
					      isEditable, metaDataEditListener, "general_language");
	  addToTab(general_language, "Language", tab, tabno, index++);

	  
	  general_description = new LangStringTypeList(metaData.get_general_description(), true,
						       isEditable, metaDataEditListener, "general_description");
	  addToTab(general_description, "Description", tab, tabno, index++);
	  
	  
	  general_keywords = new LangStringTypeList(metaData.get_general_keywords(), false,
						    isEditable, metaDataEditListener, "general_keywords");
	  addToTab(general_keywords, "Keywords", tab, tabno, index++);

	  
	  general_coverage = new LangStringTypeList(metaData.get_general_coverage(), false,
						    isEditable, metaDataEditListener, "general_coverage");
	  addToTab(general_coverage, "Coverage", tab, tabno, index++);


	  general_structure = new LangStringList(metaData.get_general_structure(), false,
						 isEditable, metaDataEditListener, "general_structure");
	  addToTab(general_structure, "Structure", tab, tabno, index++);

	  
	  String[] comboInts = {"0 (atom)", "1 (atom set)", "2 (unit)", "3 (collection)"};
	  
	  general_aggregationlevel = new IntCombo(metaData.get_general_aggregationlevel(), comboInts,
						  isEditable, metaDataEditListener, "general_aggregationlevel");
	  addToTab(general_aggregationlevel, "Aggregation Level", tab, tabno, index++);

	  break;


	  
	case LIFECYCLE:
	  
	  lifecycle_version = new LangStringComponent(metaData.get_lifecycle_version(), false,
						      isEditable, metaDataEditListener, "lifecycle_version");
	  addToTab(lifecycle_version, "Version", tab, tabno, index++);


	  lifecycle_status = new LangStringList(metaData.get_lifecycle_status(), false, 
						isEditable, metaDataEditListener, "lifecycle_status");
	  addToTab(lifecycle_status, "Status", tab, tabno, index++);


	  lifecycle_contribute = new ContributionList(metaData.get_lifecycle_contribute(),
						      isEditable, metaDataEditListener, "lifecycle_contribute");
	  addToTab(lifecycle_contribute, "Contribution", tab, tabno, index++);
      
	  break;
	  


	case METAMETADATA:
	  
	  metametadata_catalogentry = new CatalogEntryList(metaData.get_metametadata_catalogentry(),
							   isEditable, metaDataEditListener, "metametadata_catalogentry");
	  addToTab(metametadata_catalogentry, "Catalog Entry", tab, tabno, index++);

	  
	  metametadata_contribute = new ContributionList(metaData.get_metametadata_contribute(),
							 isEditable, metaDataEditListener, "metametadata_contribute");
	  addToTab(metametadata_contribute, "Contribute", tab, tabno, index++);

	  
	  metametadata_metadatascheme = new StringList(null, metaData.get_metametadata_metadatascheme(),
						       false, isEditable, metaDataEditListener, "metametadata_metadatascheme");
	  addToTab(metametadata_metadatascheme, "Metadata Scheme", tab, tabno, index++);
      
	  
	  metametadata_language = new LanguageBox(metaData.get_metametadata_language(),
						  isEditable, metaDataEditListener, "metametadata_language");
	  addToTab(metametadata_language, "Metadata Language", tab, tabno, index++);

	  break;



	case TECHNICAL:
	  
	  technical_format = new LangStringList(metaData.get_technical_format(), false,
						isEditable, metaDataEditListener, "technical_format");
	  addToTab(technical_format, "Format", tab, tabno, index++);
	  
      
	  technical_size = new IntField(10, metaData.get_technical_size(),
					isEditable, metaDataEditListener, "technical_size");
	  addToTab(technical_size, "Size", tab, tabno, index++);
	  
	  
	  technical_location = new LocationList(metaData.get_technical_location(),
					       isEditable, metaDataEditListener, "technical_location");
	  addToTab(technical_location, "Location", tab, tabno, index++);

	  
	  technical_requirements = new RequirementsList(metaData.get_technical_requirements(),
							isEditable, metaDataEditListener, "technical_requirements");
	  addToTab(technical_requirements, "Requirements", tab, tabno, index++);
      
	  
	  technical_installationremarks = new LangStringComponent(metaData.get_technical_installationremarks(), true, 
								  isEditable, metaDataEditListener, "technical_installationremarks");
	  addToTab(technical_installationremarks, "Installation Remarks", tab, tabno, index++);
	  
	  
	  technical_otherplatformrequirements = new LangStringComponent(metaData.get_technical_otherplatformrequirements(), true,
									isEditable, metaDataEditListener, "technical_otherplatformrequirements");
	  addToTab(technical_otherplatformrequirements, "Other Platform Requirements", tab, tabno, index++);

	  
	  technical_duration = new DateEdit(metaData.get_technical_duration(),
					    isEditable, metaDataEditListener, "technical_duration");
	  addToTab(technical_duration, "Duration", tab, tabno, index++);
      
	  break;



	case EDUCATIONAL:

	  educational_interactivitytype = new LangStringList(metaData.get_educational_interactivitytype(),
							     false, isEditable, metaDataEditListener, "educational_interactivitytype");
	  addToTab(educational_interactivitytype, "Interactivity type", tab, tabno, index++);

	  
	  educational_learningresourcetype = new LangStringList(metaData.get_educational_learningresourcetype(), false,
								isEditable, metaDataEditListener, "educational_learningresourcetype");
	  addToTab(educational_learningresourcetype, "Learning Resource Type", tab, tabno, index++);
	  
	  
	  String[] interComboInts = {"0 (very low)", "1 (low)", "2 (medium)", "3 (high)", "4 (very high)"};
	  educational_interactivitylevel = new IntCombo(metaData.get_educational_interactivitylevel(), interComboInts,
							isEditable, metaDataEditListener, "educational_interactivitylevel");
	  addToTab(educational_interactivitylevel, "Interactivity Level", tab, tabno, index++);

	  
	  String[] semComboInts = {"0 (very low)", "1 (low)", "2 (medium)", "3 (high)", "4 (very high)"};
	  educational_semanticdensity = new IntCombo(metaData.get_educational_semanticdensity(), semComboInts,
						     isEditable, metaDataEditListener, "educational_semanticdensity");
	  addToTab(educational_semanticdensity, "Semantic Density", tab, tabno, index++);

	  
	  educational_intendedenduserrole = new LangStringTypeList(metaData.get_educational_intendedenduserrole(), false,
								   isEditable, metaDataEditListener, "educational_intendedenduserrole");
	  addToTab(educational_intendedenduserrole, "Intended End User Role", tab, tabno, index++);

	  
	  educational_learningcontext = new LangStringList(metaData.get_educational_learningcontext(),
							   false, isEditable, metaDataEditListener, "educational_learningcontext");
	  addToTab(educational_learningcontext, "Learning Context", tab, tabno, index++);
	  
	  
	  educational_typicalagerange = new LangStringList(metaData.get_educational_typicalagerange(),
							   false, isEditable, metaDataEditListener, "educational_typicalagerange");
	  addToTab(educational_typicalagerange, "Typical Age Range", tab, tabno, index++);
	  
	  
	  String diffComboInts[] = {"0 (very easy)", "1 (easy)", "2 (medium)", "3 (difficult)", "4 (very difficult)"};
	  educational_difficulty = new IntCombo(metaData.get_educational_difficulty(), diffComboInts,
						isEditable, metaDataEditListener, "educational_difficulty");
	  addToTab(educational_difficulty, "Difficulty", tab, tabno, index++);
      
	  
	  educational_typicallearningtime = new DateEdit(metaData.get_educational_typicallearningtime(),
							 isEditable, metaDataEditListener, "educational_typicallearningtime");
	  addToTab(educational_typicallearningtime, "Typical Learning Time", tab, tabno, index++);

	  
	  educational_description = new LangStringList(metaData.get_educational_description(), true, 
						       isEditable, metaDataEditListener, "educational_description");
	  addToTab(educational_description, "Description", tab, tabno, index++);
	  
	  
	  educational_language = new LanguageList(metaData.get_educational_language(),
						  isEditable, metaDataEditListener, "educational_language");
	  addToTab(educational_language, "Language", tab, tabno, index++);

	  break;



	case RIGHTS:
	  
	  rights = new RightsPanel(metaData.get_rights(),
				   isEditable, metaDataEditListener, "rights");
	  addToTab(rights, "Rights", tab, tabno, index);
      
	  break;



	case RELATION:
	  
	  relation = new RelationList(metaData.get_relation(),
				      isEditable, metaDataEditListener, "relation");
	  addToTab(relation, "Relation", tab, tabno, index);
      
	  break;



	case ANNOTATION:
      
	  annotation = new AnnotationList(metaData.get_annotation(),
					  isEditable, metaDataEditListener, "annotation");
	  addToTab(annotation, "Annotation", tab, tabno, index);
      
	  break;



	case CLASSIFICATION:
	  
	  classification = new ClassificationList(metaData.get_classification(),
						  isEditable, metaDataEditListener, "classification");
	  addToTab(classification, "Classification", tab, tabno, index);
      
	  break;

	default:
	  Tracer.bug("Invalid tab number: " + tabno);
	}

      JScrollPane pane = new JScrollPane(tab);

      tabPane.setComponentAt(tabno, pane);
    }

  
  void addToTab(JComponent comp, String title, MetaDataPanel tab,
		int tabno, int fieldIndex)
    {
      tab.addPanel(title, comp);
      fieldEditors[tabno][fieldIndex] = (MetaDataFieldEditor) comp;
    }

  
  public void storeMetaData()
    {
      if(!component.isEditable())
	return;
      
      component.removeEditListener(editListener);
      
      if(fieldEditors[GENERAL] != null)
	{
	  if(general_title.isEdited())
	    metaData.set_general_title(general_title.getLangStringType(true));
	  
	  if(general_catalogentry.isEdited())
	    metaData.set_general_catalogentry(general_catalogentry.getCatalogEntries(true));
	  
	  if(general_language.isEdited())
	    metaData.set_general_language(general_language.getLanguages(true));
	  
	  if(general_description.isEdited())
	    metaData.set_general_description(general_description.getLangStringTypes(true));
	  
	  if(general_keywords.isEdited())
	    metaData.set_general_keywords(general_keywords.getLangStringTypes(true));
	  
	  if(general_coverage.isEdited())
	    metaData.set_general_coverage(general_coverage.getLangStringTypes(true));
	  
	  if(general_structure.isEdited())
	    metaData.set_general_structure(general_structure.getLangStringType(true));
	  
	  if(general_aggregationlevel.isEdited())
	    metaData.set_general_aggregationlevel(general_aggregationlevel.getInt(true));
	}
      

      if(fieldEditors[LIFECYCLE] != null)
	{
	  if(lifecycle_version.isEdited())
	    metaData.set_lifecycle_version(lifecycle_version.getLangString(true));
	  
	  if(lifecycle_status.isEdited())
	    metaData.set_lifecycle_status(lifecycle_status.getLangStringType(true));
	  
	  if(lifecycle_contribute.isEdited())
	    metaData.set_lifecycle_contribute(lifecycle_contribute.getContributes(true));
	}
      

      if(fieldEditors[METAMETADATA] != null)
	{
	  if(metametadata_catalogentry.isEdited())
	    metaData.set_metametadata_catalogentry(metametadata_catalogentry.getCatalogEntries(true));
	  
	  if(metametadata_contribute.isEdited())
	    metaData.set_metametadata_contribute(metametadata_contribute.getContributes(true));
	  
	  if(metametadata_metadatascheme.isEdited())
	    metaData.set_metametadata_metadatascheme(metametadata_metadatascheme.getStrings(true));
	  
	  if(metametadata_language.isEdited())
	    metaData.set_metametadata_language(metametadata_language.getLanguage(true));
	}
      

      if(fieldEditors[TECHNICAL] != null)
	{
	  if(technical_format.isEdited())
	    metaData.set_technical_format(technical_format.getLangStringType(true));
	  
	  if(technical_size.isEdited())
	    metaData.set_technical_size(technical_size.getLong(true));
	  
	  if(technical_location.isEdited())
	    metaData.set_technical_location(technical_location.getLocations(true));
	  
	  if(technical_requirements.isEdited())
	    metaData.set_technical_requirements(technical_requirements.getRequirements(true));
	  
	  if(technical_installationremarks.isEdited())
	    metaData.set_technical_installationremarks(technical_installationremarks.getLangString(true));
	  
	  if(technical_otherplatformrequirements.isEdited())
	    metaData.set_technical_otherplatformrequirements(technical_otherplatformrequirements.getLangString(true));
	  
	  if(technical_duration.isEdited())
	    metaData.set_technical_duration(technical_duration.getDateType(true));
	}
      
      
      if(fieldEditors[EDUCATIONAL] != null)
	{
	  if(educational_interactivitytype.isEdited())
	    metaData.set_educational_interactivitytype(educational_interactivitytype.getLangStringType(true));
	  
	  if(educational_learningresourcetype.isEdited())
	    metaData.set_educational_learningresourcetype(educational_learningresourcetype.getLangStrings(true));
	  
	  if(educational_interactivitylevel.isEdited())
	    metaData.set_educational_interactivitylevel(educational_interactivitylevel.getInt(true));
	  
	  if(educational_semanticdensity.isEdited())
	    metaData.set_educational_semanticdensity(educational_semanticdensity.getInt(true));
	  
	  if(educational_intendedenduserrole.isEdited())
	    metaData.set_educational_intendedenduserrole(educational_intendedenduserrole.getLangStringTypes(true));

	  if(educational_learningcontext.isEdited())
	    metaData.set_educational_learningcontext(educational_learningcontext.getLangStrings(true));
	  
	  if(educational_typicalagerange.isEdited())
	    metaData.set_educational_typicalagerange(educational_typicalagerange.getLangStrings(true));
	  
	  if(educational_difficulty.isEdited())
	    metaData.set_educational_difficulty(educational_difficulty.getInt(true));
	  
	  if(educational_typicallearningtime.isEdited())
	    metaData.set_educational_typicallearningtime(educational_typicallearningtime.getDateType(true));
	  
	  if(educational_description.isEdited())
	    metaData.set_educational_description(educational_description.getLangStringType(true));
	  
	  if(educational_language.isEdited())
	    metaData.set_educational_language(educational_language.getLanguages(true));
	}
      
      
      
      if(fieldEditors[RIGHTS] != null)
	{
	  if(rights.isEdited())
	    metaData.set_rights(rights.getRights(true));
	}
      

      if(fieldEditors[RELATION] != null)
	{      
	  if(relation.isEdited())
	    metaData.set_relation(relation.getRelations(true));
	}

      if(fieldEditors[ANNOTATION] != null)
	{
	  if(annotation.isEdited())
	    metaData.set_annotation(annotation.getAnnotations(true));
	}

      if(fieldEditors[CLASSIFICATION] != null)
	{
	  if(classification.isEdited())
	    metaData.set_classification(classification.getClassifications(true));
	}

      updateEdited();
      component.addEditListener(editListener);
      isEdited = false;
    }

  
}


//Crap

/*
  
	    
      boolean edited =
	(
	 (general_title != null
	  &&
	  (general_title.isEdited()
	   || general_catalogentry.isEdited()
	   || general_language.isEdited()
	   || general_description.isEdited()
	   || general_keywords.isEdited()
	   || general_coverage.isEdited()
	   || general_structure.isEdited()
	   || general_aggregationlevel.isEdited()))

	 ||
	 (lifecycle_version != null
	  &&
	  (lifecycle_version.isEdited()
	   || lifecycle_status.isEdited()
	   || lifecycle_contribute.isEdited()))
	 
	 ||
	 (metametadata_catalogentry != null
	  &&
	  (metametadata_catalogentry.isEdited()
	   || metametadata_contribute.isEdited()
	   || metametadata_metadatascheme.isEdited()
	   || metametadata_language.isEdited()))
	 
	 ||
	 (technical_format != null
	  &&
	  (technical_format.isEdited()
	   || technical_size.isEdited()
	   || technical_location.isEdited()
	   || technical_requirements.isEdited()
	   || technical_installationremarks.isEdited()
	   || technical_otherplatformrequirements.isEdited()
	   || technical_duration.isEdited()))
	 
	 ||
	 (educational_interactivitytype != null
	  &&
	  (educational_interactivitytype.isEdited()
	   || educational_learningresourcetype.isEdited()
	   || educational_interactivitylevel.isEdited()
	   || educational_semanticdensity.isEdited()
	   || educational_intendedenduserrole.isEdited()
	   || educational_learningcontext.isEdited()
	   || educational_typicalagerange.isEdited()
	   || educational_difficulty.isEdited()
	   || educational_typicallearningtime.isEdited()
	   || educational_description.isEdited()
	   || educational_language.isEdited()))
	 
	 || (rights != null
	     &&
	     rights.isEdited())
	 
	 || (relation != null
	     &&
	     relation.isEdited())
	 
	 || (annotation != null
	     &&
	     annotation.isEdited())
	 
	 || (classification != null
	     &&
	     classification.isEdited()));
*/
