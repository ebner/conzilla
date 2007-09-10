/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.bookmarkrdf;

/**
 * @author ioana
 */

import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.tool.ActionMapMenuTool;
import se.kth.cid.rdf.CV;
import se.kth.cid.util.Tracer;

import com.hp.hpl.jena.rdf.model.RDFException;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.SeqImpl;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

public class BookmarkConceptTool extends ActionMapMenuTool {

    BookMarkExtra bmextra;
    Concept concept;
    String kind;
    public static final String BOOKMARK_CONCEPT = "BOOKMARK_CONCEPT_ALONE";
    public static final String BOOKMARK_CONCEPT_IN_CONTEXT =
        "BOOKMARK_CONCEPT_IN_CONTEXT";
  //  MapToolsMenu tm;
    MapController mc;
    Resource bmfolder; // = new ResourceImpl();

    public BookmarkConceptTool(
        MapController mc,
        BookMarkExtra bmextra,
        String kind,
        Resource bmfolder //,
      //  MapToolsMenu tm) {
        ){
        super(kind, BookMarkExtra.class.getName(), mc);
        this.kind = kind;
        this.bmextra = bmextra;
 //       this.tm = tm;
        this.mc = mc;
        this.bmfolder = bmfolder;
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

   
        int result = 0;

        if (concept == null) {
            Tracer.debug("Concept was null");
        }

    //   if (this.kind.equalsIgnoreCase(BOOKMARK_CONCEPT)) {
        if(this.kind.startsWith(BOOKMARK_CONCEPT)){
        

            result =
                JOptionPane.showConfirmDialog(
                    controller.getView().getMapScrollPane().getDisplayer(),
                    "Do you want to bookmark this concept in itself?\n'"
                        + mapObject.getConcept().getURI()
                        + "\n\n"
                        + "Continue?",
                    "Concept:",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.NO_OPTION)
                return;

            addThisConceptasBookmark(mc, this.bmfolder); //bmextra.myBookmarksFolder);
            bmextra.bmTree.initTreeModel();
            bmextra.bmTree.revalidate();

            //       bmextra.initMenu(tm, mc);
            //bmextra.initExtra(ConzillaKit.getDefaultKit()); 
        } else //if (this.kind.equalsIgnoreCase(BOOKMARK_CONCEPT_IN_CONTEXT)) {
                  if(this.kind.startsWith(BOOKMARK_CONCEPT_IN_CONTEXT)){
                  

            result =
                JOptionPane.showConfirmDialog(
                    controller.getView().getMapScrollPane().getDisplayer(),
                    "Do you want to bookmark this concept in this context?\n'"
                        + mapObject.getConcept().getURI()
                        + "\n\n"
                        + "Continue?",
                    "Concept:",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.NO_OPTION)
                return;

            addThisConceptinContextasBookmark(mc, this.bmfolder); //bmextra.myBookmarksFolder);
            bmextra.bmTree.initTreeModel();
            bmextra.bmTree.revalidate();
            //       bmextra.initMenu(tm, mc);
            //bmextra.initExtra(ConzillaKit.getDefaultKit()); 
        }
    }
    
    public String toString (){
        String result = super.toString();
        return result +" in " + bmfolder.toString();
    }

    public void addThisConceptasBookmark(
        final MapController mc,
        Resource bookmarkfolder) {

        Tracer.debug("We are adding the current concept as a bookmark!");
        Resource reifresource;
        Resource conceptresource;
        String concepttitle = "";

        List atributes = concept.getAttributeEntry(CV.title.toString());
        if (atributes.isEmpty())
            concepttitle = "Missing title for concept bookmark";
        else
            concepttitle = ((AttributeEntry) atributes.get(0)).getValue();

        HashSet set = new HashSet();
        URI containerURI = URI.create(concept.getLoadContainer());
        set.add(containerURI.toString());
        ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
        try {
            Container loadContainer =
                store.getAndReferenceContainer(containerURI);
            set.addAll(
                loadContainer.getRequestedContainersForURI(concept.getURI()));
        } catch (ComponentException e1) {
            e1.printStackTrace();
        }

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
    }

    public void addThisConceptinContextasBookmark(
        final MapController mc,
        Resource bookmarkfolder) {

        Tracer.debug(
            "We are adding the current concept in this context as a bookmark!");
        Resource reifresource;
        Resource conceptresource;
        String concepttitle = "";

        List atributes = concept.getAttributeEntry(CV.title.toString());
        if (atributes.isEmpty())
            concepttitle = "Missing title for concept";
        else
            concepttitle = ((AttributeEntry) atributes.get(0)).getValue();

        HashSet set = new HashSet();
        HashSet setconcept = new HashSet();
        Resource cmresource;
        String contextmaptitle = "";
        URI containerURImap = URI.create(mc.getConceptMap().getLoadContainer());
        set.add(containerURImap.toString());
        URI containerURIconcept = URI.create(concept.getLoadContainer());
        setconcept.add(containerURIconcept.toString());

        ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
        try {
            Container loadContainer =
                store.getAndReferenceContainer(containerURImap);

            List atributesmap =
                mc.getConceptMap().getAttributeEntry(CV.title.toString());
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
            reifresource.addProperty(RDF.predicate, CV.includes);
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

}
