package yarhar.dialogs;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import yarhar.*;
import yarhar.map.*;

/** A dialog that allows the user to create a new SpriteType. */
public class BGColorDialog extends JDialog implements ActionListener, MouseListener {
    
    public LevelMap map;
    
    public Color bgColor;
    public JTextField colorFld = new JTextField(7);
    
    public JButton okBtn = new JButton("OK");
    public JButton cancelBtn = new JButton("Cancel");
    
    public BGColorDialog(YarharMain owner, LevelMap map) {
        super(owner, true);
        
        this.map = map;
        constructComponents();
        this.setSize(new Dimension(250,400));
        
        setTitle("Set Background Color");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        show();
    }
    
    
    
    
    public void constructComponents() {
        JPanel topPanel = DialogUtils.makeVerticalFlowPanel();
        
        bgColor = new Color(map.bgColor);
        colorFld.setEditable(false);
        colorFld.addMouseListener(this);
        updateColorFld();
        topPanel.add(DialogUtils.makeLabelFieldPanel(new JLabel("Background Color: "), colorFld));
        
        JPanel okPanel = new JPanel();
        okPanel.add(okBtn);
        okBtn.addActionListener(this);
        okPanel.add(cancelBtn);
        cancelBtn.addActionListener(this);
        topPanel.add(okPanel);
        
        this.add(topPanel);
    }
    
    
    
    
    /** Updates the color and text of colorFld to reflect bgColor */
    public void updateColorFld() {
        colorFld.setText("0x" + Integer.toHexString(bgColor.getRGB() & 0x00FFFFFF).toUpperCase());
        colorFld.setBackground(bgColor);
        
        int brightness = (bgColor.getRed() + bgColor.getBlue() + bgColor.getGreen())/3;
        if(brightness < 100)
            colorFld.setForeground(Color.WHITE);
        else
            colorFld.setForeground(Color.BLACK);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source == okBtn) {
            map.bgColor = bgColor.getRGB();
            this.dispose();
        }
        if(source == cancelBtn) {
            this.dispose();
        }
    }
    
    
    public void mouseClicked(MouseEvent e) {
        Object source = e.getSource();
        
        if(source == colorFld) {
            Color color = JColorChooser.showDialog(this, "Choose Background Color", bgColor);
            if(color != null) {
                bgColor = color;
                updateColorFld();
            }
        }
    }
   
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }
   
    public void mouseExited(MouseEvent e) {
        // Do nothing
    }
   
    public void mousePressed(MouseEvent e) {
        // Do nothing
    }
   
    public void mouseReleased(MouseEvent e) {
        // Do nothing
    }
}

