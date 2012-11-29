package yarhar.dialogs;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import yarhar.*;
import yarhar.map.*;

/** A dialog that allows the user to create a new SpriteType. */
public class RotateDialog extends JDialog implements ActionListener {
    
    public ButtonGroup rotateType = new ButtonGroup();
        public JRadioButton relativeRad = new JRadioButton("Relative",true);
        public JRadioButton absoluteRad = new JRadioButton("Absolute",false);
    
    public JTextField angleFld = new JTextField(8);
    
    public JButton okBtn = new JButton("OK");
    public JButton cancelBtn = new JButton("Cancel");
    
    public boolean returnedOK = false;
    public double angle = 0;
    public boolean isRelative = true;
    
    public RotateDialog(YarharMain owner, boolean multiRotate) {
        super(owner, true);
        
        if(multiRotate)
            absoluteRad.setEnabled(false);
        
        constructComponents();
        this.setSize(new Dimension(250,150));
        
        setTitle("Rotate Sprite(s)");
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
        
        topPanel.add(DialogUtils.makeLabelFieldPanel(new JLabel("Angle (degrees): "), angleFld));
        
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
            angle = (new Double(angleFld.getText())).intValue();
            return true;
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Invalid input. Please only enter an integer or decimal value.");
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
        if(source == absoluteRad)
            isRelative = false;
    }
    
    
    
}

