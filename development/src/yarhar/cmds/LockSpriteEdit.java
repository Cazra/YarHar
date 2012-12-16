package yarhar.cmds;

import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import pwnee.GameMath;
import yarhar.map.*;


/** Locks all the currently selected sprites. */
public class LockSpriteEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public ArrayList<SpriteInstance> sprites;
    
    public LockSpriteEdit(LevelMap map) {
        super();
        this.map = map;
        
        sprites = new ArrayList(map.selectedSprites);
        map.lockSelectedSprites();
        
        map.flagModified();
    }
    
    public void undo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            sprite.isLocked = false;
        }
        
        map.flagModified();
    }
    
    public void redo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            sprite.isLocked = true;
            sprite.isSelected = false;
        }
        
        map.flagModified();
    }
}
