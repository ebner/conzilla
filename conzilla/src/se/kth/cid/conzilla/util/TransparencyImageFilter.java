/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

/**
 * Filters a Image so that all transparent parts are replaced with a fixed
 * color.
 * 
 * @author matthias
 */
public class TransparencyImageFilter extends RGBImageFilter {

    /**
     * Creates a filtered image.
     */
    public static Image createFilteredImage(Image i, Color col) {
        TransparencyImageFilter filter = new TransparencyImageFilter(col);
        ImageProducer prod = new FilteredImageSource(i.getSource(), filter);
        Image grayImage = Toolkit.getDefaultToolkit().createImage(prod);
        return grayImage;
    }

    int color;

    public TransparencyImageFilter(Color col) {
        color = col.getRGB();
    }

    /**
     * If pixel is transparent return the fixed color, otherwise return the
     * pixels color.
     */
    public int filterRGB(int x, int y, int rgb) {
        if ((rgb & 0xff000000) == 0) {
            return color;
        }
        return rgb;
    }
}