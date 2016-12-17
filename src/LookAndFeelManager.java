import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;


public class LookAndFeelManager {
    
    static {
        // Default to the operating system's native look and feel. Duh...
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LookAndFeelManager() {
        super();
    }

    public void initChooserMenuItems(JMenu lookAndFeelMenu, ButtonGroup buttonGroup, Component... componentsToUpdate) {
        installOpenLookLookAndFeel();
        LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
        Map<String, LookAndFeelInfo> lookMap = new HashMap<String, LookAndFeelInfo>();
        LookAndFeelActionListener lookListener = new LookAndFeelActionListener(lookMap, componentsToUpdate);
        for (LookAndFeelInfo look : looks) {
            lookMap.put(look.getName(), look);
            JRadioButtonMenuItem lookMenuItem = new JRadioButtonMenuItem(look.getName());
            lookMenuItem.addActionListener(lookListener);
            buttonGroup.add(lookMenuItem);
            lookAndFeelMenu.add(lookMenuItem);
        }
    }

    /**
     * Installs the Open Look look and feel, if it can be found in the 
     * class path. Otherwise, it does nothing.
     */
    protected void installOpenLookLookAndFeel() {
        try {
            Class.forName("net.sourceforge.openlook_plaf.OpenLookLookAndFeel");
            UIManager.installLookAndFeel("Open Look", "net.sourceforge.openlook_plaf.OpenLookLookAndFeel");
        }
        catch (ClassNotFoundException e) {
            // If Open Look is not available, just do nothing.
        }
    }

}
