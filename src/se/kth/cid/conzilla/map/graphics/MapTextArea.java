/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JTextArea;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;

public class MapTextArea extends JTextArea {
    Font origFont;

    Vector mouseListeners;

    Vector mouseMotionListeners;

    Color saveColor = Color.black;

    boolean noMoreUIs = false;

    AffineTransform transform;

    private double scale;

    void maybeMakeListeners() {
        if (mouseListeners == null) {
            mouseListeners = new Vector();
            mouseMotionListeners = new Vector();
        }
    }

    public MapTextArea(double fontScale) {
        noMoreUIs = true;
        maybeMakeListeners();

        //Make sure we catch some extra clicks...
        //      setBorder(new EmptyBorder(0, 10, 0, 10));

        int bl = getCaret().getBlinkRate();
        setCaret(new DefaultCaret() {
            protected void adjustVisibility(Rectangle nloc) {
            }
        });
        getCaret().setBlinkRate(bl);
        origFont = new Font("Lucida Sans", Font.PLAIN, 12);
        setFont(origFont);

        setAutoscrolls(false);
        setWrapStyleWord(true);
        setLineWrap(true);
        setOpaque(false);
        setColumns(0);

    }

    public void updateUI() {
        if (!noMoreUIs)
            super.updateUI();
    }

    public void addMouseMotionListener(MouseMotionListener l) {
        maybeMakeListeners();

        if (isEditable())
            super.addMouseMotionListener(l);

        mouseMotionListeners.addElement(l);
    }

    public void addMouseListener(MouseListener l) {
        maybeMakeListeners();

        if (isEditable())
            super.addMouseListener(l);

        mouseListeners.addElement(l);
    }

    public void removeMouseMotionListener(MouseMotionListener l) {
        if (isEditable())
            super.removeMouseMotionListener(l);

        mouseMotionListeners.remove(l);
    }

    public void removeMouseListener(MouseListener l) {
        if (isEditable())
            super.removeMouseListener(l);

        mouseListeners.remove(l);
    }

    public void setEditable(boolean editable) {
        if (isEditable() == editable)
            return;

        super.setEditable(editable);

        Caret c = getCaret();

        // setEditable called from JTextComponent.<init> ignored here.
        if (c == null)
            return;

        c.setVisible(editable);
        c.setSelectionVisible(editable);

        setEnabled(editable);

        if (editable) {
            saveColor = getForeground();
            doSetColor(Color.gray);
        } else
            doSetColor(saveColor);

        Enumeration en = mouseMotionListeners.elements();
        while (en.hasMoreElements())
            if (editable) {
                super.addMouseMotionListener((MouseMotionListener) en.nextElement());
            } else {
                super.removeMouseMotionListener((MouseMotionListener) en.nextElement());
            }

        en = mouseListeners.elements();
        while (en.hasMoreElements())
            if (editable) {
                super.addMouseListener((MouseListener) en.nextElement());
            } else {
                super.removeMouseListener((MouseListener) en.nextElement());
            }

        repaint();
    }

    public void setColor(Color c) {
        saveColor = c;

        if (!isEditable())
            doSetColor(c);
    }

    void doSetColor(Color c) {
        setDisabledTextColor(c);
        setForeground(c);
    }
}