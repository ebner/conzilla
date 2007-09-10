/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout;
import java.util.Vector;

/** Basically a renaming of the functions in a MemGroupLayout to better
 *  fit the layer concept.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public interface LayerManager
{
    //****************Support for listeners.*******************
    //---------------------------------------------------------

    void addLayerListener(LayerListener list);
    
    void removeLayerListener(LayerListener list);

    void fireLayerChange(LayerEvent event);



    //******Support for access, creation and deletion of layers.*******
    //-----------------------------------------------------------------

    LayerLayout createLayer(String name, Object tag, ContextMap cMap);

    void addLayer(LayerLayout layer);
    
    LayerLayout getLayer(String name);

    void removeLayer(String name);

    void removeLayer(LayerLayout layer);

    Vector getLayers();


    //****Support for manipulation of visibility and order of layers.*******
    //----------------------------------------------------------------------------

    void lowerLayer(LayerLayout layer);

    void raiseLayer(LayerLayout layer);

    int getOrderOfLayer(LayerLayout layer);

    void setOrderOfLayer(LayerLayout layer, int position);

    void setLayerVisible(String name, boolean visible);

    boolean getLayerVisible(String name);


    //******Support for manipulation of active edit layer.*******
    //-----------------------------------------------------------

	//FIXME: change Group for Layer.
    void setEditGroupLayout(String name);

    LayerLayout getEditGroupLayout();



    //*****Special support for manipulation of objectlayouts*******
    //------------------------------------------------------------

    Vector getDrawerLayouts(int visibility);

    ResourceLayout getResourceLayout(String id);
}

    //    int getNumberOfLayers();
    //    Hashtable getHashedConceptLayouts(int visibility);
