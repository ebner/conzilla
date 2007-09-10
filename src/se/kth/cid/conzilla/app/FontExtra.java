/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.app;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.menu.MenuFactory;
import se.kth.cid.conzilla.properties.ConzillaResourceManager;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsMenu;

public class FontExtra implements Extra {
	ConzillaKit kit;

	public FontExtra() {
	}

	public String getName() {
		return "FontExtra";
	}

	public boolean initExtra(ConzillaKit kit) {
		this.kit = kit;
		return true;
	}

	public void exitExtra() {
	}

	public void addExtraFeatures(MapController c) {
	}

	public void refreshExtra() {
	}

	public boolean saveExtra() {
		return true;
	}

	public void extendMenu(ToolsMenu menu, final MapController c) {
		if (menu.getName().equals(MenuFactory.SETTINGS_MENU)) {
			Tool t = new Tool("FONTS", FontExtra.class.getName()) {
				public void actionPerformed(ActionEvent ae) {
					popupFontDialog();
				}
			};
			t.setIcon(Images.getImageIcon(Images.ICON_SETTINGS_FONT_SIZE));
			menu.addTool(t, 700);
		}
	}

	void popupFontDialog() {
		String[] sizess = { "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19" };
		JComboBox sizes = new JComboBox(sizess);
		sizes.setSelectedIndex(Integer.parseInt(ConfigurationManager.getConfiguration().getString(
				Settings.CONZILLA_FONT_SIZE, "10")) - 7);

		final JDialog d = new JDialog((Frame) null, ConzillaResourceManager.getDefaultManager().getString(
				FontExtra.class.getName(), "SELECT_FONT"), true);

		JPanel cp = new JPanel();

		cp.setBorder(new EmptyBorder(20, 20, 20, 20));
		cp.setLayout(new BorderLayout());
		JLabel fs = new JLabel(ConzillaResourceManager.getDefaultManager().getString(FontExtra.class.getName(),
				"FONT_SIZE")
				+ ": ");
		JLabel ex1 = new JLabel(ConzillaResourceManager.getDefaultManager().getString(FontExtra.class.getName(),
				"EXAMPLE_BUTTON")
				+ ": ");
		JLabel ex2 = new JLabel(ConzillaResourceManager.getDefaultManager().getString(FontExtra.class.getName(),
				"EXAMPLE_TEXT")
				+ ": ");
		JLabel warning = new JLabel(ConzillaResourceManager.getDefaultManager().getString(FontExtra.class.getName(),
				"WARNING"));

		final JButton but = new JButton("ABCabc");
		final JTextArea text = new JTextArea("ABCabc");

		JPanel labelPane = new JPanel();
		labelPane.setLayout(new GridLayout(0, 1));
		labelPane.add(fs);
		labelPane.add(ex1);
		labelPane.add(ex2);
		JPanel fieldPane = new JPanel();
		fieldPane.setLayout(new GridLayout(0, 1));
		fieldPane.add(sizes);
		fieldPane.add(but);
		fieldPane.add(text);

		cp.add(labelPane, BorderLayout.CENTER);
		cp.add(fieldPane, BorderLayout.EAST);
		cp.add(warning, BorderLayout.SOUTH);

		final String[] font = new String[1];

		sizes.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					font[0] = (String) e.getItem();
					int fs = Integer.parseInt(font[0]);
					Font f = new Font("Lucida Sans", Font.BOLD, fs);
					Font f2 = f.deriveFont(Font.PLAIN, (float) (f.getSize2D() * 1.2));
					but.setFont(f);
					text.setFont(f2);
				}
			}
		});

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(cp, BorderLayout.CENTER);

		JPanel buts = new JPanel();
		buts.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton ok = new JButton();
		ConzillaResourceManager.getDefaultManager().customizeButton(ok, FontExtra.class.getName(), "OK");
		JButton cancel = new JButton();
		ConzillaResourceManager.getDefaultManager().customizeButton(cancel, FontExtra.class.getName(), "CANCEL");
		buts.add(ok);
		buts.add(cancel);

		contentPane.add(buts, BorderLayout.SOUTH);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				d.setVisible(false);
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				font[0] = null;
				d.setVisible(false);
			}
		});
		d.setContentPane(contentPane);
		d.pack();
		d.setVisible(true);
		d.dispose();
		if (font[0] != null) {
			kit.getConzilla().setGlobalFontSize(Integer.parseInt(font[0]));
		}
	}
}
