/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;

import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.util.ResourceUtil;
import se.kth.cid.tree.TreeTagNode;

/** Handles the rather complicated state controll for
 *  the createLayer.
 *
 *  @author Matthias Palmer
 */
public class CreateStateControl {
    //Actions
    public static final int POSITION_CHOOSEN_FOR_CONCEPT = 0;
    public static final int SUBJECT_CHOOSEN_FOR_TRIPLE = 1;
    public static final int OBJECT_CHOOSEN_FOR_TRIPLE = 2;
    public static final int CREATE_NEW_LITERAL = 3;
    public static final int NOTHING_HAPPEND = 4;

    //States
    public static final int IDLE_STATE = 5;
    public static final int WAITING_FOR_OBJECT_STATE = 6;
    
    

	public static final String clipboardBoxURL = "http://www.conzilla.org/builtin/types/clipboardBox";
	public static final String clipboardArrowURL = "http://www.conzilla.org/builtin/types/clipboardArrow";

    Concept concept;
    Concept templateTypeConcept;
    int state;
    TreeTagNode type;
    String drawType;
    ConzillaKit kit;
    MapController controller;
    boolean isProperty;
    MapEvent mapEvent;

    public CreateStateControl(MapController controller) {
        this.controller = controller;
        this.kit = ConzillaKit.getDefaultKit();
        state = IDLE_STATE;
    }
    
    public void clearState() {
        state = IDLE_STATE;
    }

    public int changeState(MapEvent m) {
        mapEvent = m;
        if (m.mouseEvent.getID() != MouseEvent.MOUSE_CLICKED)
            return state;
        
        switch (state) {
            case IDLE_STATE:
        
                //Clicking in the background in idle mode, we position a concept.
                if (m.hitType == MapEvent.HIT_NONE) {
                    //However, if we are creating properties nothing should happen. 
                    //(We remain in state idle).
                    if (isProperty )
                        return NOTHING_HAPPEND;
        
                    //Since this was an instantatious operation we remain in state idle.
                    //And we report that a position was choosen.
                    return POSITION_CHOOSEN_FOR_CONCEPT;
                    
                        
                //Clicking on anything in idle mode, we choose the subject in a triple.
                } else {
                    //However, if we are not creating properties nothing should happen.
                    //(We remain in state idle.)
                    if (!isProperty)
                        return NOTHING_HAPPEND;
                    
                    //Since it is a two stage opreation we change the state to waiting for object.
                    state = WAITING_FOR_OBJECT_STATE;
                    //Then we return what happend, that we choose the subject.
                    return SUBJECT_CHOOSEN_FOR_TRIPLE;
                }
                
                
            case WAITING_FOR_OBJECT_STATE:

                //In any case we are finished, hence we change state back to idle.
                state = IDLE_STATE;
            
                //Clicking in the background when waiting for an object is interpreted
                //as that the object should be a new literal. 
                if (m.hitType == MapEvent.HIT_NONE) {
                    return CREATE_NEW_LITERAL;
                
                //Clicking on anything but the background means choosing that as the object in the triple.
                } else {
                    return OBJECT_CHOOSEN_FOR_TRIPLE;
                }
            
            default:
                return NOTHING_HAPPEND;
        }
    }

    public int getState() {
        return state;
    }

    public Concept getConcept() {
        return concept;
    }

    public boolean isReady() {
        return type != null;
    }
    
    public boolean isProperty() {
        return isProperty;
    }

    public void setTemplateType(TreeTagNode tag) {
        type = tag;
        Concept conceptType = (Concept) tag.getUserObject();
        isProperty = ResourceUtil.isResourceOfClassProperty(conceptType);
        drawType = type.getURI().substring(type.getURI().lastIndexOf('/') + 1);
    }

    public URI getTypeURI() {
		String tv = type.getValue();
        if (tv != null) {
        	if (tv.equals(clipboardBoxURL) ||
        		 tv.equals(clipboardArrowURL)) {
				Transferable trans =
					 Toolkit
						 .getDefaultToolkit()
						 .getSystemClipboard()
						 .getContents(
						 null);
				 try {
					 String uri =
						 (String) trans.getTransferData(
							 DataFlavor.stringFlavor);
					 if (uri != null)
						return URI.create(uri);
				 } catch (UnsupportedFlavorException ue) {} catch (IOException ie) {}
        	}
        	else
				return URI.create(type.getValue());
        }
        return null;
    }

    public String getTypeName() {
        return drawType;
    }

    public boolean typeOK() {
        return true;
    }

    public String getDrawName() {
        if (drawType == null) {
            if (templateTypeConcept != null)
                drawType = getTypeName();
            else
                drawType = "no type selected";
            if (drawType.equals(""))
                drawType = "[Unknown type name]";
        }
        
        return drawType;
    }
}
