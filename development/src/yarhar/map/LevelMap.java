package yarhar.map;

import yarhar.*;
import yarhar.cmds.*;
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
    public boolean snapToGrid = true;
    
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
    
    /** True if we are currently dragging the mouse to form a selection rectangle. */
    public boolean isSelRect = false;
    
    /** Color of the selection rectangle */
    public Color selRectColor = new Color(0x007777);
    
    
    /** The mouse's most recently recorded world coordinates */
    public Point mouseWorld = new Point(0,0);
    
    /** The starting world X of the mouse's last drag. */
    public int dragStartX = 0;
    
    /** The starting world Y of the mouse's last drag. */
    public int dragStartY = 0;
    
    /** True if sprites are currently being dragged. */
    public boolean isDrag = false;
    
    /** True if sprites are currently being cloned. */
    public boolean isCloning = false;
    
    
    
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
        mouseWorld = getMouseWorld();
        
        
        // When left click is released, resolve any operations associated with the mouse gesture.
        if(mouse.justLeftClicked) {
            // if we just finished making a selection rectangle, select all the sprites in the selection rectangle.
            if(isSelRect)
                selectionRectangle();
            
            // if we finished moving a set of sprites, finish moving them. 
            if(isDrag && !isCloning) {
                MoveSpriteEdit cmd = new MoveSpriteEdit(this);
            }
            
            // if we finished cloning a set of sprites, finish cloning them.
            if(isCloning) {
                CloneSpriteEdit cmd = new CloneSpriteEdit(this);
            }
            
            isDrag = false;
            isSelRect = false;
            isCloning = false;
        }
        
        // Click a sprite.
        if(mouse.justLeftPressed) {
            selectedSprite = selectedLayer.tryClickSprite(mouse.position);
            dragStartX = mouseWorld.x;
            dragStartY = mouseWorld.y;
            
            // possibly unselect all sprites.
            if(!keyboard.isPressed(KeyEvent.VK_SHIFT) && !keyboard.isPressed(KeyEvent.VK_CONTROL) && !selectedSprites.contains(selectedSprite))
                unselectAll();
            
            // if a sprite was click, select it!
            if(selectedSprite != null) {
                selectSprite(selectedSprite);
                initDragSprites();
            }
        }
        
        // Drag the selected sprite(s).
        if(mouse.isLeftPressed && selectedSprite != null) {
            if((mouseWorld.x != dragStartX || mouseWorld.y != dragStartY) && !isDrag) {
                isDrag = true;
                
                // clone the sprite if we were holding CTRL and begin dragging the clone instead of the original. 
                if(keyboard.isPressed(KeyEvent.VK_CONTROL)) {
                    cloneSelectedSprites();
                    isCloning = true;
                }
            }
            
            if(isDrag) {
                dragSelectedSprites(mouseWorld);
                isModified = true;
            }
        }
        
        
        // Create a selection rectangle if we drag after clicking in empty space.
        if(mouse.isLeftPressed && selectedSprite == null) {
            if((mouseWorld.x != dragStartX || mouseWorld.y != dragStartY) && !isDrag) {
                isSelRect = true; 
                
            }
        }
        
        // Press Delete to delete the currently selected sprites.
        if(keyboard.justPressed(KeyEvent.VK_DELETE) && !selectedSprites.isEmpty()) {
            DeleteSpriteEdit cmd = new DeleteSpriteEdit(this);
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
    //    SpriteInstance sprite = selectedLayer.dropSpriteType(spriteType, getSnappedCoords(mouseWorld));
        DropSpriteEdit cmd = new DropSpriteEdit(this, spriteType, getSnappedCoords(mouseWorld));
        SpriteInstance sprite = cmd.sprite;
        
        isModified = true;
        return sprite;
    }
    
    
    
    
    /** Selects a sprite and adds it to the list of currently selected sprites */
    public void selectSprite(SpriteInstance sprite) {
        sprite.isSelected = true;
        
        // prevent duplicates
        if(selectedSprites.contains(sprite))
            return;
        
        // TODO : order the selectedSprites by their z-index.
        
        selectedSprites.add(sprite);
    }
    
    /** unselects all sprites in the current layer and empties our list of selected sprites. */
    public void unselectAll() {
        selectedSprites = new LinkedList<SpriteInstance>();
        selectedLayer.selectAll(false);
    }
    
    /** Selects all sprites in the selection rectangle. */
    public void selectionRectangle() {
        int x = (int) Math.min(mouseWorld.x, dragStartX);
        int y = (int) Math.min(mouseWorld.y, dragStartY);
        int w = (int) Math.abs(mouseWorld.x - dragStartX);
        int h = (int) Math.abs(mouseWorld.y - dragStartY);
        
        for(SpriteInstance sprite : selectedLayer.sprites) {
            if(sprite.x >= x && sprite.x <= x+w && sprite.y >= y && sprite.y <= y+h) {
                selectSprite(sprite);
            }
        }
    }
    
    
    
    /** Snaps a sprite to the grid*/
    public void snapSprite(SpriteInstance sprite) {
        if(snapToGrid) {
            sprite.x = ((int)sprite.x/gridSpaceX)*gridSpaceX;
            sprite.y = ((int)sprite.y/gridSpaceY)*gridSpaceY;
        }
    }
    
    
    /** Initializes the currently selected sprites for dragging with the mouse */
    public void initDragSprites() {
        for(SpriteInstance sprite : selectedSprites) {
            sprite.startDragX = sprite.x;
            sprite.startDragY = sprite.y;
        }
    }
    
    /** Drags a sprite with the mouse */
    public void dragSprite(SpriteInstance sprite, Point mouseWorld) {
        int mdx = mouseWorld.x - dragStartX;
        int mdy = mouseWorld.y - dragStartY;
        
        sprite.x = sprite.startDragX + mdx;
        sprite.y = sprite.startDragY + mdy;
        
        snapSprite(sprite);
    }
    
    
    /** Drags all currently selected sprites with the mouse. */
    public void dragSelectedSprites(Point mouseWorld) {
        for(SpriteInstance sprite : selectedSprites) {
            dragSprite(sprite, mouseWorld);
        }
    }
    

    
    
    
    /** clones a sprite (not in the same sense as Object.clone(). */
    public SpriteInstance cloneSprite(SpriteInstance sprite) {
        SpriteInstance clone = sprite.makeClone();
        selectedLayer.addSprite(clone);
        return clone;
    }
    
    
    /** clones all currently selected sprites and sets the clones as the currently selected sprites. The original sprites become unselected. */
    public void cloneSelectedSprites() {
        LinkedList<SpriteInstance> newSelSprites = new LinkedList<SpriteInstance>();
        
        for(SpriteInstance sprite : selectedSprites) {
            SpriteInstance clone = cloneSprite(sprite);
            sprite.isSelected = false;
            clone.isSelected = true;
            
            newSelSprites.add(clone);
        }
        
        selectedSprites = newSelSprites;
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
            renderSelectionRect(g);
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
    
    public void renderSelectionRect(Graphics2D g) {
        if(!isSelRect)
            return;
        
        Stroke origStroke = g.getStroke();
        g.setStroke(new BasicStroke((float) (3/camera.zoom)));
        g.setColor(selRectColor);
        int x = (int) Math.min(mouseWorld.x, dragStartX);
        int y = (int) Math.min(mouseWorld.y, dragStartY);
        int w = (int) Math.abs(mouseWorld.x - dragStartX);
        int h = (int) Math.abs(mouseWorld.y - dragStartY);
        
        g.drawRect(x,y, w, h);
        
        g.setStroke(origStroke);
    }
    
}

