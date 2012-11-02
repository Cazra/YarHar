package yarhar.dialogs;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.swing.border.LineBorder;
import pwnee.image.ImageLoader;
import pwnee.image.ImageEffects;
import yarhar.map.*;

/** A dialog that allows the user to create a new SpriteType. */
public class NewSpriteTypeDialog extends JDialog implements ActionListener, ChangeListener {
    
    JPanel fieldsPanel;
    JPanel previewPanel;
    JScrollPane imgScrollPane;
    
    JButton okBtn = new JButton("OK");
    JButton cancelBtn = new JButton("Cancel");
    JButton cropBtn = new JButton("Crop");
    
    JTextField nameFld = new JTextField("Untitled",15);
    JTextField cropXFld = new JTextField("0",5);
    JTextField cropYFld = new JTextField("0",5);
    JTextField cropWFld = new JTextField("32",5);
    JTextField cropHFld = new JTextField("32",5);
    
    SpriteLabel curImgLabel;
    ImageIcon curImg;
    JSlider zoomSlider;
    JLabel zoomLabel = new JLabel("Zoom: x1");
    
    public NewSpriteTypeDialog(Frame owner) {
        constructComponents();
        this.setSize(new Dimension(400,200));
        
        setTitle("New Sprite");
        show();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
    
    /** Constructs the dialog's top JPanel. */
    public void constructComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        
        fieldsPanel = makeVerticalFlowPanel();
        fieldsPanel.add(makeLabelFieldPanel(new JLabel("Name: "), nameFld));
        fieldsPanel.add(constructCroppingPanel());
        
        previewPanel = new JPanel(new BorderLayout());
        previewPanel.add(new JLabel("Preview image: "), BorderLayout.NORTH);
        curImg = loadImageIconFromFile("BadImg.png");
        curImgLabel = new SpriteLabel(curImg);
        imgScrollPane = new JScrollPane(curImgLabel);
        imgScrollPane.setMinimumSize(new Dimension(200,200));
        previewPanel.add(imgScrollPane, BorderLayout.CENTER);
        previewPanel.add(makePreviewUtilsPanel(), BorderLayout.SOUTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fieldsPanel, previewPanel);
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerLocation(0.3);
        topPanel.add(splitPane, BorderLayout.CENTER);
        
        
        JPanel okPanel = new JPanel();
        okPanel.add(okBtn);
        okPanel.add(cancelBtn);
        cancelBtn.addActionListener(this);
        topPanel.add(okPanel, BorderLayout.SOUTH);
        
        this.add(topPanel);
        
    }
    
    /** Constructs the subpanel with fields for cropping the sprite's image. */
    private JPanel constructCroppingPanel() {
        JPanel cropPanel = makeVerticalFlowPanel();
        cropPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Image Cropping"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));
        
        cropPanel.add(makeLabelFieldPanel(new JLabel("crop left X: "), cropXFld));
        cropPanel.add(makeLabelFieldPanel(new JLabel("crop left Y: "), cropYFld));
        cropPanel.add(makeLabelFieldPanel(new JLabel("crop width: "), cropWFld));
        cropPanel.add(makeLabelFieldPanel(new JLabel("crop height: "), cropHFld));
        
        cropPanel.add(cropBtn);
        cropBtn.addActionListener(this);
        
        return cropPanel;
    }
    
    /** Creates a JPanel with a vertically-oriented BoxLayout. */
    public JPanel makeVerticalFlowPanel() {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result,BoxLayout.Y_AXIS));
        return result;
    }
    
    /** Creates a JPanel with a JLabel followed by a JTextField with a FlowLayout. */
    public JPanel makeLabelFieldPanel(JLabel label, JTextField field) {
        JPanel result = new JPanel();
        result.add(label);
        result.add(field);
        return result;
    }
    
    /** Creates a JPanel to house the shiny buttons for the previewPanel. */
    public JPanel makePreviewUtilsPanel() {
        JPanel result = makeVerticalFlowPanel();
        result.add(makeZoomSliderPanel());
        return result;
    }
    
    /** Creates a JPanel with our zoom slider and the JLabel next to it that tells our current zoom ammount. */
    public JPanel makeZoomSliderPanel() {
        JPanel result = new JPanel();
        zoomSlider = new JSlider(0,3,0);
        zoomSlider.setPreferredSize(new Dimension(100,30));
        zoomSlider.setSnapToTicks(true);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setMajorTickSpacing(1);
        zoomSlider.addChangeListener(this);
        result.add(zoomSlider);
        result.add(zoomLabel);
        
        return result;
    }
    
    /** Converts the zoomSlider value to its corresponding zoom value (2^n). */
    public int getZoomValue() {
        return (int) Math.pow(2,zoomSlider.getValue());
    }
    
    /** Attempts to crop our sprite image using the values from the fields in the cropping panel. */
    public void tryCropImage() {
        int cropX, cropY, cropW, cropH;
        
        try {
            cropX = (new Integer(cropXFld.getText())).intValue();
            cropY = (new Integer(cropYFld.getText())).intValue();
            cropW = (new Integer(cropWFld.getText())).intValue();
            cropH = (new Integer(cropHFld.getText())).intValue();
            
            if(cropW < 1 || cropH < 1)
                throw new NumberFormatException();
                
            curImgLabel.crop(cropX,cropY,cropW,cropH);
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Bad integer values");
            System.err.println("Bad integer values");
        }
        
    }
    
    /** Loads an ImageIcon from a file or URL. */
    public ImageIcon loadImageIconFromFile(String path) {
        ImageLoader imgLoader = new ImageLoader();
        Image img = imgLoader.loadFromFile(path);
        ImageIcon result = new ImageIcon(img);
        return result;
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source == okBtn) {
            // TODO: Add the new SpriteType to our library's current SpriteTypeGroup.
            
            this.dispose();
        }
        if(source == cancelBtn) {
            this.dispose();
        }
        if(source == cropBtn) {
            tryCropImage();
        }
    }
    
    
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if(source == zoomSlider) {
            int zoomVal = getZoomValue();
            zoomLabel.setText("Zoom: x" + zoomVal);
            curImgLabel.scale = zoomVal;
            curImgLabel.updateSize();
            
            imgScrollPane.updateUI();
        }
    }
    
    
    
}


/** 
 * Displays an image for a sprite as a swing component. Also allows the user 
 * to set the image's focal point with the left mouse button and crop it
 * with the right mouse button. 
 */
class SpriteLabel extends JLabel implements MouseListener, MouseMotionListener {
    
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
        
        
        if(button == MouseEvent.BUTTON1) {
            // Left button: set the focus point
            
            isSettingFocus = true;
            focalX = e.getX()/scale - previewOffset;
            focalY = e.getY()/scale - previewOffset;
            System.err.println(focalX + ", " + focalY);
            
        }
        else if(button == MouseEvent.BUTTON3) {
            // Right button: begin crop
            
            isCropping = true;
            cropStartX = cropCurX = e.getX()/scale - previewOffset;
            cropStartY = cropCurY = e.getY()/scale - previewOffset;
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
        // DO nothing
    }
    
    public void mouseDragged(MouseEvent e) {
        int button = e.getButton();
        
        // Left drag to place focus point
        if(isSettingFocus) {
            focalX = e.getX()/scale - previewOffset;
            focalY = e.getY()/scale - previewOffset;
            System.err.println(focalX + ", " + focalY);
        }
        
        // Right drag to crop
        if(isCropping) {
            cropCurX = e.getX()/scale - previewOffset;
            cropCurY = e.getY()/scale - previewOffset;
            System.err.println("cropping");
        }
        
        repaint();
    }
    
    /** Updates the size of this label to be the scaled image's current size with a buffer around all sides. */
    public void updateSize() {
        Icon curImg = getIcon();
        Dimension iconSize = new Dimension(curImg.getIconWidth(), curImg.getIconHeight());
        Dimension labelSize = new Dimension((iconSize.width + previewOffset*2) * scale , (iconSize.height + previewOffset*2) * scale);
        setPreferredSize(labelSize);
    }
    
    /** Crops the current image. */
    public void crop(int x, int y, int w, int h) {
        ImageIcon curImg = (ImageIcon) getIcon();
        Image img = curImg.getImage();
        img = ImageEffects.crop(img, x, y, w, h);
        
        setIcon(new ImageIcon(img));
        focalX -= x;
        focalY -= y;
        
        repaint();
    }
    
    
    public void paint(Graphics gg) {
        System.err.println("Paint SpriteLabel");
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

