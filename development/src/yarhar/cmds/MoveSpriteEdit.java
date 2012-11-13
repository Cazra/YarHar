package yarhar.cmds;

import java.util.ArrayList;
import java.awt.Point;
import yarhar.map.*;


/** Moves the selected sprites. This is intended to be created after the sprites have been moved. */
public class MoveSpriteEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public ArrayList<SpriteInstance> sprites;
    public ArrayList<Point> oldPoints;
    public ArrayList<Point> newPoints;
    
    public MoveSpriteEdit(LevelMap map) {
        super();
        this.map = map;
        
        sprites = new ArrayList(map.selectedSprites);
        oldPoints = new ArrayList();
        newPoints = new ArrayList();
        
        for(int i = 0; i < sprites.size(); i++) {
            SpriteInstance sprite = sprites.get(i);
            oldPoints.add(new Point((int)sprite.startDragX, (int)sprite.startDragY));
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
        }
        
        map.flagModified();
    }
    
    public void redo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            Point newPt = newPoints.get(i);
            
            sprite.x = newPt.x;
            sprite.y = newPt.y;
        }
        
        map.flagModified();
    }
}
