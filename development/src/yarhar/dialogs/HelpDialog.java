package yarhar.dialogs;

import javax.swing.*;
import java.awt.*;

/** A dialog that allows the user to scale the selected sprites. */
public class HelpDialog extends JDialog {
    
    public String text="The following are the available mouse controls for using the editor area. \n" +
"\n" +
"Click and drag from empty space :     pan the camera. \n" +
"\n" +
"Mouse wheel :                 zoom the camera in or out from \n" +
"                    the mouse pointer.\n" +
"\n" +
"Click a sprite :             Select a sprite\n" +
"\n" +
"Shift + click a sprite :         Add another sprite to the selection.\n" +
"\n" +
"Drag sprite(s) :             Moves all the currently selected \n" +
"                    sprites with the mouse.\n" +
"\n" +
"Shift + drag :                 Add all sprites in rectangle to \n" +
"                    the selection.\n" +
"\n" +
"Ctrl + drag sprite(s) :         Clones the currently selected sprites and \n" +
"                    moves the clones with the mouse.\n" +
"\n" +
"Space + drag :                 Pan the camera, even if you're \n" +
"                    clicking on a sprite.\n" +
"\n" +
"r + drag :                 rotate the currently selected sprites.\n" +
"\n" +
"s + drag :                scale the currently selected sprites.\n" +
"\n" +
"t + drag :                 tile the currently selected sprites.\n" +
"\n" +
"Right-click :                Opens up the right click menu.\n" +
"\n" +
"Several menu options also have keyboard shortcuts available.";
    
    
    public HelpDialog(JFrame owner) {
        super(owner, true);
        
        JScrollPane scrollpane = new JScrollPane(new JTextArea(text));
        add(scrollpane);
        
        this.setSize(new Dimension(500,600));
        
        show();
    }

}

