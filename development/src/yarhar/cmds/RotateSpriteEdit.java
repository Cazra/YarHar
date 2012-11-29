package yarhar.cmds;

import java.util.ArrayList;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import pwnee.GameMath;
import yarhar.map.*;


/** Rotates the selected sprites. */
public class RotateSpriteEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public ArrayList<SpriteInstance> sprites;
    public ArrayList<Point> oldPoints;
    public ArrayList<Point> newPoints;
    public double angle; // relative angle in degrees.
    
    public RotateSpriteEdit(LevelMap map, double angle, boolean isRelative) {
        super();
        this.map = map;
        
        sprites = new ArrayList(map.selectedSprites);
        oldPoints = new ArrayList();
        newPoints = new ArrayList();
        
        // save the sprites' old position
        for(int i = 0; i < sprites.size(); i++) {
            SpriteInstance sprite = sprites.get(i);
            oldPoints.add(new Point((int)sprite.x, (int)sprite.y));
        }
        
        // rotate the sprites about their center of mass.
        if(isRelative) {
            this.angle = angle;
            map.rotateSelectedSprites(angle);
        }
        else {
            this.angle = angle - map.selectedSprite.angle;
            map.rotateSpriteAbs(angle);
        }
        
        // save the sprites' new position
        for(int i = 0; i < sprites.size(); i++) {
            SpriteInstance sprite = sprites.get(i);
            newPoints.add(new Point((int)sprite.x, (int)sprite.y));
        }
        
        map.flagModified();
    }
    
    public void undo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            Point oldPt = oldPoints.get(i);
            
            sprite.x = oldPt.x;
            sprite.y = oldPt.y;
            sprite.rotate(sprite.angle - angle);
        }
        
        map.flagModified();
    }
    
    public void redo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            Point newPt = newPoints.get(i);
            
            sprite.x = newPt.x;
            sprite.y = newPt.y;
            sprite.rotate(sprite.angle + angle);
        }
        
        map.flagModified();
    }
}
