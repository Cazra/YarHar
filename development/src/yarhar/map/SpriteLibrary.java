package yarhar.map;

import yarhar.*;
import java.awt.*;
import pwnee.*;
import java.util.HashMap;
import java.util.TreeSet;

/** Stores all the SpriteTypes used by the map that uses this. */
public class SpriteLibrary {
    
    /** The map to which this belongs. */
    public LevelMap levelMap;
    
    /** Our library is actually divided into groups of SpriteTypes indexed by name. 
    Inside these groups, the SpriteTypes themselves are then indexed by name. */
    public HashMap<String, SpriteTypeGroup> groups = new HashMap<String, SpriteTypeGroup>();
    
    /** The set of group names in this library. */
    public TreeSet<String> groupNames = new TreeSet<String>();
    
    
    /** True if this SpriteLibrary's Swing component peer needs to be updated. */
    public boolean isModified = false;
    
    public SpriteLibrary(LevelMap parent) {
        levelMap = parent;
        
        testPopulation();
    }
    
    
    public void testPopulation() {
        addGroup("default");
        addSpriteType("default", new SpriteType());
        
        addGroup("tiles");
        addSpriteType("tiles", new SpriteType("grass"));
        addSpriteType("tiles", new SpriteType("brick"));
        addSpriteType("tiles", new SpriteType("water"));
        
        addGroup("characters");
        addSpriteType("characters", new SpriteType("Mario"));
        addSpriteType("characters", new SpriteType("Goomba"));
        addSpriteType("characters", new SpriteType("Koopa"));
        addSpriteType("characters", new SpriteType("Yoshi"));
        addSpriteType("characters", new SpriteType("Bowser"));
    }
    
    
    /** Creates a new blank group in our library, unless a group with the name already exists. */
    public void addGroup(String name) {
        if(groupNames.contains(name))
            return;
        
        groups.put(name, new SpriteTypeGroup());
        groupNames.add(name);
        
        this.isModified = true;
        
        updatePeerComponent();
    }
    
    
    /** Adds a new SpriteType to a group (or replaces any existing SpriteType with the same name). */
    public void addSpriteType(String groupName, SpriteType spriteType) {
        if(!groupNames.contains(groupName) || spriteType == null)
            return;
        
        groups.get(groupName).addSpriteType(spriteType);
        
        updatePeerComponent();
    }
    
    /** Renames a SpriteTypeGroup in this library */
    public void renameGroup(String groupName, String newName) {
        // avoid renaming nonexistent groups or the "default" group.
        if(!groupNames.contains(groupName) || groupName == "default") {
            return;
        }
        SpriteTypeGroup group = groups.get(groupName);
        
        // If newName is already in our groups, increment our new name to avoid a name collision.
        if(groupNames.contains(newName))
            newName = renameGroupRepeat(newName, 2);
        
        // rename the group
        groups.remove(groupName);
        group.name = newName;
        groups.put(newName,group);
        
        // update the library swing panel.
        this.isModified = true;
        updatePeerComponent();
    }
    
    /** Generates a new group name that avoids collisions with group names already in this library. */
    private String renameGroupRepeat(String baseNewName, int index) {
        String newName = baseNewName + index;
        if(!groupNames.contains(newName)) {
            return newName;
        }
        else {
            return renameGroupRepeat(baseNewName, index + 1);
        }
    }
    
    
    public void updatePeerComponent() {
        SpriteLibraryPanel spriteLibPanel = ((EditorPanel) levelMap.game).frame.spriteLibPanel;
        spriteLibPanel.setLibrary(this);
        spriteLibPanel.updateGroupList();
    }
}

