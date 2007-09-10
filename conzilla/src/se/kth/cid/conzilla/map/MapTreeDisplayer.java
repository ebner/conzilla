/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.tool.ActionMapMenuTool;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.LayerLayout;
import se.kth.cid.layout.ResourceLayout;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.rdf.RDFModel;
import se.kth.cid.util.AttributeEntryUtil;
import se.kth.nada.kmr.shame.applications.util.MetaDataPanel;
import se.kth.nada.kmr.shame.formlet.FormletStore;

/**
 * TODO: Description
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public class MapTreeDisplayer extends JSplitPane implements TreeSelectionListener {
    
    static {
        FormletStore.requireFormletConfigurations("formlets/formlets.rdf");
        FormletStore.requireFormletConfigurations("formlets/graphics/formlets.rdf");
    }
    static private String contextMapFCID = "http://kmr.nada.kth.se/shame/graphics/formlet#contextmapprofile";
    static private String nodeLayoutFCID = "http://kmr.nada.kth.se/shame/graphics/formlet#nodelayoutprofile";
    static private String conceptLayoutFCID = "http://kmr.nada.kth.se/shame/graphics/formlet#conceptlayoutprofile";
    static private String statementLayoutFCID = "http://kmr.nada.kth.se/shame/graphics/formlet#statementlayoutprofile";

    static public class MapTreeDisplayTool extends ActionMapMenuTool {
        
        public MapTreeDisplayTool(MapController controller) {
            super("VIEW_TREE",MapDisplayer.class.getName(),controller);
        }
        
        protected boolean updateEnabled() {
            return true;
        }

        public void actionPerformed(ActionEvent e) {
            MapTreeDisplayer.showMapTree(this.controller.getConceptMap());
        }
    }

    
    public static void showMapTree(ContextMap cMap) {
        MapTreeDisplayer mtd = new MapTreeDisplayer(cMap);
        JFrame frame = new JFrame();
        frame.setLocation(0, 0);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        String title = AttributeEntryUtil.getTitleAsString(cMap);
        frame.setTitle("Tree view of contextmap "+ title != null ? title : cMap.getURI().toString());
        frame.setContentPane(mtd);
        frame.setVisible(true);
        frame.pack();
    }
    
    public class ResourceLayoutCellRenderer extends DefaultTreeCellRenderer {

         /*Icon root, roots;
         Icon cmbm, cmbms;
         Icon bme, bmes;*/
         HashMap cachedTitles = new HashMap();
         
         public ResourceLayoutCellRenderer() {
             /*bme= gettheIcon("graphics/BookmarkKit/BookmarkEnvironment.gif");
             bmes= gettheIcon("graphics/BookmarkKit/BookmarkEnvironmentSelected.gif");
             cmbm= gettheIcon("graphics/BookmarkKit/ContextMapBookmark.gif");
             cmbms= gettheIcon("graphics/BookmarkKit/ContextMapBookmarkSelected.gif");*/            
//             root = IconUtil.getFormItem("Root");
//             roots = IconUtil.getFormItem("Root-Selected");
         }
        
    /**
          * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
          */
         public Component getTreeCellRendererComponent(
             JTree tree,
             Object node,
             boolean selected,
             boolean arg3,
             boolean arg4,
             int arg5,
             boolean arg6) {
             super.getTreeCellRendererComponent(
                 tree,
                 node,
                 selected,
                 arg3,
                 arg4,
                 arg5,
                 arg6);
//             TreeNode resourceNode = (TreeNode) node;
             
             /*switch (resourceNode.getType()) {
                 case TreeNode.ROOT :
                   setIcon(selected ? roots : root);
                 break;
                 case TreeNode.BOOKMARKFOLDER :
                     setIcon(selected ? bmes : bme);
                     break;
                 case TreeNode.CMBOOKMARK :
                     setIcon(selected ? cmbms : cmbm);
                     break;
                     //TO DO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                 case TreeNode.CONCEPTBOOKMARK :
                                   setIcon(selected ? roots : root);
                                 break;
                 case TreeNode.CONCEPTINCONTEXTBOOKMARK :
                                                   setIcon(selected ? roots : root);
                                                 break;
             }*/
             return super.getTreeCellRendererComponent(tree,getTitleForResourceLayout((ResourceLayout) node), selected, arg3, arg4,arg5, arg6);
         }
         
        public String getTitleForResourceLayout(ResourceLayout rl) {
            if (cachedTitles.containsKey(rl)) {
                return (String) cachedTitles.get(rl);
            }
            
            if (rl instanceof DrawerLayout) {
                String conceptURI = ((DrawerLayout) rl).getConceptURI();
                ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
                try {
                    Concept concept = store.getAndReferenceConcept(URI.create(conceptURI));
                    String title = AttributeEntryUtil.getTitleAsString(concept);
                    cachedTitles.put(rl, title);
                    return title;
                } catch (ComponentException e) {
                    e.printStackTrace();
                }
            }
            return rl.getURI();
        }
     }

    
    
    ContextMap conceptMap;
    JTree tree;
    MetaDataPanel mdp;
    
    public MapTreeDisplayer(ContextMap cMap) {
        super();
        this.conceptMap = cMap;
        initLayout();
    }
    
    
    private void updateView(ResourceLayout rl) {
        String containerURI = rl.getLoadContainer();
        ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
        try {
            Container cont = store.getAndReferenceContainer(URI.create(containerURI));
            if (cont instanceof RDFModel) {
                RDFModel model = (RDFModel) cont;
                mdp.setFormletConfigurationId(getFormletConfigurationId(rl));
                //mdp.present((RDFModel) cont, model.createResource(rl.getURI()));
                se.kth.nada.kmr.shame.applications.util.Container shameContainer =
                	new se.kth.nada.kmr.shame.applications.util.Container(model, URI.create(model.getURI()));
                mdp.present(shameContainer, model.createResource(rl.getURI()));
                mdp.revalidate();
                mdp.repaint();
            }
        } catch (ComponentException e) {
            e.printStackTrace();
        }
    }
    
    private String getFormletConfigurationId(ResourceLayout rl) {
        if (rl instanceof ConceptLayout) {
            return conceptLayoutFCID;
        } else if (rl instanceof StatementLayout) {
            return statementLayoutFCID;
        } else {
            return nodeLayoutFCID;
        }
    }
    
    private void initLayout() {
        tree = new JTree();
        try {
            tree.setModel(new DefaultTreeModel((LayerLayout) conceptMap.getLayerManager().getLayers().firstElement()));
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Cannot show the map since it has no layouts, not even an initial layer!");
        }
        tree.setCellRenderer(new ResourceLayoutCellRenderer());
        tree.addTreeSelectionListener(this);
/*        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2) {
                    ResourceLayout rl = (ResourceLayout) tree.getSelectionPath().getLastPathComponent();
                    updateView(rl);
                }
            }
        });*/
        
        
        mdp = new MetaDataPanel(contextMapFCID);
        setLeftComponent(new JScrollPane(tree));
        setRightComponent(mdp);
    }


    /**
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    public void valueChanged(TreeSelectionEvent e) {
        ResourceLayout rl = (ResourceLayout) e.getNewLeadSelectionPath().getLastPathComponent();
        updateView(rl);       
    }
}
