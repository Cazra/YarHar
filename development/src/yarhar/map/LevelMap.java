package yarhar.map;

import yarhar.*;
import java.awt.*;
import pwnee.*;

/** The top level object for a map being manipulated with the YarHar UI. */
public class LevelMap extends Level {
    
    /** Same as the filename for this map (just the name, not the whole path or extension). */
    String name = "New Map";
    
    /** the background color displayed in the YarHar editor for this map. */
    int bgColor = 0xFFFFFF;
    
    // This map's Sprite library.
    // SpriteLibrary spriteLib;
    
    // The list of this map's layers in order from top to bottom.
    // ArrayList<Layer> layers;
    
    /** a short description of this map's intended use. */
    String desc = "";
    
    /** The width of the grid displayed under (or above) the map. */
    double gridW = 640;
    
    /** The height of the grid displayed under (or above) the map. */
    double gridH = 480;
    
    /** The distance between vertical lines in the grid. */
    double gridSpaceX = 32;
    
    /** The distance between horizontal lines in the grid. */
    double gridSpaceY = 32;
    
    /** The color of the grid's lines */
    int gridColor = 0xFFAAAA;
    
    /** Flag to tell if the map has been modified. */
    boolean isModified = false;
    
    
    /** Creates a blank map */
    public LevelMap(EditorPanel game) {
        super(game);
    }
    
    
    public void clean() {
        if(isModified) {
            // TODO : halt and prompt user to save the map
        }
    }
    
    public void loadData() {
    
    }
    
    public void logic() {
    
    }
    
    public void render(Graphics2D g) {
        
    }
    
}

