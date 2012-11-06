package yarhar.map;

import yarhar.*;
import java.awt.*;
import pwnee.*;
import java.util.LinkedList;
import java.io.File;

/** The top level object for a map being manipulated with the YarHar UI. */
public class LevelMap extends Level {
    
    /** Same as the filename for this map (just the name, not the whole path or extension). */
    public String name = "New Map";
    
    /** the background color displayed in the YarHar editor for this map. */
    public int bgColor = 0xFFFFFF;
    
    /** This map's Sprite library. */
    public SpriteLibrary spriteLib;
    
    /** The list of this map's layers in order from top to bottom. */
    public LinkedList<Layer> layers = new LinkedList<Layer>();
    
    /** a short description of this map's intended use. */
    public String desc = "";
    
    /** If true, a grid is displayed in the editor. */
    public boolean displayGrid = true;
    
    /** The width of the grid displayed under (or above) the map. */
    public int gridW = 640;
    
    /** The height of the grid displayed under (or above) the map. */
    public int gridH = 480;
    
    /** The distance between vertical lines in the grid. */
    public int gridSpaceX = 32;
    
    /** The distance between horizontal lines in the grid. */
    public int gridSpaceY = 32;
    
    /** The color of the grid's lines */
    public int gridColor = 0xFF0000;
    
    /** Flag to tell if the map has been modified. */
    public boolean isModified = false;
    
    
    /** Creates a blank map With an unpopulated sprite library and just one layer. */
    public LevelMap(EditorPanel game) {
        this(game,null);
    }
    
    /** Load the map from a JSON text file */
    public LevelMap(EditorPanel game, File file) {
        super(game);
        
        if(file == null) {
            spriteLib = new SpriteLibrary(this);
            layers.add(new Layer("Foreground"));
            layers.add(new Layer());
            layers.add(new Layer("Background"));
        }
        else {
            // TODO : Construct the entire map from the JSON in file.
        }
        
        ((EditorPanel) game).frame.updateTitle(name);
        ((EditorPanel) game).frame.layersPanel.setMap(this);
        ((EditorPanel) game).frame.spriteLibPanel.setLibrary(this.spriteLib);
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
        // render each of the layers in order of descending index. (The last layer is rendered on the bottom and the first layer is rendered on top)
        for(int i = layers.size() - 1; i >= 0; i--) {
            layers.get(i).render(g);
        }
        
        // render the grid with translucency
        Composite oldComp = g.getComposite();
        
        // Use an AlphaComposite to apply semi-transparency to the Sprite's image.
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.25));
        renderGrid(g);
        g.setComposite(oldComp);
    }
    
    
    public void renderGrid(Graphics2D g) {
        if(!displayGrid)
            return;
        
        g.setColor(new Color(gridColor));
        for(int i = 0; i <= gridW; i += gridSpaceX) {
            g.drawLine(i, 0 , i, gridH);
        }
        for(int i = 0; i <= gridH; i += gridSpaceY) {
            g.drawLine(0,i,gridW,i);
        }
        
        g.drawRect(0,0,gridW,gridH);
    }
    
}

