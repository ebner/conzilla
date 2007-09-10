/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

/**
 * This class is used to bring up long error messages.
 * 
 * @author Mikael Nilsson
 * @version $Revision$
 */
public class ErrorMessage {
	
	static final int ROW_LENGTH = 80;

	private ErrorMessage() {
	}

	/**
	 * Shows an error dialog with the specified message.
	 * 
	 * @param title
	 *            the title of the dialog.
	 * @param errorMessage
	 *            the error message to show (helping the user).
	 * @param ex
	 *            the exception causing the error.
	 * @param parent
	 *            the component that is the parent of the message.
	 */
	public static void showError(String title, String errorMessage, Exception ex, Component parent) {
		Object[] options = null;
		int okOpt = 0;
		if (ex != null) {
			options = new Object[] { "Details", "Ok" };
			okOpt = 1;
		} else {
			options = new Object[] { "Ok" };
			okOpt = 0;
		}

		int result = JOptionPane.showOptionDialog(parent, breakString(title + ":\n\n" + errorMessage), title,
				JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[okOpt]);

		if (result == okOpt || result == JOptionPane.CLOSED_OPTION)
			return;

		detailDialog(title, errorMessage, ex, parent);
	}

	static void detailDialog(String title, String errorMessage, Exception ex, Component parent) {
		final JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(parent), title, true);

		dialog.getContentPane().setLayout(new BorderLayout());

		if (ex != null) {
			JTabbedPane tabPane = new JTabbedPane();
			tabPane.addTab("Description", makeDescription(title, errorMessage, ex));
			tabPane.addTab("Exception", makeExceptionMessage(title, errorMessage, ex));
			dialog.getContentPane().add(tabPane, BorderLayout.CENTER);
		} else {
			dialog.getContentPane().add(makeDescription(title, errorMessage, null), BorderLayout.CENTER);
		}

		JButton close = new JButton("Close");

		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});

		Box box = new Box(BoxLayout.X_AXIS);

		box.add(Box.createHorizontalGlue());

		box.add(close);

		dialog.getContentPane().add(box, BorderLayout.SOUTH);

		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}

	static JComponent makeDescription(String title, String errorMessage, Exception ex) {
		JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setLineWrap(false);

		area.setText(title + ":\n\n" + errorMessage + "\n\n" + "Details:\n\n " + ex.getMessage());
		JScrollPane pane = new JScrollPane(area);

		pane.setMinimumSize(new Dimension(400, 150));
		pane.setPreferredSize(new Dimension(400, 150));
		return pane;
	}

	static JComponent makeExceptionMessage(String title, String errorMessage, Exception ex) {
		JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setLineWrap(false);

		StringWriter writer = new StringWriter(1024);

		ex.printStackTrace(new PrintWriter(writer));

		area.setText(writer.toString());

		JScrollPane pane = new JScrollPane(area);

		pane.setMinimumSize(new Dimension(400, 150));
		pane.setPreferredSize(new Dimension(400, 150));
		return pane;
	}

	static String breakString(String s) {
		StringBuffer out = new StringBuffer();

		int start = 0;
		int next = 0;
		while (start < s.length()) {
			next = s.indexOf('\n', start);
			if (next == -1) {
				next = s.length();
			}

			String line = s.substring(start, next);

			int l = line.length();

			for (int i = 0; i <= l; i += ROW_LENGTH) {
				out.append(line.substring(i, Math.min(i + ROW_LENGTH, l)));
				out.append('\n');
			}
			start = next + 1;
		}
		return out.toString();
	}

}