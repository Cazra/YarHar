package yarhar;

import java.util.HashMap;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.awt.*;
import java.awt.event.*;
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
        return layerList.selectedLayer;
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source == newBtn) {
            String layerName = JOptionPane.showInputDialog(this, "New Layer Name");
            layerList.addLayer(layerName);
            layerList.updateUI();
        }
    }
    
}


/** A panel that lists components for the current map's layers. These layers can be dragged to reorder them and they have buttons to toggle things like visibility. */
class LayerList extends JScrollPane implements MouseListener, MouseMotionListener {
    
    public YarharMain frame;
    public LevelMap map;
    public JPanel panel = new JPanel();
    
    public Layer selectedLayer;
    
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
        
        for(Layer layer : map.layers) {
            panel.add(new LayerCell(this, layer));
        }
        
        selectedLayer = map.selectedLayer;
        
        updateUI();
    }
    
    /** Prepends a new layer to our map and selects it. */
    public void addLayer(String name) {
        Layer layer = new Layer(name);
        map.layers.addFirst(layer);
        
        updateList();
        selectedLayer = layer;
    }
    
    public void selectLayer(Layer layer) {
        if(!map.layers.contains(layer)) {
            return;
        }
        
        selectedLayer = layer;
        map.selectedLayer = layer;
    }
    
    // Mouse events
    
    public void mouseClicked(MouseEvent e) {
        // Do nothing
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
        int button = e.getButton();
        
    }
    
    public void mouseMoved(MouseEvent e) {
    }
    
    public void mouseDragged(MouseEvent e) {
        int button = e.getButton();
    }
    
    
}


class LayerCell extends JPanel implements MouseListener, ActionListener {
    
    public LayerList parent;
    public Layer layer;
    
    public JLabel nameLbl;
    public JButton visBtn;
    
    public LayerCell(LayerList parent, Layer layer) {
        super();
        this.parent = parent;
        this.layer = layer;
        
        this.setBorder(new LineBorder(new Color(0xBBBBBB), 2));
        
        nameLbl = new JLabel(layer.name);
        this.addMouseListener(this);
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
        int button = e.getButton();
        
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
        if(layer == parent.selectedLayer) {
            setBackground(new Color(0x000099));
            nameLbl.setForeground(Color.WHITE);
        }
        else {
            setBackground(new Color(0xDDDDDD));
            nameLbl.setForeground(Color.BLACK);
        }
        
        super.paint(g);
    }
}







