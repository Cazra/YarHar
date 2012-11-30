package yarhar.cmds;

import java.util.LinkedList;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import pwnee.GameMath;
import yarhar.map.*;
import yarhar.*;


/** Renames a SpriteType. */
public class RenameSpriteTypeEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public SpriteLibrary library;
    public String group;
    public SpriteType type;
    public String oldName;
    public String newName;
    
    public RenameSpriteTypeEdit(SpriteLibrary library, String group, SpriteType type, String name) {
        super();
        this.library = library;
        this.group = group;
        this.type = type;
        this.map = library.levelMap;

        oldName = type.name;
        library.renameSpriteType(group, type, name);
        newName = type.name;
        
        map.flagModified();
    }
    
    public void undo() {
        library.renameSpriteType(group, type, oldName);
        map.flagModified();
    }
    
    public void redo() {
        library.renameSpriteType(group, type, newName);
        map.flagModified();
    }
}
