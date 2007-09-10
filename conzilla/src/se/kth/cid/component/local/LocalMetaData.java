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


package se.kth.cid.component.local;

import  se.kth.cid.component.*;
import  se.kth.cid.neuron.*;
import  se.kth.cid.util.*;
import  se.kth.cid.neuron.local.*;

import java.util.*;

public class LocalMetaData implements MetaData
{

  LocalComponent component;
  
  public LocalMetaData(LocalComponent component)
    {
      this.component = component;
    }

  public Component getComponent()
    {
      return component;
    }
  
  
  /////////// General ////////////
  
  // IMS reserves this... Do not use
  //  public String        get_general_identifier();
  //  void                 set_general_identifier(String i);
  
  LangStringType general_title;

  public LangStringType get_general_title()
    {
      return general_title;
    }
  public void           set_general_title(LangStringType title)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");
      this.general_title = title;

      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "general_title"));
    }
  

  
  CatalogEntry[] general_catalogentry;

  public CatalogEntry[] get_general_catalogentry()
    {
      return general_catalogentry;
    }
  
  public void set_general_catalogentry(CatalogEntry[] ce)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.general_catalogentry = ce;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "general_catalogentry"));
    }
  
  
  String[] general_language;
  public String[]         get_general_language()
    {
      return general_language;
    }
  
  public void             set_general_language(String[] languages)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.general_language = languages;
    
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "general_language"));
    }
  
  LangStringType[] general_description;
  public LangStringType[] get_general_description()
    {
      return general_description;
    }
  public void             set_general_description(LangStringType[] description)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.general_description = description;
    
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "general_description"));
    }
  
  LangStringType[] general_keywords;
  public LangStringType[] get_general_keywords()
    {
      return general_keywords;
    }
  
  public void             set_general_keywords(LangStringType[] keywords)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.general_keywords = keywords;
    
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "general_keywords"));
    }
  
  LangStringType[] general_coverage;
  
  public LangStringType[] get_general_coverage()
    {
      return general_coverage;
    }
  public void             set_general_coverage(LangStringType[] coverage)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.general_coverage = coverage;
    
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "general_coverage"));
    }
  
  
  LangStringType general_structure;
  public LangStringType   get_general_structure()
    {
      return general_structure;
    }
  public void             set_general_structure(LangStringType structure)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.general_structure = structure;
    
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "general_structure"));
    }
  

  int general_aggregationlevel = -1;
  public int              get_general_aggregationlevel()
    {
      return general_aggregationlevel;
    }
  
  public void             set_general_aggregationlevel(int aggregationlevel)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.general_aggregationlevel = aggregationlevel;
    
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "general_aggregationlevel"));
    }
  
  
  
  /////////// Lifecycle ////////////

  LangString lifecycle_version;
  public LangString   get_lifecycle_version()
    {
      return lifecycle_version;
    }
  
  public void             set_lifecycle_version(LangString version)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.lifecycle_version = version;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "lifecycle_version"));
    }
  
  LangStringType lifecycle_status;
  public LangStringType   get_lifecycle_status()
    {
      return lifecycle_status;
    }
  
  public void             set_lifecycle_status(LangStringType status)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.lifecycle_status = status;
    
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "lifecycle_status"));
    }
  
  Contribute[] lifecycle_contribute;
  public Contribute[] get_lifecycle_contribute()
    {
      return lifecycle_contribute;
    }
  
  public void           set_lifecycle_contribute(Contribute[] contribute)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.lifecycle_contribute = contribute;
    
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "lifecycle_contribute"));
    }
  
  
  /////////// Metametadata ////////////

  // IMS reserves this... Do not use
  //  public String        get_metametadata_identifier();
  //  public void          set_metametadata_identifier(String i);

  CatalogEntry[] metametadata_catalogentry;

  public CatalogEntry[] get_metametadata_catalogentry()
    {
      return metametadata_catalogentry;
    }
  
  public void set_metametadata_catalogentry(CatalogEntry[] ce)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.metametadata_catalogentry = ce;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "metametadata_catalogentry"));
    }


  Contribute[] metametadata_contribute;
  public Contribute[]   get_metametadata_contribute()
    {
      return metametadata_contribute;
    }
  
  public void           set_metametadata_contribute(Contribute[] contribute)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.metametadata_contribute = contribute;
    
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "metametadata_contribute"));
    }
  
  String[] metametadata_metadatascheme;
  public String[]   get_metametadata_metadatascheme()
    {
      return metametadata_metadatascheme;
    }
  
  public void           set_metametadata_metadatascheme(String[] scheme)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.metametadata_metadatascheme = scheme;
    
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "metametadata_metadatascheme"));
    }


  String metametadata_language;
  public String   get_metametadata_language()
    {
      return metametadata_language;
    }
  
  public void           set_metametadata_language(String lang)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.metametadata_language = lang;
    
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "metametadata_language"));
    }

  
  
  /////////// Technical ////////////

  LangStringType technical_format;
  public LangStringType get_technical_format()
    {
      return technical_format;
    }
  
  public void             set_technical_format(LangStringType format)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.technical_format = format;

      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "technical_format"));
    }
  

  int technical_size = -1;
  public int              get_technical_size()
    {
      return technical_size;
    }
  
  public void             set_technical_size(int size)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.technical_size = size;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "technical_size"));
    }
  
  Location[] technical_location;
  public Location[]         get_technical_location()
    {
      return technical_location;
    }
  
  public void             set_technical_location(Location[] location)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.technical_location = location;

      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "technical_location"));
    }
  

  Requirements[] technical_requirements;
  public Requirements[] get_technical_requirements()
    {
      return technical_requirements;
    }
  
  public void          set_technical_requirements(Requirements[] requirements)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.technical_requirements = requirements;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "technical_requirements"));
    }
  

  LangString technical_installationremarks;
  public LangString get_technical_installationremarks()
    {
      return technical_installationremarks;
    }
  
  public void   set_technical_installationremarks(LangString installationremarks)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.technical_installationremarks = installationremarks;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "technical_installationremarks"));
    }
  

  LangString technical_otherplatformrequirements;
  public LangString get_technical_otherplatformrequirements()
    {
      return technical_otherplatformrequirements;
    }
  
  public void           set_technical_otherplatformrequirements(LangString otherplatformrequirements)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.technical_otherplatformrequirements = otherplatformrequirements;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "technical_otherplatformrequirements"));
    }
  

  DateType technical_duration;
  public DateType          get_technical_duration()
    {
      return technical_duration;
    }
  
  public void          set_technical_duration(DateType duration)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.technical_duration = duration;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "technical_duration"));
    }
  
   
  /////////// Educational ////////////

  LangStringType educational_interactivitytype;
  public LangStringType get_educational_interactivitytype()
    {
      return educational_interactivitytype;
    }
  
  public void           set_educational_interactivitytype(LangStringType interactivitytype)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_interactivitytype = interactivitytype;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "educational_interactivitytype"));
    }

  
  LangString[] educational_learningresourcetype;
  public LangString[] get_educational_learningresourcetype()
    {
      return educational_learningresourcetype;
    }
  
  public void             set_educational_learningresourcetype(LangString[] learningresourcetype)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_learningresourcetype = learningresourcetype;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "educational_learningresourcetype"));
    }
  

  int educational_interactivitylevel = -1;
  public int           get_educational_interactivitylevel()
    {
      return educational_interactivitylevel;
    }
  
  public void          set_educational_interactivitylevel(int interactivitylevel)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_interactivitylevel = interactivitylevel;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "educational_interactivitylevel"));
    }
  
  int educational_semanticdensity = -1;
  public int           get_educational_semanticdensity()
    {
      return educational_semanticdensity;
    }
  
  public void          set_educational_semanticdensity(int semanticdensity)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_semanticdensity = semanticdensity;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "educational_semanticdensity"));
    }
  
  LangStringType[] educational_intendedenduserrole;
  
  public LangStringType[] get_educational_intendedenduserrole()
    {
      return educational_intendedenduserrole;
    }
  
  public void             set_educational_intendedenduserrole(LangStringType[] intendedenduserrole)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_intendedenduserrole = intendedenduserrole;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "educational_intendedenduserrole"));
    }
  

  LangString[] educational_learningcontext;
  public LangString[] get_educational_learningcontext()
    {
      return educational_learningcontext;
    }
  
  public void             set_educational_learningcontext(LangString[] learningcontext)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_learningcontext = learningcontext;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "educational_learningcontext"));
    }
  

  LangString[] educational_typicalagerange;
  
  public LangString[] get_educational_typicalagerange()
    {
      return educational_typicalagerange;
    }
  
  public void             set_educational_typicalagerange(LangString[] typicalagerange)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_typicalagerange = typicalagerange;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "educational_typicalagerange"));
    }
  

  int educational_difficulty = -1;
  
  public int           get_educational_difficulty()
    {
      return educational_difficulty;
    }
  
  public void          set_educational_difficulty(int difficulty)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_difficulty = difficulty;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "educational_difficulty"));
    }
  

  DateType educational_typicallearningtime;
  
  public DateType          get_educational_typicallearningtime()
    {
      return educational_typicallearningtime;
    }
  
  public void          set_educational_typicallearningtime(DateType typicallearningtime)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_typicallearningtime = typicallearningtime;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "educational_typicallearningtime"));
    }
  
   
  LangStringType educational_description;
  public LangStringType get_educational_description()
    {
      return educational_description;
    }
  
  public void           set_educational_description(LangStringType description)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_description = description;

      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "educational_description"));
    }
  
   
  String[] educational_language;
  
  public String[]      get_educational_language()
    {
      return educational_language;
    }
  
  public void          set_educational_language(String[] language)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_language = language;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "educational_language"));
    }
  
   
  /////////// Rights ////////////
  
  Rights rights;
  public Rights        get_rights()
    {
      return rights;
    }
  
  public void          set_rights(Rights rights)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.rights = rights;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "rights"));
    }
  
   
   
  /////////// Relation ////////////

  Relation[] relation;
  public Relation[]    get_relation()
    {
      return relation;
    }
  
  public void          set_relation(Relation[] relation)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.relation = relation;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "relation"));
    }
  
  
  /////////// Annotation ////////////

  Annotation[] annotation;
  
  public Annotation[]  get_annotation()
    {
      return annotation;
    }
  
  public void          set_annotation(Annotation[] annotation)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.annotation = annotation;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "annotation"));
    }
  
 
  /////////// Classification ////////////

  Classification[] classification;
  
  public Classification[] get_classification()
    {
      return classification;
    }
  
  public void             set_classification(Classification[] classification)
    {
      if(!component.isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.classification = classification;
      
      component.fireEditEvent(new EditEvent(this, Component.METADATA_EDITED,
					 "classification"));
    }
}
