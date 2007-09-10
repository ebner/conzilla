/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JMenu;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.map.MapStoreManager;
import se.kth.cid.conzilla.tool.MapToolsMenu;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.rdf.RDFContainerManager;
import se.kth.cid.rdf.RDFModel;
import se.kth.cid.util.AttributeEntryUtil;

import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;



/** TODO, this class should be a tool. It should display all resources in all models in a menu in menu fashion.
 * @author matthias
 */
public class ResourceInsertionMenu extends MapToolsMenu
{
    
    private GridModel gridModel;
    
	public ResourceInsertionMenu(MapController controller, GridModel gridModel) {
        super("INSERT_CONCEPT_FROM_CONTAINER", EditMapManagerFactory.class.getName(), controller);
       this.gridModel = gridModel;
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.tool.MapMenuItem#update(se.kth.cid.conzilla.map.MapEvent)
     */
    public void update(final MapEvent mapEvent) {
        super.update(mapEvent);
        
        JMenu menu = (JMenu) getJMenuItem();
        menu.removeAll();

        MapStoreManager sm = mapEvent.mapDisplayer.getStoreManager();
        Collection concepts = sm.getConcepts();
        HashSet ids = new HashSet();
        for (Iterator conceptsIt = concepts.iterator(); conceptsIt.hasNext();) {
            Concept concept = (Concept) conceptsIt.next();
            ids.add(concept.getURI());
        }
        
        
        Iterator it = ConzillaKit.getDefaultKit().getResourceStore().getContainerManager().getContainers(Container.COMMON).iterator();
        while (it.hasNext()) {
            Container container = (Container) it.next();
            if (container instanceof RDFModel) {
                RDFModel model = (RDFModel) container;
                JMenu cmenu = new JMenu(container.getURI());
                boolean populated = false;
                menu.add(cmenu);
                int counter = 0;
                ResIterator res = model.listSubjects();
                while (res.hasNext()) {
                    final Resource resource = res.nextResource();                    
                    if (!ids.contains(resource.getURI())) {
                        if (!interestingResource(resource)) {
                            continue;
                        }
                    	if (++counter > 10) {
                    		cmenu.addSeparator();
                    		cmenu.add(new JLabel("Too many resources, list truncated"));
                    		break;
                    	}

                        
                        cmenu.add(new AbstractAction(AttributeEntryUtil.getTitleAsString(model, resource.getURI())) {
                            public void actionPerformed(ActionEvent e) {
                                insertConcept(resource, mapEvent);
                            }   
                        });
                        populated = true;
                    }
                }
                if (!populated) {
                    cmenu.setEnabled(false);
                }
                
            }
        }
    }
    
    private void insertConcept(Resource resource, MapEvent mapEvent) {
        URI uri = URI.create(resource.getURI());
        try {
            Concept concept = ConzillaKit.getDefaultKit().getResourceStore().getAndReferenceConcept(uri);
//            concept.addAttributeEntry(CV.title, uri.getURI());
            ConceptLayout ns = controller.getConceptMap().addConceptLayout(concept.getURI());
            MapObject mo =
                controller.getView().getMapScrollPane().getDisplayer().getMapObject(
                    ns.getURI());
            Dimension dim = mo.getPreferredSize();
            if (dim.width == 0)
                dim.width = 50;
            if (dim.height == 0)
                dim.height = 20;

            ns.setBoundingBox(
                LayoutUtils.preferredBoxOnGrid(
                    gridModel,
                    mapEvent.mapX,
                    mapEvent.mapY,
                    dim));

        } catch (ComponentException e) {
            e.printStackTrace();
        } catch (ReadOnlyException e) {
            e.printStackTrace();
        } catch (InvalidURIException e) {
            e.printStackTrace();
        }
    }

    private boolean interestingResource(Resource resource) {
        if (resource.isAnon()) {
            return false;
        }
        
        RDFContainerManager manager = (RDFContainerManager) ConzillaKit.getDefaultKit().getResourceStore().getContainerManager();
        Statement st = manager.findLayoutType(resource);
        if (st != null) {
            return false;
        }
        
        return true;
    }
	
    public void detach() {
        // TODO Auto-generated method stub
        super.detach();
    }
}
