package yarhar.cmds;

import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import pwnee.GameMath;
import yarhar.map.*;


/** Sets the selected sprites' opacity. */
public class OpacityLayerEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public ArrayList<SpriteInstance> sprites;
    public Layer layer;
    public double oldOpacity;
    public double newOpacity;
    
    public OpacityLayerEdit(LevelMap map, Layer layer, double opacity, boolean isRelative) {
        super();
        this.map = map;
        this.layer = layer;
        
        oldOpacity = layer.opacity;
        
        if(isRelative) 
            layer.setOpacity(layer.opacity * opacity);
        else
            layer.setOpacity(opacity);
        
        newOpacity = layer.opacity;
        
        map.flagModified();
    }
    
    public void undo() {
        layer.opacity = oldOpacity;
        
        map.flagModified();
    }
    
    public void redo() {
        layer.opacity = newOpacity;
        
        map.flagModified();
    }
}
