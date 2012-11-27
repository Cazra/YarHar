package yarhar.map;

import yarhar.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.io.File;
import org.json.*;
import pwnee.*;
import pwnee.image.*;

public class SpriteType implements Transferable {
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
    
    public Color transColor = null;
    
    
    public static DataFlavor flavor = new DataFlavor(SpriteType.class, SpriteType.class.getSimpleName());
    
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
    public SpriteType(String n, String path, int cx, int cy, int cw, int ch, Color tc) {
        if(cw > 0 && ch > 0)
            isCropped = true;
        cropX = cx;
        cropY = cy;
        cropW = cw;
        cropH = ch;
        
        transColor = tc;
        
        this.name = n;
        this.imgPath = path;
        loadImage();
    }
    
    public SpriteType(String n, String path, int cx, int cy, int cw, int ch) {
        this(n,path,cx,cy,cw,ch,null);
    }
    
    public SpriteType(JSONObject spriteJ) {
        loadJSON(spriteJ);
    }
    
    
    /** Creates a JSON string from this sprite type. */
    public String toJSON() {
        String result = "{";
        
        result += "\"name\":\"" + name + "\",";
        result += "\"img\":\"" + imgPath.replaceAll("\\\\","\\\\\\\\") + "\",";
        result += "\"cx\":" + cropX + ",";
        result += "\"cy\":" + cropY + ",";
        result += "\"cw\":" + cropW + ",";
        result += "\"ch\":" + cropH + ",";
        result += "\"fx\":" + focalX + ",";
        result += "\"fy\":" + focalY;
        if(transColor != null) 
            result += ",\"tc\":" + transColor.getRGB();
        
        result += "}";
        return result;
    }
    
    /** Loads this sprite type from json. */
    public void loadJSON(JSONObject spriteJ) {
        try {
            name = spriteJ.getString("name");
            imgPath = spriteJ.getString("img");
            cropX = spriteJ.getInt("cx");
            cropY = spriteJ.getInt("cy");
            cropW = spriteJ.getInt("cw");
            cropH = spriteJ.getInt("ch");
            focalX = spriteJ.getInt("fx");
            focalY = spriteJ.getInt("fy");
            
            if(spriteJ.has("tc"))
                transColor = new Color(spriteJ.getInt("tc"));
            
            if(cropW > 0 && cropH > 0)
                isCropped = true;
            
            loadImage();
        }
        catch (Exception e) {
            System.err.println("Error reading JSON for sprite type.");
        }
    }
    
    
    /** Loads and crops the SpriteType's image. */
    public void loadImage() {
        
        ImageLoader imgLoader = new ImageLoader(new JPanel());
        if(imgPath == "") {
            curImg = imgLoader.loadFromFile("BadImg.png");
        }
        else {
            try {
                curImg = ImageIO.read(new File(imgPath));
            }
            catch (Exception e) {
                curImg = imgLoader.loadFromFile("BadImg.png");
            }
        }

        // wait for the image's dimensions to become available.
        while(width == -1)
            width = curImg.getWidth(null);
        while(height == -1)
            height = curImg.getHeight(null);
        
        // crop if it needs cropping
        if(isCropped) {
            curImg = ImageEffects.crop(curImg, cropX, cropY, cropW, cropH);
            width = cropW;
            height = cropH;
        }
        
        // set the transparent color if there is one.
        if(transColor != null) {
            curImg = ImageEffects.setTransparentColor(curImg, transColor);
        }
        
        // finish loading the image.
        imgLoader.addImage(curImg);
        imgLoader.waitForAll();
        
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
    
    
    /** Creates an SpriteInstance from this SpriteType. */
    public SpriteInstance createInstance(double x, double y) {
        return new SpriteInstance(x,y,this);
    }
    
    
    /** Used for drag and drop */
    public Object getTransferData(DataFlavor flavor) {
        if(flavor.equals(SpriteType.flavor))
            return this;
        else
            return null;
    }
    
    /** Used for drag and drop */
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {SpriteType.flavor};
    }
    
    /** Used for drag and drop */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(SpriteType.flavor);
    }
    
}

