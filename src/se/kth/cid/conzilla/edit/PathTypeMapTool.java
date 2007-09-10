/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.event.ActionEvent;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.properties.ConzillaResourceManager;
import se.kth.cid.conzilla.tool.ActionMapMenuTool;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.style.LineStyle;

/**
 * Tool for switching between straight and curved lines.
 * 
 * @author Matthias Palmer.
 */
public class PathTypeMapTool extends ActionMapMenuTool {

    static final String CURVE_LINE = "CURVE_LINE";

    static final String STRAIGHTEN_LINE = "STRAIGHTEN_LINE";

    String currentName;

    public PathTypeMapTool(MapController cont) {
        super(CURVE_LINE, EditMapManagerFactory.class.getName(), cont);
        currentName = CURVE_LINE;
    }

    protected boolean updateEnabled() {
        if ((mapEvent.hitType != MapEvent.HIT_TRIPLELINE || mapEvent.hitType != MapEvent.HIT_BOXLINE)
                && mapEvent.mapObject.getDrawerLayout().isEditable()) {
            int type;
            if (mapEvent.hitType == MapEvent.HIT_BOXLINE)
                type = ((StatementLayout) mapObject.getDrawerLayout()).getBoxLinePathType();
            else
                type = ((StatementLayout) mapObject.getDrawerLayout())
                        .getPathType();
            if (type == LineStyle.PATH_TYPE_STRAIGHT)
                currentName = CURVE_LINE;
            else
                currentName = STRAIGHTEN_LINE;

            ConzillaResourceManager.getDefaultManager().customizeButton(
                    getJMenuItem(), EditMapManagerFactory.class.getName(),
                    currentName);

            return true;
        }
        return false;
    }

    public void actionPerformed(ActionEvent e) {
        if (mapEvent.hitType == MapEvent.HIT_BOXLINE) {
            StatementLayout ns = (StatementLayout) mapObject.getDrawerLayout();
            if (ns.getBoxLinePathType() == LineStyle.PATH_TYPE_STRAIGHT) {
                ns.setBoxLine(makeCurveLine(ns.getBoxLine()));
                ns.setBoxLinePathType(LineStyle.PATH_TYPE_CURVE);
            } else {
                ns.setBoxLinePathType(LineStyle.PATH_TYPE_STRAIGHT);
                ns.setBoxLine(makeStraightLine(ns.getBoxLine()));
            }
        } else {
            StatementLayout as = (StatementLayout) mapObject.getDrawerLayout();
            if (as.getPathType() == LineStyle.PATH_TYPE_STRAIGHT) {
                as.setLine(makeCurveLine(as.getLine()));
                as.setPathType(LineStyle.PATH_TYPE_CURVE);
            } else {
                as.setPathType(LineStyle.PATH_TYPE_STRAIGHT);
                as.setLine(makeStraightLine(as.getLine()));
            }
        }
    }

    protected ContextMap.Position [] makeStraightLine(ContextMap.Position [] line) {
        ContextMap.Position [] nl = new ContextMap.Position[((line.length - 1) / 3) + 1];
        for (int i = 0; i < nl.length; i++)
            nl[i] = line[i * 3];
        return nl;
    }

    protected ContextMap.Position [] makeCurveLine(ContextMap.Position [] line) {
        ContextMap.Position [] nl = new ContextMap.Position[((line.length - 1) * 3) + 1];
        nl[0] = line[0];
        for (int i = 1; i < line.length; i++) {
            nl[i * 3] = line[i];
            nl[i * 3 - 1] = new ContextMap.Position(
                    (int) (line[i - 1].x + ((line[i].x - line[i - 1].x) * 0.7)),
                    (int) (line[i - 1].y + ((line[i].y - line[i - 1].y) * 0.7)));
            nl[i * 3 - 2] = new ContextMap.Position(
                    (int) (line[i - 1].x + ((line[i].x - line[i - 1].x) * 0.3)),
                    (int) (line[i - 1].y + ((line[i].y - line[i - 1].y) * 0.3)));
        }
        return nl;
    }
}

