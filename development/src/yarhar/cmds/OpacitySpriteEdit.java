package yarhar.cmds;

import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import pwnee.GameMath;
import yarhar.map.*;


/** Sets the selected sprites' opacity. */
public class OpacitySpriteEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public ArrayList<SpriteInstance> sprites;
    public ArrayList<Double> oldOpacities = new ArrayList();
    public ArrayList<Double> newOpacities = new ArrayList();
    
    public OpacitySpriteEdit(LevelMap map, double opacity, boolean isRelative) {
        super();
        this.map = map;
        
        sprites = new ArrayList(map.selectedSprites);
        
        // save the sprites' old opacity
        for(int i = 0; i < sprites.size(); i++) {
            SpriteInstance sprite = sprites.get(i);
            oldOpacities.add(sprite.opacity);
        }
        
        // change the sprites' opacity and save their new opacity
        for(int i = 0; i < sprites.size(); i++) {
            SpriteInstance sprite = sprites.get(i);
            
            if(isRelative)
                sprite.opacity *= opacity;
            else
                sprite.opacity = opacity;
            sprite.setOpacity(sprite.opacity);
            
            newOpacities.add(sprite.opacity);
        }
        
        map.flagModified();
    }
    
    public void undo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            sprite.opacity = oldOpacities.get(i);
        }
        
        map.flagModified();
    }
    
    public void redo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            sprite.opacity = newOpacities.get(i);
        }
        
        map.flagModified();
    }
}
