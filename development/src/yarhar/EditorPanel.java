package yarhar;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.*;
import java.io.File;
import java.util.LinkedList;
import javax.swing.*;
import pwnee.*;
import yarhar.map.*;

public class EditorPanel extends GamePanel {
    
    public YarharMain frame;
    public Camera camera;

    public EditorPanel(YarharMain yarhar) {
        super();
        frame = yarhar;
        
        this.setPreferredSize(new Dimension(640, 480));
        this.setTransferHandler(transferHandler);
        
        // Create the camera
        camera = new Camera(this);
        
        // start with a blank LevelMap.
        changeLevel("new");
    }
    
    public void clean() {
        curLevel.clean();
    }
    
    
    /** The Game Panel's logic handles camera control and then delegates to the current LevelMap's logic. */
    public void logic() {
        super.logic();
        
        // Obtain focus in the panel when it is clicked.
        if(mouse.isAnyPressed) {
            this.requestFocusInWindow();
        }
        
        // update our Camera's state
        camera.update();
        
        // Run the current map's logic.
        curLevel.logic();
        
        
    }
    
    public void paint(Graphics g) {
        if(curLevel == null)
            return;
        
        // cast our Graphics as a Graphics2D, since it has more features and g is actually a Graphics2D anyways.
        Graphics2D g2D = (Graphics2D) g;
        
        if(isLoading) {
            paintLoading(g2D);
            return;
        }
            
            
        LevelMap curMap = (LevelMap) curLevel;
        
        // set the background color to white.
        this.setBackground(new Color(curMap.bgColor));
        
        // clear the panel with the background color.
        super.paint(g2D);
        
        // Save the original transform
        AffineTransform origTrans = g2D.getTransform();
        
        // Modify our Graphics2D transform with our camera's view transform!
        g2D.setTransform(camera.trans);
        
        // render the scene
        curLevel.render(g2D);
        
        // restore our original transform
        g2D.setTransform(origTrans);
        
        // Set our drawing color to red
        g2D.setColor(new Color(0xFF0000));
        
        // display the current frame rate.
        g2D.drawString("" + timer.fpsCounter, 10,32);
        
        // display the mouse's world coordinates.
        Point2D mouseWorld = getMouseWorld();
        g2D.drawString("Mouse world coordinates: (" + mouseWorld.getX() + ", " + mouseWorld.getY() + ")",10, 47);
        
        // display instructions.
        g2D.drawString("Move the camera by dragging the mouse.", 10,62);
        g2D.drawString("Use the mouse wheel to zoom in and out.", 10,77);
    }
    
    
    /** Rendering for loading screen */
    public void paintLoading(Graphics2D g) {
        this.setBackground(Color.WHITE);
        super.paint(g);
    }
    
    /** Changing levels either starts a new level or opens a level from a file. */
    public Level makeLevelInstance(String levelName) {
        if(levelName == "new")
            return new LevelMap(this);
        else {
            File file = new File(levelName);
            LevelMap level = new LevelMap(this, file);
            level.filePath = levelName;
            return level;
        }
    }
    
    
    /** Returns a reference to the current map. */
    public LevelMap getCurMap() {
        return (LevelMap) curLevel;
    }
    
    /** Obtains the mouse's current world coordinates in integer form. */
    public Point getMouseWorld() {
        Point mouseScr = getMousePosition();
        if(mouseScr == null)
            mouseScr = mouse.position;
        Point2D mouseWorld = camera.screenToWorld(mouseScr);
        int mx = (int) mouseWorld.getX();
        int my = (int) mouseWorld.getY();
        
        return new Point(mx,my);
    }
    
    /** Custom camera reset. */
    public void resetCamera() {
        camera.x = 0;
        camera.y = 0;
        camera.focalX = 0;
        camera.focalY = 0;
        camera.zoom = 1.0;
        camera.update();
    }
    
    
    /** Creates an instance of a SpriteType that is dragged from the library into the editor. */
    public void dropSpriteType(SpriteType spriteType) {
        Point mouseWorld = getMouseWorld();
        
        LevelMap curMap = (LevelMap) curLevel;
        curMap.dropSpriteType(spriteType,mouseWorld);
        
        this.requestFocusInWindow();
    }
    
    
    /** transfer handler for importing SpriteTypes dragged over from the library. */
    private TransferHandler transferHandler = new TransferHandler() {
        public boolean canImport(TransferHandler.TransferSupport info) {
            return true;
        }
        
        public boolean importData(TransferHandler.TransferSupport info) {
            if(!canImport(info))
                return false;
            
            Transferable t = info.getTransferable();
            
            try {
                SpriteType spriteType = (SpriteType) t.getTransferData(SpriteType.flavor);
                dropSpriteType(spriteType);
            //    System.err.println("EditorPanel drag and drop successful! " + spriteType.name);
                return true;
            }
            catch(Exception e) {
                System.err.println("EditorPanel drag and drop not successful.");
            }
            
            return false;
            
        }
    };
    
    
}






