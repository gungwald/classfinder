import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassVersionExtractor {

    private FileFilter javaFilter = new JavaFileFilter();

    public ClassVersion readVersion(InputStream in) throws IOException {
        DataInputStream din = null;
        ClassVersion ver = null;
        try {
            din = new DataInputStream(in);
            int magic = din.readInt();
            if (magic == 0xcafebabe) {
                din.readUnsignedShort();
                int major = din.readUnsignedShort();
                ver = ClassVersion.lookupByMajorVersion(major);
                // System.out.println(f.getName() + ": " + ver);
            }
        }
        finally {
            close(din);
        }
        return ver;
    }

    public void close(InputStream in) {
        if (in != null) {
            try {
                in.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ClassVersion getVersion(JarFile jar, JarEntry entry) throws IOException {
        ClassVersion v = null;
        if (entry.getSize() < 10) {
            System.out.println(entry.getName() + ": Entry is to small to read the version");
        }
        else {
            InputStream in = jar.getInputStream(entry);
            v = readVersion(in);
            if (v == null) {
                System.out.println(jar.getName() + ": " + entry.getName() + ": Unable to read version");
            }
            else {
                System.out.println(jar.getName() + ": " + entry.getName() + ": " + v.toString());
            }
        }
        return v;
    }

    public ClassVersion getVersion(File f) throws IOException {
        InputStream in = new FileInputStream(f);
        ClassVersion ver = null;
        try {
            ver = readVersion(in);
        }
        finally {
            close(in);
        }
        return ver;
    }
}
