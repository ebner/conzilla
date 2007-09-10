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


package se.kth.cid.component;

/** The representation of an IMS meta-data record. It is intended to be exportable over CORBA.
 *
 *  All data fields must obey the following contract:
 *
 *  <ul>
 *    <li>Arrays must not contain null elements</li>
 *    <li>Arrays must not be of size zero (they must instead be null).</li>
 *    <li>LangString.string must not be null (LangString must instead be null itself).</li>
 *    <li>LangStringType.langstring must not be null (LangStringType must instead be null itself).</li>
 *    <li>Location.string must not be null (Location must instead be null itself).</li>
 *  </ul>
 *
 *  Strings may be null or empty, but an empty string is regarded as an actual string value,
 *  and thus may, for example, contradict a restricted vocabulary.
 *
 *  In general, any value (with the above restrictions) is allowed to be null, and is thus
 *  interpreted as not set. Thus, for example, a Relation object may consist of all null elements.
 *  However, it is expected that editing implement does restrict the user input to reasonable
 *  values.
 *
 *  Please note that implementations in general do not enforce this contract, including the
 *  checking of restricted vocabularies, so it is up to the
 *  calling objects to follow the contract. Otherwise, the result is undefined.
 *
 *  Each set_* function fires an EditEvent with a string such as "general_title" describing the
 *  edited element.
 */

public interface MetaData
{

  /** Returns the component that this MetaData is attached to.
   *
   *  It is not obvious that MetaData objects should not be able to exist
   *  outside the world of components... 
   *
   *  @return the component that this MetaData is attached to.
   */
  Component getComponent();
  
  class LangString
  {
    public String language;
    public String string;

    public LangString(String language, String string)
      {
	this.language = language;
	this.string   = string;
      }

    public String toString()
      {
	return "LangString[language=" + language + ",string=" + string + "]";
      }
  }

  class LangStringType
  {
    public LangString[] langstring;

    public LangStringType(LangString[] langstring)
      {
	this.langstring = langstring;
      }
  }

  class DateType
  {
    public String         datetime;
    public LangStringType description;
    
    public DateType(String datetime, LangStringType description)
      {
	this.datetime    = datetime;
	this.description = description;
      }
  }


  /////////// General ////////////
  
  // IMS reserves this... Do not use
  //  String        get_general_identifier();
  //  void          set_general_identifier(String i);
  
  LangStringType   get_general_title();
  void             set_general_title(LangStringType str);

  
  class CatalogEntry
  {
    public String         catalogue;
    public LangStringType entry;

    public CatalogEntry(String catalogue, LangStringType entry)
      {
	this.catalogue = catalogue;
	this.entry     = entry;
      }
  }

  CatalogEntry[]   get_general_catalogentry();
  void             set_general_catalogentry(CatalogEntry[] ent);
  
  String[]         get_general_language();
  void             set_general_language(String[] lang);

  LangStringType[] get_general_description();
  void             set_general_description(LangStringType[] str);

  LangStringType[] get_general_keywords();
  void             set_general_keywords(LangStringType[] str);

  LangStringType[] get_general_coverage();
  void             set_general_coverage(LangStringType[] str);
  
  LangStringType   get_general_structure();
  void             set_general_structure(LangStringType structure);

  int              get_general_aggregationlevel();
  void             set_general_aggregationlevel(int level);


  /////////// Lifecycle ////////////

  // Note: Single string instance as defined by IMS XML binding. 
  LangString       get_lifecycle_version();
  void             set_lifecycle_version(LangString version);

  LangStringType   get_lifecycle_status();
  void             set_lifecycle_status(LangStringType status);

  
  
  class Contribute
  {
    public LangString     role;	// Note: Single string instance as defined by IMS XML binding. 
    public String[]       entity; // vCard!
    public DateType       date;
    
    public Contribute(LangString role, String[] entity, DateType date)
      {
	this.role   = role;
	this.entity = entity;
	this.date   = date;
      }
  }
  
  Contribute[]   get_lifecycle_contribute();
  void           set_lifecycle_contribute(Contribute[] c);

  /////////// Metametadata ////////////

  // IMS reserves this... Do not use
  //  String        get_metametadata_identifier();
  //  void          set_metametadata_identifier(String i);
  

  CatalogEntry[] get_metametadata_catalogentry();
  void           set_metametadata_catalogentry(CatalogEntry[] cats);

  Contribute[]   get_metametadata_contribute();
  void           set_metametadata_contribute(Contribute[] c);

  String[]       get_metametadata_metadatascheme();
  void           set_metametadata_metadatascheme(String[] scheme);

  String         get_metametadata_language();
  void           set_metametadata_language(String lang);

  /////////// Technical ////////////
  
  LangStringType   get_technical_format();
  void             set_technical_format(LangStringType format);
  
  long             get_technical_size();
  void             set_technical_size(long size);

  class Location
  {
    public String type; // "TEXT" or "URI".
    public String string;

    public Location(String type, String string)
      {
	this.type = type;
	this.string = string;
      }
  }
  
  Location[]       get_technical_location();
  void             set_technical_location(Location[] locs);
  
  class Requirements
  {
    public LangString     type;	// Note: Single string instance as defined by IMS XML binding. 
    public LangString     name;	// Note: Single string instance as defined by IMS XML binding. 
    public String         minimumversion;
    public String         maximumversion;
    
    public Requirements(LangString type, LangString name,
			String minimumversion, String maximumversion)
      {
	this.type       = type;
	this.name       = name;
	this.minimumversion = minimumversion;
	this.maximumversion = maximumversion;
      }
  }
  
  Requirements[] get_technical_requirements();
  void           set_technical_requirements(Requirements[] requirements);

  // Note: Single string instance as defined by IMS XML binding. 
  LangString     get_technical_installationremarks();
  void           set_technical_installationremarks(LangString rem);

  // Note: Single string instance as defined by IMS XML binding. 
  LangString     get_technical_otherplatformrequirements();
  void           set_technical_otherplatformrequirements(LangString rem);

  DateType       get_technical_duration();
  void           set_technical_duration(DateType d);

  /////////// Educational ////////////

  LangStringType get_educational_interactivitytype();
  void           set_educational_interactivitytype(LangStringType type);

  // Note: Single string instance as defined by IMS XML binding. 
  LangString[]     get_educational_learningresourcetype();
  void             set_educational_learningresourcetype(LangString[] types);

  int              get_educational_interactivitylevel();
  void             set_educational_interactivitylevel(int level);

  int              get_educational_semanticdensity();
  void             set_educational_semanticdensity(int dens);

  LangStringType[] get_educational_intendedenduserrole();
  void             set_educational_intendedenduserrole(LangStringType[] roles);

  // Note: Single string instance as defined by IMS XML binding. 
  LangString[]     get_educational_learningcontext();
  void             set_educational_learningcontext(LangString[] cont);

  // Note: Single string instance as defined by IMS XML binding. 
  LangString[]     get_educational_typicalagerange();
  void             set_educational_typicalagerange(LangString[] ranges);

  int              get_educational_difficulty();
  void             set_educational_difficulty(int diff);

  DateType         get_educational_typicallearningtime();
  void             set_educational_typicallearningtime(DateType d);
 
  LangStringType   get_educational_description();
  void             set_educational_description(LangStringType desc);

  String[]         get_educational_language();
  void             set_educational_language(String[] langs);

  /////////// Rights ////////////
  
  class Rights
  {
    public LangStringType cost;
    public LangStringType copyrightandotherrestrictions;
    public LangStringType description;
    public Rights(LangStringType cost, LangStringType copyrightetc,
		  LangStringType description)
      {
	this.cost                          = cost;
	this.copyrightandotherrestrictions = copyrightetc;
	this.description                   = description;
      }
  }

  Rights        get_rights();
  void          set_rights(Rights r);


  /////////// Relation ////////////
  
  class Relation
  {
    // Note: Single string instance as defined by IMS XML binding. 
    public LangString     kind;
    
    // IMS reserves this. Do not use.
    // public String resource_identity;

    public LangStringType resource_description;
    
    // This is an extension: relation.resource.(extension.)location
    // If it exists, it points to a contentdescription.
    public String         resource_location;
    
    public Relation(LangString kind, LangStringType description,
		    String location)
      {
	this.kind                 = kind;
	this.resource_description = description;
	this.resource_location    = location;
      }
  }

  Relation[]    get_relation();
  void          set_relation(Relation[] rel);


  /////////// Annotation ////////////

  
  class Annotation
  {
    public String         person; //vCard
    public DateType       date;
    public LangStringType description;

    public Annotation(String person, DateType date, LangStringType description)
      {
	this.person      = person;
	this.date        = date;
	this.description = description;
      }
  }
  
  Annotation[]  get_annotation();
  void          set_annotation(Annotation[] ann);

  /////////// Classification ////////////
  
  class Taxon
  {
    public String         id;
    public LangStringType entry;

    public Taxon(String id, LangStringType entry)
      {
	this.id    = id;
	this.entry = entry;
      }
  }
  
  class TaxonPath
  {
    public String  source;
    public Taxon[] taxon;
    
    public TaxonPath(String source, Taxon[] taxon)
      {
	this.source = source;
	this.taxon  = taxon;
      }
  }
  
  class Classification
  {
    public LangString       purpose; // Note: Single string instance as defined by IMS XML binding. 
    public TaxonPath[]      taxonpath;
    public LangStringType   description;
    public LangStringType[] keywords;

    public Classification(LangString purpose, TaxonPath[] taxonpath,
			  LangStringType description, LangStringType[] keywords)
      {
	this.purpose     = purpose;
	this.taxonpath   = taxonpath;
	this.description = description;
	this.keywords    = keywords;
      }
    
  }

  Classification[] get_classification();
  void             set_classification(Classification[] cs);
}
