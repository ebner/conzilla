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

/* The fifteen Dublin core elements:
 *
 *  Title       = general_title
 *  Creator     = lifecycle_contribution
 *  Subject     = general_coverage
 *  Description = general_description
 *  Publisher   = lifecycle_contribution
 *  Contributor = lifecycle_contribution
 *  Date        = lifecycle_contribution
 *  Type        = technical_format
 *  Format      = technical_format
 *  Identifier  = *general_identifier
 *  Source      = lifecycle_contribution
 *  Language    = general_language
 *  Relation    = relation
 *  Coverage    = general_coverage
 *  Rights      = rights
 *
 * A '*' means that it is missing here.
 */
public class DublinCorePanel extends JScrollPane
{
  Component component;
  MetaData  metaData;

  EditListener editListener;
  
  MetaDataPanel         metaDataPanel;
  
  LangStringList        general_title;
  LanguageList          general_language;
  LangStringTypeList    general_description;
  LangStringTypeList    general_coverage;

  ContributionList      lifecycle_contribute;
  
  LangStringList        technical_format;

  LangStringList        educational_learningresourcetype;

  RightsPanel           rights;
  
  RelationList          relation;

  ClassificationList    classification;
  
  public DublinCorePanel(Component comp)
    {
      this.component = comp;
      this.metaData  = comp.getMetaData();
      
      metaDataPanel = new MetaDataPanel();
      
      setViewportView(metaDataPanel);
      
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

      makePanelLater();
    }

  // Be sure to call!
  public void detach()
    { 
      detachFields();
      component.removeEditListener(editListener);
    }

  void detachFields()
    {
      general_title.detach();
      general_language.detach();
      general_description.detach();
      general_coverage.detach();
      
      lifecycle_contribute.detach();
      
      technical_format.detach();
      
      educational_learningresourcetype.detach();
      
      relation.detach();
      
      rights.detach();
      
      classification.detach();
    }
  
  
  void updateMetaData(String target)
    {
      detachFields();
      metaDataPanel.removeAll();
      makePanelLater();
    }

  void makePanelLater()
    {
      // Easy way to increase response time... delay initialization.
      SwingUtilities.invokeLater(new Runnable()
	{
	  public void run()
	    {
	      makePanel();
	    }
	});
    }
  
  void makePanel()
    {
      general_title = new LangStringList(metaData.get_general_title(), true,
					 false, null, "general_title");
      if(metaData.get_general_title() != null)
	metaDataPanel.addPanel("Title", general_title);
      

      general_language = new LanguageList(metaData.get_general_language(),
					  false, null, "general_language");
      if(metaData.get_general_language() != null)
	metaDataPanel.addPanel("Language", general_language);

	  
      general_description = new LangStringTypeList(metaData.get_general_description(), true,
						   false, null, "general_description");
      if(metaData.get_general_description() != null)
	metaDataPanel.addPanel("Description", general_description);
	  
      
      general_coverage = new LangStringTypeList(metaData.get_general_coverage(), false,
						false, null, "general_coverage");
      if(metaData.get_general_coverage() != null)
	metaDataPanel.addPanel("Coverage", general_coverage);

      
      lifecycle_contribute = new ContributionList(metaData.get_lifecycle_contribute(),
						  false, null, "lifecycle_contribute");
      if(metaData.get_lifecycle_contribute() != null)
	metaDataPanel.addPanel("Contribution", lifecycle_contribute);
      

      technical_format = new LangStringList(metaData.get_technical_format(), false,
					    false, null, "technical_format");
      if(metaData.get_technical_format() != null)
	metaDataPanel.addPanel("Format", technical_format);
	  
      
      educational_learningresourcetype = new LangStringList(metaData.get_educational_learningresourcetype(), false,
							    false, null, "educational_learningresourcetype");
      if(metaData.get_educational_learningresourcetype() != null)
	metaDataPanel.addPanel("Learning Resource Type", educational_learningresourcetype);
      
      rights = new RightsPanel(metaData.get_rights(),
			       false, null, "rights");
      if(metaData.get_rights() != null)
	metaDataPanel.addPanel("Rights", rights);

      relation = new RelationList(metaData.get_relation(),
				  false, null, "relation");
      if(metaData.get_relation() != null)
	metaDataPanel.addPanel("Relation", relation);
      
      classification = new ClassificationList(metaData.get_classification(),
					      false, null, "classification");
      if(metaData.get_classification() != null)
	metaDataPanel.addPanel("Classification", classification);
    }  
}

