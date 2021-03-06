package yarhar.cmds;

import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import pwnee.GameMath;
import yarhar.map.*;


/** Rotates the selected sprites. */
public class RotateSpriteEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public ArrayList<SpriteInstance> sprites;
    public ArrayList<Point2D.Double> oldPoints;
    public ArrayList<Point2D.Double> newPoints;
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
            oldPoints.add(new Point2D.Double(sprite.x, sprite.y));
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
            newPoints.add(new Point2D.Double(sprite.x, sprite.y));
        }
        
        map.flagModified();
    }
    
    public void undo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            Point2D oldPt = oldPoints.get(i);
            
            sprite.x = oldPt.getX();
            sprite.y = oldPt.getY();
            sprite.rotate(sprite.angle - angle);
        }
        
        map.flagModified();
    }
    
    public void redo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            Point2D newPt = newPoints.get(i);
            
            sprite.x = newPt.getX();
            sprite.y = newPt.getY();
            sprite.rotate(sprite.angle + angle);
        }
        
        map.flagModified();
    }
}
