package yarhar.dialogs;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.swing.border.LineBorder;
import pwnee.image.ImageLoader;
import pwnee.image.ImageEffects;
import yarhar.*;
import yarhar.map.*;


/** 
 * Displays an image for a sprite as a swing component. Also allows the user 
 * to set the image's focal point with the left mouse button and crop it
 * with the right mouse button. 
 */
public class SpriteLabel extends JLabel implements MouseListener, MouseMotionListener {
    
    public int focalX = 0;
    public int focalY = 0;
    
    public int previewOffset = 32;
    
    public int scale = 1;
    
    public boolean isSettingFocus = false;
    
    public boolean isCropping = false;
    public int cropStartX = 0;
    public int cropStartY = 0;
    public int cropCurX = 0;
    public int cropCurY = 0;
    
    public int cropOffX = 0;
    public int cropOffY= 0;
    public int cropW = 0;
    public int cropH = 0;
    
    public Color transColor = null;
    
    JLabel mousePosLabel = null;
    JLabel focalPointLabel = null;
    JLabel dimsLabel = null;
    JLabel colorLabel = null;
    
    public SpriteLabel(Icon image) {
        super(image);
        addMouseListener(this);
        addMouseMotionListener(this);
        
        updateSize();
    }

    
    public void mouseClicked(MouseEvent e) {
        // Do nothing
    }
   
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }
   
    public void mouseExited(MouseEvent e) {
        // Do nothing
    }
   
    public void mousePressed(MouseEvent e) {
        int button = e.getButton();
        
        Point mouseWorld = mouseImgPos(e);
        
        if(button == MouseEvent.BUTTON1) {
            // Left button: set the focus point
            
            isSettingFocus = true;
            setFocus(mouseWorld.x, mouseWorld.y);
        }
        else if(button == MouseEvent.BUTTON3) {
            // Right button: begin crop
            
            isCropping = true;
            cropStartX = cropCurX = mouseWorld.x;
            cropStartY = cropCurY = mouseWorld.y;
        }
        
        repaint();
    }
   
    public void mouseReleased(MouseEvent e) {
        int button = e.getButton();
        
        if(button == MouseEvent.BUTTON1) {
            // Left button: set the focus point
            
            isSettingFocus = false;
        }
        if(button == MouseEvent.BUTTON3) {
            // Right button: finish crop
            
            int x = (int) Math.min(cropStartX, cropCurX);
            int y = (int) Math.min(cropStartY, cropCurY);
            int w = (int) Math.abs(cropStartX - cropCurX) + 1;
            int h = (int) Math.abs(cropStartY - cropCurY) + 1;
            
            crop(x,y,w,h);
            
            isCropping = false;
        }
        
        repaint();
    }
    
    public void mouseMoved(MouseEvent e) {
        updateMouseLabel(e);
        updateColorLabel(e);
    }
    
    public void mouseDragged(MouseEvent e) {
        int button = e.getButton();
        
        updateMouseLabel(e);
        updateColorLabel(e);
        Point mouseWorld = mouseImgPos(e);
        
        // Left drag to place focus point
        if(isSettingFocus) {
            setFocus(mouseWorld.x, mouseWorld.y);
        }
        
        // Right drag to crop
        if(isCropping) {
            cropCurX = mouseWorld.x;
            cropCurY = mouseWorld.y;
        }
        
        repaint();
    }
    
    /** Obtains the mouse's position relative to the preview image. */
    public Point mouseImgPos(MouseEvent e) {
        int x = e.getX()/scale - previewOffset;
        int y = e.getY()/scale - previewOffset;
        
        return new Point(x,y);
    }
    
    
    /** Updates the size of this label to be the scaled image's current size with a buffer around all sides. */
    public void updateSize() {
        Icon curImg = getIcon();
        
        cropW = curImg.getIconWidth();
        cropH = curImg.getIconHeight();
        
        Dimension iconSize = new Dimension(cropW, cropH);
        Dimension labelSize = new Dimension((iconSize.width + previewOffset*2) * scale , (iconSize.height + previewOffset*2) * scale);
        setPreferredSize(labelSize);
        
        if(dimsLabel != null) {
            dimsLabel.setText("Size: " + iconSize.width + ", " + iconSize.height);
        }
    }
    
    public void setFocus(int x, int y) {
        focalX = x;
        focalY = y;
        updateFocusLabel();
    }
    
    
    /** Updates the label for displaying the mouse position. */
    public void updateMouseLabel(MouseEvent e) {
        if(mousePosLabel != null) {
            Point mouseWorld = mouseImgPos(e);
            mousePosLabel.setText("Mouse: " + mouseWorld.x + ", " + mouseWorld.y);
        }
    }
    
    public void updateColorLabel(MouseEvent e) {
        if(colorLabel != null) {
            try {
                Point mouseWorld = mouseImgPos(e);
                
                Icon icon = getIcon();
                BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics g = img.createGraphics();
                icon.paintIcon(null, g, 0,0);
                g.dispose();
                
                int[] pixel = img.getRaster().getPixel(mouseWorld.x, mouseWorld.y, new int[4]);
                int color = (pixel[0] << 16) + (pixel[1] << 8) + pixel[2];
                
                colorLabel.setText("Color at mouse: 0x" + Integer.toHexString(color & 0x00FFFFFF).toUpperCase());
            }
            catch(Exception ex) { // Pokemon exception: gotta catch 'em all!
                // An exception will be thrown if the mouse is outside the image's area.
                colorLabel.setText("Color at mouse: out of bounds");
            }
        }
    }
    
    /** Updates the label for displaying the focus point. */
    public void updateFocusLabel() {
        if(focalPointLabel != null) {
            focalPointLabel.setText("Focus point: " + focalX + ", " + focalY);
        }
    }
    
    
    public void setIcon(Icon icon) {
        super.setIcon(icon);
        updateSize();
        setFocus(0,0);
    }
    
    
    /** Crops the current image. */
    public void crop(int x, int y, int w, int h) {
        ImageIcon curImg = (ImageIcon) getIcon();
        Image img = curImg.getImage();
        img = ImageEffects.crop(img, x, y, w, h);
        
        cropOffX += x;
        cropOffY += y;
        
        setIcon(new ImageIcon(img));
        focalX -= x;
        focalY -= y;
        updateFocusLabel();
        
        updateSize();
        
        repaint();
    }
    
    /** Sets the image's transparent color. */
    public void setTransparentColor(Color color) {
        ImageIcon curImg = (ImageIcon) getIcon();
        Image img = curImg.getImage();
        
        transColor = color;
        img = ImageEffects.makeOpaque(img);
        if(color != null)
            img = ImageEffects.setTransparentColor(img, color);
        
        setIcon(new ImageIcon(img));
        repaint();
    }
    
    
    public void resetCropData() {
        cropOffX = 0;
        cropOffY = 0;
    }
    
    
    /** Creates a SpriteType from this */
    public SpriteType toSpriteType(String name, String path) {
        SpriteType result = new SpriteType(name, path, cropOffX, cropOffY, cropW, cropH, transColor);
        result.focalX = focalX;
        result.focalY = focalY;
        return result;
    }
    
    
    public void paint(Graphics gg) {
        Graphics2D g = (Graphics2D) gg;
        AffineTransform origTrans = g.getTransform();
        Composite origComp = g.getComposite();
        
        g.scale(scale,scale);
        getIcon().paintIcon(this, g, previewOffset, previewOffset);
        
        drawFocusPoint(g);
        
        if(isCropping)
            drawCropRect(g);
        
        g.setTransform(origTrans);
        g.setComposite(origComp);
    }
    
    public void drawFocusPoint(Graphics2D g) {
        AffineTransform origTrans = g.getTransform();
        Composite origComp = g.getComposite();
        
        g.setColor(new Color(0xFF0000));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        
        g.translate(previewOffset, previewOffset);
        if(scale > 1)
            g.translate(0.5,0.5);
        
        g.drawLine(focalX - 2, focalY, focalX + 2, focalY);
        g.drawLine(focalX, focalY -2, focalX, focalY +2);
        
        g.setTransform(origTrans);
        g.setComposite(origComp);
    }
    
    public void drawCropRect(Graphics2D g) {
        AffineTransform origTrans = g.getTransform();
        Composite origComp = g.getComposite();
        
        g.setColor(new Color(0x00FF00));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        
        g.translate(previewOffset, previewOffset);
        if(scale > 1)
            g.translate(0.5,0.5);
        
        int x = (int) Math.min(cropStartX, cropCurX);
        int y = (int) Math.min(cropStartY, cropCurY);
        int w = (int) Math.abs(cropStartX - cropCurX);
        int h = (int) Math.abs(cropStartY - cropCurY);
        
        g.drawRect(x,y,w,h);
        
        g.setTransform(origTrans);
        g.setComposite(origComp);
    }
}