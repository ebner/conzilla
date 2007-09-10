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
 * Used by all XmlComponentHandlers.
 *
 * The metadata must be represented as
 * <pre>
 * &lt;MetaData>
 *   &lt;Tag NAME="name1">VALUE1&lt;/Tag>
 *   &lt;Tag NAME="name2">VALUE2&lt;/Tag>
 *   ...
 * &lt;/MetaData>
 * </pre>
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlMetaDataHandler
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
    throws ReadOnlyException
    {
      XmlElement[] dataEls = root.getSubElements("Tag");

      for(int i = 0; i < dataEls.length; i++)
	{
	  String tag   = dataEls[i].getAttribute("NAME");
	  String value = dataEls[i].getCDATA();
	  metaData.setValue(tag, value);
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
      XmlElement metaDataEl = new XmlElement("MetaData");

      String[] metaDataTags = metaData.getTags();
      for(int i = 0; i < metaDataTags.length; i++)
	{
	  XmlElement metaDataTagEl = new XmlElement("Tag");
	  metaDataTagEl.setAttribute("NAME", metaDataTags[i]);
	  metaDataTagEl.setCDATA(metaData.getValue(metaDataTags[i]));
	  metaDataEl.addSubElement(metaDataTagEl);
	}

      return metaDataEl;
    }
}
