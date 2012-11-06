package yarhar.map;

import yarhar.*;
import java.awt.*;
import java.awt.geom.Point2D;
import pwnee.*;
import java.util.LinkedList;
import java.io.File;

/** The top level object for a map being manipulated with the YarHar UI. */
public class LevelMap extends Level {
    
    /** Same as the filename for this map (just the name, not the whole path or extension). */
    public String name = "New Map";
    
    /** the background color displayed in the YarHar editor for this map. */
    public int bgColor = 0xFFFFFF;
    
    /** A reference to the editor's camera. */
    public Camera camera;
    
    /** This map's Sprite library. */
    public SpriteLibrary spriteLib;
    
    /** The list of this map's layers in order from top to bottom. */
    public LinkedList<Layer> layers = new LinkedList<Layer>();
    
    public Layer selectedLayer = null;
    
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
    
    /** A list of sprites currently selected. */ 
    public LinkedList<SpriteInstance> selectedSprites = new LinkedList<SpriteInstance>();
    
    /** The currently selected sprite */
    public SpriteInstance selectedSprite = null;
    
    public double dragOffX = 0;
    public double dragOffY = 0;
    
    
    /** Creates a blank map With an unpopulated sprite library and just one layer. */
    public LevelMap(EditorPanel game) {
        this(game,null);
    }
    
    /** Load the map from a JSON text file */
    public LevelMap(EditorPanel game, File file) {
        super(game);
        
        if(file == null) {
            spriteLib = new SpriteLibrary(this);
            addLayer(new Layer("Foreground"));
            addLayer(new Layer());
            addLayer(new Layer("Background"));
        }
        else {
            // TODO : Construct the entire map from the JSON in file.
        }
        
        game.frame.updateTitle(name);
        game.frame.layersPanel.setMap(this);
        game.frame.spriteLibPanel.setLibrary(this.spriteLib);
        
        camera = game.camera;
    }
    
    
    public void clean() {
        if(isModified) {
            // TODO : halt and prompt user to save the map
        }
    }
    
    public void loadData() {
    
    }
    
    public void logic() {
        Point mouseWorld = getMouseWorld();
        
        // Click a sprite.
        if(mouse.justLeftPressed) {
            selectedLayer.selectAll(false);
            
            selectedSprite = selectedLayer.tryClickSprite(mouse.position);
            if(selectedSprite != null) {
                dragOffX = mouseWorld.x - selectedSprite.x;
                dragOffY = mouseWorld.y - selectedSprite.y;
                selectedSprite.isSelected = true;
            }
            System.err.println(selectedSprite);
            
        }
        
        // Drag the selected sprite.
        if(mouse.isLeftPressed && selectedSprite != null) {
            selectedSprite.x = (int) (mouseWorld.x - dragOffX);
            selectedSprite.y = (int) (mouseWorld.y - dragOffY);
        }
    }
    
    
    /** Obtains the mouse's current world coordinates in integer form. */
    public Point getMouseWorld() {
        Point mouseScr = mouse.position;
        Point2D mouseWorld = camera.screenToWorld(mouseScr);
        int mx = (int) mouseWorld.getX();
        int my = (int) mouseWorld.getY();
        
        return new Point(mx,my);
    }
    
    
    /** Adds a layer to this map and causes it to become selected. */
    public void addLayer(Layer layer) {
        layers.add(layer);
        selectedLayer = layer;
        
        isModified = true;
    }
    
    /** Moves a layer to another index */
    public void moveLayer(Layer layer, int destIndex) {
        int index = layers.indexOf(layer);

        layers.remove(layer);
        layers.add(destIndex, layer);
        selectedLayer = layer;
        
        isModified = true;
    }
    
    /** Drops a new sprite into the currently selected layer. */
    public void dropSpriteType(SpriteType spriteType, Point mouseWorld) {
        selectedLayer.dropSpriteType(spriteType, mouseWorld);
        
        isModified = true;
    }
    
    
    public void render(Graphics2D g) {
        synchronized(this) {
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
    }
    
    
    /** Renders the grid for this map */
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

