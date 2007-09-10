/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;

import javax.swing.JTextArea;

import se.kth.cid.component.EditEvent;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.EditMapManager;
import se.kth.cid.conzilla.edit.LineTool;
import se.kth.cid.conzilla.edit.TieTool;
import se.kth.cid.conzilla.edit.TripleEdit;
import se.kth.cid.conzilla.edit.layers.handles.HandleStore;
import se.kth.cid.conzilla.edit.layers.handles.HandledBox;
import se.kth.cid.conzilla.edit.layers.handles.HandledBoxLine;
import se.kth.cid.conzilla.edit.layers.handles.HandledLine;
import se.kth.cid.conzilla.edit.layers.handles.HandledLiteralBox;
import se.kth.cid.conzilla.edit.layers.handles.HandledMap;
import se.kth.cid.conzilla.edit.layers.handles.HandledMark;
import se.kth.cid.conzilla.edit.menu.OverBackgroundMenu;
import se.kth.cid.conzilla.edit.menu.OverConceptMenu;
import se.kth.cid.conzilla.edit.menu.OverTripleMenu;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.map.graphics.ConceptMapObject;
import se.kth.cid.conzilla.map.graphics.DrawerMapObject;
import se.kth.cid.conzilla.map.graphics.TitleDrawer;
import se.kth.cid.conzilla.map.graphics.TripleMapObject;
import se.kth.cid.conzilla.tool.MapToolsMenu;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;

public class MoveLayer extends Layer {
    LineTool linetool;
    TieTool tietool;
    boolean textEdit;
    DrawerMapObject editObject;
    boolean focusCalled;
    HandleStore store;
    protected boolean lock = false;

    MapToolsMenu menu1;
    MapToolsMenu menu2;
    MapToolsMenu menu3;
    private boolean textIsInBox;

    public MoveLayer(
        MapController controller,
        LineTool linetool,
        TieTool tietool,
        EditMapManager mm) {
        super(controller);
        this.linetool = linetool;
        this.tietool = tietool;
        textEdit = false;
        focusCalled = false;
        store = mm.getHandleStore();

        TripleEdit tripleEdit = new TripleEdit(controller, mm);

        menu1 = new OverConceptMenu(controller, tripleEdit, mm);
        menu2 = new OverTripleMenu(controller, tripleEdit, mm);
        menu3 = new OverBackgroundMenu(controller, mm);

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
    
    public void editTextOnMapObject(MapObject mo) {
        if (mo.getConcept().getTriple() == null) {
            enableTextMode(null, mo, true);
        } else if (mo.getConcept().getTriple().isObjectLiteral()){
            enableTextMode(null, mo, false);
        }
    }

    protected boolean focus(MapEvent m) {
        if (m.mouseEvent.isShiftDown()) {
            if (handles instanceof HandledMark)
                return false;

            store.clear();
            MapEvent oldm = mapevent;
            setHandledObject(new HandledMark(m, store, tietool), m);
            if (oldm != MapEvent.Null)
                handles.click(oldm);
            return true;
        }

        if (textEdit)
            return false;
        focusCalled = true;

        if (mapevent.hitType == m.hitType
            && //If same type, same mapobject and not first klick, 
        m.mapObject
                == mapevent.mapObject
            && //then old focus will do.
        mapevent != MapEvent.Null)
            return false;

        store.clear();

        if (m.hitType != MapEvent.HIT_NONE 
            && !store.editableInCurrentSession(m.mapObject.getDrawerLayout())) {
                return false;
        }
        
        switch (m.hitType) {
            case MapEvent.HIT_NONE :
                setHandledObject(
                    new HandledMap(
                        m,
                        mapdisplayer.getStoreManager().getConceptMap(),
                        this,
                        store,
                        tietool,
                        controller.getView().getMapScrollPane().getDisplayer()),
                    m);
                return true;
            case MapEvent.HIT_BOX :
            case MapEvent.HIT_BOXTITLE :
            case MapEvent.HIT_BOXDATA :
                setHandledObject(new HandledBox(m, tietool, store), m);
                return true;
            case MapEvent.HIT_BOXLINE :
                setHandledObject(
                    new HandledBoxLine(m, linetool, tietool, store),
                    m);
                return true;
            case MapEvent.HIT_TRIPLELINE :
                setHandledObject(
                    new HandledLine(m, linetool, tietool, store),
                    m);
                return true;
            case MapEvent.HIT_TRIPLELITERAL :
            case MapEvent.HIT_TRIPLELITERALBOX :
                setHandledObject(new HandledLiteralBox(m, tietool, store), m);
                return true;
        }
        return false;
    }
    
    public void mousePressed(MapEvent m) {
        if (textEdit) {
            MapObject mo = m.mapObject;
            if (mo instanceof ConceptMapObject
                && !((ConceptMapObject) mo).getEditable())
                //An inconsistent state, typically someone has resetted the mapDisplayer.
                {
                mapevent = MapEvent.Null;

                disableTextMode(m);
                focusCalled = false;
                super.mousePressed(m);
                return;
            }

            if (m.mapObject != editObject)
                disableTextMode(m);
            else
                return;
        }
        focusCalled = false;
        super.mousePressed(m);
    }

    public void mouseReleased(MapEvent m) {
        super.mouseReleased(m);
        if (handles instanceof HandledMap) {
            Collection sel = ((HandledMap) handles).getSelected();
            if (sel != null && !sel.isEmpty()) {
                ((HandledMap) handles).loadFromModel();
                setHandledObject(new HandledMark(m, store, tietool), m);
                mapevent = MapEvent.Null;
                ((HandledMark) handles).setSelected(sel);
            }
        }

        lock = true;
        store.set();
        lock = false;
    }
    public void mouseClicked(MapEvent m) {
        if (!m.mouseEvent.isShiftDown()
                && !textEdit
                && !focusCalled
                && handles != null) {
            /*(m.hitType == MapEvent.HIT_BOXTITLE
                    || m.hitType == MapEvent.HIT_BOX
                    || m.hitType == MapEvent.HIT_TRIPLELITERAL
                    || m.hitType == MapEvent.HIT_TRIPLELITERALBOX))*/
            if ((handles instanceof HandledBox 
                    && ((HandledBox) handles).isWithinTotalHandle(m))
                || ((handles instanceof HandledLine)
                    && ((HandledLine) handles).isWithinBoxHandle(m))) {
                enableTextMode(m, handles.getMapObject(), true);
                setHandledObject(null, m);
            } else if (handles instanceof HandledLiteralBox
                    && ((HandledLiteralBox) handles).isWithinLiteralHandle(m)){
                enableTextMode(m, handles.getMapObject(), false);
                setHandledObject(null, m);
            }
        }
        super.mouseClicked(m);
        lock = true;
        store.set();
        lock = false;
    }

    protected void enableTextMode(MapEvent m, MapObject mo, boolean inBox) {
        textIsInBox = inBox;
        if (textEdit) {
            disableTextMode(m);
        }
        textEdit = true;
        editObject = (DrawerMapObject) mo;
        TitleDrawer td;
        if (textIsInBox)
            td = editObject.getTitleDrawer();
        else
            td = ((TripleMapObject) editObject).getLiteralDrawer();
        JTextArea title = td.getEditableTextComponent();
        mapdisplayer.doAttractFocus(false);
        td.setTitleVisible(false);
        add(title);
        if (textIsInBox)
            editObject.setEditable(true, m);
        else
             ((TripleMapObject) editObject).setLiteralEditable(true, m);
        title.setBounds(td.getTitleBounds());
    }

    public void disableTextMode(MapEvent m) {
        textEdit = false;
        TitleDrawer td;
        if (editObject == null) {
            return;
        }
        
        if (textIsInBox)
            td = editObject.getTitleDrawer();
        else
            td = ((TripleMapObject) editObject).getLiteralDrawer();
        JTextArea title = td.getEditableTextComponent();
        mapdisplayer.doAttractFocus(true);
        td.setTitleVisible(true);
        remove(title);
        if (textIsInBox)
            editObject.setEditable(false, m);
        else
             ((TripleMapObject) editObject).setLiteralEditable(false, m);

        editObject.updateBox();
        editObject = null;
    }

    public void layerPaint(Graphics2D g, Graphics2D original) {
        g.setColor(Color.black);
        if (handles != null)
            handles.paint(g, original);
        //   else
        //      store.paint(g);
        //      paintChildren(g);
    }

    public void componentEdited(EditEvent e) {
        if (lock
            || !(e.getEditType() > ContextMap.FIRST_CONTEXTMAP_EDIT_CONSTANT
                && e.getEditType() <= ContextMap.LAST_CONTEXTMAP_EDIT_CONSTANT))
            return;

        if (handles != null)
            if (!handles.update(e)) {
                store.clear(); //A bit brutal???
                setHandledObject(null, mapevent);
                //The object didn't survive the update.
            }
    }

    public void popupMenu(MapEvent m) {
        if (m.mouseEvent.isPopupTrigger() && !m.isConsumed()) {
            switch (m.hitType) {
                case MapEvent.HIT_BOX :
                case MapEvent.HIT_BOXTITLE :
                case MapEvent.HIT_BOXDATA :
                    menu1.popup(m);
                    break;
                case MapEvent.HIT_BOXLINE :
                case MapEvent.HIT_TRIPLELINE :
                case MapEvent.HIT_TRIPLEDATA :
                case MapEvent.HIT_TRIPLELITERALBOX :
                case MapEvent.HIT_TRIPLELITERAL :
                    menu2.popup(m);
                    break;
                case MapEvent.HIT_NONE :
                    menu3.popup(m);
                    break;
            }
            m.consume();
        }
    }
}
