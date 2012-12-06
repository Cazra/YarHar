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

/** The panel that houses the Layers UI */
public class LayersPanel extends JPanel implements ActionListener {

    /** A reference back to our YarharMain */
    public YarharMain frame;
    
    /** Used to create a new layer. */
  //  public JButton newBtn = new JButton("New layer");
    
    /** The scrollable list of layers. */
    public LayerList layerList;
    
    public LayersPanel(YarharMain yarhar) {
        super();
        this.frame = yarhar;
        
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setBackground(new Color(0xDDDDDD));
        this.setBorder(new LineBorder(new Color(0xBBBBBB), 2));
        
        add(new JLabel("Layers"));
      //  add(newBtn);
      //  newBtn.addActionListener(this);
        
        layerList = new LayerList(yarhar);
        add(layerList);
    }
    
    /** Makes this panel use the layers of a particular LevelMap. */
    public void setMap(LevelMap map) {
        layerList.setMap(map);
    }
    
    
    /** Clears the list and repopulates it. */
    public void updateList() {
        layerList.updateList();
    }
    
    /** Gets the currently selected Layer. */
    public Layer getSelectedLayer() {
        return layerList.map.selectedLayer;
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        // Create a new layer.
      /*  if(source == newBtn) {
            String layerName = JOptionPane.showInputDialog(this, "New Layer Name");
            if(layerName != "" && layerName != null) {
                layerList.addLayer(layerName);
                layerList.updateUI();
            }
        }
        */
    }
    
}







