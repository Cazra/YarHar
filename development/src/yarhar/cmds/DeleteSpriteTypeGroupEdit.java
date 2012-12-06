package yarhar.cmds;

import java.util.LinkedList;
import java.awt.Point;
import yarhar.map.*;


/** Deletes a new group from our sprite library. */
public class DeleteSpriteTypeGroupEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public SpriteLibrary library;
    public String group;
    public LinkedList<SpriteType> types;
    public LinkedList<LayerInstTuple> tuples;

    
    public DeleteSpriteTypeGroupEdit(SpriteLibrary library, String group) {
        super();
        this.library = library;
        this.group = group;
        this.map = library.levelMap;
        
        types = library.removeGroup(group);
        tuples = new LinkedList<LayerInstTuple>();
        for(SpriteType type : types) {
            tuples.addAll(map.deleteAllInstances(type));
        }
        
        map.flagModified();
    }
    
    public void undo() {
        // restore the group.
        library.addGroup(group);
        
        // restore the sprite types that were in the group.
        for(SpriteType sprite : types) {
            library.addSpriteType(group, sprite);
        }
        
        // restore all the instances of those sprite types that were deleted.
        for(LayerInstTuple tuple : tuples) {
            Layer layer = tuple.layer;
            SpriteInstance instance = tuple.instance;
            
            layer.addSprite(instance);
        }
        
        map.flagModified();
    }
    
    public void redo() {
        library.removeGroup(group);
        for(SpriteType type : types) {
            map.deleteAllInstances(type);
        }
        
        map.flagModified();
    }
}
