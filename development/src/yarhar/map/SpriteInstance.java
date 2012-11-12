package yarhar.map;

import yarhar.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.io.File;
import org.json.*;
import pwnee.*;
import pwnee.image.*;
import pwnee.sprites.*;



public class SpriteInstance extends Sprite {
    
    public SpriteType type;
    public double startDragX = 0;
    public double startDragY = 0;
    
    public boolean isSelected = false;
    
    public boolean isGhost = false;
    
    public SpriteInstance(double x, double y, SpriteType type) {
        super(x,y);
        this.type = type;
        this.focalX = type.focalX;
        this.focalY = type.focalY;
        this.width = type.width;
        this.height = type.height;
    }
    
    public SpriteInstance(SpriteType type) {
        this(0,0,type);
    }
    
    public SpriteInstance(double x, double y, SpriteType type, boolean ghost) {
        this(x,y,type);
        if(ghost) {
            isGhost = true;
            opacity = 0.5;
        }
    }
    
    public SpriteInstance(JSONObject spriteJ, SpriteLibrary lib) {
        super(0,0);
        loadJSON(spriteJ, lib);
        this.focalX = type.focalX;
        this.focalY = type.focalY;
        this.width = type.width;
        this.height = type.height;
        transformChanged = true;
    }
    
    
    public SpriteInstance makeClone() {
        SpriteInstance clone = new SpriteInstance(this.x, this.y, this.type);
        
        clone.angle = this.angle;
        clone.opacity = this.opacity;
        clone.scaleUni = this.scaleUni;
        clone.scaleX = this.scaleX;
        clone.scaleY = this.scaleY;
        clone.transformChanged = true;
        clone.startDragX = this.startDragX;
        clone.startDragY = this.startDragY;
        
        return clone;
    }
    
    
    /** Creates a JSON string representing this sprite. */
    public String toJSON() {
        String result = "{";
        
        result += "\"type\":\"" + type.name + "\",";
        result += "\"x\":" + x + ",";
        result += "\"y\":" + y + ",";
        result += "\"a\":" + angle + ",";
        result += "\"o\":" + opacity + ",";
        result += "\"s\":" + scaleUni + ",";
        result += "\"sx\":" + scaleX + ",";
        result += "\"sy\":" + scaleY;
        
        result += "}";
        return result;
    }
    
    public void loadJSON(JSONObject spriteJ, SpriteLibrary lib) {
        try {
            type = lib.sprites.get(spriteJ.getString("type"));
            x = spriteJ.getDouble("x");
            y = spriteJ.getDouble("y");
            angle = spriteJ.getDouble("a");
            opacity = spriteJ.getDouble("o");
            scaleUni = spriteJ.getDouble("s");
            scaleX = spriteJ.getDouble("sx");
            scaleY = spriteJ.getDouble("sy");
            
        }
        catch(Exception e) {
            System.err.println("Error reading JSON for sprite instance");
        }
    }

    
    public void draw(Graphics2D g) {
        this.focalX = type.focalX;
        this.focalY = type.focalY;
        
        type.render(g);
        
        if(isSelected) {
            g.setColor(new Color(0x00DDDD));
            g.drawRect(0, 0, type.width, type.height);
        }
    }
    
    
    /** Returns true if the mouse is over this sprite's bounding box. */
    public boolean isClicked(Point mouseScr) {
        AffineTransform curInv;
        try {
            curInv = curTrans.createInverse();
        }
        catch (Exception e) {
            return false;
        }
        Point2D mouseRel = null;
        mouseRel = curInv.transform(mouseScr, mouseRel);
        
        double mx = mouseRel.getX();
        double my = mouseRel.getY();
        
        return (mx >= 0 && my >= 0 && mx < width && my < height);
    }
}
