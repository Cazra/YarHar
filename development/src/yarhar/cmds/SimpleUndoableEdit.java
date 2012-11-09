package yarhar.cmds;

import javax.swing.undo.AbstractUndoableEdit;
import yarhar.YarharMenuBar;

/** Superclass for all UndoableEdits used by Yarhar. When created, these edit commands automatically execute themselves and add them into Yarhar's UndoManager. */
public class SimpleUndoableEdit extends AbstractUndoableEdit {
    public SimpleUndoableEdit() {
        super();
        YarharMenuBar.undoManager.addEdit(this);
    }
    
    public boolean canRedo() { return true; }
    
    public boolean canUndo() { return true; }
}
