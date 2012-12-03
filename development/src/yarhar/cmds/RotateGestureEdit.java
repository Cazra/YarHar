package yarhar.cmds;

import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import pwnee.GameMath;
import yarhar.map.*;


/** Rotates the selected sprites. */
public class RotateGestureEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public ArrayList<SpriteInstance> sprites;
    public ArrayList<Point2D.Double> oldPoints = new ArrayList();
    public ArrayList<Point2D.Double> newPoints = new ArrayList();
    public ArrayList<Double> oldAngles = new ArrayList();
    public ArrayList<Double> newAngles = new ArrayList();
    
    public RotateGestureEdit(LevelMap map) {
        super();
        this.map = map;
        
        sprites = new ArrayList(map.selectedSprites);
        
        // save the sprites' old position
        for(int i = 0; i < sprites.size(); i++) {
            SpriteInstance sprite = sprites.get(i);
            oldPoints.add(new Point2D.Double(sprite.startDragX, sprite.startDragY));
            newPoints.add(new Point2D.Double(sprite.x, sprite.y));
            oldAngles.add(sprite.startAngle);
            newAngles.add(sprite.angle);
        }
        
        map.flagModified();
    }
    
    public void undo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            Point2D oldPt = oldPoints.get(i);
            
            sprite.x = oldPt.getX();
            sprite.y = oldPt.getY();
            sprite.rotate(oldAngles.get(i));
        }
        
        map.flagModified();
    }
    
    public void redo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            Point2D newPt = newPoints.get(i);
            
            sprite.x = newPt.getX();
            sprite.y = newPt.getY();
            sprite.rotate(newAngles.get(i));
        }
        
        map.flagModified();
    }
}
