package yarhar.cmds;

import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import pwnee.GameMath;
import yarhar.map.*;


/** Scales the selected sprites. */
public class TileSpriteEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public ArrayList<SpriteInstance> sprites;
    public ArrayList<Point2D.Double> oldTiles = new ArrayList();
    public ArrayList<Point2D.Double> newTiles = new ArrayList();
    
    public TileSpriteEdit(LevelMap map, double x, double y, boolean isRelative) {
        super();
        this.map = map;
        
        sprites = new ArrayList(map.selectedSprites);
        
        // save the sprites' old position
        for(int i = 0; i < sprites.size(); i++) {
            SpriteInstance sprite = sprites.get(i);
            oldTiles.add(new Point2D.Double(sprite.repeatX, sprite.repeatY));
        }
        
        // tile the sprites. 
        if(isRelative) {
            map.tileSelectedSprites(x,y);
        }
        else {
            map.tileSpriteAbs(x,y);
        }
        
        // save the sprites' new position
        for(int i = 0; i < sprites.size(); i++) {
            SpriteInstance sprite = sprites.get(i);
            newTiles.add(new Point2D.Double(sprite.repeatX, sprite.repeatY));
        }
        
        map.flagModified();
    }
    
    public void undo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            Point2D.Double oldTile = oldTiles.get(i);
            
            sprite.repeatX = oldTile.getX();
            sprite.repeatY = oldTile.getY();
        }
        
        map.flagModified();
    }
    
    public void redo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            Point2D.Double newTile = newTiles.get(i);
            
            sprite.repeatX = newTile.getX();
            sprite.repeatY = newTile.getY();
        }
        
        map.flagModified();
    }
}
