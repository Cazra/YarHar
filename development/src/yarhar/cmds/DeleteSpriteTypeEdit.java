package yarhar.cmds;

import java.util.LinkedList;
import java.awt.Image;
import java.awt.Point;
import yarhar.map.*;


/** Adds a new SpriteType to our library. */
public class DeleteSpriteTypeEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public SpriteLibrary library;
    public String group;
    public SpriteType type;
    public Image oldImg;
    
    public LinkedList<LayerInstTuple> tuples;
    
    public DeleteSpriteTypeEdit(SpriteLibrary library, String group, SpriteType type) {
        super();
        this.library = library;
        this.group = group;
        this.type = type;
        this.map = library.levelMap;
        
        library.removeSpriteType(group, type);
        this.oldImg = library.imgLib.remove(type.name);
        tuples = map.deleteAllInstances(type);
        
        map.flagModified();
    }
    
    public void undo() {
        library.addSpriteType(group, type);
        type.setImage(oldImg);
        
        for(LayerInstTuple tuple : tuples) {
            Layer layer = tuple.layer;
            SpriteInstance instance = tuple.instance;
            
            layer.addSprite(instance);
        }
        
        
        
        map.flagModified();
    }
    
    public void redo() {
        library.removeSpriteType(group, type);
        library.imgLib.remove(type.name);
        
        map.deleteAllInstances(type);
        
        map.flagModified();
    }
}
