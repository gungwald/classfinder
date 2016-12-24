import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;

import javax.swing.JFileChooser;

public class DirectoryChooser extends JFileChooser {

	private static final long serialVersionUID = 1L;
    
	public DirectoryChooser(String title) {
        super();
		setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        setApproveButtonText("Choose");
        setDialogTitle(title);
	}
    
	public int showOpenDialog(Component requester) {
        center(requester);
        return super.showOpenDialog(requester);
	}
	
	public String chooseDirectory(Component requester) {
        String dir = null;
        if (showOpenDialog(requester) == APPROVE_OPTION) {
        	try {
				dir = getSelectedFile().getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return dir;
	}

    public void center(Component parent) {
        Point position = new Point();
        Point parentPosition = parent.getLocation();
        Dimension dim = parent.getSize();
        position.x = parentPosition.x + dim.width/2 - this.getWidth()/2;
        position.y = parentPosition.y + dim.height/2 - this.getHeight()/2;
        this.setLocation(position);
    }
}
