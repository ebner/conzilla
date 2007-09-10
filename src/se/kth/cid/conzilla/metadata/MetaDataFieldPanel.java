/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.metadata;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;


public class MetaDataFieldPanel extends JPanel
{

  public MetaDataFieldPanel(String title, Component metaDataEditor)
    {
      
      /* // The old behaviour
	Border compoundBorder = BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
	  BorderFactory.createLoweredBevelBorder());

      Border titled = BorderFactory.createTitledBorder(compoundBorder, title,
						       TitledBorder.ABOVE_TOP,
						       TitledBorder.LEFT, null, null);
      
      setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 0, 5, 0), titled));

      */

      Border titled = BorderFactory.createTitledBorder(new EmptyBorder(0, 0, 5, 0), title,
						       TitledBorder.ABOVE_TOP,
						       TitledBorder.LEFT, null, null);
      
      setBorder(BorderFactory.createCompoundBorder(titled, new EmptyBorder(0, 20, 0, 0)));

      setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

      add(metaDataEditor);
    }

  JComponent getMetaDataComponent()
    {
      return (JComponent) getComponents()[0];
    }
}
