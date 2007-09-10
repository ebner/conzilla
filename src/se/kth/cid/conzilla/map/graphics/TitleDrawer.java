/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map.graphics;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTextArea;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.properties.ColorTheme.Colors;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.util.AttributeEntryUtil;

public class TitleDrawer extends MapDrawer {
    MapTextArea title;
    AttributeEntry ae;

    static final int ew = 4;
    static final int eh = 4;
    static final int dx = 2;
    static final int dy = 2;

    Rectangle2D outerBb;
    Rectangle bb;
    //  Rectangle scaledbb;
    protected DrawerMapObject drawerMapObject;

    double scale;

    boolean settingTitle;

    JComponent editorLayer;

    boolean displayLanguageDiscrepancy;
    boolean foreignTitle;
    boolean visible;
    boolean hardvisible;
    CellRendererPane cellRendererPane;

    public TitleDrawer(
        DrawerMapObject drawerMapObject,
        JComponent editorLayer) {
        super(drawerMapObject.getDisplayer());
        this.drawerMapObject = drawerMapObject;
        this.editorLayer = editorLayer;

        settingTitle = false;
        displayLanguageDiscrepancy = false;

        scale = drawerMapObject.getDisplayer().getScale();

        cellRendererPane = new CellRendererPane();

        title = new MapTextArea(1.0);

        title.setEditable(false);
        visible = true;
        hardvisible = true;
    }

    public TitleDrawer() {
        super();
        
        
    }

    public JTextArea getEditableTextComponent() {
        return title;
    }

    public Rectangle getTitleBounds() {
        return bb;
    }
    public void setTitleVisible(boolean bo) {
        hardvisible = bo;
        if (!hardvisible)
            cellRendererPane.remove(title);
    }
    public Dimension getPreferredSize() {
        Dimension dim1 = title.getPreferredSize();
        //	Dimension dim2 = data.preferredSize();
        //	return dim1.width > dim2.width ? new Dimension(dim1.width, dim1.height + dim2.height) :
        //	    new Dimension(dim2.width, dim1.height + dim2.height);
        return dim1;
    }

    public void setEditable(boolean editable, MapEvent m) {
        if (editable == title.isEditable())
            return;

        if (editable && drawerMapObject.getConcept() == null)
            return;


        if (editable) {
            updateTitle();

/*            Caret c = title.getCaret();
            if (m == null) {
                c.setDot(0);
                c.moveDot(title.getText().length());
            } else {
                c.setDot(
                    title.viewToModel(
                        SwingUtilities.convertPoint(
                            editorLayer,
                            m.mouseEvent.getX(),
                            m.mouseEvent.getY(),
                            title)));
            }*/
            resizeToMax();
            title.requestFocus();
        } else {
            resize();
            setTitle();
        }
        
        title.setEditable(editable);
    }

    public boolean getErrorState() {
        return drawerMapObject.getErrorState();
    }

    public void setDisplayLanguageDiscrepancy(boolean b) {
        displayLanguageDiscrepancy = b;
        drawerMapObject.colorUpdate();
    }

    public void colorUpdate(Mark mark) {
		Mark myMark = getMark(mark);
		if (displayLanguageDiscrepancy && foreignTitle)
			title.setColor((new Mark(Colors.FOREGROUND, null, null)).getTextColor());
		else
			title.setColor(myMark.getTextColor());
	}

    public void updateTitle() {
        if (settingTitle)
            return;

        title.setText(fetchString());
    }
    
    protected String fetchString() {
        if (drawerMapObject.getConcept() != null) {
            String title = AttributeEntryUtil.getTitleAsString(drawerMapObject.getConcept());
            return title != null ? title : "";
        }
        return "";
    }

    public void updateBox(Rectangle2D re) {
        outerBb = re;
        fixVisibility();
        resize();
    }
    
    protected int getVerticalAnchor() {
    	return drawerMapObject.getDrawerLayout().getVerticalTextAnchor();
    }
    
    protected int getHorizontalAnchor() {
    	return drawerMapObject.getDrawerLayout().getHorisontalTextAnchor();
    }
    
    public Rectangle computeBox() {
        if (outerBb == null) {
            return null;
        }
        
        int horisontalAnchor = getHorizontalAnchor();
        int verticalAnchor = getVerticalAnchor();
        title.setLineWrap(false);
        Dimension preferredSize = title.getPreferredSize();
        title.setLineWrap(true);
        
        int x = 0, y = 0, width, height;
        
        if (preferredSize.width < (outerBb.getWidth() - ew)) {
            //The text are allowed to take up the extra space to the right of the box.
            //This avoids (in most cases) the text to be cut of to early 
            //(due to the nonlinear scaling of some fonts in
            //the affine translation in the graphics object).
            width = preferredSize.width; //+ (int) ((re.getWidth() - preferredSize.width + ew) / 2);
            switch (horisontalAnchor) {
            case ConceptLayout.WEST:
                x = (int) (dx + outerBb.getX());
            break;
                case ConceptLayout.CENTER:
                    x =  (int) (((outerBb.getWidth() - preferredSize.width - ew) / 2)
                            + dx
                            + outerBb.getX());
                break;
                case ConceptLayout.EAST:
                    x = (int) ((outerBb.getWidth() - preferredSize.width - ew)
                            + dx
                            + outerBb.getX());
                break;
            }
        } else {
            width = (int) outerBb.getWidth() - ew;
            title.setSize(width, 1);
            preferredSize = title.getPreferredSize();
            x = (int) outerBb.getX() + dx;
        }
        if (preferredSize.height < (outerBb.getHeight() - eh)) {
            //The text are allowed to take up the extra space to the bottom of the box.
            //This avoids (in most cases) the text to be cut of to early 
            //(due to the nonlinear scaling of some fonts in 
            //the affine translation in the graphics object).
            height = preferredSize.height + (int) ((outerBb.getHeight() - preferredSize.height - eh) / 2);
            switch (verticalAnchor) {
            case ConceptLayout.NORTH:
                y = (int) (dy + outerBb.getY());
            break;
            case ConceptLayout.CENTER:
                y = (int) (((outerBb.getHeight() - preferredSize.height - eh) / 2)
                        + dy
                        + outerBb.getY());
            break;
            case ConceptLayout.SOUTH:
                y = (int) ((outerBb.getHeight() - preferredSize.height - eh)
                        + dy
                        + outerBb.getY());
            break;
            }

        } else {
            height = (int) outerBb.getHeight() - eh;
            y = (int) outerBb.getY() + dy;
        }

        return new Rectangle(x, y, width, height);
    }
    
    protected void fixVisibility() {
        if (visible != drawerMapObject.getDrawerLayout().getBodyVisible()) {
            visible = !visible;
            if (!visible)
                setEditable(false, null);
            else
                resize();
        }
    }

    public void setScale(double scale) {
        this.scale = scale;
        title.setScale(scale);
        resize();
    }
    
    public void resizeToMax() {
        bb = new Rectangle((int) outerBb.getX() + dx, 
                (int) outerBb.getY(), 
                (int) outerBb.getWidth() - ew, 
                (int) outerBb.getHeight() - eh);
        title.setSize((int) (bb.width*scale),(int) (bb.height*scale));
    }

    
    void resize() {
        bb = computeBox();
        if (bb == null) {
            return;
        }
        title.setSize((int) (bb.width*scale),(int) (bb.height*scale));
    }

    public void setTitle() {
        if (drawerMapObject.getConcept() == null)
            return;

        settingTitle = true;
        AttributeEntry ae = AttributeEntryUtil.getTitle(drawerMapObject.getConcept());

        if (ae == null) {
            AttributeEntryUtil.newTitle(drawerMapObject.getConcept(), title.getText());
//            drawerMapObject.getConcept().addAttributeEntry(CV.title, title.getText());
        } else if (!ae.getValue().equals(title.getText())) {
            if (title.getText().length() == 0) {
                drawerMapObject.getConcept().removeAttributeEntry(ae);
            } else {
                ae.setValueObject(title.getText());
            }
        }

        settingTitle = false;
        updateTitle();
    }
    public boolean didHit(MapEvent m) {
        if (bb == null)
            return false;
        return bb.contains(m.mapX, m.mapY);
    }

    void doPaint(Graphics2D g, Mark mark) {
        if (bb == null)
            return;

        title.setColor(mark.getForegroundColor());

        AffineTransform t = g.getTransform();
        AffineTransform t2 = new AffineTransform();
        t2.setToTranslation(t.getTranslateX(),t.getTranslateY());
        g.setTransform(t2);
        if (visible && hardvisible) {
            cellRendererPane.paintComponent(
                g,
                title,
                editorLayer,
                (int) (bb.x*scale),
                (int) (bb.y*scale),
                (int) (bb.width*scale),
                (int) (bb.height*scale));
        }
        g.setTransform(t);
    }
}