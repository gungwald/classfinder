import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ClassFinder {

    private JTable results;
    private JLabel statusBar;
    private JavaFileFilter javaFileFilter;
    private ClassVersionExtractor versionExtractor;

    public ClassFinder(JTable results, JLabel statusBar) {
        this.results = results;
        this.statusBar = statusBar;
        javaFileFilter = new JavaFileFilter();
        versionExtractor = new ClassVersionExtractor();
    }

    void find(File searchIn, Pattern whatToFind) {
        try {
            if (searchIn.isDirectory()) {
                statusBar.setText("Searching " + searchIn.getAbsolutePath());
                File[] files = searchIn.listFiles(javaFileFilter);
                for (File entry : files) {
                    find(entry, whatToFind);
                }
            }
            else if (searchIn.getName().endsWith(".jar")) {
                JarFile jarFile = new JarFile(searchIn);
                statusBar.setText("Searching " + jarFile.getName());
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
        String[] row = new String[4];
        ClassVersion version = versionExtractor.getVersion(jar, entry);
        row[0] = "Jar";
        row[1] = jar.getName() + ":" + entry.getName();
        row[2] = String.valueOf(version.getMajorVersion());
        row[3] = String.valueOf(version.getProductVersion());
        ((DefaultTableModel) results.getModel()).addRow(row);
    }

    private void addResultRow(File searchIn) throws IOException {
        ClassVersion version = versionExtractor.getVersion(searchIn);
        String[] row = new String[4];
        row[0] = "Directory";
        row[1] = searchIn.getAbsolutePath();
        row[2] = String.valueOf(version.getMajorVersion());
        row[3] = String.valueOf(version.getProductVersion());
        ((DefaultTableModel) results.getModel()).addRow(row);
    }

}
