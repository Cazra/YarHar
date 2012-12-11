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

/** A thin footer panel that displays some status information. */
public class StatusFooterPanel extends JPanel {

    /** A reference back to our YarharMain */
    public YarharMain frame;
    
    public JLabel statusTxt = new JLabel("Editor status");
    
    public StatusFooterPanel(YarharMain yarhar) {
        super();
        this.frame = yarhar;

        this.setBackground(new Color(0xDDDDDD));
        this.setBorder(new LineBorder(new Color(0xBBBBBB), 2));
        
        add(statusTxt);
    }
    
    public void setText(String status) {
      statusTxt.setText(status);
    }
}







