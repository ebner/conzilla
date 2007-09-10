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

package se.kth.cid.conzilla.metadata;
import se.kth.cid.component.MetaData;
import se.kth.cid.component.MetaDataUtils;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class RightsPanel extends LabelFields implements MetaDataFieldEditor
{
  LangStringList cost;
  LangStringList copyrightandotherrestrictions;
  LangStringList description;
  
  public RightsPanel(MetaData.Rights rights,
		     boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      if(rights == null)
	rights = new MetaData.Rights(null, null, null);
      

      cost = new LangStringList(rights.cost, false, editable, editListener, metaDataField);
      copyrightandotherrestrictions = new LangStringList(rights.copyrightandotherrestrictions,
							 false, editable, editListener, metaDataField);
      description = new LangStringList(rights.description, true, editable, editListener, metaDataField);

      addLabelField("Cost", cost);
      addLabelField("Copyright/Other Restrictions", copyrightandotherrestrictions);
      addLabelField("Description", description);      
    }

  public MetaData.Rights getRights(boolean resetEdited)
    {
      MetaData.Rights rights
        = new MetaData.Rights(cost.getLangStringType(resetEdited),
			      copyrightandotherrestrictions.getLangStringType(resetEdited),
			      description.getLangStringType(resetEdited));

      if(rights.cost == null
	 && rights.copyrightandotherrestrictions == null
	 && rights.description == null)
	return null;

      return rights;
    }

  public void detach()
    {
      cost.detach();
      copyrightandotherrestrictions.detach();
      description.detach();
    }

  public boolean isEdited()
    {
      return cost.isEdited() || copyrightandotherrestrictions.isEdited() || description.isEdited();
    }
}


