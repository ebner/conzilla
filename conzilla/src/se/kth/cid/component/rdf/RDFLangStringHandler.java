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
import org.w3c.rdf.vocabulary.rdf_syntax_19990222.RDF;
import se.kth.cid.vocabulary.IMSv1p2.Root;
import java.util.*;


/** LOM-LangString extraction utilities (from a RDF-Model).
 *
 *  <P>A LangString contains an oroginal string in a specified language
 *  and a set of translations.</p>
 *<P>
 * This class allows you to <ol>
 * <li> Extract a single single string in one language with the function @link #extractLangStringTranslation </li>
 * <li> Extract a LangString with the function @link #extractPossibleLangString or @link #extractKnownLangString</li>
 * <li> Extract a set of LangStrings with the function @link #extractLangStrings </li>
 *</ol></p>
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class RDFLangStringHandler
{
  /** Construction of an RDFLangStringHandler is not allowed.
   */
  private RDFLangStringHandler()
    {}
  
    /** */
  public static MetaData.LangStringType[] extractLangStrings(Model langstrings, Model model) throws ModelException
    {
	Vector langtypes = new Vector();
	MetaData.LangStringType langtype;
	if (!langstrings.isEmpty())
	    {
		Enumeration en=langstrings.elements();
		while (en.hasMoreElements())
		    {
			langtype = extractPossibleLangString(((Statement) en.nextElement()).object(), model);
			if (langtype != null)
			    langtypes.addElement(langtype);
		    }	
	    }
	
	if (langtypes.isEmpty())
	    return null;

	MetaData.LangStringType [] vlangtypes = new MetaData.LangStringType[langtypes.size()];
	langtypes.toArray(vlangtypes);
	return vlangtypes;
    }


    /** Extracts a LangString if there is any.
     *  The alternatives are: <ol>
     *  <li> It's a LangString resource, extract via @link #extractKnownLangString.</li>
     *  <li> It's another resource, a value is excavated via the @link #dumbDownTranslation algorithm.</li>
     *  <li> It's a literal, A LangString is constructed with only one translation with 
     *     language set to null.</li></ol>
     * 
     *  @see #dumbDownTranslation
     *  @see #extractKnownLangString
     */
    public static MetaData.LangStringType extractPossibleLangString(RDFNode node, Model model) throws ModelException
    {
	if (node == null)
	    return null;
	if (node instanceof Resource)
	    {
		//System.out.println("everything about the node: "+node+"\n"+
		//model.find((Resource) node, rdf_type, null).elements().nextElement());
		if (model.contains(model.getNodeFactory().createStatement((Resource) node, RDF.type, Root.LangString)))
		    return extractKnownLangString((Resource) node, model);
		else
		    {
			MetaData.LangString lang = excavateLangStringTranslation(node, model, 0);
			if (lang!=null)
			    {
				MetaData.LangString [] langs = new MetaData.LangString[1];
				langs[0] = lang;
				return new MetaData.LangStringType(langs);
			    }
			else
			    return null;
		    }
	    }
	MetaData.LangString [] langs= new MetaData.LangString[1];
	langs[0] = new MetaData.LangString(null, node.getLabel());
	return new MetaData.LangStringType(langs);
    }

    /** Extracs original string with possibly one or several translations.
     *
     *  A LangString consists of an array of translations where each translation
     *  is a string and a language.<br \>
     *  The original String appears first in the array of translations.
     *
     *  @see #extractLangStringTranslation
     *  @return MetaData.LangStringType i.e just an class wrapped around an array of 
     *  translations what LOM defines as a langstring or null if no string where found.
     */
    public static MetaData.LangStringType extractKnownLangString(Resource langstring, Model model) throws ModelException
	{
	    RDFNode value      = RDFUtil.getObject(model, langstring, RDF.value);
	    Model translations = model.find(langstring, Root.translation, null);

	    Vector langs = new Vector();
	    MetaData.LangString lang;

	    if (value!=null)
		{
		    lang=excavateLangStringTranslation(value,model, 8);
		    if (lang!=null)
			langs.addElement(lang);
		}
	    if (!translations.isEmpty())
		{
		    Enumeration en=translations.elements();
		    while (en.hasMoreElements())
			{
			    lang = excavateLangStringTranslation(((Statement) en.nextElement()).object(),model, 8);
			    if (lang!=null)
				langs.addElement(lang);
			}
		}
	    if (!langs.isEmpty())
		{
		    MetaData.LangString [] vlangs= new MetaData.LangString[langs.size()];
		    langs.toArray(vlangs);
		    return new MetaData.LangStringType(vlangs);
		}
	    else
		return null;
	}


    public static MetaData.LangString extractLangStringTranslation(Model translationModel , Model model) throws ModelException
    {
	if (translationModel.isEmpty())
	    return null;
	return excavateLangStringTranslation(RDFUtil.get1(translationModel).object(), model,8);
    }

    static final Resource dcv1p1_language = new ResourceImpl("http://purl.org/dc/elements/1.1/language"); 

    /** Extract a string with belonging language, 
     */
   private static MetaData.LangString excavateLangStringTranslation(RDFNode node, Model model, int counter) throws ModelException
    {
	if (node instanceof Literal)
		return new MetaData.LangString(null, node.getLabel());
	if (counter<1)
	    return null;
	

	//	Model language = model.find((Resource) node, DC.Language, null);
	Model language = model.find((Resource) node, dcv1p1_language, null);
	if (!language.isEmpty())
	    {
		Model rdfvalues = model.find((Resource) node, RDF.value, null);
		if (!rdfvalues.isEmpty())
		    {
			String value=excavateValue(RDFUtil.get1(rdfvalues).object(), model, --counter);
			if (value!=null)
			    return new MetaData.LangString(getLanguage(RDFUtil.get1(language).object()), value);
		    }
		return null;
	    }

	Model rdfvalues = model.find((Resource) node, RDF.value, null);
	if (!rdfvalues.isEmpty())
	    return excavateLangStringTranslation(RDFUtil.get1(rdfvalues).object(), model, --counter);
	return null;
    }

    /** This function excavates the value of a resource via a traversion of possible several resources
     *  linked together via the rdf:value property until a literal is found.<br \>
     *  It can be thought of as a part of the dumbdown algorithm as defined by Dublin Core.
     *
     *  
     *  @param node the starting point, can be a literal (done) or a resource search value for.
     *  @param model the model to search statements in.
     *  @param count the maximum number of traversions to be done before the algorithm aborts (avoids loops)
     *  @return String null if no value was found.
     */
    public static String excavateValue(RDFNode node, Model model, int counter) throws ModelException
    {
	if (node instanceof Literal)
		return node.getLabel();
	if (counter<1)
	    return null;
	
	Model rdfvalues = model.find((Resource) node, RDF.value, null);
	if (!rdfvalues.isEmpty())
	    return excavateValue(RDFUtil.get1(rdfvalues).object(), model, --counter);
	return null;
    }

    private static String getLanguage(RDFNode node) throws ModelException
    {
	return node.getLabel().substring(node.getLabel().lastIndexOf("#")+1);
    }    
}

