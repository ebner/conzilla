/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.style;

import java.awt.Color;

/**
 * @author matthias
 */
public class OverlayStyle {

    protected Color textColor = Color.BLACK;
    protected Color backgroundColor = Color.WHITE;
    protected Color foregroundColor = Color.BLACK;
    protected float lineWidth = 1;
    protected boolean marked = false;
    
    /**
     * @return Returns the backgroundColor.
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    /**
     * @param backgroundColor The backgroundColor to set.
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    /**
     * @return Returns the foregroundColor.
     */
    public Color getForegroundColor() {
        return foregroundColor;
    }
    /**
     * @param foregroundColor The foregroundColor to set.
     */
    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }
    /**
     * @return Returns the lineWidth.
     */
    public float getLineWidth() {
        return lineWidth;
    }
    /**
     * @param lineWidth The lineWidth to set.
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }
    /**
     * @return Returns the lineWidthModified.
     */
    public boolean isLineWidthModified() {
        return lineWidth != 1f;
    }
    /**
     * @return Returns the textColor.
     */
    public Color getTextColor() {
        return textColor;
    }
    /**
     * @param textColor The textColor to set.
     */
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }
    
    public void setMarked(boolean marked) {
        this.marked = marked;
    }
    
    public boolean isMarked() {
        return marked;
    }
}