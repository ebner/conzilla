/* $Id$ */
/*
  This file is part of the Conzilla browser, designed for
  the Garden of Knowledge project.
  Copyright (C) 1999  CID (http://www.nada.kth.se/cid)
  
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/


package se.kth.cid.conzilla.map.graphics;

import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conzilla.map.*;
import java.awt.*;

public class Mark 
{
    public Color textColor;
    public Color backgroundColor;
    public Color foregroundColor;
    public float lineWidth=1;
    private boolean lineWidthModified=false;

    public String textProp;
    public String backgroundProp;
    public String foregroundProp;

    private boolean backgroundLighterCalculation=false;
    
    public Mark(Color fore, Color back, Color text)
    {
	if (text!=null)
	    textColor = text;
	else 
	    textProp = MapDisplayer.COLOR_TEXT;

	if (fore!=null)
	    foregroundColor = fore;
	else
	    foregroundProp = MapDisplayer.COLOR_FOREGROUND;
	
	if (back!=null)
	    backgroundColor = back;
	else if (fore != null)
	    {
		backgroundLighterCalculation=true;
		backgroundColor = getLighterColor(foregroundColor);
	    }
	else
	    backgroundColor = GlobalConfig.getGlobalConfig().getColor(MapDisplayer.COLOR_NEURON_BACKGROUND);

	update();
    }
    
    public Mark(String fore, String back, String text)
    {
	if (text!=null)
	    textProp = text;
	else 
	    textProp = MapDisplayer.COLOR_TEXT;

	if (fore!=null)
	    foregroundProp = fore;
	else
	    foregroundProp = MapDisplayer.COLOR_FOREGROUND;

	if (back!=null)
	    backgroundProp = back;
	else if (fore != null)
	    {
		backgroundProp = null;
		backgroundLighterCalculation=true;
	    }
	else
	    backgroundProp = MapDisplayer.COLOR_NEURON_BACKGROUND;
	update();
    }

    public void update()
    {
	if (foregroundProp!=null)
	    foregroundColor = GlobalConfig.getGlobalConfig().getColor(foregroundProp);
	
	if (backgroundProp!=null)
	    backgroundColor = GlobalConfig.getGlobalConfig().getColor(backgroundProp);
	else if (foregroundProp!=null && backgroundLighterCalculation)
	    backgroundColor = getLighterColor(foregroundColor); 
	
	if (textProp!=null)
	    textColor = GlobalConfig.getGlobalConfig().getColor(textProp);
    }
    
    public void setLineWidth(float width)
    {
	lineWidth=width;
	lineWidthModified=true;
    }
    public boolean isLineWidthModified()
    {
	return lineWidthModified;
    }

    static Color getLighterColor(Color col)
    {
	int red = col.getRed();
	red += (255 - red)/4 * 3;
	int green = col.getGreen();
	green += (255 - green) /4 * 3;
	int blue = col.getBlue();
	blue += (255 - blue) /4 * 3;
	return new Color(red, green, blue);
    }
}
