package yarhar.dialogs;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import yarhar.*;
import yarhar.map.*;

/** A dialog that allows the user to create a new SpriteType. */
public class GridDialog extends JDialog implements ActionListener, MouseListener {
    
    public LevelMap map;
    
    public JCheckBox displayChk;
    public JCheckBox snapChk;
    
    public JTextField gridXFld;
    public JTextField gridYFld;
    public JTextField gridWFld;
    public JTextField gridHFld;
    
    public Color gridColor;
    public JTextField colorFld = new JTextField(7);
    
    public JButton okBtn = new JButton("OK");
    public JButton cancelBtn = new JButton("Cancel");
    
    public GridDialog(YarharMain owner, LevelMap map) {
        super(owner, true);
        
        this.map = map;
        constructComponents();
        this.setSize(new Dimension(250,400));
        
        setTitle("Grid setup");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        show();
    }
    
    
    
    
    public void constructComponents() {
        JPanel topPanel = DialogUtils.makeVerticalFlowPanel();
        
        displayChk = new JCheckBox("Display Grid", map.displayGrid);
        JPanel displayChkPanel = DialogUtils.makeCheckBoxPanel(displayChk);
        topPanel.add(displayChkPanel);
        
        snapChk = new JCheckBox("Snap to Grid", map.snapToGrid);
        JPanel snapChkPanel = DialogUtils.makeCheckBoxPanel(snapChk);
        topPanel.add(snapChkPanel);
        
        topPanel.add(makeFldsPanel());
        
        gridColor = map.gridColor;
        colorFld.setEditable(false);
        colorFld.addMouseListener(this);
        updateColorFld();
        topPanel.add(DialogUtils.makeLabelFieldPanel(new JLabel("Grid Color: "), colorFld));
        
        JPanel okPanel = new JPanel();
        okPanel.add(okBtn);
        okBtn.addActionListener(this);
        okPanel.add(cancelBtn);
        cancelBtn.addActionListener(this);
        topPanel.add(okPanel);
        
        this.add(topPanel);
    }
    
    
    public JPanel makeFldsPanel() {
        JPanel result = DialogUtils.makeVerticalFlowPanel();
        result.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Grid metrics"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));
        
        gridXFld = new JTextField("" + map.gridSpaceX, 7);
        result.add(DialogUtils.makeLabelFieldPanel(new JLabel("Grid X Spacing: "), gridXFld));
        
        gridYFld = new JTextField("" + map.gridSpaceY, 7);
        result.add(DialogUtils.makeLabelFieldPanel(new JLabel("Grid Y Spacing: "), gridYFld));
        
        gridWFld = new JTextField("" + map.gridW, 7);
        result.add(DialogUtils.makeLabelFieldPanel(new JLabel("Grid Width: "), gridWFld));
        
        gridHFld = new JTextField("" + map.gridH, 7);
        result.add(DialogUtils.makeLabelFieldPanel(new JLabel("Grid Height: "), gridHFld));
        
        return result;
    }
    
    /** Updates the color and text of colorFld to reflect gridColor */
    public void updateColorFld() {
        colorFld.setText("0x" + Integer.toHexString(gridColor.getRGB() & 0x00FFFFFF).toUpperCase());
        colorFld.setBackground(gridColor);
    }
    
    
    
    /** Attempts to set the grid's size. Returns false if not successful. */
    public boolean trySetMetrics() {
        try {
            int gridSpaceX = (new Integer(gridXFld.getText())).intValue();
            int gridSpaceY = (new Integer(gridYFld.getText())).intValue();
            int gridW = (new Integer(gridWFld.getText())).intValue();
            int gridH = (new Integer(gridHFld.getText())).intValue();
            
            if(gridSpaceX < 1 || gridSpaceY < 1 || gridW < 1 || gridH < 1)
                throw new NumberFormatException();
            
            map.gridSpaceX = gridSpaceX;
            map.gridSpaceY = gridSpaceY;
            map.gridW = gridW;
            map.gridH = gridH;
            
            return true;
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Bad grid metrics");
            return false;
        }
        
    }
    
    
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source == okBtn) {
            boolean metricsAreGood = trySetMetrics();
            if(!metricsAreGood)
                return;
            
            map.gridColor = gridColor;
            map.displayGrid = displayChk.isSelected();
            map.snapToGrid = snapChk.isSelected();
            
            this.dispose();
        }
        if(source == cancelBtn) {
            this.dispose();
        }
    }
    
    
    public void mouseClicked(MouseEvent e) {
        Object source = e.getSource();
        
        if(source == colorFld) {
            Color color = JColorChooser.showDialog(this, "Choose Grid Color", gridColor);
            if(color != null) {
                gridColor = color;
                updateColorFld();
            }
        }
    }
   
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }
   
    public void mouseExited(MouseEvent e) {
        // Do nothing
    }
   
    public void mousePressed(MouseEvent e) {
        // Do nothing
    }
   
    public void mouseReleased(MouseEvent e) {
        // Do nothing
    }
}

