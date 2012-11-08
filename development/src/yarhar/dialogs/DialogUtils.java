package yarhar.dialogs;

import javax.swing.*;
import javax.swing.event.*;


public class DialogUtils {

    /** Creates a JPanel with a vertically-oriented BoxLayout. */
    public static JPanel makeVerticalFlowPanel() {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result,BoxLayout.Y_AXIS));
        return result;
    }
    
    /** Creates a JPanel with a JLabel followed by a JTextField with a FlowLayout. */
    public static JPanel makeLabelFieldPanel(JLabel label, JTextField field) {
        JPanel result = new JPanel();
        result.add(label);
        result.add(field);
        return result;
    }
    
    
    public static JPanel makeCheckBoxPanel(JCheckBox checkBox) {
        JPanel result = new JPanel();
        result.add(checkBox);
        return result;
    }

}
