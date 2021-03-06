package yarhar;

import java.util.HashMap;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import yarhar.map.*;
import yarhar.dialogs.*;
import yarhar.cmds.*;


/** The panel housing our current Sprite Library. */
public class SpriteLibraryPanel extends JPanel {
    
    /** A reference back to our YarharMain */
    public YarharMain frame;
    
    
    /** Button used to create a new SpriteType in the currently selected SpriteTypeGroup. */
  //  public JButton newSpriteBtn = new JButton("New Sprite");
    
    /** The combo box containing our library's list of SpriteTypeGroups. */
  //  public JComboBox groupList = new JComboBox();
    
    /** Set this to false to temporarily disable ActionEvents coming from groupList. */
  //  public boolean groupListIsActive = true;
    
    /** Our current SpriteTypeGroup's list of SpriteTypes. These can be dragged and dropped onto the editor to place new sprite instances. */
  //  public SpriteLibraryJList spriteList;
    
    /** A hierarchical list containing our sprite library, divided into groups. */
    public SpriteLibraryJTree spriteTree;
    
    /** A reference to our loaded LevelMap's SpriteLibrary. */
    public SpriteLibrary spriteLib = null;
    
    /** The right-click popup menu for the sprite list. */
  //  public SpriteTypeRClickMenu spriteRMenu;
    
    public SpriteLibraryPanel(YarharMain yarhar) {
        super();
        this.frame = yarhar;
        
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setBackground(new Color(0xDDDDDD));
        this.setBorder(new LineBorder(new Color(0xBBBBBB), 2));
        
        this.add(new JLabel("Sprite Library"));
        
        /*
        this.add(newSpriteBtn);
        newSpriteBtn.addActionListener(this);
        
        this.add(groupList);
        groupList.setMaximumSize(new Dimension(150, 20)); 
        groupList.addActionListener(this);
        
        spriteList = new SpriteLibraryJList(frame, groupList);
        spriteList.setCellRenderer(new SpriteTypeRenderer());
        spriteList.setDragEnabled(true);
        spriteList.setTransferHandler(transferHandler);
        spriteList.addMouseListener(this);
        spriteRMenu = new SpriteTypeRClickMenu(this);
        */
        
        spriteTree = new SpriteLibraryJTree(frame);
        spriteTree.setTransferHandler(transferHandler);
        
        JScrollPane scrollpane = new JScrollPane(spriteTree); // new JScrollPane(spriteList);
        scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollpane);

        
    }
    
    /*
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
    */

    
    
    /** Sets our spriteLib reference. */
    public void setLibrary(SpriteLibrary spriteLib) {
        this.spriteLib = spriteLib;
        this.spriteTree.spriteLib = spriteLib;
    }
    
    /** updates groupList to reflect the groups currently contained in our internal SpriteLibrary. */
    public void updateGroupList() {
        // update the group selector combo box
      /*  String selGroup = (String) groupList.getSelectedItem();
        
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
        */
    }
    
    /** Updates spriteList to reflect the SpriteTypes currently contained in the selected SpriteTypeGroup. */
    public void updateSpriteList(String groupName) {
        //spriteList.updateModel(groupName);
    }
    
    
    public void updateSpriteTree() {
        spriteTree.updateModel();
    }
    
    
    /** The SpriteLibrary's TransferHandler for drag and drop into the editor. */
    private TransferHandler transferHandler =  new TransferHandler() {
        
        /** A reference to the node the drag started from. */
        DefaultMutableTreeNode oldNode;
        
        /** I say COPY_OR_MOVE here, but we'll just treat move like copy. */
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY_OR_MOVE;
        }
        
        /** Only allow SpriteTypes to be transferred from the library's swing component. */
        public Transferable createTransferable(JComponent c) {
            if(c == spriteTree) { // JTree implementation
                // Save the node we're starting our drag from.
                DefaultMutableTreeNode dragNode = (DefaultMutableTreeNode) spriteTree.getLastSelectedPathComponent();
                oldNode = dragNode;
                
                // Only SpriteTypes can be transferred from the tree.
                Object selected = dragNode.getUserObject();
                if(selected instanceof SpriteType) {
                    SpriteType sprite = (SpriteType) selected;
                    return sprite;
                }
            }
          /*else if(c == spriteList) { // JList implementation
                Object selected = spriteList.getSelectedValue();
                if(selected instanceof SpriteType) {
                    SpriteType sprite = (SpriteType) selected;
                    return sprite;
                }
            }
            */ 
            
            return new StringSelection("");
        }
        
        /** Placeholder for validating imports. */
        public boolean canImport(TransferHandler.TransferSupport info) {
            return true;
        }
        
        /** Drag and drop allows sprite types to be moved to different groups in the tree. */
        public boolean importData(TransferHandler.TransferSupport info) {
            if(!canImport(info))
                return false;
            
            Transferable t = info.getTransferable();
            
            try {
                SpriteType spriteType = (SpriteType) t.getTransferData(SpriteType.flavor);
                
                // determine the destination group to move the SpriteType into.
                TreePath path = spriteTree.getDropLocation().getPath();
                System.err.println(path.toString());
                Object[] pathArr = path.getPath();
                String groupName = "default";
                if(pathArr.length > 1) {
                    groupName = pathArr[1].toString(); 
                }
                
                // determine the origin group to move the SpriteType out from.
                Object[] oldPathArr = oldNode.getUserObjectPath();
                String oldGroupName = oldPathArr[1].toString();
                
                // Move the sprite from the old group to the new group.
                new MoveSpriteTypeEdit(spriteLib, spriteType, oldGroupName, groupName);
                
                return true;
            }
            catch(Exception e) { // Pokemon exception causes the import to fail if it's not a SpriteType.
                System.err.println("EditorPanel drag and drop not successful.");
            }
            
            return false;
            
        }
    };
    
}








