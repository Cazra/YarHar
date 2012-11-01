package yarhar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import yarhar.map.*;
import java.util.HashMap;

public class SpriteLibraryPanel extends JPanel implements ActionListener {
    
    public YarharMain frame;
    
    public JButton testButton = new JButton("test");
    public JComboBox groupList = new JComboBox();
    public boolean groupListIsActive = true;
    
    public JList spriteList = new JList();
    
    public SpriteLibrary spriteLib = null;
    
    
    public SpriteLibraryPanel(YarharMain yarhar) {
        super();
        this.frame = yarhar;
        
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setBackground(new Color(0xDDDDDD));
        this.setBorder(new LineBorder(new Color(0xBBBBBB), 2));
        
        this.add(testButton);
        
        this.add(groupList);
        groupList.setMaximumSize(new Dimension(150, 20)); 
        groupList.addActionListener(this);
        
        spriteList.setCellRenderer(new SpriteTypeRenderer());
        
        JScrollPane scrollpane = new JScrollPane(spriteList);
        scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollpane);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source == groupList && groupListIsActive) {
            String selGroup = (String) groupList.getSelectedItem();
            System.err.println("SpriteLibraryPanel -> groupList fired : " + selGroup);
            updateSpriteList(selGroup);
        }
    }
    
    
    public void setLibrary(SpriteLibrary spriteLib) {
        this.spriteLib = spriteLib;
    }
    
    
    public void updateGroupList() {
        // update the group selector combo box
        String selGroup = (String) groupList.getSelectedItem();
        
        groupListIsActive = false;
        if(spriteLib.isModified) {
            groupList.removeAllItems();
            for(String name : spriteLib.groupNames) {
                groupList.addItem(name);
            }
        }
        groupListIsActive = true;
        
        if(selGroup == null)
            selGroup = "default";
        groupList.setSelectedItem(selGroup);
        
    }
    
    
    public void updateSpriteList(String groupName) {
        if(groupName == null)
            groupName = "default";
            
        
        // update the SpriteType list to reflect the contents of the currently selected group
        DefaultListModel listModel = new DefaultListModel();
        
        SpriteTypeGroup group = spriteLib.groups.get(groupName);
        
        if(group == null)
            return;
        
        for(String name : group.typeNames) {
            listModel.addElement(group.spriteTypes.get(name));
        }
        
        spriteList.setModel(listModel);
    }
    
}





class SpriteTypeRenderer extends JLabel implements ListCellRenderer {
    
    public SpriteTypeRenderer() {
        setOpaque(true);
    }
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        SpriteType spriteType = (SpriteType) value;
        
        Color background;
        Color foreground;
        
        this.setText(spriteType.name);
        this.setIcon(spriteType.icon);
        this.setVerticalTextPosition(JLabel.BOTTOM);
        this.setHorizontalTextPosition(JLabel.CENTER);
        
        if(isSelected) {
            background = Color.RED;
            foreground = Color.WHITE;
        }
        else {
            background = Color.WHITE;
            foreground = Color.BLACK;
        }
        
        setBackground(background);
        setForeground(foreground);
        setBorder(new javax.swing.border.EmptyBorder(5,5,5,5));
        
        return this;
    }
}




