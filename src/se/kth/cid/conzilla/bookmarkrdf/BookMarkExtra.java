/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.bookmarkrdf;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.app.Extra;
import se.kth.cid.conzilla.browse.BrowseMapManagerFactory;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.map.graphics.Mark;
import se.kth.cid.conzilla.metadata.InfoPanel;
import se.kth.cid.conzilla.tool.MapToolsMenu;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.identity.MIMEType;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.rdf.CV;
import se.kth.cid.rdf.RDFModel;
import se.kth.cid.util.Tracer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFException;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.impl.SeqImpl;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author ioana
 */
public class BookMarkExtra implements Extra {

    public static final String BOOKMARKS = "BOOKMARKS";
    RDFModel bookMarkModel;
    Resource myBookmarksFolder;
    JFrame bmFrame;
    BookmarksTree bmTree;
    FormItemPane shame;
    public static final String BOOKMARK_CONCEPT = "BOOKMARK_CONCEPT_ALONE";
    public static final String BOOKMARK_CONCEPT_IN_CONTEXT =
        "BOOKMARK_CONCEPT_IN_CONTEXT";

    //the first bookmarkenvironment - it is an RDF:Seq for bookmarks

    /**
     * @see se.kth.cid.conzilla.app.Extra#getName()
     */
    public String getName() {
        return "Bookmark Extra";
    }

    /**
     * @see se.kth.cid.conzilla.app.Extra#initExtra(se.kth.cid.conzilla.app.ConzillaKit)
     */
    public boolean initExtra(ConzillaKit kit) {
        if (kit.getMenuFactory() == null) {
            return false;
        }
        
        URI uri = null;
		try {
			uri = new URI("urn:path:/org/conzilla/ont/defaultbookmarks.rdf");
		} catch (URISyntaxException e2) {
			e2.printStackTrace();
		}
		
        try {
            bookMarkModel =
                (RDFModel) kit.getResourceStore().getAndReferenceContainer(
                    uri);
            if (bookMarkModel != null) {
                Tracer.debug(
                    "The 'defaultbookmarks.rdf' bookMarkModel already exists.");
                myBookmarksFolder =
                    new SeqImpl(
                        "http://www.conzilla.org/local/initialbookmarkfolder",
                        bookMarkModel);
                myBookmarksFolder.addProperty(RDF.type, CV.BookmarkFolder);
                myBookmarksFolder.addProperty(RDF.type, RDF.Seq);
                myBookmarksFolder.addProperty(DC.title, "MyBookmarkFolder");
            }

        } catch (ComponentException e) {
            //If the Bookmark store file is missing, we try to create it.
            try {
                Tracer.debug(
                    "The bookMarkModel does not exist; trying to create it.");
                Object[] objs = kit.getResourceStore().checkCreateContainer(uri);
                bookMarkModel =
                    (RDFModel) kit.getResourceStore().createContainer(
                        uri,
                        (URI) objs[0],
                        (MIMEType) objs[1]);
                myBookmarksFolder =
                    new SeqImpl(
                        "http://www.conzilla.org/local/initialbookmarkfolder",
                        bookMarkModel);
                myBookmarksFolder.addProperty(RDF.type, CV.BookmarkFolder);
                myBookmarksFolder.addProperty(RDF.type, RDF.Seq);
                myBookmarksFolder.addProperty(DC.title, "MyBookmarkFolder");
            } catch (ComponentException e1) {
                e1.printStackTrace();
                return false;
            }
        }
        bookMarkModel.setPurpose(BOOKMARKS);

        bmFrame = new JFrame();
        bmFrame.setSize(new Dimension(600, 400));
        bmFrame.setLocation(0, 0);
        bmFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        bmFrame.setTitle("Bookmarks");
        shame = new FormItemPane();
        bmTree = new BookmarksTree(bookMarkModel, myBookmarksFolder, shame);
        JSplitPane split =
            new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, bmTree, shame);
        split.setDividerLocation(200);
        bmFrame.setContentPane(split);
        WindowAdapter wa = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveExtra();
            }
        };
        bmFrame.addWindowListener(wa);

        kit.getMenuFactory().addExtraMenu(
            BOOKMARKS,
            BookMarkExtra.class.getName(),
            60);
        return true;
    }

    /**
     * @see se.kth.cid.conzilla.app.Extra#extendMenu(se.kth.cid.conzilla.tool.ToolsMenu, se.kth.cid.conzilla.controller.MapController)
     */
    public void extendMenu(final ToolsMenu tm, final MapController mc) {
        if (tm.getName().equals(BOOKMARKS)) {
            tm.addMenuListener(new MenuListener() {
                public void menuCanceled(MenuEvent arg0) {
                }
                public void menuDeselected(MenuEvent arg0) {
                }
                public void menuSelected(MenuEvent arg0) {
                    initMenu(tm, mc);
                }
            });
        } else {
            if (tm.getName().equals(BrowseMapManagerFactory.BROWSE_MENU)) {

                ((MapToolsMenu) tm).addMapMenuItem(
                    conceptfoldersJmenu(mc),
                    600);
                ((MapToolsMenu) tm).addMapMenuItem(
                    conceptincontextfoldersJmenu(mc),
                    700);          
            }
        }
    }

    private void initMenu(ToolsMenu tm, MapController mc) {
        tm.removeAllTools();
        //the next one is for removing the separator that was added on every new bookmark added
        tm.removeAll();

        tm.addTool(getAddBookmark(tm, mc), 100);
        tm.addTool(getManageBM(tm, mc), 200);
        tm.addSeparator(500);
        tm.addToolsMenu(foldersJmenu(mc), 300);
        surfingOnBookmarks(mc, tm);
    }

    private ToolsMenu foldersJmenu(MapController mc) {
        ToolsMenu firstmenu = new ToolsMenu("File ContextMap-Bookmark");

        if (hasSubFolders(myBookmarksFolder)) {
            Iterator it = returnSubFolders(myBookmarksFolder);
            while (it.hasNext()) {
                final Resource childfolder = (Resource) it.next();
                addChildToJmenu(firstmenu, childfolder, mc);
            }
        }
        return firstmenu;
    }

    private MapToolsMenu conceptfoldersJmenu(MapController mc) {
        MapToolsMenu firstmenu =
            new MapToolsMenu(
                "File Concept-Bookmark",
                BookMarkExtra.class.getName(),
                mc);
                
                
        String title = "";
        NodeIterator ni =
            myBookmarksFolder.getModel().listObjectsOfProperty(
                myBookmarksFolder,
                DC.title);
        RDFNode nifirst = ni.nextNode();
        title = nifirst.toString();

        firstmenu.addMapMenuItem(
            new BookmarkConceptTool(
                mc,
                this,
                BOOKMARK_CONCEPT + " in " + title,
                myBookmarksFolder),
            50);

        Iterator it = returnSubFolders(myBookmarksFolder);
        int i = 0;
        while (it.hasNext()) {
            i += 50;
            final Resource childfolder = (Resource) it.next();
            addconceptChildToJmenu(
                firstmenu,
                childfolder,
                mc,
                BOOKMARK_CONCEPT);
        }
      //  addconceptChildToJmenu(firstmenu, myBookmarksFolder, mc, BOOKMARK_CONCEPT);
        return firstmenu;
    }

    private MapToolsMenu conceptincontextfoldersJmenu(MapController mc) {
        MapToolsMenu firstmenu =
            new MapToolsMenu(
                "File ConceptinContext-Bookmark",
                BookMarkExtra.class.getName(),
                mc);
                
        String title = "";
               NodeIterator ni =
                   myBookmarksFolder.getModel().listObjectsOfProperty(
                       myBookmarksFolder,
                       DC.title);
               RDFNode nifirst = ni.nextNode();
               title = nifirst.toString();

               firstmenu.addMapMenuItem(
                   new BookmarkConceptTool(
                       mc,
                       this,
                       BOOKMARK_CONCEPT_IN_CONTEXT + " in " + title,
                       myBookmarksFolder),
                   50);

               Iterator it = returnSubFolders(myBookmarksFolder);
               int i = 0;
               while (it.hasNext()) {
                   i += 50;
                   final Resource childfolder = (Resource) it.next();
                   addconceptChildToJmenu(
                       firstmenu,
                       childfolder,
                       mc,
                       BOOKMARK_CONCEPT_IN_CONTEXT);
               }
       // addconceptChildToJmenu(firstmenu, myBookmarksFolder, mc, BOOKMARK_CONCEPT_IN_CONTEXT);
        return firstmenu;
    }

    public void addconceptChildToJmenu(
        MapToolsMenu menu,
        final Resource bmfolder,
        final MapController mc,
        final String kind) {

        String title = "";
        NodeIterator ni =
            bmfolder.getModel().listObjectsOfProperty(bmfolder, DC.title);
        RDFNode nifirst = ni.nextNode();
        title = nifirst.toString();

        if (!hasSubFolders(bmfolder)) {
            menu.addMapMenuItem(
                new BookmarkConceptTool(
                    mc,
                    this,
                    kind + " in " + title,
                    bmfolder),
                50);
        } else {
            MapToolsMenu childmenu =
                new MapToolsMenu(title, BookMarkExtra.class.getName(), mc);
            childmenu.addMapMenuItem(
                new BookmarkConceptTool(mc, this, kind, bmfolder),
                50);
            Iterator it = returnSubFolders(bmfolder);
            int i = 0;
            while (it.hasNext()) {
                i += 50;
                final Resource childfolder = (Resource) it.next();
                addconceptChildToJmenu(childmenu, childfolder, mc, kind);
            }
            menu.addMapMenuItem(childmenu, 50 + i);
        }
    }

    public void addChildToJmenu(
        ToolsMenu menu,
        final Resource bmfolder,
        final MapController mc) {

        String title = "";
        NodeIterator ni =
            bmfolder.getModel().listObjectsOfProperty(bmfolder, DC.title);
        RDFNode nifirst = ni.nextNode();
        title = nifirst.toString();

        if (!hasSubFolders(bmfolder)) {
            menu.add(new AbstractAction("Add to" + title) {
                public void actionPerformed(ActionEvent arg0) {
                    addThisCMasBookmark(mc, bmfolder);
                    bmTree.initTreeModel();
                    bmTree.revalidate();

                }
            });
        } else {
            ToolsMenu childmenu = new ToolsMenu(title);
            childmenu.add(new AbstractAction("Add to " + title) {
                public void actionPerformed(ActionEvent arg0) {
                    addThisCMasBookmark(mc, bmfolder);
                    bmTree.initTreeModel();
                    bmTree.revalidate();
                }
            });

            Iterator it = returnSubFolders(bmfolder);
            while (it.hasNext()) {
                final Resource childfolder = (Resource) it.next();
                addChildToJmenu(childmenu, childfolder, mc);
            }
            menu.add(childmenu);
        }
    }

    public Iterator returnSubFolders(Resource bmfolder) {
        List lista = new LinkedList();
        if (!bmfolder.hasProperty(RDF.type, RDF.Seq))
            return null; //Ioana, this will crash Conzilla with a nullpointer, better throw a runtimeException...
        else {
            SeqImpl bmfolderseq = new SeqImpl(bmfolder, bmfolder.getModel());
            try {
                NodeIterator contained = bmfolderseq.iterator();
                while (contained.hasNext()) {
                    RDFNode node = (RDFNode) contained.next();
                    if (node instanceof Resource) {
                        if (((Resource) node)
                            .hasProperty(RDF.type, CV.BookmarkFolder)) {
                            lista.add((Resource) node);
                        }
                    }
                }
            } catch (RDFException e) {
                e.printStackTrace();
            }
        }
        return lista.iterator();
    }

    public boolean hasSubFolders(Resource bmfolder) {
        if (returnSubFolders(bmfolder) == null)
            return false;
        return true;
    }

    private Tool getManageBM(final ToolsMenu tm, final MapController mc) {

        Tool t =
            new Tool("EDIT_CREATED_BOOKMARKS", BookMarkExtra.class.getName()) {
            public void actionPerformed(ActionEvent ae) {
                bmFrame.setVisible(true);
            }
        };
        t.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK));
        return t;
    }

    //adding the current contextmap as bookmark in the InitialBookmarkFolder
    private Tool getAddBookmark(final ToolsMenu tm, final MapController mc) {

        Tool t = new Tool("ADD_BOOKMARK", BookMarkExtra.class.getName()) {
            public void actionPerformed(ActionEvent ae) {
                    //       TreePath path = bmTree.getEditingPath();
    addThisCMasBookmark(mc, myBookmarksFolder);
                bmTree.initTreeModel();

                //     bmTree.expandPath(path);
                //   bmTree.setSelectionPath(path);
                bmTree.revalidate();
                initMenu(tm, mc);
            }
        };
        t.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK));
        return t;
    }

    public void addThisCMasBookmark(
        final MapController mc,
        Resource bookmarkfolder) {

        Tracer.debug("We are adding the current contextmap as a bookmark!");
        HashSet set = new HashSet();
        Resource cmresource;
        Resource reifresource;
        String contextmaptitle = "";
        URI containerURI = null;
		try {
			containerURI = new URI(mc.getConceptMap().getLoadContainer());
		} catch (URISyntaxException e2) {
			e2.printStackTrace();
		}
        set.add(containerURI.toString());
        ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
        try {
            Container loadContainer =
                store.getAndReferenceContainer(containerURI);

            List atributes =
                mc.getConceptMap().getAttributeEntry(CV.title.toString());
            if (atributes.isEmpty())
                contextmaptitle = "Missing title for context map bookmark";
            else
                contextmaptitle =
                    ((AttributeEntry) atributes.get(0)).getValue();
            set.addAll(
                loadContainer.getRequestedContainersForURI(
                    mc.getConceptMap().getURI()));
        } catch (ComponentException e1) {
            e1.printStackTrace();
        }

        try {
            reifresource = bookMarkModel.createResource();
            SeqImpl bookmarkfolderseq =
                new SeqImpl(bookmarkfolder, bookmarkfolder.getModel());
            int seqsize = bookmarkfolderseq.size();

            bookmarkfolderseq.add(seqsize + 1, reifresource);
            cmresource =
                bookMarkModel.createResource(mc.getConceptMap().getURI());
            reifresource.addProperty(DC.title, contextmaptitle);
            reifresource.addProperty(RDF.type, RDF.Statement);
            reifresource.addProperty(RDF.type, CV.ContextMapBookmark);
            reifresource.addProperty(RDF.subject, cmresource);
            reifresource.addProperty(RDF.predicate, RDF.type);
            reifresource.addProperty(RDF.object, CV.ContextMap);

            Iterator setIt = set.iterator();
            while (setIt.hasNext()) {
                bookMarkModel.addRequestedContainerForURI(
                    mc.getConceptMap().getURI(),
                    (String) setIt.next());
            }

        } catch (RDFException e) {

            e.printStackTrace();
        }
        saveExtra();
    }

    private void surfingOnBookmarks(MapController mc, ToolsMenu tm) {
        addBookmarks(tm, mc, myBookmarksFolder);
        if (hasSubFolders(myBookmarksFolder)) {
            Iterator it = returnSubFolders(myBookmarksFolder);
            while (it.hasNext()) {
                final Resource childfolder = (Resource) it.next();
                addChildTosurfingJmenu(tm, childfolder, mc);
            }
        }
    }

    public void addChildTosurfingJmenu(
        final ToolsMenu menu,
        final Resource bmfolder,
        final MapController mc) {

        RDFModel m = (RDFModel) bmfolder.getModel();
        String title = "";
        NodeIterator ni = m.listObjectsOfProperty(bmfolder, DC.title);
        RDFNode nifirst = ni.nextNode();
        title = nifirst.toString();
        ToolsMenu childmenu = new ToolsMenu(title);

        if (!hasSubFolders(bmfolder))
            addBookmarks(childmenu, mc, bmfolder);

        else {
            addBookmarks(childmenu, mc, bmfolder);
            Iterator it = returnSubFolders(bmfolder);
            while (it.hasNext()) {
                final Resource childfolder = (Resource) it.next();
                addChildTosurfingJmenu(childmenu, childfolder, mc);
            }
        }
        menu.add(childmenu);
    }

    private void loadMap(final MapController mc, final Resource cmres) {
        Tracer.debug("We chose/selected an existing bookmarked contextmap!");
        ConzillaKit kit = ConzillaKit.getDefaultKit();

        //For every required container, load it.
        Collection containersToLoad =
            bookMarkModel.getRequestedContainersForURI(cmres.getURI());
        for (Iterator ctl = containersToLoad.iterator(); ctl.hasNext();) {
            String ctlURI = (String) ctl.next();
            try {
                kit.getResourceStore().getAndReferenceContainer(new URI(ctlURI));
            } catch (ComponentException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        //Then open the map.
        try {
            ContextMap oldMap = mc.getConceptMap();
            URI newMap = new URI(cmres.getURI());
            mc.showMap(newMap);
            mc.getHistoryManager().fireOpenNewMapEvent(mc, oldMap, newMap);
        } catch (ControllerException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    private void addBookmarks(
        ToolsMenu tm,
        final MapController mc,
        Resource bmfolder) {
        String cmtitle = "";
        for (Iterator iter =
            getconceptmapbookmarks(bookMarkModel, bmfolder).iterator();
            iter.hasNext();
            ) {
            final Resource bmMap = (Resource) iter.next();
            final Resource cmres =
                ((Resource) bmMap).getProperty(RDF.subject).getResource();
            NodeIterator ni =
                bmMap.getModel().listObjectsOfProperty(bmMap, DC.title);
            RDFNode nifirst = ni.nextNode();
            cmtitle = nifirst.toString();
            //the URI returned is just an identifier for the map, not it's real location (bmMap.getURI())
            tm.add(new Tool("Context Map: " + cmtitle) {
                public void actionPerformed(ActionEvent ae) {
                    loadMap(mc, cmres);
                }

            });
        }
        for (Iterator iter =
            getconceptalonebookmarks(bookMarkModel, bmfolder).iterator();
            iter.hasNext();
            ) {
            final Resource bmConcept = (Resource) iter.next();
            final Resource conceptres =
                ((Resource) bmConcept).getProperty(RDF.subject).getResource();
            NodeIterator ni =
                bmConcept.getModel().listObjectsOfProperty(bmConcept, DC.title);
            RDFNode nifirst = ni.nextNode();
            cmtitle = nifirst.toString();
            //the URI returned is just an identifier for the map, not it's real location (bmMap.getURI())
            tm.add(new Tool("Concept alone: " + cmtitle) {
                public void actionPerformed(ActionEvent ae) {
                    ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
                    try {
                        //FIXME  load containers for concept first.
                        Concept concept = store.getAndReferenceConcept(new URI(conceptres.getURI()));
                        InfoPanel.launchInfoPanelInFrame(concept);
                    } catch (URISyntaxException urise) {
                    	urise.printStackTrace();
                    } catch (ComponentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
        for (Iterator iter =
            getconceptincontextbookmarks(bookMarkModel, bmfolder).iterator();
            iter.hasNext();
            ) {
            final Resource bmConceptinctx = (Resource) iter.next();
            final Resource conceptres =
                ((Resource) bmConceptinctx)
                    .getProperty(RDF.object)
                    .getResource();
            final Resource concepmaptres =
                ((Resource) bmConceptinctx)
                    .getProperty(RDF.subject)
                    .getResource();
            NodeIterator ni =
                bmConceptinctx.getModel().listObjectsOfProperty(
                    bmConceptinctx,
                    DC.title);
            RDFNode nifirst = ni.nextNode();
            cmtitle = nifirst.toString();
            //the URI returned is just an identifier for the map, not it's real location (bmMap.getURI())
            tm.add(new Tool("Concept in context: " + cmtitle) {
                public void actionPerformed(ActionEvent ae) {

                    loadMap(mc, concepmaptres);

                    //---------------------------------code just copied from above - TO ORDER!!!!!!!!!!!!!!!!!!!

                    /*ConzillaKit kit = ConzillaKit.getDefaultKit();
                    
                    //For every required container, load it.
                    Container cont;
                    Collection containersToLoad =
                        bookMarkModel.getRequestedContainersForURI(
                            concepmaptres.getURI());
                    for (Iterator ctl = containersToLoad.iterator();
                        ctl.hasNext();
                        ) {
                        String ctlURI = (String) ctl.next();
                        try {
                            kit.getComponentStore().getAndReferenceContainer(
                                URIClassifier.parseURI(ctlURI));
                        } catch (ComponentException e) {
                            e.printStackTrace();
                        } catch (MalformedURIException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    //Then open the map.
                    try {
                        ConceptMap oldMap = mc.getConceptMap();
                        URI newMap =
                            URIClassifier.parseURI(concepmaptres.getURI());
                        mc.showMap(newMap);
                        mc.getHistoryManager().fireOpenNewMapEvent(
                            mc,
                            oldMap,
                            newMap);
                    } catch (ControllerException e) {
                        e.printStackTrace();
                    } catch (MalformedURIException e) {
                        e.printStackTrace();
                    }
                    */

                    //----------------------------------end of just copied code instead of using a function
                    Collection mos =
                        mc.getView().getMapScrollPane().getDisplayer().getMapObjects();

                    for (Iterator mosit = mos.iterator(); mosit.hasNext();) {
                        MapObject mo = (MapObject) mosit.next();
                        if (mo
                            .getConcept()
                            .getURI()
                            .equals(conceptres.getURI())) {
                            mo.pushMark(
                                new Mark(
                                    BrowseMapManagerFactory.COLOR_LASTCONCEPT,
                                    null,
                                    null),
                                mc);
                        }
                    }
                }

            });
        }
    }

    //function returns a list with all the bookmarks reifications for conceptmaps in the BookMarkFoder
    public List getconceptmapbookmarks(Model m, Resource bmfolder) {
        List lista = new LinkedList();
        try {
            Seq bmfolderseq = new SeqImpl(bmfolder, bmfolder.getModel());
            NodeIterator foldercontained = bmfolderseq.iterator();
            while (foldercontained.hasNext()) {
                RDFNode node = (RDFNode) foldercontained.next();
                if (node instanceof Resource)
                    if (((Resource) node)
                        .hasProperty(RDF.type, CV.ContextMapBookmark)) {
                        lista.add(node);
                    }
            }
        } catch (RDFException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List getconceptalonebookmarks(Model m, Resource bmfolder) {
        List lista = new LinkedList();
        try {
            Seq bmfolderseq = new SeqImpl(bmfolder, bmfolder.getModel());
            NodeIterator foldercontained = bmfolderseq.iterator();
            while (foldercontained.hasNext()) {
                RDFNode node = (RDFNode) foldercontained.next();
                if (node instanceof Resource)
                    if (((Resource) node)
                        .hasProperty(RDF.type, CV.ConceptBookmark)) {
                        lista.add(node);
                    }
            }
        } catch (RDFException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List getconceptincontextbookmarks(Model m, Resource bmfolder) {
        List lista = new LinkedList();
        try {
            Seq bmfolderseq = new SeqImpl(bmfolder, bmfolder.getModel());
            NodeIterator foldercontained = bmfolderseq.iterator();
            while (foldercontained.hasNext()) {
                RDFNode node = (RDFNode) foldercontained.next();
                if (node instanceof Resource)
                    if (((Resource) node)
                        .hasProperty(RDF.type, CV.ConceptInContextBookmark)) {
                        lista.add(node);
                    }
            }
        } catch (RDFException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.app.Extra#addExtraFeatures(se.kth.cid.conzilla.controller.MapController, java.lang.Object, java.lang.String, java.lang.String)
     */
    public void addExtraFeatures(MapController c) {
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.app.Extra#refreshExtra()
     */
    public void refreshExtra() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.app.Extra#saveExtra()
     */

    //with this function the edited bookMarkModel is saved
    public boolean saveExtra() {
        try {
            //Clean up valuemodel in shame by forcing it to edit nothing.
            shame.editFormItem(null);
            //Mark the model as edited, otherwise we can't save it.
            bookMarkModel.setEdited(true);
            //Now, save it!
            //ConzillaKit.getDefaultKit().getResourceStore().getContainerManager().saveResource(bookMarkModel);
            ConzillaKit.getDefaultKit().getResourceStore().getComponentManager().saveResource(bookMarkModel);
        } catch (ComponentException e3) {
            e3.printStackTrace();
        }
        return true;
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.app.Extra#exitExtra()
     */
    public void exitExtra() {
        // TODO Auto-generated method stub

    }

    /*
    public void conceptWeAreOn (){
        
    try {
            ConceptMap cmap=controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
            URI base = URIClassifier.parseValidURI(cmap.getURI());
    
            URI nuri=URIClassifier.parseURI(newval, base);
        
            Concept concept=controller.getConzillaKit().getComponentStore().getAndReferenceConcept(nuri);
            //      ConceptLayout ns=makeConceptLayout(concept);
    
            ConceptLayout ns = cmap.addConceptLayout(nuri.toString());
    
            java.awt.Dimension dim=controller.getMapScrollPane().getDisplayer().getMapObject(ns.getURI()).getPreferredSize();
                
            ns.setBoundingBox(LayoutUtils.preferredBoxOnGrid(gridModel, 
                                     mapEvent.mapX,
                                     mapEvent.mapY,
                                     dim));
        
            //      showTriples(ns, concept);
        } catch (ComponentException ce) {
            ErrorMessage.showError("Not found.", "Couldn't find concept.", ce,controller.getMapScrollPane().getDisplayer()); 
        } catch (MalformedURIException me) {
            ErrorMessage.showError("Not an URI.", "The identifier doesn't conform to the URI standard.", 
                       me,controller.getMapScrollPane().getDisplayer()); 
        } catch (ReadOnlyException re) {
            Tracer.bug("You shouldn't be able to choose 'insert concept' from menu when map isn't editable.");
        } catch (InvalidURIException iue) {
            ErrorMessage.showError("Not found.", "Couldn't find concept.", iue,controller.getMapScrollPane().getDisplayer()); 
        }
        */
}
