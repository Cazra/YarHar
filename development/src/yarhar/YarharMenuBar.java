package yarhar;

import javax.swing.*;
import javax.swing.undo.*;
import java.awt.event.*;
import java.io.*;
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
            // if our map has been modified since its last save, give the user the option to save it before opening a map.
            if(promptModified() == JOptionPane.CANCEL_OPTION)
                return;
                
            yarhar.editorPanel.changeLevel("new");
            undoManager.discardAllEdits();
        }
        if(source == openItem)
            loadMap();
        if(source == saveItem)
            saveMap(false);
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
    
    
    
    /** Saves the currently opened map to a JSON file selected/created with a "save as" dialog. */
    public void saveMap(boolean saveAs) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(".yarmap json files", "yarmap"));
        
        LevelMap map = yarhar.editorPanel.getCurMap();
        
        int retVal;
        File selFile;
        
        System.err.println(map.filePath);
        
        // Prompt the user with a save dialog if this map has not ever been saved or if "Save As" was chosen.
        if(map.filePath == "" || saveAs) {
            retVal = chooser.showSaveDialog(this);
            selFile = chooser.getSelectedFile();
            if(selFile != null && !selFile.getName().endsWith(".yarmap")) {
                selFile = new File(selFile.getPath() + ".yarmap");
            }
        }
        else {
            retVal = JFileChooser.APPROVE_OPTION;
            selFile = new File(map.filePath);
        }
        
        
        // Proceed to save the map.
        if(retVal == JFileChooser.APPROVE_OPTION) {
            yarhar.editorPanel.isLoading = true;
            try {
                // convert our map to a JSON string
                map.name = selFile.getName();
                map.filePath = selFile.getPath();
                String jsonStr = "{\"yarmap\":" + map.toJSON() + "}";
            
                // save the map to our selected file.
                FileWriter fw = new FileWriter(selFile);
                fw.write(jsonStr);
                fw.close();
                map.isModified = false;
            }
            catch(Exception e) {
                JOptionPane.showMessageDialog(this, "Error writing to file " + selFile.getPath());
            }
            
            yarhar.editorPanel.isLoading = false;
            yarhar.updateTitle(map.name);
        }
    }
    
    
    /** Loads a map from a file. */
    public void loadMap() {
        LevelMap map = yarhar.editorPanel.getCurMap();
        
        // if our map has been modified since its last save, give the user the option to save it before opening a map.
        if(promptModified() == JOptionPane.CANCEL_OPTION)
            return;
        
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(".yarmap json files", "yarmap"));

        int retVal = chooser.showOpenDialog(this);
        
        // load the file.
        if(retVal == JFileChooser.APPROVE_OPTION) {
            File selFile = chooser.getSelectedFile();
            
            yarhar.editorPanel.isLoading = true;
            yarhar.editorPanel.changeLevel(selFile.getPath());
            yarhar.editorPanel.isLoading = false;
        }
        
        undoManager.discardAllEdits();
    }
    
    
    /** 
     * If the map has been modified since it was last saved, the user is asked if they want to save their map. 
     * Returns JOptionPane.CANCEL_OPTION if Cancel was chosen. else JOptionPane.YES_OPTION 
     */
    public int promptModified() {
        LevelMap map = yarhar.editorPanel.getCurMap();
        
        if(map.isModified) {
            int retVal = JOptionPane.showConfirmDialog(this,"\"" + map.name + "\" has been modified. Save changes?");
            if(retVal == JOptionPane.CANCEL_OPTION)
                return JOptionPane.CANCEL_OPTION;
            if(retVal == JOptionPane.YES_OPTION)
                saveMap(false);
        }
        return JOptionPane.YES_OPTION;
    }
}

