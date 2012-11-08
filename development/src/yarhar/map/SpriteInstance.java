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
import pwnee.*;
import pwnee.image.*;
import pwnee.sprites.*;



public class SpriteInstance extends Sprite {
    
    public SpriteType type;
    public double startDragX = 0;
    public double startDragY = 0;
    
    public boolean isSelected = false;
    
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

    
    public void draw(Graphics2D g) {
        this.focalX = type.focalX;
        this.focalY = type.focalY;
        
        type.render(g);
        
        if(isSelected) {
            g.setColor(new Color(0x00DDDD));
            g.drawRect(0-(int)focalX, 0-(int)focalY, type.width, type.height);
        }
    }
    
    
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
        
        System.err.println(mx + ", " + my); 
        
        return (mx >= 0 && my >= 0 && mx < width && my < height);
    }
}
