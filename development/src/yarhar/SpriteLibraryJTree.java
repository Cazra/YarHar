package yarhar;

import java.util.LinkedList;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeExpansionEvent;
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

public class SpriteLibraryJTree extends JTree implements TreeExpansionListener, TreeSelectionListener, MouseListener {
    /** A reference back to our YarharMain */
    public YarharMain frame;
    
    /** Reference to the current sprite library. */
    public SpriteLibrary spriteLib = null;
    
    public SpriteType lastType = null;
    public String lastGroupName = "";
    
    /** Maintain a list of expanded groups. */
    public LinkedList<String> expPaths = new LinkedList<String>();
    
    public LibraryRClickMenu rClickMenu;
    
    public SpriteLibraryJTree(YarharMain frame) {
        super();
        this.frame = frame;
        
        setDragEnabled(true);
        setCellRenderer(new SpriteTypeTreeRenderer());
        getSelectionModel().setSelectionMode
            (TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        this.addTreeExpansionListener(this);
        this.addTreeSelectionListener(this);
        this.addMouseListener(this);
        rClickMenu = new LibraryRClickMenu(this);
    }
    
    public void valueChanged(TreeSelectionEvent e) {
        Object[] path = e.getPath().getPath();
        
        lastType = null;
        lastGroupName = "";
        
        if(path.length > 1) {
            lastGroupName = ((DefaultMutableTreeNode) path[1]).getUserObject().toString();
            System.err.print(lastGroupName);
        }
        if(path.length > 2) {
            lastType = (SpriteType) ((DefaultMutableTreeNode) path[2]).getUserObject();
            System.err.print("/" + lastType.toString());
        }
        System.err.println();
    }
    
    
    public void treeCollapsed(TreeExpansionEvent e) {
      Object source = e.getSource();
      
      if(source == this) {
        String gName = e.getPath().getLastPathComponent().toString();
        System.err.println("Collapsed: " + gName);
        
        if(expPaths.contains(gName))
          expPaths.remove(gName);
      }
    }
    
    public void treeExpanded(TreeExpansionEvent e) {
      Object source = e.getSource();
      
      if(source == this) {
        String gName = e.getPath().getLastPathComponent().toString();
        System.err.println("Expanded: " + gName);
        
        if(!expPaths.contains(gName))
          expPaths.add(gName);
      }
    }
    
    public void mouseClicked(MouseEvent e) {
        Object source = e.getSource();
        int btn = e.getButton();
        
        if(source == this && btn == MouseEvent.BUTTON3) {
            Point mousePos = this.getMousePosition();
            TreePath pathAtMouse = this.getClosestPathForLocation(mousePos.x, mousePos.y);
            this.setSelectionPath(pathAtMouse);
            rClickMenu.show(this, mousePos.x, mousePos.y);
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
    
    
    /** Update the underlying model for this JTree to reflect the current library contents. */
    public void updateModel() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Sprite Library");
        
        for(String groupName : spriteLib.groupNames) {
            SpriteTypeGroup group = spriteLib.groups.get(groupName);
            
            DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(groupName);
            top.add(groupNode);
            
            for(String typeName : group.typeNames) {
                SpriteType sprite = spriteLib.sprites.get(typeName);
                
                DefaultMutableTreeNode spriteNode = new DefaultMutableTreeNode(sprite);
                groupNode.add(spriteNode);
            }
        }
        
        DefaultTreeModel model = new DefaultTreeModel(top);
        this.setModel(model);
        
        // reexpand the opened groups.
        for(String group : expPaths) {
          try {
            TreePath path = this.getNextMatch(group, 1, javax.swing.text.Position.Bias.Forward);
            if(path != null) {
              this.fireTreeExpanded(path);
            }
          }
          catch (Exception e) {
            // pokemon exception - gotta catch em all!
          }
        }
    }
    
    
    
    /** Adds a new SpriteType to the library. */
    public void addSpriteType() {
        NewSpriteTypeDialog dia = new NewSpriteTypeDialog(frame, spriteLib, lastGroupName);
    }
    
    
    /** Adds a new SpriteTypeGroup to the library. */
    public void addGroup() {
        String groupName = JOptionPane.showInputDialog(this, "New group name: ");
            if(groupName != null)
                new AddSpriteTypeGroupEdit(spriteLib, groupName);
    }
    
    
    /** Edits the currently selected SpriteType. */
    public void editSelectedSpriteType() {
        if(lastType == null || lastGroupName.equals(""))
            return;
        
        NewSpriteTypeDialog dia = new NewSpriteTypeDialog(frame, spriteLib, lastType);
    }
    
    
    /** Deletes the currently selected SpriteType from our library. */
    public void deleteSelectedSpriteType() {
        if(lastType == null || lastGroupName.equals(""))
            return;
            
        new DeleteSpriteTypeEdit(spriteLib, lastGroupName, lastType);
    }
    
    /** Deletes the currently selected SpriteTypeGroup from our library. */
    public void deleteSelectedGroup() {
        if(lastGroupName.equals(""))
            return;
        if(lastGroupName.equals("default")) {
            JOptionPane.showMessageDialog(this, "Cannot delete default group.");
            return;
        }
            
        new DeleteSpriteTypeGroupEdit(spriteLib, lastGroupName);
    }
    
    
    /** Renames the currently selected SpriteType. */
    public void renameSelectedSpriteType() { 
        if(lastType == null || lastGroupName.equals(""))
            return;
        
        String name = JOptionPane.showInputDialog(this, "Rename Sprite Type");
        if(name == null)
            return;
        if(spriteLib.spriteNames.contains(name)) {
            JOptionPane.showMessageDialog(this, "Another sprite with that name already exists. Try a different name.");
            return;
        }
        new RenameSpriteTypeEdit(spriteLib, lastGroupName, lastType, name);
    }
    
    /** Renames the currently selected SpriteTypeGroup. */
    public void renameSelectedGroup() { 
        if(lastGroupName.equals(""))
            return;
        if(lastGroupName.equals("default")) {
            JOptionPane.showMessageDialog(this, "Cannot rename default group.");
            return;
        }
        
        String name = JOptionPane.showInputDialog(this, "Rename group:");
        if(name == null)
            return;
        if(spriteLib.groupNames.contains(name)) {
            JOptionPane.showMessageDialog(this, "Another group with that name already exists. Try a different name.");
            return;
        }
        
        new RenameSpriteTypeGroupEdit(spriteLib, lastGroupName, name);
    }
}



/** Custom renderer for SpriteTypes in the SpriteLibraryPanel's spriteTree. */
class SpriteTypeTreeRenderer extends DefaultTreeCellRenderer {
    
    public SpriteTypeTreeRenderer() {
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        // Call the super-method to set defaults, but treat all nodes as if they are NOT leaves. We'll do custom icons for SpriteType leaves ahead.
        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, false, row,
                        hasFocus);
        
        // Use the sprite's icon as the custom icon for this cell, if this cell's node represents a SpriteType.
        try {
            if(!(value instanceof DefaultMutableTreeNode))
                return this;
            
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            SpriteType spriteType = (SpriteType) node.getUserObject();
            
            this.setIcon(spriteType.icon);
        }
        catch (Exception ex) { // Pokemon exception: Gotta catch 'em all! 
        }
        
        return this;
    }
}



/** Right-click menu for the SpriteLibraryJTree. */
class LibraryRClickMenu extends JPopupMenu implements ActionListener {
    
    public SpriteLibraryJTree slpanel;
    
    public JMenuItem newSpriteItem = new JMenuItem("New Sprite");
    public JMenuItem newGroupItem = new JMenuItem("New Group");
    public JMenuItem editItem = new JMenuItem("Edit");
    public JMenuItem renameItem = new JMenuItem("Rename");
    public JMenuItem deleteItem = new JMenuItem("Delete");
    
    public LibraryRClickMenu(SpriteLibraryJTree slpanel) {
        super();
        this.slpanel = slpanel;
        
        this.add(newSpriteItem);
        newSpriteItem.addActionListener(this);
        
        this.add(newGroupItem);
        newGroupItem.addActionListener(this);
        
        this.add(editItem);
        editItem.addActionListener(this);
        
        this.add(renameItem);
        renameItem.addActionListener(this);
        
        this.add(deleteItem);
        deleteItem.addActionListener(this);
    }    
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        // Create a new SpriteType in a group.
        if(source == newSpriteItem) {
            slpanel.addSpriteType();
        }
        
        // Create a new group in the library.
        if(source == newGroupItem) {
            slpanel.addGroup();
        }
        
        // Edit an existing SpriteType.
        if(source == editItem) {
            slpanel.editSelectedSpriteType();
        }
        
        // Rename an existing group or SpriteType.
        if(source == renameItem) {
            if(slpanel.lastType != null) {
                slpanel.renameSelectedSpriteType();
            }
            else {
                slpanel.renameSelectedGroup();
            }
            
        }
        
        // Delete a group or SpriteType.
        if(source == deleteItem) {
            if(slpanel.lastType != null) {
                slpanel.deleteSelectedSpriteType();
            }
            else {
                slpanel.deleteSelectedGroup();
            }
        }
        
    }
    
    
    /** Updates the enabled state of copy-pasta buttons, then displays the menu. */
    public void show(Component origin, int x, int y) {
        boolean canMakeSprite = (slpanel.lastGroupName != "");
        boolean canEditSprite = (slpanel.lastType != null);
        
        newSpriteItem.setEnabled(canMakeSprite); 
        newGroupItem.setEnabled(!canEditSprite); // Don't give the user the false impression that they could make a group inside of a group.
        editItem.setEnabled(canEditSprite);
        renameItem.setEnabled(canMakeSprite);
        deleteItem.setEnabled(canMakeSprite);
        
        super.show(origin, x, y);
    }
}
