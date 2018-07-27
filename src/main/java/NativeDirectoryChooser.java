import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;

public class NativeDirectoryChooser extends FileDialog {

	private static final long serialVersionUID = 1L;

    static {
        // Mac Specific: allow the user to select a directory.
        // Note that apple.awt.fileDialogForDirectories only
        // works with FileDialog.LOAD, not FileDialog.SAVE.
        // See https://developer.apple.com/library/mac/#documentation/Java/Reference/Java_PropertiesRef/Articles/JavaSystemProperties.html
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
    }
    
	public NativeDirectoryChooser(Frame parent, String title) {
		super(parent, title, FileDialog.LOAD);
		setDirectory(System.getProperty("user.dir"));
	}

	public String chooseDirectory() {
		File f = null;
        String dir = null;
		// awtDirectorySelector.setLocation(calcRelativeCenteredPosition(mainFrame,
		// awtDirectorySelector));

		setVisible(true);
		// awtDirectorySelector.setAlwaysOnTop(true);
		// awtDirectorySelector.requestFocusInWindow();
		// awtDirectorySelector.toFront();
		// java.awt.EventQueue.invokeLater(new Runnable() {
		// public void run() {
		// awtDirectorySelector.toFront();
		// awtDirectorySelector.repaint();
		String file = getFile();
		if (file != null) {
			f = new File(file);
			if (f.isFile()) {
				f = f.getParentFile();
			}
		}
		if (f != null) {
			try {
				dir = f.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return dir;
	}

}
