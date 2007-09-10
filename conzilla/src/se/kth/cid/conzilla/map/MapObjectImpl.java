/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Vector;

import se.kth.cid.conzilla.map.graphics.Mark;
import se.kth.cid.util.LocaleManager;

public abstract class MapObjectImpl implements MapObject, PropertyChangeListener
{
  Vector     marks;
  Vector     responsibles;
  protected Vector     boundingboxes;

  protected MapDisplayer displayer;
  protected PropertyChangeListener colorListener;

  boolean visible= true;
  
  public MapObjectImpl(MapDisplayer displayer)
    {
      this.displayer   = displayer;

      marks = new Vector();
      responsibles= new Vector();

      colorListener=new PropertyChangeListener() {
	      public void propertyChange(PropertyChangeEvent evt)
	      {
		  getMark(MapObjectImpl.this).update();
		  colorUpdate();
	      }};
      boundingboxes=null;
      LocaleManager.getLocaleManager().addPropertyChangeListener(this);
    }  

    public abstract void colorUpdate();

    public void setVisible(boolean vis)
    {
	visible = vis;
    }

  public boolean getVisible()
    {
	return visible;
    }

  public MapDisplayer getDisplayer()
    {
      return displayer;
    }

  public void propertyChange(PropertyChangeEvent e)
    {
	if(e.getPropertyName().equals(LocaleManager.DEFAULT_LOCALE_PROPERTY))
	   localeUpdated();
    }
  protected void localeUpdated() {}
  
  /////////// Update support ///////////  


  protected void dumpCache()
  {
    boundingboxes = null;    
  }
      

  /////////// Editing/painting methods //////////////
  
  public void setDisplayLanguageDiscrepancy(boolean b) {}
  
  public void clearMark()
    {
      Object responsible = responsibles.elementAt(0);
      Object mark = marks.elementAt(0);

      marks = new Vector();
      responsibles= new Vector();

      if (responsibles!=null && mark!=null)
	  {
	      responsibles.addElement(responsible);
	      marks.addElement(mark);
	  }

      colorUpdate();
      displayer.repaintMap(getBoundingboxes());
    }      
  public Mark getMark()
    {
	if (marks.size()>0)
	    return (Mark) marks.lastElement();
	return new Mark((String) null, null, null);
    }

  public Mark getMark(Object responsible)
    {
	int index=responsibles.lastIndexOf(responsible);
	if (index!=-1)
	    return (Mark) marks.elementAt(index);
	return null;
    }

  public void pushMark(Mark mark, Object responsible)
    {
	initMark(mark, responsible);
	colorUpdate();
	displayer.repaintMap(getBoundingboxes());
    }

  protected void initMark(Mark mark, Object responsible)
    {
	responsibles.addElement(responsible);
	marks.addElement(mark);
    }
  public Mark popMark(Object responsible)
    {
	int index=responsibles.lastIndexOf(responsible);
	if (index!=-1)
	    {
		responsibles.remove(index);
		Mark mark=(Mark) marks.remove(index);
		colorUpdate();
		displayer.repaintMap(getBoundingboxes());
		return mark;
	    }
	return null;
    }
  public void replaceMark(Mark newMark, Object responsible)
    {
	int index=responsibles.lastIndexOf(responsible);
	if (index!=-1)
	  {
	      marks.setElementAt(newMark, index);
	      colorUpdate();
	      displayer.repaintMap(getBoundingboxes());
	  }
    }

    public boolean isDefaultMark()
    {
	return marks.size()==1;
    }

    public abstract Collection getBoundingboxes();

  ///////////// Detaching ///////////////

  public final void detach()
    {
      LocaleManager.getLocaleManager().removePropertyChangeListener(this);
      clearMark(); //Need to be done before things are detached.
      detachImpl();
    }
    
  protected abstract void detachImpl();  
}
