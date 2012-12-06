package yarhar.cmds;

import java.util.LinkedList;
import java.awt.Point;
import yarhar.map.*;


/** Moves a SpriteType to another SpriteTypeGroup. */
public class MoveSpriteTypeEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public SpriteLibrary library;
    public SpriteType spriteType;
    public String oldGroup;
    public String newGroup;

    
    public MoveSpriteTypeEdit(SpriteLibrary library, SpriteType spriteType, String oldGroup, String newGroup) {
        super();
        this.library = library;
        this.spriteType = spriteType;
        this.map = library.levelMap;
        this.oldGroup = oldGroup;
        this.newGroup = newGroup;
        
        library.moveSpriteType(spriteType, oldGroup, newGroup);
        map.flagModified();
    }
    
    public void undo() {
        library.moveSpriteType(spriteType, newGroup, oldGroup);
        map.flagModified();
    }
    
    public void redo() {
        library.moveSpriteType(spriteType, oldGroup, newGroup);
        map.flagModified();
    }
}
