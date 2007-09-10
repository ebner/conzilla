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


package se.kth.cid.conzilla.layout;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import se.kth.cid.neuron.Neuron;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.edit.LayoutUtils;
import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.menu.DefaultMenuFactory;
import se.kth.cid.conzilla.browse.ViewAlterationTool;
import javax.swing.JMenu;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;


    import se.kth.cid.conzilla.edit.*;
    import se.kth.cid.conzilla.edit.layers.*;
    import se.kth.cid.conzilla.edit.layers.handles.*;
    import se.kth.cid.conzilla.map.*;

import att.grappa.*;

/** Connects to the external dot-program to do the layout via
 *  the grappa API.
 *  Developed by the AT&T folks.
 *
 *  @author Matthias Palmer.
 */
public class LayoutViaGraphViz implements Extra, Layout
{
    GridModel gridModel;

    public LayoutViaGraphViz()
    {
    }

    public boolean initExtra(ConzillaKit kit) 
    {
	gridModel = new GridModel(6);
	return true;
    }

    public String getName() {
	return "layout";
    }
    public void refreshExtra(){}
    public void showExtra() {}
    public void closeExtra() {}
    public boolean saveExtra() {return true;}
    public void exitExtra() {}

    public void extendMenu(final ToolsMenu menu,final MapController c)
    {
	if(menu.getName().equals(DefaultMenuFactory.TOOLS_MENU))
	    menu.addTool(new Tool("LAYOUT_VIA_GRAPHVIZ", LayoutViaGraphViz.class.getName())
		{
		    public void actionPerformed(ActionEvent ae)
		    {
			Tracer.debug("Layouts the current map.");
			layout(c);
		    }
		}, 200);
    }

    public void addExtraFeatures(final MapController c, final Object o, 
				 String location, String hint)
    {}

    protected String getTitle(MapController controller, NeuronStyle ns)
    {
	Neuron neuron = getNeuron(controller, ns);
	if(neuron != null)
	    {
		MetaData md=neuron.getMetaData();
		MetaData.LangString lstr = MetaDataUtils.getLocalizedString(md.get_metametadata_language(), md.get_general_title());
		return lstr.string;
	    }
	return "";
    }

    protected Neuron getNeuron(MapController controller, NeuronStyle ns)
    {
	try {
	    URI uri = URIClassifier.parseValidURI(ns.getNeuronURI(), ns.getConceptMap().getURI());
	    return controller.getConzillaKit().getComponentStore().getAndReferenceNeuron(uri);
	} catch (ComponentException ce) {}
	return null;
    }

    protected void makeGrappaBox(NeuronStyle ns, Graph graph, String title)
    {
	Node theNode = new Node(graph, ns.getID());
	theNode.setAttribute("shape", "box");
	if (title!=null)
	    theNode.setAttribute("label", title);
	else
	    {
		theNode.setAttribute(GrappaConstants.WIDTH_ATTR, new Double(0));
		theNode.setAttribute(GrappaConstants.HEIGHT_ATTR, new Double(0));
		theNode.setAttribute("fixedsize", "true");
	    }
	graph.addNode(theNode);
    }

    protected void layoutBox(NeuronStyle ns, Graph graph)
    {
	Node node = graph.findNodeByName(ns.getID());
	if (node == null)
	    return;
	//	Rectangle bb = node.getGrappaNexus().getBounds();
	//	Shape shape = node.getGrappaNexus().shape;
	//	if (!(shape instanceof RectangularShape))
	//	    return;

	PathIterator pit = node.getGrappaNexus().getPathIterator(); 
	double [] darr = new double[6]; 
	pit.currentSegment(darr);
	int x = (int) darr[0];
	int y = (int) darr[1];
	pit.next();
	pit.next();
	pit.currentSegment(darr);
	int width = (int) darr[0] - x;
	int height =  (int) darr[1] - y;

	//	RectangularShape rect = (RectangularShape) shape;
	ns.setBoundingBox(new ConceptMap.BoundingBox(x, -y-height, width, height));
	Tracer.debug("rectangle x="+x+" y="+y+" width="+width+" height="+height);
    }


    protected void makeGrappaLines(MapController controller, NeuronStyle owner, Graph graph)
    {
	AxonStyle [] ass = owner.getAxonStyles();

	NeuronMapObject nmo = controller.getMapScrollPane().getDisplayer().getNeuronMapObject(owner.getID());
	if (ass.length==2)
	    makeGrappaLine(nmo, ass[0], ass[1], graph);
	else
	    for (int j=0; j<ass.length;j++)
		makeGrappaLine(nmo, nmo.getAxonMapObject(ass[j].getAxonID()), graph);
    }
	
    protected void makeGrappaLine(NeuronMapObject nmo, AxonMapObject amo, Graph graph)
    {
	AxonStyle as = amo.getAxonStyle();
	Node own = graph.findNodeByName(as.getOwner().getID());
	Node end = graph.findNodeByName(as.getEnd().getID());
	Edge edge = null;
	if (amo.getAxonType() == null || amo.getAxonType().getHeadType().type.equals("none"))
 	    edge = new Edge(graph, end, own, as.getOwner().getID()+"?"+as.getAxonID());
	else
	    edge = new Edge(graph, own, end, as.getOwner().getID()+"?"+as.getAxonID());

	edge.setAttribute("arrowhead", "none");
	graph.addEdge(edge);
    }

    protected void makeGrappaLine(NeuronMapObject owner, AxonStyle as0, AxonStyle as1, Graph graph)
    {
	Node n0 = graph.findNodeByName(as0.getEnd().getID());
	Node n1 = graph.findNodeByName(as1.getEnd().getID());
	Edge edge = null;
	AxonMapObject amo = owner.getAxonMapObject(as0.getAxonID());
	if (amo.getAxonType() == null || amo.getAxonType().getHeadType().type.equals("none"))
	    edge = new Edge(graph, n0, n1, owner.getNeuronStyle().getID()+"?simple");
	else
	    edge = new Edge(graph, n1, n0, owner.getNeuronStyle().getID()+"?simple");

	edge.setAttribute("arrowhead", "none");
	graph.addEdge(edge);
    }
    
    protected void layoutLine(NeuronMapObject nmo, AxonMapObject amo, Graph graph)
    {
	boolean rewind = amo.getAxonType() == null || amo.getAxonType().getHeadType().type.equals("none");

	AxonStyle as = amo.getAxonStyle();
	Edge edge = graph.findEdgeByName(as.getOwner().getID()+"?"+as.getAxonID());
	if (edge == null) 
	    Tracer.debug("couldn't find edge, "+as.getOwner().getID()+"?"+as.getAxonID());
	else
	    Tracer.debug("found edge, "+as.getOwner().getID()+"?"+as.getAxonID());

	Vector vec = extractPoints(edge);
	ConceptMap.Position [] po = new ConceptMap.Position[vec.size()];
	if (rewind)
	    for (int i=0;i<vec.size() ;i++)
		po[i] = (ConceptMap.Position) vec.elementAt(vec.size()-1-i);
	else
	    vec.copyInto(po);
	as.setPathType(2);
	as.setLine(po);
    }
    protected void layoutLine(NeuronMapObject nmo, AxonMapObject amo0, AxonMapObject amo1, Graph graph)
    {
	boolean rewind = amo0.getAxonType() == null || amo0.getAxonType().getHeadType().type.equals("none");
	Edge edge = graph.findEdgeByName(nmo.getNeuronStyle().getID()+"?simple");
	
	if (edge == null)
	    Tracer.bug("couldn't find edge, "+nmo.getNeuronStyle().getID()+"?simple");
	else
	    Tracer.debug("found edge, "+edge.getName());

	Vector vec = extractPoints(edge);
	if (vec.size()==4)
	    splitCurve(vec);
	Vector vec2 = vec;
	if (!rewind)
	    {
		vec2 = new Vector();
		for (int i=0;i<vec.size() ;i++)
		    vec2.add(vec.elementAt(vec.size()-1-i));
	    }
	
	ConceptMap.Position [] po0 = new ConceptMap.Position[4];
	ConceptMap.Position [] po1 = new ConceptMap.Position[vec2.size()-3];
	for (int i=0;i<4 ;i++)
	    po0[po0.length-1-i] = (ConceptMap.Position) vec2.elementAt(i);
	
	po1[0]=new ConceptMap.Position(po0[0].x,po0[0].y);
	for (int i=4;i<vec2.size();i++)
	    po1[i-3] = (ConceptMap.Position) vec2.elementAt(i);
	
	amo0.getAxonStyle().setPathType(2);
	amo0.getAxonStyle().setLine(po0);
	amo1.getAxonStyle().setPathType(2);
	amo1.getAxonStyle().setLine(po1);
    }

    protected void splitCurve(Vector vec)
    {
	int offset = 0;
	double [] src = new double[8];
	double [] left = new double[8];
	double [] right = new double[8];
	ConceptMap.Position pleft = (ConceptMap.Position) vec.elementAt(offset);
	ConceptMap.Position pleftc = (ConceptMap.Position) vec.elementAt(offset+1);
	ConceptMap.Position prightc = (ConceptMap.Position) vec.elementAt(offset+2);
 	ConceptMap.Position pright = (ConceptMap.Position) vec.elementAt(offset+3);

	src[0] = pleft.x; src[1] = pleft.y;
	src[2] = pleftc.x; src[3] = pleftc.y;
	src[4] = prightc.x; src[5] = prightc.y;
	src[6] = pright.x; src[7] = pright.y;
	java.awt.geom.CubicCurve2D.subdivide(src,0, left, 0, right, 0);

	pleftc.x = (int) left[2]; pleftc.y = (int) left[3];
	pright.x = (int) right[4]; pright.y = (int) right[5];

	vec.insertElementAt(new ConceptMap.Position((int) right[2], (int) right[3]), offset+2);
	vec.insertElementAt(new ConceptMap.Position((int) left[6], (int) left[7]), offset+2);
	vec.insertElementAt(new ConceptMap.Position((int) left[4], (int) left[5]), offset+2);
    }
    
    protected Vector extractPoints(Edge edge)
    {
	//PathIterator pit = new FlatteningPathIterator(edge.getGrappaNexus().getPathIterator(),5.0);
	double [] darr = new double[6]; 
	PathIterator pit = edge.getGrappaNexus().getPathIterator();
	Vector vec = new Vector();
	int x,y;
	
	do {
	    int ret = pit.currentSegment(darr);
	    switch (ret) {
	    case PathIterator.SEG_CUBICTO:	
		Tracer.debug("SEG_CUBICTO");	
		x = (int) darr[0];
		y = - (int) darr[1];
		vec.add(new ConceptMap.Position(x, y));
		x = (int) darr[2];
		y = - (int) darr[3];
		vec.add(new ConceptMap.Position(x, y));
		x = (int) darr[4];
		y = - (int) darr[5];
		vec.add(new ConceptMap.Position(x, y));
		break;
	    case PathIterator.SEG_QUADTO:
		Tracer.debug("SEG_QUADTO");
		x = (int) darr[0];
		y = - (int) darr[1];
		vec.add(new ConceptMap.Position(x, y));
		x = (int) darr[2];
		y = - (int) darr[3];
		vec.add(new ConceptMap.Position(x, y));
		break;
	    case PathIterator.SEG_LINETO:
	    case PathIterator.SEG_MOVETO:
		Tracer.debug("SEG_LINETO or SEG_MOVETO");
		x = (int) darr[0];
		y = - (int) darr[1];
		vec.add(new ConceptMap.Position(x, y));
		break;
	    default:
		Tracer.debug("SEG_CLOSE shouldn't happen within loop");
		pit.next();
		continue;
	    }
	    
	    //	    vec.add(new ConceptMap.Position(x, y));
	    pit.next();
	} while  (!pit.isDone());
	
	//the points seem to be doubled.
	if (vec.size()>2)
	    {
		int ns = (vec.size()+1  )/2;
		while (vec.size() > ns)
		    vec.removeElementAt(vec.size()-1);
	    }			
	
	if (vec.size() < 2)
	    Tracer.bug("PathITerator returned edge with no valid segment!! edge "+ edge.getName());

	return vec;
    }

  public void layout(MapController controller)
  {
      ConceptMap cMap = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
      NeuronStyle [] nss = cMap.getNeuronStyles();

      Graph graph = new Graph("contextmap");
      //      graph.setAttribute("ratio", "compress");
      graph.setAttribute("margin", "0.5,0.5");
      graph.setAttribute("size", "7.5,10");
      graph.setAttribute("fontsize","12");
      
      //Inserting our neuron-graph into the grappa-graph representation
      //---------------------------------------------------------------
      // First create all nodes
      for (int i=0;i<nss.length;i++)
	  {
	      if (nss[i].getBodyVisible())
		  makeGrappaBox(nss[i], graph, getTitle(controller, nss[i]));
	      else
		  makeGrappaBox(nss[i], graph, null);
	  }

      //Now create all edges (needs that the ends of the edges, i.e. the
      //                      nodes, already exists).
      for (int i=0;i<nss.length;i++)    
	  makeGrappaLines(controller, nss[i], graph);

      //Calling dot for layout.
      //-----------------------
      try {
	  Process child = Runtime.getRuntime().exec("/usr/bin/dot");
	  boolean Q = GrappaSupport.filterGraph(graph, child);
	  child.destroy();
      } catch(java.io.IOException e) {
	  Tracer.bug("IOException starting process!\n"+e.getMessage());
      }

      //      MyFrame frame= new MyFrame(graph);
      


      //Extracting layout out of the graph representation.
      //--------------------------------------------------
      //
      //Extracts both boxes and lines, since everything exists
      //there is no need to do lines and boxes separate.
      for (int i=0;i<nss.length;i++)    
	  {
	      Tracer.debug("layout 5");
	      if (nss[i].getBodyVisible())
		  layoutBox(nss[i], graph);
	      else
		  {
		      Node node = graph.findNodeByName(nss[i].getID());
		      if (node == null)
			  return;
		      Rectangle bb = node.getGrappaNexus().getBounds();
		      Tracer.debug("hidden rectangle :"+bb.toString());
		  }
		      
	      NeuronMapObject nmo = controller.getMapScrollPane().getDisplayer().getNeuronMapObject(nss[i].getID());
	      AxonStyle [] ass = nss[i].getAxonStyles();
	      if (ass.length != 2)
		  for (int j=0; j<ass.length;j++)
		      layoutLine(nmo, nmo.getAxonMapObject(ass[j].getAxonID()), graph);
	      else
		  layoutLine(nmo, nmo.getAxonMapObject(ass[0].getAxonID()),
			     nmo.getAxonMapObject(ass[1].getAxonID()), graph);
	  }
      Rectangle rect = graph.getGrappaNexus().getBounds();
      cMap.setDimension(new ConceptMap.Dimension(rect.width, rect.height));
      resizeAllBoxes(controller);
  }
    
    public void resizeAllBoxes(MapController controller)
    {
	HandleStore store;

	//Default is true!!!
	TieTool tietool = new TieTool();
	
	if (controller.getManager() instanceof EditMapManager)
	    store = new HandleStore(((EditMapManager) controller.getManager()).getGridModel());
	else
	    store = new HandleStore(new GridModel(6));
	MapDisplayer displayer = controller.getMapScrollPane().getDisplayer();
	ConceptMap cMap = displayer.getStoreManager().getConceptMap();
	NeuronStyle [] nss = cMap.getNeuronStyles();
	
	for (int i=0; i<nss.length;i++)
	    {
		if (!nss[i].getBodyVisible())
		    continue;
		ConceptMap.BoundingBox bbold = nss[i].getBoundingBox();
		NeuronMapObject nmo = displayer.getNeuronMapObject(nss[i].getID());
		java.awt.Dimension dim=nmo.getPreferredSize();
		ConceptMap.BoundingBox bb = LayoutUtils.preferredBoxOnGrid(gridModel, 
									   bbold.pos.x,
									   bbold.pos.y,
									   dim);
		//Sets the right followers.
		MapEvent m = new MapEvent(null, MapEvent.HIT_BOX, bbold.pos.x+1,
					  bbold.pos.y+1, nmo, 0, displayer);
		HandledBox hbox =new HandledBox(m, tietool, store);
		NeuronBoxHandlesStruct nbhs = store.getNeuronBoxHandles(nss[i]);
		nbhs.tot.drag(bb.pos.x-bbold.pos.x, bb.pos.y-bbold.pos.y);
		nbhs.lr.drag(bb.dim.width-bbold.dim.width, bb.dim.height-bbold.dim.height);
	    }
	store.set();
    }


    class MyFrame extends JFrame {
    GrappaPanel gp;
	Graph graph = null;

	public MyFrame(Graph graph) {
		super("MyFrame"); //typename
		this.graph = graph;

		setSize(650, 350);
		setLocation(50, 50);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent wev) {
				Window w = wev.getWindow();
				w.setVisible(false);
				w.dispose();
				System.exit(0);
			}
		});

		JScrollPane jsp = new JScrollPane();
		jsp.getViewport().setBackingStoreEnabled(true);

		gp = new GrappaPanel(graph);
		//gp.addGrappaListener(new MyGrappaListener());
		gp.setScaleToFit(false);
		gp.multiplyScaleFactor(0.6);

		java.awt.Rectangle bbox = graph.getBoundingBox().getBounds();

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;

		getContentPane().add("Center", jsp);

		setVisible(true);

		jsp.setViewportView(gp);
	}
}


}



//to deal specifically with relations that only have two ends.
      /*    
	      if (isSimpleEdge(ns))
		  {
		      Double [] darr = new double[6]; 
		      Edge edge = graph.findEdgeByName(ns.getURI);
		      PathIterator pit = edge.getGrappaNexus().getPathIterator();
		      Vector vec = new Vector();

		      if (pit.isDone())
			  continue;
		      int ret = pit.currentSegment(darr);
		      if (ret != PathIterator.SEG_MOVETO)
			  continue;
		      vec.add(ConceptMap.Position(darr[0],darr[1]));
		      
		      while (!pit.isDone())
			  {
			      ret = pit.currentSegment(darr);
			      vec.add(ConceptMap.Position(darr[0],darr[1]));
			  }

		      //Fixes so that there is at least three points.
		      if (vec.size() == 2)
			  {
			      ConceptMap.Position po = (ConceptMap.Position) vec.lastElement();
			      vec.add(new ConceptMap.Position(po.x, po.y));
			  }
		      if (vec.size() < 2)
			  Tracer.bug("PathITerator returned edge with no valid segment!! for neuron "+ns.getURI());
		      
		      //SimpleEdge means that this array of Axonstyles is of size 2 exactly.
		      AxonStyle [] ass = ns.getAxonStyles();		      
		      AxonStyle as1,as2;
		      //Forward direction?
		      if (ass[0].getEnd().getURI().equals(edge.getHead().getName()))
			  {
			      ass1 = ass[0];
			      ass2 = ass[1];
			  }
		      else
			  {
			      ass1 = ass[1];
			      ass2 = ass[0];
			  }
		      ConceptMap.Position [] pa1 = new ConceptMap.Position[2];
		      ConceptMap.Position [] pa2 = new ConceptMap.Position[vec.size()-1];
		      pa1[0] = vec.elementAt(0);
		      pa1[1] = vec.elementAt(1);
		      ass1.setLine(pa1);
		      for (int i=1;i<vec.size();i++)
			  ps2[i-1]=vec.elementAt(i);
		      ass2.setLine(pa2);
		  }
				  
      */			      
			      
