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
import yarhar.map.*;
import yarhar.dialogs.*;


public class LayersPanel extends JPanel implements ActionListener {

    /** A reference back to our YarharMain */
    public YarharMain frame;
    
    public JButton newBtn = new JButton("New layer");
    public LayerList layerList;
    
    public LayersPanel(YarharMain yarhar) {
        super();
        this.frame = yarhar;
        
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setBackground(new Color(0xDDDDDD));
        this.setBorder(new LineBorder(new Color(0xBBBBBB), 2));
        
        add(new JLabel("Layers"));
        add(newBtn);
        newBtn.addActionListener(this);
        
        layerList = new LayerList(yarhar);
        add(layerList);
    }
    
    
    public void setMap(LevelMap map) {
        layerList.setMap(map);
    }
    
    
    /** Clears the list and repopulates it. */
    public void updateList() {
        layerList.updateList();
    }
    
    
    public Layer getSelectedLayer() {
        return layerList.map.selectedLayer;
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source == newBtn) {
            String layerName = JOptionPane.showInputDialog(this, "New Layer Name");
            if(layerName != "" && layerName != null) {
                layerList.addLayer(layerName);
                layerList.updateUI();
            }
        }
    }
    
}


/** A panel that lists components for the current map's layers. These layers can be dragged to reorder them and they have buttons to toggle things like visibility. */
class LayerList extends JScrollPane {
    
    public YarharMain frame;
    public LevelMap map;
    public LinkedList<LayerCell> cellsList = new LinkedList<LayerCell>();
    
    /** The panel containing our "list" of LayerCells. */
    public JPanel panel = new JPanel();
    
    /** If true, we are currently reordering the layers with drag-and-drop */
    public boolean isDnD = false;
    
    public LayerCell draggedLayer = null;
    
    public int dragOffX = 0;
    public int dragOffY = 0;
    
    
    
    
    public LayerList(YarharMain yarhar) {
        super();
        this.setViewportView(panel);
        this.frame = yarhar;
        
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setBackground(new Color(0xFFFFFF));
        
    }
    
    
    public void setMap(LevelMap map) {
        this.map = map;
        updateList();
    }
    
    /** Clears the list and repopulates it. */
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
        map.layers.addFirst(layer);
        
        updateList();
        selectLayer(layer);
    }
    
    public void selectLayer(Layer layer) {
        if(!map.layers.contains(layer)) {
            return;
        }
        map.selectedLayer = layer;
    }
    
    /** Moves a layer to a new index and updates this list. */
    public void moveLayer(Layer layer, int destIndex) {
        int index = map.layers.indexOf(layer);
        
        System.err.println("Moved layer " + layer.name + " from " + index + " to " + destIndex);
        
        map.layers.remove(layer);
        
        map.layers.add(destIndex, layer);
        
        updateList();
        selectLayer(layer);
        
        
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
        System.err.println(mousePos);
        if(mousePos == null) 
            return;
        
        for(LayerCell cell : cellsList) {
            Point cellPos = cell.getLocation();
            int cellY = cellPos.y + cell.getSize().height;
            
            if(mousePos.y <= cellY) {
                targetLayer = cell.layer;
                break;
            }
        }
        int destIndex = map.layers.indexOf(targetLayer);
        moveLayer(draggedLayer.layer, destIndex);
    }
    
    
    
    
    public void paint(Graphics gg) {
        super.paint(gg);
        
        if(isDnD) {
            Graphics2D g = (Graphics2D) gg;
            Composite origComp = g.getComposite();
            AffineTransform origTrans = g.getTransform();
            
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            
            Point mousePos = getMousePosition();
            if(mousePos == null)
                mousePos = new Point(0,0);
            
            g.translate(mousePos.x - dragOffX, mousePos.y - dragOffY);
            g.drawImage(draggedLayer.image,0,0,null);
            
            g.setComposite(origComp);
            g.setTransform(origTrans);
        }
        
    }
    
}


class LayerCell extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    
    public LayerList parent;
    public Layer layer;
    
    public JLabel nameLbl;
    public JButton visBtn;
    
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
        
        if(button == MouseEvent.BUTTON1) {
            parent.selectLayer(layer);
            parent.repaint();
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
        if(parent.isDnD) {
            parent.endDnD();
        }
    }
    
    public void mouseMoved(MouseEvent e) {
    }
    
    public void mouseDragged(MouseEvent e) {
        parent.startDnD(this, e.getX(), e.getY());
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
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







