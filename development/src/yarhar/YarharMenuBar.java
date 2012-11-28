package yarhar;

import javax.swing.*;
import javax.swing.undo.*;
import java.awt.event.*;
import javax.swing.event.MenuKeyEvent;
import java.io.*;
import yarhar.cmds.*;
import yarhar.dialogs.*;
import yarhar.map.LevelMap;


/** The menu bar for the YarHar application. */
public class YarharMenuBar extends JMenuBar implements ActionListener {
    
    YarharMain yarhar;
    
    public static UndoManager undoManager = new UndoManager();
    
    JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");
    
    JMenu editMenu = new JMenu("Edit") {
        public void setSelected(boolean b) {
            updateEnabledItems(b);   
            super.setSelected(b);
        }
    };
        JMenuItem undoItem = new JMenuItem("Undo");
        JMenuItem redoItem = new JMenuItem("Redo");
        
        JMenuItem cutItem = new JMenuItem("Cut");
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem pasteItem = new JMenuItem("Paste");
        
        JMenu orderMenu = new JMenu("Order");
            JMenuItem toFrontItem = new JMenuItem("Send to Front");
            JMenuItem fwdOneItem = new JMenuItem("Forward One");
            JMenuItem bwdOneItem = new JMenuItem("Backward One");
            JMenuItem toBackItem = new JMenuItem("Send to Back");
    
        

    JMenu viewMenu = new JMenu("View");
        JMenuItem resetCamItem = new JMenuItem("Reset Camera");
        JMenuItem gridItem = new JMenuItem("Grid Setup");
        JMenuItem bgColorItem = new JMenuItem("Set Background Color");
        
    JMenu helpMenu = new JMenu("Help");
    
    
    
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
            cutItem.addActionListener(this);
            cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
            
            editMenu.add(copyItem);
            copyItem.addActionListener(this);
            copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
            
            editMenu.add(pasteItem);
            pasteItem.addActionListener(this);
            pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
            
            editMenu.add(new JSeparator());
            
            editMenu.add(orderMenu);
                orderMenu.add(toFrontItem);
                toFrontItem.addActionListener(this);
                toFrontItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_QUOTE, ActionEvent.CTRL_MASK));
                
            //    orderMenu.add(fwdOneItem);
            //    orderMenu.add(bwdOneItem);
                orderMenu.add(toBackItem);
                toBackItem.addActionListener(this);
                toBackItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, ActionEvent.CTRL_MASK));
        
        this.add(viewMenu);
        viewMenu.setMnemonic(KeyEvent.VK_V);
            viewMenu.add(resetCamItem);
            resetCamItem.addActionListener(this);
            resetCamItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
            
            viewMenu.add(gridItem);
            gridItem.addActionListener(this);
            gridItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
            
            viewMenu.add(bgColorItem);
            bgColorItem.addActionListener(this);
            
        
        this.add(helpMenu);
        helpMenu.setMnemonic(KeyEvent.VK_H);
    }
    
    
    /** Updates the enabled state of the menu items. Because a JMenuItem's enabled state messes with its accelerator, 
    we enable these items when they are no longer visible and let our ActionEvent handler decide whether they should
    work or not. */
    public void updateEnabledItems(boolean b) {
        if(b) {
            LevelMap map = yarhar.editorPanel.getCurMap();
            
            undoItem.setEnabled(undoManager.canUndo());
            redoItem.setEnabled(undoManager.canRedo());
            
            boolean copyEnabled = map.copyEnabled();
            cutItem.setEnabled(copyEnabled);
            
            copyItem.setEnabled(copyEnabled);
            
            boolean pasteEnabled = map.pasteEnabled();
            pasteItem.setEnabled(pasteEnabled);
        }
        else {
            undoItem.setEnabled(true);
            redoItem.setEnabled(true);
            
            cutItem.setEnabled(true);
            copyItem.setEnabled(true);
            pasteItem.setEnabled(true);
        }
    }
    
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        LevelMap map = yarhar.editorPanel.getCurMap();
        
        // System.err.println("Menu event");
        
        // File menu
        if(source == newItem) {
            // if our map has been modified since its last save, give the user the option to save it before opening a map.
            if(promptModified() == JOptionPane.CANCEL_OPTION)
                return;
                
            yarhar.editorPanel.changeLevel("new");
            undoManager.discardAllEdits();
        }
        if(source == openItem) {
            loadMap();
        }
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
            catch (Exception ex) { // Pokemon exception: Gotta catch 'em all!
            //    System.err.println("no more undos can be done.");
            }
        }
        if(source == redoItem) {
            try {
                undoManager.redo();
            }
            catch (Exception ex) { // Pokemon exception: Gotta catch 'em all!
            //    System.err.println("no more redos can be done.");
            }
        }
        
        if(source == cutItem && map.copyEnabled()) {
            map.cut();
        }
        if(source == copyItem && map.copyEnabled()) {
            map.copy();
        }
        if(source == pasteItem && map.pasteEnabled()) {
            map.paste();
        }
        
        if(source == toFrontItem) {
            new ToFrontEdit(map);
        }
        if(source == toBackItem) {
            new ToBackEdit(map);
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
        if(source == bgColorItem) {
            LevelMap curMap = ((LevelMap) yarhar.editorPanel.curLevel);
            new BGColorDialog(yarhar, curMap);
        }
        
        yarhar.editorPanel.keyboard.endKeyHolds();
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
            catch(Exception e) { // Pokemon exception: Gotta catch 'em all!
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

