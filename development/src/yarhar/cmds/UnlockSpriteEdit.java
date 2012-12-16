package yarhar.cmds;

import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import pwnee.GameMath;
import yarhar.map.*;


/** Unlocks all sprites in the currently selected layer. */
public class UnlockSpriteEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public ArrayList<SpriteInstance> sprites;
    
    public UnlockSpriteEdit(LevelMap map) {
        super();
        this.map = map;
        Layer layer = map.selectedLayer;
        
        sprites = new ArrayList<SpriteInstance>();
        
        // Create a list of sprites in the layer that are currently locked.
        for(SpriteInstance sprite : layer.sprites) {
          if(sprite.isLocked) {
            sprites.add(sprite);
          }
        }
        
        map.unlockAll();
        
        map.flagModified();
    }
    
    public void undo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            sprite.isLocked = true;
            sprite.isSelected = false;
        }
        
        map.flagModified();
    }
    
    public void redo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            sprite.isLocked = false;
        }
        
        map.flagModified();
    }
}
