package yarhar.cmds;

import java.util.ArrayList;
import java.awt.Point;
import yarhar.images.ImageLibrary;
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

    
    public ImportLibraryEdit(SpriteLibrary library, SpriteLibrary other, ImageLibrary oImgLib) {
        super();
        this.map = library.levelMap;
        this.oldLib = library.makeClone();
        
        for(String gName : other.groupNames) {
          // add new groups into our library.
          SpriteTypeGroup group = other.groups.get(gName);
          library.addGroup(gName);
          
          for(String tName : group.typeNames) {
            SpriteType type = other.sprites.get(tName);
            newTypes.add(type);
            
            // store the old types that are overwritten.
            if(library.sprites.containsKey(tName)) {
              oldTypes.add(library.sprites.get(tName));
            }
            
            library.imgLib.put(tName, oImgLib.get(tName));
            
            library.addSpriteType(gName, type);
          }
        }
        library.setImgLib(library.imgLib);
        this.newLib = library.makeClone();
        
        map.spriteLib.updatePeerComponent();
        
        map.flagModified();
    }
    
    public void undo() {
        map.spriteLib = oldLib.makeClone();
        
        // restore old sprite images.
        map.spriteLib.setImgLib(map.spriteLib.imgLib);
        
        map.spriteLib.updatePeerComponent();
        map.flagModified();
    }
    
    public void redo() {
        map.spriteLib = newLib.makeClone();
        
        // restore new sprite images.
        map.spriteLib.setImgLib(map.spriteLib.imgLib);
        
        map.spriteLib.updatePeerComponent();
        map.flagModified();
    }
}


