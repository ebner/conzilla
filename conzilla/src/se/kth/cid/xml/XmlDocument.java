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


package se.kth.cid.xml;

import java.util.*;

 

/** This class represents an XML document. 
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlDocument
{
  /** The names of the processing instructions.
   */
  Vector piNames;

  /** Cache of the above.
   */
  String[] piNamesArray;

  /** The data of the PIs.
   *  Maps piName --> piData.
   */
  Hashtable piDatas;
  
  /** The public ID of the DTD.
   */
  String publicDTDId;

  /** The system ID of the DTD.
   */
  String systemDTDId;

  /** The root of the element hierarchy.
   */
  XmlElement root;

  /** Constructs an XmlDocument.
   */
  public XmlDocument()
    {
      piDatas = new Hashtable();
      piNames = new Vector();

      root = new XmlElement("doc");
    }


  /** Sets the document root.
   *
   *  @param root the document root.
   */
  public void setRoot(XmlElement root)
    {
      this.root = root;
    }

  /** Gets the document root.
   *
   *  @return the document root.
   */
  public XmlElement getRoot()
    {
      return root;
    }
  
  /** Sets the public DTD ID.
   *
   *  @param npublicId the new public ID.
   *  @exception XmlDocumentException if npublicId is non-null but the
   *                                  system DTD ID is null.
   */
  public void setPublicDTDId(String npublicId)
    throws XmlDocumentException
    {
      if(npublicId != null && systemDTDId == null)
	throw new XmlDocumentException("Illegal to use PUBLIC "
				       + " without SYSTEM!");
      
      publicDTDId = npublicId;
    }

  /** Returns the public DTD ID.
   *
   *  @return the public DTD ID.
   */
  public String getPublicDTDId()
    {
      return publicDTDId;
    }
  
  /** Sets the system DTD ID.
   *
   *  @param nsystemId the new system ID.
   *  @exception XmlDocumentException if nsystemId is null but the
   *                                  public DTD ID is non-null.
   */
  public void setSystemDTDId(String nsystemId)
    throws XmlDocumentException
    {      
      if(nsystemId == null && publicDTDId != null)
	throw new XmlDocumentException("Illegal to use PUBLIC"
				       + " without SYSTEM!");
      systemDTDId = nsystemId;
    }

  
  /** Returns the system DTD ID.
   *
   *  @return the system DTD ID.
   */
  public String getSystemDTDId()
    {
      return systemDTDId;
    }


  /** Sets a processing instruction in the XML document.
   *
   *  If piData is null, the processing instruction is removed.
   *  @param piName the name of the processing instruction.
   *  @param pi the data of the processing instruction.
   *  @exception XmlDocumentException if the PI was invalid.
   */
  public void setProcessingInstruction(String piName, String piData)
    throws XmlDocumentException
    {
      if(piName.equalsIgnoreCase("xml"))
	throw new XmlDocumentException("Illegal to use \"" + piName
				       + "\" as processing instruction!");

      if(piData != null && piData.indexOf("?>") != -1)
	throw new XmlDocumentException("Illegal to use \"" + piData
				       + "\" inside processing instruction!");

      if(piDatas.get(piName) != null)
	{
	  if(piData != null)
	    piDatas.put(piName, piData);
	  else
	    {
	      piDatas.remove(piName);
	      piNames.removeElement(piName);
	      piNamesArray = null;
	    }
	}
      else
	{
	  piDatas.put(piName, piData);
	  piNames.addElement(piName);
	  piNamesArray = null;
	}
    }

  public String[] getProcessingInstructions()
    {
      if(piNamesArray == null)
	{
	  piNamesArray = new String[piNames.size()];
	  piNames.copyInto(piNamesArray);
	}
      return piNamesArray;
    }
  
  public String getProcessingInstruction(String piName)
    {
      return (String) piDatas.get(piName);
    }
  

}
