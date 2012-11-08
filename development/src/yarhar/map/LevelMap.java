package yarhar.map;

import yarhar.*;
import java.awt.*;
import java.awt.event.KeyEvent;
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
    
    /** If true, the grid snapping for placing and moving sprites will be enabled. */
    public boolean snapToGrid = false;
    
    /** The width of the grid displayed under (or above) the map. */
    public int gridW = 640;
    
    /** The height of the grid displayed under (or above) the map. */
    public int gridH = 480;
    
    /** The distance between vertical lines in the grid. */
    public int gridSpaceX = 32;
    
    /** The distance between horizontal lines in the grid. */
    public int gridSpaceY = 32;
    
    /** The color of the grid's lines */
    public Color gridColor = new Color(0xFF0000);
    
    /** Flag to tell if the map has been modified. */
    public boolean isModified = false;
    
    /** A list of sprites currently selected. */ 
    public LinkedList<SpriteInstance> selectedSprites = new LinkedList<SpriteInstance>();
    
    /** The currently selected sprite */
    public SpriteInstance selectedSprite = null;
    
    public int dragStartX = 0;
    public int dragStartY = 0;
    public boolean isDrag = false;
    
    
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
        
        if(mouse.justLeftClicked) {
            isDrag = false;
        }
        
        // Click a sprite.
        if(mouse.justLeftPressed) {
            selectedLayer.selectAll(false);
            
            selectedSprite = selectedLayer.tryClickSprite(mouse.position);
            if(selectedSprite != null) {
                // if ctrl was held, create a copy of the sprite to move instead of moving the actual sprite.
                if(keyboard.isPressed(KeyEvent.VK_CONTROL)) {
                    selectedSprite = dropSpriteType(selectedSprite.type, new Point((int)selectedSprite.x, (int)selectedSprite.y));
                }
                
                // move the sprite with the mouse
            //    dragOffX = mouseWorld.x - selectedSprite.x;
            //    dragOffY = mouseWorld.y - selectedSprite.y;
                dragStartX = mouseWorld.x;
                dragStartY = mouseWorld.y;
                
                selectedSprite.startDragX = selectedSprite.x;
                selectedSprite.startDragY = selectedSprite.y;
                
                
                selectedSprite.isSelected = true;
            }
            System.err.println(selectedSprite);
            
        }
        
        // Drag the selected sprite.
        if(mouse.isLeftPressed && selectedSprite != null) {
            if(mouseWorld.x != dragStartX || mouseWorld.y != dragStartY)
                isDrag = true;
            
            if(isDrag) {
                dragSprite(selectedSprite, mouseWorld);
                snapSprite(selectedSprite);
                isModified = true;
            }
        }
        
        // Press Delete to delete the currently selected sprites.
        
    }
    
    
    /** Obtains the mouse's current world coordinates in integer form. */
    public Point getMouseWorld() {
        Point mouseScr = mouse.position;
        Point2D mouseWorld = camera.screenToWorld(mouseScr);
        int mx = (int) mouseWorld.getX();
        int my = (int) mouseWorld.getY();
        
        return new Point(mx,my);
    }
    
    
    /** Converts a world coordinate point to snapped world coordinates if grid snap is enabled. */
    public Point getSnappedCoords(Point worldPt) {
        if(snapToGrid) {
            int x = (worldPt.x/gridSpaceX)*gridSpaceX;
            int y = (worldPt.y/gridSpaceY)*gridSpaceY;
            return new Point(x,y);
        }   
        else
            return worldPt;
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
    public SpriteInstance dropSpriteType(SpriteType spriteType, Point mouseWorld) {
        SpriteInstance sprite = selectedLayer.dropSpriteType(spriteType, getSnappedCoords(mouseWorld));
        isModified = true;
        return sprite;
    }
    
    /** Snaps a sprite to the grid*/
    public void snapSprite(SpriteInstance sprite) {
        if(snapToGrid) {
            sprite.x = ((int)sprite.x/gridSpaceX)*gridSpaceX;
            sprite.y = ((int)sprite.y/gridSpaceY)*gridSpaceY;
        }
    }
    
    /** Drags a sprite with the mouse */
    public void dragSprite(SpriteInstance sprite, Point mouseWorld) {
        int mdx = mouseWorld.x - dragStartX;
        int mdy = mouseWorld.y - dragStartY;
        
        sprite.x = sprite.startDragX + mdx;
        sprite.y = sprite.startDragY + mdy;
    }
    
    
    
    /** Renders the map */
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
        
        g.setColor(gridColor);
        for(int i = 0; i <= gridW; i += gridSpaceX) {
            g.drawLine(i, 0 , i, gridH);
        }
        for(int i = 0; i <= gridH; i += gridSpaceY) {
            g.drawLine(0,i,gridW,i);
        }
        
        g.drawRect(0,0,gridW,gridH);
    }
    
}

