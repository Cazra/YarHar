package yarhar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import pwnee.*;

/** The main window for this Pwnee game example. */
public class YarharMain extends JFrame implements WindowListener, WindowFocusListener {
    
    public JPanel borderPanel = new JPanel(new BorderLayout());
    public ToolsPanel toolsPanel;
    public SpriteLibraryPanel spriteLibPanel;
    public LayersPanel layersPanel;
    public EditorPanel editorPanel;
    public StatusFooterPanel footer;
    public JSplitPane sidePane;
    public JSplitPane splitPane;
    public YarharConfig config;
    
    public YarharMain() {
        super("YarHar!");
        int screenX = 1024;    
        int screenY = 800;
        this.setSize(screenX,screenY);
        this.setJMenuBar(new YarharMenuBar(this));
        
        this.addWindowListener(this);
        this.addWindowFocusListener(this);
        
        this.add(borderPanel);
        
        toolsPanel = new ToolsPanel(this);
    //    borderPanel.add(toolsPanel, BorderLayout.NORTH);
        
        spriteLibPanel = new SpriteLibraryPanel(this);
        layersPanel = new LayersPanel(this);
        editorPanel = new EditorPanel(this);
        sidePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, layersPanel, spriteLibPanel);
        sidePane.setContinuousLayout(true);
        sidePane.setResizeWeight(0.5);
        
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidePane, editorPanel);
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(0.0);
        
        borderPanel.add(splitPane, BorderLayout.CENTER);
        
        footer = new StatusFooterPanel(this);
        borderPanel.add(footer, BorderLayout.SOUTH);
        
        
        
        // finishing touches on Game window
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        
        config = YarharConfig.load();
        
        System.err.println("Game Window successfully created!!!");
        
        editorPanel.start();
        
        // Swing can't correctly place divider locations on startup. So we must 
        // set their locations later when everything else is initialized.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            setDividerLocations();
          }
        });
    }
    
    
    /** Sets JSplitPane divider locations */
    public void setDividerLocations() {
      sidePane.setDividerLocation(0.5);
      splitPane.setDividerLocation(0.25);
    }
    
    // WindowListener interface stuff
    
    public void windowActivated(WindowEvent e) {
        System.err.println("Window Activated");
    }
    
    public void windowClosed(WindowEvent e)  {
        System.err.println("program terminated. Restoring original display settings.");
    }
    
    public void windowClosing(WindowEvent e) {
        System.err.println("Window closing");
        config.save();
    }
    
    public void windowDeactivated(WindowEvent e) {
        System.err.println("Window deactivated");
    }
    
     public void windowDeiconified(WindowEvent e) {
        System.err.println("Window Deiconified");
        // helloPanel.requestFocusInWindow();
    }
    
     public void windowIconified(WindowEvent e) {
        System.err.println("Window Iconified");
    }
    
     public void windowOpened(WindowEvent e) {
        System.err.println("Window opened");
    }
    
    public void windowGainedFocus(WindowEvent e) {
        System.err.println("Window gained focus");
        // helloPanel.requestFocusInWindow();
    }
    
    public void windowLostFocus(WindowEvent e)  {
        System.err.println("Window lost focus");
    }
    
    /** Cleans up the application (possibly asking the user if they want to save their changes) and then exits it. */
    public void close() {
        // do some cleaning up.
        System.err.println("cleaning up before closing...");
        editorPanel.clean();
        
        // terminate.
        System.exit(0);
    }
    
    /** Append the current working map's name to the window's title. */
    public void updateTitle(String mapName) {
        this.setTitle("YarHar! - " + mapName);
    }
    
    
    /** Creates the game window and makes it fullscreen if the user provided the argument "fullscreen". */
    public static void main(String[] args) {
        YarharMain window = new YarharMain();
    }

}