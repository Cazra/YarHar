package yarhar;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.event.KeyEvent;
import pwnee.*;

public class EditorPanel extends GamePanel {
    
    public YarharMain frame;
    public Camera camera;

    public EditorPanel(YarharMain yarhar) {
        super();
        frame = yarhar;
        
        this.setPreferredSize(new Dimension(640, 480));
        
        // Create the camera
        camera = new Camera(this);
    }
    
    public void logic() {
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
            
        // Hold spacebar to rotate the camera around the last point you clicked.
        if(keyboard.isPressed(KeyEvent.VK_SPACE))
           camera.angle += 3;
    }
    
    public void paint(Graphics g) {
        // set the background color to white.
        this.setBackground(new Color(0xFFFFFF));
        
        // clear the panel with the background color.
        super.paint(g);
        
        // cast our Graphics as a Graphics2D, since it has more features and g is actually a Graphics2D anyways.
        Graphics2D g2D = (Graphics2D) g;
        
        
        // Save the original transform
        AffineTransform origTrans = g2D.getTransform();
        
        // Modify our Graphics2D transform with our camera's view transform!
        g2D.setTransform(camera.trans);
        
        // render the scene
        paintTestGrid(g2D);
        
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
        g2D.drawString("Hold spacebar to rotate the camera.", 10,92);
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
}
