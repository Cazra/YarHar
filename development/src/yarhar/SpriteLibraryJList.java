package yarhar;

import java.util.HashMap;
import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import yarhar.map.*;
import yarhar.dialogs.*;
import yarhar.cmds.*;

public class SpriteLibraryJList extends JList implements MouseListener {
    /** A reference back to our YarharMain */
    public YarharMain frame;
    
    /** Reference to the combo box containing our group list. */
    public JComboBox groupList;
    
    /** Reference to the current sprite library. */
    public SpriteLibrary spriteLib = null;
    
    public SpriteType lastType = null;
    public String lastGroupName = "";
    
    public SpriteTypeRClickMenu spriteRMenu;
    
    public SpriteLibraryJList(YarharMain frame, JComboBox groupList) {
        super();
        this.frame = frame;
        this.groupList = groupList;
        
        this.setCellRenderer(new SpriteTypeRenderer());
        this.setDragEnabled(true);
        //this.setTransferHandler(transferHandler);
        this.addMouseListener(this);
        
        spriteRMenu = new SpriteTypeRClickMenu(this);
    }
    
    
    
    public void mouseClicked(MouseEvent e) {
        Object source = e.getSource();
        int btn = e.getButton();
        
        if(source == this && btn == MouseEvent.BUTTON3) {
            this.setSelectedIndex(this.locationToIndex(new Point(e.getX(), e.getY())));
            Point mousePos = this.getMousePosition();
            spriteRMenu.show(this, mousePos.x, mousePos.y);
        }  
        
    }
   
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }
   
    public void mouseExited(MouseEvent e) {
        // Do nothing
    }
   
    public void mousePressed(MouseEvent e) {
        
    }
   
    public void mouseReleased(MouseEvent e) {
        
    }
    
    
    
    
    
    
    
    
    
    
    /** Updates spriteList to reflect the SpriteTypes currently contained in the selected SpriteTypeGroup. */
    public void updateModel(String groupName) {
        if(groupName == null)
            groupName = "default";
            
        
        // update the SpriteType list to reflect the contents of the currently selected group
        DefaultListModel listModel = new DefaultListModel();
        
        SpriteTypeGroup group = spriteLib.groups.get(groupName);
        
        if(group == null)
            return;
        
        for(String name : group.typeNames) {
            listModel.addElement(spriteLib.sprites.get(name));
        }
        
        this.setModel(listModel);
    }
    
    /** Deletes the currently selected SpriteType from our library. */
    public void deleteSelectedSpriteType() {
        String selGroup = (String) groupList.getSelectedItem();
        SpriteType selType = (SpriteType) this.getSelectedValue();
        if(selType == null || selGroup == null)
            return;
            
        new DeleteSpriteTypeEdit(spriteLib, selGroup, selType);
        updateModel(selGroup);
    }
    
    
    /** Edits the currently selected SpriteType. */
    public void editSelectedSpriteType() {
        String selGroup = (String) groupList.getSelectedItem();
        SpriteType selType = (SpriteType) this.getSelectedValue();
        
        if(selType == null || selGroup == null)
            return;
        
        NewSpriteTypeDialog dia = new NewSpriteTypeDialog(frame, spriteLib, selType);
    }
    
    /** Renames the currently selected SpriteType. */
    public void renameSelectedSpriteType() { 
        String selGroup = (String) groupList.getSelectedItem();
        SpriteType selType = (SpriteType) this.getSelectedValue();
        
        if(selType == null || selGroup == null)
            return;
        
        String name = JOptionPane.showInputDialog(this, "Rename Sprite Type");
        if(name == null)
            return;
        if(spriteLib.spriteNames.contains(name)) {
            JOptionPane.showMessageDialog(this, "Another sprite with that name already exists. Try a different name.");
            return;
        }
        new RenameSpriteTypeEdit(spriteLib, selGroup, selType, name);
    }
    
    
}



/** Custom renderer for SpriteTypes in the SpriteLibraryPanel's spriteList. */
class SpriteTypeRenderer extends JLabel implements ListCellRenderer {
    
    public SpriteTypeRenderer() {
        setOpaque(true);
    }
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if(value == null) {
            this.setText("I am Error.");
            return this;
        }

        SpriteType spriteType = (SpriteType) value;
        
        Color background;
        Color foreground;
        
        this.setText(spriteType.name);
        this.setIcon(spriteType.icon);
        this.setVerticalTextPosition(JLabel.BOTTOM);
        this.setHorizontalTextPosition(JLabel.CENTER);
        
        if(isSelected) {
            background = Color.BLUE;
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



/** Right-click menu for the sprite type list. */
class SpriteTypeRClickMenu extends JPopupMenu implements ActionListener {
    
    public SpriteLibraryJList slpanel;
    
    public JMenuItem editItem = new JMenuItem("Edit");
    public JMenuItem renameItem = new JMenuItem("Rename");
    public JMenuItem deleteItem = new JMenuItem("Delete");
    
    public SpriteTypeRClickMenu(SpriteLibraryJList slpanel) {
        super();
        this.slpanel = slpanel;
        
        this.add(editItem);
        editItem.addActionListener(this);
        
        this.add(renameItem);
        renameItem.addActionListener(this);
        
        this.add(deleteItem);
        deleteItem.addActionListener(this);
    }    
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source == editItem) {
            slpanel.editSelectedSpriteType();
        }
        if(source == renameItem) {
            slpanel.renameSelectedSpriteType();
        }
        if(source == deleteItem) {
            slpanel.deleteSelectedSpriteType();
        }
        
    }
}
