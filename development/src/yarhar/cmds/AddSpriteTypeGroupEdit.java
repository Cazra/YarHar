package yarhar.cmds;

import java.util.LinkedList;
import java.awt.Point;
import yarhar.map.*;


/** Adds a new group to our sprite library. */
public class AddSpriteTypeGroupEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public SpriteLibrary library;
    public String group;

    
    public AddSpriteTypeGroupEdit(SpriteLibrary library, String group) {
        super();
        this.library = library;
        this.group = group;
        this.map = library.levelMap;
        
        library.addGroup(group);
        
        map.flagModified();
    }
    
    public void undo() {
        library.removeGroup(group);
        
        map.flagModified();
    }
    
    public void redo() {
        library.addGroup(group);
        
        map.flagModified();
    }
}
