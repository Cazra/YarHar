package yarhar.cmds;

import java.util.LinkedList;
import java.awt.Point;
import yarhar.map.*;


/** Registers just-cloned sprites for undo/redo of the clone operation. */
public class CloneSpriteEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public LinkedList<SpriteInstance> sprites;
    public Layer layer;
    
    public CloneSpriteEdit(LevelMap map) {
        super();
        this.map = map;
        
        layer = map.selectedLayer;
        sprites = new LinkedList<SpriteInstance>(map.selectedSprites);
        
        map.flagModified();
    }
    
    public void undo() {
        for(SpriteInstance sprite : sprites) {
            layer.removeSprite(sprite);
            sprite.isSelected = false;
        }
        
        map.flagModified();
    }
    
    public void redo() {
        for(SpriteInstance sprite : sprites) {
            layer.addSprite(sprite);
            sprite.isSelected = false;
        }
        
        map.flagModified();
    }
}
