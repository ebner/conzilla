/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

/**
 * Allows you to simply change the language of Conzilla.
 * 
 * @author matthias
 */
public class LocaleChooser extends JComboBox{
    private LocaleManager localeManager;

    public LocaleChooser() {
        this.localeManager = LocaleManager.getLocaleManager();
        setEditable(false);
        updateAvailableLanguages();
        
        setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                    JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                    return super.getListCellRendererComponent(list,
                            ((Locale) value).getDisplayName(Locale.getDefault()),
                            index,
                            isSelected,
                            cellHasFocus);
                }
        });
        addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                localeManager.setDefaultLocale((Locale) getSelectedItem());
            }
        });
        LocaleManager.getLocaleManager().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateAvailableLanguages();
			}
        });
    }

    private void updateAvailableLanguages() {
        Locale[] locales = localeManager.getLocales();
        setModel(new DefaultComboBoxModel(locales));
        setSelectedItem(Locale.getDefault());
        Dimension d = getPreferredSize();
        setMaximumSize(new Dimension(d.width + 40, d.height));
    }

}