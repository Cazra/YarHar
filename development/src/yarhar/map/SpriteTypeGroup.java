package yarhar.map;

import yarhar.*;
import java.awt.*;
import pwnee.*;
import java.util.HashMap;
import java.util.TreeSet;

/** A set of SpriteTypes stored in a SpriteLibrary. */
public class SpriteTypeGroup {
    
    /** This group's unique name in its SpriteLibrary */ 
    public String name = "";
    
    /** Our library is actually divided into groups of SpriteTypes indexed by name. 
    Inside these groups, the SpriteTypes themselves are then indexed by name. */
    public HashMap<String, SpriteType> spriteTypes = new HashMap<String, SpriteType>();
    
    /** The set of group names in this library. */
    public TreeSet<String> typeNames = new TreeSet<String>();
    
    /** True if this SpriteTypeGroup's swing component peer needs to be updated. */
    public boolean isModified = false;
    
    
    public SpriteTypeGroup() {
        this("Untitled");
    }
    
    
    public SpriteTypeGroup(String n) {
        name = n;
    }
    
    /** Adds a new SpriteType to this group (or replaces any existing SpriteType with the same name). */
    public void addSpriteType(SpriteType spriteType) {
        if(spriteType == null)
            return;
        
        spriteTypes.put(spriteType.name, spriteType);
        typeNames.add(spriteType.name);
        
        isModified = true;
    }
    
}

