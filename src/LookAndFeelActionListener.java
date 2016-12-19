import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class LookAndFeelActionListener implements ActionListener {

    private Map<String, LookAndFeelInfo> lookMap;
    private Component[] componentsToUpdate;

    public LookAndFeelActionListener(Map<String, LookAndFeelInfo> lookMap, Component... componentsToUpdate) {
        this.lookMap = lookMap;
        this.componentsToUpdate = componentsToUpdate;
    }

    public void actionPerformed(ActionEvent event) {
        System.out.println(event.paramString());
        try {
            UIManager.setLookAndFeel(lookMap.get(event.getActionCommand()).getClassName());
            for (Component c : componentsToUpdate) {
                SwingUtilities.updateComponentTreeUI(c);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
