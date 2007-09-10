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
import javax.swing.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.util.MenuLibraryListener;
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;
import se.kth.cid.identity.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.InputMap;
import javax.swing.ActionMap;

/** A user friendly layer for creation of neurons.
 *  Boxes are created via clicking in the background,
 *  relations via clicking on two or more boxes or
 *  relations of other types then the one you are 
 *  trying to create.
 *  Clicking on a existing neuron of the same type will,
 *  if the neurontype has axons, allow you to continue
 *  to add new axons on that neuron (i.e. a relation).
 *  When creating axons, clicking in thebackground will abort.
 *
 *  @author Matthias Palmer
 */
public class CreateLayer extends LayerComponent implements MapMouseInputListener, MenuLibraryListener
{
  protected GridModel gridModel;
  protected MapEvent current;
  protected MapEvent oldcurrent;

  Neuron createNeuron;
  Axon firstAxon;
  MapEvent firstAxonMapEvent;
  String firstAxonType;
  NeuronStyle owner;
  ConceptMap.Position startPoint;

  CreateStateControl stateControl;
  String drawName;
  Graphics savedg;
  int width;
  int oldwidth;
    protected static final KeyStroke space =  KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE ,0);
  protected AbstractAction spaceListener;
  InputMap iMap;
  ActionMap aMap;

  public CreateLayer(MapController controller, GridModel gridModel )
    {
	super(controller, false);
	this.gridModel = gridModel;
	stateControl = new CreateStateControl(controller);
	drawName = stateControl.getDrawName();
	width = 70;


	iMap = new InputMap();
	aMap = new ActionMap();
	spaceListener = new AbstractAction()  {
		public void actionPerformed(ActionEvent ae) {
		    stateControl.stepAxonType();
		    draw(current);
		}
	    };

	String spaceObject = "space";

	iMap.put(space, spaceObject);
	aMap.put(spaceObject, spaceListener);
    }

  public void activate(MapScrollPane pane)
    {
	MapDisplayer mapdisplayer = pane.getDisplayer();	
	ConceptMap.Dimension dim = mapdisplayer.getStoreManager().getConceptMap().getDimension();
	setSize(new Dimension(dim.width, dim.height));

	InputMap iMapOld  = pane.getDisplayer().getInputMap(JComponent.WHEN_FOCUSED);
	ActionMap aMapOld = pane.getDisplayer().getActionMap();
	
	aMap.setParent(aMapOld);	    
	iMap.setParent(iMapOld);
	
	pane.getDisplayer().setInputMap(JComponent.WHEN_FOCUSED, iMap); 
	pane.getDisplayer().setActionMap(aMap);
    }

  public void deactivate(MapScrollPane pane)
    {
	InputMap iMapOld=pane.getDisplayer().getInputMap(JComponent.WHEN_FOCUSED).getParent();
	ActionMap aMapOld=pane.getDisplayer().getActionMap().getParent();
	pane.getDisplayer().setInputMap(JComponent.WHEN_FOCUSED, iMapOld); 
	pane.getDisplayer().setActionMap(aMapOld);
    }

  private void draw(MapEvent m)
    {
	drawName = stateControl.getDrawName();
	oldcurrent = current;
	oldwidth = width;
	current = m;
	if (savedg!=null)
	    width = savedg.getFontMetrics().stringWidth(drawName);

	
	double scale = controller.getMapScrollPane().getDisplayer().getScale();	

	if (oldcurrent != null)
	    {
		if (startPoint != null)
		    {
			Rectangle rect1 = new Rectangle(startPoint.x, startPoint.y, 
							oldcurrent.mapX-startPoint.x,oldcurrent.mapY-startPoint.y);
			Rectangle prect1 = se.kth.cid.conzilla.edit.layers.handles.HandledObject.positiveRectangle(rect1); 
			repaint((int) (prect1.x*scale -1),(int) (prect1.y*scale -1), 
				(int) (prect1.width*scale +2),(int) (prect1.height*scale +2));
		    }
		repaint((int) ((oldcurrent.mapX)*scale-2), (int) (oldcurrent.mapY*scale -14), oldwidth+6, 18);
	    }
    
	if (current != null)
	    {
		if (startPoint != null)
		    {
			if (stateControl.getState() == CreateStateControl.CREATE_FIRST_AXON)
			    {
				NeuronStyle firstEnd = firstAxonMapEvent.mapObject.getNeuronStyle();
				ConceptMap.Position p = new ConceptMap.Position(current.mapX, current.mapY);
				startPoint = LayoutUtils.findPosition_FirstFromBody(firstEnd, p, gridModel); 
			    }

			Rectangle rect2 = new Rectangle(startPoint.x, startPoint.y, 
							current.mapX-startPoint.x,current.mapY-startPoint.y);
			Rectangle prect2 = se.kth.cid.conzilla.edit.layers.handles.HandledObject.positiveRectangle(rect2); 
			
			repaint((int) (prect2.x*scale -1),(int) (prect2.y*scale -1), 
				(int) (prect2.width*scale +2),(int) (prect2.height*scale +2));
		    }
		repaint((int) ((current.mapX)*scale-2), (int) (current.mapY*scale -14), width+6, 18);
	    }
    }

  private void undraw(MapEvent m)
    {
	oldcurrent = current;
	current = null;
	double scale = controller.getMapScrollPane().getDisplayer().getScale();
	
	if (oldcurrent != null)
	    {
		if (startPoint != null)
		    {
			Rectangle rect1 = new Rectangle(startPoint.x, startPoint.y, 
							oldcurrent.mapX-startPoint.x,oldcurrent.mapY-startPoint.y);
			Rectangle prect1 = se.kth.cid.conzilla.edit.layers.handles.HandledObject.positiveRectangle(rect1); 
			repaint((int) (prect1.x*scale -1),(int) (prect1.y*scale -1), 
				(int) (prect1.width*scale +2),(int) (prect1.height*scale +2));
		    }
		repaint((int) ((oldcurrent.mapX)*scale-2), (int) (oldcurrent.mapY*scale -14), width+6, 18);
	    }
    }

    public void mouseMoved(MapEvent m) 
    {
	stateControl.changeState(m);
	draw(m);
    }

    public void mouseDragged(MapEvent m) 
    {
	stateControl.changeState(m);
	draw(m);
    }

    public void mouseClicked(MapEvent m) 
    {
	if (!stateControl.isReady())
	    {
		stateControl.changeState(m);
		draw(m);
		return;
	    }
	switch(stateControl.changeState(m))
	    {
	    case CreateStateControl.CREATE_BOX:
		createBox(m);
		break;
	    case CreateStateControl.CREATE_FIRST_AXON:
		createFirstAxon(m);
		break;
	    case CreateStateControl.CREATE_SECOND_AXON:
		createSecondAxon(m);
		break;
	    case CreateStateControl.CONTINUE_N_AXON:
		resumeContinueCreateAxon(m);
		break;
	    case CreateStateControl.CREATE_N_AXON:
		continueCreateAxon(m);
		break;
	    default:
		startPoint = null;
		owner = null;
		firstAxon = null;
		firstAxonMapEvent = null;
		/*	    case CreateStateControl.CREATE_BOX_NO_BOXLINE:
	    case CreateStateControl.CREATE_BOX_WITH_BOXLINE:
	    break;*/
	    }	    
    }

    public void mousePressed(MapEvent m) 
    {
	stateControl.changeState(m);
	draw(m);
    }
    public void mouseReleased(MapEvent m)
    {
	stateControl.changeState(m);
	draw(m);
    }
    
    public void mouseEntered(MapEvent m) 
    {
	stateControl.changeState(m);
	draw(m);
    }
    public void mouseExited(MapEvent m)
    {
	stateControl.changeState(m);
	undraw(m);
    }

    public void layerPaint(Graphics2D g, Graphics2D original)
    {
	double scale = controller.getMapScrollPane().getDisplayer().getScale();
	savedg=g;	

	if (current != null)
	    {
		if (startPoint != null)
		    g.drawLine((int) (startPoint.x*scale), (int) (startPoint.y*scale),
			       (int) (current.mapX*scale), (int) (current.mapY*scale));			
		g.setColor(Color.white);
		g.fillRect((int) (current.mapX*scale-2), (int) (current.mapY*scale-12),width+4, 14);
		g.setColor(Color.gray);
		g.drawRect((int) (current.mapX*scale-2), (int) (current.mapY*scale-12),width+4, 14);
		g.setColor(Color.black);
		g.drawString(drawName, (int) (current.mapX*scale), (int) (current.mapY*scale));
	    }
    }

    public void selected(Neuron neuron)
    {
	stateControl.setTemplateType(neuron);
	drawName = stateControl.getDrawName();
    }

    public void createFirstAxon(MapEvent mapEvent)
    {
	startPoint = new ConceptMap.Position(mapEvent.mapX, mapEvent.mapY);
	firstAxonMapEvent = mapEvent;
	firstAxonType = stateControl.getCurrentAxonTypeBeforeClick();
    }

    public void createSecondAxon(MapEvent mapEvent)
    {
	createNeuron = createNeuron(this, controller.getConzillaKit(), stateControl);
	if(createNeuron == null)  //Should never happen...
	    return;
	firstAxon = addAxon(createNeuron, firstAxonMapEvent.mapObject.getNeuron(), firstAxonType);

	Axon secondAxon = addAxon(createNeuron, mapEvent.mapObject.getNeuron(), stateControl.getCurrentAxonTypeBeforeClick());
	owner = insertNeuronInMap(createNeuron);
	owner.setBodyVisible(false);
	NeuronStyle firstEnd = firstAxonMapEvent.mapObject.getNeuronStyle();
	NeuronStyle secondEnd = mapEvent.mapObject.getNeuronStyle();

	ConceptMap.Position e1 = startPoint;
	ConceptMap.Position e2 = LayoutUtils.findPosition_FirstFromBody(secondEnd, e1, gridModel);
	startPoint = new ConceptMap.Position(e1.x + ((e2.x-e1.x)/2), 
					     e1.y + ((e2.y-e1.y)/2));

	ConceptMap.Position [] pos1 = new ConceptMap.Position[2];
	ConceptMap.Position [] pos2 = new ConceptMap.Position[2];
	pos1[0] = startPoint;
	pos1[1] = e1;
	pos2[0] = startPoint;
	pos2[1] = e2;
	try {
	    AxonStyle axonStyle=owner.addAxonStyle(firstAxon.getURI(), firstEnd);
	    axonStyle.setLine(pos1);
	    
	    axonStyle=owner.addAxonStyle(secondAxon.getURI(), secondEnd);
	    axonStyle.setLine(pos2);
	} catch (ConceptMapException ce)
	    {
		Tracer.bug("Can't show one of the selected axons.....");
	    }
    }

    public void resumeContinueCreateAxon(MapEvent mapEvent)
    {
	createNeuron = mapEvent.mapObject.getNeuron();
	owner = mapEvent.mapObject.getNeuronStyle();
	startPoint = LayoutUtils.findPosition_FirstFromAxons(owner, new ConceptMap.Position(mapEvent.mapX, 
											    mapEvent.mapY),
							     gridModel);
    }
    
    
    public void continueCreateAxon(MapEvent mapEvent)
    {
	Axon axon = addAxon(createNeuron, mapEvent.mapObject.getNeuron(), stateControl.getCurrentAxonTypeBeforeClick());

	ConceptMap.Position [] pos = new ConceptMap.Position[2];
	pos[0] = startPoint;
	pos[1] = LayoutUtils.findPosition_FirstFromBody(mapEvent.mapObject.getNeuronStyle(), startPoint, gridModel); 

	try {
	    AxonStyle axonStyle = owner.addAxonStyle(axon.getURI(), mapEvent.mapObject.getNeuronStyle());
	    axonStyle.setLine(pos);
	} catch (ConceptMapException ce)
	    {
		Tracer.bug("Can't show one of the selected axons.....");
	    }
	//Startingpoint remains the same....or??
    }

    private static Axon addAxon(Neuron createNeuron, Neuron endNeuron, String type)
    {
	try {	
	    URI base = URIClassifier.parseValidURI(createNeuron.getURI());
	    URI absoluteURI=URIClassifier.parseValidURI(endNeuron.getURI());
	    String relativeURI;
	    try {
		relativeURI=base.makeRelative(absoluteURI, false);
	    } catch (MalformedURIException me)
		{
		    relativeURI=absoluteURI.toString();
		}
	    return createNeuron.addAxon(type, relativeURI);
	} catch (NeuronException ne){
	} catch (InvalidURIException ie) {}
	return null;
    }

    public void createBox(MapEvent mapEvent)
    {
	Neuron neuron = createNeuron(this, controller.getConzillaKit(), stateControl);
	if(neuron == null)  //Should never happen...
	    return;
	NeuronStyle ns = insertNeuronInMap(neuron);
	fixBox(mapEvent, ns);
    }

    public NeuronStyle insertNeuronInMap(Neuron neuron)
    {
      ConceptMap cmap = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
      
      URI base = URIClassifier.parseValidURI(cmap.getURI());
      URI absoluteURI = URIClassifier.parseValidURI(neuron.getURI());
      String relativeURI;
      try {
	relativeURI = base.makeRelative(absoluteURI, false);
      } catch (MalformedURIException me)
	{
	  relativeURI = neuron.getURI();
	}

      NeuronStyle ns = null;
      try {
	  ns = cmap.addNeuronStyle(relativeURI);
      } catch(InvalidURIException ex)
	{
	  Tracer.bug("Invalid URI: " + ex.getMessage());
	}
      
      try {
	relativeURI = absoluteURI.makeRelative(base, false);
      } catch (MalformedURIException me)
	{
	  relativeURI = cmap.getURI();
	}
      
      MetaData md = neuron.getMetaData();

      //context relation
      MetaData.Relation relation = new MetaData.Relation(new MetaData.LangString(null, "context"),
							 null,
							 relativeURI);
      MetaDataUtils.addObject(md, "relation", relation);      
      return ns;
    }
    
    public void fixBox(MapEvent mapEvent, NeuronStyle ns)
    {
	java.awt.Dimension dim=controller.getMapScrollPane().getDisplayer().getNeuronMapObject(ns.getURI()).getPreferredSize();
	ns.setBoundingBox(LayoutUtils.preferredBoxOnGrid(gridModel, 
							 mapEvent.mapX,
							 mapEvent.mapY,
							 dim));
    }




    //Help functions below.
    protected static Neuron createNeuron(java.awt.Component jc, ConzillaKit kit, CreateStateControl sc)
    {
	URI typeURI = sc.getTypeURI();
	String base = sc.getBase();
	
	if (!sc.typeOK())
	    return null;

	URI uri = null;
	Object[] createRet = null;
	Neuron neuron = null;
	for (int exponent=1 ; exponent<6 ; exponent++)
	    {	
		try {
		    uri = guessURI(base, exponent, sc);
		    createRet = kit.getComponentStore().getHandler().checkCreateComponent(uri);
		} catch (MalformedURIException me) {
		    ErrorMessage.showError("Malformed URI", "Can't create component: "
					   + uri, me, jc);
		    return null;
		} catch (PathComponentException ce) {
		    if (!createPathAndCheckCreate(jc,kit, ce, uri, createRet))
			//If newly created path can't contain uri, 
			//new guesses will probably also fail. 
			return null;
		} catch(ComponentException e) {
		    //Component with uri exists... guess new uri.
		    continue;
		}

		//Component can't exist in cache.
		if (kit.getComponentStore().getCache().getComponent(uri.toString()) != null)  
		    continue;

		//Ok, success!
		break;
	    }		
	try {
	    neuron = kit.getComponentStore().getHandler().createNeuron(uri, (URI) createRet[0], (MIMEType) createRet[1], typeURI);
	    adjustMetaData(neuron.getMetaData());
	    //	    fixData(n, nType);
	    kit.getComponentStore().getCache().referenceComponent(neuron);
	    neuron.setEdited(true);
	} catch(ComponentException e) {
	    ErrorMessage.showError("Create Error",
				   "Failed to create component.", e, jc);
	    return null;
	}

	return neuron;
    }

    protected static boolean createPathAndCheckCreate(java.awt.Component jc, ConzillaKit kit, 
						      PathComponentException ce,
						      URI uri, Object[] createRet)
    {
	int ans = JOptionPane.showConfirmDialog(jc, "The directories \n"
						+ ce.getPath() + "\n"
						+ "does not exist. \n"
						+ "Create necessary directorys for this uri?");
	if (ans == JOptionPane.YES_OPTION)
	    {
		if (ce.makePath())
		    {
			try {
			    createRet = kit.getComponentStore().getHandler().checkCreateComponent(uri);
			} catch (ComponentException ce2)
			    {
				ErrorMessage.showError("Create Error", "Couldn't create component with uri = '" + uri + 
						       "',\n"+ 
						       "possible reasons:\n"+
						       "1) The ftpconnection were closed down unexpectedly.\n"+
						       "2) You don't have access rights on this server,\n"+
						       "3) URI already exists.\n"+
						       "(typically the component were snatched just in front of you.\n"+
						       "Could be yourself manually creating the component on disk.)",ce2,jc);
				return false;
			    }
		    }
		else
		    { 
			ErrorMessage.showError("Create Error","Failed to create neccessary path", null, jc);
			return false;
		    }
	    }
	else 
	    return false; //no message neccessary since you've answered no above.
	return true;
    }
    
    protected static void adjustMetaData(MetaData md) throws ReadOnlyException
    {
      MetaData.LangString [] langStrings=new MetaData.LangString[1];
      String uri = md.getComponent().getURI();
      int slashpos = uri.lastIndexOf('/');
      String suri=uri.substring(slashpos+1);
      String local_language = MetaDataUtils.getLanguageString(Locale.getDefault());
      langStrings[0] = new MetaData.LangString(local_language, suri);
      md.set_general_title(new MetaData.LangStringType(langStrings));
      
      MetaData.Location [] locations = new MetaData.Location[1];
      locations[0]=new MetaData.Location("URI", md.getComponent().getURI());
      md.set_technical_location(locations);
      
      //More???
    }

    protected static URI guessURI(String base, int exponent, CreateStateControl sc) throws MalformedURIException
    {
	int guess = (int) (Math.random()*Math.pow(10.0, exponent));
	//	Tracer.debug("trying with uri : "+base+sc.getTypeName()+guess);
	return URIClassifier.parseURI(base+sc.getTypeName()+guess);
    }
}
