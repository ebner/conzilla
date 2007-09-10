/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JPopupMenu;

import se.kth.cid.concept.Triple;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.conzilla.edit.layers.LayerManager;
import se.kth.cid.conzilla.map.MapDisplayer;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapEventListener;
import se.kth.cid.conzilla.map.graphics.ConceptMapObject;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.util.Tracer;

public class TripleEdit implements  MapEventListener
{

  protected MapController controller;
  protected LayerManager  edit;
  protected MapEvent mapEvent;
  protected MapEvent secondMapEvent;
  protected Hashtable nmoToTriples;
  protected boolean createMode;
  protected JPopupMenu menu;

  public TripleEdit(MapController controller, LayerManager edit)
  {
    this.controller=controller;
    this.edit=edit;
    
    createMode = false;
  }

  public void createTriple(MapEvent me)
    {
      createMode = true;
      activate(me);	
    }
    
  public void showTriple(MapEvent me)
    {
	createMode = false;
	activate(me);
	//	highlightChoices();
    }
  
    void activate(MapEvent me)
    {
	if (me == null)
	    Tracer.bug("Should never activate TripleEdit if not updated sucessfully"+
		       "before from an MapEvent");
	mapEvent = me;
	edit.uninstall(controller.getView().getMapScrollPane());
	MapDisplayer mapDisplayer=controller.getView().getMapScrollPane().getDisplayer();
	mapDisplayer.addMapEventListener(this,MapDisplayer.CLICK);
    }
    
    void deactivate()
    {
	Tracer.debug("deactivate!!!!!!!!!!!!!!!!!!!!!!!");
	MapDisplayer mapDisplayer=controller.getView().getMapScrollPane().getDisplayer();
	mapDisplayer.removeMapEventListener(this,MapDisplayer.CLICK);
	
    }
  
  

    //triples have only two ends nowadays....    
    /*
  protected void highlightChoices()
    {
	nmoToTriples=getNotConnectedToDrawerLayout(mapEvent.mapObject.getConcept(), 
						mapEvent.mapObject.getDrawerLayout(), 
						controller.getMapScrollPane().getDisplayer());
	Enumeration en=nmoToTriples.keys();
	for (;en.hasMoreElements();)
	    ((ConceptMapObject) en.nextElement()).pushMark(new Mark(EditMapManagerFactory.COLOR_CHOICES, null, null), this);
    }

  public static Hashtable getNotConnectedToDrawerLayout(Concept ne, DrawerLayout ns, MapDisplayer mapDisplayer)
    {
	Hashtable nmoToTriples=new Hashtable();
	
	String baseuri1=ne.getURI();
	DrawerLayout [] nss=mapDisplayer.getStoreManager().getConceptMap().getDrawerLayouts();
	Triple [] as=ne.getTriples();
	for (int i=0; i<as.length;i++)
	  if (ns.getStatementLayout(as[i].getURI()) == null) 
	    {
		URI uri1=URIClassifier.parseValidURI(as[i].objectURI(),baseuri1);
			
		for (int j=0;j<nss.length;j++)
		    {
			String baseuri2 =  nss[j].getConceptMap().getURI();
			URI uri2=URIClassifier.parseValidURI(nss[j].getConceptURI(), baseuri2);
			if (uri2.equals(uri1))
			    {
				ConceptMapObject nmo=mapDisplayer.getConceptMapObject(nss[j].getURI());
				if (nmoToTriples.get(nmo) == null)
				    {
					Vector nv=new Vector();
					nv.addElement(as[i]);
					nmoToTriples.put(nmo,nv);
				    }
				else
				    {
					Vector nv = (Vector) nmoToTriples.get(nmo);
					nv.addElement(as[i]);
				    }
			    }
		    }
	    }
	return nmoToTriples;
	}*/
 protected void highlightNone()
    {
	Enumeration en=nmoToTriples.keys();
	for (;en.hasMoreElements();)
	    ((ConceptMapObject) en.nextElement()).popMark(this);
    }

 public void eventTriggered(MapEvent m)
  {
      if (m.hitType == MapEvent.HIT_NONE)
	  {
	      done();
	      return;
	  }
      secondMapEvent=m;
      
      if (createMode)
	  create();
      else
	  show();
  }

  public void done()
      {
	  if (!createMode)
	      highlightNone();
	  deactivate();
	  edit.install(controller.getView().getMapScrollPane());
      }

  protected void create()
    {
	Tracer.debug("inside create...............1");
	if (secondMapEvent.mapObject.getConcept()==null)
	    return;
	Tracer.debug("inside create...............2");
	menu=new JPopupMenu("tripletypes");

	//FIXME: triples doesn't work this way yet.
	/*	TripleType [] at = mapEvent.mapObject.getConceptType().getTripleTypes();
	if (at.length==1)
	    createTripleWithType(at[0].getType());
	else
	    {
		for (int i=0; i<at.length; i++)
		    {
			AbstractAction aa=new AbstractAction(at[i].getType()) {
				public void actionPerformed(ActionEvent e)
				{
				    createTripleWithType((String) getValue("type"));
				}
			    };
			aa.putValue("type", at[i].getType());
			Tracer.debug("inside create...............2.5");
			menu.add(aa);
		    }
		Tracer.debug("inside create...............3");

		menu.show(controller.getMapScrollPane().getDisplayer(), 
			  secondMapEvent.mouseEvent.getX(),
			  secondMapEvent.mouseEvent.getY());
	    }
	*/
    }

  public void createTripleWithType(String type)
    {
	//Redo and think think think
	/*	try {

	URI base = URIClassifier.parseValidURI(mapEvent.mapObject.getConcept().getURI());
	URI absoluteURI=URIClassifier.parseValidURI(secondMapEvent.mapObject.getConcept().getURI());
	String relativeURI;
	try {
	    relativeURI=base.makeRelative(absoluteURI, false);
	} catch (MalformedURIException me)
	    {
		relativeURI=secondMapEvent.mapObject.getConcept().getURI();
	    }
	Triple triple=mapEvent.mapObject.getConcept().setTriple(type, relativeURI);
	addStatementLayout(triple);
	} catch (ConceptException ne){
	} catch (InvalidURIException ie) {}
	*/
    }
			    
  protected void show()
    {
	/*
      Vector nv=(Vector) nmoToTriples.get(secondMapEvent.mapObject);
      if (nv != null)
	  {
	      if (nv.size()== 1)
		  addStatementLayout((Triple) nv.elementAt(0));
	      else
		  {
		      menu=new JPopupMenu("triples");
		      Enumeration en= nv.elements();
		      for (;en.hasMoreElements();)
			  {
			      Triple ax=(Triple) en.nextElement();
			      String name=ax.getURI()+":"+ax.predicateURI();
			      AbstractAction aa=new AbstractAction(name) {
				  public void actionPerformed(ActionEvent e)
				      {
					  addStatementLayout((Triple) getValue("triple"));
				      }
			      };
			      aa.putValue("triple", ax);
			      menu.add(aa);
			  }
		      menu.show(controller.getMapScrollPane().getDisplayer(), 
				secondMapEvent.mouseEvent.getX(),
				secondMapEvent.mouseEvent.getY());

		  }
	  }
      else
	  done();
	*/
  }

  protected void addStatementLayout(Triple triple)
    {
	addStatementLayout(triple, mapEvent.mapObject.getDrawerLayout(), secondMapEvent.mapObject.getDrawerLayout(), 
		     new ContextMap.Position(secondMapEvent.mapX, secondMapEvent.mapY), ((EditMapManager) controller.getManager()).getGridModel());
    }

  public static void addStatementLayout(Triple triple, DrawerLayout subject, DrawerLayout object, ContextMap.Position click, GridModel gm)
    {		 
	/*
	try {
	    ConceptMap.Position[] pos=new ConceptMap.Position[2];
	    pos=LayoutUtils.tripleLine(subject, object, click, gm);
	    
	    //juck! this doens't work.... rethink.
	    StatementLayout tripleLayout=subject.getConceptMap().addStatementLayout(triple.getURI(), object);
	    tripleLayout.setLine(pos);
	    
	    //	    done();
	} catch (ConceptMapException ce)
	    {
		Tracer.bug("Can't show one of the selected triples.....");
	    }
	*/
    }

  protected void detachImpl()
  {
    controller=null;
    mapEvent = null;
    secondMapEvent = null;
  }
}
