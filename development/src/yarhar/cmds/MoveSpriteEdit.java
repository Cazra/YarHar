package yarhar.cmds;

import java.util.ArrayList;
import java.awt.geom.Point2D;
import yarhar.map.*;


/** Moves the selected sprites. This is intended to be created after the sprites have been moved. */
public class MoveSpriteEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public ArrayList<SpriteInstance> sprites;
    public ArrayList<Point2D.Double> oldPoints;
    public ArrayList<Point2D.Double> newPoints;
    
    public MoveSpriteEdit(LevelMap map) {
        super();
        this.map = map;
        
        sprites = new ArrayList(map.selectedSprites);
        oldPoints = new ArrayList();
        newPoints = new ArrayList();
        
        for(int i = 0; i < sprites.size(); i++) {
            SpriteInstance sprite = sprites.get(i);
            oldPoints.add(new Point2D.Double(sprite.startDragX, sprite.startDragY));
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
        }
        
        map.flagModified();
    }
    
    public void redo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            Point2D newPt = newPoints.get(i);
            
            sprite.x = newPt.getX();
            sprite.y = newPt.getY();
        }
        
        map.flagModified();
    }
}
