import java.awt.Cursor;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.Closeable;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ClassFinderThread extends Thread {

    private JTable results;
    private JLabel statusBar;
    private JavaFileFilter javaFileFilter;
    private ClassVersionExtractor versionExtractor;
    private File startDirectory;
    private Pattern searchPattern;
    private boolean stopRequested = false;
	private JButton stopButton;

    public ClassFinderThread() {
        javaFileFilter = new JavaFileFilter();
        versionExtractor = new ClassVersionExtractor();
    }

    public void setupForFind() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
            	results.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }
        });
    }
    
    public void cleanUpAfterFind() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
            	updateStatusBar("Ready");
            	results.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                stopButton.setVisible(false);
            }
        });
    }
    
    public void run() {
        setupForFind();
        find(startDirectory, searchPattern);
        cleanUpAfterFind();
    }

    public void find(File searchIn, Pattern whatToFind) {
        if (isStopRequested()) {
            return;
        }
        try {
            if (searchIn.isDirectory()) {
                updateStatusBar("Searching " + searchIn.getAbsolutePath());
                File[] files = searchIn.listFiles(javaFileFilter);
                for (File entry : files) {
                    find(entry, whatToFind);
                }
            }
            else if (searchIn.getName().endsWith(".jar")) {
                JarFile jarFile = new JarFile(searchIn);
                updateStatusBar("Searching " + jarFile.getName());
                for (JarEntry entry : Collections.list(jarFile.entries())) {
                    String name = entry.getName();
                    if (!entry.isDirectory() && name.endsWith(".class") && whatToFind.matcher(name).find()) {
                        addResultRow(jarFile, entry);
                    } else if (!entry.isDirectory() && name.endsWith(".jar")) {
                        File tmpJar = extractTmpJar(searchIn, whatToFind, jarFile, entry, name);
                        if (tmpJar != null) {
                            find(tmpJar, whatToFind);
                        }
                    }
                }
            }
            else if (whatToFind.matcher(searchIn.getName()).find()) {
                addResultRow(searchIn);
            }
        }
        catch (Exception e) {
            System.err.println("Failed to search " + searchIn + " for " + whatToFind.toString());
            e.printStackTrace();
            // Continue if one file causes an exception.
        }
    }
    private File extractTmpJar(File searchIn, Pattern whatToFind, JarFile jarFile, JarEntry entry, String name) throws IOException {
        String jarFilePath = searchIn.getAbsolutePath();
        jarFilePath = jarFilePath.replaceAll("\\\\", "/");
        String path = jarFilePath.substring(0, jarFilePath.lastIndexOf('/'));
        File dir = new File(path);
        File tmpJar = null;
        if (dir.exists()) {
            File tmpDir = new File(path + "/temp001/");
            if (!tmpDir.exists()) {
                tmpDir.mkdirs();
            }
            tmpJar = new File(path + "/temp001/" + name);

            String subTmpJar = tmpJar.getAbsolutePath();
            String subJarFilePath = subTmpJar.replaceAll("\\\\", "/");
            String subPath = subJarFilePath.substring(0, subJarFilePath.lastIndexOf('/'));
            File subDir = new File(subPath);
            if (!subDir.exists()) {
                subDir.mkdirs();
            }

            tmpJar.createNewFile();
            OutputStream outputStream = null;
            InputStream input = null;
            try {
                outputStream = new FileOutputStream(tmpJar);
                input = jarFile.getInputStream(entry);
                byte[] buffer = new byte[10240];
                int len = input.read(buffer);
                while (len > 0) {
                    outputStream.write(buffer, 0, len);
                    len = input.read(buffer);
                }
                outputStream.flush();
            } finally {
                close(outputStream);
                close(input);
            }

        }
        return tmpJar;
    }

    private static void close(Closeable obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (Throwable e){
            }
        }
    }

    private void addResultRow(JarFile jar, JarEntry entry) throws IOException {
        final String[] row = new String[4];
        ClassVersion version = versionExtractor.getVersion(jar, entry);
        row[0] = "Jar Entry";
        row[1] = jar.getName() + " : " + entry.getName();
        row[2] = String.valueOf(version.getMajorVersion());
        row[3] = String.valueOf(version.getProductVersion());
        addResultRow(row);
    }

    private void addResultRow(File searchIn) throws IOException {
        ClassVersion version = versionExtractor.getVersion(searchIn);
        final String[] row = new String[4];
        row[0] = "File";
        row[1] = searchIn.getAbsolutePath();
        row[2] = String.valueOf(version.getMajorVersion());
        row[3] = String.valueOf(version.getProductVersion());
        addResultRow(row);
    }

    protected void addResultRow(final String[] row) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                ((DefaultTableModel) results.getModel()).addRow(row);
            }
        });
    }

    protected void updateStatusBar(final String message) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                int characterWidth = (int) (statusBar.getSize().getWidth() / 5);
                String truncatedMessage = truncate(message, characterWidth);
                // Adding html tags for wrapping screws up the UI updating.
                statusBar.setText(truncatedMessage);
            }
        });
    }

    private String truncate(final String message, int max) {
        String truncatedMessage;
        if (message.length() <= max) {
            truncatedMessage = message;
        }
        else {
            truncatedMessage = message.substring(0, max);
        }
        return truncatedMessage;
    }

    /**
     * @return the startDirectory
     */
    public File getStartDirectory() {
        return startDirectory;
    }

    /**
     * @param startDirectory the startDirectory to set
     */
    public void setStartDirectory(File startDirectory) {
        this.startDirectory = startDirectory;
    }

    /**
     * @return the searchPattern
     */
    public Pattern getSearchPattern() {
        return searchPattern;
    }

    /**
     * @param searchPattern the searchPattern to set
     */
    public void setSearchPattern(Pattern searchPattern) {
        this.searchPattern = searchPattern;
    }

    public JTable getResults() {
        return results;
    }

    public void setResults(JTable results) {
        this.results = results;
    }

    public JLabel getStatusBar() {
        return statusBar;
    }

    public void setStatusBar(JLabel statusBar) {
        this.statusBar = statusBar;
    }

    public void setStopRequested(boolean stopRequested) {
        this.stopRequested = stopRequested;
    }

    public boolean isStopRequested() {
        return stopRequested;
    }

	public void setStopButton(JButton stopButton) {
		this.stopButton = stopButton;
	}

}
