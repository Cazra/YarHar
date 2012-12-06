package yarhar.cmds;

import java.util.LinkedList;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import pwnee.GameMath;
import yarhar.map.*;
import yarhar.*;


/** Renames a SpriteTypeGroup. */
public class RenameSpriteTypeGroupEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public SpriteLibrary library;
    public String oldName;
    public String newName;
    
    public RenameSpriteTypeGroupEdit(SpriteLibrary library, String group, String name) {
        super();
        this.library = library;
        this.map = library.levelMap;

        oldName = group;
        library.renameGroup(group, name);
        newName = name;
        
        map.flagModified();
    }
    
    public void undo() {
        library.renameGroup(newName, oldName);
        map.flagModified();
    }
    
    public void redo() {
        library.renameGroup(oldName, newName);
        map.flagModified();
    }
}
