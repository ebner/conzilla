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

public class NeuronTypeDTD implements ExternalEntity
{
  public static final String DTD =
"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n"+
"<!-- $ Id: NeuronType.dtd,v 1.8 1999/08/25 17:53:43 m94mni Exp $ -->\n"+
"\n"+
"<!--\n"+
"  DOCTYPE \"NeuronType\"\n"+
"  PUBLIC  \"-//CID//DTD NeuronType 1.0//EN\"\n"+
"  SYSTEM  \"NeuronType.dtd\"\n"+
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
"<!-- NeuronType -->\n"+
"\n"+
"<!ELEMENT NeuronType		(MetaData,\n"+
"                                 DataTags,\n"+
"               			 BoxStyle,\n"+
"           			 RoleType*)>\n"+
"\n"+
"<!-- END NeuronType -->\n"+
"\n"+
"\n"+
"\n"+
"<!ENTITY % MetaData PUBLIC \"-//CID//DTD MetaData 1.0//EN\" \"MetaData.dtd\">\n"+
"%MetaData;\n"+
"\n"+
"\n"+
"\n"+
"<!-- DataTags -->\n"+
"\n"+
"<!ELEMENT DataTags		(DataTag*)>\n"+
"\n"+
"<!ELEMENT DataTag		 EMPTY>\n"+
"<!ATTLIST DataTag\n"+
"	NAME			 CDATA			#REQUIRED>\n"+
"\n"+
"\n"+
"<!-- END DataTags -->\n"+
"\n"+
"\n"+
"\n"+
"<!-- BoxStyle -->\n"+
"\n"+
"<!ELEMENT BoxStyle		(Box,\n"+
"				 Line)>\n"+
"<!ELEMENT Box			 EMPTY>\n"+
"<!ATTLIST Box\n"+
"	TYPE			 CDATA			\"rectangle\"\n"+
"	COLOR			 CDATA			\"0\">\n"+
"\n"+
"<!ELEMENT Line			 EMPTY>\n"+
"<!ATTLIST Line\n"+
"	TYPE			 CDATA			\"continuous\"\n"+
"	THICKNESS		(0|1|2|3|4|5\n"+
"				|6|7|8|9|10)		\"1\"\n"+
"	COLOR			 CDATA			\"0\">\n"+
"\n"+
"\n"+
"<!-- END BoxStyle -->\n"+
"\n"+
"\n"+
"\n"+
"<!-- RoleType -->\n"+
"\n"+
"<!ELEMENT RoleType		(Multiplicity,\n"+
"				 RoleStyle)>\n"+
"<!ATTLIST RoleType\n"+
"	NAME			 CDATA			#REQUIRED>\n"+
"\n"+
"<!ELEMENT Multiplicity		 EMPTY>\n"+
"<!ATTLIST Multiplicity\n"+
"	LOWEST			 CDATA			\"0\"\n"+
"	HIGHEST			 CDATA			\"infinity\">\n"+
"\n"+
"<!ELEMENT RoleStyle		(Head,\n"+
"				 Line)>\n"+
"\n"+
"<!ELEMENT Head			 EMPTY>\n"+
"<!ATTLIST Head\n"+
"	TYPE			 CDATA			\"none\"\n"+
"	FILLED			(true|false)            \"true\"\n"+
"        SIZE                     CDATA                  \"0\">\n"+
"\n"+
"\n"+
"<!-- END RoleType -->\n"+
"\n";

  public static final String docType = "NeuronType";
  public static final String sysID = "NeuronType.dtd";
  public static final String pubID = "-//CID//DTD NeuronType 1.0//EN";

  public NeuronTypeDTD()
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
