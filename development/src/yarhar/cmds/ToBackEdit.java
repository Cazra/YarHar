package yarhar.cmds;

import java.util.ArrayList;
import yarhar.map.*;


/** Brings the current selection of sprites to the back of their layer's z-ordering. */
public class ToBackEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public ArrayList<SpriteInstance> oldOrder;
    public ArrayList<SpriteInstance> newOrder;
    
    public ToBackEdit(LevelMap map) {
        super();
        this.map = map;
        
        // make a copy of our ordering before the edit
        oldOrder = new ArrayList<SpriteInstance>(map.selectedLayer.sprites);
        
        // perform the edit
        map.toBackSelectedSprites();
        
        // make a copy of our ordering after the edit
        newOrder = new ArrayList<SpriteInstance>(map.selectedLayer.sprites);
        
        map.flagModified();
    }
    
    public void undo() {
        map.selectedLayer.sprites = new ArrayList<SpriteInstance>(oldOrder);
        map.selectedLayer.updateZOrdering();
        map.flagModified();
    }
    
    public void redo() {
        map.selectedLayer.sprites = new ArrayList<SpriteInstance>(newOrder);
        map.selectedLayer.updateZOrdering();
        map.flagModified();
    }
}
