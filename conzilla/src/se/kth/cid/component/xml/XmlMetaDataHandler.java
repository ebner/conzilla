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


package se.kth.cid.component.xml;
import se.kth.cid.component.*;
import se.kth.cid.xml.*;
import se.kth.cid.util.*;

import java.util.*;


/** Loads metadata from an XML-doc.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlMetaDataHandler extends XmlLoaderHelper
{
  /** Constructing an XmlMetaDataHandler is not allowed.
   */
  private XmlMetaDataHandler()
    {}
  
  /** Loads metadata from an XML tree.
   *  @param metaData the MetaData to load all meta-data into.
   *  @param root the MetaData element.
   *
   *  @exception ReadOnlyException if the MetaData was read-only.
   */
  public static void load(MetaData metaData, XmlElement root)
    throws ReadOnlyException, ComponentException
    {
      XmlElement record         = getSubElement(root, "record");


      
      XmlElement metametadata      = maybeGetSubElement(record, "metametadata");

      if(metametadata != null)
	{
	  
	  XmlElement[] catalogentries = metametadata.getSubElements("catalogentry");
	  if(catalogentries.length > 0)
	    {
	      MetaData.CatalogEntry[] ce = new MetaData.CatalogEntry[catalogentries.length];
	      for(int i = 0; i < catalogentries.length; i++)
		{
		  ce[i] = new MetaData.CatalogEntry(maybeGetString(catalogentries[i], "catalogue"),
						    maybeGetLangStringType(catalogentries[i], "entry"));
		}
	      metaData.set_metametadata_catalogentry(ce);
	    }

	  
	  XmlElement[] contributes = metametadata.getSubElements("contribute");
	  if(contributes.length > 0)
	    {
	      MetaData.Contribute[] co = new MetaData.Contribute[contributes.length];
	      for(int i = 0; i < contributes.length; i++)
		{
		  co[i] = new MetaData.Contribute(maybeGetLangString(contributes[i], "role"),
						  maybeGetEntities(contributes[i]),
						  maybeGetDateType(contributes[i], "date"));
		}
	      metaData.set_metametadata_contribute(co);
	    }
	  
	  metaData.set_metametadata_metadatascheme(maybeGetStrings(metametadata, "metadatascheme"));
	  metaData.set_metametadata_language(maybeGetString(metametadata, "language"));
	}




      


      XmlElement general        = maybeGetSubElement(record, "general");
      if(general != null)
	{
	  metaData.set_general_title(maybeGetLangStringType(general, "title"));
	  
	  XmlElement[] catalogentries = general.getSubElements("catalogentry");
	  if(catalogentries.length > 0)
	    {
	      MetaData.CatalogEntry[] ce = new MetaData.CatalogEntry[catalogentries.length];
	      for(int i = 0; i < catalogentries.length; i++)
		{
		  ce[i] = new MetaData.CatalogEntry(maybeGetString(catalogentries[i], "catalogue"),
						    maybeGetLangStringType(catalogentries[i], "entry"));
		}
	      metaData.set_general_catalogentry(ce);
	    }
	  metaData.set_general_language(maybeGetStrings(general, "language"));
	  metaData.set_general_description(maybeGetLangStringTypes(general, "description"));
	  metaData.set_general_keywords(maybeGetLangStringTypes(general, "keywords"));
	  metaData.set_general_coverage(maybeGetLangStringTypes(general, "coverage"));
	  metaData.set_general_structure(maybeGetLangStringType(general, "structure"));
	  metaData.set_general_aggregationlevel(maybeGetInt(general, "aggregationlevel"));
	} 
      
		




      XmlElement lifecycle      = maybeGetSubElement(record, "lifecycle");

      if(lifecycle != null)
	{
	  metaData.set_lifecycle_version(maybeGetLangString(lifecycle, "version"));
	  metaData.set_lifecycle_status(maybeGetLangStringType(lifecycle, "status"));

	  XmlElement[] contributes = lifecycle.getSubElements("contribute");
	  if(contributes.length > 0)
	    {
	      MetaData.Contribute[] co = new MetaData.Contribute[contributes.length];
	      for(int i = 0; i < contributes.length; i++)
		{
		  co[i] = new MetaData.Contribute(maybeGetLangString(contributes[i], "role"),
						  maybeGetEntities(contributes[i]),
						  maybeGetDateType(contributes[i], "date"));
		}
	      metaData.set_lifecycle_contribute(co);
	    }
	}





      



      XmlElement technical      = maybeGetSubElement(record, "technical");
      if(technical != null)
	{
	  metaData.set_technical_format(maybeGetLangStringType(technical, "format"));
	  metaData.set_technical_size(maybeGetInt(technical, "size"));
	  metaData.set_technical_location(maybeGetLocations(technical));

	  XmlElement[] requirements = technical.getSubElements("requirements");
	  if(requirements.length > 0)
	    {
	      MetaData.Requirements[] re = new MetaData.Requirements[requirements.length];
	      for(int i = 0; i < requirements.length; i++)
		{
		  re[i] = new MetaData.Requirements(maybeGetLangString(requirements[i], "type"),
						    maybeGetLangString(requirements[i], "name"),
						    maybeGetString(requirements[i], "minimumversion"),
						    maybeGetString(requirements[i], "maximumversion"));
		}
	      metaData.set_technical_requirements(re);
	    }
	  metaData.set_technical_installationremarks(maybeGetLangString(technical, "installationremarks"));
	  metaData.set_technical_otherplatformrequirements(maybeGetLangString(technical, "otherplatformrequirements"));
	  metaData.set_technical_duration(maybeGetDateType(technical, "duration"));
	}


      


      XmlElement educational    = maybeGetSubElement(record, "educational");
      if(educational != null)
	{
	  metaData.set_educational_interactivitytype(maybeGetLangStringType(educational, "interactivitytype"));
	  metaData.set_educational_learningresourcetype(maybeGetLangStrings(educational, "learningresourcetype"));
	  metaData.set_educational_interactivitylevel(maybeGetInt(educational, "interactivitylevel"));
	  metaData.set_educational_semanticdensity(maybeGetInt(educational, "semanticdensity"));
	  metaData.set_educational_intendedenduserrole(maybeGetLangStringTypes(educational, "intendedenduserrole"));
	  metaData.set_educational_learningcontext(maybeGetLangStrings(educational, "learningcontext"));
	  metaData.set_educational_typicalagerange(maybeGetLangStrings(educational, "typicalagerange"));
	  metaData.set_educational_difficulty(maybeGetInt(educational, "difficulty"));
	  metaData.set_educational_typicallearningtime(maybeGetDateType(educational, "typicallearningtime"));
	  metaData.set_educational_description(maybeGetLangStringType(educational, "description"));
	  metaData.set_educational_language(maybeGetStrings(educational, "language"));
	}
      
      XmlElement rights            = maybeGetSubElement(record, "rights");
      if(rights != null)
	{
	  metaData.set_rights(new MetaData.Rights(maybeGetLangStringType(rights, "cost"),
						 maybeGetLangStringType(rights, "copyrightandotherrestrictions"),
						  maybeGetLangStringType(rights, "description")));
	}
      
	
      XmlElement[] relations       = record.getSubElements("relation");
      if(relations.length > 0)
	{
	  MetaData.Relation[] re = new MetaData.Relation[relations.length];
	  for(int i = 0; i < relations.length; i++)
	    {
	      MetaData.LangString     kind = maybeGetLangString(relations[i], "kind");
	      MetaData.LangStringType description = null;
	      String                  location = null;
	      
	      XmlElement resource = maybeGetSubElement(relations[i], "resource");
	      if(resource != null)
		{
		  description = maybeGetLangStringType(resource, "description");
		  XmlElement extension = maybeGetSubElement(resource, "extension");
		  if(extension != null)
		    location = maybeGetString(extension, "location");
		}
	      
	      re[i] = new MetaData.Relation(kind, description, location);
	    }
	  metaData.set_relation(re);
	}
      
      
      XmlElement[] annotations     = record.getSubElements("annotation");
      if(annotations.length > 0)
	{
	  MetaData.Annotation[] an = new MetaData.Annotation[annotations.length];
	  for(int i = 0; i < annotations.length; i++)
	    {
	      an[i] = new MetaData.Annotation(maybeGetEntity(annotations[i]),
				     maybeGetDateType(annotations[i], "date"),
				     maybeGetLangStringType(annotations[i], "description"));
	    }
	  metaData.set_annotation(an);
	}
      XmlElement[] classifications = record.getSubElements("classification");
      if(classifications.length > 0)
	{	  
	  MetaData.Classification[] cl = new MetaData.Classification[classifications.length];
	  for(int i = 0; i < classifications.length; i++)
	    {
	      MetaData.TaxonPath[] paths = null;
	      
	      XmlElement[] taxonpaths = classifications[i].getSubElements("taxonpath"); 
	      if(taxonpaths.length > 0)
		{
		  paths = new MetaData.TaxonPath[taxonpaths.length];
		  for(int j = 0; j < taxonpaths.length; j++)
		    {
		      MetaData.Taxon[] taxons = null;

		      XmlElement[] taxonEls = taxonpaths[j].getSubElements("taxon");
		      if(taxonEls.length > 0)
			{
			  taxons = new MetaData.Taxon[taxonEls.length];
			  for(int k = 0; k < taxonEls.length; k++)
			    {
			      taxons[k] = new MetaData.Taxon(maybeGetString(taxonEls[k], "id"),
							     maybeGetLangStringType(taxonEls[k], "entry"));
			    }
			}
		      paths[j] = new MetaData.TaxonPath(maybeGetString(taxonpaths[j], "source"),
							taxons);
		      
		    }
		}

	      cl[i] = new MetaData.Classification(maybeGetLangString(classifications[i], "purpose"),
						  paths,
						  maybeGetLangStringType(classifications[i], "description"),
						  maybeGetLangStringTypes(classifications[i], "keywords"));
	    }
	  metaData.set_classification(cl);
	}
    }

  /** Builds a MetaData tree from the given MetaData.
   *
   *  @param MetaData the MetaData to get all meta-data from.
   *  @return a MetaData XmlElement.
   *  @exception XmlElementException if something went wrong.
   */
  public static XmlElement buildXmlTree(MetaData metaData)
    throws XmlElementException
    {
      XmlElement record = new XmlElement("record");

      
      
      XmlElement metametadata   = new XmlElement("metametadata");

      MetaData.CatalogEntry[] ce = metaData.get_metametadata_catalogentry();
      if(ce != null)
	{
	  for(int i = 0; i < ce.length; i++)
	    {
	      XmlElement catalogentry = new XmlElement("catalogentry");
	      maybeAddString(catalogentry, "catalogue", ce[i].catalogue);
	      maybeAddLangStringType(catalogentry, "entry", ce[i].entry);
	      metametadata.addSubElement(catalogentry);
	    }
	}

      MetaData.Contribute[] co = metaData.get_metametadata_contribute();
      if(co != null)
	{
	  for(int i = 0; i < co.length; i++)
	    {
	      XmlElement contribute = new XmlElement("contribute");
	      maybeAddLangString(contribute, "role", co[i].role);
	      maybeAddEntities(contribute, co[i].entity);
	      maybeAddDateType(contribute, "date", co[i].date);
	      metametadata.addSubElement(contribute);
	    }
	}

      maybeAddStrings(metametadata, "metadatascheme", metaData.get_metametadata_metadatascheme());
      maybeAddString(metametadata, "language", metaData.get_metametadata_language());

      maybeAddSubElement(record, metametadata);




      

      XmlElement general        = new XmlElement("general"); 

      maybeAddLangStringType(general, "title", metaData.get_general_title());

      ce = metaData.get_general_catalogentry();
      if(ce != null)
	{
	  for(int i = 0; i < ce.length; i++)
	    {
	      XmlElement catalogentry = new XmlElement("catalogentry");
	      maybeAddString(catalogentry, "catalogue", ce[i].catalogue);
	      maybeAddLangStringType(catalogentry, "entry", ce[i].entry);
	      general.addSubElement(catalogentry);
	    }
	}
		  
      maybeAddStrings(general, "language", metaData.get_general_language());
      maybeAddLangStringTypes(general, "description",
			      metaData.get_general_description());
      maybeAddLangStringTypes(general, "keywords",
			      metaData.get_general_keywords());
      maybeAddLangStringTypes(general, "coverage",
			      metaData.get_general_coverage());
      maybeAddLangStringType(general, "structure",
			     metaData.get_general_structure());
      maybeAddInt(general, "aggregationlevel",
		  metaData.get_general_aggregationlevel());

      maybeAddSubElement(record, general);


      

      XmlElement lifecycle      = new XmlElement("lifecycle");
      
      maybeAddLangString(lifecycle, "version",
			 metaData.get_lifecycle_version());
      maybeAddLangStringType(lifecycle, "status",
			     metaData.get_lifecycle_status());

      co = metaData.get_lifecycle_contribute();
      if(co != null)
	{
	  for(int i = 0; i < co.length; i++)
	    {
	      XmlElement contribute = new XmlElement("contribute");
	      maybeAddLangString(contribute, "role", co[i].role);
	      maybeAddEntities(contribute, co[i].entity);
	      maybeAddDateType(contribute, "date", co[i].date);
	      lifecycle.addSubElement(contribute);
	    }
	}
      
      maybeAddSubElement(record, lifecycle);


      



      
      XmlElement technical      = new XmlElement("technical");

      maybeAddLangStringType(technical, "format", metaData.get_technical_format());
      maybeAddInt(technical, "size", metaData.get_technical_size());

      maybeAddLocations(technical, metaData.get_technical_location());

      MetaData.Requirements[] re = metaData.get_technical_requirements();
      if(re != null)
	{
	  for(int i = 0; i < re.length; i++)
	    {
	      XmlElement requirements = new XmlElement("requirements");
	      maybeAddLangString(requirements, "type", re[i].type);
	      maybeAddLangString(requirements, "name", re[i].name);
	      maybeAddString(requirements, "minimumversion", re[i].minimumversion);
	      maybeAddString(requirements, "maximumversion", re[i].maximumversion);
	      technical.addSubElement(requirements);
	    }
	}
      maybeAddLangString(technical, "installationremarks",
			 metaData.get_technical_installationremarks());
      maybeAddLangString(technical, "otherplatformrequirements",
			 metaData.get_technical_otherplatformrequirements());
      maybeAddDateType(technical, "duration", metaData.get_technical_duration());

      maybeAddSubElement(record, technical);


      
      XmlElement educational    = new XmlElement("educational");

      maybeAddLangStringType(educational, "interactivitytype",
			     metaData.get_educational_interactivitytype());
      maybeAddLangStrings(educational, "learningresourcetype",
			  metaData.get_educational_learningresourcetype());
      maybeAddInt(educational, "interactivitylevel",
		  metaData.get_educational_interactivitylevel());
      maybeAddInt(educational, "semanticdensity",
		  metaData.get_educational_semanticdensity());
      maybeAddLangStringTypes(educational, "intendedenduserrole",
			      metaData.get_educational_intendedenduserrole());
      maybeAddLangStrings(educational, "learningcontext",
			  metaData.get_educational_learningcontext());
      maybeAddLangStrings(educational, "typicalagerange",
			  metaData.get_educational_typicalagerange());
      maybeAddInt(educational, "difficulty",
		  metaData.get_educational_difficulty());
      maybeAddDateType(educational, "typicallearningtime",
		       metaData.get_educational_typicallearningtime());
      maybeAddLangStringType(educational, "description",
			     metaData.get_educational_description());
      maybeAddStrings(educational, "language",
		      metaData.get_educational_language());
       
      maybeAddSubElement(record, educational);


      XmlElement rights         = new XmlElement("rights");
      
      MetaData.Rights ri = metaData.get_rights();
      if(ri != null)
	{
	  maybeAddLangStringType(rights, "cost", ri.cost);
	  maybeAddLangStringType(rights, "copyrightandotherrestrictions", ri.copyrightandotherrestrictions);
	  maybeAddLangStringType(rights, "description", ri.description);
	  record.addSubElement(rights);
	}
      
      

      
      
      MetaData.Relation[] rel = metaData.get_relation();
      if(rel != null)
	{
	  for(int i = 0; i < rel.length; i++)
	    {
	      XmlElement relation = new XmlElement("relation");
	      maybeAddLangString(relation, "kind", rel[i].kind);
	      XmlElement resource = new XmlElement("resource");
	      maybeAddLangStringType(resource, "description", rel[i].resource_description);
	      XmlElement extension = new XmlElement("extension");
	      maybeAddString(extension, "location", rel[i].resource_location);
	      maybeAddSubElement(resource, extension);
	      maybeAddSubElement(relation, resource);
	      record.addSubElement(relation);
	    }
	}

      

      MetaData.Annotation[] an = metaData.get_annotation();
      if(an != null)
	{
	  for(int i = 0; i < an.length; i++)
	    {
	      XmlElement annotation = new XmlElement("annotation");
	      maybeAddEntity(annotation, an[i].person);
	      maybeAddDateType(annotation, "date", an[i].date);
	      maybeAddLangStringType(annotation, "description",
				     an[i].description);
	      record.addSubElement(annotation);
	    }
	}

      

      MetaData.Classification[] cl = metaData.get_classification();
      if(cl != null)
	{
	  for(int i = 0; i < cl.length; i++)
	    {
	      XmlElement classification = new XmlElement("classification");
	      maybeAddLangString(classification, "purpose", cl[i].purpose);
	      
	      MetaData.TaxonPath[] tp = cl[i].taxonpath;
	      if(tp != null)
		{
		  for(int j = 0; j < tp.length; j++)
		    {
		      XmlElement taxonpath = new XmlElement("taxonpath");
		      maybeAddString(taxonpath, "source", tp[j].source);
		      MetaData.Taxon[] tn = tp[j].taxon;
		      if(tn != null)
			{
			  for(int k = 0; k < tn.length; k++)
			    {
			      XmlElement taxon = new XmlElement("taxon");
			      maybeAddString(taxon, "id", tn[k].id);
			      maybeAddLangStringType(taxon, "entry", tn[k].entry);
			      taxonpath.addSubElement(taxon);
			    }
			}
		      classification.addSubElement(taxonpath);
		    }
		}
	      maybeAddLangStringType(classification, "description", cl[i].description);
	      maybeAddLangStringTypes(classification, "keywords",
				      cl[i].keywords);
	      record.addSubElement(classification);
	    }
	}
      
      return record;
    }




  
  static String maybeGetString(XmlElement root, String elname) throws ComponentException
    {
      XmlElement el = maybeGetSubElement(root, elname);
      if(el != null)
	return el.getCDATA();

      return null;
    }

  static String[] maybeGetStrings(XmlElement root, String elname) throws ComponentException
    {
      XmlElement[] els = root.getSubElements(elname);
      String[] res = null;
      
      if(els.length > 0)
        {
	  res = new String[els.length];
	  for(int i = 0; i < els.length; i++)
	    res[i] = els[i].getCDATA();
	}

      return res;
    }

  static MetaData.Location[] maybeGetLocations(XmlElement root) throws ComponentException
    {
      XmlElement[] els = root.getSubElements("location");
      MetaData.Location[] locs = null;
      
      if(els.length > 0)
        {
	  locs = new MetaData.Location[els.length];
	  for(int i = 0; i < els.length; i++)
	    locs[i] = new MetaData.Location(loadAttribute(els[i], "type", "URI"),
					    els[i].getCDATA());
	}

      return locs;
    }

  static MetaData.LangString elToLangString(XmlElement el) throws ComponentException
    {
      if(el != null)
	return new MetaData.LangString(el.getAttribute("lang"), el.getCDATA());
      return null;
    }
  
  
  static MetaData.LangString maybeGetLangString(XmlElement root, String elname) throws ComponentException
    {
      XmlElement el = maybeGetSubElement(root, elname);
      if(el != null)
	  return elToLangString(maybeGetSubElement(el, "langstring"));
      
      return null;
    }
  
      
  
  static MetaData.LangString[] maybeGetLangStrings(XmlElement root, String elname) throws ComponentException
    {
      XmlElement[] els = root.getSubElements(elname);

      MetaData.LangString[] res = null;
      
      if(els.length > 0)
        {
	  res = new MetaData.LangString[els.length];
	  for(int i = 0; i < els.length; i++)
	    res[i] = elToLangString(maybeGetSubElement(els[i], "langstring"));
	}

      return res;
    }

  static MetaData.LangString[] elToLangStrings(XmlElement root) throws ComponentException
    {
      MetaData.LangString[] strings = null;
      
      XmlElement[] lstrels = root.getSubElements("langstring");
      if(lstrels.length > 0)
	{
	  strings = new MetaData.LangString[lstrels.length];
	  
	  for(int i = 0; i < lstrels.length; i++)
	    strings[i] = elToLangString(lstrels[i]);
	}
      return strings;
    }
  
  
  static MetaData.LangStringType maybeGetLangStringType(XmlElement root, String elname) throws ComponentException
    {
      XmlElement el = maybeGetSubElement(root, elname);

      if(el != null)
	{
	  MetaData.LangString[] strings = elToLangStrings(el);
	  if(strings != null)
	    return new MetaData.LangStringType(strings);
	}
      return null;
    }

  static MetaData.LangStringType[] maybeGetLangStringTypes(XmlElement root, String elname) throws ComponentException
    {
      MetaData.LangStringType[] langStringTypes = null;

      XmlElement[] els = root.getSubElements(elname);
      
      if(els.length > 0)
	{
	  langStringTypes = new MetaData.LangStringType[els.length];

	  for(int i = 0; i < els.length; i++)
	    langStringTypes[i] = new MetaData.LangStringType(elToLangStrings(els[i]));
	}
      return langStringTypes;
    }

  static MetaData.DateType maybeGetDateType(XmlElement root, String elname) throws ComponentException
    {
      XmlElement el = maybeGetSubElement(root, elname);
      
      if(el != null)
	{
	  return new MetaData.DateType(maybeGetString(el, "datetime"),
				       maybeGetLangStringType(el, "description"));
	}
      return null;
    }

  static int maybeGetInt(XmlElement root, String elname)  throws ComponentException
    {
      XmlElement el = maybeGetSubElement(root, elname);
      
      if(el != null)
	{
	  int value;

	  String intStr = el.getCDATA();

	  value = Integer.parseInt(intStr, 10);
      
	  if(value < 0)
	    throw new ComponentException(elname + "=\"" + intStr +
					 "\" is not a valid non-negative integer!");
	  return value;
	}
      return -1;
    }

  static String elToEntity(XmlElement el)  throws ComponentException
    {
      XmlElement vcard = maybeGetSubElement(el, "vcard");

      if(vcard != null)
	return vcard.getCDATA();

      return null;
    }
  
  
  static String maybeGetEntity(XmlElement root)  throws ComponentException
    {
      XmlElement el = maybeGetSubElement(root, "centity");
      
      if(el != null)
	return elToEntity(el);
      return null;
    }

  static String[] maybeGetEntities(XmlElement root)  throws ComponentException
    {
      XmlElement[] els = root.getSubElements("centity");

      String[] res = null;
      
      if(els.length > 0)
	{
	  res = new String[els.length];
	  for(int i = 0; i < els.length; i++)
	    res[i] = elToEntity(els[i]);
	}

      return res;
    }
  
  static XmlElement stringToEl(String string, String elname) throws XmlElementException
    {
      XmlElement str =  new XmlElement(elname);
      str.setCDATA(string);
      return str;
    }

  static void maybeAddString(XmlElement root, String elname, String value)  throws XmlElementException
    {
      if(value != null)
	root.addSubElement(stringToEl(value, elname));
    }

  static void maybeAddStrings(XmlElement root, String elname, String[] values)  throws XmlElementException
    {
      if(values != null)
	for(int i = 0; i < values.length; i++)
	  root.addSubElement(stringToEl(values[i], elname));
    }

  static void maybeAddLocations(XmlElement root, MetaData.Location[] values)  throws XmlElementException
    {
      if(values != null)
	for(int i = 0; i < values.length; i++)
	  {
	    XmlElement str =  new XmlElement("location");
	    str.setCDATA(values[i].string);
	    str.setAttribute("type", values[i].type);
	    root.addSubElement(str);
	  }
    }

  static XmlElement langStringToEl(MetaData.LangString langstring) throws XmlElementException
    {
      XmlElement lstr =  new XmlElement("langstring");
      lstr.setCDATA(langstring.string);
      lstr.setAttribute("lang", langstring.language);
      return lstr;
    }
      
  static void maybeAddLangString(XmlElement root, String elname, MetaData.LangString value)  throws XmlElementException
    {
      if(value != null)
	{
	  XmlElement newEl = new XmlElement(elname);
	  newEl.addSubElement(langStringToEl(value));
	  root.addSubElement(newEl);
	}
    }
  
  static void maybeAddLangStrings(XmlElement root, String elname, MetaData.LangString[] values) throws XmlElementException
    {
      if(values != null)
	for(int i = 0; i < values.length; i++)
	  {
	    XmlElement newEl = new XmlElement(elname);
	    newEl.addSubElement(langStringToEl(values[i]));
	    root.addSubElement(newEl);
	  }
    }
  
  static void maybeAddLangStringType(XmlElement root, String elname, MetaData.LangStringType value) throws XmlElementException
    {
      if(value != null && value.langstring != null)
	{
	  XmlElement newEl = new XmlElement(elname);
	  for(int i = 0; i < value.langstring.length; i++)
	    newEl.addSubElement(langStringToEl(value.langstring[i]));
	  root.addSubElement(newEl);
	}
    }

  static void maybeAddLangStringTypes(XmlElement root, String elname, MetaData.LangStringType[] values)  throws XmlElementException
    {
      if(values != null)
	for(int i = 0; i < values.length; i++)
	  maybeAddLangStringType(root, elname, values[i]);
    }

   static void maybeAddInt(XmlElement root, String elname, int value) throws XmlElementException
    {
      if(value >= 0)
	maybeAddString(root, elname, Integer.toString(value));
    }

  static void maybeAddEntity(XmlElement root, String entity) throws XmlElementException
    {
      if(entity != null)
	{
	  XmlElement newEl = new XmlElement("centity");
	  maybeAddString(newEl, "vcard", entity);
	  root.addSubElement(newEl);
	}
    }

  static void maybeAddEntities(XmlElement root, String[] entity)  throws XmlElementException
    {
      if(entity != null)
	for(int i = 0; i < entity.length; i++)
	  maybeAddEntity(root, entity[i]);
    }

    static void maybeAddDateType(XmlElement root, String elname, MetaData.DateType datetype) throws XmlElementException
    {
      if(datetype != null)
	{
	  XmlElement newEl = new XmlElement(elname);
	  maybeAddString(newEl, "datetime", datetype.datetime);
	  maybeAddLangStringType(newEl, "description", datetype.description);
	  root.addSubElement(newEl);
	}
    }

  
  static void maybeAddSubElement(XmlElement root, XmlElement subEl) throws XmlElementException
    {
      if(subEl != null && subEl.getSubElementNumber() > 0)
	root.addSubElement(subEl);
    }

  
  
}
