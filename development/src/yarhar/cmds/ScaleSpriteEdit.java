package yarhar.cmds;

import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import pwnee.GameMath;
import yarhar.map.*;


/** Scales the selected sprites. */
public class ScaleSpriteEdit extends SimpleUndoableEdit {
    
    public LevelMap map;
    public ArrayList<SpriteInstance> sprites;
    public ArrayList<Point2D.Double> oldPoints = new ArrayList();
    public ArrayList<Point2D.Double> newPoints = new ArrayList();
    public ArrayList<Double> oldUnis = new ArrayList();
    public ArrayList<Double> newUnis = new ArrayList();
    public ArrayList<Point2D.Double> oldScales = new ArrayList();
    public ArrayList<Point2D.Double> newScales = new ArrayList();
    
    public ScaleSpriteEdit(LevelMap map, double uni, double x, double y, boolean isRelative) {
        super();
        this.map = map;
        
        sprites = new ArrayList(map.selectedSprites);
        
        // save the sprites' old position
        for(int i = 0; i < sprites.size(); i++) {
            SpriteInstance sprite = sprites.get(i);
            oldPoints.add(new Point2D.Double(sprite.x, sprite.y));
            oldUnis.add(sprite.scaleUni);
            oldScales.add(new Point2D.Double(sprite.scaleX, sprite.scaleY));
        }
        
        // scale the sprites about their center of mass.
        if(isRelative) {
            map.scaleSelectedSprites(uni,x,y);
        }
        else {
            map.scaleSpriteAbs(uni,x,y);
        }
        
        // save the sprites' new position
        for(int i = 0; i < sprites.size(); i++) {
            SpriteInstance sprite = sprites.get(i);
            newPoints.add(new Point2D.Double(sprite.x, sprite.y));
            newUnis.add(sprite.scaleUni);
            newScales.add(new Point2D.Double(sprite.scaleX, sprite.scaleY));
        }
        
        map.flagModified();
    }
    
    public void undo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            Point2D.Double oldPt = oldPoints.get(i);
            Point2D.Double oldScale = oldScales.get(i);
            
            sprite.x = oldPt.getX();
            sprite.y = oldPt.getY();
            sprite.scaleUni = oldUnis.get(i);
            sprite.scaleX = oldScale.getX();
            sprite.scaleY = oldScale.getY();
            sprite.transformChanged = true;
        }
        
        map.flagModified();
    }
    
    public void redo() {
        for(int i = 0; i < sprites.size(); i ++) {
            SpriteInstance sprite = sprites.get(i);
            Point2D.Double newPt = newPoints.get(i);
            Point2D.Double newScale = newScales.get(i);
            
            sprite.x = newPt.getX();
            sprite.y = newPt.getY();
            sprite.scaleUni = newUnis.get(i);
            sprite.scaleX = newScale.getX();
            sprite.scaleY = newScale.getY();
            sprite.transformChanged = true;
        }
        
        map.flagModified();
    }
}
