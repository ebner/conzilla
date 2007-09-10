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

public class MetaDataDTD implements ExternalEntity
{
  public static final String DTD =
"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n"+
"<!-- $ Id: MetaData.dtd,v 1.2 1999/07/14 22:51:31 m94mni Exp $ -->\n"+
"\n"+
"<!--\n"+
"  DOCTYPE \"MetaData\"\n"+
"  PUBLIC  \"-//CID//DTD MetaData 1.0//EN\"\n"+
"  SYSTEM  \"MetaData.dtd\"\n"+
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
"<!-- MetaData -->\n"+
"\n"+
"<!ELEMENT MetaData      	(Tag*)>\n"+
"<!ELEMENT Tag	  		(#PCDATA)>\n"+
"<!ATTLIST Tag\n"+
"	NAME			 CDATA			#REQUIRED>\n"+
"\n"+
"<!-- END MetaData -->\n"+
"\n";

  public static final String docType = "MetaData";
  public static final String sysID = "MetaData.dtd";
  public static final String pubID = "-//CID//DTD MetaData 1.0//EN";

  public MetaDataDTD()
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
