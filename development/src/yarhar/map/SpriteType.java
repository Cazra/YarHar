package yarhar.map;

import yarhar.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import pwnee.*;
import pwnee.image.*;

public class SpriteType {
    /** The unique name given to this SpriteType. */
    public String name = "Untitled";
    
    /** The file path to the image used to represent this SpriteType in the editor. */
    public String imgPath = "";
    
    public Image curImg = null;
    
    public ImageIcon icon = null;
    
    /** If true, then the image for this SpriteType will be cropped when it is loaded. */
    public boolean isCropped = false;
    
    /** The x of the upperLeft corner of the image's cropping rectangle. */
    public int cropX = 0;
    
    /** The y of the upperLeft corner of the image's cropping rectangle. */
    public int cropY = 0;
    
    /** The width of the image's cropping rectangle. */
    public int cropW = -1;
    
    /** The height of the image's cropping rectangle. */
    public int cropH = -1;
    
    /** The offset of the SpriteType's world x relative to its image's upperleft corner. */
    public double focalX = 0;
    
    /** The offset of the SpriteType's world y relative to its image's upperLeft corner. */
    public double focalY = 0;
    
    
    public int width = -1;
    public int height = -1;
    
    
    /** Creates an Untitled sprite type with an undefined image. */
    public SpriteType() {
        this("Untitled","");
    }
    
    /** Creates a SpriteType with the given name, but an undefined image. */
    public SpriteType(String n) {
        this(n,"");
    }
    
    /** Creates a SpriteType with the given name and image. */
    public SpriteType(String n, String path) {
        this(n, path, 0, 0, -1, -1);
    }
    
    /** Creates a SpriteType with the given name, image, and crop data. */
    public SpriteType(String n, String path, int cx, int cy, int cw, int ch) {
        if(cw > 0 && ch > 0)
            isCropped = true;
        cropX = cx;
        cropY = cy;
        cropW = cw;
        cropH = ch;
        
        this.name = n;
        this.imgPath = path;
        loadImage();
        
        
    }
    
    /** Loads and crops the SpriteType's image. */
    public void loadImage() {
        ImageLoader imgLoader = new ImageLoader();
        
        if(imgPath == "") {
            curImg = imgLoader.loadFromFile("BadImg.png");
        }
        else {
            curImg = imgLoader.loadFromFile(imgPath);
            if(isCropped) {
                curImg = ImageEffects.crop(curImg, cropX, cropY, cropW, cropH);
            }
        }
        
        // wait for the image's dimensions to become available.
        while(width == -1)
            width = curImg.getWidth(null);
        while(height == -1)
            height = curImg.getHeight(null);
        
        createIcon();
    }
    
    
    public void render(Graphics2D g) {
        g.drawImage(curImg,0,0, null);
    }
    
    /** Creates the 32x32 icon for this Sprite . */
    public void createIcon() {
        BufferedImage iconImg = new BufferedImage(32,32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = iconImg.createGraphics();
        
        int maxDim = Math.max(width,height);
        double scale = 32.0/maxDim;
        g.scale(scale,scale);
        g.drawImage(curImg,0,0,null);
        
        icon = new ImageIcon(iconImg);
    }
}
