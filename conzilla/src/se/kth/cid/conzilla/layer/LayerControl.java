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


package se.kth.cid.conzilla.layer;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.menu.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.util.*;

import javax.swing.event.*;
import javax.swing.*;
import java.util.*;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;

/** 
 *  @author Matthias Palmer.
 */
public class LayerControl 
    implements PropertyChangeListener, ChangeListener, ActionListener, LayerListener  
{

    public final static String LAYER_CONTROL_MENU = "LAYER_CONTROL_MENU";

    JFrame frame;
    JSlider layerSlider;
    LayerEntries layerEntries;
    JButton create;
    MapController controller;
    LayerManager lMan;
    boolean commit;
    boolean lock;

    public class LayerEntries extends JPanel
    {
	/** Contains the layers in oposite order compared to the
	 *  LayerManager order. This is due to the graphical presentation.
	 */
	Vector layerEntries;
	ToolsMenu popup;
	MouseInputAdapter  mia;
	LayerEntry current;
	int currentIndex;
	LayerEntry choosenLayerEntry;

	public LayerEntries()
	{
	    layerEntries = new Vector();
	    setLayout(new VerticalListLayout(layerEntries));
	    
	    popup = new ToolsMenu(LAYER_CONTROL_MENU, LayerControl.class.getName()); 
	    popup.addTool(new Tool("REMOVE_LAYER", LayerControl.class.getName())
		    {
			public void actionPerformed(ActionEvent ae)
			{
			    Tracer.debug("Removing layer "+current.getLayerStyle().getURI());
			    LayerManager lMan = LayerControl.this.lMan;
			    if (current.getLayerStyle().getObjectStyles().size() > 0)
				{
				    int result = JOptionPane.showConfirmDialog(current, 
					"You are about to remove a non-empty layer, proceed?", 
					"Remove Layer", 
					JOptionPane.YES_NO_OPTION);
				    if (result == JOptionPane.YES_OPTION)
					lMan.removeLayer(current.getLayerStyle());
				}
			    else
				lMan.removeLayer(current.getLayerStyle());
			    LayerEntries.this.repaint();
			}
		}, 100);
	    mia  = new MouseInputAdapter()
		{
		    public void mousePressed(MouseEvent me)
		    {
			current = (LayerEntry) (me.getComponent()).getParent();

			if (me.isPopupTrigger())
			    popup.getPopupMenu().show(me.getComponent(), me.getX(), me.getY());
			else
			    {
				me = SwingUtilities.convertMouseEvent(me.getComponent() ,me , LayerEntries.this); 
				currentIndex = layerPosition(me.getY());
			    }
		    }

		    public void mouseClicked(MouseEvent me)
		    {
			if (current != null)
			    setLayerChoosen(current);
		    }
		    
		    public void mouseDragged(MouseEvent me)
		    {
			me = SwingUtilities.convertMouseEvent(me.getComponent(), me, LayerEntries.this); 
			int index = layerPosition(me.getY());

			if (currentIndex != index)
			    {
				layerEntries.remove(current);
				layerEntries.insertElementAt(current, index);
				currentIndex = index;
				LayerEntries.this.doLayout();
				LayerEntries.this.repaint();
			    }
		    }
		    public void mouseReleased(MouseEvent me)
		    {			
			LayerControl lc = LayerControl.this;
			
			me = SwingUtilities.convertMouseEvent(me.getComponent(), me, LayerEntries.this); 
			int index = layerPosition(me.getY());
			LayerStyle s = current.getLayerStyle();
			if (index != layerEntries.size()-1-lc.lMan.getOrderOfLayer(s))
			    {		
				lc.lMan.setOrderOfLayer(s, layerEntries.size()-1-index);
				int slidepos = lc.layerSlider.getValue();
				fix();
				lc.layerSlider.setValue(slidepos);
			    }
		    }
		};
	}
	public int layerPosition(int x)
	{
	    int height = (getSize().height-25)/(layerEntries.size());
	    int index =  (x+height/2)/height;

	    if (index < 0)
		return 0;
	    else if (index >= layerEntries.size())
		return layerEntries.size()-1;
	    else
		return index;
	}
	    
	public void addLayerEntry(LayerStyle ls)
	{
	    LayerEntry entry = new LayerEntry(ls);
	    JLabel label = entry.getLabel();
	    label.addMouseMotionListener(mia);	    
	    label.addMouseListener(mia);
	    layerEntries.insertElementAt(entry, 0);
	    add(entry);
	}
		
	public void addBackgroundLayer()
	{
	    JCheckBox bg = new JCheckBox("Background", true);
	    bg.setEnabled(false);
	    add(bg);
	}

	public void selectTo(int range)
	{
	    for (int i = 0; i < layerEntries.size(); i++)
		((LayerEntry) layerEntries.elementAt(i)).setLayerVisible( i >= range);
	}
	
	public void setLayerChoosen(int nr)
	{
	    LayerManager lMan = LayerControl.this.lMan;
	    if (nr < 0 || nr >= layerEntries.size())
		lMan.setEditMapGroupStyle(null);
	    else
		setLayerChoosen((LayerEntry) layerEntries.elementAt(nr));
	}
	
	protected void  setLayerChoosen(LayerEntry entry)
	{
	    if (entry == null)
		return;
	    if (choosenLayerEntry != null)
		if (choosenLayerEntry == entry)
		    return;
		else
		    choosenLayerEntry.setLayerChoosen(false);

	    LayerManager lMan = LayerControl.this.lMan;
	    lMan.setEditMapGroupStyle(entry.getLayerStyle().getURI());
	    entry.setLayerChoosen(true);
	    choosenLayerEntry = entry;
	}
	
	public Vector getLayers()
	{
	    return layerEntries;
	}

	public void clear()
	{
	    removeAll();
	    layerEntries.clear();
	}
    }

    public class LayerEntry extends JPanel
    {
	LayerStyle ls;
	JLabel label;
	JCheckBox cb;
	public LayerEntry(LayerStyle style)
	    {
		super();
		ls = style;
		label = new JLabel(style.getURI());
		setLayerChoosen(false);
		cb = new JCheckBox();
		cb.setSelected(true);
		add(cb, BorderLayout.CENTER);
		add(label, BorderLayout.EAST);
		addListeners();
	    }

	public JLabel getLabel()
	{
	    return label;
	}

	void addListeners()
	{
	    ItemListener il;
	    il = new ItemListener() {
		    public void itemStateChanged(ItemEvent e)
		    {			
			LayerControl lc = LayerControl.this;
    
			if (!lc.commit)
			    return;
			
			lc.lock = true;
			if(e.getStateChange() == ItemEvent.SELECTED)
			    {
				lc.lMan.setLayerVisible(ls.getURI(), true);
				lc.controller.getMapScrollPane().getDisplayer().repaint();
			    }
			else if(e.getStateChange() == ItemEvent.DESELECTED)
			    {
				lc.lMan.setLayerVisible(ls.getURI(), false);
				lc.controller.getMapScrollPane().getDisplayer().repaint();
			    }
			lc.lock = false;
		    }};
	    cb.addItemListener(il);
	}	
	
	public void setLayerVisible(boolean bo)
	{
	    cb.setSelected(bo);
	}
	
	public void setLayerChoosen(boolean bo)
	{
	    if (bo)
		{
		    label.setForeground(Color.red);
		    setLayerVisible(bo);
		}
	    else
		label.setForeground(Color.black);
	}

	public LayerStyle getLayerStyle()
	{
	    return ls;
	}
    }

    public LayerControl(MapController controller)
    {
	this.controller = controller;
	commit = true;
	lock = false;

	controller.addPropertyChangeListener(this);
	frame = new JFrame();

	
	frame.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    frame.setVisible(false);
		}
	    });
	
	
	layerEntries = new LayerEntries();

	//FIXME: should be fetched from LayerControl.properties.
	create = new JButton("New Layer");
	create.setActionCommand("create");
	create.addActionListener(this);

	//	layers.setLayout(new GridLayout(0, 1));

	layerSlider = new JSlider();
	layerSlider.setOrientation(JSlider.VERTICAL);
	layerSlider.addChangeListener(this);
	layerSlider.setMajorTickSpacing(1);
	layerSlider.setMinorTickSpacing(1);
	//	layerSlider.setPaintLabels(true);
	layerSlider.setSnapToTicks(true);
	layerSlider.setPaintTicks(false);
	layerSlider.setInverted(true);

	fix();
	
	layerSlider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

	frame.getContentPane().add(layerSlider, BorderLayout.CENTER);
	frame.getContentPane().add(layerEntries, BorderLayout.EAST);
	frame.getContentPane().add(create, BorderLayout.SOUTH);
    }
    
    protected void fix()
    { 
	LayerManager lMapNew = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getLayerManager();
	if (lMan != lMapNew)
	    {
		if (lMan != null)
		    lMan.removeLayerListener(this);
		lMan = lMapNew;
		lMan.addLayerListener(this);
	    }

	layerEntries.clear();
	Enumeration en = lMan.getLayers().elements();
	while (en.hasMoreElements())
	    layerEntries.addLayerEntry((LayerStyle) en.nextElement());
	layerEntries.addBackgroundLayer();	


	layerSlider.setMinimum(0);
	layerSlider.setMaximum(layerEntries.getLayers().size());
	layerSlider.setValue(0);
	frame.pack();
    }

    public void show(boolean bo)
    {
	frame.setVisible(bo);
	frame.pack();
    }

    public void stateChanged(ChangeEvent e) 
    {
	lock = true;
	int nr = (int) layerSlider.getValue();
	
	layerEntries.selectTo(nr);

        if (!layerSlider.getValueIsAdjusting())
	    layerEntries.setLayerChoosen(nr);
	lock = false;
    }

  public void addNewLayer()
    {
	String name = JOptionPane.showInputDialog("Please type a name for the new layer");
	if (name != null)
	    {
		if (lMan.getLayer(name)!=null)
		    {
			ErrorMessage.showError("Layer error", "A layer with name "+name+
					       " already exists.", null, frame);
			return;
		    }
		ConceptMap cMap = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
		//FIXME: the tag should be fetched from some source-manager,
		//instead of taking the conceptmaps uri.
		lMan.createLayer(name, cMap.getURI(), cMap);
	    }
    }
  public void layerChange(LayerEvent le)
    {
	if (!lock)
	    fix();
    }
    public void propertyChange(PropertyChangeEvent e)
    {
	if (e.getPropertyName().equals(MapController.MAP_PROPERTY))
	    fix();
    }
    public void actionPerformed(ActionEvent ae)
    {
	if (ae.getActionCommand().equals("create"))
	    addNewLayer();
    }
}
