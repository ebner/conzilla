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

/** A fully na�ve implementation of the MetaData interface.
 *
 *  @author Mikael Nilsson
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public abstract class GenericMetaData implements MetaData
{
  
  public GenericMetaData()
    {}

  public abstract Component getComponent();

  public abstract void fireEditEvent(EditEvent ee);

  public abstract boolean isEditable();
  
  /////////// General ////////////
  
  // IMS reserves this... Do not use
  //  public String        get_general_identifier();
  //  void                 set_general_identifier(String i);
  
  MetaData.LangStringType general_title;

  public MetaData.LangStringType get_general_title()
    {
      return general_title;
    }
  public void           set_general_title(MetaData.LangStringType title)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");
      this.general_title = title;

      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "general_title"));
    }
  

  
  MetaData.CatalogEntry[] general_catalogentry;

  public MetaData.CatalogEntry[] get_general_catalogentry()
    {
      return general_catalogentry;
    }
  
  public void set_general_catalogentry(MetaData.CatalogEntry[] ce)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.general_catalogentry = ce;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "general_catalogentry"));
    }
  
  
  String[] general_language;
  public String[]         get_general_language()
    {
      return general_language;
    }
  
  public void             set_general_language(String[] languages)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.general_language = languages;
    
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "general_language"));
    }
  
  MetaData.LangStringType[] general_description;
  public MetaData.LangStringType[] get_general_description()
    {
      return general_description;
    }
  public void             set_general_description(MetaData.LangStringType[] description)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.general_description = description;
    
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "general_description"));
    }
  
  MetaData.LangStringType[] general_keywords;
  public MetaData.LangStringType[] get_general_keywords()
    {
      return general_keywords;
    }
  
  public void             set_general_keywords(MetaData.LangStringType[] keywords)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.general_keywords = keywords;
    
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "general_keywords"));
    }
  
  MetaData.LangStringType[] general_coverage;
  
  public MetaData.LangStringType[] get_general_coverage()
    {
      return general_coverage;
    }
  public void             set_general_coverage(MetaData.LangStringType[] coverage)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.general_coverage = coverage;
    
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "general_coverage"));
    }
  
  
  MetaData.LangStringType general_structure;
  public MetaData.LangStringType   get_general_structure()
    {
      return general_structure;
    }
  public void             set_general_structure(MetaData.LangStringType structure)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.general_structure = structure;
    
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "general_structure"));
    }
  

  int general_aggregationlevel = -1;
  public int              get_general_aggregationlevel()
    {
      return general_aggregationlevel;
    }
  
  public void             set_general_aggregationlevel(int aggregationlevel)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.general_aggregationlevel = aggregationlevel;
    
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "general_aggregationlevel"));
    }
  
  
  
  /////////// Lifecycle ////////////

  MetaData.LangString lifecycle_version;
  public MetaData.LangString   get_lifecycle_version()
    {
      return lifecycle_version;
    }
  
  public void             set_lifecycle_version(MetaData.LangString version)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.lifecycle_version = version;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "lifecycle_version"));
    }
  
  MetaData.LangStringType lifecycle_status;
  public MetaData.LangStringType   get_lifecycle_status()
    {
      return lifecycle_status;
    }
  
  public void             set_lifecycle_status(MetaData.LangStringType status)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.lifecycle_status = status;
    
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "lifecycle_status"));
    }
  
  MetaData.Contribute[] lifecycle_contribute;
  public MetaData.Contribute[] get_lifecycle_contribute()
    {
      return lifecycle_contribute;
    }
  
  public void           set_lifecycle_contribute(MetaData.Contribute[] contribute)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.lifecycle_contribute = contribute;
    
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "lifecycle_contribute"));
    }
  
  
  /////////// Metametadata ////////////

  // IMS reserves this... Do not use
  //  public String        get_metametadata_identifier();
  //  public void          set_metametadata_identifier(String i);

  MetaData.CatalogEntry[] metametadata_catalogentry;

  public MetaData.CatalogEntry[] get_metametadata_catalogentry()
    {
      return metametadata_catalogentry;
    }
  
  public void set_metametadata_catalogentry(MetaData.CatalogEntry[] ce)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.metametadata_catalogentry = ce;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "metametadata_catalogentry"));
    }


  MetaData.Contribute[] metametadata_contribute;
  public MetaData.Contribute[]   get_metametadata_contribute()
    {
      return metametadata_contribute;
    }
  
  public void           set_metametadata_contribute(MetaData.Contribute[] contribute)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.metametadata_contribute = contribute;
    
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "metametadata_contribute"));
    }
  
  String[] metametadata_metadatascheme;
  public String[]   get_metametadata_metadatascheme()
    {
      return metametadata_metadatascheme;
    }
  
  public void           set_metametadata_metadatascheme(String[] scheme)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.metametadata_metadatascheme = scheme;
    
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "metametadata_metadatascheme"));
    }


  String metametadata_language;
  public String   get_metametadata_language()
    {
      return metametadata_language;
    }
  
  public void           set_metametadata_language(String lang)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.metametadata_language = lang;
    
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "metametadata_language"));
    }

  
  
  /////////// Technical ////////////

  MetaData.LangStringType technical_format;
  public MetaData.LangStringType get_technical_format()
    {
      return technical_format;
    }
  
  public void             set_technical_format(MetaData.LangStringType format)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.technical_format = format;

      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "technical_format"));
    }
  

  long technical_size = -1;
  public long              get_technical_size()
    {
      return technical_size;
    }
  
  public void             set_technical_size(long size)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.technical_size = size;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "technical_size"));
    }
  
  MetaData.Location[] technical_location;
  public MetaData.Location[]         get_technical_location()
    {
      return technical_location;
    }
  
  public void             set_technical_location(MetaData.Location[] location)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.technical_location = location;

      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "technical_location"));
    }
  

  MetaData.Requirements[] technical_requirements;
  public MetaData.Requirements[] get_technical_requirements()
    {
      return technical_requirements;
    }
  
  public void          set_technical_requirements(MetaData.Requirements[] requirements)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.technical_requirements = requirements;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "technical_requirements"));
    }
  

  MetaData.LangString technical_installationremarks;
  public MetaData.LangString get_technical_installationremarks()
    {
      return technical_installationremarks;
    }
  
  public void   set_technical_installationremarks(MetaData.LangString installationremarks)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.technical_installationremarks = installationremarks;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "technical_installationremarks"));
    }
  

  MetaData.LangString technical_otherplatformrequirements;
  public MetaData.LangString get_technical_otherplatformrequirements()
    {
      return technical_otherplatformrequirements;
    }
  
  public void           set_technical_otherplatformrequirements(MetaData.LangString otherplatformrequirements)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.technical_otherplatformrequirements = otherplatformrequirements;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "technical_otherplatformrequirements"));
    }
  

  MetaData.DateType technical_duration;
  public MetaData.DateType          get_technical_duration()
    {
      return technical_duration;
    }
  
  public void          set_technical_duration(MetaData.DateType duration)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.technical_duration = duration;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "technical_duration"));
    }
  
   
  /////////// Educational ////////////

  MetaData.LangStringType educational_interactivitytype;
  public MetaData.LangStringType get_educational_interactivitytype()
    {
      return educational_interactivitytype;
    }
  
  public void           set_educational_interactivitytype(MetaData.LangStringType interactivitytype)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_interactivitytype = interactivitytype;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "educational_interactivitytype"));
    }

  
  MetaData.LangString[] educational_learningresourcetype;
  public MetaData.LangString[] get_educational_learningresourcetype()
    {
      return educational_learningresourcetype;
    }
  
  public void             set_educational_learningresourcetype(MetaData.LangString[] learningresourcetype)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_learningresourcetype = learningresourcetype;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "educational_learningresourcetype"));
    }
  

  int educational_interactivitylevel = -1;
  public int           get_educational_interactivitylevel()
    {
      return educational_interactivitylevel;
    }
  
  public void          set_educational_interactivitylevel(int interactivitylevel)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_interactivitylevel = interactivitylevel;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "educational_interactivitylevel"));
    }
  
  int educational_semanticdensity = -1;
  public int           get_educational_semanticdensity()
    {
      return educational_semanticdensity;
    }
  
  public void          set_educational_semanticdensity(int semanticdensity)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_semanticdensity = semanticdensity;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "educational_semanticdensity"));
    }
  
  MetaData.LangStringType[] educational_intendedenduserrole;
  
  public MetaData.LangStringType[] get_educational_intendedenduserrole()
    {
      return educational_intendedenduserrole;
    }
  
  public void             set_educational_intendedenduserrole(MetaData.LangStringType[] intendedenduserrole)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_intendedenduserrole = intendedenduserrole;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "educational_intendedenduserrole"));
    }
  

  MetaData.LangString[] educational_learningcontext;
  public MetaData.LangString[] get_educational_learningcontext()
    {
      return educational_learningcontext;
    }
  
  public void             set_educational_learningcontext(MetaData.LangString[] learningcontext)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_learningcontext = learningcontext;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "educational_learningcontext"));
    }
  

  MetaData.LangString[] educational_typicalagerange;
  
  public MetaData.LangString[] get_educational_typicalagerange()
    {
      return educational_typicalagerange;
    }
  
  public void             set_educational_typicalagerange(MetaData.LangString[] typicalagerange)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_typicalagerange = typicalagerange;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "educational_typicalagerange"));
    }
  

  int educational_difficulty = -1;
  
  public int           get_educational_difficulty()
    {
      return educational_difficulty;
    }
  
  public void          set_educational_difficulty(int difficulty)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_difficulty = difficulty;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "educational_difficulty"));
    }
  

  MetaData.DateType educational_typicallearningtime;
  
  public MetaData.DateType          get_educational_typicallearningtime()
    {
      return educational_typicallearningtime;
    }
  
  public void          set_educational_typicallearningtime(MetaData.DateType typicallearningtime)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_typicallearningtime = typicallearningtime;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "educational_typicallearningtime"));
    }
  
   
  MetaData.LangStringType educational_description;
  public MetaData.LangStringType get_educational_description()
    {
      return educational_description;
    }
  
  public void           set_educational_description(MetaData.LangStringType description)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_description = description;

      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "educational_description"));
    }
  
   
  String[] educational_language;
  
  public String[]      get_educational_language()
    {
      return educational_language;
    }
  
  public void          set_educational_language(String[] language)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.educational_language = language;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "educational_language"));
    }
  
   
  /////////// Rights ////////////
  
  MetaData.Rights rights;
  public MetaData.Rights        get_rights()
    {
      return rights;
    }
  
  public void          set_rights(MetaData.Rights rights)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.rights = rights;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "rights"));
    }
  
   
   
  /////////// Relation ////////////

  MetaData.Relation[] relation;
  public MetaData.Relation[]    get_relation()
    {
      return relation;
    }
  
  public void          set_relation(MetaData.Relation[] relation)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.relation = relation;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "relation"));
    }
  
  
  /////////// Annotation ////////////

  MetaData.Annotation[] annotation;
  
  public MetaData.Annotation[]  get_annotation()
    {
      return annotation;
    }
  
  public void          set_annotation(MetaData.Annotation[] annotation)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.annotation = annotation;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "annotation"));
    }
  
 
  /////////// Classification ////////////

  MetaData.Classification[] classification;
  
  public MetaData.Classification[] get_classification()
    {
      return classification;
    }
  
  public void             set_classification(MetaData.Classification[] classification)
    {
      if(!isEditable())
	throw new ReadOnlyException("This component was not editable.");

      this.classification = classification;
      
      fireEditEvent(new EditEvent(getComponent(), this, Component.METADATA_EDITED,
					    "classification"));
    }
}