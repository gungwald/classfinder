import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.regex.Pattern;

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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import classfinder.GlobPattern;

public class ClassFinder {

	private JFrame mainFrame;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JMenu lookAndFeelMenu;
	private DirectoryChooser directoryChooser;
	private NativeDirectoryChooser nativeDirectoryChooser;
	private JComboBox directoryBox;
	private JTable resultTable;
	private LookAndFeelManager lookAndFeelManager;
	private ClassFinderThread classFinder;
	private JLabel statusBar;
	private JComboBox searchBox;
	private JButton searchButton;
	private JButton stopButton;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		LookAndFeelManager.setSystemLookAndFeel();

		// Enable anti-aliased text: http://wiki.netbeans.org/FaqFontRendering
		System.setProperty("awt.useSystemAAFontSettings", "lcd");
		System.setProperty("swing.aatext", "true");
		// Put the main menu at the top on a Mac because that where is should be.
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		// Needed for Java 6 on Mac.
		System.setProperty("apple.awt.graphics.UseQuartz", "true");

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClassFinder window = new ClassFinder();
					window.mainFrame.setVisible(true);
                    window.startSearch(args);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClassFinder() {
		initialize();
		initializeMacSpecific();
		directoryChooser = new DirectoryChooser("Select search directory");
		nativeDirectoryChooser = new NativeDirectoryChooser(mainFrame, "Select search directory");
		lookAndFeelManager = new LookAndFeelManager();
		lookAndFeelManager.initChooserMenuItems(lookAndFeelMenu, buttonGroup, mainFrame, directoryChooser);
		initResultTable();
	}

    public void startSearch(String[] args) {
        if (args != null && args.length > 0) {
            directoryBox.setSelectedItem(new File(args[0]).getAbsolutePath());
            searchBox.setSelectedItem(".*");
            startSearch();
        }
    }
    
	/**
	 * Ideally the column widths should be stored and retrieved across invocations.
	 */
	protected void initResultTable() {
		DefaultTableCellRenderer centeredCellRenderer = new DefaultTableCellRenderer();
		centeredCellRenderer.setHorizontalAlignment(JLabel.CENTER);
		TableColumnModel resultColumnModel = resultTable.getColumnModel();
		resultColumnModel.getColumn(0).setMinWidth(10);
		resultColumnModel.getColumn(0).setMaxWidth(130);
		resultColumnModel.getColumn(0).setPreferredWidth(100);
		resultColumnModel.getColumn(1).setMinWidth(10);
		resultColumnModel.getColumn(1).setPreferredWidth(400);
		resultColumnModel.getColumn(1).setMaxWidth(Integer.MAX_VALUE);
		resultColumnModel.getColumn(2).setMinWidth(10);
		resultColumnModel.getColumn(2).setMaxWidth(130);
		resultColumnModel.getColumn(2).setPreferredWidth(100);
		resultColumnModel.getColumn(2).setCellRenderer(centeredCellRenderer);
		resultColumnModel.getColumn(3).setMinWidth(10);
		resultColumnModel.getColumn(3).setMaxWidth(130);
		resultColumnModel.getColumn(3).setPreferredWidth(100);
		resultColumnModel.getColumn(3).setCellRenderer(centeredCellRenderer);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainFrame = new JFrame();
		mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassFinder.class.getResource("/iconfinder_magnifier-data_532758-20x20.png")));
		//mainFrame.setIconImage(new ImageIcon(ClassFinder.class.getResource("/iconfinder_magnifier-data_532758-20x20.jpeg")).getImage());
		mainFrame.setTitle("ClassFinder");
		mainFrame.setBounds(100, 100, 780, 427);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(new BoxLayout(mainFrame.getContentPane(), BoxLayout.X_AXIS));

		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
		mainFrame.getContentPane().add(mainPanel);
		mainPanel.setLayout(new BorderLayout(0, 10));

		JPanel parameterPanel = new JPanel();
		mainPanel.add(parameterPanel, BorderLayout.NORTH);
		GridBagLayout gbl_parameterPanel = new GridBagLayout();
		gbl_parameterPanel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_parameterPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_parameterPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_parameterPanel.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		parameterPanel.setLayout(gbl_parameterPanel);

		JLabel directoryLabel = new JLabel("Search start directory:");
		directoryLabel.setToolTipText("The directory where the search will start");
		GridBagConstraints gbc_directoryLabel = new GridBagConstraints();
		gbc_directoryLabel.gridx = 0;
		gbc_directoryLabel.gridy = 0;
		gbc_directoryLabel.anchor = GridBagConstraints.EAST;
		parameterPanel.add(directoryLabel, gbc_directoryLabel);

		directoryBox = new JComboBox();
		directoryBox.setToolTipText("Enter a directory where the search will start. All subdirectories and contained jar files will be searched for the given pattern.");
		directoryBox.addItem(System.getProperty("user.dir"));
		directoryLabel.setLabelFor(directoryBox);
		directoryBox.setEditable(true);
		GridBagConstraints gbc_directoryBox = new GridBagConstraints();
		gbc_directoryBox.insets = new Insets(0, 4, 0, 4);
		gbc_directoryBox.fill = GridBagConstraints.BOTH;
		gbc_directoryBox.gridx = 1;
		gbc_directoryBox.gridy = 0;
		gbc_directoryBox.anchor = GridBagConstraints.CENTER;
		parameterPanel.add(directoryBox, gbc_directoryBox);

		JButton browseButton = new JButton("Browse");
		browseButton.setToolTipText("Open a dialog to select a search directory");
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String dir = null;
				if ((event.getModifiers() & (ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK | ActionEvent.META_MASK
						| ActionEvent.SHIFT_MASK)) > 0) {
					dir = nativeDirectoryChooser.chooseDirectory();
				} else {
					dir = directoryChooser.chooseDirectory(mainFrame);
				}
				if (dir != null) {
					getDirectoryBox().addItem(dir);
					getDirectoryBox().setSelectedItem(dir);
				}
			}
		});
		GridBagConstraints gbc_browseButton = new GridBagConstraints();
		gbc_browseButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_browseButton.gridx = 2;
		gbc_browseButton.gridy = 0;
		parameterPanel.add(browseButton, gbc_browseButton);

		searchBox = new JComboBox();
		searchBox.setToolTipText("Enter a search pattern in the form of a file glob or a /regex/ that will match the name of the class you want to find. To use a regex, wrap the pattern in slashes.");
		searchBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("actionPerformed: event=" + e);
				String cmd = e.getActionCommand();
				// When Enter is pressed, 2 action events are generated:
				// 1. comboBoxChanged
				// 2. comboBoxEdited
				// We only want to trigger the search once, so we must ignore
				// one of these events.
				if (cmd != null && cmd.equals("comboBoxChanged")) {
					startSearch();
				}
			}
		});
//	Not needed because actionPerformed does it.
//		searchBox.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyTyped(KeyEvent event) {
//				if (event.getKeyCode() == KeyEvent.VK_ENTER) {
//					startSearch();
//				}
//			}
//		});
		searchBox.setEditable(true);
		GridBagConstraints gbc_searchBox = new GridBagConstraints();
		gbc_searchBox.insets = new Insets(4, 4, 0, 4);
		gbc_searchBox.fill = GridBagConstraints.BOTH;
		gbc_searchBox.gridx = 1;
		gbc_searchBox.gridy = 1;
		gbc_searchBox.anchor = GridBagConstraints.CENTER;
		parameterPanel.add(searchBox, gbc_searchBox);

		JLabel searchLabel = new JLabel("Class name search pattern:");
		searchLabel.setToolTipText("A search pattern in the form of a file glob or a /regex/ that will match the name of the class you want to find.");
		searchLabel.setLabelFor(searchBox);
		GridBagConstraints gbc_searchLabel = new GridBagConstraints();
		gbc_searchLabel.insets = new Insets(4, 0, 0, 0);
		gbc_searchLabel.gridx = 0;
		gbc_searchLabel.gridy = 1;
		gbc_searchLabel.anchor = GridBagConstraints.EAST;
		parameterPanel.add(searchLabel, gbc_searchLabel);

		searchButton = new JButton("Search");
		searchButton.setToolTipText("Start search");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startSearch();
			}
		});
		GridBagConstraints gbc_searchButton = new GridBagConstraints();
		gbc_searchButton.insets = new Insets(4, 0, 0, 0);
		gbc_searchButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_searchButton.gridx = 2;
		gbc_searchButton.gridy = 1;
		parameterPanel.add(searchButton, gbc_searchButton);

		JScrollPane resultTableScrollPane = new JScrollPane();
		mainPanel.add(resultTableScrollPane, BorderLayout.CENTER);

		resultTable = new JTable();
		resultTable.setCellSelectionEnabled(true);
		resultTable.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "Type", "Location", "Major Version", "Java Version" }));
		// resultTable.setFillsViewportHeight(true);
		resultTableScrollPane.setViewportView(resultTable);

		JPanel statusPanel = new JPanel();
		mainPanel.add(statusPanel, BorderLayout.SOUTH);
		GridBagLayout gbl_statusPanel = new GridBagLayout();
		gbl_statusPanel.columnWidths = new int[] { 37, 0, 0 };
		gbl_statusPanel.rowHeights = new int[] { 16, 0 };
		gbl_statusPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_statusPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		statusPanel.setLayout(gbl_statusPanel);

		statusBar = new JLabel("Ready");
		GridBagConstraints gbc_statusBar = new GridBagConstraints();
		gbc_statusBar.weightx = 1.0;
		gbc_statusBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_statusBar.insets = new Insets(0, 0, 0, 5);
		gbc_statusBar.anchor = GridBagConstraints.WEST;
		gbc_statusBar.gridx = 0;
		gbc_statusBar.gridy = 0;
		statusPanel.add(statusBar, gbc_statusBar);

		stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				classFinder.setStopRequested(true);
				stopButton.setVisible(false);
			}
		});
		stopButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {

			}
		});
		stopButton.setVisible(false);
		GridBagConstraints gbc_stopButton = new GridBagConstraints();
		gbc_stopButton.gridx = 1;
		gbc_stopButton.gridy = 0;
		statusPanel.add(stopButton, gbc_stopButton);

		JMenuBar menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
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
		menuBar.add(editMenu);

		JMenuItem mntmCut = new JMenuItem("Cut");
		editMenu.add(mntmCut);

		JMenuItem mntmCopy = new JMenuItem("Copy");
		editMenu.add(mntmCopy);

		JMenuItem mntmPaste = new JMenuItem("Paste");
		editMenu.add(mntmPaste);

		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic('V');
		menuBar.add(viewMenu);

		lookAndFeelMenu = new JMenu("Look & Feel");
		viewMenu.add(lookAndFeelMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		helpMenu.setHorizontalTextPosition(SwingConstants.CENTER);
		menuBar.add(helpMenu);
	}

	protected void initializeMacSpecific() {
		if (System.getProperty("os.name").contains("Mac"))
			setIconImageForMac();
	}
	
	/**
	 * Requires Mac OS 10.5 and Java 1.5.
	 */
	protected void setIconImageForMac() {
		if (System.getProperty("os.version").startsWith("10.5")) {
			Image image = mainFrame.getIconImage();
			try {
			    // import com.apple.eawt.Application
			    String className = "com.apple.eawt.Application";
			    Class<?> cls = Class.forName(className);
	
			    // Application application = Application.getApplication();
			    Object application = cls.newInstance().getClass().getMethod("getApplication").invoke(null);
	
			    // application.setDockIconImage(image);
			    application.getClass().getMethod("setDockIconImage", java.awt.Image.class).invoke(application, image);
			}
			catch (Exception e) {
			    e.printStackTrace();
			}
		}
	}
	
	protected void startSearch() {
		getSearchBox().insertItemAt(getSearchBox().getSelectedItem(), 0);
		stopButton.setVisible(true);
		classFinder = new ClassFinderThread();
		classFinder.setResults(getResultTable());
		classFinder.setStatusBar(getStatusBar());
		classFinder.setStartDirectory(new File(directoryBox.getSelectedItem().toString()));
		Pattern pattern = null;
		String searchText = getSearchBox().getSelectedItem().toString().trim();
		if (searchText.startsWith("/") && searchText.endsWith("/")) {
			Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
		} else {
			GlobPattern.compile(searchText, Pattern.CASE_INSENSITIVE);
		}
		classFinder.setSearchPattern(pattern);
		classFinder.setStopButton(getStopButton());
		if (!classFinder.isAlive()) {
			classFinder.start();
		}
	}

	protected JMenu getLookAndFeelMenu() {
		return lookAndFeelMenu;
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

	protected JTable getResultTable() {
		return resultTable;
	}

	protected JLabel getStatusBar() {
		return statusBar;
	}

	protected JComboBox getSearchBox() {
		return searchBox;
	}

	protected JButton getSearchButton() {
		return searchButton;
	}

	/**
	 * @return the classFinder
	 */
	public ClassFinderThread getClassFinder() {
		return classFinder;
	}

	/**
	 * @param classFinder the classFinder to set
	 */
	public void setClassFinder(ClassFinderThread classFinder) {
		this.classFinder = classFinder;
	}

	public JButton getStopButton() {
		return stopButton;
	}
}
