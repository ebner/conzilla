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


package se.kth.cid.conceptmap;
import se.kth.cid.neuron.*;
import se.kth.cid.component.cache.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.component.*;

public interface NeuronStyle
{
  int FIRST_NEURONSTYLE_EDIT_CONSTANT  = ConceptMap.LAST_CONCEPTMAP_ONLY_EDIT_CONSTANT + 1;
  int DETAILEDMAP_EDITED               = FIRST_NEURONSTYLE_EDIT_CONSTANT;
  int BOUNDINGBOX_EDITED      	       = FIRST_NEURONSTYLE_EDIT_CONSTANT + 1;
  int TITLE_EDITED            	       = FIRST_NEURONSTYLE_EDIT_CONSTANT + 2;
  int DATATAG_ADDED            	       = FIRST_NEURONSTYLE_EDIT_CONSTANT + 3;
  int DATATAG_REMOVED    	       = FIRST_NEURONSTYLE_EDIT_CONSTANT + 4;
  int AXONSTYLE_REMOVED                = FIRST_NEURONSTYLE_EDIT_CONSTANT + 5;
  int AXONSTYLE_ADDED                  = FIRST_NEURONSTYLE_EDIT_CONSTANT + 6;
  int LINE_EDITED             	       = FIRST_NEURONSTYLE_EDIT_CONSTANT + 7;
  int LAST_NEURONSTYLE_EDIT_CONSTANT   = LINE_EDITED;
  

  public class AxonStyleId
  {
    public NeuronStyle end;
    public String      axonID;

    public AxonStyleId(NeuronStyle end, String axonID)
      {
	this.end = end;
	this.axonID = axonID;
      }
  }
  
  
  ///////Non-visual stuff///////
  ConceptMap getConceptMap();

  String getID();
  
  ////////////NeuronStyle////////////////////

  void remove();
  
  String getNeuronURI();

  String getDetailedMap();
  void   setDetailedMap(String uri)
    throws ReadOnlyException, InvalidURIException;
  
  
  ConceptMap.BoundingBox getBoundingBox();
  void setBoundingBox(ConceptMap.BoundingBox rect) throws ReadOnlyException;
  
  String getTitle();  
  void setTitle(String title) throws ReadOnlyException;
  
  String[] getDataTags();

  void addDataTag(String tag) throws ReadOnlyException;
  
  void removeDataTag(String tag) throws ReadOnlyException;

  ConceptMap.Position[] getLine();

  void setLine(ConceptMap.Position[] line) throws ReadOnlyException;
  
  ////////////AxonStyle-varibles/////////////
  AxonStyle[] getEndOfAxons();
  
  AxonStyle[] getAxons();

  AxonStyle addAxonStyle(AxonStyleId id)
    throws ReadOnlyException, ConceptMapException;
  
}



