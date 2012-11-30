package yarhar.map;

import yarhar.*;
import java.awt.*;
import pwnee.*;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.TreeSet;
import org.json.*;

/** Stores all the SpriteTypes used by the map that uses this. */
public class SpriteLibrary {
    
    /** The map to which this belongs. */
    public LevelMap levelMap;
    
    /** Our library is actually divided into groups of SpriteTypes indexed by name. 
    Inside these groups, the SpriteTypes themselves are then indexed by name. */
    public HashMap<String, SpriteTypeGroup> groups = new HashMap<String, SpriteTypeGroup>();
    
    /** The set of group names in this library. */
    public TreeSet<String> groupNames = new TreeSet<String>();
    
    /** Our library's SpriteTypes, not divided into groups. This is our actual library. */
    public HashMap<String, SpriteType> sprites = new HashMap<String, SpriteType>();
    
    /** The set of sprite names in this library. */
    public TreeSet<String> spriteNames = new TreeSet<String>();
    
    
    /** True if this SpriteLibrary's Swing component peer needs to be updated. */
    public boolean isModified = false;
    
    public SpriteLibrary(LevelMap parent) {
        levelMap = parent;
        
    //    testPopulation();
        addGroup("default");
        updatePeerComponent();
    }
    
    
    public SpriteLibrary(LevelMap parent, JSONObject json) {
        levelMap = parent;
        loadJSON(json);
        updatePeerComponent();
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
    
    
    
    /** Creates a JSON string representing this library. */
    public String toJSON() {
        boolean isFirst = true;
    
        String result = "{";
        
        result += "\"groups\":["; 
        for(String name : groupNames) {
            if(!isFirst)
                result += ",";
            else
                isFirst = false;
            
            SpriteTypeGroup group = groups.get(name);
            result += group.toJSON();
        }
        isFirst = true;
        result += "],";
        result += "\"sprites\":["; 
        for(String name : spriteNames) {
            if(!isFirst)
                result += ",";
            else
                isFirst = false;
            
            SpriteType sprite = sprites.get(name);
            result += sprite.toJSON();
        }
        isFirst = true;
        result += "]";
        result += "}";
        
        return result;
    }
    
    
    /** Loads this sprite library from json. */
    public void loadJSON(JSONObject json) {
        try {
            JSONArray groupSetJ = json.getJSONArray("groups");
            for(int i = 0; i < groupSetJ.length(); i++) {
                JSONObject groupJ = groupSetJ.getJSONObject(i);
                addGroup(new SpriteTypeGroup(groupJ));
            }
            
            JSONArray spriteSetJ = json.getJSONArray("sprites");
            for(int i = 0; i < spriteSetJ.length(); i++) {
                JSONObject spriteJ = spriteSetJ.getJSONObject(i);
                SpriteType sprite = new SpriteType(spriteJ);
                sprites.put(sprite.name, sprite);
                spriteNames.add(sprite.name);
            }
        }
        catch(Exception e) {
            System.err.println("Error reading JSON for sprite library.");
        }
    }
    
    
    /** Creates a new blank group in our library, unless a group with the name already exists. */
    public void addGroup(String name) {
        if(groupNames.contains(name))
            return;
        
        groups.put(name, new SpriteTypeGroup(name));
        groupNames.add(name);
        
        this.isModified = true;
        
        updatePeerComponent();
    }
    
    
    /** Adds a pre-populated group in our library. If a group with the same name already exists, the groups are merged. */
    public void addGroup(SpriteTypeGroup group) {
        groups.put(group.name, group);
        groupNames.add(group.name);
        
        // TODO : merge the existing group.
        
        this.isModified = true;
        
        updatePeerComponent();
    }
    
    
    /** Adds a new SpriteType to a group (or replaces any existing SpriteType with the same name). Returns a list of (layer, instance) tuples for all instances whose type is overwritten by the new type. */
    public void addSpriteType(String groupName, SpriteType spriteType) {
        if(!groupNames.contains(groupName))
            groupName = "default";
        
        groups.get(groupName).addSpriteType(spriteType);
        sprites.put(spriteType.name, spriteType);
        spriteNames.add(spriteType.name);
        
        // overwrite the type for all instances of the replaced type.
        for(Layer layer : levelMap.layers) {
            for(SpriteInstance sprite : layer.sprites) {
                if(sprite.type.name.equals(spriteType.name)) {
                    sprite.type = spriteType;
                }
            }
        }
        
        updatePeerComponent();
    }
    
    /** Edits a sprite type. This effectively overwrites an existing SpriteType with the same name in this library and in all sprite instances with that type. */
    public void editSpriteType(SpriteType spriteType) {
        
        sprites.put(spriteType.name, spriteType);
        spriteNames.add(spriteType.name);
        
        // overwrite the type for all instances of the replaced type.
        for(Layer layer : levelMap.layers) {
            for(SpriteInstance sprite : layer.sprites) {
                if(sprite.type.name.equals(spriteType.name)) {
                    sprite.type = spriteType;
                }
            }
        }
        
        updatePeerComponent();
    }
    
    /** Renames a sprite type. This fails if another sprite shares the name that we want to change to. */
    public void renameSpriteType(String groupName, SpriteType spriteType, String name) {
        if(spriteNames.contains(name)) {
            System.err.println("name already exists in library");
            return;
        }
        
        groups.get(groupName).removeSpriteType(spriteType);
        spriteNames.remove(spriteType.name);
        sprites.remove(spriteType.name);
        
        spriteType.name = name;
        
        addSpriteType(groupName, spriteType);
    }
    
    
    /** Removes a sprite type from its group and the library. */
    public void removeSpriteType(String groupName, SpriteType spriteType) {
        if(!groupNames.contains(groupName))
            groupName = "default";
        
        groups.get(groupName).removeSpriteType(spriteType);
        sprites.remove(spriteType.name);
        spriteNames.remove(spriteType.name);
        
        updatePeerComponent();
    }
    
    
    /** Checks if this library contains a sprite type. */
    
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

