package yarhar.cmds;

import java.util.LinkedList;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import pwnee.GameMath;
import yarhar.map.*;
import yarhar.*;


/** Adds a layer to the top of the layers. */
public class AddLayerEdit extends SimpleUndoableEdit {
    
    public LayerList layerList;
    public LevelMap map;
    public LinkedList<Layer> oldLayers;
    public LinkedList<Layer> newLayers;
    
    public AddLayerEdit(LayerList list, Layer layer) {
        super();
        this.layerList = list;
        this.map = list.map;
        
        oldLayers = new LinkedList<Layer>(map.layers);
        
        map.addLayerFirst(layer);
        layerList.updateList();
        
        newLayers = new LinkedList<Layer>(map.layers);
        
        map.flagModified();
    }
    
    public void undo() {
        map.layers = new LinkedList<Layer>(oldLayers);
        layerList.updateList();
        
        map.flagModified();
    }
    
    public void redo() {
        map.layers = new LinkedList<Layer>(newLayers);
        layerList.updateList();
        
        map.flagModified();
    }
}
