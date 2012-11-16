package yarhar.map;

import yarhar.*;
import yarhar.cmds.*;
import yarhar.dialogs.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import org.json.*;
import pwnee.*;
import java.util.LinkedList;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenu;


/** The top level object for a map being manipulated with the YarHar UI. */
public class LevelMap extends Level {
    
    /** Same as the filename for this map (just the name, not the whole path or extension). */
    public String name = "New Map";
    
    /** Stores the last file path this map was saved/loaded to. */
    public String filePath = "";
    
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
    
    /** The sprite right-click menu */
    public SpriteRClickMenu spriteMenu;
    
    
    
    /** Creates a blank map With an unpopulated sprite library and just one layer. */
    public LevelMap(EditorPanel game) {
        this(game,null);
    }
    
    /** Load the map from a JSON text file */
    public LevelMap(EditorPanel game, File file) {
        super(game);
        
        if(file == null) {
            spriteLib = new SpriteLibrary(this);
            addLayer(new Layer());
        }
        else {
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                
                // read the json text from the file.
                String jsonStr = "";
                String line = br.readLine();
                while(line != null) {
                    jsonStr += line;
                    line = br.readLine();
                }
                
                // convert the json text into a json object and then construct this map from it.
                JSONObject json = new JSONObject(jsonStr);
                JSONObject yarmap = json.getJSONObject("yarmap");
                loadJSON(yarmap);
            }
            catch(Exception e) {
                System.err.println("Error reading JSON for map.");
            }
        }
        
        game.frame.updateTitle(name);
        game.frame.layersPanel.setMap(this);
        game.frame.spriteLibPanel.setLibrary(this.spriteLib);
        
        camera = game.camera;
        
        isModified = false;
        
        spriteMenu = new SpriteRClickMenu(this);
    }
    
    
    public void clean() {
    }
    
    public void loadData() {
    }
    
    
    /** Produces a JSON string representing this map*/
    public String toJSON() {
        String result = "{";
        result += "\"name\":\"" + name + "\",";
        result += "\"bgColor\":" + bgColor + ",";
        result += "\"spriteLib\":" + spriteLib.toJSON() + ",";
        result += "\"layers\":[";
        boolean isFirst = true;
        for(Layer layer : layers) {
            if(!isFirst)
                result += ",";
            else
                isFirst = false;
            
            result += layer.toJSON();
        }
        result += "],";
        result += "\"desc\":\"" + desc + "\",";
        result += "\"gridSpaceX\":" + gridSpaceX + ",";
        result += "\"gridSpaceY\":" + gridSpaceY + ",";
        result += "\"gridW\":" + gridW + ",";
        result += "\"gridH\":" + gridH + ",";
        result += "\"gridColor\":" + gridColor.getRGB();
        result += "}";
        return result;
    }
    
    /** Loads this map from a json object. */
    public void loadJSON(JSONObject yarmap) {
        try {
            name = yarmap.getString("name");
            bgColor = yarmap.getInt("bgColor");
            spriteLib = new SpriteLibrary(this, yarmap.getJSONObject("spriteLib"));
            
            JSONArray layerListJ = yarmap.getJSONArray("layers");
            for(int i = 0; i < layerListJ.length(); i++) {
                JSONObject layerJ = layerListJ.getJSONObject(i);
                Layer layer = new Layer(layerJ, spriteLib);
                addLayer(layer);
            }
            
            desc = yarmap.getString("desc");
            gridSpaceX = yarmap.getInt("gridSpaceX");
            gridSpaceY = yarmap.getInt("gridSpaceY");
            gridW = yarmap.getInt("gridW");
            gridH = yarmap.getInt("gridH");
            gridColor = new Color(yarmap.getInt("gridColor"));
        }
        catch(Exception e) {
            System.err.println("Error reading JSON.");
        }
    }
    
    
    
    
    
    
    
    
    
    public void logic() {
        mouseWorld = getMouseWorld();

        //// sprite interaction
        
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
        if(mouse.justLeftPressed || mouse.justRightPressed) {
            selectedSprite = selectedLayer.tryClickSprite(mouse.position);
            dragStartX = mouseWorld.x;
            dragStartY = mouseWorld.y;
            
            // possibly unselect all sprites.
            if(!selectedSprites.contains(selectedSprite) && (!keyboard.isPressed(KeyEvent.VK_SHIFT) || keyboard.isPressed(KeyEvent.VK_CONTROL)))
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
            }
        }
        
        
        // Create a selection rectangle if we drag after clicking in empty space.
        if(mouse.isLeftPressed && selectedSprite == null && keyboard.isPressed(KeyEvent.VK_SHIFT)) {
            if((mouseWorld.x != dragStartX || mouseWorld.y != dragStartY) && !isDrag) {
                isSelRect = true; 
                
            }
        }
        
        // Press Delete to delete the currently selected sprites.
        if(keyboard.justPressed(KeyEvent.VK_DELETE) && !selectedSprites.isEmpty()) {
            DeleteSpriteEdit cmd = new DeleteSpriteEdit(this);
        }
        
        // Right clicking a selection of sprites pops up a menu.
        if(mouse.justRightPressed && selectedSprite != null) {
            spriteMenu.show(this.game, mouse.x, mouse.y);
        }
        
        
        //// general camera controls
        
        // Pan the camera while the left mouse button is held (and shift is not held).
        if(mouse.isLeftPressed && !isSelRect && !isDrag && !isCloning) {
            camera.drag(mouse.position);
        }
           
        // Stop dragging the camera when we release the left or right mouse button.
        if(mouse.justLeftClicked)
           camera.endDrag();
            
        // Zoom in by scrolling the mouse wheel up.
        if(mouse.wheel < 0)
           camera.zoomAtScreen(1.25, mouse.position);
         
        // Zoom out by scrolling the mouse wheel down.
        if(mouse.wheel > 0)
           camera.zoomAtScreen(0.75, mouse.position);
        
        
    }
    
    
    
    
    //// Coordinates
    
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
    
    
    
    
    //// modify flag
    
    /** Flags this map as modified and puts an asterisk next to the map's name in the window title */
    public void flagModified() {
        isModified = true;
        
        EditorPanel editor = (EditorPanel) game;
        editor.frame.updateTitle(name + "*");
    }
    
    
    //// Layer operations
    
    /** Prepends a layer to this map (making the layer be on the top) and causes it to become selected. */
    public void addLayerFirst(Layer layer) {
        layers.addFirst(layer);
        selectedLayer = layer;
    }
    
    /** Appends a layer to this map (making the layer be on the bottom) and causes it to become selected. */
    public void addLayer(Layer layer) {
        layers.add(layer);
        selectedLayer = layer;
    }
    
    /** Moves a layer to another index */
    public void moveLayer(Layer layer, int destIndex) {
        int index = layers.indexOf(layer);

        layers.remove(layer);
        layers.add(destIndex, layer);
        selectedLayer = layer;
    }
    
    
    
    //// Drop sprites
    
    /** Drops a new sprite into the currently selected layer. */
    public SpriteInstance dropSpriteType(SpriteType spriteType, Point mouseWorld) {
    //    SpriteInstance sprite = selectedLayer.dropSpriteType(spriteType, getSnappedCoords(mouseWorld));
        DropSpriteEdit cmd = new DropSpriteEdit(this, spriteType, getSnappedCoords(mouseWorld));
        SpriteInstance sprite = cmd.sprite;

        return sprite;
    }
    
    
    
    
    
    
    //// Selecting sprites
    
    /** Selects a sprite and adds it to the list of currently selected sprites */
    public void selectSprite(SpriteInstance sprite) {
        sprite.isSelected = true;
        
        // prevent duplicates
        if(selectedSprites.contains(sprite))
            return;

        // order the selectedSprites by their z-index in our selection list.
        int i;
        for(i = 0; i < selectedSprites.size(); i++) {
            SpriteInstance oSprite = selectedSprites.get(i);
            if(sprite.zIndex < oSprite.zIndex) {
                break;
            }
            
        }

        selectedSprites.add(i, sprite);
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
    
    
    
    
    //// Moving sprites
    
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
    

    
    
    //// Cloning sprites
    
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
    
    
    //// Deleting sprites
    
    /** Deletes all instances of a SpriteType in this map. Returns tuples of the deleted instances and the layers they were deleted from, for the purpose of undos. */
    public LinkedList<LayerInstTuple> deleteAllInstances(SpriteType type) {
        LinkedList<LayerInstTuple> delSprites = new LinkedList<LayerInstTuple>();
        
        
        for(Layer layer : layers) {
            LinkedList<SpriteInstance> sprites = new LinkedList<SpriteInstance>(layer.sprites);
            for(SpriteInstance inst : sprites) {
                if(inst.type == type) {
                    delSprites.add(new LayerInstTuple(layer, inst));
                    layer.removeSprite(inst);
                }
            }
        }
        
        return delSprites;
    }
    
    
    //// Rendering
    
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
    
    /** Renders the selection rectangle for selecting multiple sprites. */
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


/** Menu that appears when you right click a sprite selection.*/
class SpriteRClickMenu extends JPopupMenu implements ActionListener {
    LevelMap map;
    
    JMenuItem editTypeItem = new JMenuItem("Edit type");
    JMenu orderItems = new JMenu("Order");
        JMenuItem toFrontItem = new JMenuItem("Send to front");
        JMenuItem fwdOneItem = new JMenuItem("Forward one");
        JMenuItem bwdOneItem = new JMenuItem("Backward one");
        JMenuItem toBackItem = new JMenuItem("Send to back");
    JMenuItem deleteItem = new JMenuItem("Delete");
    
    public SpriteRClickMenu(LevelMap map) {
        super();
        this.map = map;
    
        add(editTypeItem);
        editTypeItem.addActionListener(this);
        
    //    add(orderItems);
        orderItems.add(toFrontItem);
        orderItems.add(fwdOneItem);
        orderItems.add(bwdOneItem);
        orderItems.add(toBackItem);
        
        add(deleteItem);
        deleteItem.addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source == editTypeItem) {
            EditorPanel editor = (EditorPanel) map.game;
            NewSpriteTypeDialog cmd = new NewSpriteTypeDialog(editor.frame, map.spriteLib, map.selectedSprite.type);
        }
        if(source == deleteItem) {
            DeleteSpriteEdit cmd = new DeleteSpriteEdit(map);
        }
        
    }
    
}



