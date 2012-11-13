package yarhar.cmds;

import java.util.LinkedList;
import java.awt.Point;
import yarhar.map.*;


/** Adds a new SpriteType to our library. */
public class NewSpriteTypeEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public SpriteLibrary library;
    public String group;
    public SpriteType type;
    public SpriteType overwritten;
    
    public NewSpriteTypeEdit(SpriteLibrary library, String group, SpriteType type) {
        super();
        this.library = library;
        this.group = group;
        this.type = type;
        this.map = library.levelMap;
        
        overwritten = library.sprites.get(type.name);
        
        library.addSpriteType(group, type);
        
        map.flagModified();
    }
    
    public void undo() {
        library.removeSpriteType(group, type);
        
        if(overwritten != null)
            library.addSpriteType(group, overwritten);
        
        map.flagModified();
    }
    
    public void redo() {
        library.addSpriteType(group, type);
        
        map.flagModified();
    }
}
