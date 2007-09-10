/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.map.MapDisplayer;
import se.kth.cid.conzilla.util.ResourceUtil;
import se.kth.cid.graphics.BoxDraw;
import se.kth.cid.graphics.HeadDraw;
import se.kth.cid.style.BoxStyle;
import se.kth.cid.style.HeadStyle;
import se.kth.cid.style.LineStyle;
import se.kth.cid.style.OverlayStyle;
import se.kth.cid.style.StyleManager;


public class TypeSummaryComponent extends JComponent {
    public static class TypeSummaryMenuItem extends JMenuItem {
        TypeSummaryComponent box;
        public TypeSummaryMenuItem(StyleManager sm, Concept typeConcept) {
            super();
            box = new TypeSummaryComponent(sm, typeConcept);
            add(box);
            setPreferredSize(box.getPreferredSize());
        }
    }

    String title;
    JTextArea titleC;
    CellRendererPane cellRendererPane;
    OverlayStyle ostyle;
    BoxStyle boxStyle = new BoxStyle();
    LineStyle lineStyle;
    HeadStyle headStyle;
    Rectangle innerBox;
    Rectangle outerBox;
    Rectangle outerBorderedBox;
    Shape box;
    Shape head;
    Line2D line;
    boolean arrow = false;

    public TypeSummaryComponent(StyleManager styleManager, Concept typeConcept) {
        String rdftype = typeConcept.getURI();
        arrow = ResourceUtil.isResourceOfClassProperty(typeConcept);

        //Let the stylemanagement get a chance to override:
        List stack = styleManager.getStylesFromClass(rdftype);
        boxStyle.fetchStyle(styleManager, stack, arrow);
        if (arrow) {
            lineStyle = new LineStyle();
            headStyle = new HeadStyle();
            lineStyle.fetchStyle(styleManager, stack);
            headStyle.fetchStyle(styleManager, stack);
        }

        title = discoverTitleFromType(rdftype);

        int padX = 5;
        int padY = 5;
        int lineExtent = 10;
        int distanceBoxAndLine = 3;
        int summaryWidth = 120;

        ostyle = new OverlayStyle();

        Rectangle2D inner;
        Rectangle2D innerBordered;
        Rectangle2D outer;
        Rectangle2D outerBordered;

        titleC = new JTextArea();
        titleC.setOpaque(false);
        titleC.setColumns(0);

        titleC.setText(title);
        Dimension d = titleC.getPreferredSize();

        inner = new Rectangle(d);
        innerBordered = BoxDraw.calculateBorderBox(inner, boxStyle, ostyle);
        outer = BoxDraw.calculateOuterFromInnerBox(boxStyle, innerBordered);
        outerBordered = BoxDraw.calculateBorderBox(outer, boxStyle, ostyle);
        int outerBorderedWidth = (int) outerBordered.getWidth();
        if (arrow) {
            int width = summaryWidth - 2 * lineExtent;
            outerBorderedWidth = outerBorderedWidth > width ? outerBorderedWidth : width;
        } else {
            outerBorderedWidth =
                outerBorderedWidth > summaryWidth ? outerBorderedWidth : summaryWidth;
        }

        outerBordered =
            new Rectangle.Double(
                arrow ? lineExtent + padX : padX,
                padY,
                outerBorderedWidth,
                outerBordered.getHeight());
        outer =
            BoxDraw.calculateUnBorderBox(outerBordered, boxStyle, ostyle);
        innerBordered = BoxDraw.calculateInnerFromOuterBox(boxStyle, outer);
        inner =
            BoxDraw.calculateUnBorderBox(innerBordered, boxStyle, ostyle);

        box = BoxDraw.constructBox(boxStyle, outer.getBounds());
        innerBox = inner.getBounds();
        outerBox = outer.getBounds();
        outerBorderedBox = outerBordered.getBounds();

        if (arrow) {
            Point[] points = new Point[2];
            int liney = outerBorderedBox.height + padY + distanceBoxAndLine;
            points[0] = new Point(padX, liney);
            points[1] =
                new Point(
                    padX + outerBorderedBox.width + lineExtent * 2,
                    liney);
            line = new Line2D.Float(points[0], points[1]);
            head = HeadDraw.constructHead(headStyle);

            Point2D arrowTip = new Point2D.Double(1, 0.5);

            AffineTransform transform =
                AffineTransform.getTranslateInstance(
                    points[1].x,
                    points[1].y);
            //transform.rotate(Math.atan2(yLen, xLen));
            transform.scale(headStyle.getLength() * 3, headStyle.getWidth() * 3);
            transform.translate(-arrowTip.getX(), -arrowTip.getY());
            head = transform.createTransformedShape(head);
        }

        cellRendererPane = new CellRendererPane();
        Dimension dim = outerBordered.getBounds().getSize();
        if (arrow) {
            dim =
                new Dimension(
                    dim.width + 2 * padX + 1 + lineExtent * 2,
                    dim.height
                        + 2 * padY
                        + 1
                        + (int) (3 * headStyle.getWidth() * 0.5)
                        + distanceBoxAndLine);
        } else {
            dim =
                new Dimension(
                    dim.width + 2 * padX + 1,
                    dim.height + 2 * padY + 1);
        }
        setSize(dim);
        setMinimumSize(dim);
        setPreferredSize(dim);
        setVisible(true);
        setLocation(0, 0);
    }

    protected String discoverTitleFromType(String rdftype) {
        //Discover the shape coded in the type.
        if (rdftype == null) {
            rdftype = "Error in defaultMenu!!!!";
        }

        if (rdftype.indexOf("/") > 0) {
            rdftype = rdftype.substring(rdftype.lastIndexOf('/') + 1);
        }

        if (rdftype.indexOf("#") > 0) {
            rdftype = rdftype.substring(rdftype.lastIndexOf('#') + 1);
        }
        return rdftype;
    }

    public void paintComponent(Graphics g) {

        if (g instanceof Graphics2D) {
            Graphics2D g2D = (Graphics2D) g;
            MapDisplayer.setRenderingHints(g2D);
            BoxDraw.doPaint(g2D, box, innerBox, outerBox, boxStyle, ostyle);

            if (arrow) {
                Stroke s = g2D.getStroke();
                g2D.setStroke(lineStyle.getStroke());
                g2D.draw(line);
                g2D.setStroke(headStyle.getStroke());

                if (head != null) {
                    if (headStyle.isFilled() && headStyle.isClearFill()) {
                        g2D.setColor(ostyle.getForegroundColor());
                        g2D.fill(head);
                    } else if (headStyle.isClearFill()) {
                        g2D.setColor(Color.LIGHT_GRAY);
                        g2D.fill(head);
                        g.setColor(ostyle.getForegroundColor());
                        g2D.draw(head);
                    } else {
                        g2D.setColor(ostyle.getForegroundColor());
                        g2D.draw(head);
                    }
                    g2D.setStroke(s);
                }
            }

            cellRendererPane.paintComponent(
                g,
                titleC,
                this,
                (int) innerBox.getX(),
                (int) innerBox.getY(),
                (int) innerBox.getWidth(),
                (int) innerBox.getHeight());

        }
    }
}