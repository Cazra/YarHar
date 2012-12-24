package yarhar.map;

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
import yarhar.*;
import yarhar.images.ImageLibrary;

public class SpriteType implements Transferable {
    /** The unique name given to this SpriteType. */
    public String name = "Untitled";
    
    /** The ImageLibrary used to render this. */
    public ImageLibrary imgLib = null;

    /** The image used to render this, obtained from imbLib. */
    public Image curImg = null;
    
    /** An 32x32 icon version of curImg. */
    public ImageIcon icon = null;
    
    /** The offset of the SpriteType's world x relative to its image's upperleft corner. */
    public double focalX = 0;
    
    /** The offset of the SpriteType's world y relative to its image's upperLeft corner. */
    public double focalY = 0;
    
    /** The image's width. */
    public int width = -1;
    
    /** The image's height. */
    public int height = -1;
    
    
    public static DataFlavor flavor = new DataFlavor(SpriteType.class, SpriteType.class.getSimpleName());
    
    /** Creates an Untitled sprite type with an undefined image. */
    public SpriteType() {
        this("Untitled",new ImageLibrary(), null);
    }
    
    /** Creates a SpriteType with the given name, but an undefined image. */
    public SpriteType(String n) {
        this(n,new ImageLibrary(), null);
    }
    
    /** Creates a SpriteType with the given name and image. */
    public SpriteType(String n, ImageLibrary imgLib, Image img) {
        this.name = n;
        this.imgLib = imgLib;
        if(img != null) 
          imgLib.put(name, img);
        loadImage();
    }
    
    public SpriteType(JSONObject spriteJ) {
        loadJSON(spriteJ);
    }
    
    
    /** Creates a JSON string from this sprite type. */
    public String toJSON() {
        String result = "{";
        
        result += "\"name\":\"" + name + "\",";
        result += "\"fx\":" + focalX + ",";
        result += "\"fy\":" + focalY;
        
        result += "}";
        return result;
    }
    
    /** Loads this sprite type from json. */
    public void loadJSON(JSONObject spriteJ) {
        try {
            name = spriteJ.getString("name");
            focalX = spriteJ.getInt("fx");
            focalY = spriteJ.getInt("fy");
            
            loadImage();
        }
        catch (Exception e) {
            System.err.println("Error reading JSON for sprite type.");
        }
    }
    
    
    /** Loads and crops the SpriteType's image. */
    public void loadImage() {
        
        ImageLoader imgLoader = new ImageLoader(new JPanel());
        
        width = -1;
        height = -1;
        
        // try to obtain the image of this sprite type from the ImageLibrary it is currently using.
        // Use a default bad image if it can't get its image.
        if(imgLib == null) {
          curImg = ImageLibrary.getBadImg();
        }
        else {
          curImg = imgLib.get(this.name);
        }

        // wait for the image's dimensions to become available.
        while(width == -1)
            width = curImg.getWidth(null);
        while(height == -1)
            height = curImg.getHeight(null);
        
        
        // finish loading the image.
        imgLoader.addImage(curImg);
        imgLoader.waitForAll();
        
        createIcon();
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
    
    
    /** Sets this sprite's ImageLibrary and updates its image. */
    public void setImgLib(ImageLibrary lib) {
      imgLib = lib;
      loadImage();
    }
    
    
    /** Updates this sprite's image. */
    public void setImage(Image img) {
      imgLib.put(name, img);
      loadImage();
    }
    
    
    public void render(Graphics2D g) {
        g.drawImage(curImg,0,0, null);
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
    
    
    
    public String toString() {
        return name;
    }
}

