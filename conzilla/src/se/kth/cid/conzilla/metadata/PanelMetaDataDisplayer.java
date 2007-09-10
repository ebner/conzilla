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
import java.awt.*;
import java.awt.event.*;
import se.kth.cid.component.Component;


public class PanelMetaDataDisplayer extends JPanel 
{
  Component component;

  JTabbedPane tabPane;

  EditListener editListener;

  LangStringList        general_title;
  CatalogEntryList      general_catalogentry;
  LanguageList          general_language;
  LangStringTypeList    general_description;
  LangStringTypeList    general_keywords;
  LangStringTypeList    general_coverage;
  LangStringList        general_structure;
  AggregationLevelCombo general_aggregationlevel;

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
  
  
  public PanelMetaDataDisplayer(Component comp)
    {
      this.component = comp;
      
      setLayout(new BorderLayout());
      
      tabPane = new JTabbedPane();

      add(tabPane, BorderLayout.CENTER);
      
      addTabs();
      
      editListener = new EditListener()
	{
	  public void componentEdited(EditEvent e)
	    {
	      switch(e.getEditType())
		{
		case Component.METADATA_EDITED:
		  updatedMetaData();
		  break;
		default:
		}
	    }
	};
      component.addEditListener(editListener);

      updatedMetaData();
    }

  void updatedMetaData()
    {
      
    }
  

  void addTabs()
    {
      MetaData md = component.getMetaData();


      // --- General ---
      
      JPanel generalTab = new JPanel();
      generalTab.setLayout(new BoxLayout(generalTab, BoxLayout.Y_AXIS));
      
      
      /////////
      
      general_title = new LangStringList(md.get_general_title(),
					 component.isEditable());
      add(general_title, "Title", generalTab);
      
      /////////
      
      general_catalogentry = new CatalogEntryList(md.get_general_catalogentry(),
						  component.isEditable());
      add(general_catalogentry, "Catalog Entry", generalTab);
      
      /////////

      general_language = new LanguageList(md.get_general_language(),
					  component.isEditable());
      add(general_language, "Language", generalTab);

      /////////

      general_description = new LangStringTypeList(md.get_general_description(),
						   component.isEditable());
      add(general_description, "Description", generalTab);

      /////////

      general_keywords = new LangStringTypeList(md.get_general_keywords(),
						component.isEditable());
      add(general_keywords, "Keywords", generalTab);
      
      /////////

      general_coverage = new LangStringTypeList(md.get_general_coverage(),
						component.isEditable());
      add(general_coverage, "Coverage", generalTab);

      /////////
      
      general_structure = new LangStringList(md.get_general_structure(),
					     component.isEditable());
      add(general_structure, "Structure", generalTab);

      /////////
      
      general_aggregationlevel = new AggregationLevelCombo(md.get_general_aggregationlevel(), component.isEditable());
      add(general_aggregationlevel, "Aggregation Level", generalTab);




      JScrollPane genPane = new JScrollPane(generalTab);
      tabPane.addTab("General", genPane);

      // --- Lifecycle ---
      
      JPanel lifecycleTab = new JPanel();
      lifecycleTab.setLayout(new BoxLayout(lifecycleTab, BoxLayout.Y_AXIS));

      /////////
      
      lifecycle_version = new LangStringComponent(md.get_lifecycle_version(),
						  component.isEditable());
      add(lifecycle_version, "Version", lifecycleTab);

      /////////
      
      lifecycle_status = new LangStringList(md.get_lifecycle_status(),
					    component.isEditable());
      add(lifecycle_status, "Status", lifecycleTab);

      /////////
      
      lifecycle_contribute = new ContributionList(md.get_lifecycle_contribute(),
						  component.isEditable());
      add(lifecycle_contribute, "Contribution", lifecycleTab);
      
      
      JScrollPane lcPane = new JScrollPane(lifecycleTab);
      tabPane.addTab("Lifecycle", lcPane);


      // --- Metametadata ---
      
      JPanel metametadataTab = new JPanel();
      metametadataTab.setLayout(new BoxLayout(metametadataTab,
					      BoxLayout.Y_AXIS));

      /////////
      
      metametadata_catalogentry = new CatalogEntryList(md.get_metametadata_catalogentry(),
						       component.isEditable());
      add(metametadata_catalogentry, "Catalog Entry", metametadataTab);

      /////////
      
      metametadata_contribute = new ContributionList(md.get_metametadata_contribute(),
						     component.isEditable());
      add(metametadata_contribute, "Contribute", metametadataTab);
      
      /////////
      
      metametadata_metadatascheme = new StringList("Scheme", md.get_metametadata_metadatascheme(),
						   component.isEditable());
      add(metametadata_metadatascheme, "Metadata Scheme", metametadataTab);
      
      /////////
      
      metametadata_language = new LanguageBox(md.get_metametadata_language(),
					      component.isEditable());
      add(metametadata_language, "Metadata Language", metametadataTab);
      
      JScrollPane mmdPane = new JScrollPane(metametadataTab);
      tabPane.addTab("Metametadata", mmdPane);


      // --- Technical ---
      
      JPanel technicalTab = new JPanel();
      technicalTab.setLayout(new BoxLayout(technicalTab,
					   BoxLayout.Y_AXIS));
      
      /////////
      
      technical_format = new LangStringList(md.get_technical_format(),
					    component.isEditable());
      add(technical_format, "Format", technicalTab);
      
      /////////
      
      technical_size = new IntField(10, md.get_technical_size(),
				    component.isEditable());
      add(technical_size, "Size", technicalTab);
      
      /////////
      
      technical_location= new LocationList(md.get_technical_location(),
					   component.isEditable());
      add(technical_location, "Location", technicalTab);
      


      JScrollPane technicalPane = new JScrollPane(technicalTab);
      tabPane.addTab("Technical", technicalPane);


      
//	tabPane.addTab("Educational");
//	tabPane.addTab("Rights");
//	tabPane.addTab("Relation");
//	tabPane.addTab("Annotation");
//	tabPane.addTab("Classification");
      
    }

  void add(JComponent comp, String title, JPanel tab)
    {
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createTitledBorder(title));
      panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      panel.add(comp);
      
      tab.add(panel);
    }
}
