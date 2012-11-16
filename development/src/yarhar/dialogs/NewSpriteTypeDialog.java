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
import yarhar.cmds.*;

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
    
    boolean isEdit = false;
    
    /** Constructor for creating a new SpriteType. */
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
    
    
    /** Constructor for editing an existing SpriteType. */
    public NewSpriteTypeDialog(YarharMain owner, SpriteLibrary library, SpriteType editType) {
        super(owner, true);
        constructComponents();
        this.setSize(new Dimension(500,400));
        
        setTitle("New Sprite");
        
        this.library = library;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        nameFld.setText(editType.name);
        nameFld.setEditable(false);
        browseFld.setText(editType.imgPath);
        
        try {
            String path = editType.imgPath;
            curImg = new ImageIcon(path);
            imgPath = path;
        }
        catch (Exception ex) {
            curImg = loadImageIconFromResource("BadImg.png");
            imgPath = "";
        }
        
        curImgLabel.setIcon(curImg);
        curImgLabel.resetCropData();
        tryCropImage(editType.cropX, editType.cropY, editType.cropW, editType.cropH);
        imgScrollPane.updateUI();
        
        isEdit = true;
        
        show();
    }
    
    /** Constructs the dialog's top JPanel. */
    public void constructComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        
        fieldsPanel = DialogUtils.makeVerticalFlowPanel();
        fieldsPanel.add(DialogUtils.makeLabelFieldPanel(new JLabel("Name: "), nameFld));
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
        JPanel cropPanel = DialogUtils.makeVerticalFlowPanel();
        cropPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Image Cropping"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));
        
        cropPanel.add(DialogUtils.makeLabelFieldPanel(new JLabel("crop left X: "), cropXFld));
        cropPanel.add(DialogUtils.makeLabelFieldPanel(new JLabel("crop left Y: "), cropYFld));
        cropPanel.add(DialogUtils.makeLabelFieldPanel(new JLabel("crop width: "), cropWFld));
        cropPanel.add(DialogUtils.makeLabelFieldPanel(new JLabel("crop height: "), cropHFld));
        
        cropPanel.add(cropBtn);
        cropBtn.addActionListener(this);
        
        return cropPanel;
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
        JPanel result = DialogUtils.makeVerticalFlowPanel();
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
    
    public void tryCropImage(int cx, int cy, int cw, int ch) {   
        curImgLabel.crop(cx,cy,cw,ch);
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
            
            
            if(!isEdit) {
                // If we're about to overwrite a sprite type with the same name, prompt the user if they are sure they want to replace it.
                if(library.spriteNames.contains(result.name)) {
                    int retVal = JOptionPane.showConfirmDialog(this, "A sprite type with the name \"" + result.name + "\" already exists in this map's sprite library. Are you sure you want to overwrite it?");
                    
                    if(retVal != JOptionPane.YES_OPTION)
                        return;
                }
                new NewSpriteTypeEdit(library, libGroup, result);
            }
            else {
                new EditSpriteTypeEdit(library, result);
            }
            
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
