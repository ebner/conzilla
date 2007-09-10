/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map.graphics;

import java.awt.Color;

import se.kth.cid.conzilla.properties.ColorTheme;
import se.kth.cid.style.OverlayStyle;

public class Mark extends OverlayStyle {

    public String textProp;

    public String backgroundProp;

    public String foregroundProp;

    private boolean backgroundLighterCalculation = false;

    public Mark(Color fore, Color back, Color text) {
        if (text != null) {
            textColor = text;
        } else {
        	textProp = ColorTheme.Colors.FOREGROUND;
        }

        if (fore != null) {
            foregroundColor = fore;
        } else {
            foregroundProp = ColorTheme.Colors.FOREGROUND;
        }

        if (back != null)
            backgroundColor = back;
        else if (fore != null) {
            backgroundLighterCalculation = true;
            backgroundColor = ColorTheme.getBrighterColor(foregroundColor);
        } else {
            backgroundColor = ColorTheme.getColor(ColorTheme.Colors.CONCEPT_BACKGROUND);
        }
        

        update();
    }

    public Mark(String fore, String back, String text) {
        if (text != null) {
            textProp = text;
        } else {
        	textProp = ColorTheme.Colors.FOREGROUND;
        }

        if (fore != null) {
            foregroundProp = fore;
        } else {
            foregroundProp = ColorTheme.Colors.FOREGROUND;
        }

        if (back != null)
            backgroundProp = back;
        else if (fore != null) {
            backgroundProp = null;
            backgroundLighterCalculation = true;
        } else {
            backgroundProp = ColorTheme.Colors.CONCEPT_BACKGROUND;
        }
        update();
    }

    public void update() {
        if (foregroundProp != null) {
            foregroundColor = ColorTheme.getColor(foregroundProp);
        }

        if (backgroundProp != null) {
            backgroundColor = ColorTheme.getColor(backgroundProp);
        } else if (foregroundProp != null && backgroundLighterCalculation) {
            backgroundColor = ColorTheme.getBrighterColor(foregroundColor);
        }

        if (textProp != null) {
            textColor = ColorTheme.getColor(textProp);
        }
    }

}