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
    
    public NewSpriteTypeEdit(SpriteLibrary library, String group, SpriteType type) {
        super();
        this.library = library;
        this.group = group;
        this.type = type;
        this.map = library.levelMap;
        
        library.addSpriteType(group, type);
        
        map.flagModified();
    }
    
    public void undo() {
        library.removeSpriteType(group, type);
        
        map.flagModified();
    }
    
    public void redo() {
        library.addSpriteType(group, type);
        
        map.flagModified();
    }
}
