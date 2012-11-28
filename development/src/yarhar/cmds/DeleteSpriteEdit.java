package yarhar.cmds;

import java.util.ArrayList;
import java.util.LinkedList;
import java.awt.Point;
import yarhar.map.*;


/** Deletes the selected sprites. */
public class DeleteSpriteEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public LinkedList<SpriteInstance> oldLayerSprites;
    public LinkedList<SpriteInstance> sprites;
    public Layer layer;
    
    public DeleteSpriteEdit(LevelMap map) {
        super();
        this.map = map;
        
        layer = map.selectedLayer;
        sprites = new LinkedList<SpriteInstance>(map.selectedSprites);
        
        // save the old ordering of sprites in our layer.
        oldLayerSprites = new LinkedList<SpriteInstance>(layer.sprites);
        
        redo();
        
        // empty our selection.
        map.unselectAll();
        map.selectedSprite = null;
        
        map.flagModified();
    }
    
    public void undo() {
        layer.sprites = new ArrayList<SpriteInstance>(oldLayerSprites);
        map.unselectAll();
        map.selectedSprite = null;
        
        map.flagModified();
    }
    
    public void redo() {
        for(SpriteInstance sprite : sprites) {
            layer.removeSprite(sprite);
            sprite.isSelected = false;
        }
        map.unselectAll();
        map.selectedSprite = null;
        
        map.flagModified();
    }
}
