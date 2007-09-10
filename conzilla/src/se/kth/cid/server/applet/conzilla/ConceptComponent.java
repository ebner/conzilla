/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.applet.conzilla;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseListener;

import javax.swing.CellRendererPane;
import javax.swing.JTextArea;

import se.kth.cid.graphics.BoxDraw;
import se.kth.cid.style.BoxStyle;
import se.kth.cid.style.LineStyle;
import se.kth.cid.style.OverlayStyle;

/**
 * @author enok
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ConceptComponent {

    public static Color HIGHLIGHT = new Color(153, 153, 255);
    Concept c;
    JTextArea area;
    boolean highlight;
    CellRendererPane cellRendererPane;

    Rectangle outerBox;
    Rectangle innerBox;
    Rectangle hitBox;
    Shape box;
    BoxStyle style;
    OverlayStyle ostyle;
    private int areaXpos;
    private int areaYpos;
    private int areaWidth;
    private int areaHeight;

    public ConceptComponent(Concept c) {
        this.c = c;

        style = new BoxStyle();
        LineStyle hlineStyle=  new LineStyle(3.f, "Continous");
        style.setBorderLineStyle(hlineStyle);

//       style.setThickness(10.0f);
        style.setTypeStr(c.getType());
        ostyle = new OverlayStyle();

        outerBox =
            new Rectangle(
                c.getXpos(),
                c.getYpos(),
                c.getWidth(),
                c.getHeight());
        hitBox = BoxDraw.discoverHitBox(outerBox);
        innerBox = BoxDraw.calculateInnerFromOuterBox(style, outerBox);

        //Update the box's  shape.
        box = BoxDraw.constructBox(style, outerBox);

        cellRendererPane = new CellRendererPane();
        area = new JTextArea(c.getTitle());
        Dimension d = area.getPreferredSize();
        area.setSize(area.getPreferredSize());
        areaXpos = 2 + (int) ((innerBox.width - d.width) / 2.0);
        areaYpos = 2 + (int) ((innerBox.height - d.height) / 2.0);
        areaWidth = d.width;
        areaHeight = d.height;
        if (areaXpos < 2) {
            areaXpos = 2;
            areaWidth = innerBox.width;
        }
        if (areaYpos < 2) {
            areaYpos = 2;
            areaHeight = innerBox.height;
        }
        area.setLocation(areaXpos, areaYpos);
        area.setSize(areaWidth, areaHeight);
    }

    public Concept getConcept() {
        return c;
    }

    public void highlight(boolean b) {
        highlight = b;
    }

    public void paintComponent(Graphics g) {
        if (g instanceof Graphics2D) {
        }

        Graphics2D g2d = (Graphics2D) g;

        g.setColor(Color.WHITE);
        if (highlight) {
            ostyle.setLineWidth(5.f);
            ostyle.setForegroundColor(HIGHLIGHT);
            BoxDraw.doPaint((Graphics2D) g, box, innerBox, outerBox, style, ostyle);
            area.setForeground(HIGHLIGHT);
            g.setColor(HIGHLIGHT);
        } else {
            ostyle.setLineWidth(1f);
            ostyle.setForegroundColor(Color.BLACK);
            BoxDraw.doPaint((Graphics2D) g, box, innerBox, outerBox, style, ostyle);
            area.setForeground(Color.BLACK);
            g.setColor(Color.BLACK);
        }

        area.paint(
            g.create(
                areaXpos + c.getXpos(),
                areaYpos + c.getYpos(),
                areaWidth,
                areaHeight));
        /*		cellRendererPane.paintComponent(
        			g,
        			area,
        			c,
        			areaXpos,
        			areaYpos,
        			areaWidth,
        			areaHeight);*/
    }

    /**
     * @param cml
     */
    public void addConceptListener(MouseListener cml) {
        //		addMouseListener(cml);
        area.addMouseListener(cml);
    }

}
