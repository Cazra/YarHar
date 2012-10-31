package yarhar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;

public class ToolsPanel extends JPanel implements ActionListener{
    
    YarharMain frame;
    
    JButton testButton = new JButton("test");
    
    
    public ToolsPanel(YarharMain yarhar) {
        super();
        this.frame = yarhar;
        
        this.setBackground(new Color(0xDDDDDD));
        this.setBorder(new LineBorder(new Color(0xBBBBBB), 2));
        
        this.add(testButton);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
    }
}


