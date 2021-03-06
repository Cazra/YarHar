package yarhar;

import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.border.LineBorder;
import yarhar.cmds.*;
import yarhar.map.*;
import yarhar.dialogs.*;

/** A panel that lists components for the current map's layers. These layers can be dragged to reorder them and they have buttons to toggle things like visibility. */
public class LayerList extends JScrollPane {
    
    public YarharMain frame;
    public LevelMap map;
    
    /** A list of the LayerCells associated with the layers, in order from top to bottom. */
    public LinkedList<LayerCell> cellsList = new LinkedList<LayerCell>();
    
    /** The panel containing our "list" of LayerCells. */
    public JPanel panel = new JPanel();
    
    /** If true, we are currently reordering the layers with drag-and-drop */
    public boolean isDnD = false;
    
    /** Reference to the LayerCell being dragged while isDnD is true. */
    public LayerCell draggedLayer = null;
    
    /** The x-offset of the drag-ghost effect */
    public int dragOffX = 0;
    
    /** The y-offset of the drag-ghost effect */
    public int dragOffY = 0;
    
    
    /** Layer right-click menu */
    public LayerRClickMenu rClickMenu;
    
    
    public LayerList(YarharMain yarhar) {
        super();
        this.setViewportView(panel);
        this.frame = yarhar;
        
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setBackground(new Color(0xFFFFFF));
        
        rClickMenu = new LayerRClickMenu(this);
        
    }
    
    
    /** Makes this panel use the layers of a particular LevelMap. */
    public void setMap(LevelMap map) {
        this.map = map;
        updateList();
    }
    
    /** Clears the list and repopulates it to reflect our map's layers list. */
    public void updateList() {
        panel.removeAll();
        cellsList = new LinkedList<LayerCell>();
        
        for(Layer layer : map.layers) {
            LayerCell lc = new LayerCell(this, layer);
            panel.add(lc);
            cellsList.add(lc);
        }
        
        updateUI();
    }
    
    /** Prepends a new layer to our map and selects it. */
    public void addLayer(String name) {
        Layer layer = new Layer(name);
        new AddLayerEdit(this, layer);
        
        
    }
    
    /** Deletes a layer. */
    public void deleteLayer(Layer layer) {
        new DeleteLayerEdit(this, layer);
    }
    
    /** Sets the map's currently selected layer. */
    public void selectLayer(Layer layer) {
        map.selectLayer(layer);
        repaint();
    }
    
    /** Moves a layer to a new index and updates this list. */
    public void moveLayer(Layer layer, int destIndex) {
        new MoveLayerEdit(this, layer, destIndex);
    }
    
    /** Renames a layer */
    public void renameLayer(Layer layer) {
        String name = JOptionPane.showInputDialog(this, "Rename layer", layer.name);
        if(name != null)
            new RenameLayerEdit(this, layer, name);
    }
    
    /** Starts the custom drag and drop for a layer. */
    public void startDnD(LayerCell layer, int x, int y) {
        if(!isDnD) {
            isDnD = true;
            draggedLayer = layer;
            dragOffX = x;
            dragOffY = y;
        }
        repaint();
    }
    
    /** ends the custom drag and drop by moving the dragged layer to just before the target layer. */
    public void endDnD() {
        isDnD = false;
        
        Layer targetLayer = map.layers.get(0);
        Point mousePos = this.getMousePosition();
        if(mousePos == null) 
            return;
        
        // figure out where we're dropping the layer by finding out which LayerCell the mouse is hovering over. 
        for(LayerCell cell : cellsList) {
            // Get the y of the bottom border of our cell.
            Point cellPos = cell.getLocation();
            int cellY = cellPos.y + cell.getSize().height;
            
            // use the first LayerCell whose bottom edge's y is greater than the mouse's local y.
            if(mousePos.y <= cellY) {
                targetLayer = cell.layer;
                break;
            }
        }
        
        // Move the layer to its new location.
        int destIndex = map.layers.indexOf(targetLayer);
        moveLayer(draggedLayer.layer, destIndex);
    }
    
    
    
    /** Custom paint that draws a ghost of the LayerCell currently being dragged if drag and drop is happening. */
    public void paint(Graphics gg) {
        super.paint(gg);
        
        if(isDnD) {
            Graphics2D g = (Graphics2D) gg;
            
            // save original graphics state. 
            Composite origComp = g.getComposite();
            AffineTransform origTrans = g.getTransform();
            
            // apply translucency
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            
            // get the mouse's local position.
            Point mousePos = getMousePosition();
            if(mousePos == null)
                mousePos = new Point(0,0);
            
            // draw the ghost image of the cell currently being dragged.
            g.translate(mousePos.x - dragOffX, mousePos.y - dragOffY);
            g.drawImage(draggedLayer.image,0,0,null);
            
            // restore original graphics state.
            g.setComposite(origComp);
            g.setTransform(origTrans);
        }
        
    }
    
}

/** A JPanel representing a Layer in the LayerList. */
class LayerCell extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    
    /** The LayerList containing this cell. */
    public LayerList parent;
    
    /** The Layer this cell represents. */
    public Layer layer;
    
    /** Label with the layer's name. */
    public JLabel nameLbl;
    
    /** Button for toggling this layer's visibility. */
    public JButton visBtn;
    
    /** Stores the last rendered image of this cell for the LayerList's drag-ghost painting effect. */
    public BufferedImage image;
    
    public LayerCell(LayerList parent, Layer layer) {
        super();
        this.parent = parent;
        this.layer = layer;
        
        this.setBorder(new LineBorder(new Color(0xBBBBBB), 2));
        
        nameLbl = new JLabel(layer.name);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        visBtn = new JButton("?");
        visBtn.addActionListener(this);
        
        updateButton();
        
        add(visBtn);
        add(nameLbl);
    }
    
    /** Updates the visibility button to reflect the layer's current visibility state. */
    public void updateButton() {
        if(layer.isVisible)
            visBtn.setText("vis");
        else
            visBtn.setText("hid");
    }
    
    
    // Mouse events
    
    public void mouseClicked(MouseEvent e) {
        int button = e.getButton();
        Object source = e.getSource();
        
        // clicking a cell causes its layer to become selected.
        if(button == MouseEvent.BUTTON1) {
            parent.selectLayer(layer);
        }
        if(button == MouseEvent.BUTTON3) {
            parent.selectLayer(layer);
            parent.rClickMenu.show(this, e.getX(), e.getY());
        }
    }
   
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }
   
    public void mouseExited(MouseEvent e) {
        // Do nothing
    }
   
    public void mousePressed(MouseEvent e) {
        int button = e.getButton();
        
    }
   
    
    public void mouseReleased(MouseEvent e) {
        // End any drag and drop going on in the parent LayerList.
        if(parent.isDnD) {
            parent.endDnD();
        }
    }
    
    public void mouseMoved(MouseEvent e) {
    }
    
    public void mouseDragged(MouseEvent e) {
        // start or continue drag and drop with this cell.
        parent.startDnD(this, e.getX(), e.getY());
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        // toggle this layer's visibility.
        if(source == visBtn) {
            layer.isVisible = !layer.isVisible;
            updateButton();
        }
        
        
    }
    
    /** Custom painting highlights the background of the selected layer cell. */
    public void paint(Graphics g) {
        if(layer == parent.map.selectedLayer) {
            setBackground(new Color(0x000099));
            nameLbl.setForeground(Color.WHITE);
        }
        else {
            setBackground(new Color(0xDDDDDD));
            nameLbl.setForeground(Color.BLACK);
        }
        
        image = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB );
        Graphics2D imgG = image.createGraphics();
        
        super.paint(g);
        super.paint(imgG);
    }
}




/** Right-click menu for the sprite type list. */
class LayerRClickMenu extends JPopupMenu implements ActionListener {
    
    public LayerList lpanel;
    
    public JMenuItem newItem = new JMenuItem("New Layer");
    public JMenuItem renameItem = new JMenuItem("Rename");
    public JMenuItem opacityItem = new JMenuItem("Set opacity");
    public JMenuItem deleteItem = new JMenuItem("Delete");
    
    public LayerRClickMenu(LayerList lpanel) {
        super();
        this.lpanel = lpanel;
        
        this.add(newItem);
        newItem.addActionListener(this);
        
        this.add(renameItem);
        renameItem.addActionListener(this);
        
        this.add(opacityItem);
        opacityItem.addActionListener(this);
        
        this.add(deleteItem);
        deleteItem.addActionListener(this);
    }    
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source == newItem) {
            String layerName = JOptionPane.showInputDialog(this, "New Layer Name");
            if(layerName != "" && layerName != null) {
                lpanel.addLayer(layerName);
                lpanel.updateUI();
            }
        }
        if(source == renameItem) {
            lpanel.renameLayer(lpanel.map.selectedLayer);
        }
        if(source == opacityItem) {
            OpacityDialog dialog = new OpacityDialog(lpanel.frame, lpanel.map.selectedLayer.opacity);
            if(dialog.returnedOK) {
                new OpacityLayerEdit(lpanel.map, lpanel.map.selectedLayer, dialog.opacity, dialog.isRelative);
            }
        }
        if(source == deleteItem) {
            lpanel.deleteLayer(lpanel.map.selectedLayer);
        }
        
    }
}