package yarhar.dialogs;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import pwnee.image.ImageLoader;
import pwnee.image.ImageEffects;
import yarhar.*;
import yarhar.map.*;

/** A dialog that allows the user to create a new SpriteType. */
public class NewSpriteTypeDialog extends JDialog implements ActionListener, ChangeListener {
    
    SpriteLibrary library;
    String libGroup;
    
    
    JPanel fieldsPanel;
    JPanel previewPanel;
    JScrollPane imgScrollPane;
    
    JButton okBtn = new JButton("OK");
    JButton cancelBtn = new JButton("Cancel");
    JButton browseBtn = new JButton("Browse");
    JButton cropBtn = new JButton("Crop");
    
    JTextField nameFld = new JTextField("Untitled",15);
    JTextField browseFld = new JTextField("",15);
    JTextField cropXFld = new JTextField("0",5);
    JTextField cropYFld = new JTextField("0",5);
    JTextField cropWFld = new JTextField("32",5);
    JTextField cropHFld = new JTextField("32",5);
    
    SpriteLabel curImgLabel;
    ImageIcon curImg;
    String imgPath = "";
    JSlider zoomSlider;
    JLabel zoomLabel = new JLabel("Zoom: x1");
    
    JLabel mousePosLabel = new JLabel("Mouse: 0,0");
    JLabel focalPointLabel = new JLabel("Focal point: 0,0");
    JLabel dimsLabel = new JLabel("Size: 32,32");
    
    public NewSpriteTypeDialog(YarharMain owner, SpriteLibrary library, String group) {
        super(owner, true);
        constructComponents();
        this.setSize(new Dimension(500,400));
        
        setTitle("New Sprite");
        
        this.library = library;
        this.libGroup = group;
        System.err.println(library + " " + group);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        show();
    }
    
    /** Constructs the dialog's top JPanel. */
    public void constructComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        
        fieldsPanel = makeVerticalFlowPanel();
        fieldsPanel.add(makeLabelFieldPanel(new JLabel("Name: "), nameFld));
        fieldsPanel.add(constructBrowsePanel());
        fieldsPanel.add(constructCroppingPanel());
        
        previewPanel = new JPanel(new BorderLayout());
        previewPanel.add(new JLabel("Preview image: "), BorderLayout.NORTH);
        initSpriteLabel();
        imgScrollPane = new JScrollPane(curImgLabel);
        previewPanel.add(imgScrollPane, BorderLayout.CENTER);
        previewPanel.add(makePreviewUtilsPanel(), BorderLayout.SOUTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fieldsPanel, previewPanel);
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(0.0);
        splitPane.setDividerLocation(0.5);
        topPanel.add(splitPane, BorderLayout.CENTER);
        
        
        JPanel okPanel = new JPanel();
        okPanel.add(okBtn);
        okBtn.addActionListener(this);
        okPanel.add(cancelBtn);
        cancelBtn.addActionListener(this);
        topPanel.add(okPanel, BorderLayout.SOUTH);
        
        this.add(topPanel);
        
    }
    
    
    /** Constructs the panel with the open file thingy. */
    public JPanel constructBrowsePanel() {
        JPanel result = new JPanel();
        result.add(browseFld);
        result.add(browseBtn);
        browseFld.setEditable(false);
        browseBtn.addActionListener(this);
        return result;
    }
    
    
    /** Constructs the subpanel with fields for cropping the sprite's image. */
    public JPanel constructCroppingPanel() {
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
    
    /** Initializes our SpriteLabel. */
    public void initSpriteLabel() {
        curImg = loadImageIconFromResource("BadImg.png");
        curImgLabel = new SpriteLabel(curImg);
        curImgLabel.mousePosLabel = mousePosLabel;
        curImgLabel.focalPointLabel = focalPointLabel;
        curImgLabel.dimsLabel = dimsLabel;
    }
    
    /** Creates a JPanel to house the shiny buttons for the previewPanel. */
    public JPanel makePreviewUtilsPanel() {
        JPanel result = makeVerticalFlowPanel();
        result.add(makeZoomSliderPanel());
        result.add(mousePosLabel);
        result.add(focalPointLabel);
        result.add(dimsLabel);
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
        }
        
    }
    
    
    
    
    
    /** Loads an ImageIcon from a file or URL. */
    public ImageIcon loadImageIconFromResource(String path) {
        ImageLoader imgLoader = new ImageLoader();
        Image img = imgLoader.loadFromFile(path);
        ImageIcon result = new ImageIcon(img);
        return result;
    }
    
    
    /** Event listener for buttons */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source == okBtn) {
            SpriteType result = curImgLabel.toSpriteType(nameFld.getText(), imgPath);
            library.addSpriteType(libGroup, result);
            
            
            
            this.dispose();
        }
        if(source == cancelBtn) {
            this.dispose();
        }
        if(source == cropBtn) {
            tryCropImage();
        }
        if(source == browseBtn) {
            JFileChooser openDia = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter ("bmp, gif, jpg, or png file", "bmp", "gif", "jpg", "png");
            openDia.setFileFilter(filter);
            
            int retVal = openDia.showOpenDialog(this);
            
            // If successful, load the file as our preview image.
            if(retVal == JFileChooser.APPROVE_OPTION) {
                String path = "Could not load image.";
                try {
                    path = openDia.getSelectedFile().getPath();
                    curImg = new ImageIcon(path);
                    imgPath = path;
                }
                catch (Exception ex) {
                    curImg = loadImageIconFromResource("BadImg.png");
                    imgPath = "";
                }
                
                browseFld.setText(path);
                curImgLabel.setIcon(curImg);
                curImgLabel.resetCropData();
                imgScrollPane.updateUI();
            }
            
            
        }
    }
    
    
    /** Event listener for the zoom slider */
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
    
    public int cropOffX = 0;
    public int cropOffY= 0;
    public int cropW = 0;
    public int cropH = 0;
    
    JLabel mousePosLabel = null;
    JLabel focalPointLabel = null;
    JLabel dimsLabel = null;
    
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
    }
    
    public void mouseDragged(MouseEvent e) {
        int button = e.getButton();
        
        updateMouseLabel(e);
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
    
    
    public void resetCropData() {
        cropOffX = 0;
        cropOffY = 0;
    }
    
    
    /** Creates a SpriteType from this */
    public SpriteType toSpriteType(String name, String path) {
        SpriteType result = new SpriteType(name, path, cropOffX, cropOffY, cropW, cropH);
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

