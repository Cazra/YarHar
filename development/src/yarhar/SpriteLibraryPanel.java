package yarhar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;

public class SpriteLibraryPanel extends JPanel implements ActionListener {
    
    YarharMain frame;
    
    JButton testButton = new JButton("test");
    JComboBox libSelect = new JComboBox();
    JList spriteList = new JList();
    
    
    public SpriteLibraryPanel(YarharMain yarhar) {
        super();
        this.frame = yarhar;
        
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setBackground(new Color(0xDDDDDD));
        this.setBorder(new LineBorder(new Color(0xBBBBBB), 2));
        
        this.add(testButton);
        
        libSelect.addItem("default");
        this.add(libSelect);
        libSelect.setMaximumSize(new Dimension(150, 20)); 
        
        DefaultListModel listModel = new DefaultListModel();
        listModel.addElement("herp");
        listModel.addElement("derp");
        listModel.addElement("flerp");
        spriteList.setModel(listModel);
        
        JScrollPane scrollpane = new JScrollPane(spriteList);
        scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollpane);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
    }
}


