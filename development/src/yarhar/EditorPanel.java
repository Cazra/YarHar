package yarhar;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.event.KeyEvent;
import pwnee.*;
import yarhar.map.*;

public class EditorPanel extends GamePanel {
    
    public YarharMain frame;
    public Camera camera;

    public EditorPanel(YarharMain yarhar) {
        super();
        frame = yarhar;
        
        this.setPreferredSize(new Dimension(640, 480));
        
        // Create the camera
        camera = new Camera(this);
        
        // start with a blank LevelMap.
        changeLevel("new");
    }
    
    public void clean() {
        curLevel.clean();
    }
    
    public void logic() {
        super.logic();
        
        // update our Camera's state
        camera.update();
        
        // Drag the camera while the left mouse button is held.
        if(mouse.isLeftPressed) {
           camera.drag(mouse.position);
           this.requestFocusInWindow();
        }
           
        // Stop dragging the camera when we release the left mouse button.
        if(mouse.justLeftClicked)
           camera.endDrag();
            
        // Zoom in by scrolling the mouse wheel up.
        if(mouse.wheel < 0)
           camera.zoomAtScreen(1.25, mouse.position);
         
        // Zoom out by scrolling the mouse wheel down.
        if(mouse.wheel > 0)
           camera.zoomAtScreen(0.75, mouse.position);
        
        // Set the swing sprite library to use the library of the currently loaded map.
        LevelMap curMap = (LevelMap) curLevel;
        if(curMap != null)
            frame.spriteLibPanel.setLibrary(curMap.spriteLib);
    }
    
    public void paint(Graphics g) {
        if(curLevel == null)
            return;
            
        LevelMap curMap = (LevelMap) curLevel;
        
        // set the background color to white.
        this.setBackground(new Color(curMap.bgColor));
        
        // clear the panel with the background color.
        super.paint(g);
        
        // cast our Graphics as a Graphics2D, since it has more features and g is actually a Graphics2D anyways.
        Graphics2D g2D = (Graphics2D) g;
        
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
        Point2D mouseWorld = camera.screenToWorld(mouse.position);
        g2D.drawString("Mouse world coordinates: (" + mouseWorld.getX() + ", " + mouseWorld.getY() + ")",10, 47);
        
        // display instructions.
        g2D.drawString("Move the camera by dragging the mouse.", 10,62);
        g2D.drawString("Use the mouse wheel to zoom in and out.", 10,77);
    }
    
    public void paintTestGrid(Graphics2D g) {
        g.setColor(new Color(0xFFAAAA));
        
        for(int i = -320; i <= 320; i+=32) {
            g.drawLine(i,-320,i,320);
            g.drawLine(-320,i,320,i);
        }
        
        g.setColor(new Color(0xFF0000));
        g.drawLine(-320,0,320,0);
        g.drawLine(0,-320,0,320);
    }
    
    
    /** Changing levels either starts a new level or opens a level from a file. */
    public Level makeLevelInstance(String levelName) {
        if(levelName == "new")
            return new LevelMap(this);
        else
            return null;
    }
    
    
    public void resetCamera() {
        camera.x = 0;
        camera.y = 0;
        camera.focalX = 0;
        camera.focalY = 0;
        camera.zoom = 1.0;
        camera.update();
    }
}


/*
class EditorTransferHandler extends TransferHandler {
    
    public boolean canImport(TransferHandler.TransferSupport support) {
        return false;
    }
    
}
*/



