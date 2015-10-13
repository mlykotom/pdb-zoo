import gui.LoginFrame;
import utils.Logger;
import utils.Utils;

import javax.swing.*;

/**
 * Created by mlyko on 06.10.2015.
 */
public class Main {
	public static void main(String[] args) {
		/** GUI */
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException e) {
					e.printStackTrace();
				}

				LoginFrame loginFrame = new LoginFrame();
				loginFrame.setVisible(true);
			}
		});
	}
}
