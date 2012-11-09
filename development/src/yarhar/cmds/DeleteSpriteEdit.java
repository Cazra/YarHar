package yarhar.cmds;

import java.util.LinkedList;
import java.awt.Point;
import yarhar.map.*;


/** Deletes the selected sprites. */
public class DeleteSpriteEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public LinkedList<SpriteInstance> sprites;
    public Layer layer;
    
    public DeleteSpriteEdit(LevelMap map) {
        super();
        this.map = map;
        
        layer = map.selectedLayer;
        sprites = new LinkedList<SpriteInstance>(map.selectedSprites);
        
        redo();
        
        map.selectedSprites = new LinkedList<SpriteInstance>();
        map.selectedSprite = null;
    }
    
    public void undo() {
        for(SpriteInstance sprite : sprites) {
            
            // TODO : insert the sprites at the z-indices they were deleted from.
            
            layer.addSprite(sprite);
            sprite.isSelected = false;
        }
    }
    
    public void redo() {
        for(SpriteInstance sprite : sprites) {
            layer.removeSprite(sprite);
            sprite.isSelected = false;
        }
    }
}
