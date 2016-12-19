import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class ClassFinder extends Thread {

    private JTable results;
    private JLabel statusBar;
    private JavaFileFilter javaFileFilter;
    private ClassVersionExtractor versionExtractor;
    private File startDirectory;
    private Pattern searchPattern;

    public ClassFinder() {
        javaFileFilter = new JavaFileFilter();
        versionExtractor = new ClassVersionExtractor();
    }
    
    public void run() {
        ((DefaultTableModel) results.getModel()).setDataVector(new Object[][] {}, new String[] { "Container Type", "File", "Class Version", "Java Version" });
        
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        
        TableColumnModel resultColumnModel = results.getColumnModel();
        resultColumnModel.getColumn(0).setMinWidth(10);
        resultColumnModel.getColumn(0).setPreferredWidth(30);
        resultColumnModel.getColumn(1).setMinWidth(10);
        resultColumnModel.getColumn(1).setPreferredWidth(400);
        resultColumnModel.getColumn(1).setMaxWidth(Integer.MAX_VALUE);
        resultColumnModel.getColumn(2).setMinWidth(10);
        resultColumnModel.getColumn(2).setPreferredWidth(30);
        resultColumnModel.getColumn(2).setCellRenderer(center);
        resultColumnModel.getColumn(3).setMinWidth(10);
        resultColumnModel.getColumn(3).setPreferredWidth(30);
        resultColumnModel.getColumn(3).setCellRenderer(center);
        find(startDirectory, searchPattern);
        updateStatusBar("Ready");
    }
    
    public void find(File searchIn, Pattern whatToFind) {
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
                    if (! entry.isDirectory() && name.endsWith(".class") && whatToFind.matcher(name).find()) {
                        addResultRow(jarFile, entry);
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

    private void addResultRow(JarFile jar, JarEntry entry) throws IOException {
        final String[] row = new String[4];
        ClassVersion version = versionExtractor.getVersion(jar, entry);
        row[0] = "Jar";
        row[1] = jar.getName() + ":" + entry.getName();
        row[2] = String.valueOf(version.getMajorVersion());
        row[3] = String.valueOf(version.getProductVersion());
        addResultRow(row);
    }

    private void addResultRow(File searchIn) throws IOException {
        ClassVersion version = versionExtractor.getVersion(searchIn);
        final String[] row = new String[4];
        row[0] = "Directory";
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
                statusBar.setText(message);
            }
        });
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

}
