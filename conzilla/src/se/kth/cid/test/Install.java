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



package se.kth.cid.test;

import se.kth.cid.util.Tracer;
import se.kth.cid.conzilla.install.*;
import se.kth.cid.identity.*;

import se.kth.cid.identity.*;

public class Install
{
  public static void main(String[] argv)
    throws Exception
    {
      Installer.installOrExit("Trying out install.");
      Tracer.debug("Continuing...");
      System.exit(1);
    }
}





