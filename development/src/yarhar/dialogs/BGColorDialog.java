package yarhar.dialogs;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import yarhar.*;
import yarhar.map.*;

/** A dialog that allows the user to create a new SpriteType. */
public class BGColorDialog extends JDialog implements ActionListener {
    
    public LevelMap map;
    
    public ColorField colorFld;
    
    public JButton okBtn = new JButton("OK");
    public JButton cancelBtn = new JButton("Cancel");
    
    public BGColorDialog(YarharMain owner, LevelMap map) {
        super(owner, true);
        
        this.map = map;
        constructComponents();
        this.setSize(new Dimension(250,120));
        
        setTitle("Set Background Color");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        show();
    }
    
    
    
    
    public void constructComponents() {
        JPanel topPanel = DialogUtils.makeVerticalFlowPanel();
        
        colorFld = new ColorField(map.bgColor);
        topPanel.add(DialogUtils.makeLabelFieldPanel(new JLabel("Background Color: "), colorFld));
        
        JPanel okPanel = new JPanel();
        okPanel.add(okBtn);
        okBtn.addActionListener(this);
        okPanel.add(cancelBtn);
        cancelBtn.addActionListener(this);
        topPanel.add(okPanel);
        
        this.add(topPanel);
    }
    
    
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source == okBtn) {
            map.bgColor = colorFld.color.getRGB();
            this.dispose();
        }
        if(source == cancelBtn) {
            this.dispose();
        }
    }
    
}

