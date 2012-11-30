package yarhar.cmds;

import java.util.LinkedList;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.JOptionPane;
import pwnee.GameMath;
import yarhar.map.*;
import yarhar.*;


/** Deletes a layer. */
public class DeleteLayerEdit extends SimpleUndoableEdit {
    
    public LayerList layerList;
    public LevelMap map;
    public LinkedList<Layer> oldLayers;
    public LinkedList<Layer> newLayers;
    
    public DeleteLayerEdit(LayerList list, Layer layer) {
        super();
        this.layerList = list;
        this.map = list.map;
        
        oldLayers = new LinkedList<Layer>(map.layers);
        
        if(!map.deleteLayer(layer)) {
            JOptionPane.showMessageDialog(layerList, "Cannot delete layer. Map must have at least one layer!");
            // TODO : Remove this edit from the UndoManager.
        }
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
