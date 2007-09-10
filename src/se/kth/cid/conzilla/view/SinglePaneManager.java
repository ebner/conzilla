/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.view;
import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import se.kth.cid.conzilla.controller.MapController;

public class SinglePaneManager extends AbstractViewManager
{ 
    JRootPane pane;
    JPanel toolbarPanel;
    View view;
    ConzillaSplitPane conzillaSplitPane;
    //    boolean block;
    
    public SinglePaneManager()
    {
	pane = new JRootPane();
    }

    public JComponent getSinglePane()
    {
	return pane;
    }

    public String getID()
    {
	return "SINGLE_PANE_VIEW";
    }
    
    public void initManager()
    {
	super.initManager();
	conzillaSplitPane = new ConzillaSplitPane();
    Box vertical = Box.createVerticalBox();
    toolbarPanel = new JPanel();
    toolbarPanel.setLayout(new BorderLayout());
    vertical.add(toolbarPanel);
	vertical.add(conzillaSplitPane);
    pane.setContentPane(vertical);
	
	//	LocaleManager.getLocaleManager().addPropertyChangeListener(this);
    }
    
    public void detachManager()
    {
	super.detachManager();

	conzillaSplitPane.detach();
	conzillaSplitPane = null;

	//	LocaleManager.getLocaleManager().removePropertyChangeListener(this);
    }


  public View newView(MapController controller)
    {
	return newView(controller, "onlyview");
    }

  public View newView(MapController controller, String id)
    {
	if (view != null)
	    return null;
	//	block = true;
	view = new DefaultView(controller);

        conzillaSplitPane.setPanes(view.getLeftPanel(), view.getMapPanel(), view.getRightPanel(), view);
	addView(view);
	
	JMenuBar bar = makeMenuBar(view, false);
	pane.setJMenuBar(bar);
    toolbarPanel.add(view.getToolsBar(),BorderLayout.CENTER);

	//	block = false;
	//	updateTitle(tv);
	return view;
    }

    /*    public void updateTitle(View view)
    {
	int index = tabbedPane.indexOfComponent(view.getController().getMapPanel());
	tabbedPane.setTitleAt(index, getTitle(view.getController()));
    }

  String getTitle(MapController controller)
    {
      String title = "(none)";      
      if(controller.getMapScrollPane() != null)
	{
	  ConceptMap map = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
	  
	  MetaData md = map.getMetaData();
	  title = MetaDataUtils.getLocalizedString(md.get_metametadata_language(), md.get_general_title()).string;
	}
      return title;
    }

  public void propertyChange(PropertyChangeEvent e)
    {
	if(e.getPropertyName().equals(LocaleManager.DEFAULT_LOCALE_PROPERTY))
	    {
		Enumeration en = getViews();
		while (en.hasMoreElements())
		    updateTitle((View) en.nextElement());
	    }
    }
    */
    protected void closeView(View v, boolean closeController)
    {}

}
