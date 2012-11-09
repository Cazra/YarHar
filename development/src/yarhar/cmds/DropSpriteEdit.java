package yarhar.cmds;

import java.awt.Point;
import yarhar.map.*;


/** Drops a sprite from the sprite library into the currently selected layer. */
public class DropSpriteEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public SpriteType spriteType;
    public Point loc;
    
    public SpriteInstance sprite;
    public Layer layer;
    
    public DropSpriteEdit(LevelMap map, SpriteType spriteType, Point loc) {
        super();
        this.map = map;
        this.spriteType = spriteType;
        this.loc = loc;
        this.layer = map.selectedLayer;
        
        sprite = layer.dropSpriteType(spriteType, loc);
    }
    
    public void undo() {
        layer.removeSprite(sprite);
        sprite.isSelected = false;
    }
    
    public void redo() {
        layer.addSprite(sprite);
        sprite.isSelected = false;
    }
}
