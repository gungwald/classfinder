import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;

public class DirectoryChooserAwt extends FileDialog {

	private static final long serialVersionUID = 1L;

    static {
        // Mac Specific: allow the user to select a directory.
        // Note that apple.awt.fileDialogForDirectories only
        // works with FileDialog.LOAD, not FileDialog.SAVE.
        // See https://developer.apple.com/library/mac/#documentation/Java/Reference/Java_PropertiesRef/Articles/JavaSystemProperties.html
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
    }
    
	public DirectoryChooserAwt(Frame parent) {
		super(parent, "Select search directory", FileDialog.LOAD);
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
		File[] files = getFiles();
		if (files.length > 0) {
			f = files[0];
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
