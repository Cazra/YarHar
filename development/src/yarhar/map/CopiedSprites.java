package yarhar.map;

import java.awt.datatransfer.*;
import java.util.LinkedList;
import org.json.*;


/** 
 * A transferable selection of sprites. 
 * I avoided serializing the SpriteInstances by having this Transferable store the sprites as their JSON strings, 
 * which are already serializable. The constructor stores the sprites internally as their JSON and getSprites(lib) 
 * can be used to obtain the list of sprite instances represented by these JSON strings.
 */
public class CopiedSprites<E extends String> extends LinkedList<E> implements Transferable {
    
    public static DataFlavor flavor = new DataFlavor(CopiedSprites.class, CopiedSprites.class.getSimpleName());
    
    public CopiedSprites(LinkedList<SpriteInstance> sourceList) {
        super();
        for(SpriteInstance sprite : sourceList) {
            E copy = (E) sprite.toJSON();
            this.add(copy);
        }
    }
    
    /** Used for copypasta */
    public Object getTransferData(DataFlavor flavor) {
        if(flavor.equals(CopiedSprites.flavor))
            return this;
        else
            return null;
    }
    
    /** Used for copypasta */
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {CopiedSprites.flavor};
    }
    
    /** Used for copypasta */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(CopiedSprites.flavor);
    }
    
    
    /** 
     * Returns our list of SpriteInstances. lib is the SpriteLibrary used by our current map. 
     * If a copied sprite's type is does not exist in lib, then it will not be included in the
     * returned list of SpriteInstances.
     */
    public LinkedList<SpriteInstance> getSprites(SpriteLibrary lib) {
        LinkedList<SpriteInstance> result = new LinkedList<SpriteInstance>();
        for(E jsonStr : this) {
            try {
                JSONObject json = new JSONObject(jsonStr);
                SpriteInstance sprite = new SpriteInstance(json, lib);
                if(sprite.type != null) 
                    result.add(sprite);
            }
            catch (Exception e) {
                // Pokemon exception : gotta catch 'em all!
            }
        }
        
        return result;
    }
    
    
}