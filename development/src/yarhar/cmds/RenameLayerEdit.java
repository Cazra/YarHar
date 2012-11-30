package yarhar.cmds;

import java.util.LinkedList;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import pwnee.GameMath;
import yarhar.map.*;
import yarhar.*;


/** Renames a layer. */
public class RenameLayerEdit extends SimpleUndoableEdit {
    
    public LayerList layerList;
    public LevelMap map;
    public Layer layer;
    public String oldName;
    public String newName;
    
    public RenameLayerEdit(LayerList list, Layer layer, String name) {
        super();
        this.layerList = list;
        this.map = list.map;
        this.layer = layer;
        
        oldName = layer.name;
        
        layer.name = name;
        layerList.updateList();
        
        newName = name;
        
        map.flagModified();
    }
    
    public void undo() {
        layer.name = oldName;
        layerList.updateList();
        
        map.flagModified();
    }
    
    public void redo() {
        layer.name = newName;
        layerList.updateList();
        
        map.flagModified();
    }
}
