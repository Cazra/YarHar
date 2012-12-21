package yarhar.fileio;

import java.io.Serializable;
import pwnee.fileio.ObjectFileIO;
import yarhar.images.ImageLibrary;

/** 
 * A serialized object used to save and load yarhar maps. 
 * It contains only two parts: 
 * - The ImageLibrary associated with the map.
 * - The complete json String for the map.
 * 
 */
public class YarharFile implements Serializable {
  /** The map's library of images. */
  public ImageLibrary imgLib;
  
  /** The complete json string for the map. */
  public String jsonStr;

  
  
  public YarharFile(ImageLibrary il, String js) {
    imgLib = il;
    jsonStr = js;
  }
  
  
  /** Saves the YarharFile in compressed, serialized form to the file specified by path. */
  public boolean saveCompressed(String path) {
    ObjectFileIO ofio = new ObjectFileIO(); 
    
    return ofio.saveObject(this, path, ObjectFileIO.COMPRESS);
  }
  
  /** Constructs a YarharFile from the file at path containing a YarharFile's compressed, serialized data. */
  public static YarharFile loadCompressed(String path) {
    ObjectFileIO ofio = new ObjectFileIO(); 
    
    return (YarharFile) ofio.loadObject(path, ObjectFileIO.DECOMPRESS);
  }
}

