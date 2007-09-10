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
import se.kth.cid.component.*;
import java.awt.*;
import java.util.*;

public class NeuronStyle implements EditListener
{
  public static final int FIRST_NEURONSTYLE_EDIT_CONSTANT  = ConceptMap.LAST_CONCEPTMAP_ONLY_EDIT_CONSTANT + 1;
  public static final int BOUNDINGBOX_EDITED      	   = FIRST_NEURONSTYLE_EDIT_CONSTANT;
  public static final int TITLE_EDITED            	   = FIRST_NEURONSTYLE_EDIT_CONSTANT + 1;
  public static final int DATATAG_ADDED            	   = FIRST_NEURONSTYLE_EDIT_CONSTANT + 2;
  public static final int DATATAG_REMOVED            	   = FIRST_NEURONSTYLE_EDIT_CONSTANT + 3;
  public static final int LINE_EDITED             	   = FIRST_NEURONSTYLE_EDIT_CONSTANT + 4;
  public static final int DETAILEDMAP_EDITED      	   = FIRST_NEURONSTYLE_EDIT_CONSTANT + 5;
  public static final int LAST_NEURONSTYLE_EDIT_CONSTANT   = FIRST_NEURONSTYLE_EDIT_CONSTANT + 5;
  
  public class NeuronStyleEditObject 
  {
    public NeuronStyle neuronStyle;
    public Object      target;
    public NeuronStyleEditObject(NeuronStyle ns, Object o)
    {
      neuronStyle = ns;
      target = o;
    }

    public String toString()
    {
      return "NeuronStyleEditObject[" + neuronStyle + "," + target + "]";
    }
  }
  
  private ComponentLoader loader;
  private ConceptMap conceptmap;
  private Object     appobject;
  private Color      mark;

  private URI        detailedMap;
  ////////////Load-Neuron-varaibles//////////
  private NeuronType neurontype;
  private Neuron     neuron;
  private URI        neuronuri;
    
  ////////////NeuronStyle-varibles///////////
  private Rectangle  boundingbox;
  private String     title;  //Should never be null, only of zero length.
  private Point[]    line;
  private int        visibility;
  private Vector     datatags;
  // visible neurons??
  ////////////RoleStyle-varibles/////////////
  private Vector     roles;
  private Vector     playsroles;

  /////constructors and destructors//////////
  public NeuronStyle(ComponentLoader loader, URI neuronuri, ConceptMap conceptmap) throws ComponentException, NeuronStyleException
    {
      this.loader = loader;
      this.conceptmap=conceptmap;
      detailedMap=null;
      boundingbox=null;
      title="";
      roles=new Vector();
      playsroles=new Vector();
      appobject=null;
      line=new Point[0];
      datatags=new Vector();
      this.neuronuri=neuronuri;
      neuron=null;
      neurontype=null;
      revive();
    }
    public NeuronStyle(NeuronStyle copy) throws ComponentException, NeuronStyleException
    {
      loader=copy.loader;
      if (copy.detailedMap!=null)
	detailedMap=new URI(copy.detailedMap);
      else detailedMap=null;
      if (copy.boundingbox!=null)
	boundingbox=new Rectangle(copy.boundingbox);
      else boundingbox=null;
      if (copy.title!=null)
	title=new String(copy.title);
      else
	title=null;
      roles=new Vector();
      playsroles=new Vector();
      appobject=null;
      line=new Point[0];
      datatags=(Vector) copy.datatags.clone();
      if (copy.neuronuri!=null)
	neuronuri=new URI(copy.neuronuri);
      else neuronuri=null;
      
      conceptmap=null;
      neuron=null;
      neurontype=null;
      revive();              //can't just copy neuron and neurontype,
                            //it would ruin the garbagecollector in the cache
                           //if it exists. 
      mark=null;
    }

  public void revive() throws ComponentException, NeuronStyleException
    {
      disconnectNeuron();
      se.kth.cid.component.Component comp = loader.loadComponent(neuronuri, loader);
      if(! (comp instanceof Neuron))
	{
	  loader.releaseComponent(comp);
	  throw new NeuronStyleException("Component " + neuronuri +
					 "was no Neuron!!");
	}
      neuron = (Neuron) comp;
      neuron.addEditListener(this);
      
      disconnectNeuronType();
      try {
	comp = loader.loadComponent(new URI(neuron.getType()), loader);
      } catch (MalformedURIException e)
	{
	  Tracer.trace("Neuron had illegal type URI: " + neuron.getType()
		       + ": " + e.getMessage() + "!", Tracer.ERROR);
	  throw new NeuronStyleException("Neuron had illegal type URI: "
					 + neuron.getType() + ": " +
					 e.getMessage() + "!");
	}
      if(! (comp instanceof NeuronType))
	{
	  loader.releaseComponent(comp);
	  throw new NeuronStyleException("Component " + neuron.getType() +
					 "was no NeuronType!!");
	}
      neurontype = (NeuronType) comp;
      mark=null;
    }
  public void disconnectNeuron()
    {
      if (neuron!=null)
	{
	  neuron.removeEditListener(this);
	  loader.releaseComponent(neuron);
	  neuron=null;
	}
    }
  public void disconnectNeuronType()
    {
      if (neurontype!=null)
	{
	  loader.releaseComponent(neurontype);
	  neurontype=null;
	}
    }
  protected void finalize()
    {
      disconnectNeuron();
      disconnectNeuronType();
    }

  ////////Implements EditListener/////////////
  public void componentEdited(EditEvent e)
    {
      if (e.getEditType()==se.kth.cid.component.Component.URI_EDITED)
	{
	  try {
	    URI newuri=new URI((String) e.getTarget());
	    if (conceptmap!=null)   //only true if neuronstyle is disconnected
	      {
		Tracer.debug("Conceptmap: "+conceptmap.getURI());
		Tracer.debug(neuronuri.toString() +" ==> "+ newuri.toString());
		conceptmap.renameNeuronStyle(neuronuri,newuri);
	      }
	    neuronuri=newuri;
	  } catch (MalformedURIException me)
	    {
	      Tracer.trace("Inside componentEdited in NeuronStyle is somethig"
			   +"seriously wrong, the string got from the EditEvent"
			   +"should be possible to bring back in the form of a"
			   +"URI-ocject it just were!\n"+me.getMessage(),Tracer.BUG);
	    }
	}
    }
  
  /** After the neuronstyle is disconnected all data within are freezed,
   *  any try to edit will result in a serious error, a bug.
   *  All you can do with this neuronstyle is to throw it away or connect it
   *  again, possibly to another conceptmap.
   *
   *  @see connect for how to connect with a new map.*/
  public void disconnect() throws ReadOnlyException
    {
      if (!isEditable())
      	throw new ReadOnlyException("");

      for (;roles.size() > 0;)
	((RoleStyle) roles.elementAt(roles.size() - 1)).disconnect();

      for (;playsroles.size() > 0;)
	((RoleStyle) playsroles.elementAt(playsroles.size() - 1)).disconnect();

      try {
	conceptmap.removeNeuronStyle(new URI(neuron.getURI()));	
      } catch (MalformedURIException e)
	{
	  Tracer.trace("Neuron had illegal URI: " + neuron.getURI()
		       + ": " + e.getMessage() + "!", Tracer.ERROR);	  
	}
      conceptmap=null;
    }

  /** This is the way to bring life into a dead neuronstyle, it calls
   *  conceptmap to connect this neuronstyle with others roles.
   *
   *  @see disconnect   */
  public boolean connect(ConceptMap conceptmap)
    {
      if (conceptmap.addNeuronStyle(this))
	{
	  this.conceptmap=conceptmap;
	  return true;
	}
      else
	return false;
    }

  public boolean isConnected()
    {
      return conceptmap!=null;
    }
  public boolean isNeuronConnected()
    {
      return neuron!=null;
    }
  
  ///////Non-visuall stuff///////
  public boolean isEditable()
    {
      if (conceptmap!=null)
	return conceptmap.isEditable();
      else
	return true;
    }
  
  public Neuron     getNeuron()
    {
      return neuron;
    }
  
  public NeuronType getNeuronType()
    {
      return neurontype;
    }
  
  public Object  getAppObject()
    {
      return appobject;
    }
  
  public void    setAppObject(Object ob)
    {
      appobject=ob;
    }
  
  ////////////NeuronStyle////////////////////
  public URI getURI()
    {
      return neuronuri;
    }
  
  public void setURI(URI uri)
    {
      if (neuron!=null)
	{
	  loader.renameComponent(neuronuri,uri);
	  try {
	    neuron.setURI(uri.toString());
	  } catch (MalformedURIException me)
	    {
	      Tracer.trace("This error should be impossible, since we are converting a URI to"
			   +"a String and back again.\n"+me.getMessage(),Tracer.BUG);
	    }
	}
    }
  
  public Rectangle getBoundingBox()
    {
      return boundingbox;
    }
  
  public void setBoundingBox(Rectangle rect) throws ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
      boundingbox=rect;
      if (conceptmap!=null)
	conceptmap.fireEditEvent(new EditEvent(conceptmap, BOUNDINGBOX_EDITED,
					       new NeuronStyleEditObject(this, rect)));
    }
  
  public String getTitle()
    {
      return title;
    }

  public void setTitle(String title) throws ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
      this.title=title;
      if (conceptmap!=null)
	conceptmap.fireEditEvent(new EditEvent(conceptmap, TITLE_EDITED,
					     new NeuronStyleEditObject(this, title)));
    }
  
  public Vector getDataTags()
  {
    return datatags;
  }
  
  public void     addDataTag(String tag) throws ReadOnlyException
  {
    if (!isEditable())
      throw new ReadOnlyException("");
    int index=datatags.indexOf(tag);
    if (index == -1)
      {
	datatags.addElement(tag);
	if (conceptmap!=null)
	  conceptmap.fireEditEvent(new EditEvent(conceptmap, DATATAG_ADDED,
					       new NeuronStyleEditObject(this, tag)));
      }
  }
  
  public void     removeDataTag(String tag) throws ReadOnlyException
  {
    if (!isEditable())
      throw new ReadOnlyException("");
    int index = datatags.indexOf(tag);
    
    if(index != -1)
      {
	datatags.removeElement(tag);
	if (conceptmap!=null)
	  conceptmap.fireEditEvent(new EditEvent(conceptmap, DATATAG_REMOVED,
					       new NeuronStyleEditObject(this, tag)));
      }
  }


  public int getVisibility()
    {
      return visibility;
    }

  public void setVisibility(int visibility) throws ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
      this.visibility=visibility;
    }

  public Point[] getLine()
    {
	Point np[]=new Point[line.length];
      for (int i=0; i<line.length ; i++)
	np[i]=new Point(line[i]);
      return np;
    }

  public void setLine(Point[] line) throws ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
      this.line=line;
      if (conceptmap!=null)
	conceptmap.fireEditEvent(new EditEvent(conceptmap, LINE_EDITED,
					     new NeuronStyleEditObject(this, line)));
    }
  
  public URI getDetailedMap()
  {
    return detailedMap;
  }

  public void setDetailedMap(URI mapURI) throws ReadOnlyException
  {
    if (!isEditable())
      throw new ReadOnlyException("");
    this.detailedMap = mapURI;
    if (conceptmap!=null)
      conceptmap.fireEditEvent(new EditEvent(conceptmap, DETAILEDMAP_EDITED,
					   new NeuronStyleEditObject(this, mapURI)));
  }
  
  ////////////RoleStyle-varibles/////////////
  public Vector getPlaysRoles()
    {
      return playsroles;
    }

  public Vector getRoles()
    {
      return roles;
    }

  public RoleStyle addRoleStyle(String type, URI roleto) throws ReadOnlyException,
      RoleStyleException, NullPointerException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
      
      if (type == null || roleto == null)
	throw new NullPointerException("Neither of the arguments to NeuronStyle.addRoleStyle are allowed to be null.");
      
      NeuronStyle ns=conceptmap.getNeuronStyle(roleto);
      if (ns==null)
	throw new RoleStyleException("The ConceptMap does not contain a neuron with URI="+roleto.toString());

      RoleType roleType = neurontype.getRoleType(type);
      if(roleType == null)
	throw new RoleStyleException("No such role \"" + type +
				     "\" in NeuronType " + neurontype.getURI());
      Role nroles[]=neuron.getRolesOfType(type);
      Role nrole=new Role(type, roleto.toString(),0,0);
      for (int i=0; i<nroles.length;i++)
	if (nroles[i].equals(nrole))
	  {
	    Enumeration en=roles.elements();
	    for (;en.hasMoreElements();)
	      if (((RoleStyle) en.nextElement()).getRole().equals(nrole))
		throw new RoleStyleException("Role "+ type + " is already visible in map.");

	    RoleStyle rs=new RoleStyle(nroles[i],roleType
				       ,this, ns, conceptmap);
	    roles.addElement(rs);
	    ns.playsroles.addElement(rs);
	    if (conceptmap!=null)
	      conceptmap.fireEditEvent(new EditEvent(conceptmap,
						   ConceptMap.ROLESTYLE_ADDED,
						   rs));
	    return rs;
	  }
      throw new RoleStyleException("No role with type="+type+" and URI="+roleto+" in neuron " + getNeuron().getURI());
    }

  /** This function should never be called directly,
   *  instead use the disconnect function directly on the rolestyle.
   *  @see RoleStyle
   *  @param rolestyle this is the connection between two neuron(styles).
   */
  public void removeRole(RoleStyle rolestyle) 
  {
    roles.removeElement(rolestyle);
  }

  /** This function should never be called directly,
   *  instead use the disconnect function directly on the rolestyle.
   *  @see RoleStyle
   *  @param rolestyle this is the connection between two neuron(styles).
   */
  public void removePlaysRole(RoleStyle rolestyle) 
  {
    playsroles.removeElement(rolestyle);
  }
  public void mark(Color co)
    {mark=co;}
  public Color getMark()
    {return mark;}
}



