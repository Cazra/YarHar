package yarhar;

import java.util.HashMap;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import yarhar.map.*;
import yarhar.dialogs.*;
import yarhar.cmds.*;


/** The panel housing our current Sprite Library. */
public class SpriteLibraryPanel extends JPanel implements ActionListener, MouseListener {
    
    /** A reference back to our YarharMain */
    public YarharMain frame;
    
    /** Button used to create a new SpriteType in the currently selected SpriteTypeGroup. */
    public JButton newSpriteBtn = new JButton("New Sprite");
    
    /** The combo box containing our library's list of SpriteTypeGroups. */
    public JComboBox groupList = new JComboBox();
    
    /** Set this to false to temporarily disable ActionEvents coming from groupList. */
    public boolean groupListIsActive = true;
    
    /** Our current SpriteTypeGroup's list of SpriteTypes. These can be dragged and dropped onto the editor to place new sprite instances. */
    public JList spriteList = new JList();
    
    /** A reference to our loaded LevelMap's SpriteLibrary. */
    public SpriteLibrary spriteLib = null;
    
    /** The right-click popup menu for the sprite list. */
    public SpriteTypeRClickMenu spriteRMenu;
    
    
    public SpriteLibraryPanel(YarharMain yarhar) {
        super();
        this.frame = yarhar;
        
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setBackground(new Color(0xDDDDDD));
        this.setBorder(new LineBorder(new Color(0xBBBBBB), 2));
        
        this.add(new JLabel("Sprite Library"));
        
        this.add(newSpriteBtn);
        newSpriteBtn.addActionListener(this);
        
        this.add(groupList);
        groupList.setMaximumSize(new Dimension(150, 20)); 
        groupList.addActionListener(this);
        
        spriteList.setCellRenderer(new SpriteTypeRenderer());
        spriteList.setDragEnabled(true);
        spriteList.setTransferHandler(transferHandler);
        spriteList.addMouseListener(this);
        spriteRMenu = new SpriteTypeRClickMenu(this);
        
        JScrollPane scrollpane = new JScrollPane(spriteList);
        scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollpane);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source == groupList && groupListIsActive) {
            String selGroup = (String) groupList.getSelectedItem();
            updateSpriteList(selGroup);
        }
        if(source == newSpriteBtn) {
            String selGroup = (String) groupList.getSelectedItem();
            NewSpriteTypeDialog dia = new NewSpriteTypeDialog(frame, spriteLib, selGroup);
        }
    }
    
    
    public void mouseClicked(MouseEvent e) {
        Object source = e.getSource();
        int btn = e.getButton();
        
        if(source == spriteList && btn == MouseEvent.BUTTON3) {
            spriteList.setSelectedIndex(spriteList.locationToIndex(new Point(e.getX(), e.getY())));
            Point mousePos = spriteList.getMousePosition();
            spriteRMenu.show(spriteList, mousePos.x, mousePos.y);
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
    
    
    
    
    
    
    /** Sets our spriteLib reference. */
    public void setLibrary(SpriteLibrary spriteLib) {
        this.spriteLib = spriteLib;
    }
    
    /** updates groupList to reflect the groups currently contained in our internal SpriteLibrary. */
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
    
    /** Updates spriteList to reflect the SpriteTypes currently contained in the selected SpriteTypeGroup. */
    public void updateSpriteList(String groupName) {
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
        
        spriteList.setModel(listModel);
    }
    
    
    
    /** Deletes the currently selected SpriteType from our library. */
    public void deleteSelectedSpriteType() {
        String selGroup = (String) groupList.getSelectedItem();
        SpriteType selType = (SpriteType) spriteList.getSelectedValue();
        if(selType == null || selGroup == null)
            return;
            
        new DeleteSpriteTypeEdit(spriteLib, selGroup, selType);
        updateSpriteList(selGroup);
    }
    
    
    /** Edits the currently selected SpriteType. */
    public void editSelectedSpriteType() {
        String selGroup = (String) groupList.getSelectedItem();
        SpriteType selType = (SpriteType) spriteList.getSelectedValue();
        
        if(selType == null || selGroup == null)
            return;
        
        NewSpriteTypeDialog dia = new NewSpriteTypeDialog(frame, spriteLib, selGroup, selType);
    }
    
    
    /** The SpriteLibrary's TransferHandler for drag and drop into the editor. */
    private TransferHandler transferHandler =  new TransferHandler() {
        
        /** I say COPY_OR_MOVE here, but we'll just treat move like copy. */
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY_OR_MOVE;
        }
        
        /** We just need the name of our exported SpriteType so we can look it up later. */
        public Transferable createTransferable(JComponent c) {
            Object selected = spriteList.getSelectedValue();
            if(selected instanceof SpriteType) {
                SpriteType sprite = (SpriteType) spriteList.getSelectedValue();
                return sprite;
            } 
            else
                return new StringSelection("");
        }
    };
    
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
    
    public SpriteLibraryPanel slpanel;
    public SpriteLibrary library;
    
    public JMenuItem editItem = new JMenuItem("Edit");
    public JMenuItem deleteItem = new JMenuItem("Delete");
    
    public SpriteTypeRClickMenu(SpriteLibraryPanel slpanel) {
        super();
        this.slpanel = slpanel;
        
        this.add(editItem);
        editItem.addActionListener(this);
        
        this.add(deleteItem);
        deleteItem.addActionListener(this);
    }    
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source == deleteItem) {
            slpanel.deleteSelectedSpriteType();
        }
        if(source == editItem) {
            slpanel.editSelectedSpriteType();
        }
    }
}



