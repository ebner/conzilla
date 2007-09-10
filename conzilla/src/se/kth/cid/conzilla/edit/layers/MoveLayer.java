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


package se.kth.cid.conzilla.edit.layers;
import se.kth.cid.conzilla.edit.layers.handles.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

public class MoveLayer extends Layer
{
  LineTool linetool;
  TieTool tietool;
  boolean textEdit;
  NeuronMapObject editObject;
  boolean focusCalled;
  HandleStore store;
  protected boolean lock=false;
    
  public MoveLayer(MapController controller, LineTool linetool, TieTool tietool)
  {
    super(controller);
    this.linetool=linetool;
    this.tietool=tietool;
    textEdit=false;
    focusCalled=false;
    store = new HandleStore(((EditMapManager) controller.getManager()).getGridModel());
    /*    registerKeyboardAction(new AbstractAction() {
	    public void actionPerformed(ActionEvent ae)
	    {
		Tracer.debug("keyboardAction!!");
		mapevent.mapX-=5;
		handles.drag(mapevent);
		repaint();
	    }},"left",KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0),WHEN_IN_FOCUSED_WINDOW);
    */
  }
	       
  protected boolean focus(MapEvent m)
  {
      if (m.mouseEvent.isShiftDown())
	  {
	      if (handles instanceof HandledMark)
		  return false;

	      store.clear();	      
	      MapEvent oldm=mapevent;
	      setHandledObject(new HandledMark(m, store, tietool),m);
	      if (oldm != MapEvent.Null)
		  handles.click(oldm);
	      return true;
	  }

      if (textEdit)
	  return false;
      focusCalled = true;

      if ( mapevent.hitType == m.hitType &&       //If same type, same mapobject and not first klick, 
	   m.mapObject == mapevent.mapObject &&   //then old focus will do.
	   mapevent!=MapEvent.Null)
	  return false;

      store.clear();

      switch (m.hitType)
	  {
	  case MapEvent.HIT_NONE:
	      setHandledObject(new HandledMap(m, mapdisplayer.getStoreManager().getConceptMap(), this, store, tietool, controller.getMapScrollPane().getDisplayer()),m);
	      return true;
	  case MapEvent.HIT_BOX:
	  case MapEvent.HIT_BOXTITLE:
	  case MapEvent.HIT_BOXDATA:
	      setHandledObject(new HandledBox(m, tietool, store),m);
	      return true;
	  case MapEvent.HIT_BOXLINE:
	      setHandledObject(new HandledNeuronLine(m,linetool, tietool, store),m);
	      return true;
	  case MapEvent.HIT_AXONLINE:
	      setHandledObject(new HandledLine(m, linetool, tietool, store),m);
	      return true;
	  }
      return false;
  }

  public void  mousePressed(MapEvent m)
    {
	if (textEdit)
	    {
		MapObject mo=m.mapObject;
		if (mo instanceof NeuronMapObject && !((NeuronMapObject) mo).getEditable()) //An inconsistent state, typically someone has resetted the mapDisplayer.
		    {
			mapevent=MapEvent.Null;

			editTextMode(false, m);
			focusCalled=false;
			super.mousePressed(m);
			return;
		    }
		
		if (m.mapObject!=editObject || m.hitType != MapEvent.HIT_BOXTITLE)
		    editTextMode(false, m);
		else
		    return;
	    }
	focusCalled=false;
	super.mousePressed(m);
    }

  public void mouseReleased(MapEvent m)
    {
	super.mouseReleased(m);
	if (handles instanceof HandledMap)
	    {
		Collection sel = ((HandledMap) handles).getSelected();		
		if (sel!=null && !sel.isEmpty())
		    {
			((HandledMap) handles).loadFromModel();
			setHandledObject(new HandledMark(m, store, tietool),m);
			mapevent = MapEvent.Null;
			((HandledMark) handles).setSelected(sel);
		    }
	    }
		
	lock = true;
	store.set();
	lock = false;
    }
  public void mouseClicked(MapEvent m)
    {
	if (!m.mouseEvent.isShiftDown())
	    if (!textEdit && !focusCalled && m.hitType==MapEvent.HIT_BOXTITLE)
		{
		    editObject = (NeuronMapObject) m.mapObject;
		    editTextMode(true, m);
		    setHandledObject(null,m);
		}
	super.mouseClicked(m);
	lock = true;
	store.set();
	lock = false;
    }

    protected void editTextMode(boolean e, MapEvent m)
    {
	textEdit=e;
	TitleDrawer td = editObject.getTitleDrawer();
	JTextArea title = td.getEditableTextComponent();
	mapdisplayer.doAttractFocus(!e);
	td.setTitleVisible(!e);
	if (e)
	    {
		add(title);
		title.setBounds(td.getTitleBounds());
	    }
	else
	    remove(title);
	editObject.setEditable(e, m);
    }

  public void layerPaint(Graphics2D g, Graphics2D original)
  {
      g.setColor(Color.black);
      if (handles!=null)
	  handles.paint(g, original);
      else
	  store.paint(g);
      //      paintChildren(g);
  }

  public void componentEdited(EditEvent e)
  {
    if (lock ||
	!(e.getEditType() > ConceptMap.FIRST_CONCEPTMAP_EDIT_CONSTANT &&
	  e.getEditType() <= ConceptMap.LAST_CONCEPTMAP_EDIT_CONSTANT))
	return;

    if (handles!=null)
	if (!handles.update(e))   
	    {
		store.clear(); //A bit brutal???
		setHandledObject(null, mapevent); //The object didn't survive the update.
	    }
  }
}
