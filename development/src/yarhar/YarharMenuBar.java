package yarhar;

import javax.swing.*;
import javax.swing.undo.*;
import java.awt.event.*;
import yarhar.dialogs.*;
import yarhar.map.LevelMap;

/** The menu bar for the YarHar application. */
public class YarharMenuBar extends JMenuBar implements ActionListener {
    
    YarharMain yarhar;
    
    JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");
    
    JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo");
        JMenuItem redoItem = new JMenuItem("Redo");
        JMenuItem cutItem = new JMenuItem("Cut");
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem pasteItem = new JMenuItem("Paste");
        
    JMenu viewMenu = new JMenu("View");
        JMenuItem resetCamItem = new JMenuItem("Reset Camera");
        JMenuItem gridItem = new JMenuItem("Grid Setup");
        
    JMenu helpMenu = new JMenu("Help");
    
    public static UndoManager undoManager = new UndoManager();
    
    public YarharMenuBar(YarharMain yarhar) {
        super();
        this.yarhar = yarhar;
        
        undoManager.setLimit(50);
        
        this.add(fileMenu);
        fileMenu.setMnemonic(KeyEvent.VK_F);
            fileMenu.add(newItem);
            newItem.addActionListener(this);
            newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
            
            fileMenu.add(openItem);
            openItem.addActionListener(this);
            openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
            
            fileMenu.add(saveItem);
            saveItem.addActionListener(this);
            saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
            
            fileMenu.add(exitItem);
            exitItem.addActionListener(this);
            exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        
        this.add(editMenu);
        editMenu.setMnemonic(KeyEvent.VK_E);
            editMenu.add(undoItem);
            undoItem.addActionListener(this);
            undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
            
            editMenu.add(redoItem);
            redoItem.addActionListener(this);
            redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
            
            
            editMenu.add(new JSeparator());
            editMenu.add(cutItem);
            cutItem.setEnabled(false);
            editMenu.add(copyItem);
            editMenu.add(pasteItem);
        
        this.add(viewMenu);
        viewMenu.setMnemonic(KeyEvent.VK_V);
            viewMenu.add(resetCamItem);
            resetCamItem.addActionListener(this);
            resetCamItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
            
            viewMenu.add(gridItem);
            gridItem.addActionListener(this);
            gridItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
        
        this.add(helpMenu);
        helpMenu.setMnemonic(KeyEvent.VK_H);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        System.err.println("Menu event");
        
        // File menu
        if(source == newItem) {
            System.err.println("File -> newItem fired.");
            yarhar.editorPanel.changeLevel("new");
        }
        if(source == openItem)
            System.err.println("File -> openItem fired.");
        if(source == saveItem)
            System.err.println("File -> saveItem fired.");
        if(source == exitItem) {
            System.err.println("File -> exitItem fired.");
            yarhar.close();
        }
        
        // Edit menu
        if(source == undoItem) {
            try {
                undoManager.undo();
            }
            catch (Exception ex) {
                System.err.println("no more undos can be done.");
            }
        }
        if(source == redoItem) {
            try {
                undoManager.redo();
            }
            catch (Exception ex) {
                System.err.println("no more redos can be done.");
            }
        }
        
        // View menu
        if(source == resetCamItem) {
            System.err.println("View -> resetCamItem fired.");
            yarhar.editorPanel.resetCamera();
        }
        if(source == gridItem) {
            LevelMap curMap = ((LevelMap) yarhar.editorPanel.curLevel);
            new GridDialog(yarhar, curMap);
        }
        
    }
}

