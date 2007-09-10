/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.component.Component;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.ComponentFactory;
import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.component.Resource;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.clipboard.Clipboard;
import se.kth.cid.conzilla.content.ContentEditor;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.tool.ActionMapMenuTool;
import se.kth.cid.conzilla.util.IdentifierDialog;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.notions.ContentInformation;
import se.kth.cid.rdf.CV;
import se.kth.cid.util.AttributeEntryUtil;
import se.kth.cid.util.Tracer;

import com.hp.hpl.jena.rdf.model.RDFException;

/** 
 *  @author Matthias Palm?r
 *  @version $Revision$
 */
public class ManageContentMapTool extends ActionMapMenuTool implements ListSelectionListener{

    class ContentInformationRenderer extends DefaultListCellRenderer {
            /**
            * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
            */
            public java.awt.Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean hasFocus) {
                ContentInformation ci  = (ContentInformation) value;
                String label = (String) content2Label.get(cI2Content.get(value));
                if (label == null) {
                    label = value.toString();
                }
                java.awt.Component comp = super.getListCellRendererComponent(
                    list,
                    label,
                    index,
                    isSelected,
                    hasFocus);
                comp.setEnabled(isCIExpressedInSession(ci));
                return comp;
            }

        }

    HashMap cI2Content;
    HashMap content2Label;

    Concept concept;
    public static final String MANAGE_CONTENT = "MANAGE_CONTENT";
    JFrame contentFrame;
    JPanel message = new JPanel();
    ContentEditor contentEditor;
    JList ccList;
    JList cccList;
    JButton down;
    JButton up;
    ConzillaKit kit = ConzillaKit.getDefaultKit();
    //Container container;
    EditMapManager editMapManager;
    private JButton remove;
    private JButton copy;
    private JButton paste;
    private JButton pasteContext;
    private JButton createContext;
    private JButton create;
    
    public ManageContentMapTool(MapController cont, EditMapManager emm) {
        super(MANAGE_CONTENT, EditMapManagerFactory.class.getName(), cont);
        this.editMapManager = emm;
    }

    protected boolean updateEnabled() {
        Concept c;
        if (mapEvent.hitType != MapEvent.HIT_NONE
            && (c = mapObject.getConcept()) != null) {
            this.concept = c;
            Tracer.debug("Concept is:" + concept.toString());
            return true;
        }
        return false;
    }

    public void managecontent(final Container container) {
    }

    private boolean isCIExpressedInSession(ContentInformation ci) {
        String curi = ci.getContainer().getURI();
        Session session = controller.getConceptMap().getComponentManager().getEditingSesssion();
        return curi.equals(session.getContainerURIForConcepts())
        || curi.equals(session.getContainerURIForLayouts());
    }
    
    private boolean isContentExpressedInSession(Component c) {
        String curi = c.getLoadContainer();
        Session session = controller.getConceptMap().getComponentManager().getEditingSesssion();
        return curi.equals(session.getContainerURIForConcepts())
        || curi.equals(session.getContainerURIForLayouts());
    }

    private void getContentOnConcept(
        Vector contentInformations,
        Container container) {
        Set cis = concept.getContentInformation();

        for (Iterator cisIt = cis.iterator(); cisIt.hasNext();) {
            ContentInformation ci = (ContentInformation) cisIt.next();
            if (ci.getContainer() == container) {
                contentInformations.add(ci);
            }
        }
    }

    private void initLayout() {
        contentFrame = new JFrame();
        contentFrame.setLocation(0, 0);
        contentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        contentFrame.setTitle("Manage content on concept:"+AttributeEntryUtil.getTitleAsString(concept));
        ccList = new JList();
        ccList.setMinimumSize(new Dimension(400,100));
        ccList.setCellRenderer(new ContentInformationRenderer());
        ccList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ccList.setBorder(BorderFactory.createTitledBorder("Context-free"));
        
        cccList = new JList();
        cccList.setMinimumSize(new Dimension(400,100));
        cccList.setCellRenderer(new ContentInformationRenderer());
        cccList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cccList.setBorder(BorderFactory.createTitledBorder("Context-specific"));

        up = new JButton(new AbstractAction("Up") {
            public void actionPerformed(ActionEvent e) {
                moveUp();
            }
        });

        down = new JButton(new AbstractAction("Down") {
            public void actionPerformed(ActionEvent e) {
                moveDown();
            }
        });
        
        JPanel moveButtons = new JPanel();
        moveButtons.setLayout(new BoxLayout(moveButtons, BoxLayout.X_AXIS));
        moveButtons.add(up);
        moveButtons.add(Box.createHorizontalGlue());
        moveButtons.add(down);

        JPanel lists = new JPanel();
        lists.setLayout(new BoxLayout(lists, BoxLayout.Y_AXIS));
        lists.add(new JScrollPane(ccList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        lists.add(moveButtons);
        lists.add(new JScrollPane(cccList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        
        contentEditor =
            new ContentEditor(ContentEditor.contentFormletConfigurationId);
        contentEditor.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
        contentEditor.setContainerChoosable(true);
        contentEditor.setFormletConfigurationChoosable(true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(contentEditor, BorderLayout.NORTH);
        JSplitPane split =
            new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                lists,
                panel);
        //split.setDividerLocation(200);

        JPanel vertical = new JPanel();
        vertical.setLayout(new BorderLayout());
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        vertical.add(message, BorderLayout.NORTH);
        vertical.add(split, BorderLayout.CENTER);
        vertical.add(buttons, BorderLayout.SOUTH);
        vertical.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        split.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));

        
        remove = new JButton(new AbstractAction("Remove") {
            public void actionPerformed(ActionEvent e) {
                ContentInformation ci = getSelectedContainerInformation();
                if (ci != null) {
                    removeContentDialog(ci);
                    updateContent();
                }
            }
            
        });
        buttons.add(remove);
        copy = new JButton(new AbstractAction("Copy") {
            public void actionPerformed(ActionEvent e) {
                ContentInformation ci = getSelectedContainerInformation();
                if (ci != null) {
                    Clipboard c = editMapManager.getClipBoard();
                    c.setComponent((Component) cI2Content.get(ci));
                }
            }
        });
        buttons.add(copy);

        paste = new JButton(new AbstractAction("Paste") {
            public void actionPerformed(ActionEvent e) {
                Clipboard c = editMapManager.getClipBoard();
                Resource contentComponent = c.getComponent();
                createContentOnConcept(concept, (Component) contentComponent);
                updateContent();
                selectContent((Component) contentComponent);
            }
        });
        buttons.add(paste);
        
        pasteContext = new JButton(new AbstractAction("Paste in Context") {
            public void actionPerformed(ActionEvent e) {
                Clipboard c = editMapManager.getClipBoard();
                Resource contentComponent = c.getComponent();
                createContentOnConceptInContext(controller.getConceptMap(), concept, contentComponent.getURI());
                updateContent();
                selectContent((Component) contentComponent);
            }   
        });
        buttons.add(pasteContext);

        create = new JButton(new AbstractAction("Create") {
            public void actionPerformed(ActionEvent e) {
                Component contentComponent = createContentOnConceptDialog(concept);
                updateContent();
                selectContent(contentComponent);
            }   
        });
        buttons.add(create);

        createContext = new JButton(new AbstractAction("Create in Context") {
            public void actionPerformed(ActionEvent e) {
                Component contentComponent = createContentInContextDialog(
                        controller.getConceptMap(), concept);
                updateContent();
                selectContent(contentComponent);
            }   
        });
        buttons.add(createContext);

        
        buttons.add(Box.createHorizontalGlue());
        JButton done = new JButton(new AbstractAction("Done") {
            public void actionPerformed(ActionEvent e) {
                contentFrame.setVisible(false);
                done();
            } 
        });
        buttons.add(done);
        
//        ccList.addMouseListener(ma);
  //      cccList.addMouseListener(ma);
        ccList.addListSelectionListener(this);
        cccList.addListSelectionListener(this);

        WindowAdapter wa = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                done();
            }
        };

        contentFrame.addWindowListener(wa);
        contentFrame.setContentPane(vertical);
    }

    /**
     * @param component
     */
    protected void selectContent(Component component) {
        for (Iterator cis = cI2Content.keySet().iterator(); cis.hasNext();) {
            ContentInformation ci = (ContentInformation) cis.next();
            if (cI2Content.get(ci) == component) {
                if (ci.getContext() == null) {
                    ccList.setSelectedValue(ci, true);
                } else {
                    cccList.setSelectedValue(ci, true);
                }
                break;
            }
        }
    }

    protected ContentInformation getSelectedContainerInformation() {
        ContentInformation ci = (ContentInformation) ccList.getSelectedValue();
        if (ci == null) {
            ci = (ContentInformation) cccList.getSelectedValue();
        }
        return ci;
    }

    private java.awt.Component updateMessage() {
        message.removeAll();
        message.setLayout(new BoxLayout(message, BoxLayout.X_AXIS));
        Session session = controller.getConceptMap().getComponentManager().getEditingSesssion();
        JLabel glabel = new JLabel("<html><body><center><i>Content</i> can be linked to <i>concepts</i>, " +
                "either in a <i>context-free</i> way, which means that the <i>content</i> is available in all <i>context-maps</i>, " +
                "<i>or in a <i>context-specific</i> way, which means that the <i>content</i> is available only in the " +
                "specified <i>context-map</i>.</center></body></html>");
        JLabel slabel = new JLabel("<html><body><center>This dialog is restricted to manage " +
                "links to <i>content</i> from the <i>concept</i> <b><font color=blue>"
                +AttributeEntryUtil.getTitleAsString(concept)+
                "</font></b> possibly specific to the <i>context-map</i> <b><font color=blue>"
                +AttributeEntryUtil.getTitleAsString(controller.getConceptMap())+
                "</font></b> within the <i>session</i> <b><font color=blue>"
                +session.getTitle()+
                "</font></b>.</center></body></html>");
        message.add(slabel);
        message.add(Box.createVerticalStrut(5));
        message.add(glabel);
        slabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE), "Dialog-specific information"),
                BorderFactory.createEmptyBorder(5,10,5,10)));
        glabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE), "General information"),
                BorderFactory.createEmptyBorder(5,10,5,10)));
        return message;
    }

    /**
     * 
     */
    protected void updateButtons(boolean isEditable) {
        boolean ccSelection = ccList.getSelectedIndex() != -1;
        boolean cccSelection = cccList.getSelectedIndex() != -1;
        down.setEnabled(isEditable && ccSelection);
        up.setEnabled(isEditable && cccSelection);
        remove.setEnabled(isEditable);
        boolean clipboardEmpty = editMapManager.getClipBoard().getComponent() == null;
        paste.setEnabled(!clipboardEmpty);
        pasteContext.setEnabled(!clipboardEmpty);
        copy.setEnabled(ccSelection || cccSelection);
    }

    protected void moveDown() {
        ContentInformation ci = (ContentInformation) ccList.getSelectedValue();
        if(ci == null) {
            return;
        }
        removeContent(ci);
        createContentOnConceptInContext(controller.getConceptMap(), concept, ci.getContentURI());
        updateContent();
        selectContent((Component) cI2Content.get(ci));
    }

    protected void moveUp() {
        ContentInformation ci = (ContentInformation) cccList.getSelectedValue();
        if(ci == null) {
            return;
        }
        removeContent(ci);        
        createContentOnConcept(concept, (Component) cI2Content.get(ci));
        updateContent();
        selectContent((Component) cI2Content.get(ci));
    }
    
    protected void removeContent(ContentInformation ci) {
        if (ci.getContext() == null) {
            try {
                Concept concept = kit.getResourceStore().getAndReferenceConcept(new URI(ci.getConceptURI()));
                AttributeEntry ae = concept.getAttributeEntry(ci.getConceptToContentRelation(), ci.getContentURI(), Boolean.FALSE, ci.getContainer().getURI());
                concept.removeAttributeEntry(ae);
            } catch (URISyntaxException urise) {
            	urise.printStackTrace();
            } catch (ComponentException e) {
                e.printStackTrace();
            }            
        } else {
            try {
                mapObject
                .getDrawerLayout()
                .getConceptMap().removeContentInContext(ci);
            } catch (ReadOnlyException e) {
                e.printStackTrace();
            } catch (InvalidURIException e) {
                e.printStackTrace();
            }
        }
        
    }

    private void done() {
        //finish the editor off
        contentEditor.finishEdit();
    }
    
    public void actionPerformed(ActionEvent e) {
//        container =
  //          kit.getResourceStore().getContainerManager().getCurrentConceptContainer();
        
        if (contentFrame == null) {
            initLayout();
        }
        updateMessage();        
        updateContent();

        contentFrame.setSize(new Dimension(900,600));
        contentFrame.setVisible(true);
    }
    
    private void updateContent() {
        Vector allContentInformations = new Vector();
        Vector cc = new Vector();
        Vector ccc = new Vector();
        ContainerManager cm = kit.getResourceStore().getContainerManager();
        for (Iterator uris = concept.getComponentManager().getLoadedRelevantContainers().iterator(); uris.hasNext();) {
            URI uri = (URI) uris.next();
            Container container = cm.getContainer(uri.toString());
            getContentOnConcept(cc, container);
        }
        Set contentInContext = mapObject
            .getDrawerLayout()
            .getConceptMap()
            .getContentInContextForConcept(
            concept.getURI());
        
        if (contentInContext != null)  {
            ccc.addAll(contentInContext);
        }
        allContentInformations.addAll(cc);
        allContentInformations.addAll(ccc);
        
        cI2Content = new HashMap();
        content2Label = new HashMap();
        ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
        for (Iterator cIs = allContentInformations.iterator(); cIs.hasNext();) {
            ContentInformation ci = (ContentInformation) cIs.next();
            try {
                Component c =
                    store.getAndReferenceComponent(new URI(ci.getContentURI()));
                cI2Content.put(ci, c);
                String title = AttributeEntryUtil.getTitleAsString(c);
                if (c != null && title != null) {
                    content2Label.put(c, title);
                }
            } catch (URISyntaxException urise) {
            	urise.printStackTrace();
            } catch (ComponentException e1) {
                e1.printStackTrace();
            }
        }
        ccList.setListData(cc);
        cccList.setListData(ccc);
        contentEditor.finishEdit();
        updateButtons(false);
    }

    public void removeContentDialog(ContentInformation ci) {
        Component c = (Component) (cI2Content.get(ci));
        if (isContentExpressedInSession(c)) {
            int option = JOptionPane.showConfirmDialog(contentFrame, "Remove the content as well as the link? \n" +
                "Yes = Removes the content and the link \n" +
                "No = Removes only the link\n" +
                "Cancel = Aborts", 
                "Unlink and remove", JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.CANCEL_OPTION) {
                return;
            }
            removeContent(ci);
        
            if (option == JOptionPane.YES_OPTION) {
                if (c instanceof ContextMap) {
                    if (JOptionPane.showConfirmDialog(contentFrame, 
                            "This content is a Context-map, are you really sure you want to remove it?", 
                            "Confirm removal of Context-map",
                        JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                        c.remove();    
                    }
                } else {
                    c.remove();                                
                }
            }
        } else {
            int option = JOptionPane.showConfirmDialog(contentFrame, 
                    "Unlink the content from the current concept\n",
                    "Unlink content", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.CANCEL_OPTION) {
                return;
            }
            removeContent(ci);    
        }
    }
    
    public static Component createContentOnConceptDialog(Concept concept) {
        Component contentComponent;
        Container cContainer = ConzillaKit.getDefaultKit()
            .getResourceStore().getContainerManager()
            .getCurrentConceptContainer();

        IdentifierDialog dialog = new IdentifierDialog();
        dialog.addPasteButton();
        dialog.addNextButton();
        dialog.addCancelButton();

        dialog.setTextMessage(
            "The URI that you type below will be added as content\n" +
            "on the concept in the current container.\n" +
            "If there is no URI already you can generate a globally unique\n" +
            "identifier by preccsing the 'generate' button.\n" +
            "You will in the next step be given the option to fill in some\n" +
            "metadtata on this content.");
        dialog.addGenerateURIButton(generateContentURI().toString());
        dialog.pack();
        dialog.setVisible(true);
        
        String contentURI = dialog.getURI();

        if (contentURI != null &&
                contentURI.length() != 0) {
            try {
                Tracer.debug(
                    "We have a contentURI and we want to write a statement in the Concepts RDFModel: "
                        + cContainer.getURI());

                contentComponent = getOrCreateContentComponent(contentURI);
                createContentOnConcept(concept, contentComponent);
            } catch (RDFException ea) {
                throw new RuntimeException(
                    "Cannot create content on concept in concept container "
                        + cContainer.getURI());
            }
            //maybe I should not save now, but with the whole window-close?
 //           saveComponent(cContainer);
        } else {
           return null; 
        }
        return contentComponent;
    }

    public static Component createContentInContextDialog(ContextMap cMap, Concept concept) {
        IdentifierDialog dialog = new IdentifierDialog();
        dialog.addNextButton();
        dialog.addCancelButton();

        dialog.setTextMessage(
            "You are about to atach a content to this concept in this context in the RDFModel \n where "
                + "the context map layout resides.");
        dialog.addGenerateURIButton(generateContentURI().toString());
        dialog.pack();
        dialog.setVisible(true);
        String contentURI = dialog.getURI();

        if (contentURI != null 
                || contentURI.length() != 0) {
            createContentOnConceptInContext(cMap, concept, contentURI);
        } else {
            return null;
        }
        return getOrCreateContentComponent(contentURI);
    }

    /**
     * @param concept
     * @param contentComponent
     */
    private static void createContentOnConcept(Concept concept, Component contentComponent) {
        concept.addAttributeEntry(
            CV.contains.toString(),
            contentComponent);
    }

    /**
     * @param cMap
     * @param concept
     * @param contentURI
     */
    private static void createContentOnConceptInContext(ContextMap cMap, Concept concept, String contentURI) {
        try {
                cMap.addContentInContext(
                concept.getURI(),
                CV.contains.getURI(),
                contentURI);
        } catch (RDFException ea) {
            throw new RuntimeException("Cannot create content in context in layout container");
        } catch (ReadOnlyException e1) {
            e1.printStackTrace();
        } catch (InvalidURIException e2) {
            e2.printStackTrace();
        }
    }

    public static URI generateContentURI() {
        String base = ConzillaKit.getDefaultKit()
            .getResourceStore().getContainerManager().getBaseURIForConcepts();
        String nuri = generateUniqueURI(base);
        try {
			return new URI(nuri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
    }

    public static String generateUniqueURI(String baseURI) {
        return baseURI + "content";
    }

    public static Component getOrCreateContentComponent(String contentURI) {
        try {
            ConzillaKit kit = ConzillaKit.getDefaultKit();
//            URI contentrealURI = URIClassifier.parseValidURI(contentURI);
            //ContainerManager cm = kit.getResourceStore().getContainerManager();
            ComponentFactory cm = kit.getResourceStore().getComponentManager();
            Component component = kit.getResourceStore().getCache().getComponent(contentURI);
            if (component != null) {
                return component;
            }
            
            return cm.createComponent(new URI(contentURI));
        } catch (ComponentException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e) {
        JList current = ((JList) e.getSource());
        ContentInformation ci =
            (ContentInformation) current.getSelectedValue();
        if (ci == null) {
            return;
        }
        
        if (current == ccList) {
            cccList.clearSelection();
        } else {
            ccList.clearSelection();
        }
        Component comp = (Component) (cI2Content.get(ci));
        updateButtons(isCIExpressedInSession(ci));
        if (isContentExpressedInSession(comp)) {
            contentEditor.editContent(ci, (Component) (cI2Content.get(ci)));
        } else {
            contentEditor.presentContent(ci, (Component) (cI2Content.get(ci)));
        }
    }
}