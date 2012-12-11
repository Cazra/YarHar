package yarhar.dialogs;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import yarhar.*;
import yarhar.map.*;

/** A dialog that allows the user to scale the selected sprites. */
public class TileDialog extends JDialog implements ActionListener {
    
    public ButtonGroup rotateType = new ButtonGroup();
        public JRadioButton relativeRad = new JRadioButton("Relative",true);
        public JRadioButton absoluteRad = new JRadioButton("Absolute",false);
    
    public JTextField xFld = new JTextField("1.0", 8);
    public JTextField yFld = new JTextField("1.0", 8);
    
    public JButton okBtn = new JButton("OK");
    public JButton cancelBtn = new JButton("Cancel");
    
    public boolean returnedOK = false;
    public double tileX = 1.0;
    public double tileY = 1.0;
    public boolean isRelative = true;
    
    public SpriteInstance singleSprite;
    
    public TileDialog(YarharMain owner, SpriteInstance singleSprite) {
        super(owner, true);
        
        this.singleSprite = singleSprite;
        if(singleSprite == null)
            absoluteRad.setEnabled(false);
        
        constructComponents();
        this.setSize(new Dimension(250,250));
        
        setTitle("Tile Sprite(s)");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        show();
    }
    
    
    
    
    public void constructComponents() {
        JPanel topPanel = DialogUtils.makeVerticalFlowPanel();
        
        JPanel radioPanel = new JPanel();
        rotateType.add(relativeRad);
        rotateType.add(absoluteRad);
        
        radioPanel.add(relativeRad);
        relativeRad.addActionListener(this);
        
        radioPanel.add(absoluteRad);
        absoluteRad.addActionListener(this);
        topPanel.add(radioPanel);
        
        topPanel.add(DialogUtils.makeLabelFieldPanel(new JLabel("X repeat: "), xFld));
        topPanel.add(DialogUtils.makeLabelFieldPanel(new JLabel("Y repeat: "), yFld));
        
        JPanel okPanel = new JPanel();
        okPanel.add(okBtn);
        okBtn.addActionListener(this);
        okPanel.add(cancelBtn);
        cancelBtn.addActionListener(this);
        topPanel.add(okPanel);
        
        this.add(topPanel);
    }
    
    
    public boolean validateInput() {
        try {
            tileX = (new Double(xFld.getText())).doubleValue();
            tileY = (new Double(yFld.getText())).doubleValue();
            if(tileX <= 0 || tileY <= 0) {
              throw new Exception();
            }  
              
            return true;
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Invalid input. Please only enter an integer or decimal value > 0.");
        }
        return false;
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source == okBtn) {
            if(!validateInput())
                return;
            
            returnedOK = true;
            this.dispose();
        }
        if(source == cancelBtn) {
            this.dispose();
        }
        if(source == relativeRad)
            isRelative = true;
        if(source == absoluteRad) {
            isRelative = false;
            xFld.setText("" + singleSprite.repeatX);
            yFld.setText("" + singleSprite.repeatY);
        }
    }
    
    
    
}

