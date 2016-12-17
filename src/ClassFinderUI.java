import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class ClassFinderUI {

    private JFrame mainFrame;
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private JMenu mnLookFeel;
    private DirectoryChooser directoryChooser;
    private NativeDirectoryChooser nativeDirectoryChooser;
    private JComboBox directoryBox;
    private JTable table;
    private LookAndFeelManager lookAndFeelManager;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        // Enable anti-aliased text: http://wiki.netbeans.org/FaqFontRendering
        System.setProperty("awt.useSystemAAFontSettings","lcd");
        System.setProperty("swing.aatext", "true");
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ClassFinderUI window = new ClassFinderUI();
                    window.mainFrame.setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public ClassFinderUI() {
        initialize();
        directoryChooser = new DirectoryChooser("Select search directory");
        nativeDirectoryChooser = new NativeDirectoryChooser(mainFrame, "Select search directory");
        lookAndFeelManager = new LookAndFeelManager();
        lookAndFeelManager.initChooserMenuItems(mnLookFeel, buttonGroup, mainFrame, directoryChooser);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        mainFrame = new JFrame();
        mainFrame.setTitle("Class Finder");
        mainFrame.setBounds(100, 100, 780, 427);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().setLayout(new BoxLayout(mainFrame.getContentPane(), BoxLayout.X_AXIS));

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainFrame.getContentPane().add(mainPanel);
        mainPanel.setLayout(new BorderLayout(10, 20));

        JPanel parameterPanel = new JPanel();
        parameterPanel.setBorder(new TitledBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), new EmptyBorder(10, 10, 10, 10)), "Search Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
        mainPanel.add(parameterPanel, BorderLayout.NORTH);
        GridBagLayout gbl_parameterPanel = new GridBagLayout();
        gbl_parameterPanel.columnWidths = new int[] { 0, 0, 0, 0 };
        gbl_parameterPanel.rowHeights = new int[] { 0, 0, 0 };
        gbl_parameterPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
        gbl_parameterPanel.rowWeights = new double[] { 0.0, 0.0, 0.0 };
        parameterPanel.setLayout(gbl_parameterPanel);

        JLabel directoryLabel = new JLabel("Search directory:");
        directoryLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        directoryLabel.setPreferredSize(new Dimension(130, 25));
        directoryLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        GridBagConstraints gbc_directoryLabel = new GridBagConstraints();
        gbc_directoryLabel.anchor = GridBagConstraints.EAST;
        gbc_directoryLabel.insets = new Insets(0, 0, 5, 5);
        gbc_directoryLabel.gridx = 0;
        gbc_directoryLabel.gridy = 0;
        parameterPanel.add(directoryLabel, gbc_directoryLabel);

        directoryBox = new JComboBox();
        directoryBox.setPreferredSize(new Dimension(32, 40));
        directoryBox.setEditable(true);
        GridBagConstraints gbc_directoryBox = new GridBagConstraints();
        gbc_directoryBox.insets = new Insets(0, 0, 5, 5);
        gbc_directoryBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_directoryBox.gridx = 1;
        gbc_directoryBox.gridy = 0;
        parameterPanel.add(directoryBox, gbc_directoryBox);

        JButton browseButton = new JButton("Browse");
        browseButton.setPreferredSize(new Dimension(95, 40));
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String dir = null;
                if ((event.getModifiers() & (ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK | ActionEvent.META_MASK | ActionEvent.SHIFT_MASK)) > 0) {
                    dir = nativeDirectoryChooser.chooseDirectory();
                }
                else {
                    dir = directoryChooser.chooseDirectory(mainFrame);
                }
                if (dir != null) {
                    getDirectoryBox().addItem(dir);
                    getDirectoryBox().setSelectedItem(dir);
                }
            }
        });
        GridBagConstraints gbc_browseButton = new GridBagConstraints();
        gbc_browseButton.insets = new Insets(0, 0, 5, 0);
        gbc_browseButton.gridx = 2;
        gbc_browseButton.gridy = 0;
        parameterPanel.add(browseButton, gbc_browseButton);

        JComboBox searchBox = new JComboBox();
        searchBox.setEditable(true);
        searchBox.setPreferredSize(new Dimension(32, 40));
        GridBagConstraints gbc_searchBox = new GridBagConstraints();
        gbc_searchBox.insets = new Insets(0, 0, 5, 5);
        gbc_searchBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_searchBox.gridx = 1;
        gbc_searchBox.gridy = 1;
        parameterPanel.add(searchBox, gbc_searchBox);

        JLabel searchLabel = new JLabel("Search for class:");
        searchLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        searchLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        searchLabel.setPreferredSize(new Dimension(130, 25));
        GridBagConstraints gbc_searchLabel = new GridBagConstraints();
        gbc_searchLabel.anchor = GridBagConstraints.EAST;
        gbc_searchLabel.insets = new Insets(0, 0, 5, 5);
        gbc_searchLabel.gridx = 0;
        gbc_searchLabel.gridy = 1;
        parameterPanel.add(searchLabel, gbc_searchLabel);
        
                JButton searchButton = new JButton("Search");
                searchButton.setPreferredSize(new Dimension(95, 40));
                GridBagConstraints gbc_searchButton = new GridBagConstraints();
                gbc_searchButton.insets = new Insets(0, 0, 0, 5);
                gbc_searchButton.gridx = 1;
                gbc_searchButton.gridy = 2;
                parameterPanel.add(searchButton, gbc_searchButton);

        JLabel lblReady = new JLabel("Ready");
        lblReady.setPreferredSize(new Dimension(44, 25));
        mainPanel.add(lblReady, BorderLayout.SOUTH);

        JScrollPane resultTableScrollPane = new JScrollPane();
        mainPanel.add(resultTableScrollPane, BorderLayout.CENTER);

        table = new JTable();
        table.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Container Type", "File", "Class Version", "Java Version" }));
        // //table.setFillsViewportHeight(true);
        resultTableScrollPane.setViewportView(table);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setPreferredSize(new Dimension(0, 35));
        mainFrame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.setPreferredSize(new Dimension(45, 20));
        menuBar.add(fileMenu);

        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainFrame.dispose();
                System.exit(0);
            }
        });
        fileMenu.add(mntmExit);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');
        editMenu.setPreferredSize(new Dimension(47, 20));
        menuBar.add(editMenu);

        JMenuItem mntmCut = new JMenuItem("Cut");
        editMenu.add(mntmCut);

        JMenuItem mntmCopy = new JMenuItem("Copy");
        editMenu.add(mntmCopy);

        JMenuItem mntmPaste = new JMenuItem("Paste");
        editMenu.add(mntmPaste);

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
        viewMenu.setPreferredSize(new Dimension(55, 20));
        menuBar.add(viewMenu);

        mnLookFeel = new JMenu("Look & Feel");
        mnLookFeel.setPreferredSize(new Dimension(103, 25));
        viewMenu.add(mnLookFeel);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        helpMenu.setPreferredSize(new Dimension(53, 20));
        helpMenu.setHorizontalTextPosition(SwingConstants.CENTER);
        menuBar.add(helpMenu);
    }

    protected JMenu getLookAndFeelMenu() {
        return mnLookFeel;
    }

    protected JComboBox getDirectoryBox() {
        return directoryBox;
    }

    public Point calcCenteredPosition(Component component) {
        Point point = new Point();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        point.x = dim.width / 2 - component.getWidth() / 2;
        point.y = dim.height / 2 - component.getHeight() / 2;
        return point;
    }
}
