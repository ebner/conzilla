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


package se.kth.cid.rdf.metadata;
import se.kth.cid.component.MetaData;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.util.*;
import org.w3c.rdf.util.*;
import org.w3c.rdf.model.*;
import org.w3c.rdf.implementation.model.*;
import se.kth.cid.vocabulary.IMSv1p2.Lifecycle;
import org.w3c.rdf.vocabulary.dublin_core_19990702.DC;
import java.util.*;


/** Loads metadata from an RDF/XML-doc.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class RDFMetaDataHandler
{
    //title with upper or lower initial??
  static final Resource dcv1p1_title = new ResourceImpl("http://purl.org/dc/elements/1.1/title"); 

  /** Constructing an RDFMetaDataHandler is not allowed.
   */
  private RDFMetaDataHandler()
    {}
  
  /** Loads metadata from an RDF model.
   *  @param metaData the MetaData to load all meta-data into.
   *  @param root the MetaData element.
   *
   *  @exception ReadOnlyException if the MetaData was read-only.
   */
  public static void load(RDFMetaData metaData, Model model, String uri)
      throws ReadOnlyException, ModelException
    {
	Resource resource = new ResourceImpl(uri);

	Model result = model.find(resource, Lifecycle.version, null);
	MetaData.LangString ls = RDFLangStringHandler.extractLangStringTranslation(result, model);
	if (ls != null)
	    metaData.set_lifecycle_version(ls);

	/*	Enumeration triples=result.elements();
	if (!result.isEmpty())
	{
	    org.w3c.rdf.model.Statement statement = (org.w3c.rdf.model.Statement) triples.nextElement();
	    metaData.set_lifecycle_version(new MetaData.LangString(null,statement.object().getLabel()));
	    }*/
	

	//	result = model.find(resource, DC.Title, null);
	//model.find(resource, dcv1p1_title, null);


	RDFNode node = RDFUtil.getObject(model, resource, dcv1p1_title);
	MetaData.LangStringType lst = RDFLangStringHandler.extractPossibleLangString(node, model);
	if (lst != null)
	    {
		Tracer.debug("Title for " +uri+ " found. In model"+ model.getURI());
		metaData.set_general_title(lst);
	    }
	else
	    Tracer.debug("No title for "+uri+"found. In model"+ model.getURI());

	//FIXME: Severe hack!!!
	RDFNode node2 = RDFUtil.getObject(model, resource,org.w3c.rdf.vocabulary.rdf_schema_200001.RDFS.comment) ;
	
	if (node2==null)
	    return;
	ls = new MetaData.LangString(null, node2.getLabel());
	MetaData.LangString [] lss = new MetaData.LangString[1];
	lss[0]=ls;
	MetaData.LangStringType[] str = new MetaData.LangStringType[1];
	str[0] = new MetaData.LangStringType(lss);
	metaData.set_general_description(str);
    }
}

