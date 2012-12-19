package yarhar.map;

import yarhar.*;
import java.awt.*;
import org.json.*;
import pwnee.*;
import java.util.HashMap;
import java.util.TreeSet;

/** A set of SpriteTypes stored in a SpriteLibrary. */
public class SpriteTypeGroup {
    
    /** This group's unique name in its SpriteLibrary */ 
    public String name = "";
    
    /** The set of group names in this library. */
    public TreeSet<String> typeNames = new TreeSet<String>();
    
    public SpriteTypeGroup() {
        this("Untitled");
    }
    
    
    public SpriteTypeGroup(String n) {
        name = n;
    }
    
    public SpriteTypeGroup(JSONObject groupJ) {
        loadJSON(groupJ);
    }
    
    
    /** Creates a JSON string from this group */
    public String toJSON() {
        String result = "{";
        
        result += "\"name\":\"" + name + "\",";
        result += "\"sprites\":[";
        
        boolean isFirst = true;
        for(String name : typeNames) {
            if(!isFirst)
                result += ",";
            else
                isFirst = false;
            
            result += "\"" + name + "\"";
        }
        
        result += "]";
        result += "}";
        return result;
    }
    
    /** Loads this SpriteTypeGroup from json. */
    public void loadJSON(JSONObject groupJ) {
        try {
            name = groupJ.getString("name");
            JSONArray spriteSetJ = groupJ.getJSONArray("sprites");
            for(int i = 0; i < spriteSetJ.length(); i++) {
                typeNames.add(spriteSetJ.getString(i));
            }
        }
        catch (Exception e) {
            System.err.println("Error reading JSON for sprite type group.");
        }
    }
    
    /** Creates a shallow clone of this group. */
    public SpriteTypeGroup makeClone() {
      SpriteTypeGroup clone = new SpriteTypeGroup(this.name);
      clone.typeNames = new TreeSet<String>(this.typeNames);
      return clone;
    }
    
    
    /** Adds a new SpriteType to this group (or replaces any existing SpriteType with the same name). */
    public void addSpriteType(SpriteType spriteType) {
        if(spriteType == null)
            return;
        
        typeNames.add(spriteType.name);
    }
    
    
    /** Removes a SpriteType from this group. */
    public void removeSpriteType(SpriteType spriteType) {
        if(spriteType == null)
            return;
    
        typeNames.remove(spriteType.name);
    }
    
}

