package yarhar.cmds;

import java.util.LinkedList;
import java.awt.Point;
import yarhar.map.*;


/** Modifies a SpriteType in our library. */
public class EditSpriteTypeEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public SpriteLibrary library;
    public SpriteType type;
    public SpriteType overwritten;
    
    public EditSpriteTypeEdit(SpriteLibrary library, SpriteType type) {
        super();
        this.library = library;
        this.type = type;
        this.map = library.levelMap;
        
        overwritten = library.sprites.get(type.name);
        
        redo();
    }
    
    public void undo() {
        if(overwritten != null)
            library.editSpriteType(overwritten);
        
        map.flagModified();
    }
    
    public void redo() {
        library.editSpriteType(type);
        
        map.flagModified();
    }
}
