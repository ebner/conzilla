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

/****************************************************************************
 ************** This file is automagically generated! ***********************
 **************             Do not edit!              ***********************
 ****************************************************************************/

package se.kth.cid.component.xml.dtd;

import java.io.*;
import se.kth.cid.xml.*;

public class ConceptMapDTD implements ExternalEntity
{
  public static final String DTD =
"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n"+
"<!-- $ Id: ConceptMap.dtd,v 1.6 1999/07/20 00:51:13 m94mni Exp $ -->\n"+
"\n"+
"<!--\n"+
"  DOCTYPE \"ConceptMap\"\n"+
"  PUBLIC  \"-//CID//DTD ConceptMap 1.0//EN\"\n"+
"  SYSTEM  \"ConceptMap.dtd\"\n"+
"-->\n"+
"\n"+
"<!--\n"+
"  This file is part of the Conzilla browser, designed for\n"+
"  the Garden of Knowledge project.\n"+
"  Copyright (C) 1999  CID (http://www.nada.kth.se/cid)\n"+
"  \n"+
"  This program is free software; you can redistribute it and/or modify\n"+
"  it under the terms of the GNU General Public License as published by\n"+
"  the Free Software Foundation; either version 2 of the License, or\n"+
"  (at your option) any later version.\n"+
"  \n"+
"  This program is distributed in the hope that it will be useful,\n"+
"  but WITHOUT ANY WARRANTY; without even the implied warranty of\n"+
"  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"+
"  GNU General Public License for more details.\n"+
"  \n"+
"  You should have received a copy of the GNU General Public License\n"+
"  along with this program; if not, write to the Free Software\n"+
"  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA\n"+
"-->\n"+
"\n"+
"\n"+
"<!-- ConceptMap -->\n"+
"\n"+
"<!ELEMENT ConceptMap		(MetaData,\n"+
"                                 Background,\n"+
"           			 BoundingBox,\n"+
"				 MapSet?,\n"+
"				 NeuronStyle*)>\n"+
"\n"+
"<!-- END ConceptMap -->\n"+
"\n"+
"\n"+
"\n"+
"<!ENTITY % MetaData PUBLIC \"-//CID//DTD MetaData 1.0//EN\" \"MetaData.dtd\">\n"+
"%MetaData;\n"+
"\n"+
"\n"+
"\n"+
"<!-- Background -->\n"+
"\n"+
"<!ELEMENT Background		 EMPTY>\n"+
"<!ATTLIST Background\n"+
"	COLOR			 CDATA			\"0\">\n"+
"\n"+
"<!-- END Background -->\n"+
"\n"+
"\n"+
"\n"+
"<!-- BoundingBox -->\n"+
"\n"+
"<!ELEMENT BoundingBox		 EMPTY>\n"+
"<!ATTLIST BoundingBox\n"+
"	WIDTH			 CDATA			#REQUIRED\n"+
"	HEIGHT			 CDATA			#REQUIRED>\n"+
"\n"+
"<!-- END BoundingBox -->\n"+
"\n"+
"\n"+
"\n"+
"<!-- MapSet -->\n"+
"\n"+
"<!ELEMENT MapSet		 EMPTY>\n"+
"<!ATTLIST MapSet\n"+
"	MAPSETURI		 CDATA			#REQUIRED>\n"+
"\n"+
"<!-- END MapSet -->\n"+
"\n"+
"\n"+
"\n"+
"<!-- NeuronStyle -->\n"+
"\n"+
"<!ELEMENT NeuronStyle		(Visibility,\n"+
"				 DetailedMap?,\n"+
"				 Box,\n"+
"				 RoleStyle*)>\n"+
"<!ATTLIST NeuronStyle\n"+
"	NEURONURI		 CDATA			#REQUIRED>\n"+
"\n"+
"\n"+
"\n"+
"<!ELEMENT Visibility		 EMPTY>\n"+
"<!ATTLIST Visibility\n"+
"	STRENGTH		 CDATA			#REQUIRED>\n"+
"\n"+
"\n"+
"<!ELEMENT DetailedMap		 EMPTY>\n"+
"<!ATTLIST DetailedMap\n"+
"	MAPURI			 CDATA			#REQUIRED>\n"+
"\n"+
"\n"+
"<!ELEMENT Box			(BoundingBox,\n"+
"				 Position,\n"+
"				 Title,\n"+
"				 DataTags?,\n"+
"                                 Line?)>\n"+
"\n"+
"<!ELEMENT Position       	 EMPTY>\n"+
"<!ATTLIST Position\n"+
"	X			 CDATA			#REQUIRED\n"+
"	Y			 CDATA			#REQUIRED>\n"+
"\n"+
"\n"+
"<!ELEMENT Title			(#PCDATA)>\n"+
"\n"+
"\n"+
"<!ELEMENT DataTags		(DataTag*)>\n"+
"\n"+
"<!ELEMENT DataTag		 EMPTY>\n"+
"<!ATTLIST DataTag\n"+
"	NAME			 CDATA			#REQUIRED>\n"+
"\n"+
"\n"+
"<!ELEMENT RoleStyle		(Line)>\n"+
"<!ATTLIST RoleStyle\n"+
"	TYPE			 CDATA			#REQUIRED\n"+
"	NEURONURI		 CDATA			#REQUIRED>\n"+
"\n"+
"	\n"+
"<!ELEMENT Line			(Position*)>\n"+
"\n"+
"\n"+
"<!-- END NeuronStyle -->\n"+
"\n";

  public static final String docType = "ConceptMap";
  public static final String sysID = "ConceptMap.dtd";
  public static final String pubID = "-//CID//DTD ConceptMap 1.0//EN";

  public ConceptMapDTD()
    {

    }

  public Object getEntity()
    {
       return new StringReader(DTD);
    }

  public String getDocType()
    {
       return docType;
    }

  public String getSystemID()
    {
       return sysID;
    }

  public String getPublicID()
    {
       return pubID;
    }
}
