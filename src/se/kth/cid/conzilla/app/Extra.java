/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.app;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.controller.MapManagerFactory;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.conzilla.util.PriorityMenu;
import se.kth.cid.conzilla.view.View;

/** 
 * Extras plays the role of extensions/modules/plugins in Conzilla.
 * Extras are typically inserted via the reflection API in a running Conzilla 
 * as specified via a configuration file. 
 * If it is intialized succesfully it is managed in the ConzillaKit from where 
 * all extras can be requested.
 * Currently there are no dependencies between extras.
 * 
 * A special case of an extra is a {@link MapManagerFactory}.
 * 
 * @see ConzillaKit
 * @author Matthias Palmer.
 */
public interface Extra
{
	/**
	 * Every extra has a name that has to be unique among all loaded extras. 
	 * @return the name of the Extra.
	 * @see ConzillaKit#getExtra(String)
	 */
    String getName();
    
    /**
     * Every Extra is inited once, only if it the init succeeds 
     * is it allowed to be added by the {@link ConzillaKit#registerExtra(Extra)} function.
     * 
     * @param kit the ConzillaKit that is trying to add it.
     * @return true if it has been successfully initialized.
     */
    boolean initExtra(ConzillaKit kit);
    
    /**
     * An extra may add functionality by adding {@link Tool}s to a {@link ToolsMenu}.
     * The convenience method {@link ConzillaKit#extendMenu(ToolsMenu, MapController)} gives 
     * all Extras the opportunity to extend it by looping over all Extras and calling this method.
     * Hence, every implementation of this method may be called for a wide range of menus, 
     * and the intention is to use the {@link PriorityMenu#getName()} to decide if and how to 
     * extend a given menu. For example:
     * <code> if (menu.getName().equals("FILE_MENU")) {
     * 		menu.addTool(new Tool(...));
     *   }
     * </code>
     * 
     * @param menu the {@link ToolsMenu} that this Extra may extend.
     * @param mapController some menues occur several times, typically once for each {@link View},
     * the given {@link MapController} provides additional information on how to extend it.
     */
    void extendMenu(ToolsMenu menu, MapController mapController);
    
    /**
     * Allows this extra to add functionality for this specific mapcontroller.
     * You can assume that the mapController has a related {@link View} but not a 
     * map loaded.
     * This function is called once for each mapController after it has been initialized.
     * There is one exception, if the view is changed, it may be called again. So make sure
     * that you do not add things on the mapController twice, but for the view you need 
     * not worry, it is new each time this function is called.
     * 
     * @param mapController a mapController for which functionality may be added.
     */
    void addExtraFeatures(MapController mapController);
    
    /**
     * TODO remove this method if no sensible use for it can be envisioned.
     */
    void refreshExtra();
    
    /**
     * Allows the extra to save and report if it succeeded in saving.
     * If the conzillaKit fails saving all extras it might get back to the user 
     * before exiting, i.e. before doing calling {@link #exitExtra()}.
     * 
     * @return true if the extra succeeded saving.
     */
    boolean saveExtra();
    
    /**
     * Allows the extra to exit gracefully, e.g. disconnect, save etc.
     */
    void exitExtra();
}
