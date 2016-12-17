import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.Insets;

public class LookAndFeelSelector extends JDialog {
    
	private String selectedPlafClassName = null;
	private JComboBox comboBox;
    private LookAndFeelInfo[] plafInfos = null;
    private String[] plafNames = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			LookAndFeelSelector dialog = new LookAndFeelSelector();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public LookAndFeelSelector() {
		setTitle("Java Look and Feel Selector");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setMargin(new Insets(2, 10, 5, 10));
				okButton.setPreferredSize(new Dimension(70, 35));
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
                        selectedPlafClassName = plafInfos[getComboBox().getSelectedIndex()].getClassName();
                        LookAndFeelSelector.this.setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setMaximumSize(new Dimension(0, 0));
				cancelButton.setMargin(new Insets(2, 10, 5, 10));
				cancelButton.setMinimumSize(new Dimension(81, 30));
				cancelButton.setPreferredSize(new Dimension(95, 35));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
                        LookAndFeelSelector.this.setVisible(false);
                        LookAndFeelSelector.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		{
			JPanel panel = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panel.getLayout();
			getContentPane().add(panel, BorderLayout.CENTER);
			{
				JLabel lblNewLabel = new JLabel("Select Look and Feel:");
				panel.add(lblNewLabel);
			}
			{
				comboBox = new JComboBox();
				comboBox.setMinimumSize(new Dimension(100, 32));
				comboBox.setPreferredSize(new Dimension(100, 32));
				comboBox.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
                        try {
							UIManager.setLookAndFeel(plafInfos[getComboBox().getSelectedIndex()].getClassName());
							SwingUtilities.updateComponentTreeUI(LookAndFeelSelector.this);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				});
				comboBox.setModel(new DefaultComboBoxModel(getAvailableLafNames()));
				panel.add(comboBox);
			}
		}
		{
			Component verticalStrut = Box.createVerticalStrut(20);
			verticalStrut.setPreferredSize(new Dimension(5, 50));
			getContentPane().add(verticalStrut, BorderLayout.NORTH);
		}
	}

	protected JComboBox getComboBox() {
		return comboBox;
	}
    
	protected void installOpenLook() {
        try {
            Class.forName("net.sourceforge.openlook_plaf.OpenLookLookAndFeel");
            UIManager.installLookAndFeel("Open Look", "net.sourceforge.openlook_plaf.OpenLookLookAndFeel");
        }
        catch (ClassNotFoundException e) {
        	// It's OK
        }
	}
    
	protected String[] getAvailableLafNames() {
        if (plafNames == null) {
            installOpenLook();
    		plafInfos = UIManager.getInstalledLookAndFeels();
            plafNames = new String[plafInfos.length];
            for (int i = 0; i < plafInfos.length; i++) {
            	plafNames[i] = plafInfos[i].getName();
            }
        }
        return plafNames;
	}
    
	public String getSelectedPlafClassName() {
		return selectedPlafClassName;
	}
}
