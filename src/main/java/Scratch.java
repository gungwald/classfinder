import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Scratch {

	public void main(String[] args) {
		// TODO Auto-generated method stub
		URL iconURL = getClass().getResource("/some/package/favicon.png");
		// iconURL is null when not found
		ImageIcon icon = new ImageIcon(iconURL);
		JFrame frame = new JFrame();
		frame.setIconImage(icon.getImage());
	}

}
