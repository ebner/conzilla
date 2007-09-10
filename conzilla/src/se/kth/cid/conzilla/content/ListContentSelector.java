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

package se.kth.cid.conzilla.content;

import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.component.MetaDataUtils;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.util.*;

import java.util.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;

public class ListContentSelector extends JPanel implements ContentSelector
{
    public static final String COLOR_BACKGROUND               = "conzilla.content.background.color";
    public static final String COLOR_TEXT                     = "conzilla.content.text.color";
    public static final String COLOR_SELECTION_BACKGROUND     = "conzilla.content.selection.background.color";
    public static final String COLOR_SELECTION_TEXT           = "conzilla.content.selection.text.color";
    public static final String COLOR_LEAF_ASPECT              = "conzilla.content.leafaspect.color";

    public static final String COLOR_POPUP_BACKGROUND         = "conzilla.content.popup.background.color";
    public static final String COLOR_POPUP_BACKGROUND_ACTIVE  = "conzilla.content.popup.background.activecolor";

    public static final String COLOR_POPUP_TEXT               = "conzilla.content.popup.text.color";
    public static final String COLOR_POPUP_TEXT_ACTIVE        = "conzilla.content.popup.text.activecolor";

    public static final String COLOR_POPUP_BORDER             = "conzilla.content.popup.border.color";
    public static final String COLOR_POPUP_BORDER_ACTIVE      = "conzilla.content.popup.border.activecolor";
    
    static final String[] COLOR_SET = {
	COLOR_BACKGROUND,
	COLOR_TEXT,           
	COLOR_SELECTION_BACKGROUND,
	COLOR_SELECTION_TEXT,  
	COLOR_LEAF_ASPECT,
	COLOR_POPUP_BACKGROUND,
	COLOR_POPUP_BACKGROUND_ACTIVE,
	COLOR_POPUP_TEXT,
	COLOR_POPUP_TEXT_ACTIVE,
	COLOR_POPUP_BORDER,    
	COLOR_POPUP_BORDER_ACTIVE,
    };

  se.kth.cid.component.Component[] content;
  Vector selectionListeners;
  ContentMenu   contentMenu;
  PropertyChangeSupport pSupport;
  MapController controller;
  PropertyChangeListener colorListener;
    
  PopupContentInfo popup;

  double             scale = 1.0;
  Font origFont;
  Font contentPathFont;
  
  JPanel titlePane;
  JList list;
  int selected;

    static
    {
	GlobalConfig.getGlobalConfig().addDefaults(ListContentSelector.class);
	GlobalConfig.getGlobalConfig().registerColorSet("COLOR_MENU", COLOR_SET, ListContentSelector.class.getName());
    }
    
  class ContentListModel extends AbstractListModel
  {
    public ContentListModel()
      {
      }
    
    public int getSize()
      {
	return (content != null) ? content.length : 0;
      }
    
    public Object getElementAt(int index)
      {
	String title = MetaDataUtils.getLocalizedString(content[index].getMetaData().get_metametadata_language(), content[index].getMetaData().get_general_title()).string;
	
	if(title.length() > 0)
	  return title;
	else
	  return "Unknown title";	
      } 
  }
  

  private void updateColors()
  {
    list.setBackground(GlobalConfig.getGlobalConfig().getColor(COLOR_BACKGROUND));
    list.setForeground(GlobalConfig.getGlobalConfig().getColor(COLOR_TEXT));
    list.setSelectionBackground(GlobalConfig.getGlobalConfig().getColor(COLOR_SELECTION_BACKGROUND));
    list.setSelectionForeground(GlobalConfig.getGlobalConfig().getColor(COLOR_SELECTION_TEXT));
  }

  public ListContentSelector()
    {
      this.contentMenu=contentMenu;
      setFont(new Font("Lucida Sans", Font.PLAIN, 12));
      origFont = getFont();
      Tracer.debug("List: " + getFont());
      contentPathFont = new Font("Arial", Font.PLAIN, 10);

      selectionListeners = new Vector();
      pSupport = new PropertyChangeSupport(this);

      list = new JList();
      //      list.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
      //      list.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));

      titlePane = new JPanel();
      titlePane.setLayout(new BoxLayout(titlePane, BoxLayout.Y_AXIS));

      colorListener=new PropertyChangeListener() {
	      public void propertyChange(PropertyChangeEvent evt)
	      {
		  ListContentSelector.this.updateColors();
	      }};
      GlobalConfig.getGlobalConfig().addPropertyChangeListener(COLOR_BACKGROUND, colorListener);
      GlobalConfig.getGlobalConfig().addPropertyChangeListener(COLOR_TEXT, colorListener);
      GlobalConfig.getGlobalConfig().addPropertyChangeListener(COLOR_SELECTION_BACKGROUND, colorListener);
      GlobalConfig.getGlobalConfig().addPropertyChangeListener(COLOR_SELECTION_TEXT, colorListener);
      
      updateColors();

      setLayout(new java.awt.BorderLayout());
      setMinimumSize(new java.awt.Dimension(0, 0));
      setPreferredSize(new java.awt.Dimension(0,0));

      //      descriptionPopUp.getContentsetLayout(new BorderLayout());

      

      MouseInputAdapter mouseListener = new MouseInputAdapter() {
	  public void mousePressed(MouseEvent e) {
	    int index = list.locationToIndex(e.getPoint());
	    
	    if (index==-1) 
		return;
	    
	    if ((e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) && index!=-1)
		{
		    list.setSelectedIndex(index);
		    popup.removeAllPopups();
		    contentMenu.showPopup(e, index);
		}
	  }
	  public void mouseClicked(MouseEvent e) {
	    int index = list.locationToIndex(e.getPoint());

	    if (e.getClickCount()==2)
		{
		    popup.removeAllPopups();
		    select(index);
		}
	  }
	  };

      list.addMouseListener(mouseListener);
      
      list.addListSelectionListener(new ListSelectionListener() {
	      public void valueChanged(ListSelectionEvent lse)
	      {
		  if (!lse.getValueIsAdjusting() && content!=null && 
		      ((JList) lse.getSource()).getSelectedIndex()>=0)
		      pSupport.firePropertyChange(new PropertyChangeEvent(list,SELECTION, null,
									  content[((JList) lse.getSource()).getSelectedIndex()]));
	      }});
      popup=new PopupContentInfo(this, list);
    }
  protected void finalize()
    {
      GlobalConfig.getGlobalConfig().removePropertyChangeListener(COLOR_BACKGROUND, colorListener);
      GlobalConfig.getGlobalConfig().removePropertyChangeListener(COLOR_TEXT, colorListener);
      GlobalConfig.getGlobalConfig().removePropertyChangeListener(COLOR_SELECTION_BACKGROUND, colorListener);
      GlobalConfig.getGlobalConfig().removePropertyChangeListener(COLOR_SELECTION_TEXT, colorListener);
    }
      
  void addAll()
    {
      add(titlePane, java.awt.BorderLayout.NORTH);
      JScrollPane scroll=new JScrollPane();
      scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      scroll.setViewportView(list);
      scroll.getHorizontalScrollBar().setUnitIncrement(15);
      scroll.getVerticalScrollBar().setUnitIncrement(15);

      add(scroll, java.awt.BorderLayout.CENTER);

      JButton close = new JButton();
      ConzillaResourceManager.getDefaultManager().customizeButton(close, ListContentSelector.class.getName(), "CLOSE");
      add(close, java.awt.BorderLayout.SOUTH);

      close.addActionListener(new ActionListener()
	{
	  public void actionPerformed(ActionEvent e)
	    {
		selectContentFromSet(null);
		popup.deactivate();
		/*	      removeAll();
	      revalidate();
	      repaint();*/
	    }
	});

      popup.activate();

      revalidate();
      repaint();
    }
  
  public void setContentPath(String [] contpath)
    {
	titlePane.removeAll();
	int inc=2;
	JLabel label=null;
	for (int i=contpath.length-1;i>=0; i--)
	    {
		label=new JLabel(contpath[i]);
		label.setBorder(BorderFactory.createEmptyBorder(0,inc,0,2));
		label.setFont(contentPathFont.deriveFont((float) (contentPathFont.getSize2D() * scale)));
		titlePane.add(label);
		inc+=6;
	    }
	if (label!=null)
	    {
		label.setOpaque(true);
		label.setForeground(GlobalConfig.getGlobalConfig().getColor(COLOR_LEAF_ASPECT));
	    }
    }

  public void setController(final MapController controller)
    {
	this.controller=controller;
      
        PropertyChangeListener zoomListener;
	zoomListener=new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt)
		{
		    if(evt.getPropertyName().equals(MapController.ZOOM_PROPERTY))
			setScale(((Double) evt.getNewValue()).doubleValue(), ((Double) evt.getOldValue()).doubleValue());
		}};
	
	controller.addPropertyChangeListener(zoomListener);

	contentMenu = new ContentMenu(controller);
    }
  public MapController getController()
    {
	return controller;
    }

  public void select(int select)
    {
	if (list.getSelectedIndex()!=select)
	    list.setSelectedIndex(select);
	try{
	    controller.getConzillaKit().getContentDisplayer().setContent(content[select]);
	    //	      controller.getHistoryManager().fireHistoryEvent(he);
	} catch (ContentException e)
	    {
		ErrorMessage.showError("Failed to show content.", "Failed to show content.", 
				       null, list);			
	    }
    }
  
  public se.kth.cid.component.Component getSelectedContent()
  {
      int selected=list.getSelectedIndex();
      if (content!=null && selected>=0 && selected<content.length)
	  return content[selected];
      return null;
  }

  public se.kth.cid.component.Component getContent(int index)
  {
      return content[index];
  }

  public void selectContentFromSet(se.kth.cid.component.Component[] content)
    {
      se.kth.cid.component.Component[] old= this.content;
      this.content = content;
      list.setModel(new ContentListModel());
      removeAll();
      popup.deactivate();
      if(content != null)
	  {
	      addAll();
	      Dimension dim=list.getPreferredSize();
	      if (dim.width>200)
		  setPreferredSize(new Dimension(200, 0));
	      else if (dim.width<30)
		  setPreferredSize(new Dimension(30, 0));
	      else
		  setPreferredSize(dim);
		  
	  }
      else
	  setPreferredSize(new java.awt.Dimension(0,0));

      pSupport.firePropertyChange(SELECTOR, old, content );
    }

  public java.awt.Component getComponent()
    {
      return this;
    }
  
  
  public void addSelectionListener(String propertyName, PropertyChangeListener l )
    {
	pSupport.addPropertyChangeListener(propertyName, l);
    }
  
  public void removeSelectionListener(String propertyname, PropertyChangeListener l)
    {
	pSupport.removePropertyChangeListener(propertyname, l);
    }

  public void setScale(double newscale, double oldscale)
    {
	this.scale = newscale;
	list.setFont(origFont.deriveFont((float) (origFont.getSize2D() * scale)));

	Component [] comps = titlePane.getComponents();
	for (int i=0; i<comps.length;i++)
	    comps[i].setFont(contentPathFont.deriveFont((float) (contentPathFont.getSize2D() * scale)));
	
	popup.setScale(newscale, oldscale);
	
	/*      transform = AffineTransform.getScaleInstance(scale, scale);
            resizeMap();
      
      for (Iterator i = neuronMapObjects.values().iterator(); i.hasNext();)
	{
	  NeuronMapObject nmo = (NeuronMapObject) i.next();
	  nmo.setScale(scale);
	}
	*/
      revalidate();
      repaint();

    }
}
