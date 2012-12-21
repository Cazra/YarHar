package yarhar.images;

import java.awt.Image;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import pwnee.fileio.ObjectFileIO;
import pwnee.image.ImageLoader;
import pwnee.image.SerializedImage;

/** A library of SpriteType names mapped to serialized images. */
public class ImageLibrary implements Serializable {
  /** Each serialized image is keyed by the name of the SpriteType it belongs to. */
  public HashMap<String, SerializedImage> images = null;
  
  /** Constructs an empty ImageLibrary. */
  public ImageLibrary() {
    images = new HashMap<String, SerializedImage>();
  }
  
  
  /** Returns the unserialized Image associated with key. */
  public Image get(String key) {
    try {
      SerializedImage simg = images.get(key);
      Image result = simg.toImage();
      return result;
    }
    catch (Exception e) {
      System.err.println("ImageLibrary could not get image for " + key);
      return getBadImg();
    }
  }
  
  
  /** Serializes an image and binds it to key. */
  public void put(String key, Image img) {
    try {
      SerializedImage simg = new SerializedImage(img);
      images.put(key,simg);
    }
    catch (Exception e) {
      System.err.println("ImageLibrary could not put image for " + key);
    }
  }
  
  
  /** Returns a default "bad" image. */
  public static Image getBadImg() {
    ImageLoader imgLoader = new ImageLoader();
    Image img = imgLoader.loadFromFile("BadImg.png");
    return img;
  }
}
