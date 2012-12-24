package yarhar.cmds;

import java.util.LinkedList;
import java.awt.Image;
import java.awt.Point;
import yarhar.map.*;


/** Adds a new SpriteType to our library (or edits the image of an existing one). */
public class NewSpriteTypeEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public SpriteLibrary library;
    public String group;
    public SpriteType type;
    public Image oldImg;
    public Image newImg;
    public boolean isEdit;
    
    public NewSpriteTypeEdit(SpriteLibrary library, String group, String tName, Image img) {
        super();
        this.library = library;
        this.group = group;
        this.type = type;
        this.newImg = img;
        
        this.map = library.levelMap;
        
        isEdit = library.sprites.containsKey(tName);
        
        if(isEdit) {
          // We're just editing the image of an existing sprite.
          type = library.sprites.get(tName);
          oldImg = library.imgLib.get(tName);
          type.setImage(newImg);
          library.updatePeerComponent();
        }
        else {
          // We're adding an entirely new sprite to the library.
          oldImg = null;
          type = new SpriteType(tName, library.imgLib, img);
          library.addSpriteType(group, type);
        }
        
        map.flagModified();
    }
    
    public void undo() {
        if(isEdit) {
          type.setImage(oldImg);
        }
        else {
          library.removeSpriteType(group, type);
          library.imgLib.remove(type.name);
        }
        library.updatePeerComponent();
        
        map.flagModified();
    }
    
    public void redo() {
        if(!isEdit) {
          library.addSpriteType(group, type);
          library.imgLib.put(type.name, newImg);
        }
        else {
          type.setImage(newImg);
          library.updatePeerComponent();
        }
        library.updatePeerComponent();
        
        
        map.flagModified();
    }
}
