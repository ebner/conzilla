/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.bookmarkrdf;

/**
 * @author ioana
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.tool.ActionMapMenuTool;
import se.kth.cid.util.Tracer;

import com.hp.hpl.jena.rdf.model.Resource;

public class ContentTool extends ActionMapMenuTool{

    BookMarkExtra bmextra;
    Concept concept;
    String kind;
    public static final String CREATE_CONTENT = "CREATE_CONTENT";
    public static final String CREATE_CONTENT_IN_CONTEXT = "CREATE_CONTENT_IN_CONTEXT";
  //  MapToolsMenu tm;
    MapController mc;
    JFrame bmFrame;
    FormItemPane shame;

    public ContentTool(
        MapController mc,
        BookMarkExtra bmextra,
        String kind //,
      //  MapToolsMenu tm) {
        ){
        super(kind, BookMarkExtra.class.getName(), mc);
        this.kind = kind;
        this.bmextra = bmextra;
 //       this.tm = tm;
        this.mc = mc;
    }

    protected boolean updateEnabled() {
        Concept c;
        if (mapEvent.hitType != MapEvent.HIT_NONE) {
            if ((c = mapEvent.mapObject.getConcept()) != null) {
             // if((c=mapObject.getConcept())!= null){              
                this.concept = c;
                Tracer.debug("Concept is:" + concept.toString());
                return true;
            } else
                return false;
        } else
            return false;
    }

    /** This command results in a set of content is displayed.
     *  Observe that update has to have been succesfull last time called.
     *  Otherwise the view-action isn't activated and this function isn't called.
     *
     *  @see Controller.selectContent()
     */ /** This function highlight the current mapevent......
        */

    public void actionPerformed(ActionEvent e) {

//        Resource bmfolder;
//        int result = 0;

        if (concept == null) {
            Tracer.debug("Concept was null");
        }

        if (this.kind.equalsIgnoreCase(CREATE_CONTENT)) {

            createcontentonconcept(mc, bmextra.myBookmarksFolder);
            bmextra.bmTree.initTreeModel();
            bmextra.bmTree.revalidate();

            //       bmextra.initMenu(tm, mc);
            //bmextra.initExtra(ConzillaKit.getDefaultKit()); 
        } else if (this.kind.equalsIgnoreCase(CREATE_CONTENT_IN_CONTEXT)) {

          

          //  addThisConceptinContextasBookmark(mc, bmextra.myBookmarksFolder);
            bmextra.bmTree.initTreeModel();
            bmextra.bmTree.revalidate();
            //       bmextra.initMenu(tm, mc);
            //bmextra.initExtra(ConzillaKit.getDefaultKit()); 
        }
    }
    
    public void  createcontentonconcept(
        final MapController mc,
        Resource bookmarkfolder) {

//        Resource reifresource;
//        Resource conceptresource;
//        String concepttitle = "";

//        List atributes = concept.getAttributeEntry(CV.title.toString());
        
//        if (atributes.isEmpty())
//            concepttitle = "Missing title for concept";
//        else
//            concepttitle = ((AttributeEntry) atributes.get(0)).getValue();

        Iterator itcontainersconcept= concept.getComponentManager().getLoadedRelevantContainers().iterator();
        if(itcontainersconcept.hasNext()){
        
     //   RDFModel ccontainer =(RDFModel) itcontainersconcept.next();
        
   //     Tracer.debug("This is the URI of the RDFModel where the concept is: " + ccontainer.getURI());
        }
        else 
        Tracer.debug("No relevant containers for concept");
        
        bmFrame = new JFrame();
                bmFrame.setSize(new Dimension(600, 400));
                bmFrame.setLocation(0, 0);
                bmFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                bmFrame.setTitle("Create new content");
                shame = new FormItemPane();
                JComboBox predicate = new JComboBox();   
                predicate.addItem("annotea:annotates");     
                JSplitPane split =
                    new JSplitPane(JSplitPane.VERTICAL_SPLIT, predicate, shame);
                split.setDividerLocation(50);
                bmFrame.setContentPane(split);
                WindowAdapter wa = new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                       // saveExtra();
                    }
                };
                bmFrame.addWindowListener(wa);
                bmFrame.setVisible(true);
                
            String suggestedcontentURI = "";
            String newval = (String) JOptionPane.showInputDialog((java.awt.Component) mapEvent.mouseEvent.getSource(), "Enter URI for content",
                                    "New Content",
                                    JOptionPane.QUESTION_MESSAGE,
                                    null, null, suggestedcontentURI);
            Tracer.debug("Newval was:" + newval);
            
            /*
     
            if(newval != null)
              {lastval = newval;

                ConceptMap cmap=controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
                URI base = URIClassifier.parseValidURI(cmap.getURI());

                URI nuri=URIClassifier.parseURI(newval, base);
        
                Concept concept=controller.getConzillaKit().getComponentStore().getAndReferenceConcept(nuri);
                //      ConceptLayout ns=makeConceptLayout(concept);

                ConceptLayout ns = cmap.addConceptLayout(nuri.toString());
             */


//        HashSet set = new HashSet();
        URI containerURI = URI.create(concept.getLoadContainer());
            
//        set.add(containerURI.toString());
        ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
        try {
//            Container loadContainer =
            	store.getAndReferenceContainer(containerURI);
  //          set.addAll(loadContainer.getRequestedContainersForURI(concept.getURI()));
        } catch (ComponentException e1) {
            e1.printStackTrace();
        }
        /*
        try {
            reifresource = bmextra.bookMarkModel.createResource();
            SeqImpl bookmarkfolderseq =
                new SeqImpl(bookmarkfolder, bookmarkfolder.getModel());
            int seqsize = bookmarkfolderseq.size();
            bookmarkfolderseq.add(seqsize + 1, reifresource);

            conceptresource =
                bmextra.bookMarkModel.createResource(concept.getURI());

            reifresource.addProperty(DC.title, concepttitle);
            reifresource.addProperty(RDF.type, RDF.Statement);
            reifresource.addProperty(RDF.type, CV.ConceptBookmark);
            reifresource.addProperty(RDF.subject, conceptresource);
            reifresource.addProperty(RDF.predicate, RDF.type);
            reifresource.addProperty(RDF.object, CV.Concept);

            Iterator setIt = set.iterator();
            while (setIt.hasNext()) {
                bmextra.bookMarkModel.addRequestedContainerForURI(
                    concept.getURI(),
                    (String) setIt.next());
            }

        } catch (RDFException e) {

            e.printStackTrace();
        }
        bmextra.saveExtra();
        */
    }

/*
    public void addThisConceptinContextasBookmark(
        final MapController mc,
        Resource bookmarkfolder) {

        Tracer.debug(
            "We are adding the current concept in this context as a bookmark!");
        Resource reifresource;
        Resource conceptresource;
        String concepttitle = "";

        List atributes = concept.getAttributeValues(CV.title.toString());
        if (atributes.isEmpty())
            concepttitle = "Missing title for concept";
        else
            concepttitle = ((AttributeEntry) atributes.get(0)).getValue();

        HashSet set = new HashSet();
        HashSet setconcept = new HashSet();
        Resource cmresource;
        String contextmaptitle = "";
        URI containerURImap =
            URIClassifier.parseValidURI(mc.getConceptMap().getLoadContainer());
        set.add(containerURImap.toString());

        URI containerURIconcept =
            URIClassifier.parseValidURI(concept.getLoadContainer());
        setconcept.add(containerURIconcept.toString());

        ComponentStore store = mc.getConzillaKit().getComponentStore();
        try {
            Container loadContainer =
                store.getAndReferenceContainer(containerURImap);

            List atributesmap =
                mc.getConceptMap().getAttributeValues(CV.title.toString());
            if (atributesmap.isEmpty())
                contextmaptitle = "Missing title for context map";
            else
                contextmaptitle =
                    ((AttributeEntry) atributesmap.get(0)).getValue();
            set.addAll(
                loadContainer.getRequestedContainersForURI(
                    mc.getConceptMap().getURI()));

            Container loadContainerconcept =
                store.getAndReferenceContainer(containerURIconcept);
            setconcept.addAll(
                loadContainerconcept.getRequestedContainersForURI(
                    concept.getURI()));
        } catch (ComponentException e1) {
            e1.printStackTrace();
        }

        try {
            reifresource = bmextra.bookMarkModel.createResource();
            SeqImpl bookmarkfolderseq =
                new SeqImpl(bookmarkfolder, bookmarkfolder.getModel());
            int seqsize = bookmarkfolderseq.size();

            bookmarkfolderseq.add(seqsize + 1, reifresource);
            cmresource =
                bmextra.bookMarkModel.createResource(
                    mc.getConceptMap().getURI());

            conceptresource =
                bmextra.bookMarkModel.createResource(concept.getURI());

            reifresource.addProperty(
                DC.title,
                concepttitle + " in " + contextmaptitle);
            reifresource.addProperty(RDF.type, RDF.Statement);
            reifresource.addProperty(RDF.type, CV.ConceptInContextBookmark);
            reifresource.addProperty(RDF.subject, cmresource);
            reifresource.addProperty(RDF.predicate, CV.displays);
            reifresource.addProperty(RDF.object, conceptresource);

            Iterator setIt = set.iterator();
            while (setIt.hasNext()) {
                bmextra.bookMarkModel.addRequestedContainerForURI(
                    mc.getConceptMap().getURI(),
                    (String) setIt.next());
            }
            Iterator setItconcept = setconcept.iterator();
            while (setItconcept.hasNext()) {
                bmextra.bookMarkModel.addRequestedContainerForURI(
                    concept.getURI(),
                    (String) setItconcept.next());
            }

        } catch (RDFException e) {

            e.printStackTrace();
        }
        bmextra.saveExtra();
    }
    */
}
