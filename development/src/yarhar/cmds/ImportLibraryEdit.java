package yarhar.cmds;

import java.util.ArrayList;
import java.awt.Point;
import yarhar.map.*;


/** 
 * Imports a sprite library from another yarhar file into our current library. 
 * Any existing sprite types with conflicting names are overwritten.
 */
public class ImportLibraryEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public SpriteLibrary oldLib;
    public SpriteLibrary newLib;
    
    public ArrayList<SpriteType> oldTypes = new ArrayList<SpriteType>();
    public ArrayList<SpriteType> newTypes = new ArrayList<SpriteType>();
    
    public ImportLibraryEdit(SpriteLibrary library, SpriteLibrary other) {
        super();
        this.map = library.levelMap;
        this.oldLib = library.makeClone();
        
        for(String gName : other.groupNames) {
          // add new groups into our library.
          SpriteTypeGroup group = other.groups.get(gName);
        //  if(!library.groups.containsKey(gName)) {
            library.addGroup(gName);
        //  }
          
          for(String tName : group.typeNames) {
            SpriteType type = other.sprites.get(tName);
            newTypes.add(type);
            
            // store the old types that are overwritten.
            if(library.sprites.containsKey(tName)) {
              oldTypes.add(library.sprites.get(tName));
            }
            
            library.addSpriteType(gName, type);
          }
        }
        
        this.newLib = library.makeClone();
        
        map.flagModified();
    }
    
    public void undo() {
        map.spriteLib = oldLib.makeClone();
        
        // restore overwritten sprite types.
        for(SpriteType type : oldTypes) {
          map.spriteLib.editSpriteType(type);
        }
        
        map.spriteLib.updatePeerComponent();
        
        map.flagModified();
    }
    
    public void redo() {
        map.spriteLib = newLib.makeClone();
        
        for(SpriteType type : newTypes) {
          map.spriteLib.editSpriteType(type);
        }
        
        map.spriteLib.updatePeerComponent();
        
        map.flagModified();
    }
}
