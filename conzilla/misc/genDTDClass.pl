#! /opt/GNUlang/bin/perl

$classname = $ARGV[0];

print <<EOF;
/* \$Id\$ */
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

public class $classname implements ExternalEntity
{
  public static final String DTD =
EOF

$first = 1;
$pubID = "";
$sysID = "";
$docType = "";

while(<STDIN>)
  {
    if($docType eq "" && /DOCTYPE *"(.*)"/)
      {
	$docType = $1;
      }
    if($pubID eq "" && /PUBLIC *"(.*)"/)
      {
	$pubID = $1;
      }
    if($sysID eq "" && /SYSTEM *"(.*)"/)
      {
	$sysID = $1;
      }

    s/\"/\\\"/g;
    s/\$Id/\$ Id/g;
    chop;
    if(! $first)
      {
	print "+\n";
     }
    $first = 0;
    print "\"$_\\n\"";
  }

print ";\n";

close INFILE;


($sysID ne "" && $pubID ne "") || die "Did not find PUBLIC and SYSTEM ID!!";


print <<EOF;

  public static final String docType = "$docType";
  public static final String sysID = "$sysID";
  public static final String pubID = "$pubID";

  public $classname()
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
EOF


