package yarhar.map;

import yarhar.*;
import java.awt.*;
import java.awt.datatransfer.*;
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


/** Represents an instance of a SpriteType that resides in a layer in the editor area. */
public class SpriteInstance extends Sprite implements Transferable {
    
    public SpriteType type;
    public int zIndex = -1;
    public double startDragX = 0;
    public double startDragY = 0;
    public double startAngle = 0;
    public double startScaleX = 0;
    public double startScaleY = 0;
    public double startRepeatX = 0;
    public double startRepeatY = 0;
    
    public double repeatX = 1.0;
    public double repeatY = 1.0;
    
    public boolean isSelected = false;
    
    public static DataFlavor flavor = new DataFlavor(SpriteInstance.class, SpriteInstance.class.getSimpleName());
    
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
        
        // draw the sprite's image, repeating if necessary.
        if(repeatX != 1.0 || repeatY != 1.0) {
          Shape origClip = g.getClip();
          g.clipRect(0,0,(int) getWidth(), (int) getHeight());
          
          AffineTransform origTrans = g.getTransform();
        
          for(double i = 0; i < getWidth(); i+=type.width) {
            for(double j = 0; j < getHeight(); j+= type.height) {
              g.translate(i,j);
              type.render(g);
              g.setTransform(origTrans);
            }
          }
          
          g.setClip(origClip);
        }
        else {
          type.render(g);
        }
        
        
        
        if(isSelected) {
            g.setColor(new Color(0x00DDDD));
            g.drawRect(0, 0, (int) getWidth(), (int) getHeight());
        }
    }
    
    
    /** Returns the calculated width of this instance. */
    public double getWidth() {
      return type.width*repeatX;
    }
    
    /** Returns the calculated height of this instance. */
    public double getHeight() {
      return type.height*repeatY;
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
        
        return (mx >= 0 && my >= 0 && mx < getWidth() && my < getHeight());
    }
    
    
    
    /** Used for copypasta */
    public Object getTransferData(DataFlavor flavor) {
        if(flavor.equals(SpriteInstance.flavor))
            return this;
        else
            return null;
    }
    
    /** Used for copypasta */
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {SpriteInstance.flavor};
    }
    
    /** Used for copypasta */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(SpriteInstance.flavor);
    }
    
}
