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

public class SpriteLibraryJTree extends JTree {
    
    public SpriteLibrary spriteLib;
    
 //   public DragSource dragSource;
    
    public DefaultMutableTreeNode oldNode = null;
    
    public SpriteLibraryJTree(SpriteLibrary lib) {
        super();
        spriteLib = lib;
        
        setDragEnabled(true);
        setCellRenderer(new SpriteTypeTreeRenderer());
        getSelectionModel().setSelectionMode
            (TreeSelectionModel.SINGLE_TREE_SELECTION);
        
    //    dragSource = new DragSource();
    //    dragSource.createDefaultDragGestureRecognizer(this, TransferHandler.COPY_OR_MOVE, this);
    }
    
    /*
    
    public void dragGestureRecognized(DragGestureEvent e) {
        DefaultMutableTreeNode dragNode = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        
        if(dragNode != null) {
            Transferable transferable = (Transferable) dragNode.getUserObject();
            oldNode = dragNode;
            
            System.err.println(transferable.toString());
            
            dragSource.startDrag(e, DragSource.DefaultMoveDrop, transferable, this);
        }
    }
    
    public void dragEnter(DragSourceDragEvent dsde) {}
    
    public void dragExit(DragSourceEvent dse) {}
    
    public void dragOver(DragSourceDragEvent dsde) {}
    
    public void dropActionChanged(DragSourceDragEvent dsde) {}
    
    public void dragDropEnd(DragSourceDropEvent dsde) {}
    */
}



/** Custom renderer for SpriteTypes in the SpriteLibraryPanel's spriteTree. */
class SpriteTypeTreeRenderer extends DefaultTreeCellRenderer {
    
    public SpriteTypeTreeRenderer() {
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, false, row,
                        hasFocus);
                        
        if(value == null) {
            this.setText("I am Error.");
            return this;
        }
        
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
